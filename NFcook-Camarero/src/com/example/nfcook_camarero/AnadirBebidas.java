package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AnadirBebidas extends Activity {
	private int numMesa = 3; //Cambiar 
	private SQLiteDatabase dbFoster, dbPedido;
	String rutaPedido = "/data/data/com.example.camarero/Pedido.db";
	String rutaBaseFoster = "/data/data/com.example.camarero/MiBaseFoster.db";
	int cantidadBebidas;
	Spinner spinnerCantidad;
	boolean eliminarBebidas, anadirBebidas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bebidaslayout);
		eliminarBebidas = anadirBebidas = false;
		
		try{
			dbFoster = SQLiteDatabase.openDatabase(rutaBaseFoster, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			
		}catch(Exception e){
			System.out.println("Error al abrir la base de datos de Foster en anadir Bebidas");
		}
	
	//Consulta para sacar las bebidas de la base de datos principal (de la carta)--------------
	    
  	  	String[] bebidasBaseDatos = new String[]{"Nombre","Foto","Precio"};
  		Cursor cursorBaseDatosBebidas = dbFoster.query("Restaurantes", bebidasBaseDatos, "Restaurante = 'Foster' AND Categoria = 'Bebidas'",null,null, null,null);
  			
  	  //Bebidas----------------------------------------------- 
  		TableLayout tablaBebidas = (TableLayout)findViewById(R.id.tableLayoutBebidas);
  		TableRow filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  		TableRow filaTextViews = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  		
  		
  		/* Utilizamos metrics para sacar el ancho de la pantalla y asi poner 3 bebidas por fila 
  		con un tamano equitativo para cada posible pantalla*/
  		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
  		TableRow.LayoutParams parametrosBotonImagen = new TableRow.LayoutParams(metrics.widthPixels/3, metrics.widthPixels/3);
  		final ArrayList<ImageButton> botonesBebidas = new ArrayList<ImageButton>();
  		final ArrayList<TextView> textViewsBebidas = new ArrayList<TextView>();

  		int bebidasPorFila = 0;
  		while(cursorBaseDatosBebidas.moveToNext()){
  			
			if (bebidasPorFila == 3){ //si llegamos a 3, insertamos en una fila nueva, para que quede organizado
		  	  tablaBebidas.addView(filaBebidas);
		  	  tablaBebidas.addView(filaTextViews);
		  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);	
		  	  filaTextViews = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
		  	  bebidasPorFila = 1;
			}else 
				bebidasPorFila++;
			
			ImageButton botonBebidaNueva = (ImageButton)getLayoutInflater().inflate(R.layout.botonbebidas,filaBebidas,false);
			
			TextView textViewCantidad = new TextView(getApplicationContext());
			  textViewCantidad.setTextColor(Color.BLACK);
			  textViewCantidad.setGravity(Gravity.CENTER);
			  textViewCantidad.setText("0");
			  textViewCantidad.setTextSize(20);
			
			final String nombreBebida = cursorBaseDatosBebidas.getString(0); // es final para el OnClick de cada boton
			String fotoBebida = cursorBaseDatosBebidas.getString(1);
			final String precioBebida = cursorBaseDatosBebidas.getString(2); // es final para el OnClick de cada boton
			
			botonBebidaNueva.setImageResource(Integer.parseInt(fotoBebida)); // metemos la imagen en el boton
			botonBebidaNueva.setLayoutParams(parametrosBotonImagen); // le damos el ancho y alto de parametros

			botonBebidaNueva.setOnClickListener(new View.OnClickListener() {
	            
				public void onClick(View view) {
	            	//insertamos la nueva bebida en la base de datos Pedido
	            	try{
            			// Recoreremos el arraylist de botones hasta encontrar el que se ha pulsado
						Iterator<ImageButton> itBotones = botonesBebidas.iterator();
	        			boolean encontrado = false;
	        			int indiceBotones = 0;
	
	        			while(itBotones.hasNext() && !encontrado){
	        				ImageButton boton = itBotones.next();
	        				if(boton == ((ImageButton)view))
	        					encontrado = true;
	        				else
	        					indiceBotones++;
	        				
	        			}
	        			// sacamos la cantidad actual de la bebida elegida
	        			int cantidad = Integer.parseInt((String) textViewsBebidas.get(indiceBotones).getText());
	            		
	        			if(anadirBebidas){	
	        				cantidad++;
	        				textViewsBebidas.get(indiceBotones).setText(Integer.toString(cantidad));
	            			
	            		}else if(eliminarBebidas && cantidad > 0){
	            			cantidad--;
	            			textViewsBebidas.get(indiceBotones).setText(Integer.toString(cantidad));
	            			
	            		}	
	            		/*
		    			dbPedido = SQLiteDatabase.openDatabase(rutaPedido, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		    			
		            	ContentValues nuevaBebida = new ContentValues();
		            	nuevaBebida.put("NumeroMesa", numMesa);
		            	nuevaBebida.put("Nombre", nombreBebida);
		            	nuevaBebida.put("Observaciones", "observacion");
		            	nuevaBebida.put("Extras", "extra");
		            	nuevaBebida.put("Precio", precioBebida);
		            	
		            	cantidadBebidas = (Integer) spinnerCantidad.getSelectedItem();
               			for (int i=0; i<cantidadBebidas; i++){
               				dbPedido.insert("Pedido", null, nuevaBebida);
               				
               			}*/
	            		
	            	}catch(Exception e){
	            		System.out.println("Error al insertar en la base de datos pedido para actualizarla con la nueva bebida");
	            	}
	            }
	        }); 
			botonesBebidas.add(botonBebidaNueva);
			textViewsBebidas.add(textViewCantidad);
			
		filaBebidas.addView(botonBebidaNueva); //anadimos el boton a la fila
	   	filaTextViews.addView(textViewCantidad);
	  }			
	  tablaBebidas.addView(filaBebidas); //anadimos la fila a la tabla
	  tablaBebidas.addView(filaTextViews); 
	  
	  // Insertamos los dos botones para anadir y eliminar bebidas
	  Button botonEliminar = new Button(getApplicationContext());
	  botonEliminar.setTextColor(Color.BLACK);
	  botonEliminar.setText("Eliminar");
	  botonEliminar.setTextSize(20);
	  botonEliminar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				eliminarBebidas = true;
				anadirBebidas = false;
			}
	  });
	  
	  Button botonAnadir = new Button(getApplicationContext());
	  botonAnadir.setTextColor(Color.BLACK);
	  botonAnadir.setText("Anadir");
	  botonAnadir.setTextSize(20);
	  botonAnadir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				anadirBebidas = true;
				eliminarBebidas = false;
			}
	  });
  	  
	  // Metemos una fila para la separacion de los botones anadir y eliminar
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
	  TextView tvw = new TextView(getApplicationContext());
	  tvw.setTextSize(20);
	  filaBebidas.addView(tvw);
	  tablaBebidas.addView(filaBebidas);
	  
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);

  	  filaBebidas.addView(botonAnadir);
  	  filaBebidas.addView(botonEliminar);
	  tablaBebidas.addView(filaBebidas); //anadimos la fila a la tabla
	  	  
	}
	
@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
