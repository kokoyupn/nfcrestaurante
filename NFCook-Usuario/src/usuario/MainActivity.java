package usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.example.nfcook.R;

import baseDatos.HandlerDB;

import adapters.MiListImagenesRestaurantesAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private ArrayList<Integer> logosRestaurantesListaInicial, logosRestaurantes;
	private Set<String> nombresRestaurantes;
	
	private HandlerDB sql;
	private SQLiteDatabase db;
	
	public AlertDialog ventanaEmergente;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        importarBaseDatatos();
    	crearRestaurantesListView();
		
		// Cerramos la base de datos
    	sql.close();
    	
    	cargarPedidoAnterior();
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
    	
    	/*
    	 * FIXME Los restaurantes de abajo están añadidos a la lista manualmente, puesto
    	 * que al no haber información sobre ellos en la bd no saldrían si no y queremos
    	 * que sagan.
    	 */
    	logosRestaurantesListaInicial.add(getResources().getIdentifier("fridays","drawable",this.getPackageName()));
    	logosRestaurantesListaInicial.add(getResources().getIdentifier("ginos","drawable",this.getPackageName()));
    	
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
        	sql = new HandlerDB(getApplicationContext()); 
        	db = sql.open();
        }catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        }	
	}

	public void lanzar(int posicion){
		if (posicion > 1) {
			//Creación y configuración de la ventana emergente
			ventanaEmergente = new AlertDialog.Builder(MainActivity.this).create();
			View vistaAviso = LayoutInflater.from(MainActivity.this).inflate(R.layout.aviso_restaurante_no_disponible, null);
			ventanaEmergente.setView(vistaAviso);
			ventanaEmergente.show();
			
			//Crea el timer para que el mensaje solo aparezca durante 3 segundos
			final Timer t = new Timer();
			t.schedule(new TimerTask() {
				public void run() {
					ventanaEmergente.dismiss(); 
					t.cancel(); 
				}
			}, 3500);		
		}else {
			String nombreRestaurante;
			int i = 0;
			
			Iterator<String> it = nombresRestaurantes.iterator();
			while(it.hasNext() && i<posicion){
				it.next();
				i++;
			}
			nombreRestaurante = it.next();
	    	
			//Almacenamos la posición en la lista del ultimo restaurante selecionado para que al abrir la aplicación podamos cargarlo
			setUltimoRestaurante(posicion);
			
	    	Intent intent = new Intent(this,InicializarRestaurante.class);
			intent.putExtra("nombreRestaurante", nombreRestaurante);
	    	intent.putExtra("logoRestaurante",logosRestaurantes.get(posicion));
	    	startActivity(intent);
		}
    }
	
	private boolean baseDeDatosPedidoyCuentaVacias() {
		try{
			HandlerDB sqlPedido = new HandlerDB(getApplicationContext(),"Pedido.db"); 
			SQLiteDatabase dbPedido = sqlPedido.open();
			HandlerDB sqlCuenta=new HandlerDB(getApplicationContext(),"Cuenta.db"); 
			SQLiteDatabase dbCuenta = sqlCuenta.open();
			
			String[] camposPedido = new String[]{"Id"};//Campos que quieres recuperar
			Cursor cursorPedido = dbPedido.query("Pedido", camposPedido, null, null,null, null,null);
			if(!cursorPedido.moveToFirst()){
				String[] camposCuenta = new String[]{"Id"};//Campos que quieres recuperar
				Cursor cursorCuenta = dbCuenta.query("Cuenta", camposCuenta, null, null,null, null,null);
				if(!cursorCuenta.moveToFirst()){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
        }catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
         	return true;
        }
	}

	public void setUltimoRestaurante(int posicion){
		//Almacenamos la posicion del restaurante de la lista
		SharedPreferences preferencia = getSharedPreferences("Nombre_Restaurante", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("PosicionRestaurante", posicion);
		editor.commit(); //Para que surja efecto el cambio
	}
	
	public int getUltimoRestaurante(){
		//SharedPreferences nos permite recuperar datos aunque la aplicacion se haya cerrado
		SharedPreferences ultimoRestaurante = getSharedPreferences("Nombre_Restaurante", 0);
		return ultimoRestaurante.getInt("PosicionRestaurante", -1); // -1 es lo que devuelve si no hubiese nada con esa clave
	}
	
	public void onClickBotonAceptarAlertDialog(AlertDialog.Builder ventanaEmergente, final int posicion){
		ventanaEmergente.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				lanzar(posicion);
			}
		});
	}
	
	public void onClickBotonCancelarAlertDialog(AlertDialog.Builder ventanaEmergente){
		ventanaEmergente.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				try{
					HandlerDB sqlPedido = new HandlerDB(getApplicationContext(),"Pedido.db"); 
					SQLiteDatabase dbPedido = sqlPedido.open();
					HandlerDB sqlCuenta = new HandlerDB(getApplicationContext(),"Cuenta.db"); 
					SQLiteDatabase dbCuenta = sqlCuenta.open();
					dbPedido.delete("Pedido", null, null);
					dbCuenta.delete("Cuenta", null, null);
					sqlPedido.close();
					sqlCuenta.close();
	
					//Almacenamos la posicion del restaurante de la lista
					SharedPreferences preferencia = getSharedPreferences("Identificador_Unico", 0);
					SharedPreferences.Editor editor = preferencia.edit();
					editor.putInt("identificadorUnicoHijoPedido", 0); //Reseteamos el identificador unico de los pedidos
					editor.commit(); //Para que surja efecto el cambio
		        
				}catch(SQLiteException e){
		         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
		        }	
			}
		});
	}
	
	public void cargarPedidoAnterior(){
    	 
		int posicion = getUltimoRestaurante();
		
		if(posicion!=-1 && !baseDeDatosPedidoyCuentaVacias()){ // Si nunca hemos ejecutado la aplicación no habra un restaurante seleccionado
			AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(MainActivity.this);
			onClickBotonAceptarAlertDialog(ventanaEmergente, posicion);
			onClickBotonCancelarAlertDialog(ventanaEmergente);
			View vistaAviso = LayoutInflater.from(MainActivity.this).inflate(R.layout.aviso_continuar_pedido, null);
			ventanaEmergente.setView(vistaAviso);
			ventanaEmergente.show();
		}
		
	}	
}

