/**
 * 
 * 
 * Programa de ejemplo de como transmitir un fichero por un socket.
 * Esta es la parte del servidor.
 */
package sockets;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import basesDeDatos.Operaciones;

/**
 * Clases servidora que env�a un fichero al primer cliente que se lo pida.
 * 
 * 
 */
public class ServidorFichero
{
	private ArrayList<InetAddress> clientes;
	private Socket clienteActual;
	public HashMap<String,ArrayList<Boolean>> visitadas;

    /**
     * Instancia la clase servidora y la pone a la escucha del puerto 5000
     * 
     * 
     */
    public static void main(String[] args)
    {
        ServidorFichero sf = new ServidorFichero();
        sf.escucha(5000);
    }    
    
    
    /**
     * Se escucha el puerto indicado en espera de clientes a los que enviar
     * el fichero.
     * 
     * @param puerto El puerto de escucha
     */
    public void escucha(int puerto)
    {
    	clientes = new ArrayList<InetAddress>();
    	visitadas = new HashMap<String,ArrayList<Boolean>>();
    	cargaVisitados();
    	while (true){
    		try{
        	
	            // Se abre el socket servidor
	            ServerSocket socketServidor = new ServerSocket(puerto);
	            
	            // Se espera un cliente
	            clienteActual = socketServidor.accept();

	            // Llega un cliente.
	            System.out.println("Aceptado cliente");
	
	            // Se lee el mensaje de petici�n de fichero del cliente.
	            ObjectInputStream ois = new ObjectInputStream(clienteActual.getInputStream());
	            Mensaje mensaje = (Mensaje) ois.readObject();
	           
	            // Si el mensaje es de petici�n de fichero
	            if (mensaje instanceof MensajeDameFichero){
	            	// Cuando se cierre el socket, esta opcion hara que el cierre se
		            // retarde automaticamente hasta 10 segundos dando tiempo al cliente
		            // a leer los datos.
		            clienteActual.setSoLinger(true, 10);
		            
		            addIPcliente(mensaje);
		            
	            	// Se muestra en pantalla el fichero pedido y se envia
	                System.out.println("Me piden: "
	                        + ((MensajeDameFichero) mensaje).nombreFichero);
	                
	                enviaFichero(((MensajeDameFichero) mensaje).nombreFichero,
	                        new ObjectOutputStream(clienteActual.getOutputStream()));
	            		            
	            // Si el mensaje es de una consulta sql    
	            }else if (mensaje instanceof MensajeConsulta){
	            		if (((MensajeConsulta) mensaje).nombreFichero.contentEquals("")){ // si es "" sera un mensaje para eliminar la IP del cliente
		            		removeIPcliente((MensajeConsulta)mensaje);
		            		
	            		}else{
			            	addIPcliente(mensaje);
			            	// enviamos el mensaje con las direcciones ip de los clientes
			            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
			            	MensajeConsulta mensajeConIPs = (MensajeConsulta) mensaje;
			            	mensajeConIPs.ips = clientes;
			            	System.out.println(mensajeConIPs.sql);
			            	oos.writeObject(mensajeConIPs);
			            	
			            	// realizar modificacion en la base de datos (todos los datos estan en mensaje)
			            	Operaciones conexion = new Operaciones(((MensajeConsulta) mensaje).nombreFichero);
			            	conexion.insertar(((MensajeConsulta) mensaje).sql);
			            	conexion.cerrarBaseDeDatos();

	            		}
	            }else if (mensaje instanceof MensajeArrayConsultas){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeArrayConsultas mensajeConIPs = (MensajeArrayConsultas) mensaje;
	            	mensajeConIPs.ips = clientes;
	            
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// realizar modificacion en la base de datos 
	            	Operaciones conexion = new Operaciones(((MensajeArrayConsultas) mensaje).nombreFichero);
	            	Iterator<String> itConsultas = ((MensajeArrayConsultas) mensaje).consultas.iterator();
	            	while (itConsultas.hasNext()){
	            		String consulta = itConsultas.next();
	            		conexion.insertar(consulta);
	            		System.out.println(consulta);
		            	conexion.cerrarBaseDeDatos();

	            	}
	            
	            }else if (mensaje instanceof MensajeEstadoMesa){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeEstadoMesa mensajeConIPs = (MensajeEstadoMesa) mensaje;
	            	mensajeConIPs.ips = clientes;
	            	
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// realizar modificacion en la base de datos
	            	Operaciones conexion = new Operaciones(((MensajeEstadoMesa) mensaje).nombreFichero);
	            	conexion.insertar(((MensajeEstadoMesa) mensaje).sql);
	            	conexion.cerrarBaseDeDatos();

	            }else if (mensaje instanceof MensajeMesaVisitada){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeMesaVisitada mensajeConIPs = (MensajeMesaVisitada) mensaje;
	            	mensajeConIPs.ips = clientes;
	            	
	            	setVisitada(mensajeConIPs.visitado, mensajeConIPs.idMesa);
	            
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// con este mensaje no hace falta modificar nada en las bbdd del servidor

	            }else if (mensaje instanceof MensajeMesaVisitadaCobrar){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeMesaVisitadaCobrar mensajeConIPs = (MensajeMesaVisitadaCobrar) mensaje;
	            	mensajeConIPs.ips = clientes;
	            
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// con este mensaje no hace falta modificar nada en las bbdd del servidor

	            }else if (mensaje instanceof MensajeCobrarMesa){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeCobrarMesa mensajeConIPs = (MensajeCobrarMesa) mensaje;
	            	mensajeConIPs.ips = clientes;
	            
	            	// Para esta mesa, reiniciamos su array de visitas
	            	visitadas.put(mensajeConIPs.idMesa, new ArrayList<Boolean>());
	            	
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// modificar la base de datos del servidor
	            	Operaciones operacion = new Operaciones(((MensajeCobrarMesa) mensaje).nombreFichero);
	            	operacion.insertar(((MensajeCobrarMesa) mensaje).sql);
	            	System.out.println("Consulta1CobrarMesa: " + ((MensajeCobrarMesa) mensaje).sql);
	            	operacion.cerrarBaseDeDatos();
	            	Operaciones operacion2 = new Operaciones(((MensajeCobrarMesa) mensaje).nombreFichero2);
	            	operacion2.insertar(((MensajeCobrarMesa) mensaje).sql2);
	            	System.out.println("Consulta2CobrarMesa: " + ((MensajeCobrarMesa) mensaje).sql2);
	            	operacion2.cerrarBaseDeDatos();

	            }else if (mensaje instanceof MensajeMesasVisitadas){
	            	
	            	addIPcliente(mensaje);
	            	// enviamos el mensaje con las direcciones ip de los clientes
	            	ObjectOutputStream oos = new ObjectOutputStream(clienteActual.getOutputStream());
	            	MensajeMesasVisitadas mensajeConIPs = (MensajeMesasVisitadas) mensaje;
	            	mensajeConIPs.ips = clientes;
	            	
	            	mensajeConIPs.mesasVisitadas = getVisitadas();
	            	oos.writeObject(mensajeConIPs);
	            	
	            	// con este mensaje no hace falta modificar nada en las bbdd del servidor

	            }else{
	            	// Si no es el mensaje esperado, se avisa y se sale todo.
	                System.err.println (
	                        "Mensaje no esperado " + mensaje.getClass().getName());
	            }
	            
	            // Cierre de sockets 
	            clienteActual.close();
	            socketServidor.close(); 	
        	
	        }catch (Exception e){
	           System.err.println("Fallo de conexion durante transmision");
	        	// e.printStackTrace();
	        }
    	}
    }
   
    
    /**
     * Elimina la IP del cliente del Array de IPs de clientes.
     **/
    public void removeIPcliente(MensajeConsulta mensaje){
    	InetAddress ipCliente = mensaje.ips.get(0);
    	if (clientes.contains(ipCliente)){
    		clientes.remove(ipCliente);
    	}
    }
    
    /**
     * Anade, si no existia, la IP del cliente al registro de IPs de clientes
     */
    public void addIPcliente(Mensaje mensaje){
    	if (!clientes.contains(mensaje.ips.get(0)))
        	clientes.add(mensaje.ips.get(0));
	}
    
    /** Carga el Hashmap de mesas que estan siendo visitadas**/
    public void cargaVisitados(){
    	try{
	    	Operaciones operacion = new Operaciones("MesasRestaurante.db");
	    	ResultSet resultados = operacion.consultar("select * from mesasRestaurante");
	    	
	    	while (resultados.next()){
	    		ArrayList<Boolean> visitado = new ArrayList<Boolean>();
	    		String idMesa = resultados.getString("idMesa");
	    		visitadas.put(idMesa, visitado);
	    	}
	    	
	    	operacion.cerrarBaseDeDatos();
	    	
    	}catch(SQLException e){
    		
    	}
    }

    /** Anade o elimina una visita de una mesa **/
    public void setVisitada(boolean visitada, String idMesa){
    	
    	if (visitada){
    		visitadas.get(idMesa).add(visitada); // anadimos una visita a la mesa
    	}else{
    		visitadas.get(idMesa).remove(0); // quitamos una visita a esa mesa
    	}
    			
    }
    
    /** Nos devuelve un Array con todos los IDs de las mesas que tienen visita **/
    public ArrayList<ArrayList<String>> getVisitadas(){
    	Set<String> idMesas = visitadas.keySet();
    	Iterator<String> itIDMesas = idMesas.iterator();
    	ArrayList<String> mesasResultantes = new ArrayList<String>(); // pos 0 = idMesa y pos 1 = visitas
    	ArrayList<ArrayList<String>> resultado = new ArrayList<ArrayList<String>>();
    	
    	while (itIDMesas.hasNext()){
    		String mesa = itIDMesas.next();
    		ArrayList<Boolean> visitada = visitadas.get(mesa);
    		int visitas = 0;
    		if (visitada.size() != 0){
    			for (int i=0;i<visitada.size();i++){
    				visitas++;
    			}
    			mesasResultantes.add(mesa);
    			mesasResultantes.add(visitas + "");
    			resultado.add(mesasResultantes);
    		}
    	}
    	return resultado;
    }
    
    
    /**
     * Envia el fichero indicado a traves del ObjectOutputStream indicado.
     * @param fichero Nombre de fichero
     * @param oos ObjectOutputStream por el que enviar el fichero
     */
    private void enviaFichero(String fichero, ObjectOutputStream oos)
    {
        try
        {
        	String ruta = "BasesDeDatosTPV/" + fichero;
            boolean enviadoUltimo=false;
            // Se abre el fichero.
            FileInputStream fis = new FileInputStream(ruta);
              
            // Se instancia y rellena un mensaje de envio de fichero
            MensajeTomaFichero mensaje = new MensajeTomaFichero();
            mensaje.nombreFichero = fichero;
            
            // Se leen los primeros bytes del fichero en un campo del mensaje
            int leidos = fis.read(mensaje.contenidoFichero);
            
            // Bucle mientras se vayan leyendo datos del fichero
            while (leidos > -1)
            {
                
                // Se rellena el n�mero de bytes leidos
                mensaje.bytesValidos = leidos;
                
                // Si no se han leido el m�ximo de bytes, es porque el fichero
                // se ha acabado y este es el �ltimo mensaje
                if (leidos < MensajeTomaFichero.LONGITUD_MAXIMA)
                {
                    mensaje.ultimoMensaje = true;
                    enviadoUltimo=true;
                }
                else
                    mensaje.ultimoMensaje = false;
                
                // Se env�a por el socket
                oos.writeObject(mensaje);
                
                // Si es el �ltimo mensaje, salimos del bucle.
                if (mensaje.ultimoMensaje)
                    break;
                
                // Se crea un nuevo mensaje
                mensaje = new MensajeTomaFichero();
                mensaje.nombreFichero = fichero;
                
                // y se leen sus bytes.
                leidos = fis.read(mensaje.contenidoFichero);
            }
            
            if (enviadoUltimo==false)
            {
                mensaje.ultimoMensaje=true;
                mensaje.bytesValidos=0;
                oos.writeObject(mensaje);
            }
            // Se cierra el ObjectOutputStream
            oos.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
