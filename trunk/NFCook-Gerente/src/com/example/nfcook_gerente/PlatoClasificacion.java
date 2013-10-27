package com.example.nfcook_gerente;

public class PlatoClasificacion {

	private String restaurante, nombre, foto;
	private int precio, cantidadPedido, facturacion;
	
	public PlatoClasificacion (String restaurante, String nombre, String foto, int precio, int cantidadPedido){
		this.restaurante = restaurante;
		this.nombre = nombre;
		this.foto = foto;
		this.precio = precio;
		this.cantidadPedido = cantidadPedido;
		this.facturacion = (precio * cantidadPedido);
	}

	public String getRestaurante() {
		return restaurante;
	}

	public String getNombre() {
		return nombre;
	}

	public String getFoto() {
		return foto;
	}

	public int getPrecio() {
		return precio;
	}

	public int getCantidadPedido() {
		return cantidadPedido;
	}

	public int getFacturacion() {
		return facturacion;
	}
	
}
