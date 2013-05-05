package adapters;

import java.util.ArrayList;

/**
 * Clase encargada de contener todos los campos necesarios para representar cada elemento del gridview
 * de la pantalla calculadora. En particular guardaremos la siguiente información:
 * 
 * - nombre			- Nombre de cada comensal.
 * - total			- Total a pagar por cada comensal.
 * - platos			- Conjunto de todos los platos que ha consumido dicho comensal, junto
 * 					con la información de los mismos.
 * @author Abel
 *
 */
public class PadreGridViewCalculadora {
	private int pos;
	private String nombre;
	private double total;
	private ArrayList<InfomacionPlatoPantallaReparto> platos;
	
	public PadreGridViewCalculadora(int i, int pos){
		nombre = "Persona " + i;
		total = 0;
		platos = new ArrayList<InfomacionPlatoPantallaReparto>();
		this.pos = pos;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getTotal() {
		return Math.rint(total*100)/100;
	}
	
	public int getPos(){
		return pos;
	}
	
	/*
	 * Metodo encargado de devolver el número de personas con las que tiene compartido
	 * un determinado plato un usuario.
	 */
	public int dameNumeroPersonasCompartidoPlato(int posPlato){
		return platos.get(posPlato).getNumPersonasRepartido();
	}
	
	public InformacionPlatoCantidad getPlato(int pos) {
		String nombrePlato = platos.get(pos).getNombrePlato();
		int porcion = platos.get(pos).getNumPersonasRepartido();
		double precioPlato = platos.get(pos).getPrecioPlato();
		double precioPagar = platos.get(pos).precioPorPersona();
		String por = "1";
		if(porcion > 1){
			por = "1/" + porcion;
		}
		InformacionPlatoCantidad info = new InformacionPlatoCantidad(nombrePlato, por, precioPlato, precioPagar);
		return info;
	}
	
	public String dameIdPlatoEnPedido(int posPlato){
		return platos.get(posPlato).getIdPlatoEnPedido();
	}
	
	public int getNumPlatos() {
		return platos.size();
	}

	// Se encarga de actualizar la información de un comensal cuando se le asigna un plato que ha tomado
	public void anadePlato(String idPlatoEnPedido, String plato, double precio, int numPersonas){
		InfomacionPlatoPantallaReparto infoPlato = new InfomacionPlatoPantallaReparto(idPlatoEnPedido, plato, 0, precio, numPersonas);
		platos.add(infoPlato);
		total += infoPlato.precioPorPersona();
	}
	
	// Se encarga de actualizar la información de un comensal cuando se le asigna un plato que ha tomado
	public void quitaPlato(String idPlatoEnPedido){
		// Buscamos el plato por su idPlatoEnPedido
		boolean encontrado = false;
		int i, numPlatos;
		numPlatos = platos.size();
		i = 0;
		while(i<numPlatos && !encontrado){
			if(platos.get(i).getIdPlatoEnPedido() == idPlatoEnPedido){
				encontrado = true;
			}else{
				i++;
			}
		}
		
		// Vemos si lo hemos encontrado que va a ser siempre si no ha habido errores
		if(encontrado){
			// Restamos el precio que suponían antes el plato
			total -= platos.get(i).precioPorPersona();
			// Eliminamos el plato de la lista de platos de esa persona
			platos.remove(i);
		}		
	}
	
	// Se encarga de actualizar la información de un comensal cuando se le asigna un plato que ha tomado
	public void reajustaTotalPersona(String idPlatoEnPedido, int numPersonas){
		// Buscamos el plato por su idPlatoEnPedido
		boolean encontrado = false;
		int i, numPlatos;
		numPlatos = platos.size();
		i = 0;
		while(i<numPlatos && !encontrado){
			if(platos.get(i).getIdPlatoEnPedido() == idPlatoEnPedido){
				encontrado = true;
			}else{
				i++;
			}
		}
		
		// Vemos si lo hemos encontrado que va a ser siempre si no ha habido errores
		if(encontrado){
			// Restamos el precio que suponían antes el plato
			total -= platos.get(i).precioPorPersona();
			// Reestablecemos el número de personas que ha consumido ese plato
			platos.get(i).setNumPersonasRepartidos(numPersonas);
			// Aumentamos el total del usuario
			total += platos.get(i).precioPorPersona();
		}
	}
}
