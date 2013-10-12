package adapters;
/**
 * Esta clase tiene los datos necesarios contenidos en una fila de la pantalla después de login del gerente
 *  
 * @author Guille
 *
 */
public class PadreListRestaurantes {
	
	private String nombreRestaurante,calle,imagen;
	private int idRestaurante;
	private boolean checkVisibles, isSelected;
	

	public PadreListRestaurantes(String nombreRestaurante, int idRestaurante, String calle, String imagen) {
		this.nombreRestaurante = nombreRestaurante;
		this.idRestaurante = idRestaurante;
		this.calle = calle;
		this.imagen = imagen;
		checkVisibles = false;
		isSelected =false;
	}


	public int getIdRestaurante() {
		return idRestaurante;
	}

	public String getNombreRestaurante() {
		return nombreRestaurante;
	}

	public String getCalle() {
		return calle;
	}

	public String getImagen() {
		return imagen;
	}


	public boolean isCheckVisibles() {
		return checkVisibles;
	}


	public void setCheckVisibles(boolean checkVisibles) {
		this.checkVisibles = checkVisibles;
	}


	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
