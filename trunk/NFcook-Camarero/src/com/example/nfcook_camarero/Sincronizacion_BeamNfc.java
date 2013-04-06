package com.example.nfcook_camarero;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


public class Sincronizacion_BeamNfc extends Activity  implements CreateNdefMessageCallback,OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
	 
	String numMesa;
	String idCamarero;
	String numPersonas;
	 
	public void cerrarVentana()
	    {
	    	this.finish();
	    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_beam_nfc);
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		context=this;
		 // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            
            mInfoText.setText("NFC no esta activo en el dispositivo.");
        } else {
      	  // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            
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
	        super.onResume();
	        // Check to see that the Activity started due to an Android Beam
	        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
	            processIntent(getIntent());
	        }
	    }

	    @Override
	    public void onNewIntent(Intent intent) {
	        // onResume gets called after this to handle the intent
	        //setIntent(intent);
	    }
	    /**
	     * Parses the NDEF Message from the intent and prints to the TextView
	     */
	    public void processIntent(Intent intent) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        // only one message sent during the beam
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	        // record 0 contains the MIME type, record 1 is the AAR, if present
	        //mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
	        Toast.makeText(getApplicationContext(),"RECIBIDO"+new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
	        cerrarVentana();     
	        
	    }

	    /**
	     * Implementation for the CreateNdefMessageCallback interface
	     */
	  
	
		@SuppressLint("NewApi")
		public NdefMessage createNdefMessage(NfcEvent event) {
	        Time time = new Time();
	        time.setToNow();
	        String text = ("El Pedido que has echo");
	        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
	                "application/com.example.nfcook_camarero", text.getBytes())
	                );
	        return msg;
	    }
		
	/*Menu que usaremos para activar el NFC y el sbeam*/
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