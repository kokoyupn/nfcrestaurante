package com.example.nfcook_gerente;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.androidplot.xy.*;

import android.widget.CalendarView.OnDateChangeListener;
import auxiliares.ObjetoAuxGrafica;


/**
 * 
 * @author roberto
 *
 * Esta clase contiene los datos de cada empleado junto con su foto y 
 * una grafica con su facturacion, seleccionable por dias, meses o años.
 * 
 */


@SuppressLint("SimpleDateFormat")
public class InfoEmpleado extends Activity {
	
	private XYPlot mySimpleXYPlot;
	private HandlerGenerico sqlEmpleados,sqlEmpleado,sqlFicha,sqlIngresos;
	private SQLiteDatabase dbEmpleados,dbEmpleado,dbFicha,dbIngresos;
	
	private String fechaNacimiento;
	private String domicilio;
	private String dni;
	private String puesto;
	private String apellido2;
	private String nombre;
	private String foto;
	private String apellido1;
	private String idEmpleado,diaElegido,mesElegido,anioElegido;
	 
	
	private ImageView imageViewFoto;
	private FrameLayout contenedorFoto;
	private TextView nombreYApellidos;
	private Spinner selDia, selMes, selAnio;
	private Button dibDia, dibMes, dibAnio;
	private CalendarView calendario;
	private String [] spinnerDia,spinnerMes;
	private ArrayList<String> spinnerAnio;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.informacion_empleados);
		
		Bundle bundle = getIntent().getExtras();
		idEmpleado = bundle.getString("IdEmpleado");
		
		importarBaseDatos();
		cargarInfoEmpleado();
		mostrarInfo();
		funcionalidadCalendario();
		cargarSpinners();
		dibujarGrafica();
	}
	
	private void cargarSpinners() {
		
		//Se carga el spinner de los dias del mes
		spinnerDia = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14",
				"15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
		selDia = (Spinner) findViewById(R.id.selDia);
		ArrayAdapter adapterDia = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerDia);
		selDia.setAdapter(adapterDia);
		selDia.setOnItemSelectedListener( 
                new AdapterView.OnItemSelectedListener() 
                {
                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
                     {   
                    	 diaElegido = spinnerDia[position];                           
                     }  
                     public void onNothingSelected(AdapterView<?> parent) 
                     {
                         
                     }
               }
                );
		
		//Se carga el spinner de los meses
		spinnerMes = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
		selMes = (Spinner) findViewById(R.id.selMes);
		ArrayAdapter adapterMes = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerMes);
		selMes.setAdapter(adapterMes);
		selMes.setOnItemSelectedListener( 
                new AdapterView.OnItemSelectedListener() 
                {
                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
                     {   
                    	 mesElegido = spinnerMes[position];                           
                     }  
                     public void onNothingSelected(AdapterView<?> parent) 
                     {
                         
                     }
               }
                );
		
		
		//Se carga el spinner de los anios que tienes recogidos en la base de datos
		String[] campos = new String[]{"Anio"};
		Cursor c = dbFicha.query("Ficha",campos, null, null, null,null, null);
		spinnerAnio = new ArrayList<String>();
		while(c.moveToNext()){
			if( ! spinnerAnio.contains(c.getString(0)))
				spinnerAnio.add(c.getString(0));	    	
	    }
		selAnio = (Spinner) findViewById(R.id.selAnio);
		ArrayAdapter adapterAnio = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerAnio);
		selAnio.setAdapter(adapterAnio);
		
		selAnio.setOnItemSelectedListener( 
                new AdapterView.OnItemSelectedListener() 
                {
                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
                     {   
                    	 anioElegido = spinnerAnio.get(position);                           
                     }  
                     public void onNothingSelected(AdapterView<?> parent) 
                     {
                         
                     }
               }
                );

		
	}
	
	public void dibujarGrafica(){
		
		mySimpleXYPlot = (XYPlot) findViewById(R.id.graficaIngresos);
		
		dibDia = (Button)findViewById(R.id.dibDia);
		dibMes = (Button)findViewById(R.id.dibMes);
		dibAnio = (Button)findViewById(R.id.dibAnio);
		dibDia.setText("Dia");
		dibMes.setText("Mes");
		dibAnio.setText("Anio");
		
		dibDia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	
            	String[] campos = new String[]{"Hora","Importe"};
     		    String[] datos = new String[]{diaElegido,mesElegido,anioElegido};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Dia=? AND Mes=? AND Anio=?",datos, null, null,null);
    		    
    			//Se llena un arrayList con la cantidad de 
    			ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
    		    while(c.moveToNext()){
    		    	String horaCompleta = c.getString(0);
    		    	String [] hora = horaCompleta.split(":");
    		    	if(temporal.size() == 0){
    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(hora[0],c.getString(1));
		    			temporal.add(nuevo);
    		    	}else{
    		    		boolean existe = false;
	    		    	for(int i=0;i<temporal.size();i++){
	    		    		if( (temporal.get(i).getTiempo()).equals(hora[0]) ){
	    		    			temporal.get(i).sumaImporte(c.getString(1));
	    		    			existe = true;
	    		    		}
	    		    	}
	    		    	if( !existe){
	    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(hora[0],c.getString(1));
	    		    		temporal.add(nuevo);
	    		    	}
	    		    }
    		    }
    		    Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion
    		        		    
    		    Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
    		    ejeY[0] = 0;
    		    Number [] ejeX = new Number[temporal.size()+1];
    		    ejeX[0] = 0;
    		    for(int i = 0;i<temporal.size();i++){
    		    	ejeY[i+1] = temporal.get(i).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
    		    	ejeX[i+1] = Double.parseDouble(temporal.get(i).getTiempo());
    		    }
    			
    		    //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
    		    XYSeries series1 = new SimpleXYSeries(
            			Arrays.asList(ejeX),
                        Arrays.asList(ejeY),  // Array de datos
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas las escriba sin decimales
                mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
                mySimpleXYPlot.redraw();
            }});	
		
		dibMes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	
            	String[] campos = new String[]{"Dia","Importe"};
     		    String[] datos = new String[]{mesElegido,anioElegido};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Mes=? AND Anio=?",datos, null, null,null);
    		    
    			ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
    		    while(c.moveToNext()){
    		    	if(temporal.size() == 0){
    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(c.getString(0),c.getString(1));
		    			temporal.add(nuevo);
    		    	}else{
    		    		boolean existe = false;
	    		    	for(int i=0;i<temporal.size();i++){
	    		    		if( (temporal.get(i).getTiempo()).equals(c.getString(0)) ){
	    		    			temporal.get(i).sumaImporte(c.getString(1));
	    		    			existe = true;
	    		    		}
	    		    	}
	    		    	if( !existe){
	    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(c.getString(0),c.getString(1));
	    		    		temporal.add(nuevo);
	    		    	}
	    		    }
    		    }
    		    Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion
    		    
    		    Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
    		    ejeY[0] = 0;
    		    Number [] ejeX = new Number[temporal.size()+1];
    		    ejeX[0] = 0;
    		    for(int i = 0;i<temporal.size();i++){
    		    	ejeY[i+1] = temporal.get(i).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
    		    	ejeX[i+1] = Double.parseDouble(temporal.get(i).getTiempo());
    		    }
            	
    		    XYSeries series1 = new SimpleXYSeries(
            			Arrays.asList(ejeX),
                        Arrays.asList(ejeY),  // Array de datos
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas las escriba sin decimales
                mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
                mySimpleXYPlot.redraw();
            }});
		dibAnio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mySimpleXYPlot.clear();
            	
            	String[] campos = new String[]{"Mes","Importe"};
     		    String[] datos = new String[]{anioElegido};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Anio=?",datos, null, null,null);
    		    
    			ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
    		    while(c.moveToNext()){
    		    	if(temporal.size() == 0){
    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(c.getString(0),c.getString(1));
		    			temporal.add(nuevo);
    		    	}else{
    		    		boolean existe = false;
	    		    	for(int i=0;i<temporal.size();i++){
	    		    		if( (temporal.get(i).getTiempo()).equals(c.getString(0)) ){
	    		    			temporal.get(i).sumaImporte(c.getString(1));
	    		    			existe = true;
	    		    		}
	    		    	}
	    		    	if( !existe){
	    		    		ObjetoAuxGrafica nuevo = new ObjetoAuxGrafica(c.getString(0),c.getString(1));
	    		    		temporal.add(nuevo);
	    		    	}
	    		    }
    		    }
    		    Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion
    		        		    
    		    Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
    		    ejeY[0] = 0;
    		    Number [] ejeX = new Number[temporal.size()+1];
    		    ejeX[0] = 0;
    		    for(int i = 0;i<temporal.size();i++){
    		    	ejeY[i+1] = temporal.get(i).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
    		    	ejeX[i+1] = Double.parseDouble(temporal.get(i).getTiempo());
    		    }
            	
    		    XYSeries series1 = new SimpleXYSeries(
            			Arrays.asList(ejeX),
                        Arrays.asList(ejeY),  // Array de datos
                        "Facturacion"); // Nombre de la primera serie
                
                //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
                LineAndPointFormatter formato = 
                		new LineAndPointFormatter( Color.rgb(0, 200, 0),Color.rgb(0, 100, 0),Color.rgb(150, 190, 150), null );
         
                //Una vez definida la serie (datos y estilo), la añadimos al panel
                mySimpleXYPlot.addSeries(series1, formato);
                mySimpleXYPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas las escriba sin decimales
                mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
                mySimpleXYPlot.redraw();
            }});
		
	}

	

	private void mostrarInfo() {
		imageViewFoto = (ImageView)findViewById(R.id.fotoEmpleado);
		imageViewFoto.setImageResource(getResources().getIdentifier(foto,"drawable",this.getPackageName()));
		
		
		nombreYApellidos = (TextView)findViewById(R.id.datosEmpleado);
		nombreYApellidos.setText(nombre + " " + apellido1 + " " + apellido2 + '\n' +
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

	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	private void funcionalidadCalendario() {
		
		calendario = (CalendarView)findViewById(R.id.calendario);
		
		calendario.setFirstDayOfWeek(0x00000002);//Es el lunes el 0x00000002
		
		calendario.setOnDateChangeListener(new OnDateChangeListener() {

            @SuppressLint("SimpleDateFormat")
			@Override
            public void onSelectedDayChange(CalendarView view, int year, int month,int dayOfMonth) {
            	try{
            		//El calendario empieza los meses en 0 como un array
            		month++;
            		
	            	//Cálculo de las horas reales del empleado por contrato
	            	String[] campos = new String[]{"HorasContrato"};
	     		    String[] datos = new String[]{idEmpleado};
	    			Cursor c = dbEmpleados.query("Empleados",campos, "idEmpleado=?",datos, null, null,null);
	    		    c.moveToNext();
	    		    int horasContrato = Integer.parseInt(c.getString(0));
	    		    
	    		    //Cálculo de las horas que trabaja el empleado en realidad
	    		    String dia = null;
	    		    String mes = null;
	    		    if(dayOfMonth < 10){
	    		    	dia = "0" + Integer.toString(dayOfMonth); 
	    		    }else{
	    		    	dia = Integer.toString(dayOfMonth);
	    		    }
	    		    
	    		    if(month < 10){
	    		    	mes = "0" + Integer.toString(month);    		    	
	    		    }else{
	    		    	mes = Integer.toString(month);
	    		    }
	    		    String fecha = dia + "/" + mes + "/" + year;
	    		    String[] campos1 = new String[]{"IdEmpleado","Entrada","Salida"};
	     		    String[] datos1 = new String[]{idEmpleado,fecha};
	    			Cursor cu = dbEmpleado.query("Empleado",campos1, "IdEmpleado=? AND Fecha=?",datos1, null, null,null);
	    			
	    			if(cu.moveToNext() != false){
		    			
		    			SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
	    				Date entrada = formatoFecha.parse(cu.getString(1));
	    				Date salida = formatoFecha.parse(cu.getString(2));
	
	    				long diferencia = salida.getTime() - entrada.getTime(); 
	    				int diasReales = (int) (diferencia / (1000*60*60*24));  
	    				int horasReales = (int) ((diferencia - (1000*60*60*24*diasReales)) / (1000*60*60)); 
	    				
		    			/*
		    			 * Con esto una vez se pone de un color, se queda de ese color a no ser que se cambie
		    		    if(horasReales >= horasContrato){
		    		    	calendario.setSelectedWeekBackgroundColor(Color.GREEN);
		    		    }else{
		    		    	calendario.setSelectedWeekBackgroundColor(Color.RED);
		    		    }*/
		    		    
		    		     
		                AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(InfoEmpleado.this);
		                ventanaEmergente.setNegativeButton("Aceptar", null);
		                View vistaAviso = LayoutInflater.from(InfoEmpleado.this).inflate(R.layout.ventana_emergente_horas_trabajadas, null);
		  				TextView texto = (TextView)vistaAviso.findViewById(R.id.texto);
		  				String empleado = nombre + " " + apellido1 + " " + apellido2;
		  				texto.setText( empleado + '\n' + 
			  	    			"Ha trabajado: " + Integer.toString(horasReales) + " horas el dia: " + fecha + ".\n" +
			  	    					"Sus horas por contrato son: " + Integer.toString(horasContrato));
			  	    	ventanaEmergente.setView(vistaAviso);
			  	    	ventanaEmergente.show();
	    			}else{
	    				String empleado = nombre + " " + apellido1 + " " + apellido2;
	    				Toast.makeText(getApplicationContext(), empleado + " el dia " + fecha + " no ha trabajado", 0).show();
			    		   
	    			}
	
	            }catch(Exception e){
	            	System.out.print("Error en la resta de horas del calendario");
	            }
            	
            }
        });
		
		
		/*Calendar nextYear = Calendar.getInstance();
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
				System.out.println("CATCH EMPLEADOS");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlEmpleado = new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleado.db");
				dbEmpleado = sqlEmpleado.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH EMPLEADO");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlFicha = new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Ficha.db");
				dbFicha = sqlFicha.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH Ficha");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlIngresos = new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Ingresos.db");
				dbIngresos = sqlIngresos.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH Ingresos");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
	}
		
}
