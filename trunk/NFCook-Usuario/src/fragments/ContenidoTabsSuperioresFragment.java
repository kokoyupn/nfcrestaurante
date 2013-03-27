package fragments;
import  baseDatos.HandlerDB;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import usuario.DescripcionPlato;
import adapters.MiCursorAdapterBuscadorPlatos;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.example.nfcook.R;


public class ContenidoTabsSuperioresFragment extends Fragment{
		
		private static final String NOMBRE = "NOMBRE";  
		private static final String DESCRIPCION = "DESCRIPCION";  
		private List<Map<String, String>> listaPadres = new ArrayList<Map<String, String>>();  
		private List<List<Map<String, String>>> listaHijos = new ArrayList<List<Map<String, String>>>();  
		private SimpleExpandableListAdapter  mAdapter;
		private SimpleAdapter mAdapterListView;
		private ExpandableListView exp;
		public ListView listaCategoriaUnica;
		private boolean esListaExpandible;
				
		private HandlerDB sql;
		private SQLiteDatabase db;
		private AutoCompleteTextView buscador;
		
		private String tipoTab, restaurante;
		
		private Boolean cargado = false;
		
		private View vistaConExpandaleList;
		private View vistaConListView;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	if(!cargado){
	    		vistaConExpandaleList = inflater.inflate(R.layout.expandable_list_tabs_fragment, container, false);
	    		vistaConListView = inflater.inflate(R.layout.list_tabs_fragment, container, false);
	    		
	    		importarBaseDatatos(vistaConExpandaleList);
	    		crearExpandableListOlistView(vistaConExpandaleList, vistaConListView);
	    		cargado = true;
	    	}
	    	
	    	/* No pod�amos hacer un xml com�n  para ambos, pues al poner los dos tipos de lista
	    	 * solo funcionaba uno de los oyentes de la lista. Lo que hemos hecho a sido hacer 
	    	 * dos xml, uno con la expanadale list por si el padre tiene m�s de un hijo y otro 
	    	 * xml con una lista simple por si el padre solo tiene un hijo (Ejem bebidas...)
	    	 * Es simplemente una cuesti�n est�tica
	    	 */
	    	if (!esListaExpandible){
	    		cargarBarraDeBusqueda(vistaConListView);
	    		return vistaConListView;
	    	}else{
	    		cargarBarraDeBusqueda(vistaConExpandaleList);
	    		return vistaConExpandaleList;
	    	}
	    }
	    
	    public void setTipoTab(String tipoTab){
	    	this.tipoTab = tipoTab;
	    }
	    
	    public void setRestaurante(String res){
	    	this.restaurante = res;
	    }
	    
	    public void importarBaseDatatos(View v ) {
	        try{
	     	   sql=new HandlerDB(v.getContext()); 
	     	   db=sql.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(v.getContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();
	         }
		}
	    
	    public void cargarBarraDeBusqueda(View vista){
			buscador = (AutoCompleteTextView) vista.findViewById(R.id.autoCompleteTextViewBuscadorPlatos);
		    Cursor c =  db.rawQuery("SELECT Id AS _id, nombre AS item" + 
		      " FROM Restaurantes" + 
		      " WHERE Restaurante ='"+ restaurante+"' and nombre LIKE '%" +""+ "%' ", null);
			
			buscador.setAdapter(new MiCursorAdapterBuscadorPlatos(getActivity(), c, CursorAdapter.NO_SELECTION, restaurante));
			buscador.setThreshold(2);
			
			buscador.setOnItemClickListener(new OnItemClickListener() {
		
				   public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
					   //Es lo que vamos a mostrar en la barra de busqueda una vez pinchada una sugerencia.
					   Cursor c = (Cursor) arg0.getAdapter().getItem(position);
					   buscador.setText("");
					   Intent intent = new Intent(getActivity(),DescripcionPlato.class);
					   intent.putExtra("nombreRestaurante", restaurante);
					   intent.putExtra("nombrePlato", c.getString(1));
					   startActivity(intent);
				    }
				      
				 });
	    }

		public void crearExpandableListOlistView(View vistaConExpandaleList, View vistaConListView){
			try{	    	
		    	Map<String, String> padreActual;
		    	List<Map<String, String>> listaHijoActual = null;  
	    		Map<String, String> hijoActual;  
		    	
	    		// Sacamos los padres del ExpandableList 
	    		Set<String> padresLista = new HashSet<String>();
	    		String[] camposPadre = new String[]{"TipoPlato"};
		    	String[] datosPadre = new String[]{restaurante,tipoTab};
	    		Cursor cP = db.query("Restaurantes", camposPadre, "Restaurante=? AND Categoria=?",datosPadre,null, null,null);
    	    	
	    	    // Recorremos todos los registros
	    	    while(cP.moveToNext()){
	    	    	padresLista.add(cP.getString(0));
	    	    }
	    	    
	    	    Iterator<String> itPadre = padresLista.iterator();
	    	    String nombrePadre;
	    	    String[] camposHijo;
	    	    String[] datosHijo;
	    	    
	    	    //Solo tenemos un padre => utilizamos ListView por est�tica
	    	    if(padresLista.size()==1){
	    	    	esListaExpandible = false;
	    	    	nombrePadre = itPadre.next();
	    	    	camposHijo = new String[]{"Nombre","Breve"};
			    	datosHijo = new String[]{restaurante,tipoTab,nombrePadre};
	    	    	//Padres Lista
	    			padreActual = new HashMap<String, String>();  
	    			listaPadres.add(padreActual);  
	    			padreActual.put(NOMBRE, nombrePadre);
	    			listaHijoActual = new ArrayList<Map<String, String>>();
	    			
	    			//Consultamos los platos que hay dentro de ese tipo
	    			Cursor cH = db.query("Restaurantes", camposHijo, "Restaurante=? AND Categoria=? AND TipoPlato=?",datosHijo,null, null,null);
	    			while(cH.moveToNext()){
	    				hijoActual = new HashMap<String, String>();  
			    		listaHijoActual.add(hijoActual);  
			    		hijoActual.put(NOMBRE, cH.getString(0));  
			    		hijoActual.put(DESCRIPCION, cH.getString(1));  
	    				
	 	    	    }
	    			listaHijos.add(listaHijoActual);
	    	    	listaCategoriaUnica = (ListView) vistaConListView.findViewById(R.id.listViewCuenta);
	    	    	mAdapterListView = new SimpleAdapter(  
			    			getActivity(),  
			    			listaHijoActual,
			   				R.layout.contenido_lista,  
			   				new String[] {NOMBRE, DESCRIPCION },  
			    			new int[] { R.id.textViewCuenta, R.id.textViewPrecioPlatoCuenta }  
			    			);
	    	    	
	    	    	listaCategoriaUnica.setAdapter(mAdapterListView);
	    	    
	    	    	// Oyente de la lista
	    	    	listaCategoriaUnica.setOnItemClickListener(new ListView.OnItemClickListener()
	    	    	{
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
							// Abrimos la pantalla de descripcion del plato y le pasamos el nombre del plato y restaurante
							@SuppressWarnings("unchecked")
							Map<String,String> datosPlato = (Map<String, String>) listaCategoriaUnica.getItemAtPosition(arg2);
							String nombrePlato = datosPlato.get("NOMBRE");
							Intent intent = new Intent(getActivity(),DescripcionPlato.class);
							intent.putExtra("nombreRestaurante", restaurante);
							intent.putExtra("nombrePlato", nombrePlato);
					    	startActivity(intent);
						}
	    	    	});
	    	    	
	    	    }else{ // tenemos varios padres => utilizamos expandablelistview
	    	    	esListaExpandible = true;
	    	    	while(itPadre.hasNext()){
		    	    	nombrePadre = itPadre.next();
		    	    	camposHijo = new String[]{"Nombre","Breve"};
				    	datosHijo = new String[]{restaurante,tipoTab,nombrePadre};
		    	    	
				    	//Padres Lista
		    			padreActual = new HashMap<String, String>();  
		    			listaPadres.add(padreActual);  
		    			padreActual.put(NOMBRE, nombrePadre);
		    			listaHijoActual = new ArrayList<Map<String, String>>();
		    			
		    			//Consultamos los platos que hay dentro de ese tipo
		    			Cursor cH = db.query("Restaurantes", camposHijo, "Restaurante=? AND Categoria=? AND TipoPlato=?",datosHijo,null, null,null);
		    			while(cH.moveToNext()){
		    				hijoActual = new HashMap<String, String>();  
				    		listaHijoActual.add(hijoActual);  
				    		hijoActual.put(NOMBRE, cH.getString(0));  
				    		hijoActual.put(DESCRIPCION, cH.getString(1));  
		    				
		 	    	    }
		    			listaHijos.add(listaHijoActual);
		    	    }
	    	    	
			    	mAdapter = new SimpleExpandableListAdapter(  
			    			getActivity(),  
			    			listaPadres,  
			    			android.R.layout.simple_expandable_list_item_1,  
			    			new String[] {NOMBRE},  
			   				new int[] { android.R.id.text1 },  
			   				listaHijos,  
			   				R.layout.contenido_lista,  
			   				new String[] {NOMBRE, DESCRIPCION },  
			    			new int[] { R.id.textViewCuenta, R.id.textViewPrecioPlatoCuenta }  
			    			);  
			   	     
					exp = (ExpandableListView) vistaConExpandaleList.findViewById(R.id.expandableListViewPlatos);
					// Oyente de la lista expandible
					exp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
						public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
							// Abrimos la pantalla de descripcion del plato y le pasamos el nombre del plato y restaurante
							Map<String,String> datosPlato = listaHijos.get(groupPosition).get(childPosition);
							String nombrePlato = datosPlato.get("NOMBRE");
							Intent intent = new Intent(getActivity(),DescripcionPlato.class);
							intent.putExtra("nombreRestaurante", restaurante);
							intent.putExtra("nombrePlato", nombrePlato);
					    	startActivity(intent);
					        return false;
					   }
					});
					exp.setAdapter(mAdapter);
	    	   }
		    }catch(SQLiteException e){
		        Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();	
		    }   
	    }
		
	}