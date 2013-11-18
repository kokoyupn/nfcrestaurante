package recogerbeam;

import java.util.StringTokenizer;

import com.example.nfcook_camarero.Mesa;
import com.example.nfcook_camarero.R;
import com.example.nfcook_camarero.SonidoManager;
import baseDatos.HandlerGenerico;
import fragments.PantallaMesasFragment;
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


public class SincronizacionBeamNFC extends Activity  implements OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    Context context;
	 
	//Variables usadas en la manipulacion e bases de datos	
	HandlerGenerico sqlMesas, sqlRestaurante;
	SQLiteDatabase dbMesas, dbRestaurante;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	String restaurante, codigoRest, abreviaturaRest;
	
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
    	actionbar.setTitle("SINCRONIZAR PEDIDO");
    	
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
        //--------Metodos para añadir a la base de datos mesas
        decodificarPlatos(new String(msg.getRecords()[0].getPayload()));
        //Sonido para confirmar el pedido sincronizado
        sonidoManager.play(sonido);
        Toast.makeText(getApplicationContext(),"Pedido sincronizado correctamente.", Toast.LENGTH_LONG).show();
        cerrarVentana();     
        
    }
    /**
	 * Metodo que decodifica el string mandado por Beam.
	 * Va comprobando el id, si tiene extras y comentarios hasta que se encuentre un 255 como id
	 * que significa que ha terminado
	 * @param pedidoQR
	 */
	private void decodificarPlatos(String pedidoBeam) {

		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(pedidoBeam,"@");
		
		String codigoRestBeam = stPlatos.nextToken();
		
		// mismo restaurante
		if (codigoRest.equals(codigoRestBeam)){
		
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
				String ingredientes = "";
				if (plato.contains("*"))
					ingredientes =  stTodoSeparado.nextToken();
						
				anadirPlatos(abreviaturaRest+id, extras, ingredientes);
			}	
		}
		else {
			Toast.makeText(getApplicationContext(), "Los platos sincronizados no corresponden a este restaurante.", Toast.LENGTH_LONG).show();
		}
	}
		
	@SuppressLint("SdCardPath")
	public void anadirPlatos(String idQR, String extrasQR, String ingredientesQR){           
       	
		try{
			sqlRestaurante = new HandlerGenerico(getApplicationContext(), "MiBase.db");
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
	    		// FIXME no se meten los datos que vienen de la pantalla anterior porque se pierden con el beam al abrirse otra pantalla
	       		ContentValues plato = new ContentValues();
	        	int idUnico = PantallaMesasFragment.getIdUnico();
	        	/*plato.put("NumMesa", numMesa);
	        	plato.put("IdCamarero", idCamarero);*/
	        	plato.put("IdPlato", idQR);
	        	if (ingredientesFinales.equals("")) plato.put("Ingredientes", "Con todos los ingredientes");
		        else plato.put("Ingredientes", ingredientesFinales);
	        	if (extrasQR.equals(""))	plato.put("Extras","Sin guarnición");
		        else plato.put("Extras", extrasFinales);
	        	//plato.put("FechaHora", formatteDate + " " + formatteHour);
	        	plato.put("Nombre", cursor.getString(0));
	        	plato.put("Precio",cursor.getDouble(1));
	        	//plato.put("Personas",numPersonas);
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