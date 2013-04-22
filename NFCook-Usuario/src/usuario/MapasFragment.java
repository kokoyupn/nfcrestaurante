package usuario;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import buscaRutaMapa.AnalizadorRutaMapa;

import com.example.nfcook.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapasFragment extends Fragment 
					implements LocationListener, OnMarkerClickListener, 
							OnInfoWindowClickListener, OnMapClickListener{
    
    //Clase adapter del bocadillo que sale encima del marcador del mapa
    class VentanaMarcadorMapaAdapter implements InfoWindowAdapter {

    	private final View mContents;
    	
    	public VentanaMarcadorMapaAdapter()
    	{
    		mContents = getActivity().getLayoutInflater().inflate(R.layout.textview_buscador_platos, null);
    	}
    // Métodos de InfoWindowAdapter
    // Si getInfoWindow(...) devuelve null, llamará entonces a getInfoContents(...)
    // Si además getInfoContents(...) devuelve null, se muestra el contenido por defecto
    	public View getInfoContents(Marker marker) { 
    		TextView titulo = (TextView) mContents.findViewById(R.id.textViewObjetoBusqueda);
    		titulo.setTextColor(Color.rgb(065, 105, 225));
    		titulo.setText(marker.getTitle());
    		SpannableString tituloSubrayado = new SpannableString(titulo.getText());
    		tituloSubrayado.setSpan(new UnderlineSpan(), 0, tituloSubrayado.length(), 0);
    		titulo.setText(tituloSubrayado);
    		return mContents;
    	}
    	public View getInfoWindow(Marker marker) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    	
    	
    }
	
    private GoogleMap map;
	private LocationManager locationManager;
	private static ArrayList<Restaurante> restaurantes;
	private Location miUbicacion;
	ArrayList<LatLng> puntosRuta;
    TextView textViewInfoRuta;
    private Polyline polilyneActual;
    View vista;
    MapView mMapView;
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	vista = super.onCreateView(inflater, container, savedInstanceState);
    	// inflat and return the layout
    			View v = getActivity().getLayoutInflater().inflate(R.layout.mapas, container, false);
    			mMapView = (MapView) v.findViewById(R.id.map);
    			mMapView.onCreate(savedInstanceState);
    			mMapView.onResume();//needed to get the map to display immediately
    			
    			try {
    	     		MapsInitializer.initialize(getActivity().getApplicationContext());
    	 		} catch (GooglePlayServicesNotAvailableException e) {
    	     		e.printStackTrace();
    	 		}
    	 		
    	 		map = mMapView.getMap();
    	 		
    	 		//Perform any camera updates here
    			
    			return vista;
	}
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)     {
        super.onViewCreated(view, savedInstanceState);
    }
	
	@Override
	public void onResume() {
        super.onResume();
		mMapView.onResume();
        inicializarMapa();
    }
	
	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}
	
	@Override
	 public void onDestroyView() {
	        super.onDestroyView(); 
	        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));  
	        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
	        ft.remove(fragment);
	        ft.commit();
	}
	
	public void inicializarMapa() {
		
		if(restaurantes == null)
			restaurantes = new ArrayList<Restaurante>();
	    
		textViewInfoRuta = (TextView) vista.findViewById(R.id.textViewMapas);
		textViewInfoRuta.setText("Pulsa en tu restaurante para obtener +info");
		 
		// Tomamos el location manager
	    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	    miUbicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        getMiPosicion(miUbicacion);
        
		
		// Asignamos la latitud y longitud de la ubicación actual
        LatLng coordenadas;
        if(map.getMyLocation() == null)
        	coordenadas = new LatLng(miUbicacion.getLatitude(),miUbicacion.getLongitude());
        else
        	coordenadas = new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());
		CameraPosition camPos = new CameraPosition.Builder()
		        .target(coordenadas)   //Se posiciona en la ubicación actual
		        .zoom(13)         //Establecemos el zoom en 13
		        //.bearing(0)      //Establecemos la orientación al norte
		        .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
		        .build();
		 
		CameraUpdate cameraUpdate =
		    CameraUpdateFactory.newCameraPosition(camPos);
		 
		map.animateCamera(cameraUpdate);
		
		locationManager.requestLocationUpdates(
		        LocationManager.GPS_PROVIDER, 0, 0, this);
		//Añadimos cada restaurante de la Comunidad de Madrid
		map.setInfoWindowAdapter(new VentanaMarcadorMapaAdapter());
		
		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnInfoWindowClickListener(this);
		
		inicializarListaRestaurantes();
		mostrarMarcadores(map);
		ordenaRestaurantes();
		
        //puntosRuta para dibujar dicha ruta en el mapa
        puntosRuta = new ArrayList<LatLng>();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem item = menu.getItem(0);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				if(map.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
					map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				else
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				return false;
			}
		});
        return true;
	}

	
	//--------------------- MÉTODOS -----------------------------------
	//-----------------------------------------------------------------
	public void getMiPosicion(Location ubicacion)
	{
		if(ubicacion == null)
		{
			//Si no hay señal de GPS mostramos la Puerta del Sol de Madrid por defecto
			ubicacion = new Location("gps");
			ubicacion.setLatitude(40.4164904);
		    ubicacion.setLongitude(-3.7031825);
		    Toast.makeText(getActivity().getApplicationContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
		}else{
			//Toast.makeText(getApplicationContext(), "Mostrando ubicación actual", Toast.LENGTH_SHORT).show();
			ordenaRestaurantes();
		}
		miUbicacion = ubicacion;
	}
	
	private void mostrarMarcadores(GoogleMap mapa)
	{		
		mapa.addMarker(restaurantes.get(0).getMarcador());
		mapa.addMarker(restaurantes.get(1).getMarcador());
		mapa.addMarker(restaurantes.get(2).getMarcador());
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
			double distMin = calcularDistanciaEnKm(miUbicacion,new LatLng(restMasCercano.getMarcador().getPosition().latitude,restMasCercano.getMarcador().getPosition().longitude));
			double distActual = calcularDistanciaEnKm(miUbicacion, new LatLng(restaurantes.get(i).getMarcador().getPosition().latitude,restaurantes.get(i).getMarcador().getPosition().longitude));
			if(distActual < distMin) {
				restMasCercano = restaurantes.get(i);
			}
			i++;
		}
		return restMasCercano;
	}
    //-------------------------------------------------------------------------------------------------
    public void inicializarListaRestaurantes() {
    	//Salto de línea en TextView = Html.fromHtml("<br />")
		BitmapDescriptor marcadorFoster = BitmapDescriptorFactory.fromResource(R.drawable.foster_mapa);

    	if(restaurantes.size() == 0){
			Restaurante rest1 = new Restaurante("Foster's Hollywood Albufera Plaza", 
					new MarkerOptions()
						.position(new LatLng(40.391862, -3.656012))
						.title("Foster's Hollywood Albufera Plaza")
						.icon(marcadorFoster),
					calcularDistanciaEnKm(miUbicacion,new LatLng(40.391862,-3.656012)),
					"CC La Albufera Plaza"+ Html.fromHtml("<br />") +"Av. de la Albufera 153 Locales 13 y 14" + Html.fromHtml("<br />") + "28038 Madrid",
					"91 478 97 84",
					"D a J 13:00 a 17:00 y  20:00 a 24:00 V, S y Vísperas 13:00 a 17:00 y 20:00 a 1:00" + Html.fromHtml("<br />") + "Delivery: D-J de 13:00 a 17:00 y de 20:30 a 00:00. V-S Vísperas y Festivos hasta 00:30",
					"www.fostershollywood.es/albuferaplaza", true, true, true, false, false);
			restaurantes.add(rest1);
			
			Restaurante rest2 = new Restaurante("Foster's Hollywood Alcalá 230", 
					new MarkerOptions()
						.position(new LatLng(40.432216, -3.658168))
						.title("Foster's Hollywood Alcalá 230")
						.icon(marcadorFoster),
					calcularDistanciaEnKm(miUbicacion,new LatLng(40.432216, -3.658168)),
					"C/ Alcalá, 230"+ Html.fromHtml("<br />") +"28027 Madrid",
					"91 356 54 88",
					"D-J y festivos de 13.00 a 17.00 y de 20.00 a 00.00. V, S y vísperas de festivo de 13.00 a 17.00 y de 20.00 a 01.00.",
					"www.fostershollywood.es/alcala230", true, true, true, false, false);
			restaurantes.add(rest2);
			
			Restaurante rest3 = new Restaurante("Foster's Hollywood Alcalá 90", 
					new MarkerOptions()
						.position(new LatLng(40.427382, -3.678703))
						.title("Foster's Hollywood Alcalá 90")
						.icon(marcadorFoster),
					calcularDistanciaEnKm(miUbicacion,new LatLng(40.427382, -3.678703)),
					"C/ Alcalá, 90"+ Html.fromHtml("<br />") +"28009 Madrid",
					"91 781 06 26",
					"L-D de 13:00 a 17:00 y  20:00 a 24:00"+ Html.fromHtml("<br />") +"Delivery: de 13:00 a 17:00 y de 20:00 a 00:00",
					"www.fostershollywood.es/alcala90", true, true, true, true, false);
			restaurantes.add(rest3);
    	}
    }
    
    public static ArrayList<Restaurante> getRestaurantes()
    {
    	return restaurantes;
    }
    
    //--- MÉTODOS PARA CÁLCULO DE RUTA MAPA-----------------
    
    private String getDirectionsUrl(LatLng origin,LatLng dest){
    	 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
 
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                AnalizadorRutaMapa parser = new AnalizadorRutaMapa();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            //MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
 
            if(result.size()<1){
                Toast.makeText(getActivity().getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.rgb(65, 105, 225)); //Azul Royal
            }
 
            textViewInfoRuta.setText("Distancia:"+distance + ", Tiempo estimado:"+duration);
 
            // Drawing polyline in the Google Map for the i-th route
            polilyneActual = map.addPolyline(lineOptions);
        }
    }
    
    public void onLocationChanged(Location ubicacion) {
        getMiPosicion(ubicacion);
    }
 
    public void onProviderDisabled(String provider){
        //Toast.makeText(getApplicationContext(), "Proovedor OFF", Toast.LENGTH_SHORT).show();
    }
 
    public void onProviderEnabled(String provider){
    	//Toast.makeText(getApplicationContext(), "Proovedor ON", Toast.LENGTH_SHORT).show();
    }
 
    public void onStatusChanged(String provider, int status, Bundle extras){
    	 //Toast.makeText(getApplicationContext(), "Estado Proovedor: " + status, Toast.LENGTH_SHORT).show();
    }
    public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(getActivity().getBaseContext(),DescripcionRestaurante.class);
		intent.putExtra("nombreRestaurante", marker.getTitle());
    	startActivity(intent);
	}
	public boolean onMarkerClick(final Marker marker) {
		// Ya hay una ruta mostrándose
		if(puntosRuta.size()==2){
			  puntosRuta.clear();
              mostrarMarcadores(map);
        }

        // añadimos el punto origen (mi ubicación) y el destino (el marcador)
		if (map.getMyLocation() == null)
			puntosRuta.add(new LatLng(miUbicacion.getLatitude(),miUbicacion.getLongitude()));
		else
			puntosRuta.add(new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude()));
        puntosRuta.add(marker.getPosition());
        
        // Borramos una polilinea sólo si existe
        if (polilyneActual != null)
        	polilyneActual.remove();
        
        // A partir de aquí se dibuja la polilinea o ruta en el mapa
        LatLng origin = puntosRuta.get(0);
        LatLng dest = puntosRuta.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        
		return false;
	}
	public void onMapClick(LatLng point) {
		if (polilyneActual != null)
        	polilyneActual.remove();
		textViewInfoRuta.setText("Pulsa en tu restaurante para obtener +info");
	}
}

