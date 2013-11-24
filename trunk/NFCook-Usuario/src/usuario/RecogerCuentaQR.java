package usuario;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.example.nfcook.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;
import baseDatos.HandlerDB;

public class RecogerCuentaQR extends Activity {
	
	private HandlerDB sqlCuenta, sqlMiBase, sqlEquivalencia;
	private SQLiteDatabase dbCuenta, dbMiBase, dbEquivalencia;
	private String restaurante, abreviaturaRest, codigoRest;
	public static boolean enviarPorEmail;
	public static ArrayList<ContentValues> mensajesQR; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recoger_cuenta_qr);
		enviarPorEmail = false;
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		restaurante = bundle.getString("Restaurante");
		
 	    abrirBasesDeDatos();
		
 	    //obtenemos el codigo y la abreviatura del rest
 	    obtenerCodigoYAbreviaturaRestaurante();
        
		//Lectura QR
		Intent intent = new Intent("com.example.nfcook.SCAN");
		intent.putExtra("SCAN_MODE","QR_CODE_MODE");
		startActivityForResult(intent,0);
	}
	
	/**
	 * Metodo al que entra cuando se termina la captura del codigo QR
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
           if (resultCode == RESULT_OK) {
        	  // escaneo correcto
              String pedidoQR = intent.getStringExtra("SCAN_RESULT");
              // decodificamos el codigo y se mete en la base de datos
              decodificarPlatos(pedidoQR);  
           }
           cerrarBasesDeDatos();
           
           Intent intentToIniciarRestaurante = new Intent();
           intentToIniciarRestaurante.putExtra("Origen", "Cuenta");
           intentToIniciarRestaurante.putExtra("Tipo", "QR");
	       setResult(RESULT_OK, intentToIniciarRestaurante);
	       
           finish();
        }
	}
	
	/**
	 * Metodo que decodifica el string que ha leido del QR.
	 * Va comprobando el id, si tiene extras y comentarios hasta que se encuentre un 255 como id
	 * que significa que ha terminado
	 * @param pedidoQR
	 */
	private void decodificarPlatos(String pedidoQR) {
		mensajesQR= new ArrayList<ContentValues>();
		// separamos por platos
		StringTokenizer stPlatos = new StringTokenizer(pedidoQR,"@");
			
		// Compruebo si el pedido que hemos leido corresponde a este restaurate.
		if (codigoRest.equals(stPlatos.nextToken())){
		
			borrarCuentaActual();
			
			while(stPlatos.hasMoreElements()){
				String id = stPlatos.nextToken();
				anadirPlatoACuenta(abreviaturaRest+id);
			}	
			Toast.makeText(this, "Cuenta recogida correctamente", Toast.LENGTH_LONG).show();
		}
		else Toast.makeText(getApplicationContext(), "No se encuentra en el restaurante correcto.", Toast.LENGTH_LONG).show();
	}
	
	
    private void borrarCuentaActual() {
    	try{
			dbCuenta.delete("Cuenta", null, null);
		} catch(SQLiteException e){
         	Toast.makeText(this,"NO EXISTE",Toast.LENGTH_SHORT).show();
        }		
		
	}

/************************************ BASES DE DATOS  ****************************************/		
		
	/**
	 * Abre las bases de datos
	 */
	private void abrirBasesDeDatos() {
		sqlCuenta = null;
		sqlMiBase = null;
		sqlEquivalencia = null;
		dbCuenta = null;
		dbMiBase = null;
		dbEquivalencia = null;

		try {
			sqlMiBase = new HandlerDB(getApplicationContext(), "MiBase.db");
			dbMiBase = sqlMiBase.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS MI BASE: Recoger Cuenta QR (abrirBasesDatos)");
		}
		try {
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS CUENTA: Recoger Cuenta QR (abrirBasesDatos)");
		}
		try {
			sqlEquivalencia = new HandlerDB(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbEquivalencia = sqlEquivalencia.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS DE EQUIVALENCIAS: Recoger Cuenta QR (abrirBasesDatos)");
		}
	}
	
	/**
	 * Añade en Cuenta.db los platos que ha leido por QR
	 */
	private void anadirPlatoACuenta(String id){
		
		//Campos que quieres recuperar
		String[] campos = new String[]{"Id","Nombre","Precio"};
		String[] datosRestaurante = new String[]{restaurante,id};	
		Cursor cursorCuenta = dbMiBase.query("Restaurantes", campos, "Restaurante=? AND Id=?", datosRestaurante,null, null,null);
    	
    	if (cursorCuenta.moveToNext()){
    		ContentValues platoCuenta = new ContentValues();
    		platoCuenta.put("Id", cursorCuenta.getString(0));
        	platoCuenta.put("Plato", cursorCuenta.getString(1));
        	platoCuenta.put("Ingredientes", "");
        	platoCuenta.put("Extras", "");
        	platoCuenta.put("PrecioPlato",cursorCuenta.getDouble(2));
        	platoCuenta.put("Restaurante",restaurante);
        	platoCuenta.put("IdHijo", 0);
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    		
    		ContentValues mensajeCuenta = new ContentValues();
    		mensajeCuenta.put("Plato", cursorCuenta.getString(1));
    		mensajeCuenta.put("PrecioPlato",cursorCuenta.getDouble(2));
    		mensajesQR.add(mensajeCuenta);
    	}		
	}	
	
	/**
	 * MEtodo que da valor al codigo y a la abreviatura del restaurante
	 */
	private void obtenerCodigoYAbreviaturaRestaurante() {
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbEquivalencia.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);
		
		cursorPedido.moveToFirst();
		codigoRest = cursorPedido.getString(0);
		abreviaturaRest = cursorPedido.getString(1);
	}
	

	/**
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		dbEquivalencia.close();
	}
}