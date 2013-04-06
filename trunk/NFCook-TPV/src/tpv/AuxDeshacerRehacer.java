package tpv;

public class AuxDeshacerRehacer {

	private Boolean accion;
	private Producto prod;
	
	public AuxDeshacerRehacer(Boolean accion, Producto prod){
		this.accion = accion;
		this.prod = prod;
	}

	public Boolean getAccion() {
		return accion;
	}

	public Producto getProd() {
		return prod;
	}
}
