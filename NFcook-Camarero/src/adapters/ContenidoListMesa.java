package adapters;

public class ContenidoListMesa {
	
	private String nombre, extras, observaciones;
	private int id;
	private String idPlato;
	private float precio;
	
	public ContenidoListMesa(String nombre,String extras,String observaciones,float precio,int id,String idPlato){
		this.nombre = nombre;
		this.extras = extras;
		this.observaciones = observaciones;
		this.precio = precio;
		this.id = id;
		this.idPlato = idPlato;
		
	}
	
	public String getIdPlato(){
		return idPlato;
	}
	
	public int getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getExtras() {
		return extras;
	}
	
	public void setExtras(String extras) {
		this.extras = extras;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public float getPrecio() {
		return precio;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	

}
