//ClientThread.java
//© Usman Saleem, 2002 and Beyond
//usman_saleem@yahoo.com

package nfcook.servidor;

import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Observable;

public class ClienteThread extends Observable implements Runnable {
	
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;
    private boolean funcionando;

    public ClienteThread(Socket socket) throws IOException{
        this.socket = socket;
        funcionando = false;
        
        try{
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            funcionando = true;
        }
        catch (IOException ioe){
            throw ioe;
        }
    }
	
    // Cerramos la conexion con el cliente
    public void stopClient(){
        try{
        	this.socket.close();
        }catch(IOException ioe){
        	
        };
    }

    public void run(){
        Object msg = "";

        try {
        	oos.writeObject("Conexión establecida con el servidor");
		} catch (IOException e) {
			e.printStackTrace();
		}
        try{
        	// El cliente comienza a escuchar al servidor
            while ((msg = ois.readObject()) != null && funcionando){
            	// Procesamos la peticion del cliente
            	procesarPeticion(msg);
                System.out.println("Peticion: " + msg);
            }
            
            // El cliente no tiene va a realizar mas peticiones
            funcionando = false;
        }catch (IOException ioe) {
        	funcionando = false;
        }catch (ClassNotFoundException e){
        	e.printStackTrace();
		}
        
        // Cerramos la conexion
        try{
            socket.close();
            System.out.println("Cerrada conexion con el cliente con IP: " + socket.getInetAddress());
        }catch (IOException ioe){
        	
        }

        // Notificamos que un cliente, ha dejado de serlo
        this.setChanged();
        this.notifyObservers(this);
    }
    
    /**
     * Metodo encargadao de mandar un mensaje al cliente
     * 
     * @param msg
     * @throws IOException
     */
    public void enviaMensaje(String msg) throws IOException{
    	oos.writeObject(msg);
    }
    
    public void procesarPeticion(Object msg){
    	/**
    	 * Codigo de prueba en el que mandamos un fichero al cliente cuando nos manda una peticion
    	 */
    	try {
			enviarFicheroBaseDatos("pruebaN.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // Metodo encargado de enviar el fichero de una base de datos al cliente que la ha solicitado
    public void enviarFicheroBaseDatos(String nombre) throws IOException{
    	// Variable auxiliar para indicar cual es el ultimo mensaje
    	boolean enviadoUltimo = false;
    	
    	// Se abre el fichero.
    	File f = new File(nombre);
    	FileInputStream fis = new FileInputStream(f);
    	            
    	// Se instancia y rellena un mensaje de envio de fichero
    	FicheroServidor mensaje = new FicheroServidor();
    	mensaje.nombreFichero = nombre;
    	            
    	// Se leen los primeros bytes del fichero en un campo del mensaje
    	int leidos = fis.read(mensaje.contenidoFichero);
    	            
    	// Bucle mientras se vayan leyendo datos del fichero
    	while (leidos > -1){               
    	    // Se rellena el número de bytes leidos
    	    mensaje.bytesValidos = leidos;
    	                
    	    // Si no se han leido el máximo de bytes, es porque el fichero
    	    // se ha acabado y este es el último mensaje
    	    if (leidos < FicheroServidor.LONGITUD_MAXIMA){
    	         // Se marca que este es el último mensaje
    	        mensaje.ultimoMensaje = true;
    	        enviadoUltimo=true; 
    	    }else{
    	        mensaje.ultimoMensaje = false;
    	    }
    	    
    	    // Se envía por el socket   
    	    oos.writeObject(mensaje);
    	                
    	    // Si es el último mensaje, salimos del bucle.
    	    if (mensaje.ultimoMensaje){
    	    	break;
    	    }
    	    
    	    // Se crea un nuevo mensaje
    	    mensaje = new FicheroServidor();
    	    mensaje.nombreFichero = nombre;
    	                
    	    // y se leen sus bytes.
    	    leidos = fis.read(mensaje.contenidoFichero);
    	}
    	            
    	// En caso de que el fichero tenga justo un múltiplo de bytes de MensajeTomaFichero.LONGITUD_MAXIMA,
    	// no se habrá enviado el mensaje marcado como último. Lo hacemos ahora.
    	if (enviadoUltimo == false){
    	    mensaje.ultimoMensaje = true;
    	    mensaje.bytesValidos = 0;
    	    oos.writeObject(mensaje);
    	}
    	
    	// Cerramos el fichero
    	fis.close();
    }
}