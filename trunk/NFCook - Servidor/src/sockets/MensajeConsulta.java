package sockets;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Mensaje que contiene la consulta que se está transmitiendo.
 *  
 */
@SuppressWarnings("serial")
public class MensajeConsulta extends Mensaje implements Serializable 
{
	
    /** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero;

    /** Consulta que se transmite para ejecutarse en la base de datos: nombreFichero **/
    public String sql;
    
    public MensajeConsulta(String nombreFichero, String sql, ArrayList<InetAddress> ips) {
		super(ips);
		this.nombreFichero = nombreFichero;
		this.sql = sql;
	}
}
