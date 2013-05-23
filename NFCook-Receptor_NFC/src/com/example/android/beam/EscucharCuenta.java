package com.example.android.beam;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

/*
 * ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡IMPORTANTE!!!!!!!!!!!!!!!
 * 
 * Es necesario realizar la llamada a esta clase de esta forma. NO esta permitido realizar conexiones
 * en el hilo principal.
 * 
  		final EscucharCuenta escuchaCuentas = new EscucharCuenta(USUARIO, CONTRANESA);
		//Creamos un hilo para poder escuchar la cuenta.
		Thread hiloCuenta = new Thread(new Runnable() {
		    public void run() {
		    	try {
					escuchaCuentas.esperaYprocesaCuentas();
				} catch (Exception e) {
					e.printStackTrace();
				}		    }
		  });
		//Corremos el hilo
		hiloCuenta.start();
		//Esperamos hasta que termine
		while(hiloCuenta.isAlive()){}
		//Recogemos la cuenta
    	String cuentas = escuchaCuentas.getCuentas();
 * 
 * 
 * 
 */

public class EscucharCuenta {
	
	private static String cuentas;
	private String correo; //nfcookapp@gmail.com
	private String contrasena; // Macarrones
	
	/**
	 * Atributo que se va a encargar de almacenar las cuentas de todos los correos que vamos leyendo,
	 * ya que al buscar una cuenta abrimos todos los mails de cuentas que hay y puede que abramos alguno
	 * de otra mesa, entonces para no perdero ya que se marca como leído lo vamos a almacenar en esta 
	 * estructura.
	 */
	private static Map<Integer,String> historicoCuentas;
	
	@SuppressLint("UseSparseArrays")
	public EscucharCuenta(String correo, String contrasena){
		this.correo = correo;
		this.contrasena = contrasena;
		
		// Creamos el histórico de cuentas
		historicoCuentas = new HashMap<Integer, String>();
	}
		
	public void esperaYprocesaCuentas(){
		//Properties props = new Properties();
	    Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
	    try {
	        Session session = Session.getDefaultInstance(props, null);
	        Store store = session.getStore("imaps");
	        store.connect("imap.gmail.com", correo, contrasena);
	        
	        Folder inbox = store.getFolder("Inbox");
	        //Marcamos como lectura y escritura para poder marcar como leidos los mensajes en el correo
	        inbox.open(Folder.READ_WRITE);
	        //Creamos un flag para poder buscar solo los NO leidos
	        Flags leidos = new Flags(Flags.Flag.SEEN);
	        FlagTerm unseenFlagTerm = new FlagTerm(leidos, false);
	        //Buscamos los mensajes NO leidos
	        Message[] messages = inbox.search(unseenFlagTerm);
	        //Recorremos y tratacmos cada mensaje
	        for (Message message : messages) {
	            // Miramos si se trata de la sincronización de una cuenta
            	if(message.getFrom()[0].toString().equals("nfcookapp@gmail.com") && message.getSubject().equals("CUENTA")){
            		// Se visualiza, si se sabe como, el contenido de cada mensaje
            		analizaParteDeMensaje(message);
            		
            		//Marcamos el mensaje como leido en el correo
                	message.setFlag(Flags.Flag.DELETED, true);
            	}
	        }
	        
	        inbox.close(true);
	    } catch (NoSuchProviderException e) {
	        e.printStackTrace();
	        System.exit(1);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        System.exit(2);
	    }
	}
	
    /**
     * Metodo recursivo.
     * Si la parte que se pasa es compuesta, se extrae cada una de las subpartes y
     * el metodo se llama a si mismo con cada una de ellas.
     * Si la parte es un text, se escribe en pantalla.
     * Si la parte es una image, se guarda en un fichero y se visualiza en un JFrame.
     * En cualquier otro caso, simplemente se escribe el tipo recibido, pero se
     * ignora el mensaje.
     *
     * @param unaParte Parte del mensaje a analizar.
     */
    private String analizaParteDeMensaje(Message unaParte){
        String cuenta = "";
        try{
            // Si es texto, se escribe el texto.
            if (unaParte.isMimeType("text/*")) {
                cuenta = unaParte.getContent().toString();
                cuenta = (String) cuenta.subSequence(0, cuenta.length()-2);
                aniadirCuentaAHistorico(cuenta);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cuenta;
    }
    
    public void aniadirCuentaAHistorico(String cuenta){
    	// Vemos la mesa a la que corresponde
    	StringTokenizer token = new StringTokenizer(cuenta,"|");
    	// Cogemos el número de mesa
    	Integer numMesa = Integer.parseInt(token.nextToken());
    	
		historicoCuentas.put(numMesa, cuenta);
    }
    
    public String recogeTodasLasCuentas(){
    	if(!historicoCuentas.isEmpty()){
    		cuentas = "0";
    		
    		// Recorremos todas las cuentas que haya en el histórico
    		Iterator<String> it = historicoCuentas.values().iterator();
    		while(it.hasNext()){
    			cuentas += "¬" + it.next();
    		}
    	}
    	
    	return cuentas;
    }
}
