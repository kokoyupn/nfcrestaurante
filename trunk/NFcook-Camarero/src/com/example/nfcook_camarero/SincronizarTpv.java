package com.example.nfcook_camarero;




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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class SincronizarTpv extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{ 

	NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
    private String abreviaturaRest;
    
    
	//Variables para los pedidos
	String restaurante;
    String pedido;
    
    
    int numeroRestaurante;
	String abreviatura;
	/*Variables para obtener el valor equivalente del restaurante*/
	String ruta="/data/data/com.example.nfcook_camarero/databases/";
	
	HandlerGenerico sqlMesas;
	SQLiteDatabase dbMesas;
    
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincronizacion_beam_nfc);
        context= this;
                     	
        // Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" SINCRONIZAR PEDIDO");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
        
        Bundle bundle = getIntent().getExtras();
      	restaurante = bundle.getString("Restaurante");
      		
     
      	 pedido= damePedidoStr();
      	     	    	
      	// Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        //    mInfoText = (TextView) findViewById(R.id.textView);
            mInfoText.setText("NFC no esta activo en el dispositivo.");
        } else {
             
          // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
         // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
           
        }
    }
	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }
    
    //---Codificar los platos---------------------------------
	
	
	

	/** 
	 * Prepara el pedido en un string para que sea facil su tratamiento a la hora de escribir en la tag.
	 * Obtiene de la base de datos el pedido a sincronizar con la siguiente forma:
	 * "id_plato@id_plato+extras@5*Obs@id_plato+extras*Obs@";	
	 * "1@2@3@4+10010@5*Con tomate@1+01001*Con azucar@2+10010*Sin macarrones@";	
	 * 
	 * @return
	 */
	private String damePedidoStr() {
		
		//Obtengo los datos del restaurante su numero y abreviatura
        try{ //Abrimos la base de datos para consultarla
 	       	sqlMesas = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Mesas.db"); 
 	        dbMesas = sqlMesas.open();
 	     
 	    }catch(SQLiteException e){
 	        	Toast.makeText(getApplicationContext(),"No existe la base de datos Mesas",Toast.LENGTH_SHORT).show();
 	       }
        String numeroMesa= PantallaMesasFragment.dameMesa();
        
		String listaPlatosStr = dameCodigoRestaurante();
		String[] campos = new String[]{"IdPlato","Observaciones","Extras"};//Campos que quieres recuperar
		String[] datosMesa = new String[]{numeroMesa};	
		Cursor cursorPedido = dbMesas.query("Mesas", campos, "NumMesa=?", datosMesa,null, null,null);
		
    	while(cursorPedido.moveToNext()){
    		listaPlatosStr += cursorPedido.getString(0)+"*"+cursorPedido.getString(1)+"+"+cursorPedido.getString(2)+"@";
    	}
    	System.out.println("PLATOS:"+listaPlatosStr);
    	// para indicar que ha finalizado el pedido escribo un 255 
    	//listaPlatosStr += "255";
    	
    	return listaPlatosStr;
	}
	
	private String dameCodigoRestaurante(){
		// Campos que quieres recuperar
		/*String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);

		cursorPedido.moveToFirst();
		String codigoRest = cursorPedido.getString(0) + "@";
		abreviaturaRest = cursorPedido.getString(1);
		
		return codigoRest;
		*/
		return "";
	}

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
        
    }

    /**
     * Metodo encargado de crear el pedido que queremos enviar
     */
    @SuppressLint("NewApi")
    public NdefMessage createNdefMessage(NfcEvent event) {
    	pedido= "PEDIDO";
          NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/com.example.android.beam", pedido.getBytes())
       
        );
        return msg;
    }
    /**
     * Metodo encargado de cerrar la ventana
     */
    public void cerrarVentana()
    {
    	this.finish();
    }
    
    
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
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
    
    }

    /**
     * Metodo encargado de procesar el mensaje
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
    }


	
}
 