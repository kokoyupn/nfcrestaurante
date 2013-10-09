package adapters;

import java.util.ArrayList;


/**
 * Contenido del adapter de la pantalla que contiene los restaurantes del gerente
 * 
 *  -Atributos-
 * nombreRestaurante: nombre del restaurante
 * 
 * @author Alejandro Moran
 *
 */

public class PadreListInformacion {
	
	private String nombreRestaurante;
	private ArrayList<Integer> repetidos;

	
	public PadreListInformacion(String nombreRestaurante, int id){
		this.nombreRestaurante = nombreRestaurante;
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