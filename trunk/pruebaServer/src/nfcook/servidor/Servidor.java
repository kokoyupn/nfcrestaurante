//Server.java
//© Usman Saleem, 2002 and beyond.
//usman_saleem@yahoo.com

package nfcook.servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Observer;
import java.util.Vector;
import java.util.Observable;
import java.io.*;


public class Servidor implements Observer {
	
	/**
	 * Clase encargada de crear el hilo para establecer la conexion por cada cliente que se
	 * conecte al servidor
	 * 
	 * @author Abel
	 *
	 */
	private class StartServerThread extends Thread {
        private boolean escucha;

        public StartServerThread() {
            escucha = false;
        }

        @SuppressWarnings("unchecked")
		public void run() {
            escucha = true;
            try{
            	Servidor.this.servidor = new ServerSocket(Servidor.this.port);
            	
            	while (escucha){
            		// Espera a que se conecte el cliente
            		Servidor.this.cliente = Servidor.this.servidor.accept();
                    System.out.println("Conectado cliente con IP: " + Servidor.this.cliente.getInetAddress());
                    try{
                    	// Creamos un hilo para el cliente nuevo, lo anyadimos a los clientes y lo iniciamos
                        Servidor.this.clienteThread = new ClienteThread(Servidor.this.cliente);
                        Thread t = new Thread(Servidor.this.clienteThread);
                        Servidor.this.clienteThread.addObserver(Servidor.this);
                        Servidor.this.clientes.addElement(Servidor.this.clienteThread);
                        t.start();
                    } catch (IOException ioe) {
                      
                    }
                }
            } catch (IOException ioe) {
                this.stopServerThread();
            }
        }

        public void stopServerThread() {
            try {
                Servidor.this.servidor.close();
            }
            catch (IOException ioe) {
            }
            escucha = false;
        }
    }
    
	private Socket cliente;
    @SuppressWarnings("rawtypes")
	private Vector clientes;
    private ServerSocket servidor;  //Server Socket
    private StartServerThread sst; //inner class

    private ClienteThread clienteThread;

    /** Port number of Server. */
    private int port;
    private boolean escuchando;

    @SuppressWarnings("rawtypes")
	public Servidor() {
        clientes = new Vector();
        port = 5000;
        escuchando = false;
    }

    public void startServer() {
        if (!escuchando) {
            sst = new StartServerThread();
            sst.start();
            escuchando = true;
        }
    }

    @SuppressWarnings("rawtypes")
	public void stopServer() {
        if (this.escuchando) {
            this.sst.stopServerThread();
            
            // Cerramos la conexion con todos los clientes
            Iterator it = clientes.iterator();
            while(it.hasNext()){
            	ClienteThread clienteThread = (ClienteThread) it.next();
            	clienteThread.stopClient();
            }
            escuchando = false;
        }
    }


    /**
     * Se encarga de actualizar todos los clientes con el cambio que se ha producido, el cual se le
     * acaba de notificar al servidor.
     */
    @SuppressWarnings("rawtypes")
	public void update(Observable observable, Object object) {
    	if(!cliente.isClosed()){
	    	Iterator it = clientes.iterator();
	    	ClienteThread clienteThread;
	    	while(it.hasNext()){
	    		clienteThread = (ClienteThread) it.next();
	    		if(!clienteThread.equals(observable)){
	    			try {
	    				clienteThread.enviaMensaje((String) object);
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}
	    	}
    	}else{
    		// Si se ha cerrado la conexión con el cliente, lo eliminamos
    		clientes.removeElement(observable);
    	}
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
  
	//testing Client//
	public static void main(String[] argv)throws IOException {
		Servidor s = new Servidor();
	    s.startServer();
	}
}