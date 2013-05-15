package adapters;


/**
 * Configura los hijos del adapter de la ExpandableList de la pantalla historico, cada hijo sera un pedido
 * configurado.
 * 
 * -Atributos-
 * hora : hora en el que se realizo el pedido.
 * camarero        : camarero al que se le realizo el pedido.
 * precio        : precio correspondiente a ese pedido.
 * 
 * @author Busy
 *
 */
public class HijoExpandableListHistorico {
	private String camarero,hora;
	private double precio;

	
	public HijoExpandableListHistorico(String camarero, String hora, double precio) {
		this.camarero= camarero;
		this.hora = hora;
		this.precio = precio;
	}


	public String getCamarero() {
		return camarero;
	}


	public String getHora() {
		return hora;
	}

	public double getPrecio() {
		return precio;
	}

	
	public boolean equals(Object obj){
		if(obj == this) return true;
		if(!(obj instanceof HijoExpandableListHistorico)) return false;
		HijoExpandableListHistorico hijo = (HijoExpandableListHistorico) obj;
		
		Boolean camareroIdem = false;
		Boolean horaIdem = false;
		
		if(camarero == null && hijo.camarero == null){
			camareroIdem = true;
		}else{
			camareroIdem = camarero.equals(hijo.camarero);
		}
		if(hora == null && hijo.hora == null){
			horaIdem = true;
		}else if (hora != null && hijo.hora != null){
			horaIdem = hora.equals(hijo.hora);
		}
		
		return camareroIdem && horaIdem;
	}

}
