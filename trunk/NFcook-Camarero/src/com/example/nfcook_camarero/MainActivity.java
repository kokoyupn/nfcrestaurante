package com.example.nfcook_camarero;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText usuario; //Contiene el usuario que introduces por pantalla
	EditText password; //Contiene la contraseña que introduces por pantalla
	//Ruta de las bases de datos
	String rutaLogin="/data/data/com.example.nfcook_camarero/databases/";
	private SQLiteDatabase dbLogin;
	private HandlerGenerico sql;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    	super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
 /**
  * Metodo onclick de boton encargado de comprobar si el usuario y la contraseña introducidas por pantalla
  * esta en la base de datos de camareros 
  * @param boton
  */

public void  onClickBotonEntrar(View boton)
   {
	   /*Contiene en nombre que introduces por pantalla en el editText*/
	   usuario = (EditText) findViewById(R.id.editTextUsuario);
	   /*Contiene la contraseña que introduces por pantalla en el editText*/
	   password = (EditText) findViewById(R.id.editTextPass);
	
	   try{ //Abrimos la base de datos para consultarla
	       	sql = new HandlerGenerico(getApplicationContext(),rutaLogin,"Login.db"); 
	       	dbLogin = sql.open();
	    
	    }catch(SQLiteException e){
	        	Toast.makeText(getApplicationContext(),"No existe la base de datos login",Toast.LENGTH_SHORT).show();
	       }
	   
	   try{
				
		   String[] campos = new String[]{"Nombre","Contraseña"};
		   String[] datos = new String[]{usuario.getText().toString()};//, password.getText().toString()};
		   //Buscamos en la base de datos el nombre de usuario y la contraseña
		   Cursor c = dbLogin.query("Camareros",campos,"Nombre=?",datos, null,null, null);
	  	  
		   	  	   
	  	   c.moveToFirst();
       	 
	  	   String usu = c.getString(0);
	  	   String cont = c.getString(1);
       		
	  	   if (cont.equals(password.getText().toString()))//Si las contraseña que hay en la base de datos y la que a introducido el usuario son iguales
          
    	   {  Toast.makeText(getApplicationContext(),"Usuario: "+usu+"\n"+"Contraseña: "+cont, Toast.LENGTH_SHORT).show();
           	 	//Iniciamos la nueva actividad
    	   	  Intent intent = new Intent(this, InicialCamarero.class);
           	  intent.putExtra("usuario", usuario.getText().toString());
           	  startActivity(intent);	
    	   }
    	   else{
    		   //La contraseña no es la misma que la guardada en la base de datos
    		   Toast.makeText(getApplicationContext(),"Contraseña incorrecta", Toast.LENGTH_SHORT).show(); 
    	   }
  	
		}catch(Exception e){
			//No existe ese usuario en la base de datos
			Toast.makeText(getApplicationContext(),"No existe ese usuario", Toast.LENGTH_SHORT).show();
		}
	   
   }
}
