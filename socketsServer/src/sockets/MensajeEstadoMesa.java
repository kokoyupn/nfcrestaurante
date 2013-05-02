package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeEstadoMesa extends Mensaje implements Serializable{
	
	/** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero;

    /** Consulta que se transmite para ejecutarse en la base de datos: nombreFichero **/
    public String sql;
    
    /** Identificador de la mesa **/
    public String idMesa;
    
    /** Identificador del camarero **/
    public String idCamarero;
    
    /** Numero de personas en la mesa **/
    public int numPersonas;
    
    /** Estado de la mesa **/
    public int estado;
    
	public MensajeEstadoMesa(String nombreFichero, String sql, String idMesa, String idCamarero, int numPersonas, int estado, ArrayList<InetAddress> ips){
		super(ips);
		this.nombreFichero = nombreFichero;
		this.sql = sql;
		this.idMesa = idMesa;
		this.idCamarero = idCamarero;
		this.numPersonas = numPersonas;
		this.estado = estado;
	}
    
}
