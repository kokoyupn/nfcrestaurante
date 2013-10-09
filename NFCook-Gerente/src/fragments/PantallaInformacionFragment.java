package fragments;


import java.util.ArrayList;

import com.example.nfcook_gerente.InfoRestaurante;
import com.example.nfcook_gerente.R;

import adapters.ListInformacionAdapter;
import adapters.PadreListInformacion;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PantallaInformacionFragment extends Fragment {

	private View vista;
	private static ListInformacionAdapter adapterInformacion;
	private ListView listaRestaurantes;
	private ArrayList<PadreListInformacion> restaurantes;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_general, container, false);

	    // aquí debemos leer de la base de datos y cargar todos los restaurantes del gerente-------
	    // ArrayList<PadreListInformacion> restaurantes = cargaRestaurantesBBDD();
	    
	    restaurantes = new ArrayList<PadreListInformacion>();
		PadreListInformacion infoRes = new PadreListInformacion("Restaurante 1", 1);
		restaurantes.add(infoRes);
		restaurantes.add(infoRes);
		restaurantes.add(infoRes);
		restaurantes.add(infoRes);
		
		listaRestaurantes = (ListView)vista.findViewById(R.id.listaRestaurantes);
	    adapterInformacion = new ListInformacionAdapter(this.getActivity(), restaurantes); 

	    listaRestaurantes.setAdapter(adapterInformacion);
	    
	    listaRestaurantes.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	onClickRestaurante(position);
           }
        });
	    
	    return vista;
	}
	
	public void onClickRestaurante(int position){
		// al hacer click en un restaurante iremos a su descripción detallada

    	//nos llevara a la pantalla siguiente          	
    	PadreListInformacion pulsado = restaurantes.get(position);

    	Intent intentInfoRest = new Intent(PantallaInformacionFragment.this.getActivity(), InfoRestaurante.class);
    	intentInfoRest.putExtra("Restaurante", pulsado.getNombreRestaurante()); // Pasamos el restaurante pulsado

		startActivity(intentInfoRest);
	}
}
