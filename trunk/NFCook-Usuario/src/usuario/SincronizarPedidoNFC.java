package usuario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

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


public class SincronizarPedidoNFC extends Activity implements DialogInterface.OnDismissListener {
	
	private ProgressDialog	progressDialogSinc;
	private String restaurante;
	private HandlerDB sqlCuenta, sqlPedido;
	private SQLiteDatabase dbCuenta, dbPedido;
	
		/**
		 * Clase interna creada para ejecutar en segundo plano las tareas de apertura de bases de datos,
		 * codificacion de pedido, escritura NFC, transferir de pedido a cuenta y cierre bases de datos
		 */
		 public class SincronizarPedidoBackgroundAsyncTask extends AsyncTask<Void, Void, Void> {
	  
		  /**
		   * Se ejecuta antes de doInBackground.
		   * Abre las bases de datos y muestra el progresDialog ya creado	
		   */
		  @Override
		  protected void onPreExecute() {
			  abrirBasesDeDato();
			  progressDialogSinc.show(); //Mostramos el diálogo antes de comenzar
	       }
		
		  /**
		   * Ejecuta en segundo plano
		   * Codifica el pedido, envia a NFC y transfiere los platos de pedido a cuenta
		   */
		  @Override
		  protected Void doInBackground(Void... params) {	  		  
			  SystemClock.sleep(2000);
			  codificarPedido();
			  enviarPedidoACuenta();
			  return null;
		  }
		  
		  /**
		   * Se ejecuta cuando termina doInBackground,
		   * Cierra las bases de datos y tambien el progressDialog
		   */
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

        setContentView(R.layout.sincronizar_pedido_nfc);
        
        //El numero de la mesa se obtiene de la pantalla anterior
  		Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("Restaurante");
  			
  		// creamos el progresDialog que se mostrara
  		crearProgressDialogSinc();
  		
  		// ejecuta el progressDialog, codifica e intercambia datos de pedido a cuenta en segundo plano
  		new SincronizarPedidoBackgroundAsyncTask().execute();
  		          
	}
	
	/**
	 * Cierra la actividad y muestra un mensaje.
	 * Se ejecuta cuando se cierra el progressDialog
	 */
	public void onDismiss(DialogInterface dialog) {
		Toast.makeText(this, "Pedido sincronizado correctamente. Puedes verlo en cuenta.", Toast.LENGTH_LONG ).show();		
        finish();	
	}
	
	/**
	 * Crea un progressDialog con el formato que se quiera
	 */
	private void crearProgressDialogSinc() {
		progressDialogSinc = new ProgressDialog(this);
  		progressDialogSinc.setIndeterminate(true);
  		progressDialogSinc.setMessage("Espere unos segundos...");
  		progressDialogSinc.setTitle("Sincronizando pedido");
  		progressDialogSinc.setCancelable(false);
  	    // listener para que ejecute el codigo de onDismiss
  		progressDialogSinc.setOnDismissListener(this);
	}
	

/************************************ BASES DE DATOS  ****************************************/		
	
	/*FIXME puede que reviente si entra en el catch y hace el Toast pro estar dentro de async.
	 * Poner Log.i() */
	/**
	 * Abre las bases de datos Cuenta y Pedido.
	 */
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
	
	/**
	 * Borra de la base de datos Pedido.db los platos que haya para introducirlos en la base
	 * de datos Cuenta.db
	 */
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
	

	/**
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlPedido.close();		
	}
	

/********************************* CODIFICACION  ************************************/	
	
	/**
	 * Codifica el pedido para ser transferido por NFC
	 */
	private void codificarPedido(){
		String pedidoStr = damePedidoStr();
		ArrayList<Byte> al = codificarPlatos(pedidoStr);
		System.out.println(al);
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
	
	/**
	 * Codifica el pedido (listaDePlatos) y lo devuelve con un formato de ArrayList<Byte>.
	 * Para ello va separando mediante StringTokenizer los platos, obteniendo su id, 
	 * extras y observaciones.
	 * @param listaPlatos
	 * @return
	 */
	private ArrayList<Byte> codificarPlatos(String listaPlatos) {
		ArrayList <Byte> codificado = new ArrayList <Byte>();
		
		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(listaPlatos,"@");
		
		while(stPlatos.hasMoreElements()){
			
			String plato = stPlatos.nextToken();
			StringTokenizer stTodoSeparado =  new StringTokenizer(plato,"+,*");
					
			// id
			String id =  stTodoSeparado.nextToken();
			codificado.addAll(codificaIdPlato(id));
					
			// extras
			if (plato.contains("+"))  {
				String extras =  stTodoSeparado.nextToken();
				ArrayList <Byte> alExtras = codificaExtras(extras);
				// tamaño de los extras que habra que leer
				codificado.add((byte) alExtras.size());
				codificado.addAll(alExtras);	
			} else codificado.add((byte) 0);
					
			// comentarios
			if (plato.contains("*"))  {
				String comentario =  stTodoSeparado.nextToken();
				ArrayList <Byte> alComentario = codificaComentario(comentario);
				// tamaño de comentarios que habra que leer
				codificado.add((byte) alComentario.size());
				codificado.addAll(alComentario);
			} else codificado.add((byte) 0);
					
		}
		return codificado;	
	}
	
	/**
	 * Codifica el parametro de entrada comentrario y lo devuelve en formato
	 * de un arrayList<Byte>
	 * @param comentario
	 * @return
	 */
	private ArrayList<Byte> codificaComentario(String comentario) {
		ArrayList<Byte> al = new ArrayList<Byte>();
		for (int i = 0; i<comentario.length(); i++)
			al.add((byte) comentario.charAt(i));
		return al;
	 }
	
	/**
	 * Codifica el parametro de entrada id y lo devuelve en formato
	 * de un arrayList<Byte>. Será siempre un byte lo que ocupe pues lo que vamos a introducir
	 * sera su valor en binario por lo cual en 1 byte podremos meter 255 id's diferentes
	 * @param id
	 * @return
	 */
	private ArrayList<Byte> codificaIdPlato(String id) {
		ArrayList<Byte> al = new ArrayList<Byte>();
		al.add((byte) Integer.parseInt(id));
		return al;
	}
	
	/**
	 * Codifica el parametro de entrada extras y lo devuelve en formato de un arrayList<Byte>. 
	 * Primero miramos el numero de extras que vienen y hacemos mod 8 ya que en un byte meteremos
	 * 8 extras distintos por lo cual si si tenemos 22 extras nos sobren 2 huecos para llegar a 24
	 * entonces meteremos 0's para rellenarlo.
	 * Luego vamos generando 1 byte cada 8 extras.
	 * @param extras
	 * @return
	 */
	private ArrayList<Byte> codificaExtras(String extras) {
		
		ArrayList<Byte> al = new ArrayList<Byte>();
		int relleno = 0;
		int  numMod8 = extras.length() % 8;
		if (numMod8 != 0){
			// significa que quedan hueco por rellenar
			relleno = 8-numMod8;
			// rellenaremos con 0's los extras que nos falten por rellenar
			for (int p = 0; p<relleno; p++)
				extras = extras + "0";
		}
	
		int veces = extras.length()/8;
		int num;
		int posicion = 0;
		// vamos creando tantos bytes como nos hagan falta para codificar todos los extras
		for (int i = 0 ; i < veces; i++){  
			num = binToDec(extras.substring(posicion,posicion+8));
			posicion += 8;
			al.add((byte) ((char)num));	
		}
	
		return al;	 
	}
	
	/**
	 * Convierte un string binario a decimal
	 * @param pNumBin
	 * @return
	 */
	private int binToDec(String numBin) {        
		int resultado = 0 ;        
	    for( int i = 0; i < numBin.length() ; i++ ) {
	    	char digito = numBin.charAt( i ); 
	        // resultado = resultado * base + digito
	        try {         
	        	int valDigito = Integer.parseInt( Character.toString(digito) ) ;
	            resultado = resultado * 2 + valDigito ;    
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }    
	    }
	    return resultado ;       
	}


/************************************ ESCRITURA NFC  ****************************************/	
	
	
	
	
/************************************ GETTERS, SETTERS ****************************************/	
	
	
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
 