package fragments;

import java.util.ArrayList;

import adapters.InfoRestauranteAdapter;
import adapters.PadreInfoRestaurante;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.nfcook_gerente.R;

public class InformacionRestauranteFragment extends Fragment {

	private View vista;
	private static InfoRestauranteAdapter adapterInformacion;
	private FrameLayout listaRestaurantes;
	private ArrayList<PadreInfoRestaurante> restaurantes;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_restaurante, container, false);
	    
	    // aquí debemos leer de la base de datos y cargar toda la información del restaurante ------
	    /* 
	     * Bundle bundle = getActivity().getIntent().getExtras();
	     * String restaurante = bundle.getString("Restaurante");
	     * ArrayList<PadreInfoRestaurante> infoRestaurante = cargaInfoRestarurante(restaurante);
	    */
	   
	    
		
		listaRestaurantes = (FrameLayout)vista.findViewById(R.id.listaRestaurantes);
	    adapterInformacion = new InfoRestauranteAdapter(this.getActivity(), restaurantes); 

	    // BUSCAR UN LAYOUT ADECUADO 
	    //listaRestaurantes.setAdapter(adapterInformacion);
	    
	    return vista;
	}
}
