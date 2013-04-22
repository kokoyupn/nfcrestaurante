package com.example.nfcook_camarero;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText usuario; //Contiene el usuario que introduces por pantalla
	EditText password; //Contiene la contraseña que introduces por pantalla
	//Ruta de las bases de datos
	String rutaLogin="/data/data/com.example.nfcook_camarero/databases/";
	private SQLiteDatabase dbLogin;
	private HandlerGenerico sql;
	public AlertDialog ventanaEmergente;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	 //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        
    	super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
 
    /**
     * Metodo para el boton invisible que usamos para probar la aplicacion mas rapido
     * @param boton
     */
	  public void onClickBotonInvisible(View boton)
	  {
		  //Iniciamos la nueva actividad
	   	  Intent intent = new Intent(this, InicializarPantallasCamarero.class);
       	  intent.putExtra("usuario", "Foster");
       	  intent.putExtra("Restaurante","Foster");
       	  startActivity(intent); 
	  }  
 /**
  * Metodo onclick de boton encargado de comprobar si el usuario y la contraseña introducidas por pantalla
  * esta en la base de datos de camareros 
  * @param boton
  * autor:Daniel
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
		   String[] datos = new String[]{usuario.getText().toString(), password.getText().toString()};
		   //Buscamos en la base de datos el nombre de usuario y la contraseña
		   Cursor c = dbLogin.query("Camareros",campos,"Nombre=? AND Contraseña=?",datos, null,null, null);
	  	  
		   	  	   
	  	   c.moveToFirst();
       	 
	  	   String usu = c.getString(0);
	  	   String cont = c.getString(1);
       		
	  	   if (cont.equals(password.getText().toString()))//Si las contraseña que hay en la base de datos y la que a introducido el usuario son iguales
          
    	   {  
    	   	  //abrir_ventanaEmergente("Bienvenido: "+usu,R.drawable.icono_usuario);
           	  //Iniciamos la nueva actividad
    	   	  Intent intent = new Intent(this, InicializarPantallasCamarero.class);
           	  intent.putExtra("usuario", usuario.getText().toString());

           	  if (password.getText().toString().equals("foster"))
           		  intent.putExtra("Restaurante","Foster");
           	  else
           		 intent.putExtra("Restaurante","VIPS");

    	   }
    	   else{
    		   //La contraseña no es la misma que la guardada en la base de datos
    		   abrir_ventanaEmergente("Contraseña incorrecta",R.drawable.icono_password);
   			
    	   }
  	
		}catch(Exception e){
			//No existe ese usuario en la base de datos
			abrir_ventanaEmergente("No existe ese usuario",R.drawable.icono_usuario);
		}
	   
   }

/**
 * Metodo encargado de sacar un mensaje por pantalla durante 2 segundos con le mensaje indicadopor parametro y la foto indicada
 * * @param text
 * @param foto
 */

private void abrir_ventanaEmergente(String text, int foto) {
	 	View vistaAviso = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_login_camarero, null);
	    ImageView img= (ImageView) vistaAviso.findViewById(R.id.imageNFC);
		TextView texto= (TextView) vistaAviso.findViewById(R.id.textViewAvisoCamarero);
		texto.setText(text);
		img.setImageResource(foto);
	    ventanaEmergente = new AlertDialog.Builder(MainActivity.this).create();
		ventanaEmergente.setView(vistaAviso);
		ventanaEmergente.show();
		
		//Crea el timer para que el mensaje solo aparezca durante 2 segundos
		final Timer t = new Timer();
     t.schedule(new TimerTask() {
         public void run() {
            ventanaEmergente.dismiss(); 
             t.cancel(); 
         }
     }, 5000);
	
}
}
