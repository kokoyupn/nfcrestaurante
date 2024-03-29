package com.example.nfcook_gerente;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import baseDatos.HandlerGenerico;

public class MainActivity extends Activity {

	EditText usuario; //Contiene el usuario que introduces por pantalla
	EditText password; //Contiene la contraseņa que introduces por pantalla
	//Ruta de las bases de datos
	String rutaLogin="/data/data/com.example.nfcook_gerente/databases/";
	private SQLiteDatabase dbLogin;
	private HandlerGenerico sql;
	public AlertDialog ventanaEmergente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
     * Metodo para el boton invisible que usamos para probar la aplicacion mas rapido
     * @param boton
     */
	  public void onClickBotonInvisible(View boton)
	  {
		  //Iniciamos la nueva actividad
	   	  Intent intent = new Intent(this, GeneralRestaurantes.class);
       	  intent.putExtra("usuario", "Foster");
       	  intent.putExtra("Restaurante","Foster");
       	  startActivityForResult(intent,0); 
	  }  
	  
	  /**Para reiniciar los campos de los textview cuando vuelve con back*/
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
		  usuario = (EditText) findViewById(R.id.editTextUsuario);
		  password = (EditText) findViewById(R.id.editTextPass);	
		  usuario.setText("");
		  password.setText("");
	  }   
	  
	 /**
	  * Metodo onclick de boton encargado de comprobar si el usuario y la contraseņa introducidas por pantalla
	  * esta en la base de datos de camareros 
	  * @param boton
	  * autor:Daniel
	  */
	  public void  onClickBotonEntrar(View boton){
	   /*Contiene en nombre que introduces por pantalla en el editText*/
		  usuario = (EditText) findViewById(R.id.editTextUsuario);
		   // Contiene la contraseņa que introduces por pantalla en el editText
		   password = (EditText) findViewById(R.id.editTextPass);
		  
		  Intent intent = new Intent(this, GeneralRestaurantes.class);
     	  intent.putExtra("usuario", "Foster");
     	  intent.putExtra("Restaurante", "Foster");
     	  startActivityForResult(intent,0);
     	    
		 /*
	   usuario = (EditText) findViewById(R.id.editTextUsuario);
	   // Contiene la contraseņa que introduces por pantalla en el editText
	   password = (EditText) findViewById(R.id.editTextPass);
	
	   try{ //Abrimos la base de datos para consultarla
	       	sql = new HandlerGenerico(getApplicationContext(),rutaLogin,"Login.db"); 
	       	dbLogin = sql.open();
	    
	    }catch(SQLiteException e){
	        	Toast.makeText(getApplicationContext(),"No existe la base de datos login",Toast.LENGTH_SHORT).show();
	       }
	   
	   try{	
		   String[] campos = new String[]{"Nombre","Contraseņa"};
		   String[] datos = new String[]{usuario.getText().toString(), password.getText().toString()};
		   //Buscamos en la base de datos el nombre de usuario y la contraseņa
		   Cursor c = dbLogin.query("Camareros",campos,"Nombre=? AND Contraseņa=?",datos, null,null, null);
	  	  
		   	  	   
	  	   c.moveToFirst();
       	 
	  	   //String usu = c.getString(0);
	  	   String cont = c.getString(1);
	  	   
	   	  
	  	   if (cont.equals(password.getText().toString()))//Si las contraseņa que hay en la base de datos y la que a introducido el usuario son iguales
          
    	   {  
    	   	  
           	  //Iniciamos la nueva actividad
    	   	  Intent intent = new Intent(this, InicializarGerente.class);
           	  intent.putExtra("usuario", usuario.getText().toString());
           	   
           	  if (password.getText().toString().equals("foster"))
           		  intent.putExtra("Restaurante","Foster");
           	  else
           		 intent.putExtra("Restaurante","VIPS");
           	 
           	  startActivityForResult(intent,0);
    	   }
    	   else{
    		   //La contraseņa no es la misma que la guardada en la base de datos
    		   abrir_ventanaEmergente("Contraseņa incorrecta",R.drawable.icono_password);
   			
    	   }
	   	   
		}catch(Exception e){
			//No existe ese usuario en la base de datos
			System.out.println(e.getStackTrace().toString());
			abrir_ventanaEmergente("No existe ese usuario o contraseņa incorrecta",R.drawable.icono_usuario);
		}
	   */
   }

	/**
	 * Metodo encargado de sacar un mensaje por pantalla durante 2 segundos con le mensaje indicadopor parametro y la foto indicada
	 * * @param text
	 * @param foto
	 */
	
	private void abrir_ventanaEmergente(String text, int foto) {
	 	View vistaAviso = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_login_gerente, null);
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
	         @Override
			public void run() {
	            ventanaEmergente.dismiss(); 
	             t.cancel(); 
	         }
	     }, 5000);
	}
}
