package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
  		
  		/* Utilizamos metrics para sacar el ancho de la pantalla y asi poner 3 bebidas por fila 
  		con un tamano equitativo para cada posible pantalla*/
  		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
  		TableRow.LayoutParams parametrosBotonImagen = new TableRow.LayoutParams(metrics.widthPixels/3, metrics.widthPixels/3);

  		int bebidasPorFila = 0;

  		while(cursorBaseDatosBebidas.moveToNext()){
  			
			if (bebidasPorFila == 3){ //si llegamos a 3, insertamos en una fila nueva, para que quede organizado
		  	  tablaBebidas.addView(filaBebidas);
		  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
		  	  bebidasPorFila = 1;
			}else 
				bebidasPorFila++;
			
			ImageButton botonBebidaNueva = (ImageButton)getLayoutInflater().inflate(R.layout.botonbebidas,filaBebidas,false);
			
			final String nombreBebida = cursorBaseDatosBebidas.getString(0); // es final para el OnClick de cada boton
			String fotoBebida = cursorBaseDatosBebidas.getString(1);
			final String precioBebida = cursorBaseDatosBebidas.getString(2); // es final para el OnClick de cada boton
			
			botonBebidaNueva.setImageResource(Integer.parseInt(fotoBebida)); // metemos la imagen en el boton
			botonBebidaNueva.setLayoutParams(parametrosBotonImagen); // le damos el ancho y alto de parametros

			botonBebidaNueva.setOnClickListener(new View.OnClickListener() {
	            
				public void onClick(View view) {
	            	//insertamos la nueva bebida en la base de datos Pedido
	            	try{
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
               			}

	            	}catch(Exception e){
	            		System.out.println("Error al insertar en la base de datos pedido para actualizarla con la nueva bebida");
	            	}
	            }
	        }); 
	    
	   	filaBebidas.addView(botonBebidaNueva); //anadimos el boton a la fila
	  }			
	  tablaBebidas.addView(filaBebidas); //anadimos la fila a la tabla
	 	  
  	  // Insertamos la fila con el TextView y el Spinner para la cantidad
	  TextView textViewCantidad = new TextView(getApplicationContext());
	  textViewCantidad.setTextColor(Color.BLACK);
	  textViewCantidad.setGravity(Gravity.RIGHT);
	  textViewCantidad.setText("Cantidad: ");
	  textViewCantidad.setTextSize(20);

	  // Spinner
	  List<Integer> numeros = new ArrayList<Integer>();
	  for (int i=1; i<30; i++)
		  numeros.add(i);
	  
	  spinnerCantidad = new Spinner(getApplicationContext());
	  ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, numeros);
	  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  spinnerCantidad.setAdapter(dataAdapter);
	
	  // Creamos una fila nueva y anadimos TextView y Spinner:
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  	  filaBebidas.addView(textViewCantidad);  
	  filaBebidas.addView(spinnerCantidad);
	  
	  tablaBebidas.addView(filaBebidas); //anadimos la fila a la tabla
	  	  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
