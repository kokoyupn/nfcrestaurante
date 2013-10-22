package fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.example.nfcook_gerente.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

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
	public boolean esGeneral;
	private String[] ids;
	private int dia,mes,anio;
	private HandlerGenerico sqlIngresos;
	private SQLiteDatabase dbIngresos;
	
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		
		vista = inflater.inflate(R.layout.grafica, container, false);
		
		//Leemos la info para conocer los ids de los restaurantes seleccionados
	    Bundle bundleInfo = getActivity().getIntent().getExtras();
	    String stringIds = bundleInfo.getString("ids");
	    ids = stringIds.split(",");
	    esGeneral =  bundleInfo.getBoolean("esGeneral");
		 
		//Por defecto se carga al año actual
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formatteDate = df.format(date);
		anio = Integer.parseInt(formatteDate.substring(0, 4));
	    tipo = "Anio";
	    cargarAlAnio();	    

		//Establezco los oyentes de los botones
		ImageView alDia = (ImageView) vista.findViewById(R.id.botonPorDia);
		alDia.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				tipo = "Dia";
				abrirVentanaEmergente();
			}
		});
		
		ImageView alMes = (ImageView) vista.findViewById(R.id.botonPorMes);
		alMes.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				tipo= "Mes";
				abrirVentanaEmergente();
			}
		});
		
		ImageView alAnio = (ImageView) vista.findViewById(R.id.botonPorAnio);
		alAnio.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				tipo = "Anio";
				abrirVentanaEmergente();
			}
		}); 
		return vista;
	}	
	

	private void cargarAlDia(){
		//buscamos la zona donde va la gráfica 
		LinearLayout layout = (LinearLayout) vista.findViewById(R.id.contenedor);
		//Borramos la anterior vista
		layout.removeAllViews();

		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new LineGraphView(this.getActivity().getApplicationContext(), "Al día"); 
		
//		//Sombreamos por debajo del gráfico
//		graphView.setBackgroundColor(Color.CYAN); 
//		((LineGraphView) graphView).setDrawBackground(true);

		
//		// set view port, start=0, size=10
//		graphView.setViewPort(1, 24);
//		graphView.setScrollable(true);
//		// optional - activate scaling / zooming
//		graphView.setScalable(true);
	
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setVerticalLabelsWidth((int) (graphView.getGraphViewStyle().getTextSize()*3.5)); 
//	    graphView.getGraphViewStyle().setNumHorizontalLabels(24);
		
		if(!esGeneral){
			//Añdir la leyenda
			graphView.setShowLegend(true);
			graphView.setLegendAlign(LegendAlign.BOTTOM);  
			graphView.setLegendWidth(200);
		}
		
		GraphViewSeries datos = null;
		if(esGeneral)//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			datos = cargarDatosDia(Integer.parseInt(ids[0]));
		else{
			for(int i = 0; i<ids.length; i++){ 
				datos = cargarDatosDia(Integer.parseInt(ids[i]));
				if(datos != null)
					graphView.addSeries(datos); // data
			}
		}
		
	    //Añadimos el grafico con los datos nuevos y refrescamos
		layout.addView(graphView);
		layout.refreshDrawableState();
	}
	
	
	private void cargarAlMes(){
		//buscamos la zona donde va la gráfica
		LinearLayout layout = (LinearLayout) vista.findViewById(R.id.contenedor);
		//Borramos la anterior vista
		layout.removeAllViews();
		
		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new LineGraphView(this.getActivity().getApplicationContext(), "Al mes"); 
		
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//	    graphView.getGraphViewStyle().setNumHorizontalLabels(30); 
		
		//Cambiamos el color del fondo
		graphView.setBackgroundColor(Color.GRAY);
		
		if(!esGeneral){
			//Añdir la leyenda
			graphView.setShowLegend(true);
			graphView.setLegendAlign(LegendAlign.BOTTOM);  
			graphView.setLegendWidth(200);
		}
		
		GraphViewSeries datos = null;
		if(esGeneral){//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			datos = cargarDatosMes(Integer.parseInt(ids[0]));
			if(datos != null)
				graphView.addSeries(datos); // data 
		}else{
			for(int i = 0; i<ids.length; i++){
				datos = cargarDatosMes(Integer.parseInt(ids[i])); 
				if(datos != null)
					graphView.addSeries(datos); // data 
			}
		}
		
	    //Añadimos el grafico con los datos nuevos y refrescamos
		layout.addView(graphView);
		layout.refreshDrawableState();
		
	}
	

	private void cargarAlAnio(){
		//buscamos la zona donde va la gráfica 
		LinearLayout layout = (LinearLayout) vista.findViewById(R.id.contenedor);
		//Borramos la anterior vista
		layout.removeAllViews();

		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new LineGraphView(this.getActivity().getApplicationContext(), "Al año"); 
	
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(30);
//		graphView.getGraphViewStyle().setNumHorizontalLabels(12);
	
		//Modificamos los ejes
//		graphView.setHorizontalLabels(new String[] {  "E", "F", "M", "A"
//													, "M", "J", "J", "A"
//													, "S", "O", "N", "D"});  
		
		//Cambiamos el color del fondo
		graphView.setBackgroundColor(Color.GREEN);
		//((LineGraphView) graphView).setDrawBackground(true);
		
		if(!esGeneral){
			//Añdir la leyenda
			graphView.setShowLegend(true);
			graphView.setLegendAlign(LegendAlign.BOTTOM);  
			graphView.setLegendWidth(200);
		}
		
		GraphViewSeries datos = null;
		if(esGeneral){//Si es el boton de todo no tiene en cuenta el id asi que le pasamos el primero
			datos = cargarDatosAnio(Integer.parseInt(ids[0]));
			if(datos != null)
				graphView.addSeries(datos); // data 
		}else{
			for(int i = 0; i<ids.length; i++){
				datos = cargarDatosAnio(Integer.parseInt(ids[i])); 
				if(datos != null)
					graphView.addSeries(datos); // data 
			}
		}
		
		
	    //Añadimos el grafico con los datos nuevos y refrescamos
		layout.addView(graphView);
		layout.refreshDrawableState();
	}	
	

	
	public GraphViewSeries cargarDatosDia(int idRestaurante){//buscamos en la bd el dia mes y año. importe por horas

		abrirIngresosDB();
		GraphViewData[] registros = null;
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Importe","Hora"};
				String[] datos = new String[]{dia+"",mes+"",anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Dia=? AND Mes=? AND Anio=? AND IdRestaurante=?", datos, null, null,null);
			    
				if(c.getCount()>0)
					registros = new GraphViewData[c.getCount()];
				int i = 0;
				while(c.moveToNext()){
					registros[i] = new GraphViewData(c.getInt(1), c.getInt(0));
					i++;
				}
			}else{
				String[] campos = new String[]{"Importe","Hora"};
				String[] datos = new String[]{dia+"",mes+"",anio+""};
				c = dbIngresos.query("Ingresos", campos, "Dia=? AND Mes=? AND Anio=?", datos, null, null,null);
				
			    Map<Integer,Integer> temporal = null;//La clave es la hora y el valor el importe
				if(c.getCount()>0){
					temporal = new HashMap<Integer,Integer>();
					}
				while(c.moveToNext()){
					//Si no esta ya en el HashMap lo metemos, si no lo sumamos
					if(!temporal.containsKey(c.getInt(1))){
						temporal.put(c.getInt(1), c.getInt(0));
					}else{
						int aux = temporal.get(c.getInt(1));
						temporal.put(c.getInt(1), c.getInt(0) + aux);
					}
				}//End del while
				if(c.getCount()>0)
					registros = new GraphViewData[temporal.size()];
				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				int i = 0;
				while(itClave.hasNext()){
					int clave = (Integer) itClave.next();
					registros[i] = new GraphViewData(clave, temporal.get(clave));
					i++;
				}
			}
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}
		
		Random rnd = new Random();
		if(c.getCount()>0)
			return new GraphViewSeries(idRestaurante+"", new GraphViewSeriesStyle(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), 5), registros);
		else return null;
	}
	
	
	public GraphViewSeries cargarDatosMes(int idRestaurante){//buscamos en la bd el mes y año. sumar el importe por dias
		abrirIngresosDB();
		GraphViewData[] registros = null;
		GraphViewData[] registrosFinal =null; //si no esta ese restaurante devueve null
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Dia","Importe"};
				String[] datos = new String[]{mes+"",anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Mes=? AND Anio=? AND IdRestaurante=?", datos, null, null,"Dia");
				
				if(c.getCount()>0)
					registros = new GraphViewData[c.getCount()];
				int antDia = 0;
				int sumaDia = 0;
				int i = 0;
				while(c.moveToNext()){
					int auxDia = c.getInt(0);
					int auxImporte = c.getInt(1);
					if(auxDia != antDia){//si es desigual que el anterior metemos el que llevamos si no es el primero y creamos uno nuevo
						if(antDia == 0){//Es el primero
							sumaDia += auxImporte;
						}else{ 
							registros[i] = new GraphViewData(antDia, sumaDia);
							sumaDia = auxImporte;
							i++;
						}
					}else{
						sumaDia += auxImporte;
					}
					antDia = auxDia;
				}
				if(c.getCount()>0)
					//ponemos el ultimo
					registros[i] = new GraphViewData(antDia, sumaDia);
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
					registrosFinal = new GraphViewData[temporal.size()];
				}
				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				int j = 0;
				while(itClave.hasNext()){
					int clave = (Integer) itClave.next();
					registrosFinal[j] = new GraphViewData(clave, temporal.get(clave));
					j++;
				}
			}
	   
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}
		if(!esGeneral){
			if(registros != null){
				//Limpiamos los registros a null. Recorremos registros hasta encontrar un null
				int limite  = 0;
				boolean salir = false;
				while(!salir){
					if(registros[limite] == null) {
						salir = true;
					}else limite++;
				}
				registrosFinal = new GraphViewData[limite];
				for(int i = 0; i<limite; i++){
					registrosFinal[i] = registros[i];
				}		
			}
		}
		Random rnd = new Random();
		if(c.getCount()>0){
			GraphViewSeries a = new GraphViewSeries(idRestaurante+"", new GraphViewSeriesStyle(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), 5), registrosFinal);
			return a;
		}else return null;
	}
	
	
	public GraphViewSeries cargarDatosAnio(int idRestaurante){//buscamos en la bd el año. sumar el importe por meses
		abrirIngresosDB();
		GraphViewData[] registros = null;
		GraphViewData[] registrosFinal =null; //si no esta ese restaurante devueve null
		Cursor c = null;
		//Leemos los datos de la base de datos
		try{
			if(!esGeneral){
				String[] campos = new String[]{"Mes","Importe"};
				String[] datos = new String[]{anio+"",idRestaurante+""};
				c = dbIngresos.query("Ingresos", campos, "Anio=? AND IdRestaurante=?", datos, null, null,"Mes");	
				
				if(c.getCount()>0)
					registros = new GraphViewData[c.getCount()];
				int antMes = 0;
				int i = 0;
				while(c.moveToNext()){
					int actMes = c.getInt(0);
					if(actMes != antMes){
						registros[i] = new GraphViewData(actMes, calculaIngeresosDelMes(actMes,idRestaurante));
						i++;
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
				if(c.getCount()>0){
					registrosFinal = new GraphViewData[temporal.size()];
				}
				//Recorro el HasMap para ir creando los datos
				Iterator<Integer> itClave = temporal.keySet().iterator();
				int j = 0;
				while(itClave.hasNext()){
					int clave = (Integer) itClave.next();
					registrosFinal[j] = new GraphViewData(clave, temporal.get(clave));
					j++;
				}
				
			}
		}catch(Exception e){
			System.out.println("Error en la carga de Ingresos");
		}
		if(!esGeneral){
			if(registros != null){
				//Limpiamos los registros a null. Recorremos registros hasta encontrar un null
				int limite  = 0;
				boolean salir = false;
				while(!salir){
					if(registros[limite] == null) {
						salir = true;
					}else limite++;
				}
				registrosFinal = new GraphViewData[limite];
				for(int i = 0; i<limite; i++){
					registrosFinal[i] = registros[i];
				}		
			}
		}
		
		Random rnd = new Random(); 
		if(c.getCount()>0){
			return  new GraphViewSeries(idRestaurante+"", new GraphViewSeriesStyle(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)), 5), registrosFinal);
		}
		else return null;
	}
	
	private void abrirVentanaEmergente() {
		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(getActivity());
  		
  		View vistaAviso = null;
		if(tipo.equals("Dia")){
			//Elegimos la vista con los tres spinners
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_dia_mes_anio, null);
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerDia = (Spinner) vistaAviso.findViewById(R.id.spinner_dia_dma);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterDia = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.dias, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterDia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerDia.setAdapter(adapterDia);
			spinnerDia.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					dia = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerMes = (Spinner) vistaAviso.findViewById(R.id.spinner_mes_dma);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterMes = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.meses, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerMes.setAdapter(adapterMes);
			spinnerMes.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					mes = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_dma);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterAnio = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.anios, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterAnio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			
		}else if (tipo.equals("Mes")){
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_mes_anio, null);
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerMes = (Spinner) vistaAviso.findViewById(R.id.spinner_mes_ma);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterMes = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.meses, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerMes.setAdapter(adapterMes);
			spinnerMes.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					mes = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_ma);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterAnio = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.anios, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterAnio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			
		}else{
			vistaAviso = LayoutInflater.from(getActivity()).inflate(R.layout.eleccion_anio, null);
			
			//Creamos el spinner del dia con sus opciones
			Spinner spinnerAnio = (Spinner) vistaAviso.findViewById(R.id.spinner_anio_a);
			// Create an ArrayAdapter using the string array and a default spinner layout
			//Si lo vamos a coger de la base de datos usar CursorAdapter en vez de ArrayAdapter
			ArrayAdapter<CharSequence> adapterAnio = ArrayAdapter.createFromResource(ventanaEmergente.getContext(),
			        R.array.anios, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapterAnio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinnerAnio.setAdapter(adapterAnio);
			spinnerAnio.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View arg1,
						int position, long arg3) {
					anio = Integer.parseInt(parentView.getItemAtPosition(position).toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}	
		
		
		ventanaEmergente.setNegativeButton("Cancelar", null);
		ventanaEmergente.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		
			public void onClick(DialogInterface dialog, int which) {	
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
}
