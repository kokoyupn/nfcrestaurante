package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeConsultaEliminaPlatos extends Mensaje implements Serializable{
	
	/** Identificador de la mesa **/
	public String idMesa;
	
	 /** Nombre del fichero que se transmite **/
    public String nombreFichero;
	
	/** Consulta sql **/
	public String sql;
		
	public MensajeConsultaEliminaPlatos (String idMesa, String nombreFichero, String sql, ArrayList<InetAddress> ips){
		super(ips);
		this.idMesa = idMesa;
		this.nombreFichero = nombreFichero;
		this.sql = sql;
	}
}
