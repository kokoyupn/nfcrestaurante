package sockets;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Mensaje que contiene la consulta que se está transmitiendo.
 *  
 */
@SuppressWarnings("serial")
public class MensajeConsulta implements Serializable
{
	/*
	public MensajeConsulta(String nombreFichero, String sql, ArrayList<InetAddress> ips){
		this.nombreFichero = nombreFichero;
		this.sql = sql;
		this.ips = ips;
	}
	*/
    /** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero = "";

    /** Consulta que se transmite para ejecutarse en la base de datos: nombreFichero **/
    public String sql = "";
    
    /** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
}
