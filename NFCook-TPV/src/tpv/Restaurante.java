package tpv;

import interfaz.InterfazPlatos;
import interfaz.VentanaMesas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import sockets.ClienteFichero;
import sockets.OperacionesSocketsSinBD;
import tpv.Mesa.estadoMesa;
import basesDeDatos.Operaciones;

public class Restaurante {
	
	private HashMap<String,Mesa> mesasRestaurante; // La clave es el id de la mesa.
	private HashMap<String,Producto> productosRestaurante; //La clave es el id del producto.
	private static PriorityQueue<Producto> productosFavoritos;
	private ArrayList<String> idsCamareros;
	private HistoricoComandasMesas comandasMesas;
	
	private VentanaMesas ventanaMesas;
	
	private InterfazPlatos ventanaPlatos;
	
	private final static String nombreRestaurante = "'Foster'";
	


	public Restaurante(){
		productosRestaurante = new HashMap<String,Producto>();
		productosFavoritos = new PriorityQueue<Producto>(3,new MiComparator());
		mesasRestaurante = new HashMap<String,Mesa>();
		comandasMesas = new HistoricoComandasMesas();
		idsCamareros = new ArrayList<String>();
		cargarMesas();
		cargarProductos();
		cargarProductosFavoritos();
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
					nuevoProducto = new Plato(id, categoria, tipo, nombre, descripcion, foto, precio, null, extras,0);
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
	
	public static void cargarProductosFavoritos(){
		try{
			Operaciones operacion = new Operaciones("MiBaseFav.db");
			ResultSet resultados = operacion.consultar("select * from Restaurantes where Restaurante=" + nombreRestaurante);
			productosFavoritos = new PriorityQueue<Producto>(1,new MiComparator());
			
			while(resultados.next()){
				String id = resultados.getString("Id");
				String categoria = resultados.getString("Categoria");
				String tipo = resultados.getString("TipoPlato");
				String nombre = resultados.getString("Nombre");
				String descripcion = resultados.getString("Descripcion");
				String foto = resultados.getString("Foto");
				String extras = resultados.getString("Extras");
				double precio = resultados.getDouble("Precio");
				int cant = resultados.getInt("CantidadPedido");
				
				Producto nuevoProducto;
				
				if(categoria.equals("Bebidas")){
					nuevoProducto = new Bebida(id, categoria, tipo, nombre, descripcion, foto, precio, null);
				}else{
					nuevoProducto = new Plato(id, categoria, tipo, nombre, descripcion, foto, precio, null, extras,cant);
				}
				
				productosFavoritos.add(nuevoProducto);
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
			ResultSet resultados = operacion.consultar("select * from mesasRestaurante");
			ArrayList<ArrayList<String>> mesasVisitadas = ClienteFichero.pideMesasVisitadas();
			while(resultados.next()){
				String idMesa = resultados.getString("idMesa");
				int numeroPersonas = resultados.getInt("numeroPersonas");
				String idCamarero = resultados.getString("idCamarero");
				int estadoMesa = resultados.getInt("estadoMesa");
				

				Mesa nuevaMesa = new Mesa(idMesa, numeroPersonas, idCamarero, estadoMesa);
				int visitas = getVisitasMesas(mesasVisitadas, idMesa);
				while (visitas > 0){
					nuevaMesa.setVisitada(true);
					visitas--;
				}
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

	public static int getVisitasMesas(ArrayList<ArrayList<String>> mesas, String idMesa){
		Iterator<ArrayList<String>> itMesas = mesas.iterator();
		while (itMesas.hasNext()){
			ArrayList<String> mesa = itMesas.next();
			if(mesa.get(0).contentEquals(idMesa)){
				return Integer.parseInt(mesa.get(1));
			}
		}
		return 0;
	}
	
	public void setVentanaMesas(VentanaMesas ventanaMesas){
		this.ventanaMesas = ventanaMesas;
	}
	
	public void setIterfazPlatos(InterfazPlatos ventanaPlatos){
		this.ventanaPlatos = ventanaPlatos;
	}
	
	public void refrescaVentanaMesas(){
		if(ventanaMesas!=null){
			ventanaMesas.refrescarMesasPanel();
		}
	}
	
	public void refrescaInterfazPlatos(){
		if(ventanaPlatos!=null){
			ventanaPlatos.refrescarTabla();
		}
	}
	
	public void cerrarInterfazPlatosSiAbierta(String idMesa, int estado) {
		if(ventanaPlatos!=null && estado == 0){
			ventanaPlatos.mostrarAvisoYcerrar(idMesa);
		}
	}
	
	public void añadirProductoEnMesa(String idMesa, Producto producto, String extrasMarcados, String observaciones){
		
		if(producto instanceof Plato){
			((Plato) producto).setExtrasMarcados(extrasMarcados);
		}
		
		producto.setObservaciones(observaciones);
		
		mesasRestaurante.get(idMesa).añadirProducto(new TuplaProdEnv(producto, false));
		
	}
	
	public void añadirProductoEnMesaTrue(String idMesa, Producto producto, String extrasMarcados, String observaciones){
		
		if(producto instanceof Plato){
			((Plato) producto).setExtrasMarcados(extrasMarcados);
		}
		
		producto.setObservaciones(observaciones);
		
		mesasRestaurante.get(idMesa).añadirProducto(new TuplaProdEnv(producto, true));
		
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
	
	
	public String getNombreRestaurante() {
		return nombreRestaurante;
	}
	
	public int getNumeroPersonasMesa(String idMesa){
		return mesasRestaurante.get(idMesa).getNumeroPersonas();
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
		OperacionesSocketsSinBD operacionIntroducirComandaBD = new OperacionesSocketsSinBD();
		operacionIntroducirComandaBD.introducirComandaBD(arrayConsultas);
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
		FechaYHora dia = new FechaYHora();
		return "insert into infoMesas values('" + dia.getDia() + "',"+
											 "'" + idMesa + "',"+
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
		
		FechaYHora dia = new FechaYHora();
		return "insert into infoMesas values('" + dia.getDia() + "',"+
											 "'" + idMesa + "',"+
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
		
		//Eliminamos la fecha
		consultaTokenizada.nextToken();
		
		String idMesa = devuelveStringSinComillas(consultaTokenizada.nextToken());
		//Eliminamos el idCamarero
		consultaTokenizada.nextToken();
		String idProducto = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String categoria = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String tipo = devuelveStringSinComillas(consultaTokenizada.nextToken());
		String nombreProducto = devuelveStringSinComillas(consultaTokenizada.nextToken());
		double precio = Double.parseDouble(devuelveStringSinComillas(consultaTokenizada.nextToken()));
		
		String tokenObservaciones = consultaTokenizada.nextToken();
		String observaciones = "";
		if(empiezaPorComilla(tokenObservaciones) && terminaPorComilla(tokenObservaciones)){
			observaciones = devuelveStringSinComillas(tokenObservaciones);
		}else if(empiezaPorComilla(tokenObservaciones)){
			boolean primero = true;
			while(!terminaPorComilla(tokenObservaciones)){
				if(primero){
					observaciones += devuelveStringSinComillas(tokenObservaciones);
					primero = false;
				}else{
					observaciones += "," + devuelveStringSinComillas(tokenObservaciones);
				}
				tokenObservaciones = consultaTokenizada.nextToken();
			}
			if(primero){
				observaciones += devuelveStringSinComillas(tokenObservaciones);
			}else{
				observaciones += "," + devuelveStringSinComillas(tokenObservaciones);
			}
		}
		
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
				nuevoProducto = new Plato(idProducto, categoria, tipo, nombreProducto, resultados.getString("Descripcion"), resultados.getString("Foto"), precio, observaciones, extras, extrasMarcados,0);
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
			if(conComillas.charAt(0) == '\''){
				conComillas = conComillas.replaceFirst("'", "");
			}
			return conComillas.replace("')", "");
		}else if(conComillas.charAt(0) != '\'' && conComillas.charAt(conComillas.length()-1) == '\''){
			return conComillas = conComillas.replaceFirst("'", "");
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
	
	public String getIdCamareroDadaMesa(String idMesa){
		return mesasRestaurante.get(idMesa).getIdCamarero();
	}
	
	public void cargarRestaurante(){
		
		FechaYHora dia = new FechaYHora();
		Operaciones operacionSQlite = new Operaciones("InfoMesas.db");
		ResultSet resultados = operacionSQlite.consultar("select * from infoMesas where dia='" + dia.getDia() + "'" );
		try {
			while(resultados.next()){
				String idMesa = resultados.getString("idMesa");
				//String idCamarero = resultados.getString("idCamarero");
				String idProducto = resultados.getString("idProducto");
				String categoria = resultados.getString("categoria");
				String tipo = resultados.getString("tipo");
				String nombreProducto = resultados.getString("nombreProducto");
				double precio = resultados.getDouble("precio");
				String observaciones = resultados.getString("observaciones");
				String extras = resultados.getString("extras");
				String extrasMarcados = resultados.getString("extrasMarcados");
				
				Operaciones operacionSQliteMiBase = new Operaciones("MiBase.db");
				ResultSet resultadosMiBase = operacionSQliteMiBase.consultar("select Descripcion, Foto from Restaurantes where Restaurante=" + nombreRestaurante + " and Id='" + idProducto+ "'");
				try {
					resultadosMiBase.next();
					Producto nuevoProducto = null;
					
					if(categoria.equals("Bebidas")){
						nuevoProducto = new Bebida(idProducto, categoria, tipo, nombreProducto, resultadosMiBase.getString("Descripcion"), resultadosMiBase.getString("Foto"), precio, observaciones);
					}else{
						nuevoProducto = new Plato(idProducto, categoria, tipo, nombreProducto, resultadosMiBase.getString("Descripcion"), resultadosMiBase.getString("Foto"), precio, observaciones, extras, extrasMarcados,0);
					}
					
					mesasRestaurante.get(idMesa).añadirProducto(new TuplaProdEnv(nuevoProducto, true));
					actualizaEstadoMesaComanda(idMesa);
					
					operacionSQliteMiBase.cerrarBaseDeDatos();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
			operacionSQlite.cerrarBaseDeDatos();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void modificarEstadoMesallegadaPorLan(String idMesa, String idCamarero, int numeroPersonas, tpv.Mesa.estadoMesa estadoMesa){
		if(estadoMesa == tpv.Mesa.estadoMesa.ABIERTA){
			mesasRestaurante.get(idMesa).abrirMesa();
		}else if(estadoMesa == tpv.Mesa.estadoMesa.CERRADA){
			mesasRestaurante.get(idMesa).cerrarMesa();
		}
		mesasRestaurante.get(idMesa).setNumeroPersonas(numeroPersonas);
		mesasRestaurante.get(idMesa).setIdCamarero(idCamarero);
	}

	public void actualizaMesaLLegadaExtarna(String idMesa, String idCamarero, int numeroPersonas, int estado) {
		mesasRestaurante.get(idMesa).setNumeroPersonas(numeroPersonas);
		mesasRestaurante.get(idMesa).setIdCamarero(idCamarero);
		mesasRestaurante.get(idMesa).setEstado(estado);
	}
	
	public void actualizaMesaEstaVisitadaLLegadaExterna(String idMesa, boolean visitada){
		mesasRestaurante.get(idMesa).setVisitada(visitada);
	}
	
	public void actualizaMesaEstaVisitadaCobrarLLegadaExterna(String idMesa) {	
		mesasRestaurante.get(idMesa).eliminaTodasLasVisitas();
	}

	public boolean mesaVisitada(String idMesa) {
		return mesasRestaurante.get(idMesa).isVisitada();
	}
	
	public Iterator<TuplaProdEnv> getIteratorProductosEnMesa(String idMesa){
		return mesasRestaurante.get(idMesa).getIteratorProductosEnMesa();
	}
	
	public Iterator<Producto> getIteratorFavoritos(){
		return productosFavoritos.iterator();
	}
	
	public PriorityQueue<Producto> getCopiaFavoritos(){
		return copiaFavoritos();
	}
	
	public PriorityQueue<Producto> getFavoritos(){
		return productosFavoritos;
	}

	public void setProductosFavoritos(PriorityQueue<Producto> productosFavoritos) {
		Restaurante.productosFavoritos = productosFavoritos;
	}

	public void eliminaProductosDeMesa(String idMesa) {
		mesasRestaurante.get(idMesa).eliminaTodosLosPedidos();
	}
	
	public PriorityQueue<Producto> copiaFavoritos(){
		PriorityQueue<Producto> result = new PriorityQueue<Producto>(3,new MiComparator());
		Iterator<Producto> itFavs = productosFavoritos.iterator();
		while(itFavs.hasNext()){
			result.add(itFavs.next());
		}
		return result;
	}
	
	/**
	 * Busca en favoritos el producto prod y le pone la catidad a la cantidad de prod
	 * @param prod
	 */
	public static void actualizaFavs(Producto prod) {
		boolean enc = false;
		Iterator<Producto> itFavs = productosFavoritos.iterator();
		while(itFavs.hasNext() && !enc){
			Producto p = itFavs.next();
			if(p.getId() == prod.getId() && (p instanceof Plato)){
				((Plato)p).setCantiadPedido(((Plato)prod).getCantiadPedido());
				enc = true;
			}
		}

		Operaciones operacionSQlite = new Operaciones("MiBaseFav.db");
		operacionSQlite.actualizaFav(prod.getId(), ((Plato)prod).getCantiadPedido());
		operacionSQlite.cerrarBaseDeDatos();
		
	}
	
	public Producto dameProductoRestauranteDadoID(String id){
		return productosRestaurante.get(id);
	}
	
	/**
	 * Compara productos por la cantidad de veces pedidos
	 * @author Guille
	 *
	 */
	public static class MiComparator implements Comparator<Object>{

		@Override
		public int compare(Object arg0, Object arg1) {
			if(arg0 instanceof Plato && arg1 instanceof Plato){
				if (((Plato)arg0).getCantiadPedido() < ((Plato)arg1).getCantiadPedido()){
					return 1;
				}
				if (((Plato)arg0).getCantiadPedido() > ((Plato)arg1).getCantiadPedido()){
					return -1;
				}
				return 0;
			}
			//Es bebida
			return 0;
		}
		
	}

	
}



