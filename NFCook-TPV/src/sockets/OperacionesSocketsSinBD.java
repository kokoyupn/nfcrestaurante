package sockets;

import java.util.ArrayList;
import java.util.Iterator;

import tpv.FechaYHora;
import basesDeDatos.Operaciones;
import interfaz.VentanaLogin;

public class OperacionesSocketsSinBD {
	
	public void actualizaVisitadoMesa(String idMesa, boolean visitada){
		ClienteFichero.enviaMesaVisitada(idMesa, visitada);
	}
	
	public void actualizaVisitadoMesaLLegadaExterna(String idMesa, boolean visitada){
		VentanaLogin.getRestaurante().actualizaMesaEstaVisitadaLLegadaExterna(idMesa, visitada);
		VentanaLogin.getRestaurante().refrescaVentanaMesas();
	}

	public void actualizaVisitadoMesaCobrar(String idMesa) {
		ClienteFichero.enviaMesaVisitadaCobrar(idMesa);
	}
	
	public void actualizaVisitadoMesaCobrarLLegadaExterna(String idMesa){
		VentanaLogin.getRestaurante().actualizaMesaEstaVisitadaCobrarLLegadaExterna(idMesa);
		VentanaLogin.getRestaurante().refrescaVentanaMesas();
	}

	public void operacionCobrarMesa(String idMesa) {
		
		//Decimos a los demas tpv que tienen que cerrar la mesa.
		Operaciones operacionSQlite = new Operaciones("MesasRestaurante.db");
		
		String ficheroMesasRestaurante = "MesasRestaurante.db";
		String consultaMesasRestaurante = "UPDATE mesasRestaurante SET idCamarero ='" + "-" + "',"+
																	   "estadoMesa='" + 0 + "', "+
																	   "numeroPersonas='" + 0 + "' "+
																	   "where idMesa='" + idMesa + "'";
		operacionSQlite.insertar(consultaMesasRestaurante, false);
		operacionSQlite.cerrarBaseDeDatos();
		
		operacionSQlite = new Operaciones("InfoMesas.db");
		
		String ficheroInfoMesas = "InfoMesas.db";
		FechaYHora dia = new FechaYHora();
		String consultaInfoMesas = "delete from infoMesas where dia='" + dia.getDia() + "' and "+
																"idMesa='" + idMesa +"'";
		operacionSQlite.insertar(consultaInfoMesas, false);
		operacionSQlite.cerrarBaseDeDatos();
		
		ClienteFichero.enviaCobrarMesa(idMesa, ficheroMesasRestaurante, consultaMesasRestaurante, ficheroInfoMesas, consultaInfoMesas);
	}
	
	public void operacionCobrarMesaLLegadaExterna(String idMesa, String ficheroMesasRestaurante, 
												  String consultaMesasRestaurante, String ficheroInfoMesas, 
												  String consultaInfoMesas){
		
		//Decimos a los demas tpv que tienen que cerrar la mesa.
		Operaciones operacionSQlite = new Operaciones(ficheroMesasRestaurante);
		operacionSQlite.insertar(consultaMesasRestaurante, false);
		operacionSQlite.cerrarBaseDeDatos();
		
		//Eliminamos los platos de la base de datos en local.
		operacionSQlite = new Operaciones(ficheroInfoMesas);
		operacionSQlite.insertar(consultaInfoMesas, false);
		operacionSQlite.cerrarBaseDeDatos();
		
		//Eliminamos todas las visitas de esta mesa.
		VentanaLogin.getRestaurante().eliminaProductosDeMesa(idMesa);
		VentanaLogin.getRestaurante().actualizaMesaEstaVisitadaCobrarLLegadaExterna(idMesa);
		VentanaLogin.getRestaurante().actualizaMesaLLegadaExtarna(idMesa, "-", 0, 0);
		VentanaLogin.getRestaurante().cerrarInterfazPlatosSiAbierta(idMesa, 0);
		VentanaLogin.getRestaurante().refrescaVentanaMesas();
		
	}
	
	public void actualizarMesaBD(String idMesa, String idCamarero,int numeroPersonas, int estado) {
		
		String fichero = "MesasRestaurante.db";
		String consulta = "UPDATE mesasRestaurante SET idCamarero ='" + idCamarero + "',"+
														"estadoMesa='" + estado + "', "+
														"numeroPersonas='" + numeroPersonas + "' "+
														"where idMesa='" + idMesa + "'";
		
		// Enviamos el estado de la mesa al servidor y los clientes
		ClienteFichero.enviaEstadoMesa(idMesa, idCamarero, numeroPersonas, estado, fichero, consulta);
				
	}
	
	public void actualizarMesaBDLLegadaExterna(String consulta, String idMesa, String idCamarero,int numeroPersonas, int estado) {
		
		VentanaLogin.getRestaurante().actualizaMesaLLegadaExtarna(idMesa, idCamarero, numeroPersonas, estado);
		VentanaLogin.getRestaurante().cerrarInterfazPlatosSiAbierta(idMesa, estado);
		VentanaLogin.getRestaurante().refrescaVentanaMesas();
		
	}
	
	public void introducirComandaBD(ArrayList<String> arrayConsultas) {
		
		// Enviamos el array de consultas al servidor y a los otros clientes
        ClienteFichero.enviaArrayConsultas("InfoMesas.db", arrayConsultas);
        
	}
	
	public void introducirComandaBDLLegadaExterna(ArrayList<String> arrayConsultas){
		
		//Cargamos los platos en la mesa del restaurante parseando las consultas en los metodos internos
		Iterator<String> itConsultas = arrayConsultas.iterator();
		while(itConsultas.hasNext()){
			String consulta = itConsultas.next();
			VentanaLogin.getRestaurante().cargarConsultaARestaurante(consulta);
		}
		
		VentanaLogin.getRestaurante().refrescaInterfazPlatos();
		VentanaLogin.getRestaurante().refrescaVentanaMesas();
	}

}
