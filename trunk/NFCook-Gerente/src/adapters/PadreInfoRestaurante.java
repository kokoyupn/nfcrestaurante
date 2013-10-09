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

	private String nombreRestaurante, telefono, direccion;
	private ArrayList<Integer> repetidos;

	
	public PadreInfoRestaurante(String nombreRestaurante, String telefono, String direccion, int id){
		this.nombreRestaurante = nombreRestaurante;
		this.telefono = telefono;
		this.direccion = direccion;
		repetidos=new ArrayList<Integer>();
		repetidos.add(id);
	}


	public String getNombreRestaurante() {
		return nombreRestaurante;
	}
	
	public int getId() {
		return repetidos.get(0);
	}

}
