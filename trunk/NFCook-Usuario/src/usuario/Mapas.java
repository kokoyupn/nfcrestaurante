package usuario;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.nfcook.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapas extends FragmentActivity implements LocationListener{
	private LocationManager locationManager;
	private static ArrayList<Restaurante> restaurantes;
	//private String proveedor;
	private Location miUbicacion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapas);
		
		if(restaurantes == null)
			restaurantes = new ArrayList<Restaurante>();
	    
	    
	    // Tomamos el location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    miUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    mostrarPosicion(miUbicacion);
	    // Definimos un objeto del tipo criteria para que este elija qué proveedor(gps,internet...) nos 
	    // conviene utilizar
	    //Criteria criteria = new Criteria();
	    //proveedor = locationManager.getBestProvider(criteria, false);
	    
	    //Location ubicacion = locationManager.getLastKnownLocation(proveedor);
	    //Location ubicacionPorDefecto = new Location("gps");
	    //ubicacionPorDefecto.setLatitude(40.4164904);
	    //ubicacionPorDefecto.setLongitude(-3.7031825);
	    
	    // vemos si está el GPS habilitado o disponible
	    //if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    //	Toast.makeText(getApplicationContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
		//    ubicacion = ubicacionPorDefecto;
	    //} else {
	    //	Toast.makeText(getApplicationContext(), "Mostrando ubicación actual por " + proveedor, Toast.LENGTH_LONG).show();
		//    onLocationChanged(ubicacion);
	    //}
		
		final GoogleMap mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		//mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		mapa.setMyLocationEnabled(true);
		
		// Asignamos la latitud y longitud de la ubicación actual
		//LatLng madrid = new LatLng(40.4164904, -3.7031825);
		LatLng coordenadas = new LatLng(miUbicacion.getLatitude(),miUbicacion.getLongitude());
		CameraPosition camPos = new CameraPosition.Builder()
		        .target(coordenadas)   //Se posiciona en la ubicación actual
		        .zoom(13)         //Establecemos el zoom en 13
		        //.bearing(0)      //Establecemos la orientación al norte
		        .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
		        .build();
		 
		CameraUpdate cameraUpdate =
		    CameraUpdateFactory.newCameraPosition(camPos);
		 
		mapa.animateCamera(cameraUpdate);
		
		mapa.setOnMapClickListener(new OnMapClickListener() {
		    public void onMapClick(LatLng point) {
		        Projection proj = mapa.getProjection();
		        Point coord = proj.toScreenLocation(point);
		 
		        Toast.makeText(
		            Mapas.this,
		            "Click\n" +
		            "Lat: " + point.latitude + "\n" +
		            "Lng: " + point.longitude + "\n" +
		            "X: " + coord.x + " - Y: " + coord.y,
		            Toast.LENGTH_SHORT).show();
		    }
		});
		
		locationManager.requestLocationUpdates(
		        LocationManager.GPS_PROVIDER, 30000, 0, this);
		//Añadimos cada restaurante de la Comunidad de Madrid
		mostrarMarcadores(mapa);
		inicializarListaRestaurantes();
		ordenaRestaurantes();
		
		//Implementación del botón "Lista" que nos lleva a otra Activity
		//ponerOnClickListaMapa();
	}

	public void mostrarPosicion(Location ubicacion)
	{
		if(ubicacion == null)
		{
			//Si no hay señal de GPS mostramos la Puerta del Sol de Madrid por defecto
			ubicacion = new Location("gps");
			ubicacion.setLatitude(40.4164904);
		    ubicacion.setLongitude(-3.7031825);
		    Toast.makeText(getApplicationContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(getApplicationContext(), "Mostrando ubicación actual", Toast.LENGTH_SHORT).show();
		}
		miUbicacion = ubicacion;
	}
	
	private void mostrarMarcadores(GoogleMap mapa)
	{
		//Asignamos un nuevo icono al marcador
		BitmapDescriptor marcadorFoster = BitmapDescriptorFactory.fromResource(R.drawable.foster_mapa);
	    
		mapa.addMarker(new MarkerOptions()
	        .position(new LatLng(40.391862,-3.656012))
	        .icon(marcadorFoster)
	        .title("FH Albufera Plaza"));
	    mapa.addMarker(new MarkerOptions()
        	.position(new LatLng(40.432216,-3.658168))
        	.icon(marcadorFoster)
        	.title("FH Alcalá 230"));
	    mapa.addMarker(new MarkerOptions()
        	.position(new LatLng(40.427382,-3.678703))
        	.icon(marcadorFoster)
        	.title("FH Alcalá 90"));
	    mapa.addMarker(new MarkerOptions()
        	.position(new LatLng(40.38771,-3.763225))
        	.icon(marcadorFoster)
        	.title("FH Aluche"));
	    mapa.addMarker(new MarkerOptions()
        	.position(new LatLng(40.479467,-3.684711))
        	.icon(marcadorFoster)
        	.title("FH Apolonio Morales"));
	    mapa.addMarker(new MarkerOptions()
	    	.position(new LatLng(40.449617,-3.650835)).snippet("POYA GORDA")
	    	.icon(marcadorFoster)
	    	.title("FH Arturo Soria"));
	}
	
	/*public void ponerOnClickListaMapa() {
		
		Button botonMapas = (Button) findViewById(R.id.buttonMapas);
		botonMapas.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ordenaRestaurantes();
				Intent intent = new Intent(getApplication(), ListaMapas.class);
		    	startActivity(intent);
			}
		});
	}*/

	public void onLocationChanged(Location ubicacion) {
        mostrarPosicion(ubicacion);
    }
 
    public void onProviderDisabled(String provider){
        Toast.makeText(getApplicationContext(), "Proovedor OFF", Toast.LENGTH_SHORT).show();
    }
 
    public void onProviderEnabled(String provider){
    	Toast.makeText(getApplicationContext(), "Proovedor ON", Toast.LENGTH_SHORT).show();
    }
 
    public void onStatusChanged(String provider, int status, Bundle extras){
    	 Toast.makeText(getApplicationContext(), "Estado Proovedor: " + status, Toast.LENGTH_SHORT).show();
    }
    
    //Métodos para calcular la distancia a cada marcador y para generar la lista ordenada de restaurantes
    
    public double calcularDistanciaEnKm(Location actual, LatLng restaurante)
    {
    	//Fórmula de Haversine para calcular la distancia entre dos puntos del mapa (en km)
    	//  a = sin²($x/2) + cos(x1).cos(x2).sin²($y/2)
    	// Donde donde	x es la latitud, la longitud es y, R es el radio terrestre (radio medio = 6.371 kilometros)
    	//  los ángulos tienen que estar en radianes
    	
    	double radioTierra = 6371;
    	//Pasamos todo a radianes
    	double difLat = Math.toRadians(actual.getLatitude() - restaurante.latitude);
    	double difLng = Math.toRadians(actual.getLongitude() - restaurante.longitude);
    	double latAct = Math.toRadians(actual.getLatitude());
    	double latRest = Math.toRadians(restaurante.latitude);
    	
    	double a = Math.sin(difLat/2) * Math.sin(difLat/2) +
    			Math.sin(difLng/2) * Math.sin(difLng/2) * Math.cos(latAct) * Math.cos(latRest);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    	double distancia = radioTierra * c;
    	
    	return distancia;
    }
    
    public void ordenaRestaurantes()
    {
    	ArrayList<Restaurante> listaAux = new ArrayList<Restaurante>();
    	while(restaurantes.size() > 0){
	    	Restaurante restActual = buscaRestauranteMasCercano();
	    	listaAux.add(restActual);
	    	restaurantes.remove(restActual);
    	}
    	restaurantes = listaAux;
    }
	
    public Restaurante buscaRestauranteMasCercano(){
		//recorremos todas las mesas buscando la que tiene menor numero de mesa
    	Restaurante restMasCercano = restaurantes.get(0);//Pongo el primero
		int i = 1;
		while (i < restaurantes.size())
		{
			double distMin = calcularDistanciaEnKm(miUbicacion,new LatLng(restMasCercano.getLat(),restMasCercano.getLng()));
			double distActual = calcularDistanciaEnKm(miUbicacion, new LatLng(restaurantes.get(i).getLat(),restaurantes.get(i).getLng()));
			if(distActual < distMin) {
				restMasCercano = restaurantes.get(i);
			}
			i++;
		}
		return restMasCercano;
	}
    
    public void inicializarListaRestaurantes() {
    	if(restaurantes.size() == 0){
			Restaurante rest1 = new Restaurante("FH Albufera Plaza", 40.391862, -3.656012, calcularDistanciaEnKm(miUbicacion,new LatLng(40.391862,-3.656012)));
			restaurantes.add(rest1);
			Restaurante rest2 = new Restaurante("FH Alcalá 230", 40.432216, -3.658168, calcularDistanciaEnKm(miUbicacion,new LatLng(40.432216, -3.658168)));
			restaurantes.add(rest2);
			Restaurante rest3 = new Restaurante("FH Alcalá 90", 40.427382, -3.678703, calcularDistanciaEnKm(miUbicacion,new LatLng(40.427382, -3.678703)));
			restaurantes.add(rest3);
			Restaurante rest4 = new Restaurante("FH Aluche", 40.38771, -3.763225, calcularDistanciaEnKm(miUbicacion,new LatLng(40.38771, -3.763225)));
			restaurantes.add(rest4);
			Restaurante rest5 = new Restaurante("FH Apolonio Morales", 40.479467, -3.684711, calcularDistanciaEnKm(miUbicacion,new LatLng(40.479467, -3.684711)));
			restaurantes.add(rest5);
			Restaurante rest6 = new Restaurante("FH Arturo Soria", 40.449617, -3.650835, calcularDistanciaEnKm(miUbicacion,new LatLng(40.449617, -3.650835)));
			restaurantes.add(rest6);
    	}
    }
    
    public static ArrayList<Restaurante> getRestaurantes()
    {
    	return restaurantes;
    }
}
