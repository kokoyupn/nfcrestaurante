//Client
package com.usal.clientPattern;

import java.net.Socket;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;


public class Client extends Observable implements Runnable {

    /**
     * Uses to connect to the server 
     */
    private Socket socket;

    /**
     * For reading input from server. 
     */
    private ObjectInputStream ois; 

    /**
     * For writing output to server. 
     */
    private ObjectOutputStream oos;

    /**
     * Status of client. 
     */
    private boolean connected;

    /**
     * Port number of server
     */
     private int port=5000; //default port

    /**
     * Host Name or IP address in String form
     */
    private String hostName="192.168.200.119";//default host name

    public Client() {
		connected = false;
    }

    public void connect(String hostName, int port) throws IOException {
        if(!connected)
        {
	     this.hostName = hostName;
           this.port = port;
           socket = new Socket(hostName,port);
           //get I/O from socket
           //Creamos un buffer para recivir mensajes de texto del servidor
           //br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           //Creamos una conenexion para enviar objetos al servidor
           oos = new ObjectOutputStream(socket.getOutputStream());
           ois = new ObjectInputStream(socket.getInputStream());

		   connected = true;
           //initiate reading from server...
           Thread t = new Thread(this);
           t.start(); //will call run method of this class
        }
    }

    public void sendMessage(String msg) throws IOException
    {
		if(connected) {
	        oos.writeObject(msg);
        } else throw new IOException("Not connected to server");
    }

    public void disconnect() {
		if(socket != null && connected)
        {
          try {
			socket.close();
          }catch(IOException ioe) {
			//unable to close, nothing to do...
          }
          finally {
			this.connected = false;
          }
        }
    }

    public void run() {
	   Object msg; //holds the msg recieved from server
         try {
            while(connected && (msg = ois.readObject())!= null){
            	
            	System.out.println("Server:"+msg.toString());	
            	
            	File f = new File("pruebaN.txt");			    
			    if (f.length() > 0) {
			    	f.delete();
			        f = new File("pruebaN.txt");
			    }
			    OutputStream os = new FileOutputStream (f);
			    int n = 0;
			    byte buf[] = new byte [3000];
			    while ((n = ois.read(buf)) >= 0)  {// =-1 si no hay mas datos
			    	os.write(buf, 0, n); // server.get
			    	System.out.println ("... "+n);
			    }
			   //os.flush();
			    os.close();
			    
			 //notify observers, vuelta del echo del servidor//
			 //this.setChanged();
			 //notify+send out recieved msg to Observers
             //this.notifyObservers("Juanito");
            }
         }
         catch(IOException ioe) { } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         finally { connected = false; }
    }

    public boolean isConnected() {
		return connected;
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
        Client c = new Client();
        c.connect("192.168.200.119",5000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg = "";
        while(!msg.equalsIgnoreCase("quit"))
        {
           msg = br.readLine();
           c.sendMessage(msg);
        }
        c.disconnect();
    }
}