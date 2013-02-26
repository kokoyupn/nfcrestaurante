package adapters;


public class HijoExpandableListPedido {
	private String observaciones,extras;
	private double precio;
	private boolean check;
	
	
	public HijoExpandableListPedido(String observaciones, String extras, double precio) {
		this.observaciones = observaciones;
		this.extras = extras;
		this.check = false;
		this.precio = precio;
	}


	public String getObservaciones() {
		return observaciones;
	}


	public String getExtras() {
		return extras;
	}


	public boolean isCheck() {
		return check;
	}
	
	public void setCheck(){
		check = !check;
	}


	public double getPrecio() {
		return precio;
	}
	
}
