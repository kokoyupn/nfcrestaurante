package com.example.nfcook_camarero;
import java.util.ArrayList;

public class InfoPlato {
	
	ArrayList<String> extras;
	String observaciones;
	String idPlato;
	
	public InfoPlato(){
		super();
	}
	
	public ArrayList<String> getExtras() {
		return extras;
	}
	public void setExtras(ArrayList<String> extras) {
		this.extras = extras;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getIdPlato() {
		return idPlato;
	}
	public void setIdPlato(String idPlato) {
		this.idPlato = idPlato;
	}
}
