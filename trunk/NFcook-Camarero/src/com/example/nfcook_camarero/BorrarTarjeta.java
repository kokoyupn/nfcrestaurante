package com.example.nfcook_camarero;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import baseDatos.HandlerGenerico;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class BorrarTarjeta extends Activity implements DialogInterface.OnDismissListener{

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;
	boolean escritoBienEnTag;
	String restaurante, codigoRest, abreviaturaRest;
	ProgressDialog	progressDialogSinc;

	private SQLiteDatabase dbEquivalencia;
	private HandlerGenerico sqlEquivalencia;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	/**
	 * Clase interna necesaria para ejecutar en segundo plano tareas (decodificacion de pedido, lectura NFC y 
	 * a�adir a la base de datos Mesas) mientras se muestra un progress dialog. 
	 * Cuando finalicen las tareas, �ste se cerrar� y esto provocar� la ejecuci�n del m�todo onDismiss que 
	 * cerrar�  esta ventana.
	 */
	 public class SincronizarPedidoBackgroundAsyncTask extends AsyncTask<Void, Void, Void> {
  
	  /**
	   * Se ejecuta antes de doInBackground.
	    */
	  @Override
	  protected void onPreExecute() {
		 progressDialogSinc.show(); //Mostramos el di�logo antes de comenzar
       }
	
	  /**
	   * Ejecuta en segundo plano.
	   * Si la tag es Mifare Cassic lee y decodifica el pedido, y a�ade los platos a su mesa correspondiente.
	   */
	  @Override
	  protected Void doInBackground(Void... params) {	  		  
		  SystemClock.sleep(1000);
		  // si es Mifare Classic
			try {   
				ArrayList<Byte> tagInicial = new ArrayList<Byte>();
				tagInicial.add((byte) Integer.parseInt(codigoRest));
				tagInicial.add((byte)255);
				escribirEnTagNFC(tagInicial);
				
				//Sonido de confirmacion
				sonidoManager.play(sonido);
				}
			 catch (IOException e) {//Error en la lectura has alejado el dispositivo de la tag
				escritoBienEnTag = false;
				e.printStackTrace();
			} catch (FormatException e) {
				escritoBienEnTag = false;
				e.printStackTrace();
			}
		
		  return null;
	  }

	  /**
	   * Se ejecuta cuando termina doInBackground.
	     */
	  @Override
	  protected void onPostExecute(Void result) {
		   progressDialogSinc.dismiss();
	  }

	}
	 
	/**Creamos la actividad, en esta lo que vamos a hacer es detectar una tarjeta Nfc en el momento en que se detecte leeremos su contenido y mostraremos un progress Dialog 
	 * hasta que se finalize la lectura, mas tarde se decodificaran estos platos y se a�adiran a la base de datos mesas.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_lecturanfc);

		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle("BORRAR TAG");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
		ctx=this;
		
		// preparamos para NFC
  		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected }; 
			
		//Creamos la instacia del manager de sonido
		sonidoManager = new SonidoManager(getApplicationContext());
		// Pone el volumen al volumen del movil actual
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Cargamos el sonido
        sonido=sonidoManager.load(R.raw.confirm);
		
        //Obtengo el resturante
  		Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("Restaurante");
 	   
  		//obtenemos el codigo y la abreviatura del rest
		try {
			sqlEquivalencia = new HandlerGenerico(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbEquivalencia = sqlEquivalencia.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR QR");
		}
		obtenerCodigoYAbreviaturaRestaurante();
		dbEquivalencia.close();
        
		//inicializamos variables para mostrar errores
		escritoBienEnTag = false;
		
        // creamos el progresDialog que se mostrara
  		crearProgressDialogSinc(); 

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
	
	/**
	 * Cierra la actividad y muestra un mensaje en funcion de que haya sucedido. Se ejecuta cuando se cierra el progressDialog.
	 * 
	 */
	public void onDismiss(DialogInterface dialog) {
		if (escritoBienEnTag)
			Toast.makeText(this, "Tarjeta borrada correctamente", Toast.LENGTH_LONG ).show();		
		else
			Toast.makeText(this, "Error al borrar la tarjeta", Toast.LENGTH_LONG ).show();		 
		
		finish();	
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
	
	private NdefRecord createRecord(ArrayList<Byte> pedidoCodificadoEnBytes, Ndef ndef) throws UnsupportedEncodingException {

	    byte[] payload = new byte[ndef.getMaxSize()-8];
	    
	    System.out.println("TAM: " + ndef.getMaxSize());
	    for (int i = 0; i < pedidoCodificadoEnBytes.size(); i++){
	    	payload[i] = pedidoCodificadoEnBytes.get(i);
	    }
	    
	    for (int i = pedidoCodificadoEnBytes.size() ; i < ndef.getMaxSize() - 8; i++){
	    	payload[i] = 0;
	    }

	    NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
	    return recordNFC;
	}

	private void escribirEnTagNFC(ArrayList<Byte> pedidoCodificadoEnBytes) throws IOException, FormatException {

		// inicializacion de estas variables para no tener que ponerlas siempre en el catch
		escritoBienEnTag = false;
		try {
			Ndef ndef = Ndef.get(mytag);
			if (cabePedidoEnTag(pedidoCodificadoEnBytes, ndef)){
				NdefRecord[] records = { createRecord(pedidoCodificadoEnBytes,ndef) };
			    NdefMessage message = new NdefMessage(records); 
	    
		        // If the tag is already formatted, just write the message to it
		        if(ndef != null) {
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
		        // If the tag is not formatted, format it with the message
		        } else {
		            NdefFormatable format = NdefFormatable.get(mytag);
		            if(format != null) {
		                try {
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
		        }
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
		return pedidoCodificadoEnBytes.size() < ndef.getMaxSize();
		
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

	/**Con este m�todo detectamos la presencia de la tarjeta tag y establecemos la conexi�n
	 * luego procedemos a a�adir los platos a la base de datos mesas decodificandolos.
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			Toast.makeText(this, this.getString(R.string.ok_detection), Toast.LENGTH_LONG ).show();
			if(mytag == null){
					Toast.makeText(this, this.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
			}else {
					// ejecuta el progressDialog, codifica, escribe en tag 
					new SincronizarPedidoBackgroundAsyncTask().execute();
			}
		}
	}

	public void onPause(){
		super.onPause();
		adapter.disableForegroundDispatch(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume(){
		super.onResume();
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters,null);
		
		if (!adapter.isEnabled())
	        Toast.makeText(getApplicationContext(), "Por favor activa NFC", Toast.LENGTH_LONG).show();
	
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
	              
	            default:
	                finish();
	                return true;
            }
    }

}