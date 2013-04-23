package sockets;


import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import basesDeDatos.Operaciones;

public class EscuchaCliente2 extends Thread {

	private int puerto = 5002;
	/**
	 * En este metodo ejecutaremos la escucha secundaria del cliente, haciendolo en modo Servidor con ServerSocket
	 * 
	 */
	public void run(){
		
		try {
			while (true){ // para que siempre este escuchando
			
				System.out.println("Soy un Servidor");
				ServerSocket servidor = new ServerSocket(puerto);
				servidor.setReuseAddress(true);

				// se espera a un cliente. En este caso esperamos a que el servidor contacte con este cliente
				Socket cliente = servidor.accept();
				// Se lee el mensaje con la consulta a utilizar
	            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
	            Object mensaje = ois.readObject();
	            
	            if (mensaje instanceof MensajeConsulta){
	            	// ejecutamos la consulta de insercion en la base de datos
	            	Operaciones operacion = new Operaciones(((MensajeConsulta) mensaje).nombreFichero);
		            operacion.insertar(((MensajeConsulta) mensaje).sql, false); // false para que no se vuelva a enviar por socket
					System.out.println(((MensajeConsulta) mensaje).sql);

	            }else if (mensaje instanceof MensajeArrayConsultas){
	            	Operaciones operacion = new Operaciones(((MensajeArrayConsultas) mensaje).nombreFichero);
	            	operacion.introducirComandaBDLLegadaExterna(((MensajeArrayConsultas) mensaje).consultas);
	            
	            }else if (mensaje instanceof MensajeEstadoMesa){
	            	MensajeEstadoMesa mensajeUtilizar = (MensajeEstadoMesa)mensaje;
	            	Operaciones operacion = new Operaciones(mensajeUtilizar.nombreFichero);
	            	operacion.actualizarMesaBDLLegadaExterna(mensajeUtilizar.sql, mensajeUtilizar.idMesa, mensajeUtilizar.idCamarero, mensajeUtilizar.numPersonas, mensajeUtilizar.estado);
	            
	            }else if (mensaje instanceof MensajeMesaVisitada){
	            	MensajeMesaVisitada mensajeUtilizar = (MensajeMesaVisitada)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.actualizaVisitadoMesaLLegadaExterna(mensajeUtilizar.idMesa, mensajeUtilizar.visitado);
	            
	            }else if (mensaje instanceof MensajeMesaVisitadaCobrar){
	            	MensajeMesaVisitadaCobrar mensajeUtilizar = (MensajeMesaVisitadaCobrar)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.actualizaVisitadoMesaCobrarLLegadaExterna(mensajeUtilizar.idMesa);
	            
	            }else if (mensaje instanceof MensajeConsultaEliminaPlatos){
	            	MensajeConsultaEliminaPlatos mensajeUtilizar = (MensajeConsultaEliminaPlatos)mensaje;
	            	Operaciones operacion = new Operaciones(mensajeUtilizar.nombreFichero);
	            	operacion.eliminarPlatosDeMesaLLegadaExterna(mensajeUtilizar.idMesa, mensajeUtilizar.sql);
	            }
			
	            // cerramos los sockets
	            cliente.close();
	            servidor.close();
			}
            
		} catch (Exception e) {
			System.err.println("Error al escuchar al servidor desde el cliente");
		}
	}
	
}
