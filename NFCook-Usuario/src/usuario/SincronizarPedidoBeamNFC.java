package usuario;

import baseDatos.HandlerDB;

import com.example.nfcook.R;



import fragments.ContenidoTabSuperiorCategoriaBebidas;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SincronizarPedidoBeamNFC extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{ 

	NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
    
    //Variables para bases de datos
    private HandlerDB sqlCuenta, sqlPedido, sqlRestaurante ;
	private SQLiteDatabase dbCuenta, dbPedido, dbRestaurante;
    
	//Variables para los pedidos
	String restaurante, pedido, abreviaturaRest, codigoRest;
   
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincronizar_pedido_beam_nfc);
        context= this;                     	
       
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	 // Ponemos el t�tulo a la actividad
        actionbar.setTitle(" SINCRONIZAR PEDIDO");
    	// Cambiamos el fondo al ActionBar
    	actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B45F04")));
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
        
        Bundle bundle = getIntent().getExtras();
      	restaurante = bundle.getString("Restaurante");
      		
     
      	//Abro las base de datos parq procesarlas
      	abrirBasesDeDatos();
      	//Obtengo el pedido que quiero enviar
        pedido = damePedidoStr();
      	     	    	
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
	 * Abre las bases de datos Cuenta y Pedido.
	 */
    private void abrirBasesDeDatos() {
		sqlCuenta = null;
		sqlPedido = null;
		sqlRestaurante = null;
		dbCuenta = null;
		dbPedido = null;
		dbRestaurante = null;

		try {
			sqlPedido = new HandlerDB(getApplicationContext(), "Pedido.db");
			dbPedido = sqlPedido.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosPedido)");
		}
		try {
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS CUENTA: SINCRONIZAR NFC (cargarBaseDeDatosCuenta)");
		}
		try {
			sqlRestaurante = new HandlerDB(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbRestaurante = sqlRestaurante.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosResta)");
		}
	}
	
	/**
	 * Borra de la base de datos Pedido.db los platos que haya para introducirlos en la base
	 * de datos Cuenta.db
	 */
	private void enviarPedidoACuenta(){
		
		//Campos que quieres recuperar
		String[] campos = new String[]{"Id","Plato","Ingredientes","Extras","PrecioPlato","Restaurante","IdHijo"};
		String[] datosRestaurante = new String[]{restaurante};	
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);
    	
    	while(cursorPedido.moveToNext()){
    		ContentValues platoCuenta = new ContentValues();
        	platoCuenta.put("Id", cursorPedido.getString(0));
        	platoCuenta.put("Plato", cursorPedido.getString(1));
        	platoCuenta.put("Ingredientes", cursorPedido.getString(2));
        	platoCuenta.put("Extras", cursorPedido.getString(3));
        	platoCuenta.put("PrecioPlato",cursorPedido.getDouble(4));
        	platoCuenta.put("Restaurante",cursorPedido.getString(5));
        	platoCuenta.put("IdHijo", cursorPedido.getString(6));
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    	}
		
		try{
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);	
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"ERROR AL BORRAR BASE DE DATOS PEDIDO",Toast.LENGTH_SHORT).show();
		}
		
		// Reinciamos la pantalla bebidas, porque ya hemos sincronizado el pedido
		ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas();
		
	}	
	

	/**
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos(){
		sqlCuenta.close();
		sqlPedido.close();		
		sqlRestaurante.close();
	}
		
	/** 
	 * Devuelve un string con el id del restaurante + el pedido
	 * @return
	 */
	private String damePedidoStr() {
		obtenerCodigoYAbreviaturaRestaurante();
    	return codigoRest + "@" + damePedidoActual();
	}
	
	/**
	 * Prepara el pedido en un string para que sea facil su tratamiento a la
	 * hora de escribir en la tag. Obtiene de la base de datos el pedido a
	 * sincronizar con la siguiente forma:
	 * "id_plato@id_plato+extras@5*ingredientes@id_plato+extras*ingredientes@";
	 * "1@2@3@4+10010@5*01011@1+01001*1111@2+10010*1000100@";
	 * 
	 * @return
	 */
	private String damePedidoActual() {
		String listaPlatosStr = "";
		String[] campos = new String[] { "Id", "ExtrasBinarios",
				"IngredientesBinarios", "Restaurante" };// Campos que quieres recuperar
		String[] datosRestaurante = new String[] { restaurante };
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?",
				datosRestaurante, null, null, null);

		while (cursorPedido.moveToNext()) {

			// le quito fh o v para introducir solo el id numerico en la tag
			String idplato = cursorPedido.getString(0).substring(abreviaturaRest.length());

			// compruebo si hay extras y envio +Extras si hay y si no ""
			String extrasBinarios = cursorPedido.getString(1);
			if (extrasBinarios == null)
				extrasBinarios = "";
			else
				extrasBinarios = "+" + extrasBinarios;

			// compruebo si hay ingredientes y envio *Ingre si no ""
			String ingredientesBinarios = cursorPedido.getString(2);
			if (ingredientesBinarios == null)
				ingredientesBinarios = "";
			else
				ingredientesBinarios = "*" + ingredientesBinarios;

			listaPlatosStr += idplato + extrasBinarios + ingredientesBinarios + "@";
		}
		
		return listaPlatosStr;
	}
	
	private void obtenerCodigoYAbreviaturaRestaurante() {
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);
		
		cursorPedido.moveToFirst();
		codigoRest = cursorPedido.getString(0);
		abreviaturaRest = cursorPedido.getString(1);
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
          NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/recogerbeam", pedido.getBytes())
       
        );
        return msg;
    }
    /**
     * Metodo encargado de cerrar la ventana
     */
    public void cerrarVentana(){
    	this.finish();
    }
    
    
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
            	Toast.makeText(getApplicationContext(), "Pedido Sincronizado", Toast.LENGTH_LONG).show();
            	enviarPedidoACuenta();
            	cerrarBasesDeDatos();
            	Intent intent = new Intent();
    	        intent.putExtra("Origen", "Pedido");
            	setResult(RESULT_OK, intent);
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
 