package com.example.nfcook_gerente;


import java.util.ArrayList;

import adapters.MiListGeneralRestaurantesAdapter;
import adapters.PadreListRestaurantes;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * Clase que se encarga de cargar el adapter y de la pantalla inicial del gerente
 * También se establecen los onClick de los botones y su comportamiento (Cuando aparecer y desaparecer)
 * Lee de base de datos los restaurantes y los carga en el ArrayList restaurantes
 * 
 * @author Guille
 *
 */



public class GeneralRestaurantes extends Activity {
	private static MiListGeneralRestaurantesAdapter  adapterListGeneralRestaurantes;
	private ListView listViewRestaurantes;
	private ArrayList<PadreListRestaurantes> restaurantes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.general_restaurantes);
		
		listViewRestaurantes = (ListView)findViewById(R.id.listViewRestaurantes);
	    restaurantes = obtenerRestaurantes();
	    
	    adapterListGeneralRestaurantes = new MiListGeneralRestaurantesAdapter(GeneralRestaurantes.this, restaurantes);
	    
	    listViewRestaurantes.setAdapter(adapterListGeneralRestaurantes);
	    
	    listViewRestaurantes.setOnItemClickListener(new OnItemClickListener() {
  	    	
  	    	public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id){
	  	    	// Iniciamos la nueva actividad y le pasamos los datos del restaurante
	  	  		Intent intent = new Intent(GeneralRestaurantes.this, InfoRestaurante.class);
	  	  		intent.putExtra("nombre", restaurantes.get(posicion).getNombreRestaurante());
	  	  		intent.putExtra("calle", restaurantes.get(posicion).getCalle());
	  	  		intent.putExtra("telefono", restaurantes.get(posicion).getTelefono());
	  	  		intent.putExtra("logo", restaurantes.get(posicion).getImagen());
	  	  		intent.putExtra("imagen",restaurantes.get(posicion).getImagenFachada());
	  	  		intent.putExtra("id", restaurantes.get(posicion).getIdRestaurante());
	  	  		
	  	  		startActivity(intent);
  	    	}
	    });
	}


	public ArrayList<PadreListRestaurantes> obtenerRestaurantes() {
		ArrayList<PadreListRestaurantes> restaurantes = new ArrayList<PadreListRestaurantes>();
		restaurantes.add(new PadreListRestaurantes("Vips Princesa",3,"Calle de la Princesa 5, 28008, Madrid", "vips", "vips_princesa5", "+34912752063"));
		restaurantes.add(new PadreListRestaurantes("Vips Goya",4,"Calle Goya 67, Madrid", "vips", "vips_goya67", "+34912752213"));
		restaurantes.add(new PadreListRestaurantes("Vips Sanchinarro",5,"Avenida de Burgos 119, Las Tablas, 28050 Madrid", "vips", "vips_sanchinarro119", "+34915556677"));
		restaurantes.add(new PadreListRestaurantes("Foster Princesa",6,"Calle de la Princesa 13, Madrid", "logo_foster", "foster_princesa13", "+34915591914"));
		restaurantes.add(new PadreListRestaurantes("Foster Ópera",7,"Plaza Isabel II, 3 Madrid", "logo_foster", "foster_opera3","+34914678900"));
		return restaurantes;
	}
	
	public void onClickComparar(View vista) {
		
	    Button botonAceptar = (Button) findViewById(R.id.buttonAceptar);
	    Button botonCancelar = (Button) findViewById(R.id.buttonCancelar);
	    Button botonComparar = (Button) findViewById(R.id.botonComparar);
		
		for(int i =0; i< restaurantes.size(); i ++)
			restaurantes.get(i).setCheckVisibles(true);
		
    	botonComparar.setVisibility(8); 
    	botonAceptar.setVisibility(0);
    	botonAceptar.setWidth(vista.getWidth()/2);
    	botonCancelar.setVisibility(0);
    	botonCancelar.setWidth(vista.getWidth()/2);
    	
		adapterListGeneralRestaurantes = new MiListGeneralRestaurantesAdapter(GeneralRestaurantes.this, restaurantes);
		listViewRestaurantes.setAdapter(adapterListGeneralRestaurantes);
	}
	
	public void onClickAceptar(View vista) {		
		ArrayList<String> seleccionados = recorreSeleccionados();
		for(int i =0; i< seleccionados.size(); i ++)
			Log.i("----------------------> "+i,seleccionados.get(i));
	}
	
	private ArrayList<String> recorreSeleccionados() {
		ArrayList<String> seleccionados =new ArrayList<String>();
		for(int i =0; i< restaurantes.size(); i ++){
			if(restaurantes.get(i).isSelected())
				seleccionados.add(restaurantes.get(i).getNombreRestaurante());//Puede ser por el id mejor
		}
		return seleccionados;
	}


	public void onClickCancelar(View vista) {
		Button botonAceptar = (Button) findViewById(R.id.buttonAceptar);
	    Button botonCancelar = (Button) findViewById(R.id.buttonCancelar);
	    Button botonComparar = (Button) findViewById(R.id.botonComparar);
		
		for(int i =0; i< restaurantes.size(); i ++){
			restaurantes.get(i).setCheckVisibles(false);
			restaurantes.get(i).setSelected(false);
		}
		
    	botonComparar.setVisibility(0); 
    	botonAceptar.setVisibility(8);
    	botonCancelar.setVisibility(8);
    	
		adapterListGeneralRestaurantes = new MiListGeneralRestaurantesAdapter(GeneralRestaurantes.this, restaurantes);
		listViewRestaurantes.setAdapter(adapterListGeneralRestaurantes);
	}
	
	public void onClickTodos(View vista) {	
		// Iniciamos la nueva actividad
		Intent intent = new Intent(GeneralRestaurantes.this, GraficaGeneral.class);
		intent.putExtra("tipo", "porAnio");//TODO necesario?
		startActivity(intent);
	}
}
