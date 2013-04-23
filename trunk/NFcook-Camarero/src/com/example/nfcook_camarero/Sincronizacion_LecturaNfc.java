package com.example.nfcook_camarero;


import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Iterator;

import fragments.PantallaMesasFragment;
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

public class Sincronizacion_LecturaNfc extends Activity implements DialogInterface.OnDismissListener{

	//Variables usadas para el nfc
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;
	boolean leidoBienEnTag;
	boolean esMFC;
	ProgressDialog	progressDialogSinc;
	
	//Variables usadas para añadir la lista de platos a la base de datos mesas
	HandlerGenerico sqlMesas,sqlrestaurante,sqlEquivalencia;
	String numMesa;
	String idCamarero;
	String numPersonas; 
	String restaurante;
	int numeroRestaurante;
	String abreviatura;
	SQLiteDatabase dbMesas,dbMiBase,dbEquivalencia;
	ArrayList<Byte> mensaje;
	
	//Variables para el sonido
	SonidoManager sonidoManager;
	int sonido;
	//Fecha y hora
	String formatteHour;
    String formatteDate;
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
					//Decodificamos el mensaje leido de la tag y añadimos los platos a la base de datos.
					decodificar(mensaje);
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
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
		writeTagFilters = new IntentFilter[] { tagDetected }; 
		//Fecha y hora 
		//Sacamos la fecha a la que el camarero ha introducido la mesa
    	Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formatteDate = df.format(date);
        //Sacamos la hora a la que el camarero ha introducido la mesa
        Date dt = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        formatteHour = dtf.format(dt.getTime());
        
		//Creamos la instacia del manager de sonido
		sonidoManager = new SonidoManager(getApplicationContext());
		// Pone el volumen al volumen del movil actual
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Cargamos el sonido
        sonido=sonidoManager.load(R.raw.confirm);
        //Obtengo los datos del restaurante su numero y abreviatura
        try{ //Abrimos la base de datos para consultarla
 	       	sqlEquivalencia = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Equivalencia_Restaurantes.db"); 
 	        dbEquivalencia = sqlEquivalencia.open();
 	     
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
 	  	   abreviatura = c.getString(1);
 	  	   
 	  	   System.out.println("NUMERO"+numeroRestaurante+"ABREVIATURA"+abreviatura);
 	  	
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
			Toast.makeText(this, "Pedido no sincronizado. La tag no es Mifare Classic.", Toast.LENGTH_LONG ).show();		
		}
		else {
			if (leidoBienEnTag) {
			 Toast.makeText(this, "Pedido sincronizado correctamente.", Toast.LENGTH_LONG ).show();		
			}
			else {
				Toast.makeText(this, "Pedido no sincronizado.", Toast.LENGTH_LONG ).show();		 
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
		
		/*Variables para borrar la tarjeta */
		String aux = "";
		aux += "255";
		ArrayList<Byte> pedidoCodificadoEnBytes = new ArrayList<Byte>();
		
		ArrayList<Byte> al= new ArrayList<Byte>();
		al.add((byte) numeroRestaurante);
		pedidoCodificadoEnBytes.addAll(al);
		
		al = new ArrayList<Byte>();
		al.add((byte) Integer.parseInt(aux));
		pedidoCodificadoEnBytes.addAll(al);
		
		
		
		
		mensaje = new ArrayList<Byte>();
			
		// Obtiene la instacia de la tarjeta nfc
		MifareClassic mfc = MifareClassic.get(tag);
										
		// Establece la conexion
		mfc.connect();
		
		boolean sectorValido = false;		

		//----------Borrado de la tarjeta
		// para recorrer string de MifareClassic.BLOCK_SIZE en MifareClassic.BLOCK_SIZE
		int recorrerString = 0;	
		
		// relleno con 0's el pedido hasta que sea modulo16 para que luego no haya problemas ya que 
		// se escribe mandando bloques de 16 bytes
		int  numMod16 = pedidoCodificadoEnBytes.size() % 16;
		if (numMod16 != 0){
			int huecos = 16-numMod16;
			for (int i = 0; i < huecos; i++)
				pedidoCodificadoEnBytes.add((byte) 0);
		}
		
		//Variable usada para saber por el bloque que vamos
		int numBloque = 0;
		// el texto que ha escrito el usuario
		byte[] textoByte = null;
		String texto="";// Variable usada para concatenar el mensaje leido
				
		// Recorremos todos los sectores y bloques leyendo el mensaje
		   
		while (numBloque < mfc.getBlockCount()) {
			if (sePuedeLeerEnBloque(numBloque)) {
			
				// Cada sector tiene 4 bloques
				int numSector = numBloque / 4;
				//Validamos el sector con la A porque las tarjetas que tenemos usan el bit A en vez del B
				
				sectorValido = mfc.authenticateSectorWithKeyA(numSector,MifareClassic.KEY_DEFAULT);
	
				if (sectorValido) {//Si es un sector valido
				
						textoByte=mfc.readBlock(numBloque); //leemos un bloque entero
						
						for (int i=0; i<MifareClassic.BLOCK_SIZE; i++)
							{texto=texto+(char)textoByte[i];//Concatenamos el contenido del bloque en el string ya que de la tarjeta lo leemos en bytes
							 		  mensaje.add(textoByte[i]);
							}
						
						// si es menor significa que queda por escribir cosas
						if (recorrerString < pedidoCodificadoEnBytes.size()) { //textoBytes.length 
							// recorremos con un for para obtener bloques de 16 bytes
							byte[] datosAlBloque = new byte[MifareClassic.BLOCK_SIZE];
							for (int j=0; j<MifareClassic.BLOCK_SIZE; j++)
								datosAlBloque[j] = pedidoCodificadoEnBytes.get(j+recorrerString);
							// avanzo para el siguiente bloque
							recorrerString += MifareClassic.BLOCK_SIZE;
							// escribimos en el bloque
							mfc.writeBlock(numBloque, datosAlBloque);
						} else {
							// escribimos ceros en el resto de la tarjeta porque ya no queda nada por escribir
							byte[] ceros = new byte[MifareClassic.BLOCK_SIZE];
							mfc.writeBlock(numBloque, ceros);
						}
			} 	
				numBloque++;
			}
			else {
				numBloque++;
				 }
				
			
		}
		leidoBienEnTag = true; 				 
		// Cerramos la conexion
		mfc.close();
		
		
	}


	/**
	 * Metedo encargado de comprobar si se puede o no escribir en un bloque pasado por parametro
	 * @param numBloque
	 * @return
	 */
	private boolean sePuedeLeerEnBloque(int numBloque) {
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
					// ejecuta el progressDialog, codifica, escribe en tag e intercambia datos de pedido a cuenta en segundo plano
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
	        Toast.makeText(getApplicationContext(), "Por favor activa NFC.", Toast.LENGTH_LONG).show();
	
	}
	
	

	/**Decodificamos el contenido leido de la tag separando cada plato y añadiendolo a las mesas.
	 * */
	private void decodificar(ArrayList<Byte> mensaje) {
		
	//Recorremos todo el mensaje leido y vamos descomponiendo todos los platos en id-extras-comentario
		Iterator<Byte> itPlatos = mensaje.iterator();
		Iterator<Byte> iteradorAux = mensaje.iterator();
		
		boolean correcto=false;
		boolean parar=false;
		int numRestaurante;
		int n;
		while(iteradorAux.hasNext() && !correcto){
			   n = decodificaByte(iteradorAux.next());
			    correcto= n==255;
			  }
			  
			
		
		
	if (correcto){
		numRestaurante = decodificaByte(itPlatos.next());
		if(numeroRestaurante==numRestaurante )
		{
		while(itPlatos.hasNext() && !parar){
			
			// id
			int id = decodificaByte(itPlatos.next());
			//El mensaje termina con un -1 en la tag
			parar= id==255;
			
			if(!parar){//Si no ha acabado el mensaje
				// extras
				int numExtras =  decodificaByte(itPlatos.next());
				String extras = "";
				
				for (int i = 0; i < numExtras; i++ )
					extras += decToBin(itPlatos.next());
							
				// comentario
				int numComentario =  decodificaByte(itPlatos.next());
				String comentario = "";
				
				for (int i = 0; i < numComentario; i++)
					comentario += (char)decodificaByte(itPlatos.next());
				//Añadimos el plato
				añadirPlatos(restaurante,abreviatura+id,extras,comentario);
			}//if parar		
		 }//while
		}//if
	
		else{
			Toast.makeText(getApplicationContext(), "Los platos sincronizados no corresponden a este restaurante.", Toast.LENGTH_LONG).show();
		}
		}
		else {Toast.makeText(getApplicationContext(), "La tarjeta no contiene datos correctos.", Toast.LENGTH_LONG).show();
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


	public void añadirPlatos(String restaurante,String id,String extras,String observaciones)
	{
          
    	
            Cursor cursor = null;
            String extrasFinal="";
            
          try{
    			sqlrestaurante =new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
    			dbMiBase = sqlrestaurante.open();
    			
    			
        		//Campos que quiero recuperar de la base de datos
    			String[] campos = new String[]{"Nombre","Precio","Extras"};
    			//Datos que tengo para consultarla
          		String[] datos = new String[]{restaurante,id};
          		
          		cursor = dbMiBase.query("Restaurantes",campos,"Restaurante=? AND Id=?",datos,null,null,null); 
          		cursor.moveToFirst();       
                dbMiBase.close();
                // Voy a comprobar los extras que se han escogido comparando el codigo binario que leemos de la tarjeta y los extras de la base de datos.
                //Obtengo los extras de la base de datos
                String extrasBaseDatos= cursor.getString(2);
                //Separo los distintos tipos de extras
                StringTokenizer auxExtras= new StringTokenizer(extrasBaseDatos,"/");
                StringTokenizer auxExtras2 = null;
                String elemento = "";
                
                int numExtras=0;
                //Recorrro cada uno de los elementos que se me han generado en el sring tokenizer que son de la forma Guarnicion:PatatasAsada,Ensalada
                while (auxExtras.hasMoreElements())
                	{auxExtras2= new StringTokenizer((String) auxExtras.nextElement(),":");
                	 //Elimino el Guarnicion:/Salsa:/Guarnicion:
                	 auxExtras2.nextElement();
                     extrasFinal +=auxExtras2.nextElement()+",";
                	}
                //Extras final tiene todos los extras de ese plato separados por comas
                auxExtras= new StringTokenizer(extrasFinal,",");
                extrasFinal="";
                //Recorro los extras y compruebo con el codigo binario cual de los extras ha sido escogio(un 1)
                while (auxExtras.hasMoreElements())
            	  { elemento= (String) auxExtras.nextElement();
            	    if (extras.charAt(numExtras)=='1')
            		   extrasFinal +=elemento+", ";
            	    numExtras++;    	  
            	  }
                //Le quito la ultima coma al extra final para que quede estetico
                if (extrasFinal!= "")
                	extrasFinal=extrasFinal.substring(0,extrasFinal.length()-2);
    		}catch(SQLiteException e){
    		 	System.out.println("Error lectura base de datos de MIBASE");
    		}
    		
          
      		
      		try{
       			//Abro base de datos para introducir los platos en esa mesa
       			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
       			dbMesas= sqlMesas.open();
       			//Meto el plato en la base de datos Mesas
	       		ContentValues plato = new ContentValues();
	        	int idUnico = PantallaMesasFragment.getIdUnico();
	        	plato.put("NumMesa", numMesa);
	        	plato.put("IdCamarero", idCamarero);
	        	plato.put("IdPlato", id);
	        	plato.put("Observaciones", observaciones);
	        	plato.put("Extras",extrasFinal);
	        	plato.put("FechaHora", formatteDate + " " + formatteHour);
	        	plato.put("Nombre", cursor.getString(0));
	        	plato.put("Precio",cursor.getDouble(1));
	        	plato.put("Personas",numPersonas);
	        	plato.put("IdUnico", idUnico);
	        	dbMesas.insert("Mesas", null, plato);
	        	dbMesas.close();
	        	System.out.println("Añadido");
        	
      		}catch(Exception e){
    			//System.out.println("Error lectura base de datos de Mesas");
    		}
   
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
            case R.id.menu_sbeam:
                 intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
              
                startActivity(intent);
                return true;
           
            default:
                return super.onOptionsItemSelected(item);
        }
        }



	

	
    
  
}