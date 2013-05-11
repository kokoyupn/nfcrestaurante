package usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import baseDatos.HandlerDB;
import com.example.nfcook.R;
import fragments.ContenidoTabSuperiorCategoriaBebidas;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SincronizarPedidoNFC extends Activity implements
		DialogInterface.OnDismissListener {

	private ProgressDialog progressDialogSinc;
	private String restaurante;
	private HandlerDB sqlCuenta, sqlPedido, sqlRestaurante;
	private SQLiteDatabase dbCuenta, dbPedido, dbRestaurante;
	// NFC
	private NfcAdapter adapter;
	private PendingIntent pendingIntent;
	private IntentFilter writeTagFilters[];
	private Tag mytag;
	private ArrayList<Byte> pedidoCodificadoEnBytes;
	private boolean dispositivoCompatible, cabeEnTag, leidoBienDeTag, escritoBienEnTag, tagCorrupta, 
		heCalculadoTam, restauranteCorrecto, esCuenta;
	private static boolean heSincronizadoMalAntes;
	private static ArrayList<Byte> copiaSeguridad, copiaUltimoBloque;
	private static int numBloqueComienzo;
	private byte idRestaurante, idRestauranteTag;
	// Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	private static String abreviaturaRest;
	
	private ArrayList<Boolean> bloquesDepuracion; 

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
		 * Ejecuta en segundo plano. Si la tag es Mifare Cassic envia a NFC y transfiere 
		 * los platos de pedido a cuenta.
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// si es Mifare Classic
			if (dispositivoCompatible) {
				try {
					// si he sincronizado mal no tengo que leer porque ya tengo la copia y nadie mas ha escrito
					// se reinician las copias de seguridad porque no se ha sincronizado mal
					if (!heSincronizadoMalAntes)
						leerTagNFC();
					else
						leidoBienDeTag = true;
					
				} catch (IOException e1) {
					leidoBienDeTag = false;
					e1.printStackTrace();
				} catch (FormatException e1) {
					leidoBienDeTag = false;
					e1.printStackTrace();
				}
				
				if (leidoBienDeTag && !esCuenta) {
					
					if (estoyEnRestauranteCorrecto()){
						// si ha sincronizado mal entra xq para el la tag no esta corrupta
						if (!tagCorrupta) {
							codificarPedido(damePedidoActual());
							try {
								escribirEnTagNFC();
							} catch (IOException e) {
								heSincronizadoMalAntes = true;
								e.printStackTrace();
							} catch (FormatException e) {
								heSincronizadoMalAntes = true;
								e.printStackTrace();
							}
						}
					}
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
    	actionbar.setTitle(" SINCRONIZAR PEDIDO");

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

		// mortrar aviso inicial
		mostrarAvisoInicial();
		
		// para reducir mas el tiempo y que no hay problemas en la escritura
		abrirBasesDeDatos();
		obtenerIdRestYAbreviatura();		
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

		setResult(RESULT_CANCELED, null);
		if (dispositivoCompatible) {
			if (!tagCorrupta) {
				if (leidoBienDeTag) {
					if (!esCuenta){
						if (escritoBienEnTag){
							enviarPedidoACuenta();
							setResult(RESULT_OK, null);
							Toast.makeText(this,"Pedido sincronizado correctamente. Puedes verlo en cuenta",Toast.LENGTH_LONG).show();
						} else {
							if (heCalculadoTam) {
								if (!cabeEnTag) Toast.makeText(this,"Pedido no sincronizado. No cabe en la tarjeta. Llama a camarero o usa otro metodo de transmision",Toast.LENGTH_LONG).show();
								else if (heSincronizadoMalAntes) Toast.makeText(this,"Has levantado el dispositvo antes de tiempo. Tienes que volver a sincronizar",Toast.LENGTH_LONG).show();
							} else {
								if (restauranteCorrecto) Toast.makeText(this,this.getString(R.string.error_escritura),Toast.LENGTH_LONG).show();
								else Toast.makeText(this,"Pedido no sincronizado. No estas en el restaurante correcto",Toast.LENGTH_LONG).show();
							}
						}
					} else Toast.makeText(this,"Pedido no sincronizado. Avisa al camarero porque hay una cuenta en la tarjeta",Toast.LENGTH_LONG).show();
				} else Toast.makeText(this,this.getString(R.string.error_escritura),Toast.LENGTH_LONG).show();
			} else Toast.makeText(this,"Pedido no sincronizado. Tiene que sincronizar la persona que sincronizo mal",Toast.LENGTH_LONG).show();
		} else Toast.makeText(this,"Pedido no sincronizado. Tu dispositivo no es compatible con esta tarjeta",Toast.LENGTH_LONG).show();
		
		cerrarBasesDeDatos();
		if (!heSincronizadoMalAntes) finish();
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

	private void mostrarAvisoInicial() {
		AlertDialog.Builder avisoInicial = new AlertDialog.Builder(this);
		avisoInicial.setPositiveButton("Aceptar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		View vistaAviso = LayoutInflater.from(this).inflate(R.layout.aviso_continuar_pedido, null);
		TextView textoAMostrar = (TextView) vistaAviso.findViewById(R.id.textViewInformacionAviso);
		textoAMostrar.setText("ATENCIÓN. Una vez puesto el dispositivo sobre la tarjeta espere hasta que termine de sincronizar. "
						+ "\n\nNo lo levante hasta que se cierre el mensaje y suene un pitido.");
		avisoInicial.setView(vistaAviso);
		avisoInicial.show();

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
	 * Borra de la base de datos Pedido.db los platos que haya para
	 * introducirlos en la base de datos Cuenta.db
	 */
	private void enviarPedidoACuenta() {

		// Campos que quieres recuperar
		String[] campos = new String[] { "Id", "Plato", "Observaciones",
				"Extras", "PrecioPlato", "Restaurante", "IdHijo" };
		String[] datosRestaurante = new String[] { restaurante };
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?",
				datosRestaurante, null, null, null);

		while (cursorPedido.moveToNext()) {
			ContentValues platoCuenta = new ContentValues();
			platoCuenta.put("Id", cursorPedido.getString(0));
			platoCuenta.put("Plato", cursorPedido.getString(1));
			platoCuenta.put("Observaciones", cursorPedido.getString(2));
			platoCuenta.put("Extras", cursorPedido.getString(3));
			platoCuenta.put("PrecioPlato", cursorPedido.getDouble(4));
			platoCuenta.put("Restaurante", cursorPedido.getString(5));
			platoCuenta.put("IdHijo", cursorPedido.getString(6));
			dbCuenta.insert("Cuenta", null, platoCuenta);
		}

		try {
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);
		} catch (SQLiteException e) {
			Toast.makeText(getApplicationContext(),"ERROR AL BORRAR BASE DE DATOS PEDIDO", Toast.LENGTH_SHORT).show();
		}

		// Reinciamos la pantalla bebidas, porque ya hemos sincronizado el pedido
		ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas();

	}
	
	private void obtenerIdRestYAbreviatura(){
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);

		cursorPedido.moveToFirst();
		idRestaurante = (byte) Integer.parseInt(cursorPedido.getString(0));
		abreviaturaRest = cursorPedido.getString(1);
	}
	
	private boolean estoyEnRestauranteCorrecto(){
		return restauranteCorrecto = (idRestaurante == idRestauranteTag);
	}

	/**
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlPedido.close();
		sqlRestaurante.close();
	}

	/********************************* CODIFICACION ************************************/

	/**
	 * Prepara el pedido en un string para que sea facil su tratamiento a la
	 * hora de escribir en la tag. Obtiene de la base de datos el pedido a
	 * sincronizar con la siguiente forma:
	 * "id_plato@id_plato+extras@5*Obs@id_plato+extras*Obs@";
	 * "1@2@3@4+10010@5*Con tomate@1+01001*Con azucar@2+10010*Sin macarrones@";
	 * 
	 * @return
	 */
	private String damePedidoActual() {
		String listaPlatosStr = "";
		String[] campos = new String[] { "Id", "ExtrasBinarios",
				"Observaciones", "Restaurante" };// Campos que quieres recuperar
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

			// compruebo si hay observaciones y envio *Observaciones si hay y si
			// no ""
			String observaciones = cursorPedido.getString(2);
			if (observaciones == null)
				observaciones = "";
			else
				observaciones = "*" + observaciones;

			listaPlatosStr += idplato + extrasBinarios + observaciones + "@";
		}

		return listaPlatosStr;
	}

	/**
	 * Codifica el pedido (listaDePlatos) y lo devuelve con un formato de
	 * ArrayList<Byte>. Para ello va separando mediante StringTokenizer los
	 * platos, obteniendo su id, extras y observaciones.
	 * 
	 * @param copia
	 * @param listaPlatosStr
	 * @return
	 */
	private void codificarPedido(String listaPlatosStr) {

		// variable donde ira el pedido con la codificacion final
		pedidoCodificadoEnBytes = new ArrayList<Byte>();

		// el comienzo del array será el ultimo bloque escrito falle o no falle
		for (int i = 0; i < copiaUltimoBloque.size(); i++)
			pedidoCodificadoEnBytes.add(copiaUltimoBloque.get(i));
		
		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(listaPlatosStr, "@");

		while (stPlatos.hasMoreElements()) {

			String plato = stPlatos.nextToken();
			StringTokenizer stTodoSeparado = new StringTokenizer(plato, "+,*");

			// id
			String id = stTodoSeparado.nextToken();
			pedidoCodificadoEnBytes.addAll(codificaIdPlato(id));

			// extras
			if (plato.contains("+")) {
				String extras = stTodoSeparado.nextToken();
				ArrayList<Byte> alExtras = codificaExtras(extras);
				// tamaño de los extras que habra que leer
				pedidoCodificadoEnBytes.add((byte) alExtras.size());
				pedidoCodificadoEnBytes.addAll(alExtras);
			} else
				pedidoCodificadoEnBytes.add((byte) 0);

			// comentarios
			if (plato.contains("*")) {
				String comentario = stTodoSeparado.nextToken();
				ArrayList<Byte> alComentario = codificaComentario(comentario);
				// tamaño de comentarios que habra que leer
				pedidoCodificadoEnBytes.add((byte) alComentario.size());
				pedidoCodificadoEnBytes.addAll(alComentario);
			} else
				pedidoCodificadoEnBytes.add((byte) 0);

		}
		
		// para indicar que ha finalizado el pedido escribo un 255
		pedidoCodificadoEnBytes.add((byte) Integer.parseInt("255"));
	}

	/**
	 * Codifica el parametro de entrada comentrario y lo devuelve en formato de
	 * un arrayList<Byte>
	 * 
	 * @param comentario
	 * @return
	 */
	private ArrayList<Byte> codificaComentario(String comentario) {
		ArrayList<Byte> al = new ArrayList<Byte>();
		for (int i = 0; i < comentario.length(); i++)
			al.add((byte) comentario.charAt(i));
		return al;
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

	/**
	 * Codifica el parametro de entrada extras y lo devuelve en formato de un
	 * arrayList<Byte>. Primero miramos el numero de extras que vienen y hacemos
	 * mod 8 ya que en un byte meteremos 8 extras distintos por lo cual si
	 * tenemos 22 extras usaremos 4 bytes. El ultimo byte quedaria incompleto
	 * porque quedaban 6 extras y nos falan 2 mas. Entonces meteremos 0's para
	 * rellenar el byte incompleto. Una vez completado, vamos generando 1 byte
	 * cada 8 extras.
	 * 
	 * @param extras
	 * @return
	 */
	private ArrayList<Byte> codificaExtras(String extras) {

		ArrayList<Byte> al = new ArrayList<Byte>();
		int relleno = 0;
		int numMod8 = extras.length() % 8;
		if (numMod8 != 0) {
			// significa que quedan huecos y rellenamos con 0's para completar
			// el byte
			relleno = 8 - numMod8;
			for (int p = 0; p < relleno; p++)
				extras = extras + "0";
		}

		int veces = extras.length() / 8;
		int num;
		int posicion = 0;
		// vamos creando tantos bytes como nos hagan falta para codificar todos
		// los extras
		for (int i = 0; i < veces; i++) {
			num = binToDec(extras.substring(posicion, posicion + 8));
			posicion += 8;
			al.add((byte) ((char) num));
		}

		return al;
	}

	/**
	 * Convierte un string binario a decimal
	 * 
	 * @param pNumBin
	 * @return
	 */
	private int binToDec(String numBin) {
		int resultado = 0;
		for (int i = 0; i < numBin.length(); i++) {
			char digito = numBin.charAt(i);
			// resultado = resultado * base + digito
			try {
				int valDigito = Integer.parseInt(Character.toString(digito));
				resultado = resultado * 2 + valDigito;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultado;
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
		heSincronizadoMalAntes = false;

		boolean sectorValido = false;
		// para avanzar los bloques (en el 0 no se puede escribir)
		int numBloque = numBloqueComienzo;		
		
		// para recorrer string de MifareClassic.BLOCK_SIZE en MifareClassic.BLOCK_SIZE
		int recorrerString = 0;

		// relleno con 0's el pedido hasta que sea modulo16 para que luego no haya problemas ya que
		// se escribe mandando bloques de 16 bytes
		int numMod16 = pedidoCodificadoEnBytes.size() % 16;
		if (numMod16 != 0) {
			int huecos = 16 - numMod16;
			for (int i = 0; i < huecos; i++)
				pedidoCodificadoEnBytes.add((byte) 0);
		}

		if (cabePedidoEnTag(pedidoCodificadoEnBytes, mfc)) {
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
						if (recorrerString < pedidoCodificadoEnBytes.size()) {
							// recorremos con un for para obtener bloques de 16 bytes
							byte[] datosAlBloque = new byte[MifareClassic.BLOCK_SIZE];
							for (int i = 0; i < MifareClassic.BLOCK_SIZE; i++)
								datosAlBloque[i] = pedidoCodificadoEnBytes.get(i + recorrerString);
							// avanzo para el siguiente bloque
							recorrerString += MifareClassic.BLOCK_SIZE;
							// escribimos en el bloque
							mfc.writeBlock(numBloque, datosAlBloque);
						} else {
							escritoBienEnTag = true;
						}
					}
				}
				numBloque++;
			}
		}

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
	private boolean cabePedidoEnTag(ArrayList<Byte> pedidoCodificadoEnBytes,
			MifareClassic mfc) {
		int bytesTag = mfc.getBlockCount() * 16;
		int bytesProhibidosTag = (mfc.getSectorCount() + 1) * 16;
		int bytesLibresTag = bytesTag - bytesProhibidosTag;
		heCalculadoTam = true;
		cabeEnTag = copiaSeguridad.size() + pedidoCodificadoEnBytes.size() < bytesLibresTag;
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

		// para copia de seguridad
		copiaSeguridad = new ArrayList<Byte>();
		copiaUltimoBloque = new ArrayList<Byte>();
		
		//depurar
		bloquesDepuracion = new ArrayList<Boolean>();

		// Obtiene la instacia de la tarjeta nfc
		MifareClassic mfc = MifareClassic.get(mytag);
		// Establece la conexion
		mfc.connect();

		// Variable usada para saber por el bloque que vamos
		int numBloque = 0;
		// el texto que ha escrito el usuario
		byte[] textoByte = null;
		// para copiaSeguridad y errores
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
				bloquesDepuracion.add(sectorValido);
				if (sectorValido) {// Si es un sector valido
					textoByte = mfc.readBlock(numBloque); // leemos un bloque entero

					if (!esPrimerBloque){
						esCuenta = menosDos == textoByte[0];
						idRestauranteTag = textoByte[0];
						esPrimerBloque = true;
					}
					
					// copia ultimo bloque bloque
					copiaUltimoBloque = new ArrayList<Byte>();
					int i = 0;
					while (i < MifareClassic.BLOCK_SIZE && !menosUnoEncontrado) {
						if (textoByte[i] != menosUno) copiaUltimoBloque.add(textoByte[i]);
						else menosUnoEncontrado = true;
						numBloqueComienzo = numBloque;
						i++;
					}
					
					//copia seguridad
					copiaSeguridad.addAll(copiaUltimoBloque);
				}
			}
			numBloque++;
		}
		leidoBienDeTag = true;
		if (!menosUnoEncontrado) tagCorrupta = true;
		else tagCorrupta = false;

		// Cerramos la conexion
		mfc.close();
	}

	/************************************ GETTERS, SETTERS ****************************************/

	public HandlerDB getSqlCuenta() {
		return sqlCuenta;
	}

	public void setSqlCuenta(HandlerDB sqlCuenta) {
		this.sqlCuenta = sqlCuenta;
	}

	public ProgressDialog getProgressDialogSinc() {
		return progressDialogSinc;
	}

	public void setProgressDialogSinc(ProgressDialog progressDialogSinc) {
		this.progressDialogSinc = progressDialogSinc;
	}

	public String getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(String restaurante) {
		this.restaurante = restaurante;
	}

	public HandlerDB getSqlPedido() {
		return sqlPedido;
	}

	public void setSqlPedido(HandlerDB sqlPedido) {
		this.sqlPedido = sqlPedido;
	}

	public SQLiteDatabase getDbCuenta() {
		return dbCuenta;
	}

	public void setDbCuenta(SQLiteDatabase dbCuenta) {
		this.dbCuenta = dbCuenta;
	}

	public SQLiteDatabase getDbPedido() {
		return dbPedido;
	}

	public void setDbPedido(SQLiteDatabase dbPedido) {
		this.dbPedido = dbPedido;
	}

	public NfcAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(NfcAdapter adapter) {
		this.adapter = adapter;
	}

	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}

	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
	}

	public IntentFilter[] getWriteTagFilters() {
		return writeTagFilters;
	}

	public void setWriteTagFilters(IntentFilter[] writeTagFilters) {
		this.writeTagFilters = writeTagFilters;
	}

	public Tag getMytag() {
		return mytag;
	}

	public void setMytag(Tag mytag) {
		this.mytag = mytag;
	}

	public ArrayList<Byte> getPedidoCodificadoEnBytes() {
		return pedidoCodificadoEnBytes;
	}

	public void setPedidoCodificadoEnBytes(
			ArrayList<Byte> pedidoCodificadoEnBytes) {
		this.pedidoCodificadoEnBytes = pedidoCodificadoEnBytes;
	}

}
