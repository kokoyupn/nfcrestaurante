package com.example.escrituratagnfc;

import android.os.Bundle;
import android.app.Activity;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint({ "ParserError", "ParserError" })
public class MainActivity extends Activity{

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	Tag mytag;
	Context ctx;
	String[][] mTechLists;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ctx=this; 
		Button btnWrite = (Button) findViewById(R.id.button);
		final TextView message = (TextView)findViewById(R.id.edit_message);

		btnWrite.setOnClickListener(new View.OnClickListener(){
			
			@SuppressLint("NewApi") 
			public void onClick(View v) {
				try {
					if(mytag==null){
						Toast.makeText(ctx, ctx.getString(R.string.tag_no_detectada), Toast.LENGTH_LONG ).show();
					}else{
						write(message.getText().toString(),mytag);
						Toast.makeText(ctx, ctx.getString(R.string.escritura_ok), Toast.LENGTH_LONG ).show();
					}
				} catch (IOException e) {
					Toast.makeText(ctx, ctx.getString(R.string.error_escritura), Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				} catch (FormatException e) {
					Toast.makeText(ctx, ctx.getString(R.string.error_escritura) , Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				}
			}
		});

		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
		writeTagFilters = new IntentFilter[] { tagDetected }; 
		
	}

	

	@SuppressLint("NewApi")
	/**
	 * Metodo encargado de escribir en el tag. Escribira en el tag el texto introducido por 
	 * el usuario. Los bloques que queden sin escribir seran reescritos con 0's eliminando
	 * el texto que hubiese anteriormente
	 * @param text
	 * @param tag
	 * @throws IOException
	 * @throws FormatException
	 */
	private void write(String text, Tag tag) throws IOException, FormatException {	
		
		// Obtenemos instancia de MifareClassic para el tag.
		MifareClassic mfc = MifareClassic.get(tag);
										
		// Habilitamos operaciones de I/O
		mfc.connect();

		boolean sectorValido = false;				
		// para avanzar los bloques (en el 0 no se puede escribir)
		int numBloque = 0;
		// el texto que ha escrito el usuario
		byte[] textoBytes  = text.getBytes(); 
		// para recorrer string de MifareClassic.BLOCK_SIZE en MifareClassic.BLOCK_SIZE
		int recorrerString = 0;
		// para recorrer string de 1 en 1
		int aux = 0;	
				
		// voy a recorrer todos los sectores y bloques escribiendo el mensaje
		// Los demas donde no haya que escribir se se rellenaran con 0's
		while (numBloque < mfc.getBlockCount()) {
			
			if (sePuedeEscribirEnBloque(numBloque)) {
			
				// cada sector tiene 4 bloques
				int numSector = numBloque / 4;
						
				// autentifico con la A para escritura
				sectorValido = mfc.authenticateSectorWithKeyA(numSector,MifareClassic.KEY_DEFAULT);
				if (sectorValido) {
							
					// si es menor significa que queda por escribir cosas
					if (recorrerString < textoBytes.length ) {
								
						byte[] auxString = new byte[MifareClassic.BLOCK_SIZE];
							
						for (int i=0; i<MifareClassic.BLOCK_SIZE; i++) {
							// si se cumple el bloque tendra texto
							if (aux < textoBytes.length) {
								auxString[i] = textoBytes[i+recorrerString];
								aux++;
							}
							// nos hemos quedado sin texto y metemos 0's
							else auxString[i] = 0;
						}           
						recorrerString += MifareClassic.BLOCK_SIZE;
								
						// escribimos en el bloque
						mfc.writeBlock(numBloque, auxString);
						
					} else {
						// escribimos ceros porque ya no queda nada por escribir
						byte[] ceros = new byte[MifareClassic.BLOCK_SIZE];
						mfc.writeBlock(numBloque, ceros);
					}
				
				}
				numBloque++;
			} 
			else {  /**TODO  hacer los avances siempre fuera de los ifs */
				numBloque++;
			}
				      
			
		}
		
		// Cerramos la conexion
		mfc.close();
	}


	/** 
	 * Comprueba si se puede escribir o no en los bloques de las tag
	 * @param numBloque
	 * @return
	 */
	private boolean sePuedeEscribirEnBloque(int numBloque) {
		return (numBloque+1) % 4 != 0 && numBloque != 0 ; 
	}
	
	

	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			Toast.makeText(this, this.getString(R.string.tag_detectada) + mytag.toString(), Toast.LENGTH_LONG ).show();
		}
	}
	
	@SuppressLint("NewApi")
	@Override
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
	
	
	/**TODO 
	 * Falta acondicionar el codigo de codificacion de los platos con el de escritura por nfc  
	 * import java.util.ArrayList;
import java.util.StringTokenizer;


public class codificar {

	public static void main(String[] args) {

		String listaPlatos = "1@2@3@4+10010@5*Con semen@1+01001*Con semen@2+10010*Sin macarrones@";		
		codificarPlatos(listaPlatos);
	}
	
	private static ArrayList<Byte> codificarPlatos(String listaPlatos){
		
		ArrayList <Byte> codificado = new ArrayList <Byte>();
		
		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(listaPlatos,"@");
		
		while(stPlatos.hasMoreElements()){
			
			String plato = stPlatos.nextToken();
			StringTokenizer stTodoSeparado =  new StringTokenizer(plato,"+,*");
					
			// id
			String id =  stTodoSeparado.nextToken();
			codificado.addAll(codificaIdPlato(id));
					
			// extras
			if (plato.contains("+"))  {
				String extras =  stTodoSeparado.nextToken();
				ArrayList <Byte> alExtras = codificaExtras(extras);
				// tamaño de los extras que habra que leer
				codificado.add((byte) alExtras.size());
				codificado.addAll(alExtras);	
			} else codificado.add((byte) 0);
					
			// comentarios
			if (plato.contains("*"))  {
				String comentario =  stTodoSeparado.nextToken();
				ArrayList <Byte> alComentario = codificaComentario(comentario);
				// tamaño de comentarios que habra que leer
				codificado.add((byte) alComentario.size());
				codificado.addAll(alComentario);
			} else codificado.add((byte) 0);
					
		}
		System.out.println(codificado);
		return codificado;	
	
	}
	

	private static ArrayList<Byte> codificaComentario(String comentario) {
		
		ArrayList<Byte> al = new ArrayList<Byte>();
		
		for (int i = 0; i<comentario.length(); i++)
			al.add((byte) comentario.charAt(i));
		
		return al;
	 }
	 
	private static ArrayList<Byte> codificaIdPlato(String id) {
		
		ArrayList<Byte> al = new ArrayList<Byte>();
		al.add((byte) Integer.parseInt(id));
		return al;
	}

	private static ArrayList<Byte> codificaExtras(String extras) {
		
		ArrayList<Byte> al = new ArrayList<Byte>();
		
		int relleno = 0;
		int  numMod8 = extras.length()% 8;
		if (numMod8 != 0){
			relleno = 8-numMod8;
			for (int p=0; p<relleno;p++)
				extras = extras + "0";
		}
	
		int veces = extras.length()/8;
		int num;
		int posicion=0;
		for (int i=0 ; i<veces; i++){  
			num = binToDec(extras.substring(posicion,posicion+8));
			posicion += 8;
			al.add((byte) ((char)num));	
		}
	
		return al;	 
	}
	
	private static int binToDec(String pNumBin) {        
	        
		int resultado = 0 ;        
	        
	    for( int i = 0; i < pNumBin.length() ; i++ ) {
	           
	    	char digito = pNumBin.charAt( i ); 
	        // en general, resultado = resultado * base + digito
	            
	        try {         
	        	int valDigito = Integer.parseInt( Character.toString(digito) ) ;
	            resultado = resultado * 2 + valDigito ;    
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }    
	    }
	    return resultado ; 
	        
	}
  
}

	
	 */
}