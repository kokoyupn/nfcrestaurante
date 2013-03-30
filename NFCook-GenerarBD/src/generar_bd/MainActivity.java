package generar_bd;

import com.example.nfcook.R;
import com.example.nfcook.R.id;
import com.example.nfcook.R.layout;

import baseDatos.Handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Clase encargada de ofrecer al usuario las diferentes bases de datos que puede generar en función
 * de la posición de la lista que seleccione.
 * @author Abel
 *
 */
public class MainActivity extends Activity{
	/*
	 * Añadir aquí el título de la base de datos nueva que queramos generar y posteriormemte crear la clase
	 * dónde se cree y configurar el lanzar de esta actividad para que cuando corramos el proyecto y 
	 * pulsemos sobre la opción de la lista se genera la base de datos.
	 */
	private String titulosBases[] = {"Generar Bases Datos de Restaurantes","Generar Bases Datos de Login","Generar Bases Datos de Mesas", "Generar Bases Datos de Cuenta"};
	public Handler base;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {  	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titulosBases));

        lv.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				lanzar(arg2);
			}
        });
    }
    
    // Metodo en donde configuraremos la actividad que genera la base de datos que seleccionemos
    public void  lanzar(int arg2)
    { Intent intent;
    	switch (arg2){
    		// Generar base de datos de restaurantes
    		
    		case 0:
    			intent = new Intent(this,BaseDatosRestaurantes.class);
    	    	startActivity(intent);
    	    	break;
    		case 1:
    			intent = new Intent(this,BaseDatosLogin.class);
    	    	startActivity(intent);
    	    break;
    		case 2:
    			intent = new Intent(this,BaseDatosMesas.class);
    	    	startActivity(intent);
    	    break;
    		case 3:
    			intent = new Intent(this,BaseDatosCuenta.class);
    	    	startActivity(intent);
    	    break;
    	}
    		
    }
}



