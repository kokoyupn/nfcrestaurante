package tpv;

import java.util.ArrayList;

enum estadoMesa{CERRADA,ABIERTA};

public class Mesa {
	
	private ArrayList<Producto> productosEnMesa;
	private String idMesa;
	private int numeroPersonas;
	private String idCamarero;
	private estadoMesa estado;
	private double dineroTotalEnMesa;
	
	
	public Mesa(String idMesa, int numeroPersonas) {
		this.idMesa = idMesa;
		this.numeroPersonas = numeroPersonas;
		estado = estadoMesa.CERRADA;
		dineroTotalEnMesa = 0;
	}

	public ArrayList<Producto> getProductosEnMesa() {
		return productosEnMesa;
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

	public void añadirProducto(Producto producto) {
		productosEnMesa.add(producto);
		dineroTotalEnMesa+=producto.getPrecio();
	}
	
	public void abrirMesa(){
		estado = estadoMesa.ABIERTA;
	}
	
	public void cerrarMesa(){
		estado = estadoMesa.CERRADA;
	}
	
	

}
