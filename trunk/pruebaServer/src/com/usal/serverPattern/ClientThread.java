//ClientThread.java
//© Usman Saleem, 2002 and Beyond
//usman_saleem@yahoo.com

package com.usal.serverPattern;

import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Observable;

public class ClientThread extends Observable implements Runnable {
    /** For reading input from socket */
    private ObjectInputStream br;

    /** For writing output to socket. */
    private ObjectOutputStream pw;

    /** Socket object representing client connection */

    private Socket socket;
    private boolean running;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        running = false;
        //get I/O from socket
        try {
            br = new ObjectInputStream(socket.getInputStream());
            
            pw = new ObjectOutputStream(socket.getOutputStream());
            running = true; //set status
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }
	
    /** 
     *Stops clients connection
     */

    public void stopClient()
    {
        try {
		this.socket.close();
        }catch(IOException ioe){ };
    }

    public void run() {
        Object msg = ""; //will hold message sent from client

        try {
			pw.writeObject("Bienvenido puto TOPOR");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	  //start listening message from client//

        try {
                while ((msg = br.readObject()) != null && running) {
                	// Enviamos un fichero al cliente que ha mandado el mensaje
                	// Una variable auxiliar para marcar cuando se envía el último mensaje
                	boolean enviadoUltimo = false;
                	
                	// Se abre el fichero.
                	File f = new File("pruebaN.txt");
                	FileInputStream fis = new FileInputStream(f);
                	            
                	// Se instancia y rellena un mensaje de envio de fichero
                	FicheroServidor mensaje = new FicheroServidor();
                	mensaje.nombreFichero = "pruebaN.txt";
                	            
                	// Se leen los primeros bytes del fichero en un campo del mensaje
                	int leidos = fis.read(mensaje.contenidoFichero);
                	            
                	// Bucle mientras se vayan leyendo datos del fichero
                	while (leidos > -1)
                	{               
                	    // Se rellena el número de bytes leidos
                	    mensaje.bytesValidos = leidos;
                	                
                	    // Si no se han leido el máximo de bytes, es porque el fichero
                	    // se ha acabado y este es el último mensaje
                	    if (leidos < FicheroServidor.LONGITUD_MAXIMA)
                	    {
                	         // Se marca que este es el último mensaje
                	        mensaje.ultimoMensaje = true;
                	        enviadoUltimo=true; 
                	    }
                	    else
                	        mensaje.ultimoMensaje = false;
                	                
                	    // Se envía por el socket   
                	    pw.writeObject(mensaje);
                	                
                	    // Si es el último mensaje, salimos del bucle.
                	    if (mensaje.ultimoMensaje)
                	        break;
                	                
                	    // Se crea un nuevo mensaje
                	    mensaje = new FicheroServidor();
                	    mensaje.nombreFichero = "pruebaN.txt";
                	                
                	    // y se leen sus bytes.
                	    leidos = fis.read(mensaje.contenidoFichero);
                	}
                	            
                	// En caso de que el fichero tenga justo un múltiplo de bytes de MensajeTomaFichero.LONGITUD_MAXIMA,
                	// no se habrá enviado el mensaje marcado como último. Lo hacemos ahora.
                	if (enviadoUltimo==false)
                	{
                	    mensaje.ultimoMensaje=true;
                	    mensaje.bytesValidos=0;
                	    pw.writeObject(mensaje);
                	}
                	// Se cierra el ObjectOutputStream
                	pw.close();
                	
                    //provide your server's logic here//
			
                    //right now it is acting as an ECHO server//

                    //pw.println(msg); //echo msg back to client//
                    System.out.println(msg);
                }
                running = false;
            }
            catch (IOException ioe) {
                running = false;
            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        //it's time to close the socket
        try {
            this.socket.close();
            System.out.println("Closing connection");
        } catch (IOException ioe) { }

        //notify the observers for cleanup etc.
        this.setChanged();              //inherit from Observable
        this.notifyObservers(this);     //inherit from Observable
    }
    
    public void sendMessage(String msg) throws IOException
    {
    	pw.writeObject(msg);
    }
}