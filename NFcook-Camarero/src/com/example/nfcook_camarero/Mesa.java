package com.example.nfcook_camarero;


import java.io.File;
import java.util.ArrayList;



import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Mesa extends Activity {
	private int numMesa=3;//Asignar desde fuera
	private SQLiteDatabase dbc,dbHistorico;
	private TableLayout platos;
	private Context  contexto;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descripcionmesa);
		contexto=getApplicationContext();
		
		TextView mesa = (TextView)findViewById(R.id.textView1);
		mesa.setText("Numero de Mesa: "+ String.valueOf(numMesa) );
		
		
		try{
			//CAMBIAR DIRECCION porque  no funciona si no:
			//Casa: 
			String historico="/data/data/com.example.nfcook/Historico.db";
			//Uni: 
			String pedido="/data/data/com.example.nfcook/Pedido.db";
			dbc= SQLiteDatabase.openDatabase(pedido, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			dbHistorico= SQLiteDatabase.openDatabase(historico, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		}catch(Exception e){
			System.out.println("CATCH");
			Log.i(null, "MIE, nullRDA");
		}
	//	if(dbc!=null){
		//	dbc.execSQL("CREATE TABLE Pedido(NumeroMesa TEXT,Nombre TEXT,Observaciones TEXT,Extras TEXT,Precio INTEGER)");
			//dbHistorico.execSQL("CREATE TABLE Historico(NumeroMesa TEXT,Nombre TEXT,Observaciones TEXT,Extras TEXT,Precio INTEGER)");
			
	//	}
	//	if(dbHistorico!=null){
		//	dbHistorico.execSQL("CREATE TABLE Historico(NumeroMesa TEXT,Nombre TEXT,Observaciones TEXT,Extras TEXT,Precio INTEGER)");
			
	//	}
		
		
		//Meter registros base de datos
  	    ContentValues a = new ContentValues();
	   	a.put("NumeroMesa", 5);
	   	a.put("Nombre", "Cheese Fries");
	   	a.put("Observaciones", "Observaciones");
	   	a.put("Extras", "Extras");
	   	a.put("Precio", 8.35);
	    dbc.insert("Pedido", null, a);
	    
	    ContentValues b = new ContentValues();
	   	b.put("NumeroMesa", 3);
	   	b.put("Nombre", "AlitasAlitasAlitas");
	   	b.put("Observaciones", "obs");
	   	b.put("Extras", "ext");
	   	b.put("Precio", 45);
	    dbc.insert("Pedido", null, b);
	    
	    ContentValues ce = new ContentValues();
	   	ce.put("NumeroMesa", 2);
	   	ce.put("Nombre", "combo");
	   	ce.put("Observaciones", "Obsss");
	   	ce.put("Extras", "Extrs");
	   	ce.put("Precio", 888);
	    dbc.insert("Pedido", null, ce);
	    dbc.insert("Pedido", null, ce);
	   //dbc.insert("Pedido", null, ce);
	   // dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    //dbc.insert("Pedido", null, ce);
	    
	    
	    
  	    //----------------------------------
	    
	    String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio"};
  	  	String[] nmesa = new String[]{"numMesa"};
  		Cursor c = dbc.query("Pedido", campos, null,null, null,null, null);
  	  /*  
  	    platos = (TableLayout)findViewById(R.id.tableLayout1);
  	    
  	    //Cabeceras--------------------------------------------
  	    LinearLayout cabecera = (LinearLayout)findViewById(R.id.cabeceras);
  	    
  	    
  	    TextView ediCab = new TextView(getApplicationContext());
  	    ediCab.setText("CB");
  		ediCab.setTextSize(12);
  		ediCab.setTypeface(Typeface.DEFAULT_BOLD);
  		ediCab.setTextColor(Color.BLACK);
  		ediCab.setWidth(60);
  		cabecera.addView(ediCab);
  	   
  	    TextView nCab = new TextView(getApplicationContext());
		nCab.setText("Nombre");
		nCab.setTextSize(12);
		nCab.setTypeface(Typeface.DEFAULT_BOLD);
		nCab.setTextColor(Color.BLACK);
		nCab.setWidth(100);
		cabecera.addView(nCab);
		
		TextView oCab = new TextView(getApplicationContext());
		oCab.setText("Observaciones");
		oCab.setTextSize(12);
		oCab.setTypeface(Typeface.DEFAULT_BOLD);
		oCab.setWidth(100);
		oCab.setTextColor(Color.BLACK);
		cabecera.addView(oCab);
		
		TextView eCab = new TextView(getApplicationContext());
		eCab.setText("Extras");
		eCab.setTextSize(12);
		eCab.setTypeface(Typeface.DEFAULT_BOLD);
		eCab.setWidth(90);
		eCab.setTextColor(Color.BLACK);
		cabecera.addView(eCab);
		
		TextView pCab = new TextView(getApplicationContext());
		pCab.setText("Precio");
		pCab.setTypeface(Typeface.DEFAULT_BOLD);
		pCab.setTextSize(12);
		pCab.setWidth(50);
		//pCab.setWidth(LayoutParams.WRAP_CONTENT);
		pCab.setTextColor(Color.BLACK);
		cabecera.addView(pCab);
		//Cabeceras------------------------------------------
		
		
  	  	float precioAcumulado=0;
		
		while(c.moveToNext()){
			TableRow plato = new TableRow(getApplicationContext());
			
			CheckBox cb= new CheckBox(getApplicationContext());
			cb.setWidth(LayoutParams.WRAP_CONTENT);
			plato.addView(cb);
			
			TextView nombre = new TextView(getApplicationContext());
			//Los indices son en funcion de los campos de cursor, no de la base de datos
			nombre.setText(c.getString(0));//La ultima fila siempre la saca cortada y no se por que
			nombre.setTextSize(10);
			nombre.setPadding(0, 0, 0, 5);
			nombre.setTextColor(Color.BLACK);
			nombre.setWidth(100);
			//nombre.setHeight(LayoutParams.MATCH_PARENT);
			plato.addView(nombre);
        
			TextView observaciones = new TextView(getApplicationContext());
			observaciones.setText(c.getString(1));
			observaciones.setTextSize(10);
			observaciones.setPadding(1, 1, 1, 1);
			observaciones.setTextColor(Color.BLACK);
			observaciones.setWidth(100);
			//observaciones.setHeight(LayoutParams.MATCH_PARENT);//Si lo pones no escribe nada dentro
			plato.addView(observaciones);
			
			TextView extras = new TextView(getApplicationContext());
			extras.setText(c.getString(2));
			extras.setPadding(1, 1, 1, 1);
			extras.setTextSize(10);
			extras.setTextColor(Color.BLACK);
			extras.setWidth(90);
			//extras.setHeight(LayoutParams.MATCH_PARENT);
			plato.addView(extras);
			
			TextView precio = new TextView(getApplicationContext());
			precio.setText(c.getString(3));
			precio.setPadding(1, 1, 1, 1);
			precio.setTextSize(10);
			precio.setTextColor(Color.BLACK);
			//precio.setHeight(LayoutParams.MATCH_PARENT);
			precio.setWidth(30);
			plato.addView(precio);
			precioAcumulado=precioAcumulado+(c.getFloat(3));
			
			LayoutInflater inflater= getLayoutInflater();
			Button editar = (Button)inflater.inflate(R.layout.editarboton,plato,false);
			editar.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	//Intent intent = new Intent(contexto,DescripcionPlato.class);
	    			//startActivity(intent);
	            }
	        });
			editar.setTextSize(12);
			//El tamaño del boton se ajusta lo que ocupe la fila de alto, 
			//por si ocupa mas de una linea el nombre,descripcion u 
			//observaciones del plato
			plato.addView(editar);
			
			platos.addView(plato);
			
		}
		
		TableRow prec = new TableRow(getApplicationContext());
		
		TextView va = new TextView(getApplicationContext());
		va.setText("");
		prec.addView(va);//Tres vacias para poner en las de la derecha precio y el precio
		TextView vac = new TextView(getApplicationContext());
		vac.setText("");
		prec.addView(vac);
		TextView vacc = new TextView(getApplicationContext());
		vacc.setText("");
		prec.addView(vacc);
		
		
		TextView precioTotal = new TextView(getApplicationContext());
		precioTotal.setText("Precio: ");
		prec.addView(precioTotal);
		
		TextView valorPrecio = new TextView(getApplicationContext());
		valorPrecio.setText(Float.toString(precioAcumulado));
		prec.addView(valorPrecio);
		
		platos.addView(prec);
		
		
		
		Button cobrar = (Button)findViewById(R.id.button1);
		cobrar.setText("Cobrar");
		cobrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		
            		dbHistorico.openDatabase("/data/data/com.example.camarero/Historico.db", null,SQLiteDatabase.OPEN_READWRITE);
            		
            		String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio"};
              	  	Cursor c = dbc.query("Pedido", campos, null,null, null,null, null);
            		Log.d("Cursor tiene:",String.valueOf(c.getCount()));
              		
            		while(c.moveToNext()){
            			
	            		//Añades los platos a la base de datos del historico
            			ContentValues nuevo = new ContentValues();
            			nuevo.put("NumeroMesa",numMesa);
            			nuevo.put("Nombre",c.getString(0));
	            		nuevo.put("Observaciones",c.getString(1) );
	            		nuevo.put("Extras", c.getString(2));
	            		nuevo.put("Precio",c.getString(3));
	            		dbHistorico.insert("Historico", null, nuevo);
		            	
	            		//Eliminas los platos de la lista
	            		platos.removeViewAt(1);            			
	                	Log.d("Quedan:", String.valueOf(platos.getChildCount()));
	            	    
	            	    
	            	   
	            	}
            		
            		platos.removeViewAt(1);//Para eliminar la fila del precio
            		
            		//Eliminas platos de la base de datos de entrada
            		dbc.delete("Pedido","1", null); 
            		            		
            	}catch(Exception e){
            		Log.d("Entra:","Catch de boton cobrar");
            		
            	}
            }
        });
		
		
		Button addPlato = (Button)findViewById(R.id.button2);
		addPlato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		dbc.openDatabase("/data/data/com.example.camarero/Pedido.db", null,SQLiteDatabase.OPEN_READWRITE);
            		
            		
            		}catch(Exception e){
            		Log.d("Entra:","Catch de boton addPlato");
            		
            	}
            }
        });
		
		
		Button addBebida = (Button)findViewById(R.id.button3);
		addBebida.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		dbc.openDatabase("/data/data/com.example.camarero/Pedido.db", null,SQLiteDatabase.OPEN_READWRITE);
            		
            		
            		}catch(Exception e){
            		Log.d("Entra:","Catch de boton addBebida");
            		
            	}
            }
        });
		
		
		Button eliminar = (Button)findViewById(R.id.button4);
		eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		int totalPlatos=platos.getChildCount();
            		int i=0;
            		float precio=0;

            		Log.d("Al ppio hay",String.valueOf(totalPlatos));
            		while(i<totalPlatos-2){
            			
            			//La primera fila de la tabla son las cabeceras y no hay checkbox
                   		TableRow fila = (TableRow)platos.getChildAt(i+1);
                   		CheckBox cbox= (CheckBox) fila.getChildAt(0);
                   		if ( cbox.isChecked() ){
                   			//Lo quitas de la lista
                   			Log.d("antes del","removeview");                   			
                   			platos.removeViewAt(i+1);
                   			totalPlatos--;
                   			
                   		}else{
                        	Log.d("Entra en el","ELSE");
                        	TextView precFila= (TextView) fila.getChildAt(4);
                        	precio = precio + Float.parseFloat(precFila.getText().toString());
                        	i++;
                        	}
                   		
                   		TableRow f = (TableRow)platos.getChildAt(platos.getChildCount()-1);
                   		TextView p= (TextView) f.getChildAt(4);
                   		p.setText(String.valueOf(precio));
                   		
                   		
                   		//Actualizar la base de datos de Pedido
                   		dbc.delete("Pedido","1", null);//La borras porque la vas a rellenar de nuevo con lo que queda(o no si esta vacio)
                   		if(totalPlatos<=2)//Eliminas la fila del precio si solo quedan la fila de la cabecera y la del precio
               				platos.removeViewAt(1);
               			
                   		else{//Si queda algun plato, actualizas la base de datos de Pedido
                   			for(int j=1;j<platos.getChildCount()-1;j++){
                   				TableRow reañadir = (TableRow)platos.getChildAt(j);
                   				
                       			String nombre = ((TextView) reañadir.getChildAt(1)).getText().toString();
                       			String obs = ((TextView) reañadir.getChildAt(2)).getText().toString();
                       			String ext = ((TextView) reañadir.getChildAt(3)).getText().toString();
                       			String pre = ((TextView) reañadir.getChildAt(4)).getText().toString();
                       			
                       			ContentValues nuev = new ContentValues();
                       			nuev.put("NumeroMesa", numMesa);
                       			nuev.put("Nombre", nombre);
                       			nuev.put("Observaciones", obs);
                       			nuev.put("Extras", ext);
                       			nuev.put("Precio", pre);
                       		    dbc.insert("Pedido", null, nuev);
                   			}
                   		}
                   	}
            	}catch(Exception e){
            		Log.d("Entra:","Catch de boton eliminar");
            		
            	}
            }
        });
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
