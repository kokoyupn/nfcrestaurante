package com.example.nfcook_camarero;

import java.util.ArrayList;

import adapters.ContenidoListMesa;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.MiListAdapterMesa;
import adapters.PadreExpandableListEditar;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
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
	private ArrayList<MesaView> listaDeMesas;
	private Activity actividad;
	
	private View.OnTouchListener tocuhListener;
	private boolean moviendose;
	private View vista;
	private int inicial, itemId,XinicialEvento,YinicialEvento;
	private boolean noBorrar=false;
	private boolean noVolver=false;
	private boolean entrar=false;
	private boolean primeraVez,mueves;
	
	
	private AutoCompleteTextView actwObservaciones;
	private String restaurante;
	private static String obsAntesEditar;
	private static String extrasAntesEditar;
	
	
	private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private static ExpandableListView expandableListEditarExtras;
	private static Context context;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pedidomesa);
		actividad = this;
		
		//Necesario para actualizar la lista de las mesas al añadir un plato
		context = Mesa.this;
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante = bundle.getString("Restaurante");
		
		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" MESA " + numMesa + ": PEDIDO ACTUAL");
		
		try{
			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
			
			//Añadir platos a la ListView----------------------------------------------------
	  	  	platos = (ListView)findViewById(R.id.listaPlatos);
		    elemLista = obtenerElementos();
	         
	  	    adapter = new MiListAdapterMesa(this, elemLista);
	  	     
	  	    precioTotal = (TextView)findViewById(R.id.precioTotal);
	  	    precioTotal.setText(Double.toString( Math.rint(adapter.getPrecio()*100)/100 )+" €");
	  	     
	  	    platos.setAdapter(adapter);
	  	    

	  	    platos.setOnItemClickListener(new OnItemClickListener() {
	  	    	
	  	    	public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id){
	  	    			System.out.println("EDITAR");
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
	  	    
	  	    //Esta linea llama al evento onTouch de abajo
	  	    tocuhListener = new View.OnTouchListener() {
	  	    	
	  	    	public boolean onTouch(View v, MotionEvent event) {
	            	switch (event.getAction() ) {
	            		case MotionEvent.ACTION_DOWN:
	            			System.out.println("DOWN");
	            			mueves=false;
	            			primeraVez=true;
	            			noVolver=false;
	            			XinicialEvento=(int) event.getRawX();
	            			YinicialEvento=(int) event.getRawY();
	            			
	            			System.out.println("YRawinicial"+YinicialEvento);
	            			
	            			itemId = platos.pointToPosition((int) event.getX(), (int) event.getY());
	        	        	int pos=itemId-platos.getFirstVisiblePosition();
	        	        	vista= platos.getChildAt(pos);
	        	        	
	        	        	int[] coord=new int[2];//Vista
        		        	vista.getLocationOnScreen(coord);
        		        	inicial=coord[0];
        		        	
        		        	//Si le pones un return true no funciona el onclick
        		        	break;
	        	        	
	            		case MotionEvent.ACTION_UP:
	            			
	            			System.out.println("UP");
	            			if(mueves){
		            			if(!noBorrar){
			            			if(moviendose){
			            				
			            				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			            				Display display = wm.getDefaultDisplay();
			            				int ancho = display.getWidth()/4*3;
				            			if (event.getX()>inicial && event.getX() - XinicialEvento > ancho && event.getX() > XinicialEvento){
				            				//Calculas para ver si donde levantas el dedo es la misma vista de donde empezaste
			            		        	int altoVista=vista.getHeight();
			            		        	int[] coordenadas=new int[2];//Vista
			            		        	vista.getLocationOnScreen(coordenadas);//Guarda X e Y
			            		        	int finVista=coordenadas[1]+altoVista;
			            		        	
			            		        	//Entras si sigues con el dedo en la misma vista
			            		        	if(event.getRawY()>coordenadas[1] && event.getRawY()<finVista){
			            		        		try{//FIXME
			            		        		ContenidoListMesa platoSeleccionado = (ContenidoListMesa)adapter.getItem(itemId);
			            		        		String identificador = Integer.toString(platoSeleccionado.getIdRepetido());
			            	    				
			            	    				try{
			            	    					sqlMesas=new HandlerGenerico(context, "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			            	    					dbMesas= sqlMesas.open();
			            	    					
			            	    					dbMesas.delete("Mesas", "IdUnico=?",new String[]{identificador});
			            	    				}catch(Exception e){
			            	    					System.out.println("Error borrar de la base pedido en ondrag");
			            	    				}
			            	    				
			            	    				
			            	    				//Resta 1 a la cantidad de esa posicion del adapter
			            	    				adapter.deletePosicion(itemId);//FIXME probar esto bien
			            	    				
			            	    				
			            	    				
			            	    				//Pones que esa vista tenga una distancia de la izquierda de 0 porque si no la 
			            	    				//siguiente vista la coloca con el margen de donde estuviera el raton al levantar
			            	    				vista.setTranslationX(0);
			            	    				//Notificas que has borrado un elemento del adapter y que repinte la lista
			            	    				adapter.notifyDataSetChanged();
			            	    				
			            	    				//Recalculamos el precio(será cero ya que no quedan platos en la lista)
			            	    				precioTotal.setText(Double.toString( Math.rint( adapter.getPrecio()*100 )/100) +" €");
			            		        	}catch(Exception e){
			            	    				System.out.println("Acceso fuera de rango");
			            		        	}  
			            	            		}
				            		     }else{
				            		    	 //Si no se ha borrado, se devuelve a su sitio
				            				 vista.setTranslationX(0);
				            			 }
			            			}
			            			if(noVolver)
			            				//Si has activado el scroll piedes la seleccion de la vista
			            				noVolver=false;
		            			}
	            			}else
	            				//Si no pasas por el ACTION_MOVE, es un click, asique permites devolviendo false que ejecute el onClick
		                        return false;
	            			
	            			break;
	            		  
	            		case MotionEvent.ACTION_MOVE:
	            			System.out.println("MOVE");
	            			mueves=true;
	            			if(primeraVez){
	            				System.out.println("yRAWEvento"+event.getRawY());
	            				int x,y;
	            				y = (int) Math.abs(event.getRawY()-YinicialEvento);
	            				System.out.println("y"+y);
	            				x = (int) Math.abs(event.getRawX()-XinicialEvento);
	            				System.out.println("x"+x);
	            				if(y>x)
	            					entrar = false;//No hacerlo
	            				else
	            					entrar = true;
	            			}
	            			
	            			if(entrar){
	            				System.out.println("entra");
	            				primeraVez=false;
		            			try{
		            				int xActual=(int) event.getRawX();
		            				int desplazamiento=XinicialEvento-xActual;
		            				
		            				int altoV=vista.getHeight();
	            		        	int[] coordena=new int[2];//Vista
	            		        	vista.getLocationOnScreen(coordena);//Guarda X e Y
	            		        	int finVis=coordena[1]+altoV;
	            		        	
	            		        	//Desplazas la vista si no te sales de ella verticalmente, si es la primera vez y si no has activado el scroll
			            			if(!moviendose && !(event.getRawY()<coordena[1]) && !(event.getRawY()>finVis) && !noVolver){
			            				moviendose=true;
			            				noBorrar=false;
			            				
			            				if(desplazamiento<0){//Si mueves el dedo a la derecha
				            				vista.setTranslationX(event.getRawX()-XinicialEvento);
				            				System.out.println("primera vez que desplaza1");
				            				
				            			}
				            			else if(desplazamiento>0 && vista.getTranslationX()==0)
				            				vista.setTranslationX(0);
				            			
				        	        
			        	        	//Si te sales de la vista, devuelves false para que lo procese el metodo(el que 
			        	        	//activa el scroll) y pierdes el poder desplazar la vista(y la devuelves a su sitio)
			            			}else if( event.getRawY()<coordena[1] || event.getRawY()>finVis ){	 
			            				vista.setTranslationX(0);
			            				noBorrar=true;
			            				noVolver=true;
			            				return false;
			            			
			            			//Si no has perdido la vista, la mueves horizontalmente
			            			}else if(!noVolver){
			            				noBorrar=false;
			            				
			            				if(desplazamiento<=0)//Si desplazas a la derecha
				            				vista.setTranslationX(event.getRawX()-XinicialEvento);
			            				else if(desplazamiento>0 && vista.getTranslationX()==0)
				            				vista.setTranslationX(0);
				            			else if(desplazamiento>0)
				            				vista.setTranslationX(event.getRawX()-XinicialEvento);
				            			else if(desplazamiento==0)
				            				vista.setTranslationX(event.getRawX());
			            				
			            			}
		            			
		            			}catch(Exception e){
		            				System.out.println("catch MOVE");	
		            			}
	            			}else
	            				return false;//Consigues que actue el scroll 
	            			break;
	            	}
	            	return true;//Si esta a false, se pone a la vez scroll y desplazamiento horiz. de la vista
	            }
	        };
	        
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
            		intent.putExtra("Restaurante", restaurante);
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
        		    Intent intent = new Intent(actividad, AnadirBebida.class);
            		intent.putExtra("NumMesa", numMesa);
            		intent.putExtra("IdCamarero",idCamarero);
            		intent.putExtra("Personas", numPersonas);
            		intent.putExtra("Restaurante", restaurante);
            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Catch funcionalidad de boton AñadirBebida "+e.getMessage()+
            				"LOcalizacion: "+e.getCause());
            		
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
		    
		    boolean primero = true;
		    int numElems=0;
		    while(c.moveToNext()){
		    	numElems++;
		    	System.out.println("Elems: "+numElems);
		    	if(primero){
		    		elementos.add(new ContenidoListMesa(c.getString(0) ,c.getString(2),c.getString(1),Double.parseDouble(c.getString(3)),c.getInt(4),c.getString(5)));
		    		System.out.println("Primero: "+c.getString(0));
		    		primero=false;
		    	}else{
		    		int i = 0;
		    		boolean repetido = false;
		    		while(i<elementos.size() && !repetido){
		    			System.out.println("Entra");
	    				String n = elementos.get(i).getNombre();
	    				String e = elementos.get(i).getExtras();
	    				//if(e==null)
	    					//e="";
	    				String o = elementos.get(i).getObservaciones();
	    				//if(o==null)
	    					//o="";
			    		if( n.equals(c.getString(0)) &&
			    			e.equals(c.getString(2)) &&
			    			o.equals(c.getString(1)) ){
			    				System.out.println("Repes: "+c.getString(0));
			    				repetido = true;
			    				elementos.get(i).sumaCantidad();//Le sumas 1 a ese elemento del array que esta repetido
			    		}else
			    			i++;
		    		}
		    		if(!repetido){
		    			elementos.add(new ContenidoListMesa(c.getString(0) ,c.getString(2),c.getString(1),Double.parseDouble(c.getString(3)),c.getInt(4),c.getString(5)));
		    			System.out.println("nombre: "+elementos.get(i).getNombre()+" extras: "+elementos.get(i).getExtras()+" obs: "+elementos.get(i).getObservaciones());
		    		}
		    	}
		    	
		    }
		    	
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
	
	protected void onClickBotonAceptarAlertDialog(Builder ventanaEmergente,final int posicion) {
		
		ventanaEmergente.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				importarBaseDatatosMesa();
				String nuevosExtrasMarcados = null;
				obsAntesEditar = adapter.getObservacionesPlato(posicion);//FIXME probar
				extrasAntesEditar = adapter.getExtrasMarcados(posicion);//FIXME probar
				
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
				
				/*/PRUEBAS--------------
				sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
				dbMesas= sqlMesas.open();
				elemLista = obtenerElementos();
				sqlMesas.close();*/
				//PRUEBA--------------
				
				//sddsfsdf
				ContenidoListMesa aux = (ContenidoListMesa)adapter.getItem(posicion);
				if(nuevosExtrasMarcados==null)
					nuevosExtrasMarcados="";
				if(observacionesNuevas==null)
					observacionesNuevas="";
				
				if(aux.getCantidad()==1){//Si solo hay un elemento(no hay varios iguales), cambia extras y observaciones
					adapter.setExtras(posicion,nuevosExtrasMarcados);
					adapter.setObservaciones(posicion,observacionesNuevas);
				}
				
				
				//mirar si qeda igual q otro------------
				//Si se ha modificado uno y queda igual que algun otro, esta funcion se encarga 
				//ya de buscar y borrar el que mas a la derecha este y de sumar 1 al de la 
				//izquierda
				buscaComunesEditar(aux,posicion,nuevosExtrasMarcados,observacionesNuevas);
				//----------------------------------	
				adapter = new MiListAdapterMesa(actividad, elemLista);	
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
	
	public static boolean buscaComunes(ContenidoListMesa platoNuevo){
		boolean comunes = false;
		String nombre = platoNuevo.getNombre();
		String observaciones = platoNuevo.getObservaciones();
		String extras = platoNuevo.getExtras();
		int i = 0;
		while(i<adapter.getCount() && !comunes){
			ContenidoListMesa elemento = (ContenidoListMesa) adapter.getItem(i);
			System.out.println("PlatoNuevo: "+platoNuevo.getNombre()+"Elemento: "+elemento.getNombre());
			if( (elemento.getNombre()).equals(nombre) && 
				(elemento.getObservaciones()).equals(observaciones) && 
				(elemento.getExtras()).equals(extras) ){
					elemento.sumaCantidad();
					elemento.aniadeId(platoNuevo.getId());
					System.out.println("CantidadElemento: "+elemento.getCantidad());
					comunes = true;
				}
			i++;
		}
		return comunes;
	}
	
	public static void buscaComunesEditar(ContenidoListMesa platoEditar,int posicion,String extrasNuevo,String obsNuevas){
		boolean comunes = false;
		
		
		
		if(platoEditar.getCantidad()==1){
			String nombre = platoEditar.getNombre();
			String observaciones = platoEditar.getObservaciones();
			String extras = platoEditar.getExtras();
			int i = 0;
			while(i<adapter.getCount() && !comunes){
				ContenidoListMesa elemento = (ContenidoListMesa) adapter.getItem(i);
				if( i!=posicion &&
					(elemento.getNombre()).equals(nombre) && 
					(elemento.getObservaciones()).equals(observaciones) && 
					(elemento.getExtras()).equals(extras) ){
						
						comunes = true;
						
						if(i<posicion){
							elemento.sumaCantidad();
							elemento.aniadeId(platoEditar.getId());
							adapter.deletePosicion(posicion);
						}else{
							platoEditar.sumaCantidad();
							platoEditar.aniadeId(elemento.getId());
							adapter.deletePosicion(i);
						}
							
					}
				i++;
			}
		}else if(platoEditar.getCantidad()>1){//Estaban agrupados y se han hecho diferentes
			//ContenidoListMesa elemento = (ContenidoListMesa) adapter.getItem(posicion);//FIXME no se
			//System.out.println("elem(pos): "+elemento.getExtras());
			//System.out.println("platoEditar: "+platoEditar.getExtras());
			
			int idUnicoPlatoNuevo = platoEditar.getIdRepetido();
			platoEditar.eliminaId();
			platoEditar.restaCantidad();
			ContenidoListMesa nuevo = new ContenidoListMesa(platoEditar.getNombre(),extrasNuevo,obsNuevas,platoEditar.getPrecioUnidad(),idUnicoPlatoNuevo,platoEditar.getIdPlato());
			
			String nombre = nuevo.getNombre();
			String observaciones = nuevo.getObservaciones();
			String extras = nuevo.getExtras();
			int i=0;
			boolean comun=false;
			while(i<adapter.getCount() && !comun){
				ContenidoListMesa elemento = (ContenidoListMesa) adapter.getItem(i);
				if( i!=posicion &&
					(elemento.getNombre()).equals(nombre) && 
					(elemento.getObservaciones()).equals(observaciones) && 
					(elemento.getExtras()).equals(extras) ){
						
						comun = true;
						elemento.sumaCantidad();
						elemento.aniadeId(nuevo.getId());
					}
				i++;
			}
			if(!comun)
				adapter.addPlato(nuevo);
		}
		
	}

	public static void actualizaListPlatos(ContenidoListMesa platoNuevo){
		System.out.println("actualizaListPlatos");
		
		if(!buscaComunes(platoNuevo))
			adapter.addPlato(platoNuevo);
		
		platos.setAdapter(adapter);
		precioTotal.setText( Math.rint(adapter.getPrecio()*100)/100 +" €");
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
