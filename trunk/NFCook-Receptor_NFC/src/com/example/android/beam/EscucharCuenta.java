package com.example.android.beam;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
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
	
	private String cuentas;
	private String correo; //nfcookapp@gmail.com
	private String contrasena; // Macarrones
	
	
	public EscucharCuenta(String correo, String contrasena){
		this.correo = correo;
		this.contrasena = contrasena;
	}
	
	public String getCuentas(){
		return cuentas;
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
            	if(message.getFrom()[0].toString().equals("NFCook <nfcookapp@gmail.com>") && message.getSubject().equals("CUENTA")){
            		// Se visualiza, si se sabe como, el contenido de cada mensaje
            		cuentas += analizaParteDeMensaje(message);
            	}
            	//Marcamos el mensaje como leido en el correo
            	message.setFlag(Flag.SEEN, true);
	        }
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
    private static String analizaParteDeMensaje(Message unaParte)
    {
        String cuenta = "";
        try
        {
            if(unaParte.getContent() instanceof Multipart)
            {                                  
                Multipart mime = (Multipart) unaParte.getContent();

                for (int i = 0; i < mime.getCount(); i++)
                {
                    BodyPart part = mime.getBodyPart(i);
                    cuenta += part.getContent().toString();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cuenta;
    }

}
