package com.example.nfcook_gerente;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import adapters.ContenidoSpinnerHijo;
import adapters.MiSpinnerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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

import com.androidplot.series.XYSeries;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYStepMode;

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
	
	private MultitouchPlot mySimpleXYPlot;
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
	private TextView nombreYApellidos;
	private Button dibDia, dibMes, dibAnio,botonCalendario;
	private ArrayList<ContenidoSpinnerHijo> spinnerAnio,spinnerDia,spinnerMes;
	private TextView periodoElegido;
	private TextView puestoEmpleado;
	private TextView dniEmpleado;
	private TextView domicilioEmpleado;
	private TextView nacimientoEmpleado;
	
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.informacion_empleados);
		
		Bundle bundle = getIntent().getExtras();
		idEmpleado = bundle.getString("IdEmpleado");
		
		importarBaseDatos();
		cargarInfoEmpleado();
		mostrarInfo();
		funcionalidadCalendario();
		dibujarGrafica();
		
		// Cambiamos el fondo al ActionBar
	  	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#004400")));
	  	getActionBar().setTitle(" DATOS EMPLEADO");
	}

	private void cargarSpinners(String distincion,Spinner selDia,Spinner selMes, Spinner selAnio, Builder ventanaEmergenteSpinners) {
		
		if(distincion.equals("dia")){
			
			//Se carga el spinner de los dias del mes
			spinnerDia = new ArrayList<ContenidoSpinnerHijo>();
			
			for(int i = 1; i<=31; i++){ 
				spinnerDia.add(new ContenidoSpinnerHijo(i));
			}
			
			MiSpinnerAdapter adapterDia = new MiSpinnerAdapter(ventanaEmergenteSpinners.getContext(),spinnerDia); 
			selDia.setAdapter(adapterDia);
			
			selDia.setOnItemSelectedListener( 
	                new AdapterView.OnItemSelectedListener() 
	                {
	                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
	                     {   
	                    	diaElegido = Integer.toString(((ContenidoSpinnerHijo) parent.getItemAtPosition(position)).getDato());                 
	                     }  
	                     public void onNothingSelected(AdapterView<?> parent) 
	                     {
	                         
	                     }
	               }
	                );
		}
		
		if( distincion.equals("mes") || distincion.equals("dia") ){
			
			//Se carga el spinner de los meses
			spinnerMes = new ArrayList<ContenidoSpinnerHijo>();
			
			for(int i = 1; i<=12; i++){ 
				spinnerMes.add(new ContenidoSpinnerHijo(i));
			}
			
			MiSpinnerAdapter adapterMes = new MiSpinnerAdapter(ventanaEmergenteSpinners.getContext(),spinnerMes); 
			selMes.setAdapter(adapterMes);
			
			//FIXME spinnerMes.setSelection(mes-1,true);

			selMes.setOnItemSelectedListener( 
	                new AdapterView.OnItemSelectedListener() 
	                {
	                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
	                     {   
	                    	 mesElegido = Integer.toString(((ContenidoSpinnerHijo) parent.getItemAtPosition(position)).getDato()); 
	                     }  
	                     public void onNothingSelected(AdapterView<?> parent) 
	                     {
	                         
	                     }
	               }
	                );
		}
		
		
		//Se carga el spinner de los anios que tienes recogidos en la base de datos
		String[] campos = new String[]{"Anio"};
		Cursor c = dbFicha.query("Ficha",campos, null, null, null,null, null);
		
		spinnerAnio = new ArrayList<ContenidoSpinnerHijo>();
		while(c.moveToNext()){
			if( ! spinnerAnio.contains(c.getString(0)))
				spinnerAnio.add( new ContenidoSpinnerHijo( Integer.parseInt(c.getString(0))));	    	
	    }
		
		MiSpinnerAdapter adapterAnio = new MiSpinnerAdapter(ventanaEmergenteSpinners.getContext(),spinnerAnio); 
		selAnio.setAdapter(adapterAnio);
		
		selAnio.setOnItemSelectedListener( 
                new AdapterView.OnItemSelectedListener() 
                {
                     public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) 
                     {   
                    	 anioElegido = Integer.toString(((ContenidoSpinnerHijo) parent.getItemAtPosition(position)).getDato());   
                     }  
                     public void onNothingSelected(AdapterView<?> parent) 
                     {
                         
                     }
               }
                );

		
	}

	protected void onClickBotonAceptarAlertDialogDia(final MultitouchPlot grafica,final Builder ventanaEmergenteSpinners) {
		ventanaEmergenteSpinners.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				grafica.clear();
				
            	
            	String[] campos = new String[]{"Hora","Importe","IdEmpleado"};
     		    String[] datos = new String[]{diaElegido,mesElegido,anioElegido,idEmpleado};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Dia=? AND Mes=? AND Anio=? AND IdEmpleado=?",datos, null, null,null);
    		    
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
    		        
    		    String titulo = diaElegido + "/" + mesElegido + "/"+ anioElegido;
    		    formatoGrafica(grafica,temporal,"Hora",titulo);
    		    
                /*periodoElegido = (TextView) findViewById(R.id.periodoElegido);
                periodoElegido.setText("Fecha de la grafica: " + 
                		diaElegido + "/" + mesElegido + "/"+ anioElegido);*/
                		
		
			}
		});	
	}

	protected void onClickBotonAceptarAlertDialogMes(final MultitouchPlot grafica,final Builder ventanaEmergenteSpinners) {
		ventanaEmergenteSpinners.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		
				grafica.clear();
            	
            	String[] campos = new String[]{"Dia","Importe","IdEmpleado"};
     		    String[] datos = new String[]{mesElegido,anioElegido,idEmpleado};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Mes=? AND Anio=? AND IdEmpleado=?",datos, null, null,null);
    		    
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
    		    
    		    String titulo = mesElegido + "/"+ anioElegido;
    		    formatoGrafica(grafica,temporal,"Dia",titulo);
    		    
    		    /*periodoElegido = (TextView) findViewById(R.id.periodoElegido);
                periodoElegido.setText("Fecha de la grafica: " + mesElegido + "/"+ anioElegido);*/
		
			}
		});	
	}

	protected void onClickBotonAceptarAlertDialogAnio(final MultitouchPlot grafica,final Builder ventanaEmergenteSpinners) {
		ventanaEmergenteSpinners.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		
				grafica.clear();
            	
            	String[] campos = new String[]{"Mes","Importe","IdEmpleado"};
     		    String[] datos = new String[]{anioElegido,idEmpleado};
    			Cursor c = dbIngresos.query("Ingresos",campos, "Anio=? AND IdEmpleado=?",datos, null, null,null);
    		    
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
    		    
    		    formatoGrafica(grafica,temporal,"Mes",anioElegido);
    		    
    		   /* periodoElegido = (TextView) findViewById(R.id.periodoElegido);
                periodoElegido.setText("Fecha de la grafica: " + anioElegido);*/
		
			}
		});	
	}
	
	public void formatoGrafica(final MultitouchPlot grafica, ArrayList<ObjetoAuxGrafica> temporal,String periodo,String titulo){
		
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
        LineAndPointFormatter formato = new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(0, 100, 0), Color.rgb(150, 190, 150));  
        
        //change the line width
        Paint paint = formato.getLinePaint();
        paint.setStrokeWidth(5);
        formato.setLinePaint(paint);
 
        //Una vez definida la serie (datos y estilo), la añadimos al panel
        grafica.addSeries(series1, formato);
        
        //EjeX
        grafica.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
        grafica.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
        //grafica.setRangeStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
        grafica.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
        //EjeY
        Number rangoEjeY = (ejeY[ejeY.length-1]).intValue() + 5;//Con esto el ejeY llega toma como maximo valor, la mayor facturacion + 5 para que se vea bien
        grafica.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
        
        grafica.setDomainLabel(periodo);
        grafica.getLegendWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 200, SizeLayoutType.ABSOLUTE));
        grafica.getTitleWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 170, SizeLayoutType.ABSOLUTE));
        grafica.setTitle(titulo);
        
        grafica.disableAllMarkup();
        grafica.redraw();
        
        
	}
	
	public void dibujarGrafica(){
		
		mySimpleXYPlot = (MultitouchPlot) findViewById(R.id.graficaIngresos);
		//activamos el zoom
		mySimpleXYPlot.initTouchHandling();
		
		mySimpleXYPlot.setRangeLabel("Euros");
		
		dibDia = (Button)findViewById(R.id.dibDia);
		dibMes = (Button)findViewById(R.id.dibMes);
		dibAnio = (Button)findViewById(R.id.dibAnio);
		
		dibDia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(InfoEmpleado.this);
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	View vistaAviso = LayoutInflater.from(InfoEmpleado.this).inflate(R.layout.eleccion_dia_mes_anio, null);
				Spinner selDia = (Spinner)vistaAviso.findViewById(R.id.spinner_dia_dma);
				Spinner selMes = (Spinner)vistaAviso.findViewById(R.id.spinner_mes_dma);
				Spinner selAnio = (Spinner)vistaAviso.findViewById(R.id.spinner_anio_dma);
				ventanaEmergenteSpinners.setView(vistaAviso);
				cargarSpinners("dia",selDia,selMes,selAnio,ventanaEmergenteSpinners);
				onClickBotonAceptarAlertDialogDia(mySimpleXYPlot,ventanaEmergenteSpinners);
				ventanaEmergenteSpinners.show();
				
            }});
		
		dibMes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(InfoEmpleado.this);
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	
            	View vistaAviso = LayoutInflater.from(InfoEmpleado.this).inflate(R.layout.eleccion_mes_anio, null);
				Spinner selMes = (Spinner)vistaAviso.findViewById(R.id.spinner_mes_ma);
				Spinner selAnio = (Spinner)vistaAviso.findViewById(R.id.spinner_anio_ma);
				ventanaEmergenteSpinners.setView(vistaAviso);
				cargarSpinners("mes",null,selMes,selAnio,ventanaEmergenteSpinners);
				onClickBotonAceptarAlertDialogMes(mySimpleXYPlot,ventanaEmergenteSpinners);
				ventanaEmergenteSpinners.show();
				
           }});
		dibAnio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(InfoEmpleado.this);
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	
            	View vistaAviso = LayoutInflater.from(InfoEmpleado.this).inflate(R.layout.eleccion_anio, null);
				Spinner selAnio = (Spinner)vistaAviso.findViewById(R.id.spinner_anio_a);
				ventanaEmergenteSpinners.setView(vistaAviso);
				cargarSpinners("anio",null,null,selAnio,ventanaEmergenteSpinners);
				onClickBotonAceptarAlertDialogAnio(mySimpleXYPlot,ventanaEmergenteSpinners);
				ventanaEmergenteSpinners.show();
				
            }});
		
	}   	

	private void mostrarInfo() {
		imageViewFoto = (ImageView)findViewById(R.id.fotoEmpleado);
		imageViewFoto.setImageResource(getResources().getIdentifier(foto,"drawable",this.getPackageName()));
		
		nombreYApellidos = (TextView)findViewById(R.id.nombreEmpleado);
		puestoEmpleado = (TextView)findViewById(R.id.puestoEmpleado);
		dniEmpleado = (TextView)findViewById(R.id.dni);
		domicilioEmpleado = (TextView)findViewById(R.id.domicilioEmpleado);
		nacimientoEmpleado = (TextView)findViewById(R.id.nacimiento);
		
		nombreYApellidos.setText(nombre + " " + apellido1 + " " + apellido2);
		puestoEmpleado.setText(puesto);
		dniEmpleado.setText(dni);
		domicilioEmpleado.setText(domicilio);
		nacimientoEmpleado.setText(fechaNacimiento);
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
		
		botonCalendario = (Button)findViewById(R.id.botonCalendario);
		botonCalendario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				AlertDialog.Builder ventanaEmergenteCalendario = new AlertDialog.Builder(InfoEmpleado.this);
				ventanaEmergenteCalendario.setNegativeButton("Aceptar", null);
				
		        View vistaAviso = LayoutInflater.from(InfoEmpleado.this).inflate(R.layout.ventana_emergente_calendario, null);
				CalendarView calendario = (CalendarView)vistaAviso.findViewById(R.id.calendario);
				TextView encabezado = (TextView) vistaAviso.findViewById(R.id.tituloCalendario);
				encabezado.setText("Calendario");
				
				
				calendario.setFirstDayOfWeek(0x00000002);//Es el lunes el 0x00000002
				ventanaEmergenteCalendario.setView(vistaAviso);
				ventanaEmergenteCalendario.show();
				   
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
