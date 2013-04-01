package adapters;

/**
 * Clase encargada de almacenar la información necesaria para cada plato. Todos los atributos
 * de la clase se utilizan a la hora de implementar la funcionalidad de la calculadora.
 * 
 * @author Abel
 *
 */
public class InfomacionPlatoPantallaReparto {
	private String idPlatoEnPedido;
	private String nombrePlato;
	private int fotoPlato;
	private double precioPlato;
	private int numPersonasRepartido;
	
	public InfomacionPlatoPantallaReparto(String idPlatoEnPedido, String nombrePlato, int fotoPlato, double precioPlato){
		this.idPlatoEnPedido = idPlatoEnPedido;
		this.nombrePlato = nombrePlato;
		this.fotoPlato = fotoPlato;
		this.precioPlato = precioPlato;
		numPersonasRepartido = 0;
	}
	
	public InfomacionPlatoPantallaReparto(String idPlatoEnPedido, String nombrePlato, int fotoPlato, double precioPlato, int numPersonasRepartido){
		this.idPlatoEnPedido = idPlatoEnPedido;
		this.nombrePlato = nombrePlato;
		this.fotoPlato = fotoPlato;
		this.precioPlato = precioPlato;
		this.numPersonasRepartido = numPersonasRepartido;
	}
	
	public void setNumPersonasRepartidos(int numPersonasRepartido){
		this.numPersonasRepartido = numPersonasRepartido;
	}
	
	public String getNombrePlato(){
		return nombrePlato;
	}
	
	public String getIdPlatoEnPedido(){
		return idPlatoEnPedido;
	}
	
	public int getFotoPlato(){
		return fotoPlato;
	}
	
	public int getNumPersonasRepartido(){
		return numPersonasRepartido;
	}
	
	public double getPrecioPlato(){
		return precioPlato;
	}
	
	public void anyadePersonaAlReparto(){
		numPersonasRepartido++;
	}
	
	public void quitaPersonaAlReparto(){
		numPersonasRepartido--;
	}
	
	// Metodo encargadado de dividir el precio del plato entre los comensales que lo han tomado
	public double precioPorPersona(){
		return precioPlato/numPersonasRepartido;
	}
}
