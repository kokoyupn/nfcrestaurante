package fragments;
import  baseDatos.Handler;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import usuario.DescripcionPlato;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.example.nfcook.R;


public class TabsFragment extends Fragment {
		
		private static final String NOMBRE = "NOMBRE";  
		private static final String DESCRIPCION = "DESCRIPCION";  
		private List<Map<String, String>> listaPadres = new ArrayList<Map<String, String>>();  
		private List<List<Map<String, String>>> listaHijos = new ArrayList<List<Map<String, String>>>();  
		private SimpleExpandableListAdapter  mAdapter;
		private SimpleAdapter mAdapterListView;
		private ExpandableListView exp;
		public ListView listaCategoriaUnica;
		private boolean esListaExpandible;
		
		private Handler sql;
		private SQLiteDatabase db;
		
		private String tipoTab, restaurante;
		
		private Boolean cargado = false;
		
		private View vistaConExpandaleList;
		private View vistaConListView;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	if(!cargado){
	    		vistaConExpandaleList = inflater.inflate(R.layout.tabs_fragment_expandable_list, container, false);
	    		vistaConListView = inflater.inflate(R.layout.tabs_fragment_list_view, container, false);
	    		importarBaseDatatos(vistaConExpandaleList);
	    		crearExpandableListOlistView(vistaConExpandaleList, vistaConListView);
	    		cargado=true;
	    	}
	    	
	    	/* No podíamos hacer un xml común  para ambos, pues al poner los dos tipos de lista
	    	 * solo funcionaba uno de los oyentes de la lista. Lo que hemos hecho a sido hacer 
	    	 * dos xml, uno con la expanadale list por si el padre tiene más de un hijo y otro 
	    	 * xml con una lista simple por si el padre solo tiene un hijo (Ejem bebidas...)
	    	 * Es simplemente una cuestión estética
	    	 */
	    	if (!esListaExpandible)
	    		return vistaConListView;
	    	else
	    		return vistaConExpandaleList;
	    }
	    
	    public void setTipoTab(String tipoTab){
	    	this.tipoTab = tipoTab;
	    }
	    
	    public void setRestaurante(String res){
	    	this.restaurante = res;
	    }
	    
	    public void importarBaseDatatos(View v ) {
	        try{
	     	   sql=new Handler(v.getContext()); 
	     	   db=sql.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(v.getContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();
	      		
	         }
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
	    	    
	    	    //Solo tenemos un padre => utilizamos ListView por estética
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
	    	    	listaCategoriaUnica = (ListView) vistaConListView.findViewById(R.id.listView1);
	    	    	mAdapterListView = new SimpleAdapter(  
			    			getActivity(),  
			    			listaHijoActual,
			   				R.layout.contenido_lista,  
			   				new String[] {NOMBRE, DESCRIPCION },  
			    			new int[] { R.id.textView1, R.id.textView2 }  
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
			    			new int[] { R.id.textView1, R.id.textView2 }  
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
							/*
					        m.replace(R.id.RelativeLayout1, fragmentDescripcion);
					        m.addToBackStack("DescripcionPlato");
					        m.commit();*/
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