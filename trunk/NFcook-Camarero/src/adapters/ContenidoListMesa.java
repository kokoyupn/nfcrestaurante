package adapters;

public class ContenidoListMesa {
	
	private String nombre, extras, observaciones;
	int id;
	float precio;
	
	public ContenidoListMesa(String nombre,String extras,String observaciones,float precio,int id){
		this.nombre = nombre;
		this.extras = extras;
		this.observaciones = observaciones;
		this.precio = precio;
		this.id = id;
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

	public String getObservaciones() {
		return observaciones;
	}

	public float getPrecio() {
		return precio;
	}

	

}
