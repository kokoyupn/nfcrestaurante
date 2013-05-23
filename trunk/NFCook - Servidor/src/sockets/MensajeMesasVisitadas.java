package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeMesasVisitadas extends Mensaje implements Serializable{

	public ArrayList<ArrayList<String>> mesasVisitadas;
	
	public MensajeMesasVisitadas(ArrayList<InetAddress> ips){
		super(ips);
		this.mesasVisitadas = new ArrayList<ArrayList<String>>();
	}

}

