package com.usal.clientPattern;
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
public class FicheroCliente implements Serializable
{
	
    /** path completo del fichero que se pide */
    public String nombreFichero;
    
    public FicheroCliente (String nombreFichero){
		this.nombreFichero = nombreFichero;
	}
}
