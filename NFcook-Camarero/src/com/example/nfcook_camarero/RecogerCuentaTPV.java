package com.example.nfcook_camarero;

import java.util.StringTokenizer;

import fragments.PantallaMesasFragment;

import baseDatos.HandlerGenerico;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;


public class RecogerCuentaTPV extends Activity  implements OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
    HandlerGenerico sqlRestaurante;
	SQLiteDatabase dbRestaurante;
	
	private static HandlerGenerico sqlMesas;
	private static SQLiteDatabase dbMesas;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	String restaurante, numMesa, numMesaLeida; 
	    
	/**Metodo que se encarga de cerrar la ventana
	 * */
	public void cerrarVentana(){
		this.finish();
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_beam_nfc);
		
		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" RECOGER CUENTA TPV");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
    	
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		restaurante = bundle.getString("Restaurante");
		    
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
        
       procesarCuenta("a");
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
                Toast.makeText(getApplicationContext(), "Pedido Sincronizado.", Toast.LENGTH_LONG).show();
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
	     * Metodo encargado de procesar el mensaje que se le envia de un dispositivo al otro
	     * @param intent
	     */
	    public void processIntent(Intent intent) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // only one message sent during the beam
	        NdefMessage msg = (NdefMessage) rawMsgs[0];        
	        
	        procesarCuentas(new String(msg.getRecords()[0].getPayload()));
	        
	        //Sonido para confirmar el pedido sincronizado
	        sonidoManager.play(sonido);
	        Toast.makeText(getApplicationContext(),"Cuenta recogida correctamente correctamente.", Toast.LENGTH_LONG).show();
	        cerrarVentana();     
	        
	    }

	/***
	 * Obtiene la cuenta de las cuentas separadas por &
	 * Formato: rest & numMesaX / cuentaX & numMesaY / cuentaY & numMesaZ cuentaZ
	 * @param string
	 * @return
	 */
	private void procesarCuentas(String cuentas) {
		
		boolean mesaEncontrada = false;
		String cuenta = "";
		StringTokenizer st = new StringTokenizer(cuentas,"&");
		
		if (estoyEnRestauranteCorrecto(st.nextToken())){
	
			while(st.hasMoreElements() && !mesaEncontrada){
				StringTokenizer stCuenta = new StringTokenizer(st.nextToken(),"/");
				String numMesaCuenta = stCuenta.nextToken();
				cuenta = stCuenta.nextToken();
				mesaEncontrada = numMesaCuenta.equals(numMesa);
			}	
			if (mesaEncontrada) procesarCuenta(cuenta);
			else Toast.makeText(this, "Error al recoger la cuenta. No existe ninguna cuenta para la mesa "+ numMesa, Toast.LENGTH_SHORT).show();
    	} 
		else Toast.makeText(this, "Error al recoger la cuenta. No se encuentra el restaurante correcto", Toast.LENGTH_SHORT).show();	
	}
	

	//Metodo encargado de procesar el string que le llega por correo.
	//FORMATO: id+extras*observaciones*nombrePlato*precio*numPersonas*idUnico*idCamarero*fechaHora@siguientePlato(Igual)
    private void procesarCuenta(String string) {

    	string = "fh8+Poco hecha, Roquefort, Patatas Fritas*_*PLATO ESTRELLA: Director`s Choice*10.85*5*2*idCamarero*fechaHora@";/* +
    			"3+No configurable*_*nombrePlato*4.76*3*61*idCamarero*fechaHora" +
    			"@10+extra1+extra2*OBS*nombrePlato*2.2*4*62*idCamarero*fechaHora";*/
    	
    	try{
    		sqlMesas=new HandlerGenerico(context, "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
			dbMesas= sqlMesas.open();
			
			dbMesas.delete("Mesas", "NumMesa=?",new String[]{numMesa});
			
			StringTokenizer platos = new StringTokenizer(string,"@");
			while(platos.hasMoreTokens()){
				String plato = platos.nextToken();
				StringTokenizer campos = new StringTokenizer(plato,"*");
				String idYExtras = campos.nextToken();
				StringTokenizer idOExtras = new StringTokenizer(idYExtras,"+");
				String id = idOExtras.nextToken();//Tienes el id del plato
				
				String extrasParaBD = "";
				boolean tiene = true;
				boolean primero = true;
				while(idOExtras.hasMoreTokens() && tiene){
					String extra = idOExtras.nextToken();
					if(extra.equals("No configurable"))
						tiene = false;
					else{
						if(primero){
							extrasParaBD = extra;//Vas concatenando los extras
							primero = false;
						}else
							extrasParaBD = extrasParaBD + ", " + extra;
					}
					
				}
				System.out.println("ExtrasParaDB: "+extrasParaBD);
				
				String obs = campos.nextToken();//Tienes las observaciones
				if(obs.equals("_"))
					obs="";
				System.out.println("Observac: "+obs);
				
				String nombrePlato,precio,numPersonas,idUnico,idCamarero,fechaHora;
				nombrePlato = campos.nextToken();
				precio = campos.nextToken();
				numPersonas = campos.nextToken();
				idUnico = campos.nextToken();
				idCamarero = campos.nextToken();
				fechaHora = campos.nextToken();
				
				//Introduces el plato en la base de datos
				ContentValues nuevo = new ContentValues();
	        	nuevo.put("NumMesa", numMesa);
				nuevo.put("IdCamarero", idCamarero);
				nuevo.put("IdPlato", id);
				nuevo.put("Observaciones", obs);
				nuevo.put("Extras", extrasParaBD);
				nuevo.put("FechaHora", fechaHora);
				nuevo.put("Nombre", nombrePlato);
				nuevo.put("Precio",Double.parseDouble(precio));
				nuevo.put("Personas",Integer.parseInt(numPersonas));
				nuevo.put("IdUnico", Integer.parseInt(idUnico));
				nuevo.put("Sincro", 1);
				
	        	dbMesas.insert("Mesas", null, nuevo);
	        }
			
			//Prueba-----------------------------------------
			String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio","IdUnico","IdPlato","Sincro"};
        	String[] numeroDeMesa = new String[]{numMesa};
        	Cursor c = dbMesas.query("Mesas",campos, "NumMesa=?",numeroDeMesa, null,null, null);
        	while(c.moveToNext()){
        		System.out.println(c.getString(0));
		    	String extras=c.getString(2);
		    	if (extras==null)
		    		System.out.println("era null");
		    	else
		    		System.out.println(c.getString(2));
		    	
		    	System.out.println(c.getString(1));
		    	System.out.println(c.getString(3));
		    	System.out.println(c.getString(4));
		    	System.out.println(c.getString(5));
		    	System.out.println("Sincro: " + c.getString(6));
		    }
        	//Prueba-----------------------------------------
        	
        	
			dbMesas.close();
			
		}catch(Exception e){
			System.out.println("Error metodo procesarCuenta de cla clase RecogerCuentaTPV.java");
		}
  
	}

   
	private boolean estoyEnRestauranteCorrecto(String id){
		
		abrirBaseDatos();
		
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);

		cursorPedido.moveToFirst();
		String idRestaurante = cursorPedido.getString(0);
			
		// cerrar
		dbRestaurante.close();
		
		return (idRestaurante.equalsIgnoreCase(id));
	}
   

	private void abrirBaseDatos() {
		// apertura de la base de datos
		sqlRestaurante = null;
		dbRestaurante = null;

		try {
			sqlRestaurante = new HandlerGenerico(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbRestaurante = sqlRestaurante.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS EQUIV REST (estoyEnRestauranteCorrecto)");
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
         if (item.getItemId() == R.id.menu_nfc){
         	intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
         } else if (item.getItemId() ==  R.id.menu_sbeam){
        	 intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS); 
             startActivity(intent);
         } else finish();
         return true;
    }


	
    
  
}