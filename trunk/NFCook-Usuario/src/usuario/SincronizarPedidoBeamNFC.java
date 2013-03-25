package usuario;

import com.example.nfcook.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SincronizarPedidoBeamNFC extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.sincronizar_pedido_beam_nfc);
     
	}
}
 