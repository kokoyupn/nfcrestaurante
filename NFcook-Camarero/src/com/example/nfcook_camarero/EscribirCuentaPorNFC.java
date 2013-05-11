package com.example.nfcook_camarero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.widget.Toast;

public class EscribirCuentaPorNFC extends Activity implements
		DialogInterface.OnDismissListener {

	private ProgressDialog progressDialogSinc;
	private String restaurante, numMesa;
	private HandlerGenerico sqlMesas, sqlRestaurante;
	private SQLiteDatabase dbMesas, dbRestaurante;
	// NFC
	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter writeTagFilters[];
	private Tag mytag;
	private boolean dispositivoCompatible, cabeEnTag, escritoBienEnTag, heCalculadoTam;
	private byte idRestaurante;
	private ArrayList<Byte> cuentaCodificadaEnBytes;
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
		 * Se ejecuta antes de doInBackground. Muestra el progresDialog ya creado.
		 */
		@Override
		protected void onPreExecute() {
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
					escribirEnTagNFC();
				} catch (IOException e) {
					escritoBienEnTag = false;
					e.printStackTrace();
				} catch (FormatException e) {
					escritoBienEnTag = false;
					e.printStackTrace();
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

		setContentView(R.layout.escribir_cuenta);
		
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" ESCRIBIR CUENTA");

		// El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		restaurante = bundle.getString("Restaurante");
		numMesa = bundle.getString("NumMesa");

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
		
		// esto antes para que tarde menos la escritura por nfc y evitar problemas
		abrirBasesDeDatos();
		obtenerIdRestYAbreviatura();
		codificarCuenta(dameCuentaString());
		
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
			if (escritoBienEnTag) Toast.makeText(this,"Cuenta sincronizada correctamente",Toast.LENGTH_LONG).show();
			else {
				if (heCalculadoTam) {
					if (cabeEnTag) Toast.makeText(this, "Cuenta no sincronizada. Has levantado el dispositivo antes de tiempo.",Toast.LENGTH_LONG).show();
					else Toast.makeText(this,"Cuenta no sincronizada. No cabe en la tarjeta",Toast.LENGTH_LONG).show();
				} else Toast.makeText(this, "Cuenta no sincronizada. Has levantado el dispositivo antes de tiempo.",Toast.LENGTH_LONG).show();
			}
		} else Toast.makeText(this,"Cuenta no sincronizada. Tu dispositivo no es compatible con esta tarjeta.",Toast.LENGTH_LONG).show();
				
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
	 * Abre las bases de datos mesas y restaurante.
	 */
	private void abrirBasesDeDatos() {
		sqlMesas = null;
		sqlRestaurante = null;
		dbMesas = null;
		dbRestaurante = null;

		try {
			sqlMesas = new HandlerGenerico(getApplicationContext(), "Mesas.db");
			dbMesas = sqlMesas.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS MESAS: SINCRONIZAR NFC (abrirBasesDeDatos)");
		}
		try {
			sqlRestaurante = new HandlerGenerico(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbRestaurante = sqlRestaurante.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS RESTAURANTE EQ: SINCRONIZAR NFC (abrirBasesDeDatos)");
		}
	}
	
	private void obtenerIdRestYAbreviatura(){
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursor = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);

		cursor.moveToFirst();
		idRestaurante = (byte) Integer.parseInt(cursor.getString(0));
		abreviaturaRest = cursor.getString(1);
	}

	/**
	 * Cierra las bases de datos mesas y restaurante
	 */
	private void cerrarBasesDeDatos() {
		sqlMesas.close();
		sqlRestaurante.close();
	}

	/********************************* CODIFICACION ************************************/

	/**
	 * Prepara en un string la cuenta de la sgte forma:
	 * "id1@id2@id3.....
	 * 
	 * @return
	 */
	private String dameCuentaString() {
		
		String cuenta = "";
		String[] campos = new String[] { "IdPlato"};// Campos que quieres recuperar
		String[] datosRestaurante = new String[] { numMesa };
		Cursor cursor = dbMesas.query("Mesas", campos, "NumMesa=?",datosRestaurante, null, null, null);

		while (cursor.moveToNext()) {
			// le quito fh o v para introducir solo el id numerico en la tag
			String idplato = cursor.getString(0).substring(abreviaturaRest.length());
			cuenta += idplato + "@";
		}

		return cuenta;
	}

	/**
	 * Codifica la cuenta y pondra al inicio un -2 indicando que es una cuenta, el idRest, 
	 * los id platos y luego un -1 indicando el final
	 * @param cuenta
	 * @return
	 */
	private void codificarCuenta(String cuenta) {

		// variable donde ira el pedido con la codificacion final
		cuentaCodificadaEnBytes = new ArrayList<Byte>();
		// para indicar que ha finalizado el pedido escribo un 254 (-2)
		cuentaCodificadaEnBytes.add((byte) Integer.parseInt("254"));	
		// se mete el idRest
		cuentaCodificadaEnBytes.add(idRestaurante);
		// separamos por platos
		StringTokenizer stCuenta = new StringTokenizer(cuenta, "@");
		
		while (stCuenta.hasMoreElements()) {
			String id = stCuenta.nextToken();
			cuentaCodificadaEnBytes.addAll(codificaIdPlato(id));
		}
		
		// para indicar que ha finalizado el pedido escribo un 255 (-1)
		cuentaCodificadaEnBytes.add((byte) Integer.parseInt("255"));
	}


	/**
	 * Codifica el parametro de entrada id y lo devuelve en formato de un
	 * arrayList<Byte>. Será siempre un byte lo que ocupe pues lo que vamos a
	 * introducir sera su valor en binario por lo cual en 1 byte podremos meter
	 * 255 id's diferentes
	 * 
	 * @param id
	 * @return
	 */
	private ArrayList<Byte> codificaIdPlato(String id) {
		ArrayList<Byte> al = new ArrayList<Byte>();
		al.add((byte) Integer.parseInt(id));
		return al;
	}

	/************************************ ESCRITURA NFC ****************************************/

	/**
	 * Metodo encargado de escribir en el tag. Escribira en el tag el texto
	 * introducido por el usuario. Los bloques que queden sin escribir seran
	 * reescritos con 0's eliminando el texto que hubiese anteriormente
	 * 
	 * @param text
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void escribirEnTagNFC() throws IOException, FormatException {

		// Obtenemos instancia de MifareClassic para el tag.
		MifareClassic mfc = MifareClassic.get(mytag);

		// Habilitamos operaciones de I/O
		mfc.connect();

		escritoBienEnTag = false;

		boolean sectorValido = false;
		// para avanzar los bloques (en el 0 no se puede escribir)
		int numBloque = 0;		
		// para recorrer string de MifareClassic.BLOCK_SIZE en MifareClassic.BLOCK_SIZE
		int recorrerString = 0;
		// relleno con 0's el pedido hasta que sea modulo16 para que luego no haya problemas ya que
		// se escribe mandando bloques de 16 bytes
		int numMod16 = cuentaCodificadaEnBytes.size() % 16;
		if (numMod16 != 0) {
			int huecos = 16 - numMod16;
			for (int i = 0; i < huecos; i++)
				cuentaCodificadaEnBytes.add((byte) 0);
		}

		if (cabeCuentaEnTag(cuentaCodificadaEnBytes, mfc)) {
			// recorro todos los bloques escribiendo el pedido. Cuando acabe escribo 0's en los que sobren
			while (numBloque < mfc.getBlockCount() && !escritoBienEnTag) {
				// comprobamos si el bloque puede ser escrito o es un bloque prohibido
				if (sePuedeEscribirEnBloque(numBloque)) {
					// cada sector tiene 4 bloques
					int numSector = numBloque / 4;
					// autentifico con la key A para escritura
					sectorValido = mfc.authenticateSectorWithKeyA(numSector,MifareClassic.KEY_DEFAULT);
					if (sectorValido) {
						// si es menor significa que queda por escribir cosas
						if (recorrerString < cuentaCodificadaEnBytes.size()) {
							// recorremos con un for para obtener bloques de 16 bytes
							byte[] datosAlBloque = new byte[MifareClassic.BLOCK_SIZE];
							for (int i = 0; i < MifareClassic.BLOCK_SIZE; i++)
								datosAlBloque[i] = cuentaCodificadaEnBytes.get(i + recorrerString);
							// avanzo para el siguiente bloque
							recorrerString += MifareClassic.BLOCK_SIZE;
							// escribimos en el bloque
							mfc.writeBlock(numBloque, datosAlBloque);
						} else {
							byte[] ceros = new byte[MifareClassic.BLOCK_SIZE];
							mfc.writeBlock(numBloque, ceros);
						}
					}
				}
				numBloque++;
			}
		}
		escritoBienEnTag = true;
		
		// Cerramos la conexion
		mfc.close();
	}

	/**
	 * Devuelve un booleano informando de si el pedido cabe o no cabe en la
	 * tarjeta
	 * 
	 * @param pedidoCodificadoEnBytes
	 * @param mfc
	 * @return
	 */
	private boolean cabeCuentaEnTag(ArrayList<Byte> cuentaCodificadoEnBytes, MifareClassic mfc) {
		int bytesTag = mfc.getBlockCount() * 16;
		int bytesProhibidosTag = (mfc.getSectorCount() + 1) * 16;
		int bytesLibresTag = bytesTag - bytesProhibidosTag;
		heCalculadoTam = true;
		cabeEnTag = cuentaCodificadoEnBytes.size() < bytesLibresTag;
		return cabeEnTag;
		
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
			Toast.makeText(this, this.getString(R.string.ok_detection),Toast.LENGTH_SHORT).show();

			// compruebo que la tarjeta sea mifare classic
			String[] tecnologiasTag = mytag.getTechList();
			dispositivoCompatible = false;
			for (int i = 0; i < tecnologiasTag.length; i++)
				dispositivoCompatible |= tecnologiasTag[i].equals("android.nfc.tech.MifareClassic");

		}
		if (mytag == null) {
			Toast.makeText(this, this.getString(R.string.error_detected),Toast.LENGTH_SHORT).show();
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

}
