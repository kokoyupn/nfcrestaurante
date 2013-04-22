package com.example.nfcook_camarero;




import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Iterator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Borrar_tarjeta extends Activity implements DialogInterface.OnDismissListener{

	//Variables usadas para el nfc
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;
	boolean escritoBienEnTag;
	boolean esMFC;
	ProgressDialog	progressDialogSinc;
	String restaurante;
	int numeroRestaurante;
	/*Variables para obtener el valor equivalente del restaurante*/
	String ruta="/data/data/com.example.nfcook_camarero/databases/";
	private SQLiteDatabase dbEquivalencia;
	private HandlerGenerico sql;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	/**
	 * Clase interna necesaria para ejecutar en segundo plano tareas (decodificacion de pedido, lectura NFC y 
	 * añadir a la base de datos Mesas) mientras se muestra un progress dialog. 
	 * Cuando finalicen las tareas, éste se cerrará y esto provocará la ejecución del método onDismiss que 
	 * cerrará  esta ventana.
	 */
	 public class SincronizarPedidoBackgroundAsyncTask extends AsyncTask<Void, Void, Void> {
  
	  /**
	   * Se ejecuta antes de doInBackground.
	    */
	  @Override
	  protected void onPreExecute() {
		 progressDialogSinc.show(); //Mostramos el diálogo antes de comenzar
       }
	
	  /**
	   * Ejecuta en segundo plano.
	   * Si la tag es Mifare Cassic lee y decodifica el pedido, y añade los platos a su mesa correspondiente.
	   */
	  @Override
	  protected Void doInBackground(Void... params) {	  		  
		  SystemClock.sleep(1000);
		  // si es Mifare Classic
		  if (esMFC) {
				try {   
					read(mytag);//Se ha detectado la tag procedemos a leerla
									
					//Sonido de confirmacion
					sonidoManager.play(sonido);
					}
				 catch (IOException e) {//Error en la lectura has alejado el dispositivo de la tag
					Toast.makeText(ctx, ctx.getString(R.string.error_reading), Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				} catch (FormatException e) {
					Toast.makeText(ctx, ctx.getString(R.string.error_reading) , Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				}
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
	 * hasta que se finalize la lectura, mas tarde se decodificaran estos platos y se añadiran a la base de datos mesas.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_lecturanfc);

		
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
  		/*Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("restaurante");*/
  		restaurante="Foster";
  		
  		 try{ //Abrimos la base de datos para consultarla
 	       	sql = new HandlerGenerico(getApplicationContext(),ruta,"Equivalencia_Restaurantes.db"); 
 	        dbEquivalencia = sql.open();
 	     
 	    }catch(SQLiteException e){
 	        	Toast.makeText(getApplicationContext(),"No existe la base de datos equivalencia",Toast.LENGTH_SHORT).show();
 	       }
 	   
 	   try{
 		  /**Campos de la base de datos Restaurante TEXT,Numero INTEGER,Abreviatura TEXT
 	        * Nombre de la tabla de esa base de datos Restaurantes*/		
 		   String[] campos = new String[]{"Numero","Abreviatura"};
 		   String[] datos = new String[]{restaurante};
 		   //Buscamos en la base de datos el nombre de usuario y la contraseña
 		   Cursor c = dbEquivalencia.query("Restaurantes",campos,"Restaurante=?",datos, null,null, null);
 	  	   		   	 	   
 	  	   c.moveToFirst();
        	
 	  	   numeroRestaurante = c.getInt(0);
 	  	   String abreviatura = c.getString(1);
 	  	   
 	  	
 		}catch(Exception e){ }
 	   
        
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
		if (!esMFC) {
			Toast.makeText(this, "No se ha borrado la tag. La tag no es Mifare Classic.", Toast.LENGTH_LONG ).show();		
		}
		else {
			if (escritoBienEnTag) {
			 Toast.makeText(this, "Borrado correctamente.", Toast.LENGTH_LONG ).show();		
			}
			else {
				Toast.makeText(this, "Borrado no completado", Toast.LENGTH_LONG ).show();		 
			}
		}
		finish();	
	}

	
	//@SuppressLint("NewApi")
	/**
	 * Metodo que se encarga de leer bloques de la tarjeta nfc
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void read(Tag tag) throws IOException, FormatException {	
		
		String aux = "";
		aux += "255";
		ArrayList<Byte> pedidoCodificadoEnBytes = new ArrayList<Byte>();
		
		ArrayList<Byte> al = new ArrayList<Byte>();
		al.add((byte) numeroRestaurante);
		pedidoCodificadoEnBytes.addAll(al);
		
		al = new ArrayList<Byte>();
		al.add((byte) Integer.parseInt(aux));
		pedidoCodificadoEnBytes.addAll(al);
		
		
		// Obtenemos instancia de MifareClassic para el tag.
				MifareClassic mfc = MifareClassic.get(mytag);
												
				// Habilitamos operaciones de I/O
				mfc.connect();

				boolean sectorValido = false;				
				
				// para recorrer string de MifareClassic.BLOCK_SIZE en MifareClassic.BLOCK_SIZE
				int recorrerString = 0;	
				// para saber si se ha escrito o no
				escritoBienEnTag = false;
				
				// relleno con 0's el pedido hasta que sea modulo16 para que luego no haya problemas ya que 
				// se escribe mandando bloques de 16 bytes
				int  numMod16 = pedidoCodificadoEnBytes.size() % 16;
				if (numMod16 != 0){
					int huecos = 16-numMod16;
					for (int i = 0; i < huecos; i++)
						pedidoCodificadoEnBytes.add((byte) 0);
				}
				for (int i=0; i<mfc.getBlockCount();i++)
				{
					if (sePuedeEscribirEnBloque(i)) {
						// cada sector tiene 4 bloques
						int numSector = i / 4;
						// autentifico con la key A para escritura
						sectorValido = mfc.authenticateSectorWithKeyA(numSector,MifareClassic.KEY_DEFAULT);
						if (sectorValido) {
							// si es menor significa que queda por escribir cosas
							if (recorrerString < pedidoCodificadoEnBytes.size()) { //textoBytes.length 
								// recorremos con un for para obtener bloques de 16 bytes
								byte[] datosAlBloque = new byte[MifareClassic.BLOCK_SIZE];
								for (int j=0; j<MifareClassic.BLOCK_SIZE; j++)
									datosAlBloque[j] = pedidoCodificadoEnBytes.get(j+recorrerString);
								// avanzo para el siguiente bloque
								recorrerString += MifareClassic.BLOCK_SIZE;
								// escribimos en el bloque
								mfc.writeBlock(i, datosAlBloque);
							} else {
								// escribimos ceros en el resto de la tarjeta porque ya no queda nada por escribir
								byte[] ceros = new byte[MifareClassic.BLOCK_SIZE];
								mfc.writeBlock(i, ceros);
							}
						}
					} 
				}
				escritoBienEnTag = true;
								
				// Cerramos la conexion
				mfc.close();
		
	}

	/**
	 * Metedo encargado de comprobar si se puede o no escribir en un bloque pasado por parametro
	 * @param numBloque
	 * @return
	 */
	private boolean sePuedeEscribirEnBloque(int numBloque) {
		return (numBloque+1) % 4 != 0 && numBloque != 0 ; 
	}

	/**Con este método detectamos la presencia de la tarjeta tag y establecemos la conexión
	 * luego procedemos a añadir los platos a la base de datos mesas decodificandolos.
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			Toast.makeText(this, this.getString(R.string.ok_detection), Toast.LENGTH_LONG ).show();
			// compruebo que la tarjeta sea mifare classic
			String[] tecnologiasTag = mytag.getTechList();
			esMFC = false;
			for (int i = 0; i < tecnologiasTag.length; i++)
				esMFC |= tecnologiasTag[i].equals("android.nfc.tech.MifareClassic");
		   }
			if(mytag == null){
					Toast.makeText(this, this.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
			}else {
					// ejecuta el progressDialog, codifica, escribe en tag 
					new SincronizarPedidoBackgroundAsyncTask().execute();
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
                return super.onOptionsItemSelected(item);
        }
        }



	

	
    
  
}