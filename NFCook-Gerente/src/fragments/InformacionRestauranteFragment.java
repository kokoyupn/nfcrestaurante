package fragments;

import com.example.nfcook_gerente.R;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * @author: Alejandro Moran
 * 
 * Esta clase implementará el fragment de la información de un restaurante.
 * 
 * Implementa LocationListener para poder obtener la ubicación actual
 * al seleccionar la opción de Maps, para realizar la ruta de su posición
 * hasta el restaurante.
 * 
 * **/
public class InformacionRestauranteFragment extends Fragment implements LocationListener {

	private View vista;
	private String telefonoRestaurante, calleRestaurante;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_restaurante, container, false);
	   
	    // Leemos la información del restaurante 
	    Bundle bundleInfo = getActivity().getIntent().getExtras();
	    String nombreRestaurante = bundleInfo.getString("nombre");
	    telefonoRestaurante = bundleInfo.getString("telefono"); // global porque lo utilizaremos en onClick
	    calleRestaurante = bundleInfo.getString("calle"); // global porque lo utilizaremos en onClick
	    String cp = bundleInfo.getString("cp");
	    String poblacion = bundleInfo.getString("poblacion");
	    String logo = bundleInfo.getString("logo");
	    String imagenFachada = bundleInfo.getString("imagen");
	    float ratingRestaurante = bundleInfo.getFloat("rating");
	    
	    // logo restaurante
	    ImageView logoRes = (ImageView) vista.findViewById(R.id.logo_restaurante); 
	    int imagen = getResources().getIdentifier(logo, "drawable", getActivity().getPackageName());
	    logoRes.setImageResource(imagen);
	    
	    // nombre restaurante
	    TextView nombreRes = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombreRes.setText(nombreRestaurante); 
	    
	    // rating restaurante
	    RatingBar ratingRes = (RatingBar) vista.findViewById(R.id.ratingRestaurante);
	    ratingRes.setRating(ratingRestaurante);
	    //ratingRes.setEnabled(false);
		
	    // teléfono restaurante
	    TextView telefonoRes = (TextView) vista.findViewById(R.id.telefonoRestaurante);
	    telefonoRes.setText(Html.fromHtml("<u>" + telefonoRestaurante + "</u>")); 
	    telefonoRes.setOnClickListener(new View.OnClickListener() {
	        @Override
			public void onClick(View arg0) {
	        	onClickPhoneCall(); 
	        }
	    });
	    
	    // dirección restaurante
	    TextView calleRes = (TextView) vista.findViewById(R.id.calleRestaurante);
	    calleRes.setText(calleRestaurante); 
	    calleRes.setOnClickListener(new View.OnClickListener() {
	        @Override
			public void onClick(View arg0) {
	        	onClickDirections();
	        }
	    });
	   
	    // CP restaurante
	    TextView cpRes = (TextView) vista.findViewById(R.id.cpRestaurante);
	    cpRes.setText(cp);
	    
	    // poblacion restaurante
	    TextView poblacionRes = (TextView) vista.findViewById(R.id.poblacionRestaurante);
	    poblacionRes.setText(poblacion);
	    
	    // botón llamar
	    Button botonLlamar = (Button) vista.findViewById(R.id.llamar);
	    botonLlamar.setOnClickListener(new View.OnClickListener() {
	        @Override
			public void onClick(View arg0) {
	        	onClickPhoneCall(); 	        	
	        }
	    });
	    
	    // botón mapas
	    Button botonMapas = (Button) vista.findViewById(R.id.direcciones);
	    botonMapas.setOnClickListener(new View.OnClickListener() {
	        @Override
			public void onClick(View arg0) {
	        	onClickDirections(); 	        	
	        }
	    });
	    
	    // imagen fachada restaurante
	    ImageView imagenRes = (ImageView) vista.findViewById(R.id.imagenRestaurante); 
	    imagen = getResources().getIdentifier(imagenFachada, "drawable", getActivity().getPackageName());
	    imagenRes.setImageResource(imagen);
		
	    return vista;
	}
	
	
	// Este método nos permitirá obtener la ubicación actual y llamará a Maps con la ruta hasta al restaurante
	private void onClickDirections(){

	    	double latitudOrigen = 0.0;
	    	double longitudOrigen = 0.0;
	    	LocationManager locationManager;
	    	Location miUbicacion;
	    	
	    	// cogemos nuestra localización actual
	    	locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    	miUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	
	    	if (miUbicacion == null){
	    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	    		miUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    	}
	    	latitudOrigen = miUbicacion.getLatitude();
	    	longitudOrigen = miUbicacion.getLongitude();
	    	
	    	// lanzamos Maps con la ruta definida
	    	Intent intent = new Intent(Intent.ACTION_VIEW, 
	    				Uri.parse("http://maps.google.com/maps?f=d&saddr="+calleRestaurante+"&daddr="
	    							+latitudOrigen+","+longitudOrigen));
			intent.setComponent(new ComponentName("com.google.android.apps.maps", 
					"com.google.android.maps.MapsActivity"));
            startActivity(intent);
	    	
	}
	
	
	// En este método llamaremos a ACTION_CALL para llamar al pulsar el número de teléfono o el botón
	private void onClickPhoneCall(){
		try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + telefonoRestaurante)); // "tel:"+ telefono
            startActivity(callIntent);
		} catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
            activityException.printStackTrace();
		}
	}

	
	/* Métodos para LocationListener
	 * no hace falta rellenarlos porque son solo para realizar acciones
	 * al cambio de localización, al estar disabled, enabled, o al cambiar el estado.
	 */
	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	
}
