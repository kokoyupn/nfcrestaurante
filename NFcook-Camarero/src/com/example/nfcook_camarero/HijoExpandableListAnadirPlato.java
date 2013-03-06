package com.example.nfcook_camarero;

import java.util.ArrayList;


public class HijoExpandableListAnadirPlato {
	private ArrayList<String> idHijos;
	private ArrayList<Integer> numImag;
	private ArrayList<String> nombrePl;
	
	
	public HijoExpandableListAnadirPlato(ArrayList<String> idHijos, ArrayList<Integer> numImag, ArrayList<String> nombrePl) {
		this.idHijos = idHijos;
		this.numImag = numImag;
		this.nombrePl = nombrePl;
	}

	public ArrayList<String> getIds() {
		return idHijos;
	}
	
	public ArrayList<Integer> getNumImagenes() {
		return numImag;
	}
	
	public ArrayList<String> getNombrePl() {
		return nombrePl;
	}
	
}
