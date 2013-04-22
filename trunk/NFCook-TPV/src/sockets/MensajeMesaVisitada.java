package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeMesaVisitada implements Serializable{
	
	/** Identificador de la mesa **/
	public String idMesa;
	
	/** Indica si la mesa esta siendo visitada **/
	public boolean visitado;
	
	/** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
	
	public MensajeMesaVisitada (String idMesa, boolean visitado, ArrayList<InetAddress> ips){
		this.idMesa = idMesa;
		this.visitado = visitado;
		this.ips = ips;
	}
}
