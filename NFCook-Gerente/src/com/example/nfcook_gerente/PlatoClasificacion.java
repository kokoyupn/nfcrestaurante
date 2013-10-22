package com.example.nfcook_gerente;

public class PlatoClasificacion {

	private String restaurante, nombre, foto;
	private int precio, cantidadPedido;
	
	public PlatoClasificacion (String restaurante, String nombre, String foto, int precio, int cantidadPedido){
		this.restaurante = restaurante;
		this.nombre = nombre;
		this.foto = foto;
		this.precio = precio;
		this.cantidadPedido = cantidadPedido;
		
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
	
	
}
