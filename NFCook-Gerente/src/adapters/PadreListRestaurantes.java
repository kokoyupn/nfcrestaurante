package adapters;


/**
 * Esta clase tiene los datos necesarios contenidos en una fila de la pantalla despu�s de login del gerente
 *  
 * @author Guille
 *
 */
public class PadreListRestaurantes {
	
	private String nombreRestaurante, calle, cp, poblacion, imagen, telefono, imagenFachada;
	private int idRestaurante;
	private float rating;
	private boolean checkVisibles, isSelected;
	

	public PadreListRestaurantes(String nombreRestaurante, int idRestaurante, String calle, String cp, String poblacion, String imagen, String imagenFachada, String telefono, float rating) {

		this.nombreRestaurante = nombreRestaurante;
		this.idRestaurante = idRestaurante;
		this.calle = calle;
		this.cp = cp;
		this.poblacion = poblacion;
		this.imagen = imagen;
		this.imagenFachada = imagenFachada;
		this.telefono = telefono;
		this.rating = rating;
		checkVisibles = false;
		isSelected = false;
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
	
	public String getCP() {
		return cp;
	}
	
	public String getPoblacion() {
		return poblacion;
	}

	public String getImagen() {
		return imagen;
	}
	
	public String getImagenFachada(){
		return imagenFachada;
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


	public String getTelefono() {
		return telefono;
	}
	
	public float getRating() {
		return rating;
	}
}
