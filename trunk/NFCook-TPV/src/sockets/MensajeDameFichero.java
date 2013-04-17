package sockets;
/**
 * 
 * 
 * Programa de ejemplo de como transmitir un fichero por un socket.
 * Esta es el mensaje que contiene los cachos de fichero que se van enviando
 * 
 */

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Mensaje para pedir un fichero.
 * 
 *
 */
@SuppressWarnings("serial")
public class MensajeDameFichero implements Serializable
{
    /** path completo del fichero que se pide */
    public String nombreFichero;
    
    public ArrayList<InetAddress> ips;
}
