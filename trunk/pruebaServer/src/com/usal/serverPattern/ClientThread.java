//ClientThread.java
//© Usman Saleem, 2002 and Beyond
//usman_saleem@yahoo.com

package com.usal.serverPattern;

import java.net.Socket;
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
                	 //notify the observers for cleanup etc.
                    this.setChanged();              //inherit from Observable
                    this.notifyObservers(msg);     //inherit from Observable
                    
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