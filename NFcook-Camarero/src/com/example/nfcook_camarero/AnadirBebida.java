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
import android.app.ActionBar;
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
    private String restaurante;
    
    public static double getTotal(){
		return Math.rint(total*100)/100;
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	contexto = this.getApplicationContext();
		
    	// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" AÑADIR BEBIDA");
        
		setContentView(R.layout.tab_superior_categoria_bebidas);
		
		// Declaraciones -------------------------------------
		eliminarBebidas = false;
		anadirBebidas = true;
  		
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		personasMesa = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante = bundle.getString("Restaurante");
		
		cargarBebidas(this);//ya se importa dentro la base de datos
					
		aplicarAdapter();
		
		validar();
	}
    
    public void validar(){
    	validar = (Button) findViewById(R.id.validar);
    	
    	validar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		anyadirBebidas();
            		finish();
            		
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
			String[] rest = new String[]{restaurante};
	  		Cursor cursorBaseDatosBebidas = dbRestaurante.query("Restaurantes", bebidasBaseDatos, "Restaurante =? AND Categoria = 'Bebidas'",rest,null, null,null);
	  		
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
					
					String idBebida = bebida.getIdPlato();
					int idUnico = PantallaMesasFragment.getIdUnico();//ya suma 1 dentro
					PantallaMesasFragment.getInstanciaClase().setUltimoIdentificadorUnico();
		        	
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
		        	System.out.println(idUnico);
				    
		        	System.out.println("LEga1");
		        	
		        	dbPedido.insert("Mesas", null, nuevaBebida);
		        	
		        	Mesa.actualizaListPlatos();
					
		        	uds--;
		        	bebida.eliminaUnidad();
		        	System.out.println("LEga2");
			    }
			}
			
			actualizaGridView();
        	
	    	// Cerramos la base de datos de pedido
			sql.close();
			
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
	
	public static void actualizaGridView(){
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
	
}
