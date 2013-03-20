package com.example.nfcook_camarero;


public class PadreExpandableListAnadirPlato {
    private String titulo;
    private HijoExpandableListAnadirPlato hijo;
 
    
    
    public PadreExpandableListAnadirPlato(String titulo, HijoExpandableListAnadirPlato hijo) {
		this.titulo = titulo;
		this.hijo = hijo;
	}

	public String getTitle() {
        return titulo;
    }
 
    public void setTitle(String titulo) {
        this.titulo = titulo;
    }
 

    public HijoExpandableListAnadirPlato getHijo() {
        return hijo;
    }

    
    public String toString(){
    	return titulo;
    }
    
    public String getIdPlato(int position){	
    	return hijo.getIdPlato(position);
    }

	public String getNombrePlato(int position) {
		return hijo.getNombrePlato(position);
	}

	public double getPrecioPlato(int position) {
		return  hijo.getPrecioPlato(position);
	}
	
}