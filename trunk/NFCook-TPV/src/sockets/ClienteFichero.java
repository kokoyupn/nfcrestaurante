package sockets;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Pide un fichero al ServidorFichero, lo escribe en pantalla cuando lo recibe y
 * lo guarda en disco.
 * También permite enviar consultas al servidor y a los otros tpv
 * 
 * 
 */
public class ClienteFichero
{
	private static InetAddress hostLocal;
	private final static int puerto = 5000;
	private final static String servidor = "192.168.1.54";

    /**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia la consulta sql
	 * junto con el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaConsulta(String fichero, String sql){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            MensajeConsulta mensajeConsulta = new MensajeConsulta();
           	mensajeConsulta.nombreFichero = fichero;
           	mensajeConsulta.sql = sql;
           	mensajeConsulta.ips = new ArrayList<InetAddress>();
           	mensajeConsulta.ips.add(socket.getLocalAddress());
           
            oos.writeObject(mensajeConsulta);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MensajeConsulta mensajeConIPs = (MensajeConsulta) ois.readObject(); // mensajeConIPs tiene la misma info que mensajeConsulta
            transmiteConsultasLocal(mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar consulta al Servidor");
		}
		
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia el array con consultas sql
	 * junto con el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaArrayConsultas(String fichero, ArrayList<String> consultas){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            MensajeArrayConsultas mensajeArrayConsulta = new MensajeArrayConsultas();
            mensajeArrayConsulta.nombreFichero = fichero;
            mensajeArrayConsulta.consultas = consultas;
            mensajeArrayConsulta.ips = new ArrayList<InetAddress>();
            mensajeArrayConsulta.ips.add(socket.getLocalAddress());
           
            oos.writeObject(mensajeArrayConsulta);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            MensajeArrayConsultas mensajeConIPs = (MensajeArrayConsultas) ois.readObject(); // mensajeConIPs tiene la misma info que mensajeConsulta
            transmiteConsultasLocal(mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar consulta al Servidor");
		}
		
	}
	
	private static void transmiteConsultasLocal(MensajeConsulta mensaje){
		// recorremos todos los clientes salvo el actual para enviarles la consulta sql
    	try {
    		int i = 0;
    		while(i<mensaje.ips.size()){
    			if(!mensaje.ips.get(i).equals(hostLocal)){

    				Socket socket = new Socket(mensaje.ips.get(i), 5002);
    		    	ObjectOutputStream oos;
    				System.out.println("Aceptado servidor");
    				oos = new ObjectOutputStream(socket.getOutputStream());
    				 
    		        MensajeConsulta mensajeConsulta = new MensajeConsulta();
    		       	mensajeConsulta.nombreFichero = mensaje.nombreFichero;
    		       	mensajeConsulta.sql = mensaje.sql;
    		       
    		        oos.writeObject(mensajeConsulta);

    		        // cerramos el socket
    				socket.close();
    				oos.close();

    			}
    			i++;
    			}
    		
    	}catch (IOException e) {
			System.err.println("Error al enviar las consultas a los Clientes");
    		//e.printStackTrace();
    	}
		
	}
	
	private static void transmiteConsultasLocal(MensajeArrayConsultas mensaje){
		// recorremos todos los clientes salvo el actual para enviarles el array con las consultas sql
    	try {
    		int i = 0;
    		while(i<mensaje.ips.size()){
    			if(!mensaje.ips.get(i).equals(hostLocal)){

    				Socket socket = new Socket(mensaje.ips.get(i), 5002);
    		    	ObjectOutputStream oos;
    				System.out.println("Aceptado servidor");
    				oos = new ObjectOutputStream(socket.getOutputStream());
    				 
    				MensajeArrayConsultas mensajeArrayConsulta = new MensajeArrayConsultas();
    				mensajeArrayConsulta.nombreFichero = mensaje.nombreFichero;
    				mensajeArrayConsulta.consultas = mensaje.consultas;
    				
    		        oos.writeObject(mensajeArrayConsulta);

    		        // cerramos el socket
    				socket.close();
    				oos.close();

    			}
    			i++;
    			}
    		
    	}catch (IOException e) {
			System.err.println("Error al enviar las consultas a los Clientes");
    		//e.printStackTrace();
    	}
		
	}
    /**
     * Establece comunicación con el servidor en el puerto indicado. Pide el
     * fichero. Cuando llega, lo escribe en pantalla y en disco duro.
     * 
     * @param fichero
     *            path completo del fichero que se quiere
     * @param servidor
     *            host donde está el servidor
     * @param puerto
     *            Puerto de conexión
     */
    public static void pide(String fichero)
    {
        try
        {
            // Se abre el socket.
            Socket socket = new Socket(servidor, puerto);
           
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            MensajeDameFichero mensaje = new MensajeDameFichero();
            mensaje.nombreFichero = fichero;
        	mensaje.ips = new ArrayList<InetAddress>();
           	mensaje.ips.add(socket.getLocalAddress());
            String ruta = "BasesDeDatosTPV/" + mensaje.nombreFichero;
            
            oos.writeObject((Object)mensaje);

            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(ruta);

            // Se crea un ObjectInputStream del socket para leer los mensajes
            // que contienen el fichero.
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            MensajeTomaFichero mensajeRecibido;
            Object mensajeAux;
            do
            {
                // Se lee el mensaje en una variabla auxiliar
                mensajeAux = ois.readObject();
            	
                // Si es del tipo esperado, se trata
                if (mensajeAux instanceof MensajeTomaFichero)
                {
                    mensajeRecibido = (MensajeTomaFichero) mensajeAux;
                    // Se escribe en pantalla y en el fichero
                    System.out.print(new String(
                            mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos));
                    fos.write(mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos);
                } else
                {
                    // Si no es del tipo esperado, se marca error y se termina
                    // el bucle
                    System.err.println("Mensaje no esperado "
                            + mensajeAux.getClass().getName());
                    break;
                }
            } while (!mensajeRecibido.ultimoMensaje);
            
            // Se cierra socket y fichero
            fos.close();
            ois.close();            
            oos.close();
            socket.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    
    /**
     * Metodo que envia la IP del TPV para eliminarlo del servidor al cerrarse el programa.
     * Envia un objeto de tipo MensajeConsulta pero con el nombre de fichero vacio, 
     * para indicar que es para el cierre del TPV.
     * **/
    public static void enviaIPtpv(){
    	
    	try{
	    	// Se abre el socket.
	        Socket socket = new Socket(servidor, puerto);
	       
	        // Se envía un mensaje de petición de fichero.
	        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        MensajeConsulta mensaje = new MensajeConsulta();
	    	mensaje.ips = new ArrayList<InetAddress>();
	       	mensaje.ips.add(socket.getLocalAddress());
	        
	        oos.writeObject((Object)mensaje);
	        
	        //cerramos el socket
	        socket.close();
	        oos.close();
        
    	}catch(Exception exceptionCierreTPV){
    		System.err.println("Fallo al enviar el cierre del TPV");
    	}

    }
    

}
