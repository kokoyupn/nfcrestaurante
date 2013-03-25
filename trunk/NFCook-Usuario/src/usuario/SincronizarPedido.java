package usuario;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import fragments.ContenidoTabSuperiorCategoriaBebidas;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class SincronizarPedido extends Activity implements DialogInterface.OnDismissListener {
	
	private ProgressDialog	progressDialogSinc;
	private String restaurante;
	private HandlerDB sqlCuenta, sqlPedido;
	private SQLiteDatabase dbCuenta, dbPedido;
	
		public class BackgroundAsyncTask extends AsyncTask<Void, Void, Void> {
	  
		  @Override
		  protected void onPreExecute() {
			  abrirBasesDeDato();
			  progressDialogSinc.show(); //Mostramos el diálogo antes de comenzar
	       }
		
		  @Override
		  protected Void doInBackground(Void... params) {	  		  
			  SystemClock.sleep(2000);
			  codificarPedido();
			  enviarPedidoACuenta();
			  return null;
		  }
		  
		  @Override
		  protected void onPostExecute(Void result) {
			  cerrarBasesDeDatos();
			  progressDialogSinc.dismiss();
		  }
	
		}
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.sincronizar_pedido);
        
        //El numero de la mesa se obtiene de la pantalla anterior
  		Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("Restaurante");
  			
  		crearProgressDialogSinc();
  		
  		new BackgroundAsyncTask().execute();
  		          
	}
	
	public void onDismiss(DialogInterface dialog) {
		Toast.makeText(this, "Pedido sincronizado correctamente. Puedes verlo en cuenta.", Toast.LENGTH_LONG ).show();		
        finish();	
	}
	
	private void crearProgressDialogSinc() {
		progressDialogSinc = new ProgressDialog(this);
  		progressDialogSinc.setIndeterminate(true);
  		progressDialogSinc.setMessage("Espere unos segundos...");
  		progressDialogSinc.setTitle("Sincronizando pedido");
  		progressDialogSinc.setCancelable(false);
  	    // listener para que ejecute el codigo de onDismiss
  		progressDialogSinc.setOnDismissListener(this);
	}
	
	/*FIXME puede que reviente si entra en el catch y hace el Toast pro estar dentro de async.
	 * Poner Log.i() */
	private void abrirBasesDeDato() {
		sqlCuenta = null;
		sqlPedido = null;
		dbCuenta = null;
		dbPedido = null;
		
		try{
			sqlPedido = new HandlerDB(getApplicationContext(), "Pedido.db");
			dbPedido = sqlPedido.open();
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosCuenta)",Toast.LENGTH_SHORT).show();
		}
		try{
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS CUENTA: SINCRONIZAR NFC",Toast.LENGTH_SHORT).show();
		}
		
	}

	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlPedido.close();		
	}
	
	private void codificarPedido(){
		String pedidoStr = damePedidoStr();
		codificarPlatos(pedidoStr);		
	}

	/** Obtiene de la base de datos el pedido a sincronizar con la siguiente forma:
	 * "id_plato@id_plato+extras@5*Obs@id_plato+extras*Obs@";	
	 * "1@2@3@4+10010@5*Con tomate@1+01001*Con azucar@2+10010*Sin macarrones@";		
	 * @return
	 */
	private String damePedidoStr() {
		String pedidoStr = "";
		String[] campos = new String[]{"Id","ExtrasBinarios","Observaciones","Restaurante"};//Campos que quieres recuperar
		String[] datosRestaurante = new String[]{restaurante};	
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);
    	
    	while(cursorPedido.moveToNext()){
    		
    		String idplato = ""; 
    		if (restaurante.equals("Foster")) idplato = cursorPedido.getString(0).substring(2);
    		else if (restaurante.equals("Vips")) idplato = cursorPedido.getString(0).substring(1);
    		 
    		String extrasBinarios = cursorPedido.getString(1);
    		String observaciones = cursorPedido.getString(2);
        	
    		if (extrasBinarios == null) extrasBinarios = "";
    		else extrasBinarios = "+" + extrasBinarios;
    		
    		if (observaciones == null) observaciones = "";
    		else observaciones = "*" + observaciones;
    		
        	pedidoStr += idplato + extrasBinarios + observaciones +"@";     	
    	}
    	Log.i("PEDIDO: ", pedidoStr);
    	
    	/*FIXME revienta por dentro si haces un Toast en un thread, en un async, etc.*/
    	//Toast.makeText(getApplicationContext(),"PEDIDO: " + pedidoStr,Toast.LENGTH_LONG).show();
    	
    	return pedidoStr;
	}
	
	private void codificarPlatos(String pedidoStr) {

	}	

	private void enviarPedidoACuenta(){
		
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
		
		try{
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);	
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"ERROR AL BORRAR BASE DE DATOS PEDIDO",Toast.LENGTH_SHORT).show();
		}
		
		// Reinciamos la pantalla bebidas, porque ya hemos sincronizado el pedido
		ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas((Activity)this);
		
		/*
		 * FIXME Quitar cuando estén los fragments de bebida y calculadora
		 */
		InicializarRestaurante.setTabInferiorPulsado("tabCuenta");
	}
	
	

	public HandlerDB getSqlCuenta() {
		return sqlCuenta;
	}

	public void setSqlCuenta(HandlerDB sqlCuenta) {
		this.sqlCuenta = sqlCuenta;
	}

	public ProgressDialog getProgressDialogSinc() {
		return progressDialogSinc;
	}

	public void setProgressDialogSinc(ProgressDialog progressDialogSinc) {
		this.progressDialogSinc = progressDialogSinc;
	}

	public String getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(String restaurante) {
		this.restaurante = restaurante;
	}

	public HandlerDB getSqlPedido() {
		return sqlPedido;
	}

	public void setSqlPedido(HandlerDB sqlPedido) {
		this.sqlPedido = sqlPedido;
	}

	public SQLiteDatabase getDbCuenta() {
		return dbCuenta;
	}

	public void setDbCuenta(SQLiteDatabase dbCuenta) {
		this.dbCuenta = dbCuenta;
	}

	public SQLiteDatabase getDbPedido() {
		return dbPedido;
	}

	public void setDbPedido(SQLiteDatabase dbPedido) {
		this.dbPedido = dbPedido;
	}

}
 