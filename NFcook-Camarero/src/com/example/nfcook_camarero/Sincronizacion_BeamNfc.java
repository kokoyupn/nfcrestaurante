package com.example.nfcook_camarero;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import fragments.PantallaMesasFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;


public class Sincronizacion_BeamNfc extends Activity  implements OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
	 
	//Variables usadas en la manipulacion e bases de datos	
	HandlerGenerico sqlMesas,sqlrestaurante;
	SQLiteDatabase dbMesas,dbMiBase;
	
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	String restaurante;
	int numeroRestaurante;
	String abreviatura;
	/*Variables para obtener el valor equivalente del restaurante*/
	String ruta="/data/data/com.example.nfcook_camarero/databases/";
	private SQLiteDatabase dbEquivalencia;
	private HandlerGenerico sqlEquivalencia;
	
	
	//Fecha y hora
	String formatteHour;
	String formatteDate;
	    
	/**Metodo que se encarga de cerrar la ventana
	 * */
	public void cerrarVentana()
	    {
	    	this.finish();
	    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_beam_nfc);
		
		context=this;
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		restaurante=bundle.getString("Restaurante");
		//restaurante="Foster";
		
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
 	        
		//Creamos la instacia del manager de sonido
		sonidoManager = new SonidoManager(getApplicationContext());
		// Pone el volumen al volumen del movil actual
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Cargamos el sonido
        sonido=sonidoManager.load(R.raw.confirm);
        
		 // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            
            mInfoText.setText("NFC no esta activo en el dispositivo.");
        } else {
      	  // Register callback to set NDEF message
            //mNfcAdapter.setNdefPushMessageCallback(this, this);
        	
        	// Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
	}
	
	
	 /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }
    
    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Pedido Sincronizado", Toast.LENGTH_LONG).show();
                cerrarVentana();
                break;
            }
        }
    };
    

    @Override
	public void onPause(){
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
		
	}
	 @Override
	   public void onResume() {
	        super.onResume();//esto
	        // Check to see that the Activity started due to an Android Beam
	        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
	            processIntent(getIntent());
	        }
	    }

	    @Override
	    public void onNewIntent(Intent intent) {
	        setIntent(intent);
	    	
	    }
	   
	    /**
	     *Metodo encargado de procesar el mensaje que se le envia de un dispositivo al otro
	     */
	    public void processIntent(Intent intent) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // only one message sent during the beam
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	        
	        Toast.makeText(getApplicationContext(),"Pedido sincronizado correctamente", Toast.LENGTH_LONG).show();

	        //--------Metodos para añadir a la base de datos mesas
	        decodificar(new String(msg.getRecords()[0].getPayload()));
	        //Sonido para confirmar el pedido sincronizado
	        sonidoManager.play(sonido);
	        cerrarVentana();     
	        
	    }
	/**
	 * Metodo que dado una lista de platos se encarga de añadir a la base de datos de mesas los platos que ha elegido el usuario
	 * @param listaPlatosStr: lista de platos que tenemos que añadir a la base de datos
	 */
	public void decodificar (String listaPlatosStr)
	{
		boolean parar=false;
		// separamos por platos
				StringTokenizer stPlatos = new StringTokenizer(listaPlatosStr,"@");
				
				while(stPlatos.hasMoreElements()){
					//Para cada plato lo decodificamos y lo añadimos a la base de datos
					String plato = stPlatos.nextToken();
					StringTokenizer stTodoSeparado =  new StringTokenizer(plato,"+,*");
					
					String extras,comentario;
					extras=comentario="";
					// id
					int id =  Integer.parseInt(stTodoSeparado.nextToken());
					parar= id==255;
					if(!parar){//Si no ha acabado el mensaje		
						// extras
						if (plato.contains("+"))  {
							extras =  stTodoSeparado.nextToken();
								
						}
						// comentarios
						if (plato.contains("*"))  {
							comentario =  stTodoSeparado.nextToken();
						}
						añadirPlatos(restaurante,abreviatura+id,extras,comentario);
					}			
				}
				
	}
	

	public void añadirPlatos(String restaurante,String id,String extras,String observaciones)
	{
		
    		
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
                
                int numExtras=0;
                //Recorrro cada uno de los elementos que se me han generado en el sring tokenizer que son de la forma Guarnicion:PatatasAsada,Ensalada
                while (auxExtras.hasMoreElements())
                	{auxExtras2= new StringTokenizer((String) auxExtras.nextElement(),":");
                	 //Elimino el Guarnicion:/Salsa:/Guarnicion:
                	 auxExtras2.nextElement();
                     extrasFinal +=auxExtras2.nextElement()+",";
                	}
                //Extras final tiene todos los extras de ese plato separados por comas
                auxExtras= new StringTokenizer(extrasFinal,",");
                extrasFinal="";
                //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
                while (auxExtras.hasMoreElements())
            	  { elemento= (String) auxExtras.nextElement();
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
	        	
	        	plato.put("NumMesa",PantallaMesasFragment.dameMesa());
	        	plato.put("IdCamarero",PantallaMesasFragment.dameCamarero()); 
	        	plato.put("IdPlato", id);
	        	plato.put("Observaciones", observaciones);
	        	plato.put("Extras",extrasFinal);
	        	plato.put("FechaHora", formatteDate + " " + formatteHour);
	        	plato.put("Nombre", cursor.getString(0));
	        	plato.put("Precio",cursor.getDouble(1));
	        	plato.put("Personas",PantallaMesasFragment.dameNumPersonas());
	        	plato.put("IdUnico", PantallaMesasFragment.getIdUnico());
	        	dbMesas.insert("Mesas", null, plato);
	        	dbMesas.close();
	              	
      		}catch(Exception e){
    		
    		}
   
	}

		
	/*Menu que usaremos para activar el NFC y el beam*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nfc, menu);
        
        return true;
    }
    
   
	public boolean onOptionsItemSelected(MenuItem item) {
         Intent intent;
            switch (item.getItemId()) {
            case R.id.menu_nfc:
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_sbeam:
                 intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
              
                startActivity(intent);
                return true;
           
            default:
                return super.onOptionsItemSelected(item);
        }
        }


	
    
  
}