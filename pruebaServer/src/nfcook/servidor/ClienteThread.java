package nfcook.servidor;

import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Observable;

import nfcook.mensajes.Mensaje;
import nfcook.mensajes.MensajeFichero;

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
            	if(msg instanceof Mensaje)
            		procesarPeticion((Mensaje) msg);
            	else
            		System.err.println("Mensaje del cliente no esperado " + msg.getClass().getName());
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
    
    public void procesarPeticion(Mensaje msg){
    	/**
    	 * Codigo de prueba en el que mandamos un fichero al cliente cuando nos manda una peticion
    	 */
    	if (msg instanceof MensajeFichero){
    		try {
    			enviarFicheroBaseDatos((MensajeFichero) msg);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	// Si es de tipo String se muestra por pantalla
        //else if (msg instanceof String) 
        	//System.out.println("Server: " + msg.toString());                    
    	// Si no es del tipo esperado, se marca error y se termina el bucle
    	else 
    		System.err.println("Mensaje del cliente no esperado " + msg.getClass().getName());
    }
    
    // Metodo encargado de enviar el fichero de una base de datos al cliente que la ha solicitado
    public void enviarFicheroBaseDatos(MensajeFichero mensaje) throws IOException{
    	// Variable auxiliar para indicar cual es el ultimo mensaje
    	boolean enviadoUltimo = false;
    	
    	// Se abre el fichero.
    	File f = new File(mensaje.getRutaFichero() + mensaje.getNombreFichero());
    	FileInputStream fis = new FileInputStream(f);
    	            
    	// Se instancia y rellena un mensaje de envio de fichero
    	MensajeFichero mensajeEnviar = new MensajeFichero(mensaje.getNombreFichero(), mensaje.getRutaFichero());
    	            
    	// Se leen los primeros bytes del fichero en un campo del mensaje
    	int leidos = fis.read(mensajeEnviar.getContenidoFichero());
    	            
    	// Bucle mientras se vayan leyendo datos del fichero
    	while (leidos > -1){               
    	    // Se rellena el número de bytes leidos
    		mensajeEnviar.setBytesValidos(leidos);
    	                
    	    // Si no se han leido el máximo de bytes, es porque el fichero
    	    // se ha acabado y este es el último mensaje
    	    if (leidos < MensajeFichero.getLongitudMaxima()){
    	         // Se marca que este es el último mensaje
    	    	mensajeEnviar.setUltimoMensaje(true);
    	        enviadoUltimo=true; 
    	    }else{
    	    	mensajeEnviar.setUltimoMensaje(false);
    	    }
    	    
    	    // Se envía por el socket   
    	    oos.writeObject(mensajeEnviar);
    	                
    	    // Si es el último mensaje, salimos del bucle.
    	    if (mensajeEnviar.isUltimoMensaje()){
    	    	break;
    	    }
    	    
    	    // Se crea un nuevo mensaje
    	    mensajeEnviar = new MensajeFichero(mensaje.getNombreFichero(), mensaje.getRutaFichero());
    	                
    	    // y se leen sus bytes.
    	    leidos = fis.read(mensajeEnviar.getContenidoFichero());
    	}
    	            
    	// En caso de que el fichero tenga justo un múltiplo de bytes de MensajeTomaFichero.LONGITUD_MAXIMA,
    	// no se habrá enviado el mensaje marcado como último. Lo hacemos ahora.
    	if (enviadoUltimo == false){
    		mensajeEnviar.setUltimoMensaje(true);
    		mensajeEnviar.setBytesValidos(0);
    	    oos.writeObject(mensaje);
    	}
    	
    	// Cerramos el fichero
    	fis.close();
    }
}