package fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook_gerente.R;

public class InformacionRestauranteFragment extends Fragment {

	private View vista;
	private String telefonoRestaurante;
	private double latitud, longitud;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_restaurante, container, false);
	    
	    // Leemos la información del restaurante 
	    // OJOOOOOOOOOOO          HABRÁ QUE RECOGER MÁS INFO DE LA BASE DE DATOS
	    Bundle bundleInfo = getActivity().getIntent().getExtras();
	    String nombreRestaurante = bundleInfo.getString("nombre");
	    telefonoRestaurante = bundleInfo.getString("telefono");
	    String calleRestaurante = bundleInfo.getString("calle");
	    String logoRestaurante = bundleInfo.getString("logo");
	    
	    TextView nombreRes = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombreRes.setText(nombreRestaurante); 
	     
	    TextView telefonoRes = (TextView) vista.findViewById(R.id.telefonoRestaurante);
	    telefonoRes.setText(Html.fromHtml("<b>Tel.: </b>" + "<u>" + telefonoRestaurante + "</u>")); 
	    telefonoRes.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickPhoneCall(); 
	        }
	    });
	    
	    TextView calleRes = (TextView) vista.findViewById(R.id.direccionRestaurante);
	    calleRes.setText(Html.fromHtml("<b>Dir.: </b>" + "<u>" + calleRestaurante + "</u>")); 

	    ImageView imagenRestaurante = (ImageView) vista.findViewById(R.id.imagenLogo); // imagen del logo
	    int img = getResources().getIdentifier(logoRestaurante, "drawable", getActivity().getPackageName());
	    imagenRestaurante.setImageResource(img);
	    
	    Button botonLlamar = (Button) vista.findViewById(R.id.llamar);
	    botonLlamar.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickPhoneCall(); 
	        }
	    });
	    
	    /*
	    try {
	    	Geocoder g = new Geocoder(getActivity());
	    	List<Address> direcciones = g.getFromLocationName(calleRestaurante, 1);
	    	Address direccion = direcciones.get(0);
	    	latitud = direccion.getLatitude();
	    	longitud = direccion.getLongitude();
	    	
		} catch (IOException e) {
			Log.i("error maps","error al encontrar la dirección del restaurante");
		}
		
		calleRes.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	
	        	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
	        		    Uri.parse("http://maps.google.com/maps?saddr="+direccion.getLatitude()+"20.344,34.34&daddr=20.5666,45.345"));
	        		startActivity(intent);
	        }
	    });
		
	    */

	    return vista;
	}
	
	
	// En este metodo llamaremos a ACTION_CALL para llamar al pulsar el número de teléfono
	private void onClickPhoneCall(){
		try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+telefonoRestaurante)); // "tel:"+ telefono
            startActivity(callIntent);
     } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
     }
	}
}
