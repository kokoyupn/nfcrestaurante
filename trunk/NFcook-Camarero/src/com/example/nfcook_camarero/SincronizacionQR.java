package com.example.nfcook_camarero;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import baseDatos.HandlerGenerico;
import fragments.PantallaMesasFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;

public class SincronizacionQR extends Activity {
	
	private	HandlerGenerico sqlMesas,sqlRestaurante,sqlEquivalencia;
	private	SQLiteDatabase dbMesas,dbRestaurante,dbEquivalencia;
	private	String numMesa, idCamarero, numPersonas, restaurante, abreviaturaRest, codigoRest; 
	//Fecha y hora
	String formatteHour, formatteDate;
	    
	@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_qr);
		
		//obtenemos datos de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante=bundle.getString("Restaurante");
		
		//sacamos la fecha a la que el camarero ha introducido la mesa
		Calendar cal = new GregorianCalendar();
	    Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		formatteDate = df.format(date);
		
		//sacamos la hora a la que el camarero ha introducido la mesa
		Date dt = new Date();
		SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
		formatteHour = dtf.format(dt.getTime());
		
		//obtenemos el codigo y la abreviatura del rest
		try {
			sqlEquivalencia = new HandlerGenerico(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbEquivalencia = sqlEquivalencia.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR QR");
		}
		obtenerCodigoYAbreviaturaRestaurante();
		dbEquivalencia.close();
        
		//lectura QR
		Intent intent = new Intent("com.example.nfcook_camarero.SCAN");
		intent.putExtra("SCAN_MODE","QR_CODE_MODE");
		startActivityForResult(intent,0);
	}

	
	/**
	 * MEtodo que da valor al codigo y a la abreviatura del restaurante
	 */
	private void obtenerCodigoYAbreviaturaRestaurante() {
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbEquivalencia.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);
		
		cursorPedido.moveToFirst();
		codigoRest = cursorPedido.getString(0);
		abreviaturaRest = cursorPedido.getString(1);
	}

	
	/**
	 * Metodo al que entra cuando se termina la captura del codigo QR
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
           if (resultCode == RESULT_OK) {
        	  // escaneo correcto
              String pedidoQR = intent.getStringExtra("SCAN_RESULT");
              // decodificamos el codigo y se mete en la base de datos
              decodificarPlatos(pedidoQR);
              Toast.makeText(this, "Pedido sincronizado correctamente", Toast.LENGTH_LONG).show();
              finish();
           } else if (resultCode == RESULT_CANCELED) {
              // lectura incorrecta o cancelada
        	  finish(); 
           }
        }
	}
	
	/**
	 * Metodo que decodifica el string que ha leido del QR.
	 * Va comprobando el id, si tiene extras y comentarios hasta que se encuentre un 255 como id
	 * que significa que ha terminado
	 * @param pedidoQR
	 */
	private void decodificarPlatos(String pedidoQR) {

		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(pedidoQR,"@");
		
		String codigoRestQR = stPlatos.nextToken();
		
		// mismo restaurante
		if (codigoRest.equals(codigoRestQR)){
		
			while(stPlatos.hasMoreElements()){
				
				String plato = stPlatos.nextToken();
				StringTokenizer stTodoSeparado =  new StringTokenizer(plato,"+,*");
						
				// id
				String id =  stTodoSeparado.nextToken();
				
				// extras
				String extras = "";
				if (plato.contains("+"))
					extras =  stTodoSeparado.nextToken();
							
				// comentarios
				String observaciones = "";
				if (plato.contains("*"))
					observaciones =  stTodoSeparado.nextToken();
						
				anadirPlatos(abreviaturaRest+id, extras, observaciones);
			}	
		}
		else {
			Toast.makeText(getApplicationContext(), "Los platos sincronizados no corresponden a este restaurante.", Toast.LENGTH_LONG).show();
		}
	}
	
	@SuppressLint("SdCardPath")
	public void anadirPlatos(String idQR, String extrasQR, String ingredientesQR){           
       	
		try{
			sqlRestaurante =new HandlerGenerico(getApplicationContext(), "MiBase.db");
			dbRestaurante = sqlRestaurante.open();
		
			//Campos que quiero recuperar de la base de datos y datos que tengo para consultarla
			String[] campos = new String[]{"Nombre","Precio","Extras","Ingredientes"};
	      	String[] datos = new String[]{restaurante, idQR};
	      	
      		Cursor cursor = dbRestaurante.query("Restaurantes",campos,"Restaurante=? AND Id=?",datos,null,null,null); 
      		cursor.moveToFirst();       
      		dbRestaurante.close();			
	
	      	String extrasSeparadosPorComas = obtenerExtrasSeparadosPorComas(cursor.getString(2));
	        String extrasFinales = compararExtrasQRconBD(extrasSeparadosPorComas, extrasQR);
	        String ingredientesFinales = compararIngredientesQRconBD(cursor.getString(3), ingredientesQR);
	        
	        try{	      	        	
	        	sqlMesas = new HandlerGenerico(getApplicationContext(), "Mesas.db");
	    		dbMesas = sqlMesas.open();
	        	
	   			//Meto el plato en la base de datos Mesas
	       		ContentValues plato = new ContentValues();
	        	int idUnico = PantallaMesasFragment.getIdUnico();
	        	plato.put("NumMesa", numMesa);
	        	plato.put("IdCamarero", idCamarero);
	        	plato.put("IdPlato", idQR);
	        	if (ingredientesFinales.equals("")) plato.put("Observaciones", "Con todos los ingredientes");
		        else plato.put("Observaciones", ingredientesFinales);
	        	if (extrasQR.equals(""))	plato.put("Extras","Sin guarnición");
		        else plato.put("Extras", extrasFinales);
	        	plato.put("FechaHora", formatteDate + " " + formatteHour);
	        	plato.put("Nombre", cursor.getString(0));
	        	plato.put("Precio",cursor.getDouble(1));
	        	plato.put("Personas",numPersonas);
	        	plato.put("IdUnico", idUnico);
	        	plato.put("Sincro", 0);
	        	dbMesas.insert("Mesas", null, plato);  	
	        	dbMesas.close();
		        
		        //FIXME Probar. Añadimos una unidad a las veces que se ha pedido el plato
	        	Mesa.actualizarNumeroVecesPlatoPedido(idQR);
	        	Mesa.pintarBaseDatosMiFav();
	        	
	      	}catch(Exception e){
	    		System.out.println("Error en base de datos de Mesas en anadirPlatos QR");
	      	}
	        
		}catch(SQLiteException e){
	    		 System.out.println("Error en base de datos de MIBASE");
		}
	}

	private String compararExtrasQRconBD(String extrasSeparadosPorComasBD, String extrasQR) {
		StringTokenizer extrasST = new StringTokenizer(extrasSeparadosPorComasBD, ",");
	    String extras = "";
	    int i = 0;
	    //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
	    while (extrasST.hasMoreElements()){ 
	    	 String elem = (String) extrasST.nextElement();
	    	 if (extrasQR.charAt(i)=='1')
	    		 extras +=elem + ", ";
	    	 i++;    	  
	     }
	     //Le quito la ultima coma al extra final para que quede estetico
	     if (extras!= "")
	        extras = extras.substring(0, extras.length()-2);
	     
	     return extras;
	}

	/**
	 * Devuelve en un string los extras separados por comas
	 */
	private String obtenerExtrasSeparadosPorComas(String extrasBD) {
		
		String extras = "";   
		
		// Voy a comprobar los extras que se han escogido comparando el codigo binario que leemos de la tarjeta y los extras de la base de datos.
        StringTokenizer auxExtrasPadre = new StringTokenizer(extrasBD,"/"); //Separo los distintos tipos de extras
     
        // quitamos el padre de los extras para que solo queden los extras separados por comas en extrasFinal
        while (auxExtrasPadre.hasMoreElements()){
        	StringTokenizer auxExtrasHijo = new StringTokenizer((String) auxExtrasPadre.nextElement(),":");   
        	auxExtrasHijo.nextElement(); // eliminamos el padre
            extras += auxExtrasHijo.nextElement() + ","; 
        }
	
        return extras;
	}
	
	/**
	 * Compara los ingredientes de QR con los de la BD, devuelve los marcados
	 */
	private String compararIngredientesQRconBD(String ingredientesBD, String ingredientesQR) {
		StringTokenizer ingredientesST = new StringTokenizer(ingredientesBD, "%");
	    String ingredientes = "";
	    int i = 0;
	    //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
	    while (ingredientesST.hasMoreElements()){ 
	    	 String elem = (String) ingredientesST.nextElement();
	    	 if (ingredientesQR.charAt(i)=='0')
	    		 ingredientes +=elem + ", ";
	    	 i++;    	  
	     }
	     //Le quito la ultima coma al extra final para que quede estetico
	     if (ingredientes!= "")
	    	 ingredientes = ingredientes.substring(0, ingredientes.length()-2);
	     
	     return ingredientes;
	}

}