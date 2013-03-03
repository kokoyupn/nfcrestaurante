package adapters;

/**
 * Configura los hijos del adapter de la ExpandableList de la pantalla de pedido, cada hijo sera un plato
 * configurado.
 * 
 * -Atributos-
 * observaciones : almacena el texto escrito de un usuario.
 * extras        : almacena los extras seleccionados por el usuario.
 * id            : campo necesario para hacer modificaciones sobre la base de datos.
 * precio        : precio correspondiente a ese plato.
 * 
 * @author Prado
 *
 */
public class HijoExpandableListPedido {
	private String observaciones, extras, id;
	private double precio;	
	
	public HijoExpandableListPedido(String observaciones, String extras, double precio, String id) {
		this.observaciones = observaciones;
		this.extras = extras;
		this.precio = precio;
		this.id = id;
	}


	public String getObservaciones() {
		return observaciones;
	}


	public String getExtras() {
		return extras;
	}

	public double getPrecio() {
		return precio;
	}

	public String getId() {
		return id;
	}


	public void setExtrasObs(String extras, String observaciones) {
		this.extras = extras;
		this.observaciones = observaciones;
	}
	
}
