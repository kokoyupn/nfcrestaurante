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

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;
	/*Variables usadas para añadir la lista de platos a la base de datos mesas
	 * */
	HandlerGenerico sqlMesas,sqlrestaurante;
	String numMesa;
	String idCamarero;
	String numPersonas; 
	SQLiteDatabase dbMesas,dbMiBase;
	ArrayList<Byte> mensaje;
	/**Creamos la actividad, al pulsar el boton de lectura se comprueba si el dispositivo ha detectado la tag si es asi comienza la lectura
	 * 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lectura_nfc);

		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		//añadirPlatos();
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
	 * Metodo que se encarga de leer bloques de la tag y lo introduce en un textview que mostraremos por pantalla
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
				 
		
	//	System.out.println(mensaje);
		
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
	 * 
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			Toast.makeText(this, this.getString(R.string.ok_detection), Toast.LENGTH_LONG ).show();
			try {   
				read(mytag);//Se ha detectado la tag procedemos a leerla
				
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
