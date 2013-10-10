package fragments;

import java.util.ArrayList;

import adapters.InfoRestauranteAdapter;
import adapters.PadreInfoRestaurante;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nfcook_gerente.R;

public class InformacionRestauranteFragment extends Fragment {

	private View vista;
	private static InfoRestauranteAdapter adapterInformacion;
	private RelativeLayout infoRestaurante;
	private ArrayList<PadreInfoRestaurante> restaurantes;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_restaurante, container, false);
	    
	    // aquí debemos leer de la base de datos y cargar toda la información del restaurante ------
	    /* 
	     * Bundle bundle = getActivity().getIntent().getExtras();
	     * String restaurante = bundle.getString("Restaurante");
	     * ArrayList<PadreInfoRestaurante> infoRestaurante = cargaInfoRestarurante(restaurante);
	    */
	   
	    infoRestaurante = (RelativeLayout)vista.findViewById(R.id.listaRestaurantes);
	    TextView nombreRestaurante = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombreRestaurante.setText("nombre restaurante"); // restaurante
	    
	    TextView telefonoRestaurante = (TextView) vista.findViewById(R.id.telefonoRestaurante);
	    telefonoRestaurante.setText("+34608088230"); // String telefono = infoRestaurante.getTelefono();
	    
	    TextView direccionRestaurante = (TextView) vista.findViewById(R.id.direccionRestaurante);
	    direccionRestaurante.setText("C/lopez de hoyos 122"); // direccion = infoRestaurante.getDireccion();
	    
	    // ImageView imagenRestaurante = (ImageView) vista.findViewById(R.id.imagenLogo); // anadiremos la imagen de la fachada del restaurante
	    // imagenRestaurante.setImageResource(MiExpandableListAnadirPlatoAdapter.getDrawable(context,platos.get(position).getImagen()));

		telefonoRestaurante.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickPhoneCall(); 
	        }
	    });
	    
	    // BUSCAR UN LAYOUT ADECUADO, no hace falta adapter, RelativeLayout
	    // adapterInformacion = new InfoRestauranteAdapter(this.getActivity(), restaurantes);
	    // listaRestaurantes.setAdapter(adapterInformacion);
	    
	    return vista;
	}
	
	
	private void onClickPhoneCall(){
		try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+41766242587")); // "tel:"+ telefono
            startActivity(callIntent);
     } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
     }
	}
}
