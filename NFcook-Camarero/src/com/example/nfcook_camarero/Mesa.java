package com.example.nfcook_camarero;

import java.util.ArrayList;

import adapters.ContenidoListMesa;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.MiListAdapterMesa;
import adapters.PadreExpandableListEditar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Añade los componentes de cada pedido a la mesa
 * 
 * -Atributos-
 * numMesa : Indica el numero de la mesa actual.
 * dbPedido : Base de datos con todos los pedidos de todas las mesas.
 * dbHistorico : Base de datos donde se almacenan todos los platos pedidos una vez cobrada esa mesa.
 * platos : Componente ListView que mostrará los platos de la mesa actual.
 * elemLista : ArrayList con los platos de la mesa actual, que se utiliza para crear el adapter del componente ListView.
 * adapter : Objeto de la clase ListaMesaAdapter.
 * precioTotal : TextView que contiene el precio total de los pedidos de la mesa actual.
 * 
 * @author Rober
 */


public class Mesa extends Activity {

	private HandlerGenerico sqlMesas,sqlHistorico;
	private String numMesa;
	private String idCamarero;
	private String numPersonas; 
	private SQLiteDatabase dbMesas,dbHistorico;
	private static ListView platos;
	private ArrayList<ContenidoListMesa> elemLista;
	private static MiListAdapterMesa adapter;
	private static TextView precioTotal;
	private int indicePulsado;
	private ArrayList<MesaView> listaDeMesas;
	private Activity actividad;
	
	private GestureDetector detector;
	private View.OnTouchListener tocuhListener;
	private View seleccionado;
	
	
	
	private AutoCompleteTextView actwObservaciones;
	
	
	private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private static ExpandableListView expandableListEditarExtras;
	private static Context context;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.pedidomesa);
		actividad = this;
		
		//Necesario para actualizar la lista de las mesas al añadir un plato
		context = Mesa.this;
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		
		TextView mesa = (TextView)findViewById(R.id.numeroDeMesa);
		mesa.setText("Mesa "+ String.valueOf(numMesa) );
		
		try{
			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
			
			//Añadir platos a la ListView----------------------------------------------------
	  	  	platos = (ListView)findViewById(R.id.listaPlatos);
		    elemLista = obtenerElementos();
	         
	  	    adapter = new MiListAdapterMesa(this, elemLista);
	  	     
	  	    precioTotal = (TextView)findViewById(R.id.precioTotal);
	  	    precioTotal.setText(Double.toString( Math.rint(adapter.getPrecio()*100/100) )+" €");
	  	     
	  	    platos.setAdapter(adapter);
	  	    

	  	    platos.setOnItemClickListener(new OnItemClickListener() {
	  	    	
	  	    	public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id){
	  	    		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(Mesa.this);
	  	    		ventanaEmergente.setNegativeButton("Cancelar", null);
	  				onClickBotonAceptarAlertDialog(ventanaEmergente, posicion);
	  				//onClickBotonCancelarAlertDialog(ventanaEmergente);
	  				View vistaAviso = LayoutInflater.from(Mesa.this).inflate(R.layout.ventana_emergente_editar_anadir_plato, null);
	  				expandableListEditarExtras = (ExpandableListView) vistaAviso.findViewById(R.id.expandableListViewExtras);
	  				TextView encabezadoDialog = (TextView) vistaAviso.findViewById(R.id.textViewEditarAnadirPlato);
	  				encabezadoDialog.setText("Editar Plato");
	  				TextView tituloPlato = (TextView) vistaAviso.findViewById(R.id.textViewTituloPlatoEditarYAnadir);
	  				actwObservaciones = (AutoCompleteTextView) vistaAviso.findViewById(R.id.autoCompleteTextViewObservaciones);
	  				tituloPlato.setText(adapter.getNombrePlato(posicion));
	  				actwObservaciones.setText(adapter.getObservacionesPlato(posicion));
	  				cargarExpandableListExtras(posicion);
	  				ventanaEmergente.setView(vistaAviso);
	  				ventanaEmergente.show();
	  			}
	  	    });
	  	    
	  	  
	  	    detector = new GestureDetector(context,new Detector());
	  	    System.out.println("aaaaa");
	  	    tocuhListener = new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	            	switch (event.getAction() ) { 
	            		case MotionEvent.ACTION_DOWN:
	            	
		            	try{
		  					if(Detector.getSeleccionado())
		  						seleccionado = Mesa.getPlatos().getChildAt(Detector.getitemId());
		  						seleccionado.setBackgroundColor(Color.WHITE);
		  						Button delete = (Button) seleccionado.findViewById(R.id.boton_borrar);
		  		        		delete.setVisibility(android.view.View.INVISIBLE);
		  			            Detector.setSeleccionado(false);
		  				}catch(Exception e){
		  					System.out.println("No creado el Detector");
		  				}
	            	}
	                return detector.onTouchEvent(event);
	            }
	        };
	        // prevent the view to be touched
	        platos.setOnTouchListener(tocuhListener);
	        
	        
	  	   
	    }catch(Exception e){
			System.out.println("Error lectura base de datos de Pedido");
		}
		

		
		
		//Boton AñadirPlato---------------------------------------------------------------
		Button aniadirPlato = (Button)findViewById(R.id.aniadirPlato);
		aniadirPlato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		Intent intent = new Intent(actividad, AnadirPlatos.class);
            		//Le pasamos a la siguiente pantalla el numero de la mesa que se ha pulsado
            		intent.putExtra("NumMesa", numMesa);
            		intent.putExtra("IdCamarero",idCamarero);
            		intent.putExtra("Personas", numPersonas);
            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton AñadirPlato");
            		
            	}
            }
        });
		//Boton AñadirPlato---------------------------------------------------------------
		
		
		//Boton AñadirBebida--------------------------------------------------------------
		Button aniadirBebida = (Button)findViewById(R.id.aniadirBebida);
		aniadirBebida.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
        		    Intent intent = new Intent(actividad, AnadirBebidas.class);
            		intent.putExtra("NumMesa", numMesa);
            		intent.putExtra("IdCamarero",idCamarero);
            		intent.putExtra("Personas", numPersonas);

            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Catch funcionalidad de boton AñadirBebida");
            		
            	}
            }
        });
		//Boton AñadirBebida--------------------------------------------------------------
	}

	/**
	 * Obtiene los elementos del aArrayList, en funcion de la base de datos y del contenido 
	 * de la mesa actual, que servirá para confeccionar el adapter de la ListView.
	 * 
	 * @return un ArrayList con los elementos de la mesa actual.
	 */
	private ArrayList<ContenidoListMesa> obtenerElementos() {
		ArrayList<ContenidoListMesa> elementos=null;
		try{
			String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio","IdUnico","IdPlato"};
		    String[] numeroDeMesa = new String[]{numMesa};
		    
		    Cursor c = dbMesas.query("Mesas",campos, "NumMesa=?",numeroDeMesa, null,null, null);
		    
		    elementos = new ArrayList<ContenidoListMesa>();
		     
		    while(c.moveToNext())
		    	elementos.add(new ContenidoListMesa(c.getString(0) ,c.getString(2),c.getString(1),Double.parseDouble(c.getString(3)),c.getInt(4),c.getString(5)));
		    	
		    return elementos;
		    
		}catch(Exception e){
			System.out.println("Error en obtenerElementos");
			return elementos;
		}
	}
	
	/*private ArrayList<MesaView> borrarMesaActual(){
		System.out.println("llega");
		Iterator<MesaView> it = listaDeMesas.iterator();
		boolean encontrado=false;
		int i=0;
		
		System.out.println("llega");
		while(it.hasNext() && !encontrado){
			MesaView actual = it.next();
			System.out.println("Numero de mesa actual "+actual.getNumMesa());
			if(actual.getNumMesa().equals(numMesa)){
				System.out.println("Borra la mesa: "+actual.getNumMesa());
				listaDeMesas.remove(i);
				encontrado = true;
			}
			i++;
		}
		return listaDeMesas;
	}*/


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	protected void onClickBotonAceptarAlertDialog(Builder ventanaEmergente,final int posicion) {
		
		ventanaEmergente.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				importarBaseDatatosMesa();
				String nuevosExtrasMarcados = null;
				if(adapterExpandableListEditarExtras != null){ // El plato tiene extras
					nuevosExtrasMarcados = adapterExpandableListEditarExtras.getExtrasMarcados();
				}
		    	
		    	String observacionesNuevas;
		    	if(!actwObservaciones.getText().toString().equals("")){
		    		observacionesNuevas = actwObservaciones.getText().toString();
		    	}else{
		    		observacionesNuevas = adapter.getObservacionesPlato(posicion);
		    	}
		    	
		    	ContentValues platoEditado = new ContentValues();
		    	platoEditado.put("Extras", nuevosExtrasMarcados);
		    	platoEditado.put("Observaciones", observacionesNuevas);
		        String[] camposUpdate = {numMesa,adapter.getIdPlato(posicion),String.valueOf(adapter.getIdPlatoUnico(posicion))};
		        dbMesas.update("Mesas", platoEditado, "NumMesa=? AND IdPlato =? AND IdUnico=?", camposUpdate);
				sqlMesas.close();
				
				adapter.setExtras(posicion,nuevosExtrasMarcados);
				adapter.setObservaciones(posicion,observacionesNuevas);
				platos.setAdapter(adapter);
			}
		});
		
	}
	
	
	private void importarBaseDatatosMesa(){
		try{
			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
		}catch(SQLiteException e){
		 	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS MESA",Toast.LENGTH_SHORT).show();
		}	
	}
	
	public void cargarExpandableListExtras(int posicion){
		HandlerGenerico sqlMiBase=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
		SQLiteDatabase dbMiBase= sqlMiBase.open();
  		String[] campos = new String[]{"Extras"};
  		String[] datos = new String[]{adapter.getIdPlato(posicion)};
  		
  		Cursor cursor = dbMiBase.query("Restaurantes",campos,"Id =?",datos,null,null,null); 
  		cursor.moveToFirst();
  		
  		String extrasPlato = cursor.getString(0);
  		String extrasMarcados = adapter.getExtrasMarcados(posicion);
  		  		
  		if(!extrasPlato.equals("")){
  			String[] tokensExtrasMarcados = extrasMarcados.split(",");
  			String[] tokens = extrasPlato.split("/");
	            ArrayList<PadreExpandableListEditar> categoriasExtras =  new ArrayList<PadreExpandableListEditar>();
		        for(int i= 0; i< tokens.length ;i++){
		        	String[] nombreExtra = null;
		        	String extraSeleccionadoPradreI = tokensExtrasMarcados[i];
					try{
						nombreExtra = tokens[i].split(":");
						
						String categoriaExtraPadre = nombreExtra[0];
						
						// Creamos los hijos, serán la variedad de extras
						String[] elementosExtra = null;

						elementosExtra = nombreExtra[1].split(",");
						
						ArrayList<HijoExpandableListEditar> variedadExtrasListaHijos = new ArrayList<HijoExpandableListEditar>();
						ArrayList<String> extrasHijo = new ArrayList<String>();
						boolean[] extrasPulsados = new boolean[elementosExtra.length];
						for(int j=0; j<elementosExtra.length;j++)
						{
							if(extraSeleccionadoPradreI.contains(elementosExtra[j])){
								extrasPulsados[j] = true;
							}else{
								extrasPulsados[j] = false;
							}
							extrasHijo.add(elementosExtra[j]);
						}
						HijoExpandableListEditar extrasDeUnaCategoria = new HijoExpandableListEditar(extrasHijo, extrasPulsados);
						// Añadimos la información del hijo a la lista de hijos
						variedadExtrasListaHijos.add(extrasDeUnaCategoria);
						PadreExpandableListEditar padreCategoriaExtra = new PadreExpandableListEditar(adapter.getIdPlato(posicion),categoriaExtraPadre, variedadExtrasListaHijos);
						if(i==0){//Expandimos el primer padre por estetica
							padreCategoriaExtra.setExpandido(true);
						}
						// Añadimos la información del padre a la lista de padres
						categoriasExtras.add(padreCategoriaExtra);
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),"Error en el formato de extra en la BD", Toast.LENGTH_SHORT).show();
					}
				}
		        // Creamos el adapater para adaptar la lista a la pantalla.
		    	adapterExpandableListEditarExtras = new MiExpandableListAdapterEditar(getApplicationContext(), categoriasExtras,1);
		        expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);  
  		}else{
  			//Actualizamos el adapter a null, ya que es static, para saber que este plato no tiene extras.
  			adapterExpandableListEditarExtras = null;
  			expandableListEditarExtras.setVisibility(ExpandableListView.INVISIBLE);
  		}
	}

	public static void actualizaListPlatos(ContenidoListMesa platoNuevo){
		adapter.addPlato(platoNuevo);
		platos.setAdapter(adapter);
		precioTotal.setText( Math.rint(adapter.getPrecio()*100/100) +" €");
	}

	public static void actualizaExpandableList() {
		expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListEditarExtras.expandGroup(groupPositionMarcar);
	}
	
	public static MiListAdapterMesa getAdapter(){
		return adapter;
	}
	
	public static ListView getPlatos(){
		return platos;
	}

	public static TextView getPrecioTotal() {
		return precioTotal;
	}

	public static Context getContext() {
		return context;
	}
	
}
