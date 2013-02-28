package com.example.nfcook_camarero;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AnadirBebidas extends Activity {
	private int numMesa, idCamarero, personasMesa;  
	private SQLiteDatabase dbRestaurante, dbPedido;
	String rutaPedido = "/data/data/com.example.nfcook_camarero/databases/Mesas.db";
	String rutaBaseFoster = "/data/data/com.example.nfcook_camarero/databases/MiBase.db";
	boolean eliminarBebidas, anadirBebidas;
	ArrayList<ImageButton> botonesBebidas;
	ArrayList<TextView> textViewsBebidas;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bebidaslayout);
		// Declaraciones -------------------------------------
		eliminarBebidas = anadirBebidas = false;
  		int bebidasPorFila = 0;

		// Layouts
  		TableLayout tablaBebidas = (TableLayout)findViewById(R.id.tableLayoutBebidas);
  		TableRow filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  		TableRow filaTextViews = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  		
  		/* Utilizamos metrics para sacar el ancho de la pantalla y asi poner 3 bebidas por fila 
  		con un tamano equitativo para cada posible pantalla*/
		DisplayMetrics metrics = this.getResources().getDisplayMetrics(); // metricas de la pantalla del terminal
  		TableRow.LayoutParams parametrosBotonImagen = new TableRow.LayoutParams(metrics.widthPixels/3, metrics.widthPixels/3);
  		botonesBebidas = new ArrayList<ImageButton>();
  		textViewsBebidas = new ArrayList<TextView>();
  		
  		// Fin Declaraciones ---------------------------------
  		
		try{
			dbRestaurante = SQLiteDatabase.openDatabase(rutaBaseFoster, null, SQLiteDatabase.CREATE_IF_NECESSARY);

		}catch(Exception e){
			System.out.println("Error al abrir la base de datos de Foster en anadir Bebidas");
		}
	
		// Consulta para sacar las bebidas de la base de datos principal (de la carta)  
  	  	String[] bebidasBaseDatos = new String[]{"Id","Nombre","Foto","Precio"};
  		Cursor cursorBaseDatosBebidas = dbRestaurante.query("Restaurantes", bebidasBaseDatos, "Restaurante = 'Foster' AND Categoria = 'Bebidas'",null,null, null,null);
  		
  		while(cursorBaseDatosBebidas.moveToNext()){
  			
			if (bebidasPorFila == 3){ //si llegamos a 3, insertamos en una fila nueva, para que quede organizado
				tablaBebidas.addView(filaBebidas);
			  	tablaBebidas.addView(filaTextViews);
			  	filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);	
			  	filaTextViews = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
			  	bebidasPorFila = 1;
			
			}else 
				bebidasPorFila++;
			
			// TextView
			TextView textViewCantidad = new TextView(getApplicationContext());
			textViewCantidad.setTextColor(Color.BLACK);
			textViewCantidad.setGravity(Gravity.CENTER);
			textViewCantidad.setText("0");
			textViewCantidad.setTextSize(20);
			
			// ImageButton
			String idBebida = cursorBaseDatosBebidas.getString(0); 
			String nombreBebida = cursorBaseDatosBebidas.getString(1); 
			String fotoBebida = cursorBaseDatosBebidas.getString(2);
			String precioBebida = cursorBaseDatosBebidas.getString(3); 
			String[] nombrePrecioId = new String[3]; // almacenamos el nombre y el precio de la bebida como tag de cada boton
			nombrePrecioId[0] = nombreBebida;
			nombrePrecioId[1] = precioBebida;
			nombrePrecioId[2] = idBebida;
			
			ImageButton botonBebidaNueva = (ImageButton)getLayoutInflater().inflate(R.layout.botonbebidas,filaBebidas,false);
			botonBebidaNueva.setTag(nombrePrecioId);
			botonBebidaNueva.setImageResource(Integer.parseInt(fotoBebida)); // metemos la imagen en el boton
			botonBebidaNueva.setLayoutParams(parametrosBotonImagen); // le damos el ancho y alto de parametros

			botonBebidaNueva.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
	            	onClickBotonBebida(view);
	            }
	        }); 
			botonesBebidas.add(botonBebidaNueva);
			textViewsBebidas.add(textViewCantidad);
			
		filaBebidas.addView(botonBebidaNueva); //anadimos el boton a la fila
	   	filaTextViews.addView(textViewCantidad);
	   	
	  }// fin while de cursorBaseDatosBebidas ------------------------
  		
	  tablaBebidas.addView(filaBebidas); // anadimos la fila de botones a la tabla
	  tablaBebidas.addView(filaTextViews); // anadimos la fila de text views a la tabla
	  
	  // Insertamos los dos botones para anadir y eliminar bebidas y validar
	  Button botonEliminar = insertaBotonEliminar();
	  Button botonAnadir = insertaBotonAnadir();
	  Button botonValidar = insertaBotonValidar();
	  
	  // Metemos una fila para la separacion de los botones anadir y eliminar
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
	  TextView tvw = new TextView(getApplicationContext());
	  tvw.setTextSize(20);
	  filaBebidas.addView(tvw); // Anadimos tableView para dar espacio
	  tablaBebidas.addView(filaBebidas);
	  
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);

  	  filaBebidas.addView(botonAnadir); // Anadimos boton Anadir
  	  filaBebidas.addView(botonEliminar); // Anadimos boton Eliminar
	  tablaBebidas.addView(filaBebidas); // Anadimos la fila a la tabla
	  	  
  	  filaBebidas = (TableRow)getLayoutInflater().inflate(R.layout.filabebidas, tablaBebidas, false);
  	  filaBebidas.addView(botonValidar); // Anadimos boton Validar
	  tablaBebidas.addView(filaBebidas); 
	  	  
	}
	
	// Boton Eliminar
	public Button insertaBotonEliminar(){
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
		 return botonEliminar;
	}
	
	// Boton Anadir
	public Button insertaBotonAnadir(){
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
		 return botonAnadir;
	}
	
	// Boton Validar
	public Button insertaBotonValidar(){
		 Button botonValidar = new Button(getApplicationContext());
		 botonValidar.setTextColor(Color.BLACK);
		 botonValidar.setText("Validar");
		 botonValidar.setTextSize(20);
		 botonValidar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// actualizamos la base de datos con las bebidas nuevas
				insertaBebidasEnBaseDeDatos();
			}
		 });
		 return botonValidar;
	}
	
	public void insertaBebidasEnBaseDeDatos(){
		try{
			dbPedido = SQLiteDatabase.openDatabase(rutaPedido, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			for(int i=0; i<textViewsBebidas.size(); i++){
				TextView t = textViewsBebidas.get(i);
				int bebidas = Integer.parseInt((String)t.getText());
				
				if(bebidas != 0){
					String[] nombrePrecioId = (String[]) botonesBebidas.get(i).getTag();
					String[] fechaHora = fechaYHora(); 
					String nombreBebida = nombrePrecioId[0];
					String precioBebida = nombrePrecioId[1];
					String idBebida = nombrePrecioId[2];
					
					ContentValues nuevaBebida = new ContentValues();
	            	nuevaBebida.put("NumMesa", numMesa);
	            	nuevaBebida.put("idCamarero", idCamarero);
	            	nuevaBebida.put("idPlato", idBebida);
	            	nuevaBebida.put("Observaciones", "");
	            	nuevaBebida.put("Extras", "");
	            	nuevaBebida.put("FechaHora", fechaHora[0] + " " + fechaHora[1]); //[0]=fecha [1]=hora
	            	nuevaBebida.put("Nombre", nombreBebida);
	            	nuevaBebida.put("Precio", precioBebida);
	            	nuevaBebida.put("Personas", personasMesa);
	            	
					for(int bebida=0; bebida<bebidas; bebida++)
						// vamos anadiendo una a una la bebida 
	       				dbPedido.insert("Mesas", null, nuevaBebida);
				}
			}
	 	}catch(Exception e){
    		System.out.println("Error al insertar en la base de datos pedido para actualizarla con las nuevas bebidas");
    	}
	}
	
	public String[] fechaYHora(){
		//Sacamos la hora a la que el camarero ha introducido la mesa
    	Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatteDate = df.format(date);
        
        Date dt = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        String formatteHour = dtf.format(dt.getTime());
        
        String[] horaFecha = new String[2];
        horaFecha[0] = formatteDate;
        horaFecha[1] = formatteHour;
        
       return horaFecha;
	}
	
	public void onClickBotonBebida(View view){
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
