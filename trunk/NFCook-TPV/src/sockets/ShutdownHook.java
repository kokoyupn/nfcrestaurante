package sockets;


/** Thread que se ejecutará cuando la aplicación se cierre. 
 * De esta forma podremos eliminar la IP del TPV de la lista de clientes del servidor.
 **/
public class ShutdownHook extends Thread{

	private String idMesa;
	
	public ShutdownHook(String idMesa){
		this.idMesa = idMesa;
	}
	
	public void run(){
		
		if (idMesa != null)
			 // si estamos dentro de una mesa, cambiamos el estado a NO visitada
			ClienteFichero.enviaMesaVisitada(idMesa, false);
		
		// enviamos la IP del TPV al servidor para eliminarlo de su lista de IPs
		ClienteFichero.enviaIPtpv();

	}
}
