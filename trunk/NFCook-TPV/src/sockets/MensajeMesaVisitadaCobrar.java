package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeMesaVisitadaCobrar implements Serializable{
	
	/** Identificador de la mesa **/
	public String idMesa;
	
	/** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
	
	public MensajeMesaVisitadaCobrar (String idMesa, ArrayList<InetAddress> ips){
		this.idMesa = idMesa;
		this.ips = ips;
	}
}
