package adapters;

import java.util.ArrayList;

public class ContenidoListMesa {
	
	private String nombre, extras, observaciones;
	private int id;
	private String idPlato;
	private double precio;
	private int cantidad;
	private ArrayList<Integer> repetidos;
	
	public ContenidoListMesa(String nombre,String extras,String observaciones,double precio,int id,String idPlato){
		this.nombre = nombre;
		this.extras = extras;
		this.observaciones = observaciones;
		this.precio = precio;
		this.id = id;
		this.idPlato = idPlato;
		this.cantidad = 1;
		repetidos=new ArrayList<Integer>();
		repetidos.add(id);
	}
	
	
	
	public String getIdPlato(){
		return idPlato;
	}
	
	public int getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getExtras() {
		return extras;
	}
	
	public void setExtras(String extras) {
		this.extras = extras;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public double getPrecio() {
		return precio*cantidad;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public int getCantidad() {
		return cantidad;
	}

	public void sumaCantidad() {
		cantidad++;
		
	}

	public void restaCantidad() {
		cantidad--;
		
	}



	public void aniadeId(int id) {
		repetidos.add(id);
		
	}



	public void eliminaId() {
		repetidos.remove(0);//FIXME no se si bien
		
	}

	

}
