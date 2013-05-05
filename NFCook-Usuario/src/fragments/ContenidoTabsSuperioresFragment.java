package fragments;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import usuario.DescripcionPlato;
import adapters.MiCursorAdapterBuscadorPlatos;
import adapters.MiExpandableListTabsSuperioresCategoriasAdapter;
import adapters.MiListTabsSuperioresCategoriasAdapter;
import adapters.PadreExpandableListTabsSuperioresCategorias;
import adapters.PadreListTabsSuperioresCategorias;
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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;
import baseDatos.HandlerDB;

import com.example.nfcook.R;

/**
 * Clase encargada de cargar todos los platos dentro de una determinada categoría. Los platos se mostrarán
 * de diferente forma en función de si tienen imágen para mostrar o no, si esa categoría dispone de varios 
 * tipos de platos, etc.
 * 
 * @author Abel
 *
 */
public class ContenidoTabsSuperioresFragment extends Fragment{
		private View vistaConExpandableListView;
		private View vistaConListView;
		
		private HandlerDB sqlPlatos;
		private SQLiteDatabase dbPlatos;
		
		private ListView listViewPlatosUnicoTipo;
		private ExpandableListView expandableListViewPlatosVariosTipo;
		private boolean unicoTipoPlato;
		
		private ArrayList<PadreListTabsSuperioresCategorias> platos;
		private MiListTabsSuperioresCategoriasAdapter miAdapterListTabsSuperioresCategorias;
		
		private ArrayList<PadreExpandableListTabsSuperioresCategorias> tiposPlatosConPlatos;
		private MiExpandableListTabsSuperioresCategoriasAdapter miAdapterExpandableListTabsSuperioresCategorias;
				
		private AutoCompleteTextView buscador;
		
		private String categoriaTab, restaurante;
		
		private Boolean cargado = false;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	if(!cargado){
	    		vistaConExpandableListView = inflater.inflate(R.layout.expandable_list_tabs_fragment, container, false);
	    		vistaConListView = inflater.inflate(R.layout.list_tabs_fragment, container, false);
	    		
	    		// Importamos la base de datos de los platos
	    		importarBaseDatos();
	    		
	    		// Realizamos la carga de los platos
	    		if (categoriaTab.toLowerCase().endsWith("mis\nfavoritos"))
	    			cargarPlatosFavoritos();
	    		else 
	    			cargarPlatosCategoria();
	    		
	    		// Preparamos el buscador
		    	if (!unicoTipoPlato){
		    		cargarBarraDeBusqueda(vistaConExpandableListView);
		    	}else{
		    		cargarBarraDeBusqueda(vistaConListView);
		    	}
		    	
	    		cargado = true;
	    	}else if (categoriaTab.toLowerCase().endsWith("mis\nfavoritos")){
	    		// actualizamos los platos
		    	cargarPlatosFavoritos();
	    	}
	    	
	    	/* No podíamos hacer un xml común  para ambos, pues al poner los dos tipos de lista
	    	 * solo funcionaba uno de los oyentes de la lista. Lo que hemos hecho a sido hacer 
	    	 * dos xml, uno con la expanadale list por si el padre tiene más de un hijo y otro 
	    	 * xml con una lista simple por si el padre solo tiene un hijo (Ejem bebidas...)
	    	 * Es simplemente una cuestión estética
	    	 */
	    	if (unicoTipoPlato){
	    		return vistaConListView;
	    	}else{
	    		return vistaConExpandableListView;
	    	}
	    }
	    
	    public void setcategoriaTab(String categoriaTab){
	    	this.categoriaTab = categoriaTab;
	    }
	    
	    public void setRestaurante(String res){
	    	this.restaurante = res;
	    }
	    
	    public void importarBaseDatos() {
	        try{
	     	   sqlPlatos = new HandlerDB(getActivity().getApplicationContext()); 
	     	   dbPlatos = sqlPlatos.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(getActivity().getApplicationContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();
	         }
		}
	    
	    public void cargarBarraDeBusqueda(View vista){
			buscador = (AutoCompleteTextView) vista.findViewById(R.id.autoCompleteTextViewBuscadorPlatos);
		    Cursor c =  dbPlatos.rawQuery("SELECT Id AS _id, nombre AS item" + 
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
					   startActivityForResult(intent,0);
				    }
				      
				 });
	    }

	    /*No va a agrupar por categorias ya que una persona no va a tener tantos favoritos
	     * como para tener que buscar por categorias. Ademas de que se ve mucho mas estetico
	     * sin agrupar nada. */
	    public void cargarPlatosFavoritos(){
	    	// Sacamos los tipos de platos que hay dentro de esa categoría
    		Set<String> tiposPlato = new HashSet<String>();
    		String[] camposSacar = new String[]{"Id", "TipoPlato", "Nombre", "Breve", "Foto"};
	    	String[] camposCondicionanConsulta = new String[]{restaurante, "star_si"};
    		Cursor cP = dbPlatos.query("Restaurantes", camposSacar, "Restaurante=? AND Favorito=?", camposCondicionanConsulta,null, null,null);
	    	
    		PadreListTabsSuperioresCategorias plato;
    	    // Añadimos todos los tipos de platos que haya
    		platos = new ArrayList<PadreListTabsSuperioresCategorias>();
    		int imagenPlato;
    		boolean tieneImagen;
    		String imagen;
    	    while(cP.moveToNext()){
    	    	// Metemos el tipo al conjunto (Si ya está no lo duplica)
    	    	tiposPlato.add(cP.getString(1));
    	    	
    	    	// Añadimos el plato
    	    	imagen = cP.getString(4);
    	    	// Vemos si la imágen es suya y no es la de no disponible
    	    	tieneImagen = !imagen.equals("fnd_" + sacaSiglasRestauranteIdPlato(cP.getString(0))); 
    	    	imagenPlato = getResources().getIdentifier(imagen, "drawable", getActivity().getPackageName());
    	    	plato = new PadreListTabsSuperioresCategorias(cP.getString(2), cP.getString(3), imagenPlato, tieneImagen);
				platos.add(plato);
    	    }
    	    
    	    unicoTipoPlato = true;
       			
       		listViewPlatosUnicoTipo = (ListView) vistaConListView.findViewById(R.id.listViewPlatosTabsSuperiores);
       		miAdapterListTabsSuperioresCategorias = new MiListTabsSuperioresCategoriasAdapter(getActivity().getApplicationContext(), platos, restaurante);
       		listViewPlatosUnicoTipo.setAdapter(miAdapterListTabsSuperioresCategorias);
    	    
    	    // Oyente de la lista
       		listViewPlatosUnicoTipo.setOnItemClickListener(new ListView.OnItemClickListener()
    	    {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
					// Abrimos la pantalla de descripcion del plato y le pasamos el nombre del plato y restaurante
					Intent intent = new Intent(getActivity(),DescripcionPlato.class);
					intent.putExtra("nombreRestaurante", restaurante);
					intent.putExtra("nombrePlato", platos.get(arg2).getNombrePlato());
				    startActivityForResult(intent,0);
				}
    	    });
    	}
	    
	    @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	// actualizamos los platos
	    	cargarPlatosFavoritos();
		}
	    
	    
		public void cargarPlatosCategoria(){
    		// Sacamos los tipos de platos que hay dentro de esa categoría
    		Set<String> tiposPlato = new HashSet<String>();
    		String[] camposSacar = new String[]{"Id", "TipoPlato", "Nombre", "Breve", "Foto"};
	    	String[] camposCondicionanConsulta = new String[]{restaurante, categoriaTab};
    		Cursor cP = dbPlatos.query("Restaurantes", camposSacar, "Restaurante=? AND Categoria=?", camposCondicionanConsulta,null, null,null);
	    	
    		PadreListTabsSuperioresCategorias plato;
    	    // Añadimos todos los tipos de platos que haya
    		platos = new ArrayList<PadreListTabsSuperioresCategorias>();
    		int imagenPlato;
    		boolean tieneImagen;
    		String imagen;
    	    while(cP.moveToNext()){
    	    	// Metemos el tipo al conjunto (Si ya está no lo duplica)
    	    	tiposPlato.add(cP.getString(1));
    	    	
    	    	// Añadimos el plato
    	    	imagen = cP.getString(4);
    	    	// Vemos si la imágen es suya y no es la de no disponible
    	    	tieneImagen = !imagen.equals("fnd_" + sacaSiglasRestauranteIdPlato(cP.getString(0))); 
    	    	imagenPlato = getResources().getIdentifier(imagen, "drawable", getActivity().getPackageName());
    	    	plato = new PadreListTabsSuperioresCategorias(cP.getString(2), cP.getString(3), imagenPlato, tieneImagen);
				platos.add(plato);
    	    }
    	    
    	    // Vemos si se trata de una categoría con un único tipo de plato
    	    if(tiposPlato.size() == 1){
    	    	unicoTipoPlato = true;
       			
       			listViewPlatosUnicoTipo = (ListView) vistaConListView.findViewById(R.id.listViewPlatosTabsSuperiores);
       			miAdapterListTabsSuperioresCategorias = new MiListTabsSuperioresCategoriasAdapter(getActivity().getApplicationContext(), platos, restaurante);
       			listViewPlatosUnicoTipo.setAdapter(miAdapterListTabsSuperioresCategorias);
    	    
    	    	// Oyente de la lista
       			listViewPlatosUnicoTipo.setOnItemClickListener(new ListView.OnItemClickListener()
    	    	{
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
						// Abrimos la pantalla de descripcion del plato y le pasamos el nombre del plato y restaurante
						Intent intent = new Intent(getActivity(),DescripcionPlato.class);
						intent.putExtra("nombreRestaurante", restaurante);
						intent.putExtra("nombrePlato", platos.get(arg2).getNombrePlato());
				    	startActivity(intent);
					}
    	    	});
    	    
       		// Tenemos varios tipos en esa categoría
    	    }else{
    	    	unicoTipoPlato = false;
    	    	
    	    	// Recorremos todos los tipos que hay en esa categoría
    	    	Iterator<String> itTiposPlatos = tiposPlato.iterator();
    	    	String tipoPlato;
    	    	
    	    	// Creamos el array que contendra todos los tipos de platos y sus platos
        		tiposPlatosConPlatos = new ArrayList<PadreExpandableListTabsSuperioresCategorias>();
        		PadreExpandableListTabsSuperioresCategorias tipoPlatosConPlatos;
    	    	while(itTiposPlatos.hasNext()){
    	    		tipoPlato = itTiposPlatos.next();
    	    		String[] camposSacarTipoPlato = new String[]{"Id", "Nombre", "Breve", "Foto"};
        	    	String[] camposCondicionanConsultaTipoPlato = new String[]{restaurante, tipoPlato};
            		Cursor cPTipoPlato = dbPlatos.query("Restaurantes", camposSacarTipoPlato, "Restaurante=? AND TipoPlato=?", camposCondicionanConsultaTipoPlato, null, null,null);
        	    	
            	    // Creamos el array que contendra los platos de un tipo
            		platos = new ArrayList<PadreListTabsSuperioresCategorias>();
            		while(cPTipoPlato.moveToNext()){
            	    	// Creamos la info del plato
            	    	// Vemos si la imágen es suya y no es la de no disponible
            			imagen = cPTipoPlato.getString(3);
            	    	tieneImagen = !imagen.equals("fnd_" + sacaSiglasRestauranteIdPlato(cPTipoPlato.getString(0))); 
            	    	imagenPlato = getResources().getIdentifier(imagen, "drawable", getActivity().getPackageName());
            	    	plato = new PadreListTabsSuperioresCategorias(cPTipoPlato.getString(1), cPTipoPlato.getString(2), imagenPlato, tieneImagen);
        				
            	    	// Añadimos el plato
            	    	platos.add(plato);
            	    }
            		// Creamos un tipo con sus platos
            		tipoPlatosConPlatos = new PadreExpandableListTabsSuperioresCategorias(tipoPlato, platos);  
            		// Añadimos ese tipo a la lista de todos los tipos con sus platos
            		tiposPlatosConPlatos.add(tipoPlatosConPlatos);
    	    	}
    	    	
    	    	expandableListViewPlatosVariosTipo = (ExpandableListView) vistaConExpandableListView.findViewById(R.id.expandableListViewPlatos);
    	    	miAdapterExpandableListTabsSuperioresCategorias = new MiExpandableListTabsSuperioresCategoriasAdapter(getActivity().getApplicationContext(), tiposPlatosConPlatos, restaurante);
    	    	expandableListViewPlatosVariosTipo.setAdapter(miAdapterExpandableListTabsSuperioresCategorias);
    	    
    	    	// Oyente de la lista expandible
    	    	expandableListViewPlatosVariosTipo.setOnChildClickListener(new OnChildClickListener() {				
					public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
						Intent intent = new Intent(getActivity(),DescripcionPlato.class);
						intent.putExtra("nombreRestaurante", restaurante);
						intent.putExtra("nombrePlato", tiposPlatosConPlatos.get(groupPosition).getNombrePlato(childPosition));
				    	startActivity(intent);
						return false;
					}
				});
    	    }
		}
		
		/*
		 * Metodo encargado de sacar las letras del id de un plato. Estas letras son siglas del restaurante
		 * y lo llevarán todos los platos del restaurante junto con un número. Ambas partes configuran el id
		 * de cada plato.
		 */
		public String sacaSiglasRestauranteIdPlato(String id){
			boolean fin = false;
			String idLetras = "";
			int numCar = id.length();
			int i = 0;
			char c;
			while(i<numCar && !fin){
				c = id.charAt(i);
				if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'){
					idLetras = idLetras + c;
				}else{
					fin = true;
				}
				i++;
			}
			return idLetras.toLowerCase();
		}
		
	}