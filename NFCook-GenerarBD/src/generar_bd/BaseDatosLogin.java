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

public class BaseDatosLogin extends Activity {	   
	public Handler sql;
	public SQLiteDatabase db;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_generada);
        
        // Importamos la base de datos donde vamos a hacer la carga de los platos de los restaurantes
        try{
        	sql=new Handler(getApplicationContext(),"Login.db"); 
        	db=sql.open();
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
     		
        } 

        ContentValues n21 = new ContentValues();
     	n21.put("Nombre", "Abel");
     	n21.put("Contraseña", "1234");
     	db.insert("Camareros", null, n21);
     	           
    	
     	ContentValues n22 = new ContentValues();
     	n22.put("Nombre", "Carlos");
     	n22.put("Contraseña", "1234");
     	db.insert("Camareros", null, n22);
     	
     	ContentValues n23 = new ContentValues();
     	n23.put("Nombre", "Javi");
     	n23.put("Contraseña", "1234");
     	db.insert("Camareros", null, n23);
     	
     	ContentValues n24 = new ContentValues();
     	n24.put("Nombre", "Dani");
     	n24.put("Contraseña", "1234");
     	db.insert("Camareros", null, n24);
     	
     	ContentValues n25 = new ContentValues();
     	n25.put("Nombre", "Rober");
     	n25.put("Contraseña", "1234");
     	db.insert("Camareros", null, n25);
     	
     	ContentValues n26 = new ContentValues();
     	n26.put("Nombre", "AlexM");
     	n26.put("Contraseña", "1234");
     	db.insert("Camareros", null, n26);
     	
     	ContentValues n27 = new ContentValues();
     	n27.put("Nombre", "AlexV");
     	n27.put("Contraseña", "1234");
     	db.insert("Camareros", null, n27);
     	
     	ContentValues n28 = new ContentValues();
     	n28.put("Nombre", "JuanDiego");
     	n28.put("Contraseña", "1234");
     	db.insert("Camareros", null, n28);
     	
     	ContentValues n29 = new ContentValues();
     	n29.put("Nombre", "Alvaro");
     	n29.put("Contraseña", "1234");
     	db.insert("Camareros", null, n29);
     	
     	ContentValues n30 = new ContentValues();
     	n30.put("Nombre", "admin");
     	n30.put("Contraseña", "admin");
     	db.insert("Camareros", null, n30);
/************************************************FIN FOSTER*************************************************/
        // Cerramos la base de datos
     	sql.close();
    }
 }