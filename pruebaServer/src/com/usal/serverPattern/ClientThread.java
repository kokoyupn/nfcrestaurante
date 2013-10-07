//ClientThread.java
//© Usman Saleem, 2002 and Beyond
//usman_saleem@yahoo.com

package com.usal.serverPattern;

import java.net.Socket;
import java.io.ObjectInputStream;
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
    private PrintWriter pw;

    /** Socket object representing client connection */

    private Socket socket;
    private boolean running;
    
    private String mensaje;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        running = false;
        //get I/O from socket
        try {
            br = new ObjectInputStream(socket.getInputStream());
            
            pw = new PrintWriter(socket.getOutputStream(), true);
            running = true; //set status
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }
	
    public void sendMessage(Object msg) throws IOException
    {
		pw.println(msg);
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
        Object msg; //will hold message sent from client

	pw.println("Welcome to Java based Server");
		

	  //start listening message from client//

        try {
                while ((msg = br.readObject()) != null && running) {
                    //provide your server's logic here//
			
                    //right now it is acting as an ECHO server//

                    pw.println("HOLA"); //echo msg back to client//
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
}