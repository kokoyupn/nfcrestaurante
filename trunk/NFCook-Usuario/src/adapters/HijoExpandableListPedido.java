package adapters;


public class HijoExpandableListPedido {
	private String observaciones,extras;
	private boolean check;
	
	
	public HijoExpandableListPedido(String observaciones, String extras) {
		this.observaciones = observaciones;
		this.extras = extras;
		this.check = false;
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
	
	
}
