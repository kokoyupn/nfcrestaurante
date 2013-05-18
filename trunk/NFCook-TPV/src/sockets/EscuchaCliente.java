package sockets;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import basesDeDatos.Operaciones;

public class EscuchaCliente extends Thread {

	private int puerto = 5002;
	/**
	 * En este metodo ejecutaremos la escucha del cliente, haciendolo en modo Servidor con ServerSocket
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
				System.out.println(cliente.getInetAddress());
	            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
	            Object mensaje = ois.readObject();
	            ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
	            
	            if (mensaje instanceof MensajeConsulta){
	            	// ejecutamos la consulta de insercion en la base de datos
	            	Operaciones operacion = new Operaciones(((MensajeConsulta) mensaje).nombreFichero);
					operacion.insertar(((MensajeConsulta) mensaje).sql, false);
	            	System.out.println(((MensajeConsulta) mensaje).sql);

	            }else if (mensaje instanceof MensajeArrayConsultas){
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.introducirComandaBDLLegadaExterna(((MensajeArrayConsultas) mensaje).consultas);
	            
	            }else if (mensaje instanceof MensajeEstadoMesa){
	            	MensajeEstadoMesa mensajeUtilizar = (MensajeEstadoMesa)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.actualizarMesaBDLLegadaExterna(mensajeUtilizar.sql, mensajeUtilizar.idMesa, mensajeUtilizar.idCamarero, mensajeUtilizar.numPersonas, mensajeUtilizar.estado);
	            
	            }else if (mensaje instanceof MensajeMesaVisitada){
	            	MensajeMesaVisitada mensajeUtilizar = (MensajeMesaVisitada)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.actualizaVisitadoMesaLLegadaExterna(mensajeUtilizar.idMesa, mensajeUtilizar.visitado);
	            
	            }else if (mensaje instanceof MensajeMesaVisitadaCobrar){
	            	MensajeMesaVisitadaCobrar mensajeUtilizar = (MensajeMesaVisitadaCobrar)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.actualizaVisitadoMesaCobrarLLegadaExterna(mensajeUtilizar.idMesa);
	            
	            }else if (mensaje instanceof MensajeCobrarMesa){
	            	MensajeCobrarMesa mensajeUtilizar = (MensajeCobrarMesa)mensaje;
	            	OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
	            	operacion.operacionCobrarMesaLLegadaExterna(mensajeUtilizar.idMesa, mensajeUtilizar.nombreFichero, mensajeUtilizar.sql, mensajeUtilizar.nombreFichero2, mensajeUtilizar.sql2);
	            
	            }
            	oos.writeObject(null);

	           
	            ois.close();
	            oos.close();
	            
	            // cerramos los sockets
            	cliente.close();
	            servidor.close();
			}
            
		} catch (Exception e) {
			System.err.println("Error al escuchar al servidor desde el cliente");
			e.printStackTrace();
		}
	}
	
}
