package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Mensaje implements Serializable{

    /** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
	public ArrayList<InetAddress> ips;
	
	public Mensaje (ArrayList<InetAddress> ips){
		this.ips = ips;
	}
	
}
