package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Mesa extends Activity {
	private int numMesa;
	private SQLiteDatabase dbPedido,dbHistorico;
	private TableLayout platos;
	
	//CAMBIAR DIRECCION porque no ES IGUAL EN TODOS LOS PCs:
	String rutaPedido="/data/data/com.example.camarero/Mesas.db";
	String rutaHistorico="/data/data/com.example.camarero/Historico.db";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ventanamesa);
		
		TextView mesa = (TextView)findViewById(R.id.numeroDeMesa);
		mesa.setText("Numero de Mesa: "+ String.valueOf(numMesa) );
		
		
		try{
			dbPedido= SQLiteDatabase.openDatabase(rutaPedido, null, SQLiteDatabase.OPEN_READWRITE);
			dbHistorico= SQLiteDatabase.openDatabase(rutaHistorico, null, SQLiteDatabase.OPEN_READWRITE);
		
		    String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio"};
	  	  	Cursor c = dbPedido.query("Mesas",campos, null,null, null,null, null);
	  		
	  	    
	  	  	//Cabeceras--------------------------------------------------------------------
	  	    LinearLayout cabecera = (LinearLayout)findViewById(R.id.cabeceras);
	  	    
	  	    //Primer texto sin nombre de cabecera porque es el checkbox
	  	    TextView ediCab = new TextView(getApplicationContext());
		    ediCab.setTextSize(12);
			ediCab.setTypeface(Typeface.DEFAULT_BOLD);
			ediCab.setTextColor(Color.BLACK);
			ediCab.setWidth(61);
			cabecera.addView(ediCab);
		    
		    for(int i=0;i<c.getColumnCount();i++){
	  	    	TextView tituloCabecera = new TextView(getApplicationContext());
	  	    	tituloCabecera.setText(c.getColumnName(i));
	  	    	tituloCabecera.setTextSize(12);
	  	    	tituloCabecera.setTypeface(Typeface.DEFAULT_BOLD);
	  	    	tituloCabecera.setTextColor(Color.BLACK);
	  	    	tituloCabecera.setWidth(90);
	  	  		cabecera.addView(tituloCabecera);
	  	     }
		     //Cabeceras-------------------------------------------------------------------
		    
			
			 //Añadir platos a la tabla----------------------------------------------------
	  	  	 platos = (TableLayout)findViewById(R.id.tableLayoutPlatos);
	  	  	
			 while(c.moveToNext()){
				 
				LayoutInflater inflaterFila= getLayoutInflater();
				TableRow plato = (TableRow)inflaterFila.inflate(R.layout.filaventanamesa,platos,false);
				
				CheckBox cb= new CheckBox(getApplicationContext());
				cb.setGravity(Gravity.CENTER_VERTICAL);
				plato.addView(cb);
				
				for(int i=0;i<c.getColumnCount();i++){
					TextView contenido = new TextView(getApplicationContext());
					contenido.setText(c.getString(i));
					contenido.setTextSize(10);
					contenido.setPadding(0, 0, 0, 5);
					contenido.setTextColor(Color.BLACK);
					contenido.setWidth(90);
					plato.addView(contenido);
					
				}
				
				LayoutInflater inflater= getLayoutInflater();
				Button editar = (Button)inflater.inflate(R.layout.editarboton,plato,false);
				editar.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	/*Intent intent = new Intent(contexto,DescripcionPlato.class);
		    			startActivity(intent);*/
		            }
		        });
				editar.setTextSize(12);
				//El tamaño del boton se ajusta lo que ocupe la fila de alto, 
				//por si ocupa mas de una linea el nombre,descripcion u 
				//observaciones del plato
				plato.addView(editar);
				
				platos.addView(plato);
				
			}
			//Añadir platos a la tabla----------------------------------------------------
			 
			 
			//Calcular el precio total de la cuenta y se pone en un textView--------------
			if(c.getCount()>0){//Solo si hay algun plato en la base de datos de pedido
				try{
					String[] columnaPrecio = new String[]{"Precio"};
		  	  		Cursor cursorPrecios = dbPedido.query("Mesas", columnaPrecio, null,null, null,null, null);
		  	  		
					TableRow prec = new TableRow(getApplicationContext());
					
					float precioAcumulado=0;
					
					while(cursorPrecios.moveToNext()){
						Log.d("Actual",Float.toString(cursorPrecios.getFloat(0)));
						precioAcumulado=precioAcumulado+(cursorPrecios.getFloat(0));
						Log.d("Acumulado",Float.toString(precioAcumulado));
						
						}
					
					//Varias vacias para poner en las de la derecha "precio:" y el precio
					for(int i=0;i<c.getColumnCount()-1;i++){
						TextView textVacio = new TextView(getApplicationContext());
						textVacio.setText("");
						prec.addView(textVacio);
					}
					TextView precioTotal = new TextView(getApplicationContext());
					precioTotal.setText("Precio: ");
					prec.addView(precioTotal);
					
					TextView valorPrecio = new TextView(getApplicationContext());
					valorPrecio.setText(Float.toString(precioAcumulado));
					valorPrecio.setGravity(1);//El 1 alinea a la derecha el textView
					prec.addView(valorPrecio);
					
					platos.addView(prec);
					
				}catch(Exception e){
					System.out.println("Error al calcular el precio");
				}
			}
			//Calcular el precio total de la cuenta y se pone en un textView-------------- 
			
		}catch(Exception e){
			System.out.println("Error lectura base de datos de Pedido");
		}
		
		
		//Boton Cobrar--------------------------------------------------------------------
		Button cobrar = (Button)findViewById(R.id.botonCobrar);
		cobrar.setText("Cobrar");
		cobrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		Cursor filasPedido = dbPedido.query("Mesas", null, null,null, null,null, null);
            		Cursor filasHistorico = dbHistorico.query("Historico", null, null,null, null,null, null);
            		
            		while(filasPedido.moveToNext()){
            			//Añades los platos a la base de datos del historico
            			ContentValues nuevo = new ContentValues();
            			
            			for (int i=0;i<filasPedido.getColumnCount();i++)
            				for (int j=0;j<filasPedido.getColumnCount();j++){
            					if(filasPedido.getColumnName(i).equals(filasHistorico.getColumnName(j)))
            						nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));
            			
            				}
            			dbHistorico.insert("Historico", null, nuevo);
	            		
	            		//Eliminas los platos de la lista
	            		platos.removeViewAt(0);            			
	               }
            		
            		platos.removeViewAt(0);//Para eliminar la fila del precio
            		
            		//Eliminas platos de la base de datos de entrada
            		dbPedido.delete("Mesas","1", null); 
            		            		
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton cobrar");
            		
            	}
            }
        });
		//Boton Cobrar--------------------------------------------------------------------
	
		
		//Boton AñadirPlato---------------------------------------------------------------
		Button addPlato = (Button)findViewById(R.id.botonAniadirPlato);
		addPlato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		dbPedido.openDatabase(rutaPedido, null,SQLiteDatabase.OPEN_READWRITE);
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton AñadirPlato");
            		
            	}
            }
        });
		//Boton AñadirPlato---------------------------------------------------------------
		
		
		//Boton AñadirBebida--------------------------------------------------------------
		Button addBebida = (Button)findViewById(R.id.botonAniadirBebida);
		addBebida.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		dbPedido.openDatabase(rutaPedido, null,SQLiteDatabase.OPEN_READWRITE);
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton AñadirBebida");
            		
            	}
            }
        });
		//Boton AñadirBebida--------------------------------------------------------------
		
		
		//Boton Eliminar------------------------------------------------------------------
		Button eliminar = (Button)findViewById(R.id.botonEliminar);
		eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		int totalPlatos=platos.getChildCount();
            		int i=0;
            		float precio=0;
            		Cursor c = dbPedido.query("Mesas", null, null,null, null,null, null);
                   	ArrayList<ContentValues> temporal = new ArrayList<ContentValues>();
            		
            		while(i<totalPlatos-1 && c.moveToNext()){
            			TableRow fila = (TableRow)platos.getChildAt(i);
                   		CheckBox cbox= (CheckBox) fila.getChildAt(0);
                   		
                   		if ( cbox.isChecked() ){
                   			platos.removeViewAt(i);//Se elimina de la lista
                   			totalPlatos--;//Se resta 1 al total de platos
                   			
                   		}else{
                   			//Se guardan todas las columnas y contenido en arrayList provisional
                   			ContentValues nuevo = new ContentValues();
                   			
                   			for (int j=0;j<c.getColumnCount();j++){
                   				nuevo.put(c.getColumnName(j), c.getString(j));
                   				if(c.getColumnName(j).equals("Precio"))
                   					precio = precio + Float.parseFloat(c.getString(j));
                   			}
                   			temporal.add(nuevo);
                   			i++;
                        }
                   	}
            		
            		//Se pone el nuevo precio total---------------------------------------
            		TableRow filaPrecio = (TableRow)platos.getChildAt(platos.getChildCount()-1);
               		TextView precioFinal= (TextView) filaPrecio.getChildAt(4);
               		precioFinal.setText(String.valueOf(precio));
               		
            		//Se borra y se rellena con lo que queda(o no si esta vacio)
               		dbPedido.delete("Mesas","1", null);
               		Iterator<ContentValues> it = temporal.iterator();
               		while(it.hasNext()){
               			ContentValues contenido = it.next();
               			dbPedido.insert("Mesas", null, contenido);
               		}
               		
               		if(totalPlatos<=1)//Eliminas la fila del precio si solo quedan la fila del precio
           				platos.removeViewAt(0);
           			
            	}catch(Exception e){
	        		System.out.println("Catch funcionalidad de boton Eliminar");
	        		
	        	}
            }
        });
		//Boton Eliminar------------------------------------------------------------------
			
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
