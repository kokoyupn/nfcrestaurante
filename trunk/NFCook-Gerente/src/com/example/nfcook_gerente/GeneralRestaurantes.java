package com.example.nfcook_gerente;


import java.util.ArrayList;

import baseDatos.HandlerGenerico;

import adapters.MiListGeneralRestaurantesAdapter;
import adapters.PadreListRestaurantes;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
 
/** 
=======
 
/** 
=======
import android.widget.Toast;
 
/** 
>>>>>>> .r521
>>>>>>> .r526
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
	private HandlerGenerico sqlRestaurantes;
	private SQLiteDatabase dbRestaurantes;
	
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
  	    	
  	    	@Override
			public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id){
	  	    	// Iniciamos la nueva actividad y le pasamos los datos del restaurante
	  	  		Intent intent = new Intent(GeneralRestaurantes.this, InicializarInformacionRestaurante.class);
	  	  		intent.putExtra("nombre", restaurantes.get(posicion).getNombreRestaurante());
	  	  		intent.putExtra("calle", restaurantes.get(posicion).getCalle());
	  	  		intent.putExtra("cp", restaurantes.get(posicion).getCP());
	  	  		intent.putExtra("poblacion", restaurantes.get(posicion).getPoblacion());
	  	  		intent.putExtra("telefono", restaurantes.get(posicion).getTelefono());
	  	  		intent.putExtra("imagen",restaurantes.get(posicion).getImagenFachada());
	  	  		intent.putExtra("id", restaurantes.get(posicion).getIdRestaurante());
	  	  		intent.putExtra("rating", restaurantes.get(posicion).getRating());
	  	  		
	  	  		startActivity(intent);
  	    	}
	    });
	} 


	public ArrayList<PadreListRestaurantes> obtenerRestaurantes() {

		ArrayList<PadreListRestaurantes> restaurantes = new ArrayList<PadreListRestaurantes>();
		restaurantes.add(new PadreListRestaurantes("Vips Princesa",3,"Calle de la Princesa 5", "28008", "Madrid", "vips", "vips_princesa5", "+34912752063", 5));
		restaurantes.add(new PadreListRestaurantes("Vips Goya",4,"Calle Goya 67", "28020", "Madrid", "vips", "vips_goya67", "+34912752213", 4));
		restaurantes.add(new PadreListRestaurantes("Vips Sanchinarro",5,"Avenida de Burgos 119", "28050", "Madrid", "vips", "vips_sanchinarro119", "+34915556677", 3));
		restaurantes.add(new PadreListRestaurantes("Foster Princesa",6,"Calle de la Princesa 13", "28039", "Madrid", "logo_foster", "foster_princesa13", "+34915591914", 5));
		restaurantes.add(new PadreListRestaurantes("Foster Ópera",7,"Plaza Isabel II 3", "28076", "Madrid", "logo_foster", "foster_opera3","+34914678900", 3));

		//Importamos la base de datos
//		try {
//			sqlRestaurantes = new HandlerGenerico(getApplicationContext(),
//					"/data/data/com.example.nfcook_gerente/databases/",
//					"Restaurantes.db");
//			dbRestaurantes = sqlRestaurantes.open();
//		} catch (SQLiteException e) {
//			System.out.println("CATCH");
//			Toast.makeText(getApplicationContext(),
//					"NO EXISTE LA BASE DE DATOS", Toast.LENGTH_SHORT).show(); 
//		}
//		
//		//Leemos los datos de la base de datos
//		try{
//		String[] campos = new String[]{"Nombre","Calle","Telefono","ImagenRest","ImagenFachada","id"};
//	   
//		//String[] datos = new String[]{"Vips Goya"};
//		Cursor c = dbRestaurantes.query("Restaurantes", campos, null, null, null, null,null);
//	    
//		//Cargamos los datos en la los atributos correspondientes de la clase
//		restaurantes = new ArrayList<PadreListRestaurantes>();
//		while(c.moveToNext()){
//			restaurantes.add(new PadreListRestaurantes(	c.getString(0),	//nombre
//														c.getInt(5),	//id
//														c.getString(1), //calle
//														c.getString(3), //imagen
//														c.getString(4), //imagenFachada
//														c.getString(2)));//telefono
//		}
//	   
//		}catch(Exception e){
//			System.out.println("Error en la carga de Restaurantes");
//		}

		

		return restaurantes;
	}
	
	public void onClickComparar(View vista) {
		
	    ImageView botonAceptar = (ImageView) findViewById(R.id.buttonAceptar);
	    ImageView botonCancelar = (ImageView) findViewById(R.id.buttonCancelar);
	    ImageView botonComparar = (ImageView) findViewById(R.id.botonComparar);
		
		for(int i =0; i< restaurantes.size(); i ++)
			restaurantes.get(i).setCheckVisibles(true);
		
    	botonComparar.setVisibility(8); 
    	botonAceptar.setVisibility(0);
    	//botonAceptar.setWidth(vista.getWidth()/2);

    	botonCancelar.setVisibility(0);


    	//botonCancelar.setWidth(vista.getWidth()/2);
    	
		adapterListGeneralRestaurantes = new MiListGeneralRestaurantesAdapter(GeneralRestaurantes.this, restaurantes);
		listViewRestaurantes.setAdapter(adapterListGeneralRestaurantes);
	}
	
	public void onClickAceptar(View vista) {		
		ArrayList<String> seleccionados = recorreSeleccionados();
		//TODO
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
		ImageView botonAceptar = (ImageView) findViewById(R.id.buttonAceptar);
		ImageView botonCancelar = (ImageView) findViewById(R.id.buttonCancelar);
		ImageView botonComparar = (ImageView) findViewById(R.id.botonComparar);
		
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
		intent.putExtra("tipo", "porAnio");
		startActivity(intent);
	}
}
