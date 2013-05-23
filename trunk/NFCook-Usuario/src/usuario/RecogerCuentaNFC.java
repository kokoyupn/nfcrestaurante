package usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import baseDatos.HandlerDB;
import com.example.nfcook.R;

import fragments.PantallaInicialRestaurante;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
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
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Toast;

public class RecogerCuentaNFC extends Activity implements
		DialogInterface.OnDismissListener {

	private ProgressDialog progressDialogSinc;
	private String restaurante;
	private HandlerDB sqlCuenta, sqlMiBase, sqlRestaurante;
	private SQLiteDatabase dbCuenta, dbMiBase, dbRestaurante;
	// NFC
	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter writeTagFilters[];
	private Tag mytag;
	private boolean dispositivoCompatible, restauranteCorrecto, leidoBienDeTag, estaLaCuenta, tagCorrupta;
	private byte idRestauranteTag;
	private ArrayList<Byte> cuentaBytes;
	// Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	private static String abreviaturaRest; 

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
			progressDialogSinc.show(); // Mostramos el diálogo antes de comenzar
		}

		/**
		 * Ejecuta en segundo plano. Si la tag es Mifare Cassic codifica el
		 * pedido, envia a NFC y transfiere los platos de pedido a cuenta.
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// si es Mifare Classic
			if (dispositivoCompatible) {
				try {
					leerTagNFC();
				} catch (IOException e1) {
					leidoBienDeTag = false;
					e1.printStackTrace();
				} catch (FormatException e1) {
					leidoBienDeTag = false;
					e1.printStackTrace();
				}
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
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };

		// creamos el progresDialog que se mostrara
		crearProgressDialogSinc();

		// configuracion del sonido
		configurarSonido();
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

		if (dispositivoCompatible){
			if (leidoBienDeTag){
				if (!tagCorrupta){
					if (estoyEnRestauranteCorrecto()){
						if (estaLaCuenta) {
							decodificarPlatos(dameCuentaString(cuentaBytes));
							Toast.makeText(this,"Cuenta recogida correctamente",Toast.LENGTH_LONG).show();
						} else Toast.makeText(this,"Error al recoger la cuenta. No hay ninguna cuenta en la tarjeta",Toast.LENGTH_LONG).show();			
					} else Toast.makeText(this,"Error al recoger la cuenta. No estas en el restaurante correcto.",Toast.LENGTH_LONG).show();
				} else Toast.makeText(this,"Error al recoger la cuenta",Toast.LENGTH_LONG).show();
			} else Toast.makeText(this,"Error al recoger la cuenta. Has levantado el dispositivo antes de tiempo",Toast.LENGTH_LONG).show();
		} else Toast.makeText(this,"Error al recoger la cuenta. Tu dispositivo no es compatible con esta tarjeta",Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent();
        intent.putExtra("Origen", "Cuenta");
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
		sqlRestaurante = null;
		dbCuenta = null;
		dbMiBase = null;
		dbRestaurante = null;

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
			sqlRestaurante = new HandlerDB(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbRestaurante = sqlRestaurante.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS Restaurante: SINCRONIZAR NFC (cargarBaseDeDatosResta)");
		}
	}
	
	private boolean estoyEnRestauranteCorrecto(){
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);

		cursorPedido.moveToFirst();
		byte idRest = (byte) Integer.parseInt(cursorPedido.getString(0));
		abreviaturaRest = cursorPedido.getString(1);
		restauranteCorrecto = (idRest == idRestauranteTag);
		
		return restauranteCorrecto;
	}

	/**
	 * Cierra las bases de datos Cuenta y Restaurante
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlRestaurante.close();
	}

	/********************************* DECODIFICACION ************************************/

	/**
	 * Metodo que decodifica el string que ha leido del QR.
	 * Va comprobando el id, si tiene extras y comentarios hasta que se encuentre un 255 como id
	 * que significa que ha terminado
	 * @param cuenta
	 */
	private void decodificarPlatos(String cuenta) {

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
        	platoCuenta.put("Observaciones", "");
        	platoCuenta.put("Extras", "");
        	platoCuenta.put("PrecioPlato",cursor.getDouble(2));
        	platoCuenta.put("Restaurante",restaurante);
        	platoCuenta.put("IdHijo", 0);
    		dbCuenta.insert("Cuenta", null, platoCuenta);
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
		
		// quitamos el idRestaurante y el -2 de que es cuenta
		itPlatos.next();
		itPlatos.next();
		
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
		
		
	/**
	 * Comprueba si se puede escribir o no en los bloques de las tag
	 * 
	 * @param numBloque
	 * @return
	 */
	private boolean sePuedeEscribirEnBloque(int numBloque) {
		return (numBloque + 1) % 4 != 0 && numBloque != 0;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Toast.makeText(this, this.getString(R.string.tag_detectada),
					Toast.LENGTH_SHORT).show();

			// compruebo que la tarjeta sea mifare classic
			String[] tecnologiasTag = mytag.getTechList();
			dispositivoCompatible = false;
			for (int i = 0; i < tecnologiasTag.length; i++)
				dispositivoCompatible |= tecnologiasTag[i].equals("android.nfc.tech.MifareClassic");

		}
		if (mytag == null) {
			Toast.makeText(this, this.getString(R.string.tag_no_detectada),
					Toast.LENGTH_SHORT).show();
		} else {
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
	 * Metodo que se encarga de leer bloques de la tarjeta nfc
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void leerTagNFC() throws IOException, FormatException {

		// Obtiene la instacia de la tarjeta nfc
		MifareClassic mfc = MifareClassic.get(mytag);
		// Establece la conexion
		mfc.connect();

		// va lo que lee
		cuentaBytes = new ArrayList<Byte>();
		// Variable usada para saber por el bloque que vamos
		int numBloque = 0;
		// el texto que ha escrito el usuario
		byte[] textoByte = null;
		// para errores
		boolean sectorValido = false;
		leidoBienDeTag = true;
		Byte menosUno = (byte) Integer.parseInt("255");
		Byte menosDos = (byte) Integer.parseInt("254");
		boolean menosUnoEncontrado = false;
		boolean esPrimerBloque = false;

		// Recorremos todos los sectores y bloques leyendo el mensaje
		while (numBloque < mfc.getBlockCount() && !menosUnoEncontrado) {
			if (sePuedeEscribirEnBloque(numBloque)) {
				// Cada sector tiene 4 bloques
				int numSector = numBloque / 4;
				// Validamos el sector con la A porque las tarjetas que tenemos usan el bit A en vez del B
				sectorValido = mfc.authenticateSectorWithKeyA(numSector,MifareClassic.KEY_DEFAULT);

				if (sectorValido) {// Si es un sector valido
					textoByte = mfc.readBlock(numBloque); // leemos un bloque entero

					if (!esPrimerBloque){
						estaLaCuenta = textoByte[0] == menosDos;
						idRestauranteTag = textoByte[1];
						esPrimerBloque = true;
					}
					
					int i = 0;
					while (i < MifareClassic.BLOCK_SIZE && !menosUnoEncontrado) {
						if (textoByte[i] != menosUno) cuentaBytes.add(textoByte[i]);
						else menosUnoEncontrado = true;
						i++;
					}
				}
			}
			numBloque++;
		}
		leidoBienDeTag = true;
		tagCorrupta = !menosUnoEncontrado;

		// Cerramos la conexion
		mfc.close();
	}

}
