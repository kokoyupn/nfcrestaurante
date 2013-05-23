package sockets;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Mensaje que contiene la consulta que se está transmitiendo.
 *  
 */
@SuppressWarnings("serial")
public class MensajeArrayConsultas extends Mensaje implements Serializable
{
	
    /** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero;
    
    /** ArrayList con todas las consultas de platos enviadas para un pedido **/
    public ArrayList<String> consultas;
    
    public MensajeArrayConsultas(String nombreFichero, ArrayList<String> consultas, ArrayList<InetAddress> ips){
		super(ips);
		this.nombreFichero = nombreFichero;
		this.consultas = consultas;
	}
 
}
