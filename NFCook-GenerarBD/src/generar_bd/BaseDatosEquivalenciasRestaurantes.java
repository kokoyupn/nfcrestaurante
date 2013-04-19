package generar_bd;

import com.example.nfcook.R;
import com.example.nfcook.R.layout;

import baseDatos.Handler;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;

public class BaseDatosEquivalenciasRestaurantes extends Activity {	   
	public Handler sql;
	public SQLiteDatabase db;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_generada);
        
        // Importamos la base de datos donde vamos a hacer la carga de los platos de los restaurantes
        try{
        	sql=new Handler(getApplicationContext(),"Equivalencia_Restaurantes.db"); 
        	db=sql.open();
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
     		
        } 
       /**Campos de la base de datos Restaurante TEXT,Numero INTEGER,Abreviatura TEXT
        * Nombre de la tabla de esa base de datos Restaurantes*/
        int id=0;
    	ContentValues valores = new ContentValues();
    	valores.put("Restaurante","Foster");
    	valores.put("Numero", id);
    	valores.put("Abreviatura", "fh");
    	db.insert("Restaurantes", null,valores);
    	id++;
    	
    	valores = new ContentValues();
    	valores.put("Restaurante","VIPS");
    	valores.put("Numero", id);
    	valores.put("Abreviatura", "v");
    	db.insert("Restaurantes", null,valores);
    	id++;
     	
/************************************************FIN FOSTER*************************************************/
        // Cerramos la base de datos
     	sql.close();
    }
 }