package fragments;

import com.example.nfcook.R;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PromocionesFragment extends Fragment {
	
	private String restaurante;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActivity().getActionBar();
    	actionbar.setTitle(" PROMOCIONES");
    	
		View viewPromociones = inflater.inflate(R.layout.promociones, container, false);
		WebView webPromociones = (WebView) viewPromociones.findViewById(R.id.webViewPromociones);
		webPromociones.getSettings().setJavaScriptEnabled(true);  
		
		webPromociones.setWebViewClient(new WebViewClient()
	        {
	            // evita que los enlaces se abran fuera nuestra app en el navegador de android
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url)
	            {
	                return false;
	            }   
	             
	        });
		
		webPromociones.getSettings().setBuiltInZoomControls(false); 
		webPromociones.getSettings().setSupportZoom(false);
		webPromociones.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);   
		webPromociones.getSettings().setAllowFileAccess(true); 
        webPromociones.getSettings().setDomStorageEnabled(true);
		if(restaurante.equals("Foster")){
			webPromociones.loadUrl("http://www.elchequegorron.es/");
		}else if(restaurante.equals("VIPS")){
			webPromociones.loadUrl("http://www.vips.es/promociones");
		}

		return webPromociones;
    }

	public void setRestaurante(String restaurante) {
		this.restaurante = restaurante;
	}

}
