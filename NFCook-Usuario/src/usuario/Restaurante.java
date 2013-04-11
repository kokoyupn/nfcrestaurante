package usuario;

import com.google.android.gms.maps.model.MarkerOptions;

public class Restaurante {

	private String nombre;
	private MarkerOptions marcador;
	private double distancia;
	private String direccion;
	private String telefono; //int?
	private String horario;
	private String url;
	private boolean takeAway;
	private boolean delivery; //lo pides desde casa y vas a recogerlo
	private boolean menuMediodia;
	private boolean magia;
	private boolean cumpleanios;
	
	//Salto de línea en TextView = Html.fromHtml("<br />")
	public Restaurante(String n, MarkerOptions marc, double dist, String dir, 
			String telf, String hor, String url, boolean takeAway, boolean delivery,
			boolean menuMediodia, boolean magia, boolean cumpleanios)
	{
		nombre = n;
		marcador = marc;
		distancia = dist;
		direccion = dir;
		telefono = telf;
		horario = hor;
		this.url = url;
		this.takeAway = takeAway;
		this.delivery = delivery;
		this.menuMediodia = menuMediodia;
		this.magia = magia;
		this.cumpleanios = cumpleanios;
	}
	
	public String getNombre()
	{
		return nombre;
	}
	
	public MarkerOptions getMarcador()
	{
		return marcador;
	}
	
	public double getDistancia()
	{
		return distancia;
	}
	
	public String getDireccion()
	{
		return direccion;
	}
	
	public String getTelefono()
	{
		return telefono;
	}
	
	public String getHorario()
	{
		return horario;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setDistancia(double d)
	{
		distancia = d;
	}

	public boolean isTakeAway() {
		return takeAway;
	}

	public void setTakeAway(boolean takeAway) {
		this.takeAway = takeAway;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public boolean isMenuMediodia() {
		return menuMediodia;
	}

	public void setMenuMediodia(boolean menuMediodia) {
		this.menuMediodia = menuMediodia;
	}

	public boolean isMagia() {
		return magia;
	}

	public void setMagia(boolean magia) {
		this.magia = magia;
	}

	public boolean isCumpleanios() {
		return cumpleanios;
	}

	public void setCumpleanios(boolean cumpleanios) {
		this.cumpleanios = cumpleanios;
	}
}
