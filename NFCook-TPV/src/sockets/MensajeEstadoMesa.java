package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeEstadoMesa implements Serializable{
	/*
	public MensajeEstadoMesa(String nombreFichero, String sql, String idMesa, String idCamarero, int numPersonas, int estado, ArrayList<InetAddress> ips){
		this.nombreFichero = nombreFichero;
		this.sql = sql;
		this.idMesa = idMesa;
		this.idCamarero = idCamarero;
		this.numPersonas = numPersonas;
		this.estado = estado;
		this.ips = ips;
	}
	*/
	
	/** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero = "";

    /** Consulta que se transmite para ejecutarse en la base de datos: nombreFichero **/
    public String sql = "";
    
    /** Identificador de la mesa **/
    public String idMesa = "";
    
    /** Identificador del camarero **/
    public String idCamarero = "";
    
    /** Numero de personas en la mesa **/
    public int numPersonas = 0;
    
    /** Estado de la mesa **/
    public int estado = 0;
    
    /** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
}
