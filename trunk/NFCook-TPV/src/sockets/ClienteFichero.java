package sockets;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
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
	private final static int puertoClientes = 5002;
	private final static String servidor = "nfcook.no-ip.org";


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
           	ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
           	ips.add(socket.getLocalAddress());
            MensajeConsulta mensajeConsulta = new MensajeConsulta(fichero, sql, ips);

            oos.writeObject(mensajeConsulta);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
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
            ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
            ips.add(socket.getLocalAddress());
            MensajeArrayConsultas mensajeArrayConsulta = new MensajeArrayConsultas(fichero, consultas, ips);
       
           
            oos.writeObject(mensajeArrayConsulta);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar array consultas al Servidor");
		}
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia el estado de una mesa junto con 
	 * la consulta sql y el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaEstadoMesa(String idMesa, String idCamarero, int numPersonas, int estado, String fichero, String sql){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
            ips.add(socket.getLocalAddress());
            MensajeEstadoMesa mensajeEstadoMesa = new MensajeEstadoMesa(fichero, sql, idMesa, idCamarero, numPersonas, estado, ips);
            
            oos.writeObject(mensajeEstadoMesa);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar el estado de una mesa al Servidor");
		}
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia el array con consultas sql
	 * junto con el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaMesaVisitada(String idMesa, boolean visitada){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
            ips.add(socket.getLocalAddress());
            MensajeMesaVisitada mensajeMesaVisitada = new MensajeMesaVisitada(idMesa, visitada, ips);
           
            oos.writeObject(mensajeMesaVisitada);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar mesa visitada al Servidor");
		}
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia el array con consultas sql
	 * junto con el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaMesaVisitadaCobrar(String idMesa){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
            ips.add(socket.getLocalAddress());
            MensajeMesaVisitadaCobrar mensajeMesaVisitada = new MensajeMesaVisitadaCobrar(idMesa, ips);
           
            oos.writeObject(mensajeMesaVisitada);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar mesa visitada cobrar al Servidor");
		}
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Envia el id de la mesa y la consulta sql
	 * junto con el fichero que habra que actualizar en el Servidor.
	**/
	public static void enviaConsultaEliminaPlatos(String idMesa, String fichero, String sql){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
           	ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
           	ips.add(socket.getLocalAddress());
           	MensajeConsultaEliminaPlatos mensajeConsultaElimina = new MensajeConsultaEliminaPlatos(idMesa, fichero, sql, ips);

            oos.writeObject(mensajeConsultaElimina);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar consulta elimina platos al Servidor");
		}
		
	}
	
	/**
	 * Establece comunicacion con el servidor en el puerto indicado. Enviara el mensaje correspondiente a cobrar una mesa
	 * junto con el id de la mesa, las consultas sql y los ficheros que habra que actualizar en el Servidor.
	**/
	public static void enviaCobrarMesa(String idMesa, String fichero, String sql, String fichero2, String sql2){

		try{
			// Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            hostLocal = socket.getLocalAddress();
            
            // Se envía un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
           	ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
           	ips.add(socket.getLocalAddress());
           	MensajeCobrarMesa mensajeCobrarMesa = new MensajeCobrarMesa(idMesa, fichero, sql, fichero2, sql2, ips);

            oos.writeObject(mensajeCobrarMesa);
            
            // recibir las IP internas de todos los clientes y enviar esta misma info
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object mensajeConIPs = ois.readObject();
            transmiteLocal((Mensaje)mensajeConIPs);
            
            // cerramos el socket
            socket.close();
            oos.close();
            ois.close();
            
		}catch(Exception excepcionEnviaConsulta){
			System.err.println("Fallo al enviar consulta cobrar mesa al Servidor");
		}
	}

	
	private static void transmiteLocal(Mensaje mensaje){
		// recorremos todos los clientes salvo el actual para enviarles la consulta sql
    	try {
    		int i = 0;
    		while(i<mensaje.ips.size()){
    			if(!mensaje.ips.get(i).equals(hostLocal)){

    				Socket socket = new Socket(mensaje.ips.get(i), puertoClientes);
    		    	ObjectOutputStream oos;
    				System.out.println("Aceptado servidor");
    				oos = new ObjectOutputStream(socket.getOutputStream());
    				 
    				oos.writeObject(mensaje);
    		    
    		        // cerramos el socket
    				socket.close();
    				oos.close();

    			}
    			i++;
    			}
    		
    	}catch (IOException e) {
			System.err.println("Error al transmitir a los Clientes");
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
            ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
           	ips.add(socket.getLocalAddress());
            MensajeDameFichero mensaje = new MensajeDameFichero(fichero, ips);
        	
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
           
        } catch (SocketException e){
        	System.err.println("Fallo al pedir el fichero("+fichero+")al servidor, reintentando...");
        	pide(fichero);
        
        }catch (Exception e){
        	System.err.println("Fallo al pedir el fichero("+fichero+")al servidor");
        
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
	        ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
	       	ips.add(socket.getLocalAddress());
	        MensajeConsulta mensaje = new MensajeConsulta("", "", ips);
	        
	        oos.writeObject((Object)mensaje);
	        
	        //cerramos el socket
	        socket.close();
	        oos.close();
        
    	}catch(Exception exceptionCierreTPV){
    		System.err.println("Fallo al enviar el cierre del TPV");
    	}

    }
    

}
