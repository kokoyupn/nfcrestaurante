package fragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import adapters.ContenidoSpinnerHijo;
import adapters.MiSpinnerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import auxiliares.ObjetoAuxGrafica;
import baseDatos.HandlerGenerico;

import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;
import com.example.nfcook_gerente.InicializarInformacionRestaurante;
import com.example.nfcook_gerente.MultitouchPlot;
import com.example.nfcook_gerente.R;

/**
 * 
 * @author Guille
 * 
 * Esta clase implementará el fragment de los ingresos del restaurante.
 * Aqui se cargaran los datos en las gráficas y se hara la lectura de estos datos de base de datos.
 *
 */
public class IngresosFragment extends Fragment{
	private View vista;	
	private String tipo;
	public boolean esGeneral, modoZoomActivado, leyendaActivada;
	private String[] ids;
	private int dia,mes,anio;      
	private HandlerGenerico sqlIngresos;
	private SQLiteDatabase dbIngresos;
	private MultitouchPlot myMultitouchPlot;
	private ArrayList<String> spinnerAnio;
	private ArrayList<Integer> coloresUsados;
	private Number maximoGlobal;
	

	@SuppressLint("SimpleDateFormat")
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	
		vista = inflater.inflate(R.layout.grafica, container, false);
		
		//oculta el teclado que aparece automaticamente
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				
		//Leemos la info para conocer los ids de los restaurantes seleccionados
	    final Bundle bundleInfo = getActivity().getIntent().getExtras();
	    String stringIds = bundleInfo.getString("ids");
	    ids = stringIds.split(",");
	    esGeneral =  bundleInfo.getBoolean("esGeneral");
	    modoZoomActivado = false;
	    leyendaActivada = true;
	    coloresUsados = new ArrayList<Integer>();
	    maximoGlobal = 0;
	    
		abrirIngresosDB();
	    cargarSpinnerAnio();
	    
		//Por defecto se carga al año actual
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime(); 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formatteDate = df.format(date);
		dia = Integer.parseInt(formatteDate.substring(8, 10));
		mes = Integer.parseInt(formatteDate.substring(5, 7));
		anio = Integer.parseInt(formatteDate.substring(0, 4));
	    tipo = "Anio";
	    cargarAlAnio();

		//Establezco los oyentes de los botones
		Button alDia = (Button) vista.findViewById(R.id.botonPorDia);
		alDia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tipo = "Dia";
				abrirVentanaEmergente();
			}
		});
		
		Button alMes = (Button) vista.findViewById(R.id.botonPorMes);
		alMes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tipo= "Mes";
				abrirVentanaEmergente();
			}
		});
		
		Button alAnio = (Button) vista.findViewById(R.id.botonPorAnio);
		alAnio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tipo = "Anio";
				abrirVentanaEmergente();
			}
		}); 
		
		
		final ImageView zoom = (ImageView) vista.findViewById(R.id.zoomLock);
		zoom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//cambiamos el icono       
				String icono = null;
				if(leyendaActivada){
					myMultitouchPlot.getLegendWidget().setVisible(false);
					icono = "mostrar_leyenda";
				}else{
					myMultitouchPlot.getLegendWidget().setVisible(true);
					icono = "ocultar_leyenda";
				}
//				int img = getActivity().getResources().getIdentifier(icono,"drawable", getActivity().getPackageName());
//			    imgLeyenda.setImageResource(img);
				
				leyendaActivada = !leyendaActivada;
				
		        myMultitouchPlot.disableAllMarkup();
		        myMultitouchPlot.redraw();
			}
		});
		
		return vista;
	}	
	 

	private void cargarAlDia(){
		
		myMultitouchPlot = (MultitouchPlot) vista.findViewById(R.id.graficaIngresosGeneral);
		myMultitouchPlot.setRangeLabel("Euros");
		myMultitouchPlot.clear();
    	
		ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
		if(esGeneral){//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			temporal = cargarDatosDia(Integer.parseInt(ids[0]));
			
			Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data
			
			Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			ejeY[0] = 0;
		    Number [] ejeX = new Number[temporal.size()+1];
		    ejeX[0] = 0; 
		    for(int k = 0;k<temporal.size();k++){
		    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
		    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
		    }
		    				    
		    //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
		    //int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
		    int color = dameColorSinUsar();
	        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);
	        
	        //change the line width
	        Paint paint = formato.getLinePaint();
	        paint.setStrokeWidth(5);
	        formato.setLinePaint(paint);
		  
			 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
		    XYSeries series1 = new SimpleXYSeries(
	    			Arrays.asList(ejeX),
	                Arrays.asList(ejeY),  // Array de datos
	                "Ingresos globales"); // Nombre de la primera serie
			
		    myMultitouchPlot.addSeries(series1, formato);
			//EjeX
		    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
		    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
		    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
	        //EjeY
		    Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
		    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);			
			
		}else{ //Viene de comparar varios o uno solo
			for(int i = 0; i<ids.length; i++){ 
				temporal = cargarDatosDia(Integer.parseInt(ids[i]));
				
				Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data
				
				Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			    ejeY[0] = 0;
			    Number [] ejeX = new Number[temporal.size()+1];
			    ejeX[0] = 0;
			    for(int k = 0;k<temporal.size();k++){
			    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
			    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
			    }
			    			     
			    //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
			    int color = dameColorSinUsar();
		        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);
		        
		        //change the line width
		        Paint paint = formato.getLinePaint();
		        paint.setStrokeWidth(5);
		        formato.setLinePaint(paint);

				 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
			    XYSeries series1 = new SimpleXYSeries(
		    			Arrays.asList(ejeX),
		                Arrays.asList(ejeY),  // Array de datos
		                "Restaurante con Id: "+ ids[i]); // Nombre de la serie
				  
			    myMultitouchPlot.addSeries(series1, formato);
				//EjeX
			    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
			    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
			    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
		        //EjeY
			    Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
			    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
			}
		}
		        
		myMultitouchPlot.setDomainLabel("Dia");
		myMultitouchPlot.getTitleWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 170, SizeLayoutType.ABSOLUTE));
		myMultitouchPlot.setTitle("");
		
		dibujaLeyenda();
        
		myMultitouchPlot.disableAllMarkup();
		myMultitouchPlot.redraw();
		
		TextView fechaSeleccionada = (TextView)  vista.findViewById(R.id.textViewFechaSeleccionada);
		fechaSeleccionada.setText(dia + "-" + mes + "-" + anio);
	} 
	
	
	private void cargarAlMes(){
		myMultitouchPlot = (MultitouchPlot) vista.findViewById(R.id.graficaIngresosGeneral);
		myMultitouchPlot.setRangeLabel("Euros");
		myMultitouchPlot.clear();
    	
		ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
		
		if(esGeneral){//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			temporal = cargarDatosMes(Integer.parseInt(ids[0]));
			
			Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data

			Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			ejeY[0] = 0;
		    Number [] ejeX = new Number[temporal.size()+1];
		    ejeX[0] = 0;
		    for(int k = 0;k<temporal.size();k++){
		    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
		    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
		    }
		     
		    //Modificamos los colores de la serie color de linea, color de punto, relleno respectivamente
		    int color = dameColorSinUsar();
	        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);
	        
	        //change the line width
	        Paint paint = formato.getLinePaint();
	        paint.setStrokeWidth(5);
	        formato.setLinePaint(paint);
		  
			 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
		    XYSeries series1 = new SimpleXYSeries(
	    			Arrays.asList(ejeX),
	                Arrays.asList(ejeY),  // Array de datos
	                "Ingresos globales"); // Nombre de la primera serie
			
		    myMultitouchPlot.addSeries(series1, formato);
		    
			//EjeX
		    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
		    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
		    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
	        //EjeY
		    Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
		    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
	        
		}else{
			for(int i = 0; i<ids.length; i++){
				temporal = cargarDatosMes(Integer.parseInt(ids[i])); 
				
				Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data
				
				Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			    ejeY[0] = 0;
			    Number [] ejeX = new Number[temporal.size()+1];
			    ejeX[0] = 0;
			    for(int k = 0;k<temporal.size();k++){
			    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
			    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
			    }
			    		    
			    //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
			    int color = dameColorSinUsar();
		        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);
		        
		        //change the line width
		        Paint paint = formato.getLinePaint();
		        paint.setStrokeWidth(5);
		        formato.setLinePaint(paint);
		        
				 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
			    XYSeries series1 = new SimpleXYSeries(
		    			Arrays.asList(ejeX),
		                Arrays.asList(ejeY),  // Array de datos
		                "Restaurante con Id: "+ ids[i]); // Nombre de la serie
				
			    myMultitouchPlot.addSeries(series1, formato);	
			    
				//EjeX
			    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
			    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
			    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
		        //EjeY
			    Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
			    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
			}
		}
       
		myMultitouchPlot.setDomainLabel("Mes");
		myMultitouchPlot.getTitleWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 170, SizeLayoutType.ABSOLUTE));
		myMultitouchPlot.setTitle("");
        		
		dibujaLeyenda();
		
		myMultitouchPlot.disableAllMarkup();
		myMultitouchPlot.redraw();
		
		TextView fechaSeleccionada = (TextView)  vista.findViewById(R.id.textViewFechaSeleccionada);
		fechaSeleccionada.setText(mes + "-" + anio);
	} 
	

	private void cargarAlAnio(){
		myMultitouchPlot = (MultitouchPlot) vista.findViewById(R.id.graficaIngresosGeneral);
		myMultitouchPlot.setRangeLabel("Euros");
		 
		myMultitouchPlot.clear();
    	
		ArrayList<ObjetoAuxGrafica> temporal = new ArrayList<ObjetoAuxGrafica>();
		
		if(esGeneral){//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			temporal = cargarDatosAnio(Integer.parseInt(ids[0]));
			
			Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data

			Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			ejeY[0] = 0;
		    Number [] ejeX = new Number[temporal.size()+1];
		    ejeX[0] = 0;
		    for(int k = 0;k<temporal.size();k++){
		    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
		    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
		    }
		    
		    				    
		    //Modificamos los colores de la serie color de linea, color de punto, relleno respectivamente
		    int color = dameColorSinUsar();
	        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);		
	        
	        //change the line width
	        Paint paint = formato.getLinePaint();
	        paint.setStrokeWidth(5);
	        formato.setLinePaint(paint);
	        
			 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
		    XYSeries series1 = new SimpleXYSeries(
	    			Arrays.asList(ejeX),
	                Arrays.asList(ejeY),  // Array de datos
	                "Ingresos globales"); // Nombre de la serie
			
		    myMultitouchPlot.addSeries(series1, formato);	 
		    
			//EjeX
		    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
		    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
		    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
	        //EjeY
	       // Number rangoEjeY = (ejeY[ejeY.length-1]).intValue() + 5;//Con esto el ejeY llega toma como maximo valor, la mayor facturacion + 5 para que se vea bien
	        Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
		    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
		    
		    
		}else{
			for(int i = 0; i<ids.length; i++){
				temporal = cargarDatosAnio(Integer.parseInt(ids[i])); 
				
				Collections.sort(temporal);//Ordena el arrayList en funcion del campo "tiempo" del objeto que ocupa cada posicion // data
				
				Number [] ejeY = new Number[temporal.size()+1];//Para que empiece en el cero el eje Y
			    ejeY[0] = 0;
			    Number [] ejeX = new Number[temporal.size()+1];
			    ejeX[0] = 0;
			    for(int k = 0;k<temporal.size();k++){
			    	ejeY[k+1] = temporal.get(k).getImporte();// = {4,10,11,7,13,3,8,16,36,18,19,23,55,1,9};//Son los valores del eje Y
			    	ejeX[k+1] = Double.parseDouble(temporal.get(k).getTiempo());
			    }
			    				    
			    //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
			    int color = dameColorSinUsar();
		        LineAndPointFormatter formato = new LineAndPointFormatter(color,  Color.BLACK, null);
		        
		        //change the line width
		        Paint paint = formato.getLinePaint();
		        paint.setStrokeWidth(5);
		        formato.setLinePaint(paint);
		         
				 //En el eje X pone tantos valores como le metas al eje Y, en este caso de 9h a 23h
			    XYSeries series1 = new SimpleXYSeries(
		    			Arrays.asList(ejeX),
		                Arrays.asList(ejeY),  // Array de datos
		                "Restaurante con Id: "+ ids[i]); // Nombre de la serie
				
			    myMultitouchPlot.addSeries(series1, formato);
			    
				//EjeX
			    myMultitouchPlot.setDomainValueFormat(new DecimalFormat("0"));//Para que las horas(ejeX) las escriba sin decimales
			    myMultitouchPlot.setDomainStep(XYStepMode.SUBDIVIDE, temporal.size()+1);//Pone tantos valores en el eje X como elementos tenga el array de meses +1 para el cero
			    myMultitouchPlot.setDomainBoundaries(0,ejeX[ejeX.length-1] , BoundaryMode.FIXED);
		        //EjeY
			    Number rangoEjeY = (calculaMaximo(ejeY).intValue() + 5);
			    myMultitouchPlot.setRangeBoundaries(0, rangoEjeY , BoundaryMode.AUTO);
			    	
			} 
		} 
		        
        myMultitouchPlot.setDomainLabel("Año");
        myMultitouchPlot.getTitleWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 270, SizeLayoutType.ABSOLUTE));
        myMultitouchPlot.setTitle("");
        
        dibujaLeyenda();
        
        myMultitouchPlot.disableAllMarkup();
        myMultitouchPlot.redraw();
        
        TextView fechaSeleccionada = (TextView)  vista.findViewById(R.id.textViewFechaSeleccionada);
		fechaSeleccionada.setText(anio+"");	
	}	
	  

	
	@SuppressLint("UseSparseArrays")
	public ArrayList<ObjetoAuxGrafica> cargarDatosDia(int idRestaurante){//buscamos en la bd el dia mes y año. importe por horas
		ArrayList<ObjetoAuxGrafica> registros = new ArrayList<ObjetoAuxGrafica>();
		
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Importe","Hora"};
				String[] datos = new String[]{dia+"",mes+"",anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Dia=? AND Mes=? AND Anio=? AND IdRestaurante=?", datos, null, null,null);

//				while(c.moveToNext()){
//					String hora = c.getInt(1)+"";
//					registros.add(new ObjetoAuxGrafica(hora.substring(0, 2), c.getInt(0)+""));
//				}
			}else{
				String[] campos = new String[]{"Importe","Hora"};
				String[] datos = new String[]{dia+"",mes+"",anio+""};
				c = dbIngresos.query("Ingresos", campos, "Dia=? AND Mes=? AND Anio=?", datos, null, null,null);
			}
			
			    Map<Integer,Integer> temporal = null;//La clave es la hora y el valor el importe
				if(c.getCount()>0){
					temporal = new HashMap<Integer,Integer>();
					}
				while(c.moveToNext()){
					String hora = (c.getInt(1)+"").substring(0, 2);//parseamos para sacar la hora
					//Si no esta ya en el HashMap lo metemos, si no lo sumamos
					if(!temporal.containsKey(Integer.parseInt(hora))){
						temporal.put(Integer.parseInt(hora), c.getInt(0));
					}else{
						int aux = temporal.get(c.getInt(1));
						temporal.put(Integer.parseInt(hora), c.getInt(0) + aux);
					}
				}//End del while

				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				while(itClave.hasNext()){
					int clave = itClave.next();
					registros.add(new ObjetoAuxGrafica(clave+"", temporal.get(clave)+"")); 
				}
			//}
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}	
				
		return registros;	
	}
	
	
	@SuppressLint("UseSparseArrays")
	public ArrayList<ObjetoAuxGrafica> cargarDatosMes(int idRestaurante){//buscamos en la bd el mes y año. sumar el importe por dias
		ArrayList<ObjetoAuxGrafica> registros = new ArrayList<ObjetoAuxGrafica>();
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Dia","Importe"};
				String[] datos = new String[]{mes+"",anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Mes=? AND Anio=? AND IdRestaurante=?", datos, null, null,"Dia");
				
				int antDia = 0;
				int sumaDia = 0;
				while(c.moveToNext()){
					int auxDia = c.getInt(0);
					int auxImporte = c.getInt(1);
					if(auxDia != antDia){//si es desigual que el anterior metemos el que llevamos si no es el primero y creamos uno nuevo
						if(antDia == 0){//Es el primero
							sumaDia += auxImporte;
						}else{ 
							registros.add(new ObjetoAuxGrafica(antDia+"", sumaDia+""));
							sumaDia = auxImporte;
						}
					}else{
						sumaDia += auxImporte;
					}
					antDia = auxDia;
				}
				if(c.getCount()>0)
					//ponemos el ultimo
					registros.add(new ObjetoAuxGrafica(antDia+"", sumaDia+""));
			}else{//Es general
				String[] campos = new String[]{"Dia","Importe"};
				String[] datos = new String[]{mes+"",anio+""};
				c = dbIngresos.query("Ingresos", campos, "Mes=? AND Anio=?", datos, null, null,"Dia");
				
				Map<Integer,Integer> temporal = null;//La clave es el dia y el valor el importe
				if(c.getCount()>0)
					temporal = new HashMap<Integer, Integer>();
				int antDia = 0;
				int sumaDia = 0;
				while(c.moveToNext()){
					int auxDia = c.getInt(0);
					int auxImporte = c.getInt(1);
					if(auxDia != antDia){//si es desigual que el anterior metemos el que llevamos si no es el primero y creamos uno nuevo
						if(antDia == 0){//Es el primero
							sumaDia += auxImporte;
						}else{ 
							if(!temporal.containsKey(c.getInt(1))){
								temporal.put(antDia, sumaDia);
							}else{
								int aux = temporal.get(c.getInt(1));
								temporal.put(antDia, sumaDia + aux);
							}
							sumaDia = auxImporte;
						}
					}else{
						sumaDia += auxImporte;
					}
					antDia = auxDia;
				}
				if(c.getCount()>0){
					//ponemos el ultimo
					temporal.put(antDia, sumaDia);
				}
				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				while(itClave.hasNext()){
					int clave = itClave.next();
					registros.add(new ObjetoAuxGrafica(clave+"", temporal.get(clave)+""));
				}
			}
	   
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}
		return registros;
	}
	
	
	@SuppressLint("UseSparseArrays")
	public ArrayList<ObjetoAuxGrafica> cargarDatosAnio(int idRestaurante){//buscamos en la bd el año. sumar el importe por meses
		ArrayList<ObjetoAuxGrafica> registros = new ArrayList<ObjetoAuxGrafica>();
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Mes","Importe"};
				String[] datos = new String[]{anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Anio=? AND IdRestaurante=?", datos, null, null,"Mes");	

				int antMes = 0;
				while(c.moveToNext()){
					int actMes = c.getInt(0);
					if(actMes != antMes){
						registros.add(new ObjetoAuxGrafica(actMes+"", calculaIngeresosDelMes(actMes,idRestaurante)+""));
					}
					antMes = actMes;
				}
			}else{
				String[] campos = new String[]{"Mes","Importe"};
				String[] datos = new String[]{anio+""};
				c = dbIngresos.query("Ingresos", campos, "Anio=?", datos, null, null,"Mes");	
				
				Map<Integer,Integer> temporal = null;//La clave es el mes y el valor el importe 
				if(c.getCount()>0)
					temporal = new HashMap<Integer, Integer>();
				int antMes = 0;
				while(c.moveToNext()){
					int actMes = c.getInt(0);
					if(actMes != antMes){
						//Dentro del metodo preguntamos si es general y no tenemos en cuenta el idRestaurante 
						int ingrMes = calculaIngeresosDelMes(actMes,0);
						temporal.put(actMes, ingrMes);
					}
					antMes = actMes;
				}

				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				while(itClave.hasNext()){
					int clave = itClave.next();
					registros.add(new ObjetoAuxGrafica(clave+"", temporal.get(clave)+"")); 
				}
				
			}
		}catch(Exception e){ 
			System.out.println("Error en la carga de Ingresos");
		}   
		    
		return registros;  
	} 
	    
	private void abrirVentanaEmergente() {
		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(getActivity());
  		 
  		View vistaAviso = null;
		if(tipo.equals("Dia")){
			//Elegimos la vista con los tres spinners
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_dia_mes_anio, null);
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerDia = (Spinner) vistaAviso.findViewById(R.id.spinner_dia_dma);
	 
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerDias = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 1; i<=31; i++){ 
				datosSpinnerDias.add(new  ContenidoSpinnerHijo(i));
			}
			MiSpinnerAdapter adapterDia = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerDias); 
			// Apply the adapter to the spinner
			spinnerDia.setAdapter(adapterDia);
			//Ponemos dor defecto el dia en que se usa
			spinnerDia.setSelection(dia-1,true);
			spinnerDia.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					dia =  ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerMes = (Spinner) vistaAviso.findViewById(R.id.spinner_mes_dma);
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerMeses = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 1; i<=12; i++){ 
				datosSpinnerMeses.add(new ContenidoSpinnerHijo(i));
			}
			MiSpinnerAdapter adapterMes = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerMeses); 
			// Apply the adapter to the spinner
			spinnerMes.setAdapter(adapterMes);
			spinnerMes.setSelection(mes-1,true);
			spinnerMes.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					mes = ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			 
			//Creamos el spinner del año con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_dma);
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerAnios = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 0; i<this.spinnerAnio.size(); i++){ 
				datosSpinnerAnios.add(new ContenidoSpinnerHijo(Integer.parseInt(this.spinnerAnio.get(i))));
			}
			MiSpinnerAdapter adapterAnio = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerAnios); 
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			//No le ponemos el setSelected porque lo va leyendo de la bas de datos y siempre va a poner el ultimo año el primero
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			
		}else if (tipo.equals("Mes")){
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_mes_anio, null);
			
			//Creamos el spinner del mes con sus opciones
			Spinner spinnerMes = (Spinner) vistaAviso.findViewById(R.id.spinner_mes_ma);
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerMeses = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 1; i<=12; i++){ 
				datosSpinnerMeses.add(new ContenidoSpinnerHijo(i));
			}
			MiSpinnerAdapter adapterMes = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerMeses); 
			// Apply the adapter to the spinner
			spinnerMes.setAdapter(adapterMes);
			spinnerMes.setSelection(mes-1,true);
			spinnerMes.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					mes = ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			  
			//Creamos el spinner del año con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_ma);
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerAnios = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 0; i<this.spinnerAnio.size(); i++){ 
				datosSpinnerAnios.add(new ContenidoSpinnerHijo(Integer.parseInt(this.spinnerAnio.get(i))));
			}
			MiSpinnerAdapter adapterAnio = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerAnios); 
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
		}else{
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_anio, null);
			
			//Creamos el spinner del año con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_a);
			//datos a mostrar
	        ArrayList<ContenidoSpinnerHijo> datosSpinnerAnios = new ArrayList<ContenidoSpinnerHijo>();
			for(int i = 0; i<this.spinnerAnio.size(); i++){ 
				datosSpinnerAnios.add(new ContenidoSpinnerHijo(Integer.parseInt(this.spinnerAnio.get(i))));
			}
			MiSpinnerAdapter adapterAnio = new MiSpinnerAdapter(ventanaEmergente.getContext(),datosSpinnerAnios); 
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = ((ContenidoSpinnerHijo) parentView.getItemAtPosition(position)).getDato();
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}	
		
		
		ventanaEmergente.setNegativeButton("Cancelar", null);
		ventanaEmergente.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		
			public void onClick(DialogInterface dialog, int which) {
				//Reseteamos el maxmo global
			    maximoGlobal = 0;

				if(tipo.equals("Dia")){
					cargarAlDia();
				}else if(tipo.equals("Mes")){
						cargarAlMes();
					}else{
						cargarAlAnio();
					}
			}		
		});
		
		ventanaEmergente.setView(vistaAviso);
		ventanaEmergente.show();
	}
	
	public void abrirIngresosDB(){
		//Importamos la base de datos
		try {
			sqlIngresos = new HandlerGenerico(getActivity(),
					"/data/data/com.example.nfcook_gerente/databases/",
					"Ingresos.db");
			dbIngresos = sqlIngresos.open();
		} catch (SQLiteException e) {
			System.out.println("CATCH");
			Toast.makeText(getActivity(),
					"NO EXISTE LA BASE DE DATOS", Toast.LENGTH_SHORT).show(); 
		}
}
	
	public int calculaIngeresosDelMes(int mes, int idRest){
		int importe = 0;
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Importe"};
				String[] datos = new String[]{mes+"", anio+"", idRest+""};
				Cursor c = dbIngresos.query("Ingresos", campos, "Mes=? AND Anio=? AND IdRestaurante=?", datos, null, null,null);
		
				while(c.moveToNext()){
					importe += c.getInt(0);
				}
			}else{
				String[] campos = new String[]{"Importe"};
				String[] datos = new String[]{mes+"", anio+""};
				Cursor c = dbIngresos.query("Ingresos", campos, "Mes=? AND Anio=?", datos, null, null,null);
		
				while(c.moveToNext()){
					importe += c.getInt(0);
				}
			}
	   
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}
		return importe;
	}
	
	public void cargarSpinnerAnio(){
		//Se carga el spinner de los anios que tienes recogidos en la base de datos
		String[] campos = new String[]{"Anio"};
		Cursor c = dbIngresos.query("Ingresos",campos, null, null, null,null, null);
		spinnerAnio = new ArrayList<String>();
		while(c.moveToNext()){
			if( ! spinnerAnio.contains(c.getString(0)))
				spinnerAnio.add(c.getString(0));	    	
	    }
	}
	
	@SuppressLint("NewApi")
	public void dibujaLeyenda(){ 
		//obtenemos el ancho de la pantalla para luego calcular el tamaño de la leyenda
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int ancho = size.x - 200;//200 de margenes
		float columnas = ancho/250;
		float filas;
		if((ids.length % columnas) == 0)
			   filas = ids.length / columnas;
		  else
			   filas = (ids.length / columnas) +1;
		
        myMultitouchPlot.getGraphWidget().getDomainLabelPaint().setTextSize(20);
        myMultitouchPlot.getGraphWidget().getRangeLabelPaint().setTextSize(20);
        myMultitouchPlot.getGraphWidget().setDomainLabelWidth(20);
        //myMultitouchPlot.getGraphWidget().setRangeLabelWidth(myMultitouchPlot.getGraphWidget().getRangeLabelWidth()*2);
        myMultitouchPlot.getGraphWidget().setPadding(20, 20, 20, 20);
        
        //Usamos un grid:
        if(ids.length>=columnas || esGeneral)
        	myMultitouchPlot.getLegendWidget().setTableModel(new DynamicTableModel((int)columnas,(int)filas));

        //ajustamos la leyenda
        if (esGeneral){
        	myMultitouchPlot.getLegendWidget().setSize(new SizeMetrics(40, SizeLayoutType.ABSOLUTE, 250, SizeLayoutType.ABSOLUTE));
        }else if(ids.length<columnas){
        	myMultitouchPlot.getLegendWidget().setSize(new SizeMetrics(40, SizeLayoutType.ABSOLUTE, ids.length * 250, SizeLayoutType.ABSOLUTE));
        }else{
    		myMultitouchPlot.getLegendWidget().setSize(new SizeMetrics(filas*40, SizeLayoutType.ABSOLUTE, columnas * 250, SizeLayoutType.ABSOLUTE));
        }
        
        //Añadimos un fondo semitransparente
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(100);
        myMultitouchPlot.getLegendWidget().setBackgroundPaint(bgPaint);
        
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        myMultitouchPlot.getLegendWidget().setTextPaint(paint);
        myMultitouchPlot.getLegendWidget().setPadding(5, 1, 1, 1);       

    	//Recolocamos el grid
        myMultitouchPlot.position(myMultitouchPlot.getLegendWidget(),
        		40,
        		XLayoutStyle.ABSOLUTE_FROM_RIGHT,
        		80,
        		YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
        		AnchorPosition.RIGHT_BOTTOM);
	}
	
	//Se usa para ver el maximo valor de la "y" en la grafica. 
	//Hay que comparar con el maximo global por si se estan comparando varias
	public Number calculaMaximo(Number[] lista){
		Number maximo = 0;
		for(int i = 0; i < lista.length; i++){
			if((lista[i].floatValue() >= maximo.floatValue()))
				maximo = lista[i];
		}
		if(maximo.floatValue() >= maximoGlobal.floatValue())
			maximoGlobal = maximo;
		return maximoGlobal;
	}
	
	public int dameColorSinUsar(){
		Random rnd = new Random();				    
	    //Modificamos los colores de la primera serie color de linea, color de punto, relleno respectivamente
	    int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	    while(estaRepetido(color)){
	    	color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	    }
	    //Añadimos este color a la lista
	    coloresUsados.add(color);
		return color;
	}


	private boolean estaRepetido(int color) {
		//recorremos la lista de colores para ver si esta repetido
		for(int i=0; i< coloresUsados.size(); i++){
			if(coloresUsados.get(i)== color) return true;
		}
		return false;
	}
	
}
