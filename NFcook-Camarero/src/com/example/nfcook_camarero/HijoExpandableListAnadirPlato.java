package com.example.nfcook_camarero;

import java.util.ArrayList;


public class HijoExpandableListAnadirPlato {
	private ArrayList<String> idHijos;
	private ArrayList<String> numImag;
	private ArrayList<String> nombrePl;
	private ArrayList<Double> precio;
	
	public HijoExpandableListAnadirPlato(ArrayList<String> idHijos, ArrayList<String> numImag, ArrayList<String> nombrePl, ArrayList<Double> precio) {
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
	
	public ArrayList<Double> getPrecio() {
		return precio;
	}
	
	public String getIdPlato(Integer position){
		return idHijos.get(position);
	}

	public String getNombrePlato(int position) {
		return nombrePl.get(position);
	}

	public double getPrecioPlato(int position) {
		return precio.get(position);
	}
	
}
