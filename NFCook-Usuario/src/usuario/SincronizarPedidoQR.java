package usuario;
import baseDatos.HandlerDB;

import com.example.nfcook.R;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.encode.QRCodeEncoder;

import fragments.ContenidoTabSuperiorCategoriaBebidas;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class SincronizarPedidoQR extends Activity {
 
	private HandlerDB sqlCuenta, sqlPedido, sqlRestaurante;
	private SQLiteDatabase dbCuenta, dbPedido, dbRestaurante;
	private String restaurante;
	private boolean fueGeneradoBienQR;
	private String abreviaturaRest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sincronizar_pedido_qr);   
        
     // Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" SINCRONIZAR PEDIDO");
        
        // El numero de la mesa se obtiene de la pantalla anterior
  		Bundle bundle = getIntent().getExtras();
  		restaurante = bundle.getString("Restaurante");
  		// genero QR
        abrirBasesDeDatos();
        generarQR(damePedidoStr()); 
        if (fueGeneradoBienQR) enviarPedidoACuenta();
        cerrarBasesDeDatos();
    }
    
    /**
     * Redefino el metodo onBackPressed para que cuando se pulse el boton back
     * del movil salga un alert dialog avisando de si se quiere salir o no cerrando 
     * la actividad (y yendo a cuenta) o quedandose en ella respectivamente
     */
    @Override
    public void onBackPressed() {
    	// si se ha generado el QR muestro el aviso
    	if (fueGeneradoBienQR) {
	    	//creo el alert dialog que se mostrara al pulsar en el boton back
	    	AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(this);
			onClickBotonAceptarAlertDialog(ventanaEmergente);
			onClickBotonCancelarAlertDialog(ventanaEmergente);
			View vistaAviso = LayoutInflater.from(this).inflate(R.layout.aviso_continuar_pedido, null);
			//modifico el texto a mostrar
			TextView textoAMostar = (TextView) vistaAviso.findViewById(R.id.textViewInformacionAviso);
			textoAMostar.setText("¿Está seguro que desa salir?. \n\nSu pedido será trasnferido a Cuenta y el código QR desaparecerá." +
					"\n\n Antes de salir asegurese de que el camarero haya leido su código QR. ");
			ventanaEmergente.setView(vistaAviso);
			ventanaEmergente.show();
    	} else {
    		// no se ha generado y salgo directamente volviendo a pedido
    		setResult(RESULT_CANCELED, null);
    		finish();
    	}
    }

    /**
     * boton NO del alert dialog. No hace nada pero por debajo cierra el dialog.
     * @param ventanaEmergente
     */
	private void onClickBotonCancelarAlertDialog(Builder ventanaEmergente) {
		ventanaEmergente.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			
			}
		});
	}
	
	/**
	 * boton SI del alert dialog. Finaliza la actividad y cierra el dialog por debajo. 
	 * @param ventanaEmergente
	 */
	private void onClickBotonAceptarAlertDialog(Builder ventanaEmergente) {
		ventanaEmergente.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				setResult(RESULT_OK, null);
				finish();
			}
		});
	}

	/**
	 * Genera un codigo QR usando la libreria ZXing. Primero analiza el tamaño de la pantalla
	 * del dispositivo y en funcion de eso genera un QR del tamaño adecuado.
	 * Hay codigo diferente para calcular el tamaño para que funcione en todas las apis.
	 * Si se genera bien el QR se muestra en pantalla.
	 * @param pedido
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void generarQR(String pedido){ 
		// Para establecer tamaño
		 WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		 Display display = manager.getDefaultDisplay();
		 int width, height;
         //añadido para que funcione desde la api 11
		 if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13 ) {
	         width = display.getWidth();
	         height = display.getHeight();
	     } else {
	    	 Point size = new Point();
	         display.getSize(size);
	         width = size.x;
	         height = size.y;
	     }
		 int smallerDimension = width < height ? width : height;
		 smallerDimension = smallerDimension * 7 / 8;
		
		// Uso de la libreria para preparar la generacion de codigo
		Intent intent = new Intent();
		intent.setAction("com.google.zxing.client.android.ENCODE");
		intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
		intent.putExtra("ENCODE_DATA", pedido);
		 
		// Generar QR
		Bitmap bitmap = null;
		QRCodeEncoder qrCodeEncoder = null;
		try {
			// genera el QR del tamaño calculado anteriormente y el intent de la libreria
			qrCodeEncoder = new QRCodeEncoder(this, intent, smallerDimension, false);
			// para que te devuelva el QR como bitmap
			bitmap = qrCodeEncoder.encodeAsBitmap();
			// pongo el QR en la imageView del layout
			ImageView imagenQR = (ImageView) this.findViewById(R.id.imageViewQR);
			imagenQR.setImageBitmap(bitmap);
			// mesaje para que avise al camarero
			Toast.makeText(this, "Pedido sincronizado correctamente. Avisa al camarero para que le haga una foto.", Toast.LENGTH_LONG).show();
			fueGeneradoBienQR = true;
		} catch (WriterException e) {
			// mesaje para que avise al camarero
			Toast.makeText(this, "Pedido muy grande. Avise al camarero por favor.", Toast.LENGTH_LONG).show();
			fueGeneradoBienQR = false;
		}
    }
	
/************************************ BASES DE DATOS  ****************************************/		
		
	
	private void abrirBasesDeDatos() {
		sqlCuenta = null;
		sqlPedido = null;
		sqlRestaurante = null;
		dbCuenta = null;
		dbPedido = null;
		dbRestaurante = null;

		try {
			sqlPedido = new HandlerDB(getApplicationContext(), "Pedido.db");
			dbPedido = sqlPedido.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosPedido)");
		}
		try {
			sqlCuenta = new HandlerDB(getApplicationContext(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS CUENTA: SINCRONIZAR NFC (cargarBaseDeDatosCuenta)");
		}
		try {
			sqlRestaurante = new HandlerDB(getApplicationContext(), "Equivalencia_Restaurantes.db");
			dbRestaurante = sqlRestaurante.open();
		} catch (SQLiteException e) {
			System.out.println("NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosResta)");
		}
	}
	
	/**
	 * Borra de la base de datos Pedido.db los platos que haya para introducirlos en la base
	 * de datos Cuenta.db
	 */
	private void enviarPedidoACuenta(){
		
		//Campos que quieres recuperar
		String[] campos = new String[]{"Id","Plato","Observaciones","Extras","PrecioPlato","Restaurante","IdHijo"};
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
        	platoCuenta.put("IdHijo", cursorPedido.getString(6));
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    	}
		
		try{
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);	
		}catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"ERROR AL BORRAR BASE DE DATOS PEDIDO",Toast.LENGTH_SHORT).show();
		}
		
		// Reinciamos la pantalla bebidas, porque ya hemos sincronizado el pedido
		ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas();
		
	}	
	

	/**
	 * Cierra las bases de datos Cuenta y Pedido
	 */
	private void cerrarBasesDeDatos() {
		sqlCuenta.close();
		sqlPedido.close();
		sqlRestaurante.close();
	}
	
	/** 
	 * Prepara el pedido en un string para que sea facil su tratamiento a la hora de escribir en la tag.
	 * Obtiene de la base de datos el pedido a sincronizar con la siguiente forma:
	 * "id_plato@id_plato+extras@5*Obs@id_plato+extras*Obs@";	
	 * "1@2@3@4+10010@5*Con tomate@1+01001*Con azucar@2+10010*Sin macarrones@";	
	 * 
	 * @return
	 */
	private String damePedidoStr() {
		String listaPlatosStr = dameCodigoRestaurante();
		
		String[] campos = new String[]{"Id","ExtrasBinarios","Observaciones","Restaurante"};//Campos que quieres recuperar
		String[] datosRestaurante = new String[]{restaurante};	
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);
    	
    	while(cursorPedido.moveToNext()){
    		
    		// le quito fh o v para introducir solo el id numerico en la tag
    		String idplato = cursorPedido.getString(0).substring(abreviaturaRest.length());
    		
        	// compruebo si hay extras y envio +Extras si hay y si no ""
    		String extrasBinarios = cursorPedido.getString(1);
    		if (extrasBinarios == null) extrasBinarios = "";
    		else extrasBinarios = "+" + extrasBinarios;
    		
    		// compruebo si hay observaciones y envio *Observaciones si hay y si no ""
    		String observaciones = cursorPedido.getString(2);
    		if (observaciones == null) observaciones = "";
    		else observaciones = "*" + observaciones;
    		
    		listaPlatosStr += idplato + extrasBinarios + observaciones +"@";     	
    	}
    	
    	return listaPlatosStr;
	}

	private String dameCodigoRestaurante() {
		// Campos que quieres recuperar
		String[] campos = new String[] { "Numero", "Abreviatura" };
		String[] datos = new String[] { restaurante };
		Cursor cursorPedido = dbRestaurante.query("Restaurantes", campos, "Restaurante=?",datos, null, null, null);
		
		cursorPedido.moveToFirst();
		String codigoRest = cursorPedido.getString(0) + "@";
		abreviaturaRest = cursorPedido.getString(1);
		
		return codigoRest;
	}

}
