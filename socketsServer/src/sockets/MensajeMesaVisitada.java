package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeMesaVisitada extends Mensaje implements Serializable{
	
	/** Identificador de la mesa **/
	public String idMesa;
	
	/** Indica si la mesa esta siendo visitada **/
	public boolean visitado;
		
	public MensajeMesaVisitada (String idMesa, boolean visitado, ArrayList<InetAddress> ips){
		super(ips);
		this.idMesa = idMesa;
		this.visitado = visitado;
	}
}
