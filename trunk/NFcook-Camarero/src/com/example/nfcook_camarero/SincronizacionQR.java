package com.example.nfcook_camarero;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import baseDatos.HandlerGenerico;

import fragments.PantallaMesasFragment;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;


public class SincronizacionQR extends Activity {
	
	private	HandlerGenerico sqlMesas,sqlrestaurante,sqlEquivalencia;;
	private	String numMesa;
	private	String idCamarero;
	private	String numPersonas; 
	private	SQLiteDatabase dbMesas,dbMiBase,dbEquivalencia;
	
	String restaurante;
	int numeroRestaurante;
	String abreviatura;
	
	//Fecha y hora
	String formatteHour;
	String formatteDate;
	    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_qr);
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante=bundle.getString("Restaurante");
		
		
		//Fecha y hora 
				//Sacamos la fecha a la que el camarero ha introducido la mesa
		Calendar cal = new GregorianCalendar();
	    Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		formatteDate = df.format(date);
		//Sacamos la hora a la que el camarero ha introducido la mesa
		Date dt = new Date();
		SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
		formatteHour = dtf.format(dt.getTime());
		
		 //Obtengo los datos del restaurante su numero y abreviatura
        try{ //Abrimos la base de datos para consultarla
 	       	sqlEquivalencia = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Equivalencia_Restaurantes.db"); 
 	        dbEquivalencia = sqlEquivalencia.open();
 	     
 	    }catch(SQLiteException e){
 	        	Toast.makeText(getApplicationContext(),"No existe la base de datos equivalencia",Toast.LENGTH_SHORT).show();
 	       }
 	   
 	   try{
 		  /**Campos de la base de datos Restaurante TEXT,Numero INTEGER,Abreviatura TEXT
 	        * Nombre de la tabla de esa base de datos Restaurantes*/		
 		   String[] campos = new String[]{"Numero","Abreviatura"};
 		   String[] datos = new String[]{restaurante};
 		   //Buscamos en la base de datos el nombre de usuario y la contraseña
 		   Cursor c = dbEquivalencia.query("Restaurantes",campos,"Restaurante=?",datos, null,null, null);
 	  	   
 	  	   c.moveToFirst();
        	 
 	  	   numeroRestaurante = c.getInt(0);
 	  	   abreviatura = c.getString(1);
 	  	   
 	  	   System.out.println("NUMERO"+numeroRestaurante+"ABREVIATURA"+abreviatura);
 	  	
 		}catch(Exception e){ }
        
		//Lectura QR
		Intent intent = new Intent("com.example.nfcook_camarero.SCAN");
		intent.putExtra("SCAN_MODE","QR_CODE_MODE");
		startActivityForResult(intent,0);
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
		
		String a= stPlatos.nextToken();
		int numero= Integer.parseInt(a);
		// Compruebo si el pedido que hemos leido corresponde a este restaurate.
		if (numeroRestaurante==numero)
		{
		
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
					
			anadirPlatos(restaurante, abreviatura+id, extras, observaciones);
		}	
		}
		else {
			Toast.makeText(getApplicationContext(), "Los platos sincronizados no corresponden a este restaurante.", Toast.LENGTH_LONG).show();
			
		}
	}
	
	public void anadirPlatos(String restaurante,String id,String extras,String observaciones){
          
    	
        Cursor cursor = null;
        String extrasFinal="";
            
        try{
    		sqlrestaurante =new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
    		dbMiBase = sqlrestaurante.open();
    			
        	//Campos que quiero recuperar de la base de datos
    		String[] campos = new String[]{"Nombre","Precio","Extras"};
    		//Datos que tengo para consultarla
          	String[] datos = new String[]{restaurante,id};
          		
          	cursor = dbMiBase.query("Restaurantes",campos,"Restaurante=? AND Id=?",datos,null,null,null); 
          	cursor.moveToFirst();       
            dbMiBase.close();
            // Voy a comprobar los extras que se han escogido comparando el codigo binario que leemos de la tarjeta y los extras de la base de datos.
            //Obtengo los extras de la base de datos
            String extrasBaseDatos= cursor.getString(2);
            //Separo los distintos tipos de extras
            StringTokenizer auxExtras= new StringTokenizer(extrasBaseDatos,"/");
            StringTokenizer auxExtras2 = null;
            String elemento = "";
         
            int numExtras = 0;
            //Recorrro cada uno de los elementos que se me han generado en el string tokenizer que son de la forma Guarnicion:PatatasAsada,Ensalada
            while (auxExtras.hasMoreElements()){
            	auxExtras2= new StringTokenizer((String) auxExtras.nextElement(),":");
            	//Elimino el Guarnicion:/Salsa:/Guarnicion:
                auxExtras2.nextElement();
                extrasFinal += auxExtras2.nextElement() + ",";
             }
             //Extras final tiene todos los extras de ese plato separados por comas
             auxExtras = new StringTokenizer(extrasFinal,",");
             extrasFinal="";
             //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
             while (auxExtras.hasMoreElements()){ 
            	 elemento= (String) auxExtras.nextElement();
            	 if (extras.charAt(numExtras)=='1')
            		 extrasFinal +=elemento+", ";
            	 numExtras++;    	  
             }
             //Le quito la ultima coma al extra final para que quede estetico
             if (extrasFinal!= "")
                extrasFinal=extrasFinal.substring(0,extrasFinal.length()-2);
    	}catch(SQLiteException e){
    		 System.out.println("Error lectura base de datos de MIBASE");
    	}
    			
      	try{
       		//Abro base de datos para introducir los platos en esa mesa
       		sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
       		dbMesas= sqlMesas.open();
       		//Meto el plato en la base de datos Mesas
	       	ContentValues plato = new ContentValues();
	        int idUnico = PantallaMesasFragment.getIdUnico();
	        plato.put("NumMesa", numMesa);
	        plato.put("IdCamarero", idCamarero);
	        plato.put("IdPlato", id);
	        if (observaciones.equals(""))
	        	plato.put("Observaciones", "Sin observaciones");
	        else 
	        	plato.put("Observaciones", observaciones);
	        if (extras.equals(""))
	        	plato.put("Extras","Sin guarnición");
	        else 
	        	plato.put("Extras",extrasFinal);
	        plato.put("FechaHora", formatteDate + " " + formatteHour);
	        plato.put("Nombre", cursor.getString(0));
	        plato.put("Precio",cursor.getDouble(1));
	        plato.put("Personas",numPersonas);
	        plato.put("IdUnico", idUnico);
	        plato.put("Sincro", 0);
	        dbMesas.insert("Mesas", null, plato);
	        dbMesas.close();
	        
	        //FIXME Probar. Añadimos una unidad a las veces que se ha pedido el plato
        	Mesa.actualizarNumeroVecesPlatoPedido(id);
        	Mesa.pintarBaseDatosMiFav();
        	
      	}catch(Exception e){
    		//System.out.println("Error lectura base de datos de Mesas");
      	}
   
	}
}