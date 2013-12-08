package adapters;

import java.util.ArrayList;


/**
 * Contenido del adapter de la pantalla que contiene 
 * la información de un restaurante
 * 
 *  -Atributos-
 * nombreRestaurante: nombre del restaurante
 * 
 * @author Alejandro Moran
 *
 */
public class PadreInfoRestaurante {

	private String nombreRestaurante, telefono, direccion, logo;
	private ArrayList<Integer> repetidos;

	
	public PadreInfoRestaurante(String nombreRestaurante, String telefono, String direccion, String logo, int id){
		this.nombreRestaurante = nombreRestaurante;
		this.telefono = telefono;
		this.direccion = direccion;
		this.logo = logo;
		repetidos=new ArrayList<Integer>();
		repetidos.add(id);
	}

	public String getNombreRestaurante() {
		return nombreRestaurante;
	}
	
	public int getId() {
		return repetidos.get(0);
	}

	public String getTelefono() {
		return telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getLogo() {
		return logo;
	}

	public ArrayList<Integer> getRepetidos() {
		return repetidos;
	}

}
