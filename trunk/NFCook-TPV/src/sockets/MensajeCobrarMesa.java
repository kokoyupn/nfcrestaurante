package sockets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MensajeCobrarMesa extends Mensaje implements Serializable{
    
	/** Identificador de la mesa **/
	public String idMesa;
	
    // eliminara de dos bases de datos
	/** Nombre del fichero que se transmite */
    public String nombreFichero;
    public String nombreFichero2;
    
    /** Consulta que se transmite para ejecutarse en la base de datos: nombreFichero **/
    public String sql;
    public String sql2;

	public MensajeCobrarMesa(String idMesa, String nombreFichero, String sql, String nombreFichero2, String sql2, ArrayList<InetAddress> ips){
		super(ips);
		this.idMesa = idMesa;
		this.nombreFichero = nombreFichero;
		this.nombreFichero2 = nombreFichero2;
		this.sql2 = sql2;
	}
}
