package sockets;

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
		
		//Dejamos de tener visitada esta mesa. TODO en el llegada externa
//		OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
//		operacion.actualizaVisitadoMesaCobrar(idMesa);
		
		operacionSQlite = new Operaciones("InfoMesas.db");
		
		String ficheroInfoMesas = "InfoMesas.db";
		FechaYHora dia = new FechaYHora();
		String consultaInfoMesas = "delete from infoMesas where dia='" + dia.getDia() + "' and "+
																"idMesa='" + idMesa +"'";
		operacionSQlite.insertar(consultaInfoMesas, false);
		operacionSQlite.cerrarBaseDeDatos();
		
		ClienteFichero.enviaCobrarMesa(idMesa, ficheroMesasRestaurante, consultaMesasRestaurante, ficheroInfoMesas, consultaInfoMesas);
	}

}
