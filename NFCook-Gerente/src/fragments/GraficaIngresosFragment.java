package fragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import adapters.ContenidoSpinnerHijo;
import adapters.MiSpinnerAdapter;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import auxiliares.ObjetoAuxGrafica;
import baseDatos.HandlerGenerico;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;
import com.example.nfcook_gerente.InicializarDatosEmpleado;
import com.example.nfcook_gerente.InicializarInformacionRestaurante;
import com.example.nfcook_gerente.MultitouchPlot;
import com.example.nfcook_gerente.R;

public class GraficaIngresosFragment extends Fragment{
	private View vista;	
	private MultitouchPlot mySimpleXYPlot;
	private HandlerGenerico sqlEmpleados,sqlEmpleado,sqlFicha,sqlIngresos;
	private SQLiteDatabase dbEmpleados,dbEmpleado,dbFicha,dbIngresos;


	private String idEmpleado,diaElegido,mesElegido,anioElegido;
	 
	private Button dibDia, dibMes, dibAnio;
	private ArrayList<ContenidoSpinnerHijo> spinnerAnio,spinnerDia,spinnerMes;
	private TextView periodoElegido;

	private boolean modoZoomActivado, leyendaActivada;
	
	
	
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.grafica, container, false);
		
		//oculta el teclado que aparece automaticamente
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		Bundle bundle = getActivity().getIntent().getExtras();
		idEmpleado = bundle.getString("IdEmpleado");
		
		importarBaseDatos();
		dibujarGrafica();
		
		//Cargamos la grafica inicialmente por el año actual
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formatteDate = df.format(date);
		
		modoZoomActivado = false;
		leyendaActivada = true;
		diaElegido = formatteDate.substring(8, 10);
		mesElegido = formatteDate.substring(5, 7);
		anioElegido = formatteDate.substring(0, 4);
		cargaInicial();
		
		//Establecemos los oyentes
		final ImageView zoom = (ImageView) vista.findViewById(R.id.zoomLock);
		zoom.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {	
				InicializarDatosEmpleado.pausaViewPager();
				if (modoZoomActivado)	
					mySimpleXYPlot.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return false;
						}
					});
				else mySimpleXYPlot.initTouchHandling();
				modoZoomActivado = !modoZoomActivado;
				
				//cambiamos el icono
				String icono = null;
				if(modoZoomActivado)
					icono = "zoom_unlock";
				else 
					icono = "zoom_lock";
				int img = getResources().getIdentifier(icono,"drawable", getActivity().getPackageName());
			    zoom.setImageResource(img);
			}      
		}); 
		
		
		final ImageView imgLeyenda = (ImageView) vista.findViewById(R.id.ocultaLeyenda);
		imgLeyenda.setOnClickListener(new OnClickListener() { 
			
			@Override
			public void onClick(View v) {
				//cambiamos el icono       
				String icono = null;
				if(leyendaActivada){
					mySimpleXYPlot.getLegendWidget().setVisible(false);
					icono = "mostrar_leyenda";
				}else{
					mySimpleXYPlot.getLegendWidget().setVisible(true);
					icono = "ocultar_leyenda";
				}
				int img = getResources().getIdentifier(icono,"drawable", getActivity().getPackageName());
			    imgLeyenda.setImageResource(img);
				
				leyendaActivada = !leyendaActivada;
				
				mySimpleXYPlot.disableAllMarkup();
				mySimpleXYPlot.redraw();
			}
		});
		
		
		// Cambiamos el fondo al ActionBar
		getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#004400")));
		getActivity().getActionBar().setTitle(" DATOS EMPLEADO");
		
		return vista;
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
			//Ponemos dor defecto el dia en que se usa
			selDia.setSelection(Integer.parseInt(diaElegido)-1,true);
			
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
			selMes.setSelection(Integer.parseInt(mesElegido)-1,true);

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
    		        
    		    formatoGrafica(grafica,temporal,"Hora");
    		    
                periodoElegido = (TextView) vista.findViewById(R.id.textViewFechaSeleccionada);
                periodoElegido.setText(diaElegido + "-" + mesElegido + "-"+ anioElegido);
                		
		
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

    		    formatoGrafica(grafica,temporal,"Dia");
    		    
    		    periodoElegido = (TextView) vista.findViewById(R.id.textViewFechaSeleccionada);
                periodoElegido.setText(mesElegido + "-"+ anioElegido);
		
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
    		    
    		    formatoGrafica(grafica,temporal,"Mes");
    		    
    		    periodoElegido = (TextView) vista.findViewById(R.id.textViewFechaSeleccionada);
                periodoElegido.setText(anioElegido);
		
			}
		});	
	}
	
	public void formatoGrafica(final MultitouchPlot grafica, ArrayList<ObjetoAuxGrafica> temporal,String periodo){
		
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
                "Facturación"); // Nombre de la primera serie
                
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
        Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
        grafica.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
        
        grafica.setDomainLabel(periodo);
        grafica.getTitleWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 270, SizeLayoutType.ABSOLUTE));
        grafica.setTitle("");
        
        dibujaLeyenda(grafica);
        
        grafica.disableAllMarkup();
        grafica.redraw();
   
	}
	 
	public void dibujarGrafica(){
		
		mySimpleXYPlot = (MultitouchPlot) vista.findViewById(R.id.graficaIngresosGeneral);

		
		mySimpleXYPlot.setRangeLabel("Euros");
		
		dibDia = (Button)vista.findViewById(R.id.botonPorDia);
		dibMes = (Button)vista.findViewById(R.id.botonPorMes);
		dibAnio = (Button)vista.findViewById(R.id.botonPorAnio);
		
		dibDia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(getActivity());
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	View vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_dia_mes_anio, null);
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
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(getActivity());
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	
            	View vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_mes_anio, null);
				Spinner selMes = (Spinner)vistaAviso.findViewById(R.id.spinner_mes_ma);
				Spinner selAnio = (Spinner)vistaAviso.findViewById(R.id.spinner_anio_ma);
				ventanaEmergenteSpinners.setView(vistaAviso);
				cargarSpinners("mes",null,selMes,selAnio,ventanaEmergenteSpinners);
				onClickBotonAceptarAlertDialogMes(mySimpleXYPlot,ventanaEmergenteSpinners);
				ventanaEmergenteSpinners.show();
				
           }});
		dibAnio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	AlertDialog.Builder ventanaEmergenteSpinners = new AlertDialog.Builder(getActivity());
            	ventanaEmergenteSpinners.setNegativeButton("Cancelar", null);
            	
            	View vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_anio, null);
				Spinner selAnio = (Spinner)vistaAviso.findViewById(R.id.spinner_anio_a);
				ventanaEmergenteSpinners.setView(vistaAviso);
				cargarSpinners("anio",null,null,selAnio,ventanaEmergenteSpinners);
				onClickBotonAceptarAlertDialogAnio(mySimpleXYPlot,ventanaEmergenteSpinners);
				ventanaEmergenteSpinners.show();
				
            }});
		
	}   	



	
	public void importarBaseDatos(){
		 try{
				sqlEmpleados = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleados.db");
				dbEmpleados = sqlEmpleados.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH EMPLEADOS");
				Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlEmpleado = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleado.db");
				dbEmpleado = sqlEmpleado.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH EMPLEADO");
				Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlFicha = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Ficha.db");
				dbFicha = sqlFicha.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH Ficha");
				Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
		 try{
				sqlIngresos = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Ingresos.db");
				dbIngresos = sqlIngresos.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH Ingresos");
				Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		 }
	}
	
	
	
	//Se usa para ver el maximo valor de la "y" en la grafica. 
	public Number calculaMaximo(Number[] lista){
		Number maximo = 0;
		for(int i = 0; i < lista.length; i++){
			if((lista[i].floatValue() >= maximo.floatValue()))
				maximo = lista[i];
		}
		return maximo;
	}
	
	
	public void dibujaLeyenda(MultitouchPlot myMultitouchPlot){ 
		//aumentamos la fuente de los ejes
        myMultitouchPlot.getGraphWidget().getDomainLabelPaint().setTextSize(20);
        myMultitouchPlot.getGraphWidget().getRangeLabelPaint().setTextSize(20);
        myMultitouchPlot.getGraphWidget().setDomainLabelWidth(20);
        //myMultitouchPlot.getGraphWidget().setRangeLabelWidth(myMultitouchPlot.getGraphWidget().getRangeLabelWidth()*2);
        myMultitouchPlot.getGraphWidget().setPadding(20, 20, 20, 20);
        
        myMultitouchPlot.getLegendWidget().setSize(new SizeMetrics(40, SizeLayoutType.ABSOLUTE, 200, SizeLayoutType.ABSOLUTE));
       
        //Cambiamos el color de la leyeda a blanco y su tamaño
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        myMultitouchPlot.getLegendWidget().setTextPaint(paint);
        myMultitouchPlot.getLegendWidget().setPadding(5, 1, 1, 1);      
        
      //Añadimos un fondo semitransparente
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(100);
        myMultitouchPlot.getLegendWidget().setBackgroundPaint(bgPaint);
          

    	//Recolocamos el grid
        myMultitouchPlot.position(myMultitouchPlot.getLegendWidget(),
        		40,
        		XLayoutStyle.ABSOLUTE_FROM_RIGHT,
        		80,
        		YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
        		AnchorPosition.RIGHT_BOTTOM);
	}
	
	
	private void cargaInicial() {
		MultitouchPlot grafica = (MultitouchPlot) vista.findViewById(R.id.graficaIngresosGeneral);
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
	    
	    formatoGrafica(grafica,temporal,"Mes");
	    
	    periodoElegido = (TextView) vista.findViewById(R.id.textViewFechaSeleccionada);
        periodoElegido.setText(anioElegido);
		
	}
		
}

