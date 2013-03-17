package usuario;

import com.example.nfcook.R;

import fragments.PedidoFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class SincronizarPedido extends Activity implements DialogInterface.OnDismissListener {
	
	private ProgressDialog	progressDialogSinc;
	private AlertDialog.Builder alertaSincCorrecta;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.sincronizar_pedido);
        
        sincronizarPedido();
           
	}
	
	
	
	public void onDismiss(DialogInterface dialog) {
		
		
		Toast.makeText(this, "Pedido sincronizado correctamente", Toast.LENGTH_LONG ).show();
		/*
		alertaSincCorrecta  = new AlertDialog.Builder(this);
		alertaSincCorrecta.setMessage("Pincha en cuenta para verlo");
		alertaSincCorrecta.create();
		alertaSincCorrecta.show();
		try {
			alertaSincCorrecta.wait(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
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
					Thread.sleep(3000);
					
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
 