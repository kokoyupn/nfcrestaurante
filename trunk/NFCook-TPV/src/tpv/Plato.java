package tpv;

public class Plato extends Producto{
	
	private String extras;
	private String extrasMarcados;
	
	
	public Plato(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio, String observaciones, String extras) {
		
		super(id, categoria, tipo, nombre, descripción, foto, precio, observaciones);
		this.extras = extras;
		extrasMarcados = "";
	}

	

	public Plato(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio,
			String observaciones, String extras, String extrasMarcados) {
		super(id, categoria, tipo, nombre, descripción, foto, precio,
				observaciones);
		this.extras = extras;
		this.extrasMarcados = extrasMarcados;
	}



	public String getExtrasMarcados() {
		return extrasMarcados;
	}
	
	public String getExtras() {
		return extras;
	}

	public void setExtrasMarcados(String extrasMarcados) {
		this.extrasMarcados = extrasMarcados;
	}
	
	public String toString(){
		return super.toString() + " - " + extrasMarcados + "\n \n";
	}
}
