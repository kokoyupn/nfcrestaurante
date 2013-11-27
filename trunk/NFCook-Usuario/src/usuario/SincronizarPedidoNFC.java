package usuario;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
	private boolean cabeEnTag, leidoBienDeTag, escritoBienEnTag, tagCorrupta, restauranteCorrecto, esCuenta;
	private static boolean heSincronizadoMalAntes;
	private static ArrayList<Byte> copiaSeguridad;
	private String abreviaturaRest, codigoRest;
	// Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;

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
			
			// leemos y hacemos copia seguridad
			try {
				// si he sincronizado mal no tengo que leer porque ya tengo la copia y nadie mas ha escrito
				// se reinician las copias de seguridad porque no se ha sincronizado mal
//				if (!heSincronizadoMalAntes){
					hacerCopiaSeguridad(leerTagNFC(mytag)); // lee de la tarjeta y hace la copia de seguridad
					if(copiaSeguridad != null)
						System.out.println("Copia seguridad: " + copiaSeguridad.toString()); //TODO quitar
//				}
				
			} catch (IOException e1) {
				leidoBienDeTag = false;
				e1.printStackTrace();
			} catch (FormatException e1) {
				leidoBienDeTag = false;
				e1.printStackTrace();
			}
				
			// escribimos
			if (leidoBienDeTag && !esCuenta && (!tagCorrupta || heSincronizadoMalAntes)) {
				
				if (estoyEnRestauranteCorrecto(copiaSeguridad.get(0))){
					// une la copia de seguridad con el pedido actual y añade al principio el rest
					codificarPedido(damePedidoActual());
					try {
						System.out.println("Pedido nuevo: " + pedidoCodificadoEnBytes.toString()); //TODO quitar
						escribirEnTagNFC(pedidoCodificadoEnBytes); 
					} catch (IOException e) {
						e.printStackTrace();
					} catch (FormatException e) {
						e.printStackTrace();
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

		// mortrar aviso inicial
		mostrarAvisoInicial();
		
		// para reducir mas el tiempo y que no hay problemas en la escritura
		abrirBasesDeDatos();
		obtenerIdRestYAbreviatura();		
		
		//inicializamos variables para mostrar errores
		escritoBienEnTag = tagCorrupta = restauranteCorrecto = esCuenta = false;
		leidoBienDeTag = cabeEnTag = true;
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
		
		Intent intent = new Intent();
		intent.putExtra("Origen", "Pedido");
		setResult(RESULT_CANCELED, intent);
		if (!tagCorrupta) {
			if (leidoBienDeTag) {
				if (!esCuenta){
					if (escritoBienEnTag){
						enviarPedidoACuenta();
						setResult(RESULT_OK, intent);
						Toast.makeText(this,"Pedido sincronizado correctamente. Puedes verlo en cuenta",Toast.LENGTH_LONG).show();
					} else {						
						if (heSincronizadoMalAntes) Toast.makeText(this,"Has levantado el dispositvo antes de tiempo. Tienes que volver a sincronizar",Toast.LENGTH_LONG).show();
						else if (!cabeEnTag) Toast.makeText(this,"Pedido no sincronizado. No cabe en la tarjeta. Llama a camarero o usa otro metodo de transmision",Toast.LENGTH_LONG).show();
						else if (!restauranteCorrecto) Toast.makeText(this,"Pedido no sincronizado. No estas en el restaurante correcto",Toast.LENGTH_LONG).show();
						else Toast.makeText(this,this.getString(R.string.error_escritura),Toast.LENGTH_LONG).show();	
					}
				} else Toast.makeText(this,"Pedido no sincronizado. Avisa al camarero porque hay una cuenta en la tarjeta",Toast.LENGTH_LONG).show();
			} else Toast.makeText(this,this.getString(R.string.error_escritura),Toast.LENGTH_LONG).show();
		} else {
			if (heSincronizadoMalAntes) Toast.makeText(this,"Has levantado el dispositvo antes de tiempo. Tienes que volver a sincronizar",Toast.LENGTH_LONG).show();
			else Toast.makeText(this,"Pedido no sincronizado. Tiene que sincronizar la persona que sincronizó mal",Toast.LENGTH_LONG).show();
		}
		
		if (!heSincronizadoMalAntes && leidoBienDeTag){
			cerrarBasesDeDatos();
			finish();
		}
	}
	
	public void onBackPressed() {
		if (!heSincronizadoMalAntes) 
			super.onBackPressed();
	    else 
	    	Toast.makeText(this,"No puedes volver atrás hasta que sincronices correctamente",Toast.LENGTH_LONG).show();
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
		String[] campos = new String[] { "Id", "Plato", "Ingredientes", "Extras", "PrecioPlato", "Restaurante", "IdHijo" };
		String[] datosRestaurante = new String[] { restaurante };
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante, null, null, null);

		while (cursorPedido.moveToNext()) {
			ContentValues platoCuenta = new ContentValues();
			platoCuenta.put("Id", cursorPedido.getString(0));
			platoCuenta.put("Plato", cursorPedido.getString(1));
			platoCuenta.put("Ingredientes", cursorPedido.getString(2));
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
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		//sqlPedido.close();
		sqlRestaurante.close();
	}

	/********************************* CODIFICACION ************************************/

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

	/**
	 * Codifica el pedido (listaDePlatos) y lo devuelve con un formato de
	 * ArrayList<Byte>. Para ello va separando mediante StringTokenizer los
	 * platos, obteniendo su id, extras e ingredientes.
	 * 
	 * @param copia
	 * @param listaPlatosStr
	 * @return
	 */
	private void codificarPedido(String listaPlatosStr) {

		// variable donde ira el pedido con la codificacion final
		pedidoCodificadoEnBytes = new ArrayList<Byte>();

		// metemos la copia de seguridad
		pedidoCodificadoEnBytes.addAll(copiaSeguridad);
		
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
				ArrayList<Byte> alExtras = codificaExtrasEIngredientes(extras);
				// tamaño de los extras que habra que leer
				pedidoCodificadoEnBytes.add((byte) alExtras.size());
				pedidoCodificadoEnBytes.addAll(alExtras);
			} else
				pedidoCodificadoEnBytes.add((byte) 0);

			// comentarios
			if (plato.contains("*")) {
				String ingredientes = stTodoSeparado.nextToken();
				ArrayList<Byte> alIngredientes = codificaExtrasEIngredientes(ingredientes);
				// tamaño de comentarios que habra que leer
				pedidoCodificadoEnBytes.add((byte) alIngredientes.size());
				pedidoCodificadoEnBytes.addAll(alIngredientes);
			} else
				pedidoCodificadoEnBytes.add((byte) 0);

		}
		
		// para indicar que ha finalizado el pedido escribo un 255
		pedidoCodificadoEnBytes.add((byte) Integer.parseInt("255"));
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
	private ArrayList<Byte> codificaExtrasEIngredientes(String cod) {

		ArrayList<Byte> al = new ArrayList<Byte>();
		int relleno = 0;
		int numMod8 = cod.length() % 8;
		if (numMod8 != 0) {
			// significa que quedan huecos y rellenamos con 0's para completar
			// el byte
			relleno = 8 - numMod8;
			for (int p = 0; p < relleno; p++)
				cod = cod + "0";
		}

		int veces = cod.length() / 8;
		int num;
		int posicion = 0;
		// vamos creando tantos bytes como nos hagan falta para codificar todos
		// los extras
		for (int i = 0; i < veces; i++) {
			num = binToDec(cod.substring(posicion, posicion + 8));
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
	 * ATENCIÓN: Sólo se utilizan los primeros 439 bytes de la tarjeta.
	 * Del 439 al 462 no se pueden utilizar
	 * 
	 * @param text
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	
	private NdefRecord createRecord(ArrayList<Byte> pedidoCodificadoEnBytes, Ndef ndef) throws UnsupportedEncodingException {

	    String lang       = "en";
	    byte[] langBytes  = lang.getBytes("UTF-8");
	    int    langLength = langBytes.length;
	    
	    byte[] payload    = new byte[ndef.getMaxSize() - (1 + langLength) - 12];
	    System.out.println("PUTA");
	    System.out.println(payload.length);
	    System.out.println(ndef.getMaxSize());

	    // set status byte (see NDEF spec for actual bits)
	    payload[0] = (byte) langLength;

	    // copy langbytes and textbytes into payload
	    System.arraycopy(langBytes, 0, payload, 1, langLength);
	
	    for (int i = 0; i < pedidoCodificadoEnBytes.size(); i++){
	    	payload[i+langLength+1] = pedidoCodificadoEnBytes.get(i);
	    }

	    NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, 
	                                       NdefRecord.RTD_TEXT, 
	                                       new byte[0], 
	                                       payload);

	    return record;
	}
	
	private NdefRecord createRecord(ArrayList<Byte> pedidoCodificadoEnBytes) throws UnsupportedEncodingException {

		String lang       = "en";
	    byte[] langBytes  = lang.getBytes("UTF-8");
	    int    langLength = langBytes.length;
	    
	    byte[] payload    = new byte[454 - (1 + langLength) - 12];

	    // set status byte (see NDEF spec for actual bits)
	    payload[0] = (byte) langLength;

	    // copy langbytes and textbytes into payload
	    System.arraycopy(langBytes, 0, payload, 1, langLength);
	
	    for (int i = 0; i < pedidoCodificadoEnBytes.size(); i++){
	    	payload[i+langLength+1] = pedidoCodificadoEnBytes.get(i);
	    }

	    NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, 
	                                       NdefRecord.RTD_TEXT, 
	                                       new byte[0], 
	                                       payload);

	    return record;
	}

	private void escribirEnTagNFC(ArrayList<Byte> pedidoCodificadoEnBytes) throws IOException, FormatException {

		// inicializacion de estas variables para no tener que ponerlas siempre en el catch
		try {
			Ndef ndef = Ndef.get(mytag);	    
	        // If the tag is already formatted, just write the message to it
	        if(ndef != null) {
	    		escribirTagNFC(pedidoCodificadoEnBytes);
	        // If the tag is not formatted, format it with the message
	        } else {
	            formatearTagNFC(pedidoCodificadoEnBytes);
	            //mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);   
	           // escribirTagNFC(pedidoCodificadoEnBytes);
	        }
	    } catch(Exception e) {
	    	System.out.println("ultimo try");
	    }
	}
		
	private void escribirTagNFC(ArrayList<Byte> pedidoCodificadoEnBytes) throws IOException, FormatException {

		// inicializacion de estas variables para no tener que ponerlas siempre en el catch
		escritoBienEnTag = false;
        heSincronizadoMalAntes = false;
		try {
			Ndef ndef = Ndef.get(mytag);	    
		        // If the tag is already formatted, just write the message to it
		        if(ndef != null) {
		        	if (cabePedidoEnTag(pedidoCodificadoEnBytes, ndef)){
			        	NdefRecord[] records = { createRecord(pedidoCodificadoEnBytes, ndef) };
					    NdefMessage message = new NdefMessage(records); 
			        	
			            ndef.connect();
			 
			            // Make sure the tag is writable
			            if(!ndef.isWritable()) {
			                System.out.println("tag no es writable");
			            }
			 
			            try {// Write the data to the tag		                
			                ndef.writeNdefMessage(message);
			                escritoBienEnTag = true;
			                tagCorrupta = false;
			            } catch (TagLostException tle) {
			            	System.out.println("tag lost exception al escribir");	
			            	heSincronizadoMalAntes = true;
			            } catch (IOException ioe) {
			            	System.out.println("error IO al escribir");
			            	heSincronizadoMalAntes = true;
			            } catch (FormatException fe) {
			            	System.out.println("error format al escribir");
			            	heSincronizadoMalAntes = true;
			            }
		        	}
		        }
	    } catch(Exception e) {
	    	System.out.println("ultimo try");
	    	cabeEnTag = false;
	    }
	}	
		
	
	private void formatearTagNFC(ArrayList<Byte> pedidoCodificadoEnBytes) throws IOException, FormatException {

		// inicializacion de estas variables para no tener que ponerlas siempre en el catch
		escritoBienEnTag = false;
		heSincronizadoMalAntes = false;
		try {
            NdefFormatable format = NdefFormatable.get(mytag);
            if(format != null) {
                try {
                	NdefRecord[] records = { createRecord(pedidoCodificadoEnBytes) };
    			    NdefMessage message = new NdefMessage(records); 
                	
                    format.connect();
                    format.format(message);
                    escritoBienEnTag = true;
                    tagCorrupta = false;
                } catch (TagLostException tle) {
                	System.out.println("tag lost exception al formatear");
                	heSincronizadoMalAntes = true;
                } catch (IOException ioe) {
                	System.out.println("error IO al formatear");
                	heSincronizadoMalAntes = true;
                } catch (FormatException fe) {
                	System.out.println("error format al formatear");
                	heSincronizadoMalAntes = true;
                }
	        } else {
	        	System.out.println("format es null");
            }
	    } catch(Exception e) {
	    	System.out.println("ultimo try");
	    }
	}
	
	
	/**
	 * Devuelve un booleano informando de si el pedido cabe o no cabe en la
	 * tarjeta
	 */
	private boolean cabePedidoEnTag(ArrayList<Byte> pedidoCodificadoEnBytes, Ndef ndef) {
		return cabeEnTag = pedidoCodificadoEnBytes.size() < ndef.getMaxSize() - 15;
		
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
	 * Metodo que se encarga de leer bloques de la tarjeta nfc
	 * 
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private ArrayList<Byte> leerTagNFC(Tag tag) throws IOException, FormatException {
		tagCorrupta = false;
		ArrayList<Byte> mensajeEnBytesBueno = new ArrayList<Byte>();
		
		Ndef ndef = Ndef.get(tag);
		if(ndef != null){
			NdefMessage message = ndef.getCachedNdefMessage();
			if(message != null){
				byte[] mensajeEnBytes = message.toByteArray();
				// Con este "for" eliminamos los datos inservibles del array de bytes
				for (int i=0; i<mensajeEnBytes.length-10; i++){
					mensajeEnBytesBueno.add(mensajeEnBytes[i+10]);
				}
				leidoBienDeTag = true;
			}else leidoBienDeTag = false;
		}else{
			tagCorrupta = true;
		}
		return mensajeEnBytesBueno;		
	}

	private void hacerCopiaSeguridad(ArrayList<Byte> mensaje) {
	
		ArrayList<Byte> copiaSeguridadAux = new ArrayList<Byte>();
		
		
		if(!tagCorrupta && leidoBienDeTag){
			Iterator<Byte> itMensaje = mensaje.iterator();
			copiaSeguridadAux.add(itMensaje.next()); //metemos el idRest
			
			boolean parar = false;
			while(itMensaje.hasNext() && !parar){					
		
				Byte idByte = itMensaje.next();
				int id = decodificaByte(idByte);
					
				parar = id==255 || id==254;//El mensaje termina con un -1 en la tag
				esCuenta = id==254;			
		
				if(!parar){ //Si no ha acabado el mensaje
					
					copiaSeguridadAux.add(idByte); // metemos el idByte en la copia de seguridad
					
					// extras
					Byte extrasByte = itMensaje.next();
					copiaSeguridadAux.add(extrasByte); // metemos extrasByte en la copia de seguridad
					int numBytesExtras =  decodificaByte(extrasByte);
					for (int i = 0; i < numBytesExtras; i++ )
						copiaSeguridadAux.add(itMensaje.next());  //metemos los extras binarios en la copia de seguridad
											
					// ingredientes		
					Byte ingredByte = itMensaje.next();
					copiaSeguridadAux.add(ingredByte); // metemos ingredByte en copia de seguridad
					int numBytesIngredientes = decodificaByte(ingredByte);				
					for(int i=0; i < numBytesIngredientes; i++){
						copiaSeguridadAux.add(itMensaje.next()); // metemos ingred binarios en copia de seguridad
					}
				}//if parar             
			 }//while	
			
			if(!parar) 
				tagCorrupta = true;
			else{
				copiaSeguridad = new ArrayList<Byte>();
				copiaSeguridad.addAll(copiaSeguridadAux);
			}
		}
	}
	
	/**Metodo que se encargar de convertir un byte dado por parametro a un tipo int
	 *  
	 * @param idByte
	 * @return
	 */
		 
	public static int decodificaByte(byte idByte){
		int id = (int)idByte;
		if (id < 0) return id + 256;
		else return id;
	}
	
	/** Convierte un numero decimal en su equivalente en binario
	 *  
	 * @param decimal
	 * @return
	 */
	public static String decToBin(int decimal){
		
		int base = 2;
		int result = 0;
		int multiplier = 1;
		
		if (decimal < 0)
			decimal = 256 + decimal;
		
		while (decimal>0){
			int residue = decimal%base;
			decimal = decimal/base;
			result = result +residue*multiplier;
			multiplier = multiplier * 10;
		}
		// rellenamos con ceros a la izquierda para que tenga siempre 8 bytes
		String resultStr = "";
		String numBytes = ""+result;
		int veces = 8 - numBytes.length();
		
		for (int i = 0; i < veces; i++)
			resultStr += "0";
	
		resultStr += result;
			
		return resultStr;
		
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
