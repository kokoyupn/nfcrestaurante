package sockets;
/**
 * 
 * 
 * Programa de ejemplo de como transmitir un fichero por un socket.
 * Esta es el mensaje que contiene los cachos de fichero que se van enviando
 * 
 */


import java.io.Serializable;

/**
 * Mensaje que contiene parte del fichero que se está transmitiendo.
 * 
 * 
 *
 */
@SuppressWarnings("serial")
public class MensajeTomaFichero implements Serializable
{
	/*
	public MensajeTomaFichero (String nombreFichero, boolean ultimoMensaje, int bytesValidos, byte[] contenidoFichero){
		this.nombreFichero = nombreFichero;
		this.ultimoMensaje = ultimoMensaje;
		this.bytesValidos = bytesValidos;
		this.contenidoFichero = contenidoFichero;
	}
	*/
    /** Nombre del fichero que se transmite. Por defecto "" */
    public String nombreFichero="";

    /** Si este es el último mensaje del fichero en cuestión o hay más después */
    public boolean ultimoMensaje=true;

    /** Cuantos bytes son válidos en el array de bytes */
    public int bytesValidos=0;

    /** Array con bytes leidos del fichero */
    public byte[] contenidoFichero = new byte[LONGITUD_MAXIMA];
    
    /** Número máximo de bytes que se enviaán en cada mensaje */
    public final static int LONGITUD_MAXIMA=10;
}
