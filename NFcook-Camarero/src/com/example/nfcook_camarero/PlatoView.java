package com.example.nfcook_camarero;


public class PlatoView{


    private String nombreP; 
    private String imagen;
    private String idPlato;
    private double precio;
    
    public PlatoView(String nombre,String img, String id, double prec) {
    	nombreP = nombre;
    	imagen = img;
    	idPlato = id;
    	precio = prec;
    }

    public String getNombrePlato(){
    	return nombreP;
    }

	public String getImagen() {
		return imagen;
	}
   
    public String getIdPlato(){
    	return idPlato;
    }
    
    public Double getPrecio(){
    	return precio;
    }
    
}
