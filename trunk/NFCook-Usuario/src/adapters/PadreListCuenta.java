package adapters;

/**
 * Configura los padres del adapter de la ListView de la pantalla cuenta,cada padre mostrará la
 * siguiente información por cada plato: nombre, unidades y precio del conjunto.
 * 
 * -Atributos-
 * plato				   : nombre del plato.
 * precioUnidad            : precio individual del plato, no aparece.
 * precioTotal             : precio del conjunto de platos con mismo nombre.
 * cantidad                : número de unidades que hemos seleccionado de un plato.
 * 
 * @author Abel
 *
 */
public class PadreListCuenta {

	private String plato;
	private double precioUnidad;
	private double precioTotal;
	private int cantidad;
	
	public PadreListCuenta(String plato, double precioUnidad){
		this.plato = plato;
		this.precioTotal = this.precioUnidad = precioUnidad;
		cantidad = 1;
	}
	
	public String getPlato(){
		return plato;
	}
	
	public double getPrecioUnidad(){
		return Math.rint(precioUnidad*100)/100;
	}
	
	public double getPrecioTotal(){
		return Math.rint(precioTotal*100)/100;
	}
	
	public int getCantidad(){
		return cantidad;
	}
	
	public void actualizaPrecioTotal(double precioUnidad){
		this.precioTotal += precioUnidad;
	}
	
	public void actualizaCantidad(){
		this.cantidad++;
	}
}
