package usuario;

public class Restaurante {

	private String nombre;
	private double latitud;
	private double longitud;
	private double distancia;
	
	public Restaurante(String n, double lat, double lng, double d)
	{
		nombre = n;
		latitud = lat;
		longitud = lng;
		distancia = d;
	}
	
	public String getNombre()
	{
		return nombre;
	}
	
	public double getLat()
	{
		return latitud;
	}
	
	public double getLng()
	{
		return longitud;
	}
	
	public double getDistancia()
	{
		return distancia;
	}
	
	public void setDistancia(double d)
	{
		distancia = d;
	}
}
