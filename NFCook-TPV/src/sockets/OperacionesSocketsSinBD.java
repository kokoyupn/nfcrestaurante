package sockets;

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

}
