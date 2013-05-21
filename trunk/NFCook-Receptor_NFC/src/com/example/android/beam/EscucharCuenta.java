package com.example.android.beam;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class EscucharCuenta {
	private String cuentas;
	
	public EscucharCuenta(){
		esperaYprocesaCuentas();
	}
	
	public String getCuentas(){
		return cuentas;
	}
	
	public String esperaYprocesaCuentas(){
		/*// Se obtiene la Session
        Properties prop = new Properties();
        prop.setProperty("mail.pop3.starttls.enable", "false");
        prop.setProperty(
            "mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.pop3.socketFactory.fallback", "false");
        prop.setProperty("mail.pop3.port", "995");
        prop.setProperty("mail.pop3.socketFactory.port", "995");
        Session sesion = Session.getInstance(prop);
        
        try{
        	// Se obtiene el Store y el Folder, para poder leer el
        	// correo.
            Store store = sesion.getStore("pop3");
            store.connect(
                "pop.gmail.com", "nfcookapp@gmail.com", "Macarrones");
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Se obtienen los mensajes.
            javax.mail.Message[] mensajes = folder.getMessages();

            // Se escribe from y subject de cada mensaje
            for (int i = 0; i < mensajes.length; i++){
            	// Miramos si se trata de la sincronización de un pedido
            	if(mensajes[i].getFrom()[0].toString().equals("nfcookapp@gmail.com") &&
            			mensajes[i].getSubject().equals("CUENTA")){
            		// Se visualiza, si se sabe como, el contenido de cada mensaje
            		cuentas += "&" + analizaParteDeMensaje(mensajes[i]);
            	}
            }

            folder.close(false);
            store.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
		Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");

	    props.setProperty("mail.store.socketFactory.class",
	                "com.imap.DummySSLSocketFactory");


	    // Prevents to fall into NOT-secure connection
	    props.setProperty("mail.pop3.socketFactory.fallback", "false");

	    try {
	        Session session = Session.getDefaultInstance(props, null);
	        Store store = session.getStore("imaps");
	        store.connect("nfcookapp@gmail.com", "nfcookapp@gmail.com", "Macarrones");

	        System.out.println(store);

	        Folder inbox = store.getFolder("Inbox");
	        inbox.open(Folder.READ_ONLY);
	        Message messages[] = inbox.getMessages();
	        for (Message message : messages) {
	            System.out.println(message);
	        }
	    } catch (NoSuchProviderException e) {
	        e.printStackTrace();
	        System.exit(1);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        System.exit(2);
	    }
        return cuentas;
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
    		// Si es texto, se escribe el texto.
            if (unaParte.isMimeType("text/*")) {
                InputStream stream = unaParte.getInputStream();
                while(stream.available() != 0){
                	cuenta += (char) stream.read();
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
