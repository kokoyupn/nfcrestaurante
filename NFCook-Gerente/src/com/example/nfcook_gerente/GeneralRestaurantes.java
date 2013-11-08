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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
 

import android.widget.Toast;
 
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
	  	  		intent.putExtra("logo",restaurantes.get(posicion).getImagen());
	  	  		intent.putExtra("imagen",restaurantes.get(posicion).getImagenFachada());
	  	  		intent.putExtra("ids", restaurantes.get(posicion).getIdRestaurante() + "");
	  	  		intent.putExtra("rating", restaurantes.get(posicion).getRating());
	  	  		//Pasar la variable para indicar que no es la opcion de todos los rest. (false)
	  	  		intent.putExtra("esGeneral", false);
	  	  		//variable que indica si tiene que aparecer el tab de informacion o no
	  	  		intent.putExtra("sinInfo", false);
	  	  		
	  	  		startActivity(intent);
  	    	}
	    });
	    
	    //Establecemos los oyentes de los botones
	    final ImageView botonComparar = (ImageView) findViewById(R.id.botonComparar);
	    botonComparar.setOnClickListener(new OnClickListener() {
			public void onClick(View vista) {
			 	ImageView botonAceptar = (ImageView) findViewById(R.id.buttonAceptar);
			    ImageView botonCancelar = (ImageView) findViewById(R.id.buttonCancelar);
				
				for(int i =0; i< restaurantes.size(); i ++)
					restaurantes.get(i).setCheckVisibles(true);
				
		    	botonComparar.setVisibility(8); 
		    	botonAceptar.setVisibility(0);
		    	botonCancelar.setVisibility(0);
		    	
				adapterListGeneralRestaurantes = new MiListGeneralRestaurantesAdapter(GeneralRestaurantes.this, restaurantes);
				listViewRestaurantes.setAdapter(adapterListGeneralRestaurantes);				
			}
		});
		
	    
	    ImageView botonAceptar = (ImageView) findViewById(R.id.buttonAceptar);
	    botonAceptar.setOnClickListener(new OnClickListener() {
			public void onClick(View vista) {
				ArrayList<Integer> seleccionados = recorreSeleccionados();
				if (seleccionados.size() > 0){//ha seleccionado alguno
					// Iniciamos la nueva actividad y le pasamos los datos del restaurante
			  		Intent intent = new Intent(GeneralRestaurantes.this, InicializarInformacionRestaurante.class);
			  		
			  		String ids = "";
			  		for(int i =0; i< seleccionados.size(); i ++){
			  			ids = ids + seleccionados.get(i).toString() + ",";
			  		}
			  		//Quitamos la última coma
			  		ids = ids.substring(0, ids.length()-1);
	
			  		intent.putExtra("ids", ids);
					//Pasar la variable para indicar que no es la opcion de todos los rest. (false)
			  		intent.putExtra("esGeneral", false);
			  		//variable que indica si tiene que aparecer el tab de informacion o no
			  		intent.putExtra("sinInfo", true);
			  		
			  		startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "Debe seleccionar al menos un restaurante",  Toast.LENGTH_LONG).show();		
				}
			}
	    });

		final ImageView botonCancelar = (ImageView) findViewById(R.id.buttonCancelar);
		botonCancelar.setOnClickListener(new OnClickListener() {
			public void onClick(View vista) {
				ImageView botonAceptar = (ImageView) findViewById(R.id.buttonAceptar);
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
		});
	    
		
		final ImageView botonTodos = (ImageView) findViewById(R.id.todosLosRestaurantes);
		botonTodos.setOnClickListener(new OnClickListener() {
			public void onClick(View vista) {
				ArrayList<Integer> todos = dameIds();
				
				// Iniciamos la nueva actividad y le pasamos los datos del restaurante
		  		Intent intent = new Intent(GeneralRestaurantes.this, InicializarInformacionRestaurante.class);
		  		
		  		String ids = "";
		  		for(int i =0; i< todos.size(); i ++){
		  			ids = ids + todos.get(i).toString() + ",";
		  		}
		  		//Quitamos la última coma
		  		ids = ids.substring(0, ids.length()-1);

		  		intent.putExtra("ids", ids);
				//Pasar la variable para indicar que no es la opcion de todos los rest. (false)
		  		intent.putExtra("esGeneral", true);
		  		//variable que indica si tiene que aparecer el tab de informacion o no
		  		intent.putExtra("sinInfo", true);
			  		
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
	
	
	
	private ArrayList<Integer> recorreSeleccionados() {
		ArrayList<Integer> seleccionados =new ArrayList<Integer>();
		for(int i =0; i< restaurantes.size(); i ++){
			if(restaurantes.get(i).isSelected())
				seleccionados.add(restaurantes.get(i).getIdRestaurante());
		}
		return seleccionados;
	}
	
	
	private ArrayList<Integer> dameIds() {
		ArrayList<Integer> todos =new ArrayList<Integer>();
		for(int i =0; i< restaurantes.size(); i ++)
			todos.add(restaurantes.get(i).getIdRestaurante());
		return todos;
	}
}
