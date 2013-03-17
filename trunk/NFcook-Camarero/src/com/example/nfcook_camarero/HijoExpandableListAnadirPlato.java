package com.example.nfcook_camarero;

import java.util.ArrayList;


public class HijoExpandableListAnadirPlato {
	private ArrayList<String> idHijos;
	private ArrayList<String> numImag;
	private ArrayList<String> nombrePl;
	private ArrayList<Float> precio;
	
	
	public HijoExpandableListAnadirPlato(ArrayList<String> idHijos, ArrayList<String> numImag, ArrayList<String> nombrePl, ArrayList<Float> precio) {
		this.idHijos = idHijos;
		this.numImag = numImag;
		this.nombrePl = nombrePl;
		this.precio = precio;
	}

	public ArrayList<String> getIds() {
		return idHijos;
	}
	
	public ArrayList<String> getNumImagenes() {
		return numImag;
	}
	
	public ArrayList<String> getNombrePl() {
		return nombrePl;
	}
	
	public ArrayList<Float> getPrecio() {
		return precio;
	}
	
}
