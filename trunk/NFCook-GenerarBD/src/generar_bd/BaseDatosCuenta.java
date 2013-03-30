package generar_bd;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;
import baseDatos.Handler;

import com.example.nfcook.R;

public class BaseDatosCuenta extends Activity {	   
	public Handler sql;
	public SQLiteDatabase db;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_generada);
        
        // Importamos la base de datos  Cuenta
        try{
        	sql=new Handler(getApplicationContext(),"Cuenta.db"); 
        	db=sql.open();
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
     		
        }
        
     	sql.close();
    }
 }
