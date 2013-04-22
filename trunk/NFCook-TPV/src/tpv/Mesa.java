package tpv;

import java.util.ArrayList;

import tpv.Mesa.estadoMesa;


public class Mesa {

	public enum estadoMesa{CERRADA, ABIERTA, COMANDA};
	
	private ArrayList<TuplaProdEnv> productosEnMesa;
	private String idMesa;
	private int numeroPersonas;
	private String idCamarero;
	private estadoMesa estado;
	private double dineroTotalEnMesa;
	private boolean visitada;
	
	
	public Mesa(String idMesa, int numeroPersonas, String idCamarero) {
		productosEnMesa = new ArrayList<TuplaProdEnv>();
		this.idMesa = idMesa;
		this.numeroPersonas = numeroPersonas;
		if(!idCamarero.equals("-")){
			this.idCamarero = idCamarero;
		}
		estado = estadoMesa.CERRADA;
		dineroTotalEnMesa = 0;
	}

	public ArrayList<TuplaProdEnv> getProductosEnMesa() {
		return productosEnMesa;
	}
	public void setProductosEnMesa(ArrayList<TuplaProdEnv> productosEnMesa) {
		this.productosEnMesa = productosEnMesa;
	}
	
	public String getIdMesa() {
		return idMesa;
	}

	public int getNumeroPersonas() {
		return numeroPersonas;
	}

	public String getIdCamarero() {
		return idCamarero;
	}

	public estadoMesa getEstado() {
		return estado;
	}
	

	public double getDineroTotalEnMesa() {
		return dineroTotalEnMesa;
	}
	
	public void setNumeroPersonas(int numeroPersonas){
		this.numeroPersonas = numeroPersonas;
	}
	
	public void setIdCamarero(String idCamarero) {
		this.idCamarero = idCamarero;
	}

	public void añadirProducto(TuplaProdEnv producto) {
		productosEnMesa.add(producto);
		dineroTotalEnMesa+=producto.getProd().getPrecio();
	}

	public void abrirMesa() {
		estado = estadoMesa.ABIERTA;
	}
	
	public void cerrarMesa() {
		estado = estadoMesa.CERRADA;
	}
	
	public void activarComanda(){
		estado = estadoMesa.COMANDA;
	}
	
	public void desactivarComanda(){
		estado = estadoMesa.ABIERTA;
	}

	public boolean mesaVacia() {
		return productosEnMesa.size() == 0;
	}
	
	public double actualizarDineroTotal(){
		dineroTotalEnMesa = 0;
		for(int i = 0; i < productosEnMesa.size();i++){
			dineroTotalEnMesa += productosEnMesa.get(i).getProd().getPrecio();
		}
		return dineroTotalEnMesa;
	}
	
	public int campareTo(Mesa mesa){
		if(this.idMesa.length()>mesa.idMesa.length()){
			return 1;
		}else if(this.idMesa.length()<mesa.idMesa.length()){
			return -1;
		}else{
			return this.idMesa.compareTo(mesa.idMesa);
		}
		
	}

	public boolean esMesaCerrada() {
		return estado == estadoMesa.CERRADA;
	}

	public void setVisitada(boolean visitada) {
		this.visitada = visitada;
	}
	
	public boolean isVisitada(){
		return visitada;
	}

	public void setEstado(int estado) {
		switch (estado) {
		case 0:
			this.estado = estadoMesa.CERRADA;
			break;
		case 1:
			this.estado = estadoMesa.ABIERTA;
			break;
		case 2:
			this.estado = estadoMesa.COMANDA;
			break;
		}
	}

	public static int getIntDadoEstado(estadoMesa estado) {
		if(estado == estadoMesa.CERRADA){
			return 0;
		}else if(estado == estadoMesa.ABIERTA){
			return 1;
		}else if(estado == estadoMesa.COMANDA){
			return 2;
		}else{
			return -1;
		}
	}

}
