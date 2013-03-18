package usuario;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import fragments.PedidoFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class SincronizarPedido extends Activity implements DialogInterface.OnDismissListener {
	
	private ProgressDialog	progressDialogSinc;
	private AlertDialog.Builder alertaSincCorrecta;
	private String restaurante;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.sincronizar_pedido);
        
      //El numero de la mesa se obtiene de la pantalla anterior
  		Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("Restaurante");
        
  		new Thread(new Runnable() {
  		    public void run() {
  		        //Aquí ejecutamos nuestras tareas costosas
  		    	cargarBaseDeDatosCuenta();
  		    }
  		}).start();	
  		
  		sincronizarPedido();
           
	}
	
	private void cargarBaseDeDatosCuenta(){
		HandlerDB sqlCuenta = null, sqlPedido = null;
		SQLiteDatabase dbCuenta = null, dbPedido = null;
		
		try{
			sqlPedido = new HandlerDB(getApplicationContext(), "Pedido.db");
			dbPedido = sqlPedido.open();
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS PEDIDO, SINCRONIZAR NFC",Toast.LENGTH_SHORT).show();
		}
		try{
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS CUENTA, SINCRONIZAR NFC",Toast.LENGTH_SHORT).show();
		}
		
		String[] campos = new String[]{"Id","Plato","Observaciones","Extras","PrecioPlato","Restaurante"};//Campos que quieres recuperar
		String[] datosRestaurante = new String[]{restaurante};	
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);
    	
    	while(cursorPedido.moveToNext()){
    		ContentValues platoCuenta = new ContentValues();
        	platoCuenta.put("Id", cursorPedido.getString(0));
        	platoCuenta.put("Plato", cursorPedido.getString(1));
        	platoCuenta.put("Observaciones", cursorPedido.getString(2));
        	platoCuenta.put("Extras", cursorPedido.getString(3));
        	platoCuenta.put("PrecioPlato",cursorPedido.getDouble(4));
        	platoCuenta.put("Restaurante",cursorPedido.getString(5));
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    	}
		sqlCuenta.close();
		try{
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);	
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"ERROR AL BORRAR BASE DE DATOS PEDIDO",Toast.LENGTH_SHORT).show();
		}
		sqlPedido.close();
		sqlPedido.close();
	}
	
	
	
	public void onDismiss(DialogInterface dialog) {
		
		Toast.makeText(this, "Pedido sincronizado correctamente", Toast.LENGTH_LONG ).show();		
        finish();
		
	}

	public void onClickNFCVolver(View v){
		finish();
	}
	
	
	private void sincronizarPedido() {
		
		// sale un mensaje de espera mediente un dialogo
		progressDialogSinc = ProgressDialog.show(this, "Sincronizando pedido", "Espere unos segundos...", true, false);	
		// listener para que ejecute el codigo de onDismiss
		progressDialogSinc.setOnDismissListener(this);
		
		Thread hiloProgressDialog = new Thread(new Runnable() { 
			public void run() {
				try {
					Thread.sleep(4000);
					
				} catch (InterruptedException e) { 
					Log.i("Thead: ","Error en hilo de sincronizar pedido");
				}
				progressDialogSinc.dismiss();
				
			}

		});
		
		hiloProgressDialog.start();
	}  
	
	/**TODO Falta poner el codigo para NFC
	 * */
}
 