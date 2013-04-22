package sockets;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Mensaje que contiene la consulta que se está transmitiendo.
 *  
 */
@SuppressWarnings("serial")
public class MensajeArrayConsultas implements Serializable
{
	/*
	public MensajeArrayConsultas(String nombreFichero, ArrayList<String> consultas, ArrayList<InetAddress> ips){
		this.nombreFichero = nombreFichero;
		this.consultas = consultas;
		this.ips = ips;
	}*/
	
    /** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero = "";
    
    /** ArrayList con todas las consultas de platos enviadas para un pedido **/
    public ArrayList<String> consultas;
    
    /** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
}
