package com.example.nfcook_gerente;



import java.util.Arrays;





import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.androidplot.xy.*;


/**
 * 
 * @author roberto
 *
 * Esta clase contiene los datos de cada empleado junto con su foto y 
 * una grafica con su facturacion, seleccionable por dias, meses o años.
 * 
 */


public class InfoEmpleado extends Activity {
	
	private XYPlot mySimpleXYPlot;
	private HandlerGenerico sqlEmpleados;
	private SQLiteDatabase dbEmpleados;
	
	private String fechaNacimiento;
	private String domicilio;
	private String dni;
	private String puesto;
	private String apellido2;
	private String nombre;
	private String foto;
	private String apellido1;
	private String idEmpleado;
	
	private ImageView imageViewFoto;
	private FrameLayout contenedorFoto;
	private TextView nombreYApellidos;
	private Button dia, mes, anio;

	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.informacion_empleados);
		
		Bundle bundle = getIntent().getExtras();
		idEmpleado = bundle.getString("IdEmpleado");
		
		importarBaseDatos();
		cargarInfoEmpleado();
		mostrarInfo();
		funcionalidadCalendario();
		funcionalidadBotones();
	}
	
	private void funcionalidadBotones() {
		dia = (Button)findViewById(R.id.dia);
		mes = (Button)findViewById(R.id.mes);
		anio = (Button)findViewById(R.id.anio);
		
		dia.setText("Dia");
		mes.setText("Mes");
		anio.setText("Anio");
		
		mySimpleXYPlot = (XYPlot) findViewById(R.id.graficaIngresos);
		
		dia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	
            	//En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
            	Number[] ejeY = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
            	
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(ejeY),  // Array de datos
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Sólo valores verticales
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.redraw();
            }});	
		
		mes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	//Meter 12 valores para que ponga 30 valores en el eje X
            	Number[] ejeY = {2,10,4,5,20,5,34,100,76,23,122,56,
            			2,10,4,5,20,5,34,100,76,23,122,56,2,10,4,5,20,5};
            	
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(ejeY),  // Array de datos
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Sólo valores verticales
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.redraw();
            }});
		anio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	
            	Number[] ejeY = {2,10,4,5,20,5,34,100,76,23,122,56};//Son los valores del eje Y
            	
            	
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(ejeY),  // Array de datos
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Sólo valores verticales
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.redraw();
            }});
		
	}

	

	private void mostrarInfo() {
		imageViewFoto = (ImageView)findViewById(R.id.fotoEmpleado);
		imageViewFoto.setImageResource(getResources().getIdentifier(foto,"drawable",this.getPackageName()));
		
		
		nombreYApellidos = (TextView)findViewById(R.id.datosEmpleado);
		nombreYApellidos.setText(nombre + '\n' + apellido1 + '\n' + apellido2 + '\n' + '\n' +
				"Puesto:\n" + " " + puesto + '\n' + "DNI:\n" + " " + dni + '\n' + 
				"Domicilio:\n" + " " + domicilio + '\n' + "Fecha de nacimiento:\n" + " " + fechaNacimiento);
	    
		//Display display = getWindowManager().getDefaultDisplay();
		//int ancho = display.getWidth();
		//imageViewFoto.getLayoutParams().width = 10;
		//FrameLayout.LayoutParams parametros = new FrameLayout.LayoutParams(,ancho/4);
		//imageViewFoto.setLayoutParams(parametros);
		
	}

	private void cargarInfoEmpleado() {
		try{
			String[] campos = new String[]{"Foto","Nombre","Apellido1","Apellido2","Puesto","IdEmpleado","DNI","Domicilio","FechaNacimiento"};
		   
			String[] datos = new String[]{idEmpleado};
			Cursor c = dbEmpleados.query("Empleados",campos, "idEmpleado=?",datos, null, null,null);
		    
		   c.moveToNext();
		   nombre = c.getString(1);
		   apellido1 = c.getString(2);
		   apellido2 = c.getString(3);
		   foto = c.getString(0);
		   puesto = c.getString(4);
		   idEmpleado = c.getString(5);
		   dni = c.getString(6);
		   domicilio = c.getString(7);
		   fechaNacimiento = c.getString(8);
		   
		  
		   
		}catch(Exception e){
			System.out.println("Error en cargarInfoEmpleado");
			
			
		}
		
	}

	private void funcionalidadCalendario() {
		/*
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		calendario = (CalendarPickerView) findViewById(R.id.calendario);
		Date today = new Date();
		calendario.init(today, nextYear.getTime()).withSelectedDate(today);*/
		
	}

	

	public void importarBaseDatos(){
		 try{
				sqlEmpleados = new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleados.db");
				dbEmpleados = sqlEmpleados.open();
			}catch(SQLiteException e){
				System.out.println("CATCH");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
			}
	}
		
}
