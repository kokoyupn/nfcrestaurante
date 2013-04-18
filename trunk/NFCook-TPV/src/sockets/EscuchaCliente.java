package sockets;


import java.io.ObjectInputStream;
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
	            ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
	            MensajeConsulta mensaje = (MensajeConsulta) ois.readObject();
				System.out.println(mensaje.sql);
	            
				// ejecutamos la consulta de insercion en la base de datos
				Operaciones operacion = new Operaciones(mensaje.nombreFichero);
	            operacion.insertar(mensaje.sql, false); // false para que no se vuelva a enviar por socket
	            
	            // cerramos los sockets
	            cliente.close();
	            servidor.close();
			}
            
		} catch (Exception e) {
			System.err.println("Error al escuchar al servidor desde el cliente");
		}
	}
	
}
