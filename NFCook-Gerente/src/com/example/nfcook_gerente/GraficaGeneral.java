package com.example.nfcook_gerente;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraficaGeneral extends Activity{
	private String tipo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.grafica);
	
		Bundle bundle = getIntent().getExtras();
		tipo = bundle.getString("tipo"); 
		
		//La primera vez se muestra al año
		if(tipo.equals("porMes")){
			cargarAlMes();
		}else if(tipo.equals("porDia")){
			cargarAlDia();
		}else cargarAlAnio();		
		
	}

//	private void modificarEjes(GraphView graphView) {
//		graphView = new LineGraphView(this, "example");
//		CustomLabelFormatter miLabel = new CustomLabelFormatter() {
//				
//				@Override
//				public String formatLabel(double value, boolean isValueX) {
//					if (isValueX) {
//						if (value < 5) {
//							return "smallll";
//						} else if (value < 15) {
//							return "middle";
//						} else {
//							return "big";
//						}
//					}
//					return null; // let graphview generate Y-axis label for us
//				}
//			};
//		graphView.setCustomLabelFormatter(miLabel);
//	}
//	
	private void cargarAlDia(){
//		//Le ponemos scroll
//		// draw sin curve
//		int num = 150;
//		GraphViewData[] data = new GraphViewData[num];
//		double v=0;
//		for (int i=0; i<num; i++) {
//			v += 0.2;
//			data[i] = new GraphViewData(i, Math.sin(v));
//		}
//		GraphView graphView = new LineGraphView(this, "GraphViewDemo");
//		// add data
//		graphView.addSeries(new GraphViewSeries(data));
//		// set view port, start=2, size=40
//		graphView.setViewPort(2, 40);
//		graphView.setScrollable(true);
//		// optional - activate scaling / zooming
//		graphView.setScalable(true);
		
		
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			new GraphViewData(1, 2.0d)
			, new GraphViewData(2, 1.5d)
			, new GraphViewData(3, 2.5d)
			, new GraphViewData(4, 1.0d)
			, new GraphViewData(5, 3.0d)
			, new GraphViewData(6, 2.0d)
			, new GraphViewData(7, 3.5d)
			, new GraphViewData(8, 2.0d)
			, new GraphViewData(9, 1.2d)
			, new GraphViewData(10, 4.0d)
			, new GraphViewData(11, 2.9d)
			, new GraphViewData(12, 3.9d)
			, new GraphViewData(13, 2.0d)
			, new GraphViewData(14, 1.5d)
			, new GraphViewData(15, 2.5d)
			, new GraphViewData(16, 1.0d)
			, new GraphViewData(17, 3.0d)
			, new GraphViewData(18, 2.0d)
			, new GraphViewData(19, 3.5d)
			, new GraphViewData(20, 2.0d)
			, new GraphViewData(21, 1.2d)
			, new GraphViewData(22, 4.0d)
			, new GraphViewData(23, 2.9d)
			, new GraphViewData(24, 3.9d)
		});

		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new LineGraphView(this, "Todos los restaurantes"); 
		graphView.addSeries(exampleSeries); // data
		
		// set view port, start=0, size=10
		graphView.setViewPort(1, 24);
		graphView.setScrollable(true);
		// optional - activate scaling / zooming
		graphView.setScalable(true);
	
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.GREEN);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(30);
	    graphView.getGraphViewStyle().setNumHorizontalLabels(24);
		
		//Cambiamos el color del fondo
		graphView.setBackgroundColor(Color.GREEN);
		
		//buscamos el la zona donde va la gráfica y le cargaos la que acabamos de crear
		LinearLayout layout = (LinearLayout) findViewById(R.id.contenedor);
		layout.addView(graphView);

	}
	
	private void cargarAlMes(){
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			new GraphViewData(1, 2.0d)
			, new GraphViewData(2, 1.5d)
			, new GraphViewData(3, 2.5d)
			, new GraphViewData(4, 1.0d)
			, new GraphViewData(5, 3.0d)
			, new GraphViewData(6, 2.0d)
			, new GraphViewData(7, 3.5d)
			, new GraphViewData(8, 2.0d)
			, new GraphViewData(9, 1.2d)
			, new GraphViewData(10, 4.0d)
			, new GraphViewData(11, 2.9d)
			, new GraphViewData(12, 3.9d)
			, new GraphViewData(13, 2.0d)
			, new GraphViewData(14, 1.5d)
			, new GraphViewData(15, 2.5d)
			, new GraphViewData(16, 1.0d)
			, new GraphViewData(17, 3.0d)
			, new GraphViewData(18, 2.0d)
			, new GraphViewData(19, 3.5d)
			, new GraphViewData(20, 2.0d)
			, new GraphViewData(21, 1.2d)
			, new GraphViewData(22, 4.0d)
			, new GraphViewData(23, 2.9d)
			, new GraphViewData(24, 3.9d)
		});
 
		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new BarGraphView(this, "Todos los restaurantes"); 
		graphView.addSeries(exampleSeries); // data
	
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.GREEN);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(30);
	    graphView.getGraphViewStyle().setNumHorizontalLabels(24); //TODO poner todos los dias
		
		//Cambiamos el color del fondo
		graphView.setBackgroundColor(Color.GREEN);
		
		//buscamos el la zona donde va la gráfica y le cargaos la que acabamos de crear
		LinearLayout layout = (LinearLayout) findViewById(R.id.contenedor);
		layout.addView(graphView);
	}
	
	private void cargarAlAnio(){
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			new GraphViewData(1, 2.0d)
			, new GraphViewData(2, 1.5d)
			, new GraphViewData(3, 2.5d)
			, new GraphViewData(4, 1.0d)
			, new GraphViewData(5, 3.0d)
			, new GraphViewData(6, 2.0d)
			, new GraphViewData(7, 3.5d)
			, new GraphViewData(8, 2.0d)
			, new GraphViewData(9, 1.2d)
			, new GraphViewData(10, 4.0d)
			, new GraphViewData(11, 2.9d)
			, new GraphViewData(12, 3.9d)
		});

		//Si queremos grafico de barras cambiar LineGraphView por BarGraphView
		GraphView graphView = new LineGraphView(this, "Todos los restaurantes"); 
		graphView.addSeries(exampleSeries); // data
	
		//cambiamos el estilo
		graphView.getGraphViewStyle().setGridColor(Color.GREEN);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(30);
		graphView.getGraphViewStyle().setNumHorizontalLabels(12);
	//	graphView.getGraphViewStyle().setNumVerticalLabels(4);
	//	graphView.getGraphViewStyle().setVerticalLabelsWidth(300);
	
		
		
		//Modificamos los ejes
		graphView.setHorizontalLabels(new String[] {  "enero", "febrero", "marzo", "abril"
													, "mayo", "junio", "julio", "agosto"
													, "septiembre", "octubre", "noviembre", "diciembre"});  
		//graphView.setVerticalLabels(new String[] {"high", "middle", "low"}); 
		//modificarEjes(graphView);
		
		//Cambiamos el color del fondo
		graphView.setBackgroundColor(Color.GREEN);
		
		//buscamos el la zona donde va la gráfica y le cargaos la que acabamos de crear
		LinearLayout layout = (LinearLayout) findViewById(R.id.contenedor);
		layout.addView(graphView);
	}
	
	public void onClickAlAnio(View vista){
		if(!tipo.equals("porAnio")){
			Intent intent = new Intent(this, GraficaGeneral.class);
			intent.putExtra("tipo", "porAnio");
			this.finish();
			startActivity(intent);
		}
//		cargarAlAnio();
	}
	
	public void onClickAlMes(View vista){
		if(!tipo.equals("porMes")){
			Intent intent = new Intent(this, GraficaGeneral.class);
			intent.putExtra("tipo", "porMes");
			this.finish();
			startActivity(intent);
		}
//		cargarAlMes();
	}
	
	public void onClickAlDia(View vista){
		if(!tipo.equals("porDia")){
			Intent intent = new Intent(this, GraficaGeneral.class);
			intent.putExtra("tipo", "porDia");
			this.finish();
			startActivity(intent);
		}
//		cargarAlDia();
	}
	
}
