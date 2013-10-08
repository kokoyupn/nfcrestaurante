package nfcook.cliente;

import java.net.Socket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Observable;

import nfcook.mensajes.Mensaje;
import nfcook.mensajes.MensajeFichero;

public class Cliente extends Observable implements Runnable {
	
	private static Cliente cliente = null; 
    private Socket socket;
    private ObjectInputStream ois; 
    private ObjectOutputStream oos;

    private boolean conectado; // estado del cliente
    private int port=5000; 
    private String hostName="192.168.200.119";

    private Cliente() {
    	conectado = false;
    }
    
    public static Cliente getInstancia(){
    	if(cliente == null){
    		cliente  = new Cliente();
    	}
    	return cliente;
    }

    /**
     * Conexion con el servidor
     */
    public void conectar(String hostName, int port) throws IOException {
    	if(!conectado) {
    		this.hostName = hostName;
    		this.port = port;
            socket = new Socket(hostName,port);                
            //Creamos una conenexion para enviar objetos al servidor
            oos = new ObjectOutputStream(socket.getOutputStream());
            //Creamos un buffer para objetos del servidor
            ois = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            // inicia la lectura desde el servidor
            Thread t = new Thread(this);
            // llama al metodo run de esta clase     
            t.start(); 
        }
    }


    public void enviarMensaje(Mensaje msg) throws IOException{
		if(conectado) {
	        oos.writeObject(msg);
        } else throw new IOException("No estas conectado al servidor");
    }
    
    /**
     * Cerramos la conexion del cliente con el servidor
     */
    public void desconectar() {
    	if(socket != null && conectado){
    		try {
    			socket.close();
          	} catch(IOException ioe) { }
            finally {
            	this.conectado = false;
          	}
        }
    }

    /**
     * Se encarga de procesar el objeto recibido del servidor. Este puede ser un 
     * fichero con una base de datos, un mensaje tipo string o un error.
     */
    public void run() {
	   Object msg;
         try {
            while(conectado && (msg = ois.readObject())!= null){            	
            	// Si es del tipo fichero recibe el fichero
            	if (msg instanceof MensajeFichero) 
            		recibirFicheroBaseDatos((MensajeFichero) msg);	
            	// Si es de tipo String se muestra por pantalla
                else if (msg instanceof String) 
                	System.out.println("Server: " + msg.toString());                    
            	// Si no es del tipo esperado, se marca error y se termina el bucle
            	else 
            		System.err.println("Mensaje no esperado " + msg.getClass().getName());
            }
         }
         catch(IOException ioe) { } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        finally { 
        	conectado = false; 
        }
    }
    
    /**
     * Metodo encargado de recibir un fichero enviado por el servidor
     */
    public void recibirFicheroBaseDatos(MensajeFichero mensaje){
        try{
            // Se envía un mensaje de petición de fichero.
            File f = new File (mensaje.getRutaFichero() + mensaje.getNombreFichero());

            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(f);

            while(!mensaje.isUltimoMensaje()){
            	fos.write(mensaje.getContenidoFichero(), 0, mensaje.getBytesValidos()); // Se escribe en el fichero
            	 if (!mensaje.isUltimoMensaje()){
            		 mensaje = (MensajeFichero) ois.readObject(); // Se lee el mensaje en una variabla auxiliar 
            	 }
            }

            fos.flush();
            fos.close();            
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    

    public boolean isConnected() {
		return conectado;
    }


    public int getPort(){
            return port;
        }

    public void setPort(int port){
            this.port = port;
        }

    public String getHostName(){
            return hostName;
        }

    public void setHostName(String hostName){
            this.hostName = hostName;
        }

	//testing Client//
    public static void main(String[] argv)throws IOException {
        /*Cliente c = new Cliente();
        c.conectar("192.168.200.119",5000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg = "";
        while(!msg.equalsIgnoreCase("quit")) {
           msg = br.readLine();
           c.enviarMensaje(msg);
        }
        c.desconectar();*/
    }
}