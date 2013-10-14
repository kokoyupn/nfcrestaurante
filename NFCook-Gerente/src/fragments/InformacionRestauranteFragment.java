package fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;

import com.example.nfcook_gerente.R;

public class InformacionRestauranteFragment extends Fragment {

	private View vista;
	private String telefonoRestaurante, calleRestaurante;
	//private GoogleMap map;
	private LocationManager locationManager;
	private Location miUbicacion;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    vista = inflater.inflate(R.layout.informacion_restaurante, container, false);
	    
	    // Leemos la información del restaurante 
	    // OJOOOOOOOOOOO          HABRÁ QUE RECOGER MÁS INFO DE LA BASE DE DATOS
	    Bundle bundleInfo = getActivity().getIntent().getExtras();
	    String nombreRestaurante = bundleInfo.getString("nombre");
	    telefonoRestaurante = bundleInfo.getString("telefono"); // global porque lo utilizaremos en onClick
	    calleRestaurante = bundleInfo.getString("calle"); // global porque lo utilizaremos en onClick
	    String logoRestaurante = bundleInfo.getString("logo");
	    String imagenFachada = bundleInfo.getString("imagen");
	    
	    // nombre restaurante
	    TextView nombreRes = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombreRes.setText(nombreRestaurante); 
	     
	    // teléfono restaurante
	    TextView telefonoRes = (TextView) vista.findViewById(R.id.telefonoRestaurante);
	    telefonoRes.setText(Html.fromHtml("<b>Tel.: </b>" + "<u>" + telefonoRestaurante + "</u>")); 
	    telefonoRes.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickPhoneCall(); 
	        }
	    });
	    
	    // dirección restaurante
	    TextView calleRes = (TextView) vista.findViewById(R.id.direccionRestaurante);
	    calleRes.setText(Html.fromHtml("<b>Dir.: </b>" + "<u>" + calleRestaurante + "</u>")); 
	    calleRes.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	//onClickDirections();
	        	String UriString = "geo:44.873799766954136, -91.92715644836426?z=22";
	            Uri geoUri = Uri.parse(UriString);
	            Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
	            startActivity(mapCall);
	            //TODO repasar si funciona en android
	        }
	    });
	   
	    // logo restaurante
	    ImageView logoRes = (ImageView) vista.findViewById(R.id.imagenLogo);
	    int img = getResources().getIdentifier(logoRestaurante, "drawable", getActivity().getPackageName());
	    logoRes.setImageResource(img);
	    
	    // botón llamar
	    Button botonLlamar = (Button) vista.findViewById(R.id.llamar);
	    botonLlamar.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickPhoneCall(); 	        	
	        }
	    });
	    
	    // botón mapas
	    Button botonMapas = (Button) vista.findViewById(R.id.direcciones);
	    botonMapas.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	onClickDirections(); 	        	
	        }
	    });
	    
	    // imagen fachada restaurante
	    ImageView imagenRes = (ImageView) vista.findViewById(R.id.imagenRestaurante); 
	    int imagen = getResources().getIdentifier(imagenFachada, "drawable", getActivity().getPackageName());
	    imagenRes.setImageResource(imagen);
		

	    return vista;
	}
	
	
	private void onClickDirections(){
		/*
	    try {

	    	// cogemos nuestra localización actual
	    	LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
	    	CurrentLocationListener locationListener = new CurrentLocationListener();
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		    
		    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    	miUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	    	//	getMiPosicion(miUbicacion);
	        
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

	    	double latitudOrigen = locationListener.getLatitud();
	    	double longitudOrigen = locationListener.getLongitud();
	    	System.out.println(latitudOrigen);
	    	System.out.println(latitudOrigen);
	    	
	    	// localización de destino
	    	Geocoder g = new Geocoder(getActivity());
	    	List<Address> direcciones = g.getFromLocationName(calleRestaurante, 1);
	    	while (direcciones.size() == 0){
	    		direcciones = g.getFromLocationName(calleRestaurante, 1);
	    	}
	    	if (direcciones.size() > 0){
	    		Address direccion = direcciones.get(0);
	    	double latitudDestino = direccion.getLatitude();
	    	double longitudDestino = direccion.getLongitude();
	    	System.out.println(latitudDestino);
	    	System.out.println(longitudDestino);

	    	// llamamos a Maps con la ruta deseada
	    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
        		    Uri.parse("http://maps.google.com/maps?saddr="+latitudOrigen+","+longitudOrigen+
        		    						"&daddr="+latitudDestino+","+longitudDestino));
        	startActivity(intent);
	    	}
		} catch (IOException e) {
			Log.i("error maps","error al encontrar la dirección del restaurante");
			e.printStackTrace();
		}
		*/
	}
	
	/*
	public void getMiPosicion(Location ubicacion) {
		if(map.getMyLocation() == null)
		{
			//Si no hay señal de GPS mostramos la Puerta del Sol de Madrid por defecto
			ubicacion = new Location("gps");
			ubicacion.setLatitude(40.4164904);
		    ubicacion.setLongitude(-3.7031825);
		    //Toast.makeText(getApplicationContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
		}else{
			//Toast.makeText(getApplicationContext(), "Mostrando ubicación actual", Toast.LENGTH_SHORT).show();
			ubicacion = map.getMyLocation();
		}
		miUbicacion = ubicacion;
	}
	*/
	
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
