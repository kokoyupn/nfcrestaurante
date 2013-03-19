package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.Iterator;


import adapters.MiListAdapterMesa;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;

import adapters.ContenidoListMesa;

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
	private TextView precioTotal;
	private int indicePulsado;
	private ArrayList<MesaView> listaDeMesas;
	private Activity actividad;
	private int x;
	
	
	private AutoCompleteTextView actwObservaciones;
	
	
	private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private static ExpandableListView expandableListEditarExtras;
	private static Context context;
	

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
		mesa.setText("Mesa: "+ String.valueOf(numMesa) );
		
		try{
			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
			
			//Añadir platos a la ListView----------------------------------------------------
	  	  	platos = (ListView)findViewById(R.id.listaPlatos);
		    elemLista = obtenerElementos();
	         
	  	    adapter = new MiListAdapterMesa(this, elemLista);
	  	     
	  	    precioTotal = (TextView)findViewById(R.id.precioTotal);
	  	    precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
	  	     
	  	    platos.setAdapter(adapter);
	  	    

	  	    platos.setOnItemClickListener(new OnItemClickListener() {
	  	    	
	  	    	public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id){
	  	    		
	  	    		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(Mesa.this);
	  	    		ventanaEmergente.setNegativeButton("Cancelar", null);
	  				onClickBotonAceptarAlertDialog(ventanaEmergente, posicion);
	  				//onClickBotonCancelarAlertDialog(ventanaEmergente);
	  				View vistaAviso = LayoutInflater.from(Mesa.this).inflate(R.layout.ventana_emergente_editar_anadir_plato, null);
	  				expandableListEditarExtras = (ExpandableListView) vistaAviso.findViewById(R.id.expandableListViewExtras);
	  				TextView tituloPlato = (TextView) vistaAviso.findViewById(R.id.textViewTituloPlatoEditarYAnadir);
	  				actwObservaciones = (AutoCompleteTextView) vistaAviso.findViewById(R.id.autoCompleteTextViewObservaciones);
	  				tituloPlato.setText(adapter.getNombrePlato(posicion));
	  				actwObservaciones.setText(adapter.getObservacionesPlato(posicion));
	  				cargarExpandableListExtras(posicion);
	  				ventanaEmergente.setView(vistaAviso);
	  				ventanaEmergente.show();
	  	    	}
	  	    });
	  	    
	  	    
	  	    platos.setOnItemLongClickListener(new OnItemLongClickListener(){
	  	    	public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
	  	    		indicePulsado=position;
	  	    		
					DragShadowBuilder myShadow = new DragShadowBuilder(v);
					
					/*/Prueba
					View borrando;
					LayoutInflater inflater = (LayoutInflater) actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				    borrando = inflater.inflate(com.example.nfcook_camarero.R.layout.hijo_mesa_borrado, null);
				    v.setBackgroundColor(Color.BLACK);
				    //Prueba*/
							
					
					//ClipData info = ClipData.newPlainText("posicion", Integer.toString(position));
					v.startDrag(null,myShadow,v,0);
					
					return true;					
				}});
	  	    
	  	   platos.setOnDragListener(new OnDragListener() {
		    	public boolean onDrag(View view, DragEvent event) {
		    		
		    		//Las acciones se realizan al soltar el elemento de la lista arrastrado.
		    		if(event.getAction()==DragEvent.ACTION_DRAG_ENDED){
		    			
		    			ContenidoListMesa platoSeleccionado = (ContenidoListMesa) adapter.getItem(indicePulsado);
	    				String identificador = Integer.toString(platoSeleccionado.getId());
	    				try{
	    					dbMesas.delete("Mesas", "IdUnico=?",new String[]{identificador});
	    				}catch(Exception e){
	    					System.out.println("Error borrar de la base pedido en ondrag");
	    				}
	    				
	    				adapter.deletePosicion(indicePulsado);
	    				platos.setAdapter(adapter);
	    				
	    				//Recalculamos el precio(será cero ya que no quedan platos en la lista)
	            		precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
		    		
		    		}
		    	    return true;
		    	}
			});
	  	}catch(Exception e){
			System.out.println("Error lectura base de datos de Pedido");
		}
		
		
		//Boton Cobrar--------------------------------------------------------------------
		Button cobrar = (Button)findViewById(R.id.botonCobrar);
		cobrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		sqlHistorico=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Historico.db");
        			dbHistorico= sqlHistorico.open();
        			
            		String[] numeroDeMesa = new String[]{numMesa};
        		    Cursor filasPedido = dbMesas.query("Mesas", null, "NumMesa=?", numeroDeMesa,null, null, null);
            		Cursor filasHistorico = dbHistorico.query("Historico", null, null,null, null,null, null);
            		System.out.println("LLEGA");
            		
            		while(filasPedido.moveToNext()){
            			//Añades los platos a la base de datos del historico y borras de la lista de platos
            			ContentValues nuevo = new ContentValues();
            			int plato=0;
            			
            			for (int i=0;i<filasPedido.getColumnCount();i++){
            				for (int j=0;j<filasPedido.getColumnCount();j++){
            					if(filasPedido.getColumnName(i).equals(filasHistorico.getColumnName(j))){
            						nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));
            						
	            					if(filasPedido.getColumnName(i).equals("IdUnico")){	
	            						plato = Integer.parseInt(filasPedido.getString(i));
	            						adapter.deleteId(plato);
	            					}
	            				}
            				}
            			}
            			/*FIXME No borrar por si las bases de historico y pedido tienen las mismas columnas
            			for (int i=0;i<filasPedido.getColumnCount();i++){
            				nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));
            				if(filasPedido.getColumnName(i).equals("IdUnico")){	
        						plato = Integer.parseInt(filasPedido.getString(i));
        						adapter.deleteId(plato);
        					}
            			}*/
            			dbHistorico.insert("Historico", null, nuevo);
	            	}
            		
            		//Carga el adapter sin los platos borrados
            		platos.setAdapter(adapter); 
            		
            		//Recalculamos el precio(será cero ya que no quedan platos en la lista)
            		precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
            		
            		//Borra de la base de datos los platos de esta mesa
            		dbMesas.delete("Mesas", "NumMesa=numMesa", null);
            		
            		//Se borra la mesa y se vuelve a la pantalla anterior.
            		InicialCamarero.eliminarDeArray(numMesa);
            		finish();
            			
            		
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton cobrar");
            	}
            }
        });
		//Boton Cobrar--------------------------------------------------------------------
		
		
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
            		importarBaseDatatosMesa();
            		String[] campos = new String[]{"IdCamarero","Personas"};
        		    String[] numeroDeMesa = new String[]{numMesa};
        		    
        		    Cursor c = dbMesas.query("Mesas",campos, "NumMesa=?",numeroDeMesa, null,null, null);
        		    c.moveToNext();
        		    
        		    Intent intent = new Intent(actividad, AnadirBebidas.class);
            		intent.putExtra("numMesa", numMesa);
            		intent.putExtra("idCamarero",c.getString(0));
            		intent.putExtra("personasMesa", c.getString(1));
            		sqlMesas.close();
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
		    	elementos.add(new ContenidoListMesa(c.getString(0) ,c.getString(2),c.getString(1),Float.parseFloat(c.getString(3)),c.getInt(4),c.getString(5)));
		    	
		    return elementos;
		    
		}catch(Exception e){
			System.out.println("Error en obtenerElementos");
			return elementos;
		}
	}
	
	private ArrayList<MesaView> borrarMesaActual(){
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
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	protected void onClickBotonAceptarAlertDialog(Builder ventanaEmergente,final int posicion) {
		
		ventanaEmergente.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
			
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
		HandlerGenerico sqlMesas = null;
		SQLiteDatabase dbMesas = null;
		try{
			sqlMesas=new HandlerGenerico(context, "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
		}catch(SQLiteException e){
		 	Toast.makeText(context,"NO EXISTE BASE DE DATOS MESA",Toast.LENGTH_SHORT).show();
		}
		adapter.addPlato(platoNuevo);
		platos.setAdapter(adapter);
	}

	public static void actualizaExpandableList() {
		expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListEditarExtras.expandGroup(groupPositionMarcar);
	}
	
}
