package tpv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import tpv.Mesa.estadoMesa;

import basesDeDatos.Operaciones;

public class Restaurante {
	
	private HashMap<String,Mesa> mesasRestaurante; // La clave es el id de la mesa.
	private HashMap<String,Producto> productosRestaurante; //La clave es el id del producto.
	private HistoricoComandasMesas comandasMesas;
	
	private final String nombreRestaurante = "'Foster'";
	
	public Restaurante(){
		productosRestaurante = new HashMap<String,Producto>();
		mesasRestaurante = new HashMap<String,Mesa>();
		comandasMesas = new HistoricoComandasMesas();
		cargarMesas();
		cargarProductos();
	}
	
	private void cargarProductos(){
		try{
			Operaciones operacion = new Operaciones("MiBase.db");
			ResultSet resultados = operacion.consultar("select * from Restaurantes where Restaurante=" +nombreRestaurante);
			
			while(resultados.next()){
				String id = resultados.getString("Id");
				String categoria = resultados.getString("Categoria");
				String tipo = resultados.getString("TipoPlato");
				String nombre = resultados.getString("Nombre");
				String descripcion = resultados.getString("Descripcion");
				String foto = resultados.getString("Foto");
				String extras = resultados.getString("Extras");
				double precio = resultados.getDouble("Precio");
				
				Producto nuevoProducto;
				
				if(categoria.equals("Bebidas")){
					nuevoProducto = new Bebida(id, categoria, tipo, nombre, descripcion, foto, precio, null);
				}else{
					nuevoProducto = new Plato(id, categoria, tipo, nombre, descripcion, foto, precio, null, extras);
				}
				
				productosRestaurante.put(id, nuevoProducto);
			}
			operacion.cerrarBaseDeDatos();
			
        }catch (SQLException e) {
            System.out.println("Mensaje:"+e.getMessage());
            System.out.println("Estado:"+e.getSQLState());
            System.out.println("Codigo del error:"+e.getErrorCode());
            JOptionPane.showMessageDialog(null, ""+e.getMessage());
        }
		
	}
	
	private void cargarMesas(){
		try{
			Operaciones operacion = new Operaciones("MesasRestaurante.db");
			ResultSet resultados = operacion.consultar("select * from mesas");
			
			while(resultados.next()){
				String idMesa = resultados.getString("NumeroMesa");
				int numeroPersonas = resultados.getInt("NumeroPersonas");
				
				Mesa nuevaMesa = new Mesa(idMesa, numeroPersonas);
				mesasRestaurante.put(idMesa, nuevaMesa);
			}
			operacion.cerrarBaseDeDatos();
			
        }catch (SQLException e) {
            System.out.println("Mensaje:"+e.getMessage());
            System.out.println("Estado:"+e.getSQLState());
            System.out.println("Codigo del error:"+e.getErrorCode());
            JOptionPane.showMessageDialog(null, ""+e.getMessage());
        }
		
	}
	
	public void añadirProductoEnMesa(String idMesa, Producto producto, String extrasMarcados, String observaciones){
		
		if(producto instanceof Plato){
			((Plato) producto).setExtrasMarcados(extrasMarcados);
		}
		
		producto.setObservaciones(observaciones);
		
		mesasRestaurante.get(idMesa).añadirProducto(new TuplaProdEnv(producto, false));
		
	}
	
	public Iterator<Mesa> getIteratorMesas(){
		Iterator<Mesa> itMesasSinOrdenar = mesasRestaurante.values().iterator();
		ArrayList<Mesa> arrayMesas = new ArrayList<Mesa>();
		while(itMesasSinOrdenar.hasNext()){
			Mesa unaMesa = itMesasSinOrdenar.next();
			addOrdenado(arrayMesas, unaMesa);
		}
		
		return arrayMesas.iterator();
		
	}

	private void addOrdenado(ArrayList<Mesa> arrayMesas, Mesa mesaAdd) {
		Iterator<Mesa> it = arrayMesas.iterator();
		boolean encontrado = false;
		int i = 0;
		while(it.hasNext() && !encontrado){
			Mesa mesa = it.next();
			if(mesaAdd.campareTo(mesa)<0){
				encontrado = true;
			}else{
				i++;				
			}
		}
		arrayMesas.add(i, mesaAdd);
	}

	public void actualizarNumeroPersonasMesa(String idMesa, int numeroPersonas) {
		mesasRestaurante.get(idMesa).setNumeroPersonas(numeroPersonas);
	}

	public void actualizaEstadoMesaAbierta(String idMesa) {
		mesasRestaurante.get(idMesa).abrirMesa();		
	}

	public Iterator<Producto> getIteratorProductos(){
		return productosRestaurante.values().iterator();
	}

	public void actualizaEstadoMesaCerrada(String idMesa) {
		mesasRestaurante.get(idMesa).cerrarMesa();				
	}

	public boolean mesaVacia(String idMesa) {
		return mesasRestaurante.get(idMesa).mesaVacia();
	}

	public estadoMesa dameEstadoMesa(String idMesa) {
		return mesasRestaurante.get(idMesa).getEstado();
	}
	
	public void addComandaAMesa(String idMesa, String idCamarero, ArrayList<Producto> productos){
		Comanda comanda = new Comanda(productos, idMesa, idCamarero);
		comandasMesas.añadirComandaPorMesa(idMesa, comanda);
		mesasRestaurante.get(idMesa).activarComanda();
	}
	
}
