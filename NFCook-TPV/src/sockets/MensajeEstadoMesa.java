package sockets;

import java.net.InetAddress;
import java.util.ArrayList;

import tpv.Mesa.estadoMesa;

public class MensajeEstadoMesa {

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
    public estadoMesa estado = estadoMesa.CERRADA;
    
    /** ArrayList con las direcciones IP de los clientes que se han conectado al servidor **/
    public ArrayList<InetAddress> ips;
}
