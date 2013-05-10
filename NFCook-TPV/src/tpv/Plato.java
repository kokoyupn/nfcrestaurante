package tpv;

public class Plato extends Producto{
	
	private String extras;
	private String extrasMarcados;
	private int cantiadPedido;
	
	
	public Plato(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio, String observaciones, String extras, int cantiadPedido) {
		
		super(id, categoria, tipo, nombre, descripción, foto, precio, observaciones);
		this.extras = extras;
		extrasMarcados = "";
		this.cantiadPedido = cantiadPedido;
	}

	

	public Plato(String id, String categoria, String tipo, String nombre,
			String descripción, String foto, double precio,
			String observaciones, String extras, String extrasMarcados, int cantiadPedido) {
		super(id, categoria, tipo, nombre, descripción, foto, precio,
				observaciones);
		this.extras = extras;
		this.extrasMarcados = extrasMarcados;
		this.cantiadPedido = cantiadPedido;
	}



	public int getCantiadPedido() {
		return cantiadPedido;
	}

	public void setCantiadPedido(int cantiadPedido) {
		this.cantiadPedido = cantiadPedido;
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
		return super.toString() + " - " + extrasMarcados + "\n";
	}
}
