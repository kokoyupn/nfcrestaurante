package com.example.nfcook_camarero;




import java.util.StringTokenizer;

import baseDatos.HandlerGenerico;
import fragments.PantallaMesasFragment;
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
	
	    
	/**Metodo que se encarga de cerrar la ventana
	 * */
	public void cerrarVentana(){
	    	this.finish();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_beam_nfc);
		
		context=this;
		
		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" RECOGER CUENTA TPV");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
    	
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		restaurante=bundle.getString("Restaurante");
	
		    
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
	     *Metodo encargado de procesar el mensaje que se le envia de un dispositivo al otro
	     */
	    public void processIntent(Intent intent) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // only one message sent during the beam
	        NdefMessage msg = (NdefMessage) rawMsgs[0];        
	        
	        procesarCuenta(new String(msg.getRecords()[0].getPayload()));
	        
	        //Sonido para confirmar el pedido sincronizado
	        sonidoManager.play(sonido);
	        Toast.makeText(getApplicationContext(),"Cuenta recogida correctamente correctamente.", Toast.LENGTH_LONG).show();
	        cerrarVentana();     
	        
	    }

		
	/**Metodo encargado de procesar el string que le llega por correo.
	* Formato: rest@id+extras*obs@id+extras*obs.....
	* Ejemplo: rest@id1+e1+e2*obs@id2+e1+e2+e3*obs@id*/
    private void procesarCuenta(String string) {
	    	
    	if (estoyEnRestauranteCorrecto()){
	    		
    	} else {
	    		Toast.makeText(this, "Error al recoger la cuenta del TPV. No estas en restaurante correcto.", Toast.LENGTH_LONG);
	    }
	}

	private boolean estoyEnRestauranteCorrecto() {
			// TODO Auto-generated method stub
			return false;
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