package com.example.nfcook_camarero;


public class PlatoView{


    private String nombreP; 
    private String imagen;
    private String idPlato;
    private Float precio;
    
    public PlatoView(String nombre,String img, String id, Float prec) {
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
    
    public Float getPrecio(){
    	return precio;
    }
    
}
