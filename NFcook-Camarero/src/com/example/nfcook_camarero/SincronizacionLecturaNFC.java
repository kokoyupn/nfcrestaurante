package com.example.nfcook_camarero;


import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;

import baseDatos.HandlerGenerico;
import fragments.PantallaMesasFragment;
import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SincronizacionLecturaNFC extends Activity implements DialogInterface.OnDismissListener{

	//Variables usadas para el nfc
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	boolean leidoBienEnTag, tagCorrupta, restauranteCorrecto, esCuenta, tengoNdefFormatable;
	ProgressDialog	progressDialogSinc;
	
	HandlerGenerico sqlMesas,sqlRestaurante,sqlEquivalencia;	
	SQLiteDatabase dbMesas,dbRestaurante,dbEquivalencia;
	
	String numMesa, idCamarero, numPersonas, restaurante, codigoRest, abreviaturaRest;
	ArrayList<Byte> mensajeEnBytesBueno;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	
	//Fecha y hora
	String formatteHour, formatteDate;
    
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
		  //SystemClock.sleep(1000);
		  try {   
				read(mytag);
				if (!tagCorrupta && leidoBienEnTag){
					//Decodificamos el mensaje leido de la tag y a�adimos los platos a la base de datos.
					decodificar(mensajeEnBytesBueno);
					//Sonido de confirmacion
					sonidoManager.play(sonido);
				}
		  } catch (IOException e) {//Error en la lectura has alejado el dispositivo de la tag
				leidoBienEnTag = false;
				e.printStackTrace();
			} catch (FormatException e) {
				leidoBienEnTag = false;
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
	@SuppressLint("SimpleDateFormat")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_lecturanfc);

		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle("SINCRONIZAR PEDIDO");
    	actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0B3861")));
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	    	
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante=bundle.getString("Restaurante");
		
		ctx=this;
		
		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected }; 

		//Sacamos la fecha a la que el camarero ha introducido la mesa
    	Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formatteDate = df.format(date);
        
        //Sacamos la hora a la que el camarero ha introducido la mesa
        Date dt = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        formatteHour = dtf.format(dt.getTime());
        
		//Creamos la instacia del manager de sonido, ponemos volumen y cargamos el sonido
		sonidoManager = new SonidoManager(getApplicationContext());
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sonido=sonidoManager.load(R.raw.confirm);
       
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
  		tagCorrupta = esCuenta = false;
  		leidoBienEnTag = restauranteCorrecto = true;
                
		// creamos el progresDialog que se mostrara
  		crearProgressDialogSinc(); 
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
		if(restauranteCorrecto){	
			if(!tagCorrupta) {
				if (!esCuenta){
					if (leidoBienEnTag) Toast.makeText(this, "Pedido sincronizado correctamente", Toast.LENGTH_LONG ).show();		
			        else Toast.makeText(this, "Pedido no sincronizado", Toast.LENGTH_LONG ).show();	
				} else Toast.makeText(getApplicationContext(), "Hay una cuenta en la tarjeta", Toast.LENGTH_LONG).show();
		    } else Toast.makeText(getApplicationContext(), "La tarjeta est� corrupta, usa la opci�n borrar para restablecerla", Toast.LENGTH_LONG).show();
		} else Toast.makeText(getApplicationContext(), "Los platos en la tarjeta no corresponden a este restaurante", Toast.LENGTH_LONG).show();
		finish();	
	}

	/**
	 * Metodo que se encarga de leer de la tarjeta nfc
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void read(Tag tag) throws IOException, FormatException {	
		
		 /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
		mensajeEnBytesBueno = new ArrayList<Byte>();
		Ndef ndef = Ndef.get(tag);
		if (ndef != null){
			NdefMessage message = ndef.getCachedNdefMessage();
			if(message != null){
				byte[] mensajeEnBytes = message.toByteArray();			
				// Con este "for" eliminamos los datos inservibles del array de bytes
				for (int i=0; i<mensajeEnBytes.length-10; i++){
					mensajeEnBytesBueno.add(mensajeEnBytes[i+10]);
				}
				leidoBienEnTag = true;
			} else {
				if (!tengoNdefFormatable)
					tagCorrupta = true;	
			    else leidoBienEnTag = false;
			}
		} else{
			tagCorrupta = true;
		}
	}
		

	/**Con este m�todo detectamos la presencia de la tarjeta tag y establecemos la conexi�n
	 * luego procedemos a a�adir los platos a la base de datos mesas decodificandolos.
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			//Toast.makeText(this, this.getString(R.string.ok_detection), Toast.LENGTH_LONG ).show();
	
			ArrayList<String> tech = new ArrayList<String>();
			for(int i = 0; i<mytag.getTechList().length; i++)
				tech.add(mytag.getTechList()[i]);
			
			if (tech.contains("android.nfc.tech.NdefFormatable")){
				tengoNdefFormatable = true;
			}
			
			if(mytag == null){
				Toast.makeText(this, this.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
			}else {
				// ejecuta el progressDialog, codifica, escribe en tag e intercambia datos de pedido a cuenta en segundo plano
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
	        Toast.makeText(getApplicationContext(), "Por favor activa NFC.", Toast.LENGTH_LONG).show();
	
	}
	
	/**
	 * Decodificamos el contenido leido de la tag separando cada plato y a�adiendolo a las mesas.
	 */
	private void decodificar(ArrayList<Byte> mensaje) {
		
		Iterator<Byte> itPlatos = mensaje.iterator();
		boolean parar=false;
		
		int numRestaurante = decodificaByte(itPlatos.next());  //ID DEL REST
		
		restauranteCorrecto = numRestaurante == Integer.parseInt(codigoRest);		
		
		if (restauranteCorrecto){
		
			while(itPlatos.hasNext() && !parar){					
	
				int id = decodificaByte(itPlatos.next());
				
				parar = id==255 || id==254; //El mensaje termina con un -1 en la tag o esCuenta
				esCuenta = id==254;	
		
				if(!parar){ //Si no ha acabado el mensaje
					// extras
					int numBytesExtras =  decodificaByte(itPlatos.next());
					String extrasBinarios = "";				
					for (int i = 0; i < numBytesExtras; i++ )
						extrasBinarios += decToBin(itPlatos.next());
											
					// ingredientes				
					int numBytesIngredientes = decodificaByte(itPlatos.next());
					String ingredientesBinarios = "";
					for(int i=0; i < numBytesIngredientes; i++){
						ingredientesBinarios += decToBin(itPlatos.next());
					}
					
					//A�adimos el plato
					anadirPlatos(abreviaturaRest+id,extrasBinarios,ingredientesBinarios);
				}//if parar             
			 }//while
			
			if(!parar) 
				tagCorrupta = true;
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


	@SuppressLint("SdCardPath")
	public void anadirPlatos(String idNFC, String extrasNFC, String ingredientesNFC){           
       	
		try{
			sqlRestaurante =new HandlerGenerico(getApplicationContext(), "MiBase.db");
			dbRestaurante = sqlRestaurante.open();
		
			//Campos que quiero recuperar de la base de datos y datos que tengo para consultarla
			String[] campos = new String[]{"Nombre","Precio","Extras","Ingredientes"};
	      	String[] datos = new String[]{restaurante, idNFC};
	      	
      		Cursor cursor = dbRestaurante.query("Restaurantes",campos,"Restaurante=? AND Id=?",datos,null,null,null); 
      		cursor.moveToFirst();       
      		dbRestaurante.close();			
	
	      	String extrasSeparadosPorComas = obtenerExtrasSeparadosPorComas(cursor.getString(2));
	        String extrasFinales = compararExtrasQRconBD(extrasSeparadosPorComas, extrasNFC);
	        String ingredientesFinales = compararIngredientesNFCconBD(cursor.getString(3), ingredientesNFC);
	        
	        try{	      	        	
	        	sqlMesas = new HandlerGenerico(getApplicationContext(), "Mesas.db");
	    		dbMesas = sqlMesas.open();
	        	
	   			//Meto el plato en la base de datos Mesas
	       		ContentValues plato = new ContentValues();
	        	int idUnico = PantallaMesasFragment.getIdUnico();
	        	PantallaMesasFragment.getInstanciaClase().setUltimoIdentificadorUnico();
	        	plato.put("NumMesa", numMesa);
	        	plato.put("IdCamarero", idCamarero);
	        	plato.put("IdPlato", idNFC);

	        	if (ingredientesFinales.equals("")) plato.put("Ingredientes", "Con todos los ingredientes");
		        else plato.put("Ingredientes", ingredientesFinales);

	        	if (extrasNFC.equals(""))	plato.put("Extras","Sin guarnici�n");
		        else plato.put("Extras", extrasFinales);
	        	plato.put("FechaHora", formatteDate + " " + formatteHour);
	        	plato.put("Nombre", cursor.getString(0));
	        	plato.put("Precio",cursor.getDouble(1));
	        	plato.put("Personas",numPersonas);
	        	plato.put("IdUnico", idUnico);
	        	plato.put("Sincro", 0);
	        	dbMesas.insert("Mesas", null, plato);  	
	        	dbMesas.close();
		        
		        //FIXME Probar. A�adimos una unidad a las veces que se ha pedido el plato
	        	Mesa.actualizarNumeroVecesPlatoPedido(idNFC);
	        	Mesa.pintarBaseDatosMiFav();
	        	
	      	}catch(Exception e){
	    		System.out.println("Error en base de datos de Mesas en anadirPlatos NFC");
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
	private String compararIngredientesNFCconBD(String ingredientesBD, String ingredientesNFC) {
		StringTokenizer ingredientesST = new StringTokenizer(ingredientesBD, "%");
	    String ingredientes = "";
	    int i = 0;
	    //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
	    while (ingredientesST.hasMoreElements()){ 
	    	 String elem = (String) ingredientesST.nextElement();
	    	 if (ingredientesNFC.charAt(i)=='0')
	    		 ingredientes +=elem + ", sin ";
	    	 i++;    	  
	     }
	     //Le quito la ultima coma al extra final para que quede estetico
	     if (ingredientes!= "")
	    	 ingredientes = "Sin " + ingredientes.substring(0, ingredientes.length()-6).toLowerCase();
	     
	     return ingredientes;
	}


 
	/*Menu que usaremos para activar el NFC y el sbeam*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nfc, menu);
        
        return true;
    }
    
   
	@SuppressLint("InlinedApi")
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