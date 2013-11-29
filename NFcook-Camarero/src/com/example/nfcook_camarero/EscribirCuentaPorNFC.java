package com.example.nfcook_camarero;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import baseDatos.HandlerGenerico;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.MenuItem;
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
	private boolean cabeEnTag, escritoBienEnTag, tengoNdefFormatable;
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
			
			try {				
				// se decodifica antes para no perder tiempo de escritura
				escribirEnTagNFC(cuentaCodificadaEnBytes);
			} catch (IOException e) {
				escritoBienEnTag = false;
				e.printStackTrace();
			} catch (FormatException e) {
				escritoBienEnTag = false;
				e.printStackTrace();
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
    	actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0B3861")));

    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
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
		
		//inicializamos variables para mostrar errores
		escritoBienEnTag = false;
		cabeEnTag = true;
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
		if (escritoBienEnTag){
			Toast.makeText(this,"Cuenta sincronizada correctamente",Toast.LENGTH_LONG).show();
		} else {						
			if (!cabeEnTag) Toast.makeText(this,"Cuenta no sincronizada. No cabe en la tarjeta",Toast.LENGTH_LONG).show();
			else Toast.makeText(this,"Cuenta no sincronizada. Has levantado el dispositivo antes de tiempo. Vuelve a intentarlo",Toast.LENGTH_LONG).show();	
		}
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
	 * Codifica la cuenta y pondra al inicio el idRest, un -2 indicando que es una cuenta,
	 * los id platos y luego un -1 indicando el final
	 * @param cuenta
	 * @return
	 */
	private void codificarCuenta(String cuenta) {

		// variable donde ira el pedido con la codificacion final
		cuentaCodificadaEnBytes = new ArrayList<Byte>();
		// se mete el idRest
		cuentaCodificadaEnBytes.add(idRestaurante);
		// para indicar que es una cuenta un -2
		cuentaCodificadaEnBytes.add((byte) Integer.parseInt("254"));	
		// separamos por platos y los metemos al arrayList
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
	        	NdefMessage message = ndef.getCachedNdefMessage();
	        	if(message != null){				
	        		escribirTagNFC(pedidoCodificadoEnBytes);
	        	}else{
	        		
					if (!tengoNdefFormatable)
						escribirTagNFC(pedidoCodificadoEnBytes);
					
					else 
						formatearTagNFC(pedidoCodificadoEnBytes);
	        	}
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
			            } catch (TagLostException tle) {
			            	System.out.println("tag lost exception al escribir");		            	
			            } catch (IOException ioe) {
			            	System.out.println("error IO al escribir");
			            } catch (FormatException fe) {
			            	System.out.println("error format al escribir");
			            }
		        	}
		        }
	    } catch(Exception e) {
	    	System.out.println("ultimo try");
	    }
	}	
		
	
	private void formatearTagNFC(ArrayList<Byte> pedidoCodificadoEnBytes) throws IOException, FormatException {

		// inicializacion de estas variables para no tener que ponerlas siempre en el catch
		escritoBienEnTag = false;
		try {
            NdefFormatable format = NdefFormatable.get(mytag);
            if(format != null) {
                try {
                	NdefRecord[] records = { createRecord(pedidoCodificadoEnBytes) };
    			    NdefMessage message = new NdefMessage(records); 
                	
                    format.connect();
                    format.format(message);
                    escritoBienEnTag = true;
                } catch (TagLostException tle) {
                	System.out.println("tag lost exception al formatear");
                } catch (IOException ioe) {
                	System.out.println("error IO al formatear");
                } catch (FormatException fe) {
                	System.out.println("error format al formatear");
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
			//Toast.makeText(this, this.getString(R.string.ok_detection),Toast.LENGTH_SHORT).show();
			
			ArrayList<String> tech = new ArrayList<String>();
			for(int i = 0; i<mytag.getTechList().length; i++)
				tech.add(mytag.getTechList()[i]);
			
			if (tech.contains("android.nfc.tech.NdefFormatable")){
				tengoNdefFormatable = true;
			}
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

}
