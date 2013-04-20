package tpv;

public class Producto {
	private String id;
	private int idUnico;
	private String categoria;
	private String tipo;
	private String nombre;
	private String descripción;
	private String foto;
	private double precio;
	private String observaciones;
	
	public Producto(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio, String observaciones) {
		this.id = id;
		this.categoria = categoria;
		this.tipo = tipo;
		this.nombre = nombre;
		this.descripción = descripción;
		this.foto = foto;
		this.precio = precio;
		this.observaciones = observaciones;
	}
	

	public Producto(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio, String observaciones, int idUnico) {
		this.id = id;
		this.categoria = categoria;
		this.tipo = tipo;
		this.nombre = nombre;
		this.descripción = descripción;
		this.foto = foto;
		this.precio = precio;
		this.observaciones = observaciones;
		this.idUnico = idUnico;
	}


	public String getId() {
		return id;
	}

	public String getCategoria() {
		return categoria;
	}

	public String getTipo() {
		return tipo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setIdUnico(int idUnico) {
		this.idUnico = idUnico;
	}
	
	public int getIdUnico(){
		return idUnico;
	}

	public String getDescripción() {
		return descripción;
	}

	public String getFoto() {
		return foto;
	}

	public double getPrecio() {
		return precio;
	}
	
	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public boolean equals(Object o){
		if (o == null) return false;
		if (!(o instanceof Producto))return false;
		Producto p = (Producto) o;
		if (this.idUnico == p.getIdUnico()) return true;
		return false;
	}
	
	public String toString(){
		String mensaje = "*****" + "nombre" + "*****" + "\n";
		if(!observaciones.equals("")){
			mensaje +=" - " + observaciones + "\n";
		}
		return mensaje;		
	}
	
}
