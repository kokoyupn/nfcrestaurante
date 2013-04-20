package tpv;

import interfaz.VentanaMesas;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import tpv.Mesa.estadoMesa;

import basesDeDatos.Operaciones;

public class Restaurante {
	
	private HashMap<String,Mesa> mesasRestaurante; // La clave es el id de la mesa.
	private HashMap<String,Producto> productosRestaurante; //La clave es el id del producto.
	private ArrayList<String> idsCamareros;
	private HistoricoComandasMesas comandasMesas;
	
	private VentanaMesas ventanaMesas;
	
	private final String nombreRestaurante = "'Foster'";
	
	public Restaurante(){
		productosRestaurante = new HashMap<String,Producto>();
		mesasRestaurante = new HashMap<String,Mesa>();
		comandasMesas = new HistoricoComandasMesas();
		idsCamareros = new ArrayList<String>();
		cargarMesas();
		cargarProductos();
		cargarCamareros();
	}
	
	private void cargarCamareros() {
		try{
			Operaciones operacion = new Operaciones("login.db");
			ResultSet resultados = operacion.consultar("select * from camareros");
			
			while(resultados.next()){
				String idCamarero = resultados.getString("IdCamarero");
				idsCamareros.add(idCamarero);
			}
			operacion.cerrarBaseDeDatos();
			
        }catch (SQLException e) {
            System.out.println("Mensaje:"+e.getMessage());
            System.out.println("Estado:"+e.getSQLState());
            System.out.println("Codigo del error:"+e.getErrorCode());
            JOptionPane.showMessageDialog(null, ""+e.getMessage());
        }		
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
	
	public void setVentanaMesas(VentanaMesas ventanaMesas){
		this.ventanaMesas = ventanaMesas;
	}
	
	public void refrescaVentanaMesas(){
		if(ventanaMesas!=null){
			ventanaMesas.refrescarMesasPanel();
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

	public void actualizaEstadoMesaAbierta(String idMesa, String idCamarero) {
		mesasRestaurante.get(idMesa).abrirMesa();		
		mesasRestaurante.get(idMesa).setIdCamarero(idCamarero);
	}
	
	public void actualizaEstadoMesaCerrada(String idMesa) {
		mesasRestaurante.get(idMesa).cerrarMesa();				
	}
	
	public void actualizaEstadoMesaComanda(String idMesa) {
		mesasRestaurante.get(idMesa).activarComanda();				
	}

	public Iterator<Producto> getIteratorProductos(){
		return productosRestaurante.values().iterator();
	}

	public boolean mesaVacia(String idMesa) {
		return mesasRestaurante.get(idMesa).mesaVacia();
	}

	public estadoMesa dameEstadoMesa(String idMesa) {
		return mesasRestaurante.get(idMesa).getEstado();
	}
	
	/**
	 * Añadimos la comanda, la enviamos a cocina y por Red local para los demas tpv
	 * @param idMesa
	 * @param idCamarero
	 * @param productos
	 */
	public void addComandaAMesa(String idMesa, String idCamarero, ArrayList<Producto> productos){
		Comanda comanda = new Comanda(productos, idMesa, idCamarero);
		comandasMesas.añadirComandaPorMesa(idMesa, comanda);
		mesasRestaurante.get(idMesa).activarComanda();
		
		ArrayList<String> arrayConsultas = creaArrayConsultasInfoMesa(idMesa, idCamarero, productos);
		Operaciones operacionSQlite = new Operaciones("InfoMesas.db");
		operacionSQlite.introducirComandaBD(arrayConsultas);
		operacionSQlite.cerrarBaseDeDatos();
	}
	
	public boolean existeCamarero(String idCamarero){
		return idsCamareros.contains(idCamarero);
	}
	
	public ArrayList<String> creaArrayConsultasInfoMesa(String idMesa, String idCamarero, ArrayList<Producto> productos){
		Iterator<Producto> itProductos = productos.iterator();
		ArrayList<String> arrayConsultas = new ArrayList<String>();
		while(itProductos.hasNext()){
			Producto unProducto = itProductos.next();
			arrayConsultas.add(crearConsultaProducto(unProducto, idCamarero, idMesa));
		}
		return arrayConsultas;
	}

	public String crearConsultaProducto(Producto unProducto, String idCamarero, String idMesa) {
		String idProducto = unProducto.getId();
		String categoria = unProducto.getCategoria();
		String tipo = unProducto.getTipo();
		String nombreProducto = unProducto.getNombre();
		double precio = unProducto.getPrecio();
		String observaciones = unProducto.getObservaciones();
		String extrasMarcados = null;
		String extras = null;
	
		String consulta = null;
		
		if(unProducto instanceof Plato){
			extrasMarcados = ((Plato) unProducto).getExtrasMarcados();
			extras = ((Plato) unProducto).getExtras();
			consulta = generaConsultaPlato(idMesa, idCamarero, idProducto, categoria, tipo, nombreProducto, precio, observaciones, extras, extrasMarcados);

		}else{
			consulta = generaConsultaBeida(idMesa, idCamarero, idProducto, categoria, tipo, nombreProducto, precio, observaciones);
		}
		return consulta;
	}

	private String generaConsultaBeida(String idMesa, String idCamarero,
									   String idProducto, String categoria, String tipo,
									   String nombreProducto, double precio, String observaciones) {
		
		return "insert into infoMesas values('"+ idMesa + "',"+
											 "'" + idCamarero + "'," +
											 "'" + idProducto + "'," +
											 "'" + categoria + "'," +
											 "'" + tipo + "',"+
											 "'" + nombreProducto + "'," +
											 "'" + precio + "'," +
											 "'" + observaciones + "',"+
											 "'" + null +"'," +
											 "'" + null + "'"+
											 ")";
	}

	private String generaConsultaPlato(String idMesa, String idCamarero,String idProducto, String categoria, String tipo,
									   String nombreProducto, double precio, String observaciones,
									   String extras, String extrasMarcados) {
		
		return "insert into infoMesas values('"+ idMesa + "',"+
											"'" + idCamarero + "'," +
											"'" + idProducto + "'," +
											"'" + categoria + "'," +
											"'" + tipo + "',"+
											"'" + nombreProducto + "'," +
											"'" + precio + "'," +
											"'" + observaciones + "',"+
											"'" + extras +"'," +
											"'" + extrasMarcados + "'"+
											")";
	}

	public void cargarConsultaARestaurante(String consulta) {
		StringTokenizer consultaTokenizada = new StringTokenizer(consulta , ",");		
		
		String idMesa = devuelveStringSinComillas(consultaTokenizada.nextToken());
		//Eliminamos el idCamarero
		consultaTokenizada.nextToken();
		String idProducto = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String categoria = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String tipo = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String nombreProducto = devuelveStringSinComillas(consultaTokenizada.nextToken());
		double precio = Double.parseDouble(devuelveStringSinComillas(consultaTokenizada.nextToken()));
		String observaciones = devuelveStringSinComillas(consultaTokenizada.nextToken());
		
		String tokenExtras = consultaTokenizada.nextToken();
		String extras = "";
		if(empiezaPorComilla(tokenExtras) && terminaPorComilla(tokenExtras)){
			extras = devuelveStringSinComillas(tokenExtras);
		}else if(empiezaPorComilla(tokenExtras)){
			boolean primero = true;
			while(!terminaPorComilla(tokenExtras)){
				if(tokenExtras.contains("\\") || primero){
					extras += devuelveStringSinComillas(tokenExtras);
					primero = false;
				}else{
					extras += "," + devuelveStringSinComillas(tokenExtras);
				}
				tokenExtras = consultaTokenizada.nextToken();
			}
			extras += devuelveStringSinComillas(tokenExtras);
		}
		
		String tokenExtrasMarcados = consultaTokenizada.nextToken();
		String extrasMarcados = "";
		if(empiezaPorComilla(tokenExtrasMarcados) && terminaPorComilla(tokenExtrasMarcados)){
			extrasMarcados = devuelveStringSinComillas(tokenExtrasMarcados);
		}else if(empiezaPorComilla(tokenExtrasMarcados)){
			boolean primero = true;
			while(!terminaPorComilla(tokenExtrasMarcados)){
				if(primero){
					extrasMarcados += devuelveStringSinComillas(tokenExtrasMarcados);
					primero = false;
				}else{
					extrasMarcados += "," + devuelveStringSinComillas(tokenExtrasMarcados);
				}
				tokenExtrasMarcados = consultaTokenizada.nextToken();
			}
			if(primero){
				extrasMarcados += devuelveStringSinComillas(tokenExtrasMarcados);
			}else{
				extrasMarcados += "," + devuelveStringSinComillas(tokenExtrasMarcados);
			}
		}
		
		Operaciones operacionSQlite = new Operaciones("MiBase.db");
		ResultSet resultados = operacionSQlite.consultar("select Descripcion, Foto from Restaurantes where Restaurante=" + nombreRestaurante + " and Id='" + idProducto+ "'");
		try {
			resultados.next();
			Producto nuevoProducto = null;
			
			if(categoria.equals("Bebidas")){
				nuevoProducto = new Bebida(idProducto, categoria, tipo, nombreProducto, resultados.getString("Descripcion"), resultados.getString("Foto"), precio, observaciones);
			}else{
				nuevoProducto = new Plato(idProducto, categoria, tipo, nombreProducto, resultados.getString("Descripcion"), resultados.getString("Foto"), precio, observaciones, extras, extrasMarcados);
			}
			
			mesasRestaurante.get(idMesa).añadirProducto(new TuplaProdEnv(nuevoProducto, true));
			actualizaEstadoMesaComanda(idMesa);
			
			operacionSQlite.cerrarBaseDeDatos();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private String devuelveStringSinComillas(String conComillas){
		if(!conComillas.contains("'")){
			return conComillas;
		}else if(conComillas.contains(")")){
			return conComillas.replace("')", "");
		}
		int posChar = 0;
		String conComillasAux = "";
		boolean encontrado = false;
		
		/*
		 * Nos quedamos solo con 'token'
		 */
		
		while(posChar< conComillas.length()){
			if((conComillas.charAt(posChar) == '\'') && !encontrado){
				encontrado = true;
				conComillasAux += conComillas.charAt(posChar);
			}else if(encontrado){
				conComillasAux += conComillas.charAt(posChar);
			}
			posChar++;
		}
		
		/*
		 * Eliminamos '   ' y )
		 */
		posChar = 0;
		String sinComillas = "";
		int a  = conComillasAux.length();
		while(posChar< conComillasAux.length()){
			if((conComillasAux.charAt(posChar) == '\'' || conComillasAux.charAt(posChar) == ')') && (posChar == 0 || posChar == (conComillasAux.length()-1))){
				posChar++;
			}else{
				sinComillas += conComillasAux.charAt(posChar);
				posChar++;
			}
		}
		
		return sinComillas;	
	}
	
	public boolean empiezaPorComilla(String palabra){
		return palabra.charAt(0) == '\'';
	}
	
	public boolean terminaPorComilla(String palabra){
		return palabra.charAt(palabra.length()-1) == '\'' || palabra.charAt(palabra.length()-1) == ')';
	}
	
}
