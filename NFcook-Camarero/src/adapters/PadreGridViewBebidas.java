package adapters;

/**
 * Clase encargada de contener todos los campos necesarios para representar cada elemento del gridview
 * de la pantalla añadir bebidas.
 * 
 * -Atributos-
 * idPlato: Se gurda el id del plato.
 * nombre: Nombre de la bebida.
 * foto: Ruta de la foto.
 * precioUnidad: Precio de una sola unidad.
 * unidades: Unidades que ha seleccionado el usuario.
 * precioTotal: Precio de la bebida.
 * 
 * @author Rober
 *
 */
public class PadreGridViewBebidas {
	private String idPlato;
	private String nombre;
	private String rutaFoto;
	private double precioUnidad;
	private int unidades;
	private double precioTotal;
	
	public PadreGridViewBebidas(String idPlato,String nombre, String rutaFoto, double precioUnidad){
		this.idPlato = idPlato;
		this.nombre = nombre;
		this.rutaFoto = rutaFoto;
		this.precioUnidad = precioUnidad;
		this.unidades = 0;
		this.precioTotal = 0;
	}
	
	public String getIdPlato() {
		return idPlato;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getRutaFoto() {
		return rutaFoto;
	}
	
	public double getPrecioUnidad(){
		return precioUnidad;
	}

	public int getUnidades() {
		return unidades;
	}

	public double getPrecioTotal() {
		return Math.rint(precioTotal*100)/100;
	}

	public void anyadeUnidad() {
		unidades += 1;
		precioTotal += precioUnidad;
	}
	
	public void eliminaUnidad() {
		unidades -= 1;
		precioTotal -= precioUnidad;
	}
	
}
