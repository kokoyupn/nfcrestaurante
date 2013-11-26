package usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Toast;

public class RecogerCuentaNFC extends Activity implements DialogInterface.OnDismissListener {

	private ProgressDialog progressDialogSinc;
	private HandlerDB sqlCuenta, sqlMiBase, sqlEquivalencia;
	private SQLiteDatabase dbCuenta, dbMiBase, dbEquivalencia;
	
	// NFC
	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter writeTagFilters[];
	private Tag mytag;
	private boolean restauranteCorrecto, leidoBienDeTag, esCuenta, tagCorrupta;
	private String restaurante, abreviaturaRest, codigoRest;
	private ArrayList<Byte> cuentaLeidaEnBytes;
	
	// Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	public static ArrayList<ContentValues> mensajes; 
	public static String mensaje="";
	public static boolean cuentaRecibidaConExito;
	
	public static boolean enviarPorEmail;

	/**
	 * Clase interna necesaria para ejecutar en segundo plano tareas
	 * (codificacion de pedido, escritura NFC y transferencia de pedido a
	 * cuenta) mientras se muestra un progress dialog. Cuando finalicen las
	 * tareas, éste se cerrará y esto provocará la ejecución del método
	 * onDismiss que preparará los datos a enviar al Fragment que lanzó la
	 * actividad SincronizarPedidoNFC y cerrará a ésta.
	 */
	public class SincronizarPedidoBackgroundAsyncTask extends
			AsyncTask<Void, Void, Void> {

		/**
		 * Se ejecuta antes de doInBackground. Abre las bases de datos y muestra
		 * el progresDialog ya creado.
		 */
		@Override
		protected void onPreExecute() {
			abrirBasesDeDatos();			
			//obtenemos el codigo y la abreviatura del rest
			obtenerCodigoYAbreviaturaRestaurante();
			progressDialogSinc.show();
			
		}

		/**
		 * Ejecuta en segundo plano. Si la tag es Mifare Cassic codifica el
		 * pedido, envia a NFC y transfiere los platos de pedido a cuenta.
		 */
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// comprueba errores y prepara el arrayList leido para decodificarlo en el metodo onDismiss
				comprobarErrores(read(mytag));
			} catch (IOException e1) {
				leidoBienDeTag = false;
				e1.printStackTrace();
			} catch (FormatException e1) {
				leidoBienDeTag = false;
				e1.printStackTrace();
			}

			// mejor aqui para buscar siempre que haga lo anterior que es lo importante
			sonidoManager.play(sonido);
			SystemClock.sleep(1000);
			return null;
		}


		/**
		 * Se ejecuta cuando termina doInBackground. Cierra las bases de datos y
		 * tambien el progressDialog.
		 */
		@Override
		protected void onPostExecute(Void result) {
			progressDialogSinc.dismiss();
		}

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		enviarPorEmail = false;
		
		cuentaRecibidaConExito = false;
		
		setContentView(R.layout.sincronizar_pedido_nfc);
		
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" RECOGER CUENTA");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);

		// El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		restaurante = bundle.getString("Restaurante");

		// preparamos para NFC
		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };

		// creamos el progresDialog que se mostrara
		crearProgressDialogSinc();

		// configuracion del sonido
		configurarSonido();
		
		//inicializamos variables para mostrar errores
		leidoBienDeTag = tagCorrupta = false;
		restauranteCorrecto = esCuenta = true;
		
	}
	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }

	/**
	 * Cierra la actividad y muestra un mensaje. Se ejecuta cuando se cierra el
	 * progressDialog. Con el primer parametro de setResult te enviamos al
	 * Fragment del que fue lanzado informacion para saber como actuar
	 * dependiendo de si ha escrito bien o ha habido algun problema. El segundo
	 * parametro null es un Intent y lo podemos usar si queremos enviarle otra
	 * cosa como por ejemplo un string o un dato que necesite.
	 */
	public void onDismiss(DialogInterface dialog) {

		if (leidoBienDeTag){
			if (!tagCorrupta){
				if (restauranteCorrecto){
					if (esCuenta) {
						decodificarPlatos(dameCuentaString(cuentaLeidaEnBytes));
						cuentaRecibidaConExito = true;
						Toast.makeText(this,"Cuenta recogida correctamente",Toast.LENGTH_LONG).show();
					} else Toast.makeText(this,"Error al recoger la cuenta. No hay ninguna cuenta en la tarjeta",Toast.LENGTH_LONG).show();			
				} else Toast.makeText(this,"Error al recoger la cuenta. No estas en el restaurante correcto",Toast.LENGTH_LONG).show();
			} else Toast.makeText(this,"La tarjeta está averiada. Llama al camarero",Toast.LENGTH_LONG).show();
		} else Toast.makeText(this,"Error al recoger la cuenta. Has levantado el dispositivo antes de tiempo",Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent();
        intent.putExtra("Origen", "Cuenta");
        intent.putExtra("Tipo", "NFC");
        setResult(RESULT_OK, intent);
        
		cerrarBasesDeDatos();

		finish();
	}

	/**
	 * Crea un progressDialog con el formato que se quiera.
	 */
	private void crearProgressDialogSinc() {
		progressDialogSinc = new ProgressDialog(this);
		progressDialogSinc.setIndeterminate(true);
		progressDialogSinc.setMessage("Espere unos segundos...");
		progressDialogSinc.setTitle("Sincronizando pedido");
		progressDialogSinc.setCancelable(false);
		// listener para que ejecute el codigo de onDismiss cuando el dialog se cierre
		progressDialogSinc.setOnDismissListener(this);
	}

	private void configurarSonido() {
		// Creamos la instacia del manager de sonido
		sonidoManager = new SonidoManager(getApplicationContext());
		// Pone el volumen al volumen del movil actual
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Cargamos el sonido
		sonido = sonidoManager.load(R.raw.confirm);
	}

	/************************************ BASES DE DATOS ****************************************/

	/**
	 * Abre las bases de datos Cuenta, mibase y restaurante_equiv.
	 */
	private void abrirBasesDeDatos() {
		sqlCuenta = null;
		sqlMiBase = null;
		sqlEquivalencia = null;
		dbCuenta = null;
		dbMiBase = null;
		dbEquivalencia = null;

		try {
			sqlMiBase = new HandlerDB(getApplicationContext(), "MiBase.db");
			dbMiBase = sqlMiBase.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS MiBase: RecogerCuentaNFC (cargarBaseDeDatosPedido)");
		}
		try {
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS CUENTA: SINCRONIZAR NFC (cargarBaseDeDatosCuenta)");
		}
		try {
			sqlEquivalencia = new HandlerDB(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbEquivalencia = sqlEquivalencia.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS Restaurante: SINCRONIZAR NFC (cargarBaseDeDatosResta)");
		}
	}

	/**
	 * Cierra las bases de datos Cuenta y Restaurante
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlEquivalencia.close();
	}

	/********************************* DECODIFICACION ************************************/

	
	/**
	 * Comprueba si estoy en restaurante correcto, si es una cuenta y procesa la cuenta dejandola lista
	 * para decodificar y añadirla a la base de datos quitandole el codRest, el byte cuenta y los ceros del final
	 * @param cuentaLeida
	 */
	private void comprobarErrores(ArrayList<Byte> cuentaLeida) {

		cuentaLeidaEnBytes = new ArrayList<Byte>();
		
		if (!tagCorrupta && leidoBienDeTag){
		
			if (estoyEnRestauranteCorrecto(cuentaLeida.remove(0))){
				if (hayUnaCuenta(cuentaLeida.remove(0))){
					Iterator<Byte> itCuenta = cuentaLeida.iterator();
					
					boolean parar = false;
					while(itCuenta.hasNext() && !parar){					
						
						Byte idByte = itCuenta.next();
						parar = decodificaByte(idByte)==255; //El mensaje termina con un -1 en la tag
	
						if(!parar)
							cuentaLeidaEnBytes.add(idByte); // metemos el idByte en la cuenta
					 }	
					
					if(!parar) 
						tagCorrupta = true;
				}
			}	
		}
	}
	
	/**
	 * Metodo que decodifica el string que ha leido del QR.
	 * Va comprobando el id, si tiene extras y comentarios hasta que se encuentre un 255 como id
	 * que significa que ha terminado
	 * @param cuenta
	 */
	private void decodificarPlatos(String cuenta) {
		mensajes = new ArrayList<ContentValues>();
		
		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(cuenta,"@");
			
		borrarCuentaActual();
			
		while(stPlatos.hasMoreElements()){
			String id = stPlatos.nextToken();
			anadirPlatoACuenta(abreviaturaRest+id);
		}	
	}
	
	/**
	 * Añade en Cuenta.db los platos que ha leido por NFC
	 */
	private void anadirPlatoACuenta(String id){
		
		//Campos que quieres recuperar
		String[] campos = new String[]{"Id","Nombre","Precio"};
		String[] datosRestaurante = new String[]{restaurante,id};	
		Cursor cursor = dbMiBase.query("Restaurantes", campos, "Restaurante=? AND Id=?", datosRestaurante,null, null,null);
    	
    	if (cursor.moveToNext()){
    		ContentValues platoCuenta = new ContentValues();
    		platoCuenta.put("Id", cursor.getString(0));
        	platoCuenta.put("Plato", cursor.getString(1));
        	platoCuenta.put("Ingredientes", "");
        	platoCuenta.put("Extras", "");
        	platoCuenta.put("PrecioPlato",cursor.getDouble(2));
        	platoCuenta.put("Restaurante",restaurante);
        	platoCuenta.put("IdHijo", 0);
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    		ContentValues mensajeCuenta = new ContentValues();
    		mensajeCuenta.put("Plato", cursor.getString(1));
    		mensajeCuenta.put("PrecioPlato",cursor.getDouble(2));
    		mensajes.add(mensajeCuenta);
    	}	
	}	
	
    private void borrarCuentaActual() {
    	try{
			dbCuenta.delete("Cuenta", null, null);
		} catch(SQLiteException e){
         	Toast.makeText(this,"NO EXISTE",Toast.LENGTH_SHORT).show();
        }
	}
	
    /**
     * Devuelve en un string la cuenta con los id's:  id1@id2@id3.....
     * @param cuentaBytes
     * @return
     */
	private String dameCuentaString(ArrayList<Byte> cuentaBytes) {	
		//Recorremos todo el mensaje leido y vamos descomponiendo todos los platos en id-extras-comentario
		Iterator<Byte> itPlatos = cuentaBytes.iterator();
		
		String cuentaStr = "";
		
		while(itPlatos.hasNext())
			cuentaStr += decodificaByte(itPlatos.next()) + "@";
		
		return cuentaStr;
	}
		
	/**Metodo que se encargar de convertir un byte dado por parametro a un tipo int
	 *  
	* @param idByte
	* @return
	*/
	public int decodificaByte(byte idByte){
		int id = (int)idByte;
		if (id < 0) return id + 256;
		else return id;
	}
		

	@Override
	protected void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			//Toast.makeText(this, this.getString(R.string.tag_detectada), Toast.LENGTH_SHORT).show();
		}
		if (mytag != null) {
			// ejecuta el progressDialog, codifica, escribe en tag e intercambia
			// datos de pedido a cuenta en segundo plano
			new SincronizarPedidoBackgroundAsyncTask().execute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		adapter.disableForegroundDispatch(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters,null);
	}

	/********************************** LECTURA NFC **********************************************/

	/**
	 * Metodo que se encarga de leer de la tarjeta nfc
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private ArrayList<Byte> read(Tag tag) throws IOException, FormatException {	
		
		 /*
        * See NFC forum specification for "Text Record Type Definition" at 3.2.1
        *
        * http://www.nfc-forum.org/specs/
        *
        * bit_7 defines encoding
        * bit_6 reserved for future use, must be 0
        * bit_5..0 length of IANA language code
        */
		Ndef ndef = Ndef.get(tag);
		ArrayList<Byte> cuentaLeida = new ArrayList<Byte>();
		tagCorrupta = false;
		leidoBienDeTag = true;
		
		if (ndef != null){
			NdefMessage message = ndef.getCachedNdefMessage();
			if(message != null){
				byte[] mensajeEnBytes = message.toByteArray();
				// Con este "for" eliminamos los datos inservibles del array de bytes
				for (int i=0; i<mensajeEnBytes.length-10; i++){
					cuentaLeida.add(mensajeEnBytes[i+10]);
				}
			} else leidoBienDeTag = false;
		} else tagCorrupta = true;
		
		return cuentaLeida;
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
	 * Devuelve si estoy en el restaurante correcto y da valor a la variable para mostrar errores
	 * @param codigoRestTag
	 * @return
	 */
	private boolean estoyEnRestauranteCorrecto(byte codigoRestTag){
		return restauranteCorrecto = ((byte) Integer.parseInt(codigoRest) == codigoRestTag);
	}
	
	/**
	 * Devuelve si hay una cuenta y da valor a la variable para mostrar errores
	 * @param cuentaTag
	 * @return
	 */
	private boolean hayUnaCuenta (byte cuentaTag){
		return esCuenta = ((byte) 254 == cuentaTag);
	}

}