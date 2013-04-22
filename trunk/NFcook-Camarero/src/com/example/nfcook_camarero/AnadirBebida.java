package com.example.nfcook_camarero;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//import usuario.DescripcionPlato;


import com.example.nfcook_camarero.R;

import fragments.PantallaMesasFragment;
import adapters.ContenidoListMesa;
import adapters.MiGridViewBebidasAdapter;
import adapters.PadreGridViewBebidas;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
//import baseDatos.HandlerDB;

/**
 * Clase en la que el camarero puede añadir o eliminar bebidas al pedido del usuario
 * de una forma muy rapida.
 * 
 * @author Rober 
 *
 */
public class AnadirBebida extends Activity{

	private static HandlerGenerico sql;
	private static SQLiteDatabase dbRestaurante, dbPedido;
	
	private static String numMesa, idCamarero, personasMesa;  
	
	boolean eliminarBebidas, anadirBebidas;
	
	private static GridView gridViewBebidas;
	private static MiGridViewBebidasAdapter adapterGridViewBebidas;
    private static ArrayList<PadreGridViewBebidas> bebidas;
    private static double total;
    
    private static View vistaTabCategoriaBebida;
    
    private static Context contexto;
    
    private static TextView textViewTotalPagarBebidas;
    
    private static Button validar;
    
    /*public static void setRestaurante(String res){
    	restaurante = res;
    }*/
    
    /*public static void setTipoTab(String tipo){
    	tipoTab = tipo;
    }*/
    
    //???
    public static double getTotal(){
		return Math.rint(total*100)/100;
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	contexto = this.getApplicationContext();
		
		//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.tab_superior_categoria_bebidas);
		
		// Declaraciones -------------------------------------
		eliminarBebidas = false;
		anadirBebidas = true;
  		
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		personasMesa = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		
		
		//vistaTabCategoriaBebida = inflater.inflate(R.layout.tab_superior_categoria_bebidas, container, false);
		
		cargarBebidas(this);//ya se importa dentro la base de datos
					
		/*
		 * Este if se encarga de que el usaurio no pierda las bebidas una vez haya ya entrado
		 * en la pestaña bebidas e incluso haber seleccionado alguna. Si entra por el if 
		 * quiere decir que no ha seleccionado ninguna bebida o bien ha sincronizado ya
		 * las bebidas que quería.
		 * 
		 * Sirve para reiniciar las unidades de las bebidas a 0.
		 */
		/*if(bebidas == null){
			// Cargamos las bebidas que haya en la base de datos
			cargarBebidas(getActivity());
			total = 0;
			
			// Precargamos la pantalla bebida si hubiera ya seleccionado bebidas en pedido
			hayBebidasEnPedido(getActivity());
		}*/
		
		// Aplicamos el adapater que hemos creado sobre el gridView
		aplicarAdapter();
		
		validar();
		
		//Hasta qki tb llega
		
    	//return vistaTabCategoriaBebida;
	}
    
    public void validar(){
    	validar = (Button) findViewById(R.id.validar);
    	
    	validar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		anyadirBebidas();
            		
            	}catch(Exception e){
            		System.out.println("Localizacion: "+e.getLocalizedMessage()+
            				" Error: "+ e.getMessage()+" Causa: "+e.getCause());
            	}
            }
    	});
            
    }
    
	// Importamos la base de datos de los restaurantes
    public void importarBaseDatos() {
    	try{
			sql = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","MiBase.db"); 
			dbRestaurante = sql.open();
		}catch(Exception e){
			System.out.println("Error al abrir la base de datos de Foster en anadir Bebidas");
		}
	}

	public  void cargarBebidas(Activity activity){
		try{
			// Creamos el arrayList de bebidas
			bebidas = new ArrayList<PadreGridViewBebidas>();
			PadreGridViewBebidas bebida;
			
			// Importamos la base de datos
			importarBaseDatos();
			
			String[] bebidasBaseDatos = new String[]{"Id","Nombre","Foto","Precio"};
	  		Cursor cursorBaseDatosBebidas = dbRestaurante.query("Restaurantes", bebidasBaseDatos, "Restaurante = 'Foster' AND Categoria = 'Bebidas'",null,null, null,null);
	  		
    	    // Recorremos todos los registros
    	    while(cursorBaseDatosBebidas.moveToNext()){
    	    	bebida = new PadreGridViewBebidas(cursorBaseDatosBebidas.getString(0),cursorBaseDatosBebidas.getString(1),cursorBaseDatosBebidas.getString(2),cursorBaseDatosBebidas.getDouble(3));
    	    	bebidas.add(bebida);
    	    }
    	    
    	    // Cerramos la base de datos de los platos
    	 	sql.close();
    	 	
	    }catch(SQLiteException e){
	        Toast.makeText(activity,"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();	
	    }   
    }
	
	public void aplicarAdapter(){
		//Mostramos el total gastado en bebidas en su textview
		textViewTotalPagarBebidas = (TextView) findViewById(R.id.textViewTotalPagarBebidas);
		textViewTotalPagarBebidas.setText(getTotal() + " €");
		
		// Creamos el adapater de la lista que mostrará la cuenta
		gridViewBebidas = (GridView) findViewById(R.id.gridViewBebidas);
		
        adapterGridViewBebidas = new MiGridViewBebidasAdapter(this, bebidas);
        
        gridViewBebidas.setAdapter(adapterGridViewBebidas);
        
        
        //AKI LLEGA!!!!!!
	}	
	
	public static  void anyadirBebidas(){
		try{
			// Abrimos la base de datos de pedido
			sql = new HandlerGenerico(contexto,"/data/data/com.example.nfcook_camarero/databases/","Mesas.db"); 
			dbPedido = sql.open();
			
			
			for(int i=0; i<bebidas.size();i++){
				// Cargamos la info del plato
			    PadreGridViewBebidas bebida = bebidas.get(i);
		     	
			    int uds = bebida.getUnidades();
			    while(uds > 0){
				    // Metemos la info de la bebida en la base de datos de pedido
				    double precio = bebida.getPrecioUnidad();
					String[] fechaHora = fechaYHora(); 
					String nombreBebida = bebida.getNombre();
					//String pb = nombrePrecioId[1];
					//double precioBebida = Double.parseDouble(pb);
					String idBebida = bebida.getIdPlato();
					int idUnico = PantallaMesasFragment.getIdUnico();//ya suma 1 dentro
				    
					System.out.println("LEga");
					
				    ContentValues nuevaBebida = new ContentValues();
		        	nuevaBebida.put("NumMesa", numMesa);
		        	nuevaBebida.put("IdCamarero", idCamarero);
		        	nuevaBebida.put("IdPlato", idBebida);
		        	nuevaBebida.put("Observaciones", "");
		        	nuevaBebida.put("Extras", "");
		        	nuevaBebida.put("FechaHora", fechaHora[0] + " " + fechaHora[1]); //[0]=fecha [1]=hora
		        	nuevaBebida.put("Nombre", nombreBebida);
		        	nuevaBebida.put("Precio", Double.toString(precio));
		        	nuevaBebida.put("Personas", personasMesa);
		        	nuevaBebida.put("IdUnico", idUnico);
				    
		        	System.out.println("LEga1");
		        	
		        	dbPedido.insert("Mesas", null, nuevaBebida);
					ContenidoListMesa platoNuevo = new ContenidoListMesa(nombreBebida,"", "", precio,idUnico,idBebida);
		        	Mesa.actualizaListPlatos(platoNuevo);
					
		        	uds--;
		        	bebida.eliminaUnidad();
		        	System.out.println("LEga2");
			    }
			}
			
			actualizaGridView();
        	
	    	//plato.put("IdHijo", DescripcionPlato.getIdentificadorUnicoHijoPedido() + "");
	    	
	    	
	    	// Aumentamos el identificador único de pedido
	    	//DescripcionPlato.sumaIdentificadorUnicoHijoPedido();
	    		    	
	    	// Cerramos la base de datos de pedido
			sql.close();
			
			// Aumentamos el precio total de bebidas y el número de unidades de bebida
			/*bebidas.get(pos).anyadeUnidad();
			sumarTotal(pos);*/
			
	    }catch(SQLiteException e){
	         Toast.makeText(vistaTabCategoriaBebida.getContext(),"ERROR AL ABRIR LA BD DE PEDIDO A LA HORA DE INSERTAR UNA BEBIDA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
	    }
	}
	
	public static String[] fechaYHora(){
		//Sacamos la hora a la que el camarero ha introducido la mesa
    	Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatteDate = df.format(date);
        
        Date dt = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        String formatteHour = dtf.format(dt.getTime());
        
        String[] horaFecha = new String[2];
        horaFecha[0] = formatteDate;
        horaFecha[1] = formatteHour;
        
       return horaFecha;
	}
	
	
	public void eliminarBebida(int pos){
		/*if(bebidas.get(pos).getUnidades() > 0){
			try{
				// Abrimos la base de datos de pedido
				sql = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Mesas.db"); 
				dbPedido = sql.open();
				
			 	// Cargamos la info del plato
				String[] bebidasBaseDatos = new String[]{"Id","Nombre","Foto","Precio"};
		  		
				
			    PadreGridViewBebidas bebida = bebidas.get(pos);
			 	String[] camposSacar = new String[]{"IdHijo"};
				String[] datosQueCondicionan = new String[]{restaurante,bebida.getIdPlato()};
				Cursor cursorBaseDatosBebidas = dbRestaurante.query("Restaurantes", bebidasBaseDatos, "Restaurante = 'Foster' AND Categoria = 'Bebidas'",null,null, null,null);
		  		
				Cursor cP = dbRestaurante.query("Pedido", camposSacar, "Id=?",datosQueCondicionan,null, null,null);
		
				// Miramos a ver si existe algun elemento, debe exisitir
			    if(cP.moveToNext()){
			    	String idBebida = cP.getString(0);
			    	String[] camposDelete = {bebida.getIdPlato(),idBebida};
					dbPedido.delete("Pedido", "Id = ? AND IdHijo =?", camposDelete);
			    }
				
				sql.close();
				// Disminuimos el precio total de bebidas y el número de unidades de esa bebida
				restarTotal(pos);
				bebidas.get(pos).eliminaUnidad();
		    }catch(SQLiteException e){
		         	Toast.makeText(vistaTabCategoriaBebida.getContext(),"ERROR AL ABRIR LA BD DE PEDIDO A LA HORA DE ELIMINAR UNA BEBIDA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
		    }
		}*/
	}
	
	public static void actualizaGridView(){
		// Actualizamos cada componente del grid
		//textViewTotalPagarBebidas.setText(getTotal() + " €");
		
		//Actualizamos precio total
		double totalPagar = 0;
		for(int i=0;i<bebidas.size();i++){
			PadreGridViewBebidas elem = bebidas.get(i);
			totalPagar = totalPagar + elem.getPrecioUnidad() * elem.getUnidades();
		}
		
		textViewTotalPagarBebidas.setText(Double.toString(totalPagar) + " €");
		
		
		// Actualizamos el adapter		
		adapterGridViewBebidas.notifyDataSetChanged();
	}
	
	public static void sumarTotal(int pos){
		total += bebidas.get(pos).getPrecioUnidad();
	}
	
	public static void restarTotal(int pos){
		if(total > 0){
			total -= bebidas.get(pos).getPrecioUnidad();
		}
	}
	
	/*
	 * Metodo encargado de reiniciar los contadores de bebidas seleccionadas y el precio total.
	 * Este método se llamará en el momento en que se produzca la sincronización, mientras
	 * tanto las undiades de bebidas que hemos seleccionado permanecerán intactas.
	 */
	public void reiniciarPantallaBebidas(Activity activity){
		bebidas = null;
		total = 0;
					
		// Cargamos las bebidas que haya en la base de datos
		cargarBebidas(this);
	}
	
	/*public static void hayBebidasEnPedido(Activity activity){
		boolean encontrado;
		int numBebidas = bebidas.size();
		int i;
		try{
			// Abrimos la base de datos de pedido
			sqlPedido = new HandlerDB(activity,"Pedido.db"); 
		 	dbPedido = sqlPedido.open();
		 	
		 	// Cargamos los id de todos los platos que hay por si hubiera alguna bebida
		 	String[] camposSacarPedido = new String[]{"Id"};
			String[] datosQueCondicionanPedido = new String[]{restaurante};
			Cursor cP = dbPedido.query("Pedido", camposSacarPedido, "Restaurante=?", datosQueCondicionanPedido,null, null,null);
	
			// Abrimos la base de datos de los platos
	    	sql = new HandlerDB(activity); 
	     	db = sql.open();
	     	
			// Miramos para cada plato si es bebida
		    while(cP.moveToNext()){		     	
		     	String[] camposSacarPlato = new String[]{"Id","Nombre","Precio"};
		    	String[] datosQueCondicionanPlato = new String[]{cP.getString(0),restaurante,tipoTab};
	    		Cursor cPlato = db.query("Restaurantes", camposSacarPlato, "Id=? AND Restaurante=? AND Categoria=?",datosQueCondicionanPlato,null, null,null);
	    		
	    		// Miramos si efectivamente era una bebida
	    		if(cPlato.moveToNext()){
	    			// Miramos en que posición está la bebida para sumarle unidades
	    			encontrado = false;
	    			i = 0;
	    			while(!encontrado && i<numBebidas){
	    				if(bebidas.get(i).getIdPlato().equals(cPlato.getString(0))){
	    					encontrado = true;
	    				}else{
	    					i++;
	    				}
	    			}
	    			
	    			// Salimos con la pos en dónde esta la bebida y actualizamos la misma
	    			bebidas.get(i).anyadeUnidad();
	    			sumarTotal(i);
	    			
	    		} // Fin if es bebida
		    } // Fin platos cuenta
		    
		    // Cerramos las bases de datos de pedido y de platos
		    sql.close();
		    sqlPedido.close();
		    
	    }catch(SQLiteException e){
	         	Toast.makeText(activity,"ERROR AL INTENTAR CARGAR BEBIDAS DE LA BD CUENTA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
	    }
	}*/
	
	/*public static void eliminarBebidaDesdePedido(String idPlato, Activity activity){
		if(bebidas == null){
			// Cargamos las bebidas que haya en la base de datos
			cargarBebidas(activity);
			total = 0;
			
			// Precargamos la pantalla bebida si hubiera ya seleccionado bebidas en pedido
			hayBebidasEnPedido(activity);
		}
		
		boolean encontrado = false;
		int numBebidas = bebidas.size();
		int i = 0;
		
		// Buscamos la bebida para quitarle una unidad
		while(i<numBebidas && !encontrado){
			if(bebidas.get(i).getIdPlato().equals(idPlato)){
				encontrado = true;
			}else{
				i++;
			}
		}
		
		// Una vez encontrado le quitamos una unidad para que se vea luego reflejado
		bebidas.get(i).eliminaUnidad();
		
		// Actualizamos el total gastado en bebidas
		restarTotal(i);
	}*/
	
}
