package usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.nfcook.R;

import baseDatos.Handler;

import adapters.MiListImagenesRestaurantesAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private ArrayList<Integer> logosRestaurantesListaInicial, logosRestaurantes;
	private Set<String> nombresRestaurantes;
	
	private Handler sql;
	private SQLiteDatabase db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        importarBaseDatatos();
    	crearRestaurantesListView();
    	
    	// Cerramos la base de datos
    	sql.close();
    }
    
    private void crearRestaurantesListView() {	
    	// Vemos cuantos restaurantes hay en la base de datos para cargar sus logos de forma generica
    	nombresRestaurantes = new HashSet<String>();
    	try{
    		String[] campos = new String[]{"Restaurante"};
	    	Cursor c = db.query("Restaurantes", campos, null, null, null, null,null);
	    	
	    	// Vamos metiendo los restaurantes que vayan apareciendo a un conjunto
	    	while(c.moveToNext()){
	    		String nombre = c.getString(0);
	    		nombresRestaurantes.add(nombre);
	    	} 
	    }catch(SQLiteException e){
	        Toast.makeText(getApplicationContext(),"ERROR BASE DE DATOS -> TABS",Toast.LENGTH_SHORT).show();	
	    }
    	
    	// Creamos la lista de Imagenes del restaurante
    	logosRestaurantesListaInicial = new ArrayList<Integer>();
    	logosRestaurantes = new ArrayList<Integer>(); 
    	String nombre, logo;
    	nombre = logo = "";
    	Iterator<String> it = nombresRestaurantes.iterator();
    	while(it.hasNext()){
    		nombre = it.next();
    		logosRestaurantesListaInicial.add(getResources().getIdentifier(nombre.toLowerCase(),"drawable",this.getPackageName()));
    		logo = "logo_"+nombre.toLowerCase();
    		logosRestaurantes.add(getResources().getIdentifier(logo,"drawable",this.getPackageName()));
    	}
    	
    	ListView lv = (ListView) findViewById(R.id.listaLogosRestarurtantes);
        lv.setAdapter(new MiListImagenesRestaurantesAdapter(this, logosRestaurantesListaInicial));

        lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View v, int position, long id){
				lanzar(position); 
			}
        });	
	}
    
    private void importarBaseDatatos(){
        try{
        	sql = new Handler(getApplicationContext()); 
        	db = sql.open();
        }catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        }	
	}

	public void lanzar(int posicion){
		String nombreRestaurante;
		int i = 0;
		
		Iterator<String> it = nombresRestaurantes.iterator();
		while(it.hasNext() && i<posicion){
			it.next();
			i++;
		}
		nombreRestaurante = it.next();
    	
    	Intent intent = new Intent(this,InicializarRestaurante.class);
		intent.putExtra("nombreRestaurante", nombreRestaurante);
    	intent.putExtra("logoRestaurante",logosRestaurantes.get(posicion));
    	startActivity(intent);
    }	
}

