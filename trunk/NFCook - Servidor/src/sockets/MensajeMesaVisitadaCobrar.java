package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeMesaVisitadaCobrar extends Mensaje implements Serializable{
	
	/** Identificador de la mesa **/
	public String idMesa;
	
	public MensajeMesaVisitadaCobrar (String idMesa, ArrayList<InetAddress> ips){
		super(ips);
		this.idMesa = idMesa;
	}
}
