package com.example.nfcook_camarero;


import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Iterator;

import adapters.ContenidoListMesa;
import adapters.MiListAdapterMesa;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LecturaNfc extends Activity{

	//Variables usadas para el nfc
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;
	
	//Variables usadas para añadir la lista de platos a la base de datos mesas
	HandlerGenerico sqlMesas,sqlrestaurante;
	String numMesa;
	String idCamarero;
	String numPersonas; 
	SQLiteDatabase dbMesas,dbMiBase;
	ArrayList<Byte> mensaje;
	/**Creamos la actividad, al pulsar el boton de lectura se comprueba si el dispositivo ha detectado la tag si es asi comienza la lectura
	 * una vez que leemos de la tarjeta lo que hacemos es añadirlo a la base de datos Mesas. 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lectura_nfc);

		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		
		ctx=this;
		
		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
		writeTagFilters = new IntentFilter[] { tagDetected }; 
				
		
	}

	//@SuppressLint("NewApi")
	/**
	 * Metodo que se encarga de leer bloques de la tarjeta nfc
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void read(Tag tag) throws IOException, FormatException {	
		

		mensaje = new ArrayList<Byte>();
			
		// Obtiene la instacia de la tarjeta nfc
		MifareClassic mfc = MifareClassic.get(tag);
										
		// Establece la conexion
		mfc.connect();
		
		boolean sectorValido = false;		
		
		
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
			} 	
				numBloque++;
			}
			else {
				numBloque++;
				 }
				
			
		}
		final TextView message = (TextView)findViewById(R.id.textView1);
		message.setText(texto);//Mostramos pantalla el mensaje
				 
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
			try {   
				read(mytag);//Se ha detectado la tag procedemos a leerla
				//Decodificamos el mensaje leido de la tag y añadimos los platos a la base de datos.
				decodificar(mensaje);
				Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_LONG ).show();
					
				}
			 catch (IOException e) {//Error en la lectura has alejado el dispositivo de la tag
				Toast.makeText(ctx, ctx.getString(R.string.error_reading), Toast.LENGTH_LONG ).show();
				e.printStackTrace();
			} catch (FormatException e) {
				Toast.makeText(ctx, ctx.getString(R.string.error_reading) , Toast.LENGTH_LONG ).show();
				e.printStackTrace();
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
		
	}
	
	

	/**Decodificamos el contenido leido de la tag separando cada plato y añadiendolo a las mesas.
	 * */
	private void decodificar(ArrayList<Byte> mensaje) {
		
	//Recorremos todo el mensaje leido y vamos descomponiendo todos los platos en id-extras-comentario
		Iterator<Byte> itPlatos = mensaje.iterator();
		
		boolean parar=false;
		
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
				añadirPlatos("Foster","fh"+id,extras,comentario);
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


	public void añadirPlatos(String restaurante,String id,String extras,String observaciones)
	{
          
    		//Sacamos la fecha a la que el camarero ha introducido la mesa
        	Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formatteDate = df.format(date);
            //Sacamos la hora a la que el camarero ha introducido la mesa
            Date dt = new Date();
            SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
            String formatteHour = dtf.format(dt.getTime());
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
            		   extrasFinal +=elemento+",";
            	    numExtras++;    	  
            	  }
                //Le quito la ultima coma al extra final para que quede estetico
                extrasFinal=extrasFinal.substring(0,extrasFinal.length()-1);
    		}catch(SQLiteException e){
    		 	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS MiBase(Restaurante)",Toast.LENGTH_SHORT).show();
    		}
    		
          
      		
      		try{
       			//Abro base de datos para introducir los platos en esa mesa
       			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
       			dbMesas= sqlMesas.open();
       			//Meto el plato en la base de datos Mesas
	       		ContentValues plato = new ContentValues();
	        	int idUnico = InicialCamarero.getIdUnico();
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
    			System.out.println("Error lectura base de datos de Mesas");
    		}
   
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