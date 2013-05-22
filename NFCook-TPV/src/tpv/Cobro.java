package tpv;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import basesDeDatos.Operaciones;

public class Cobro {

	private ArrayList<Producto> cobro;
	private FechaYHora horaEnvioYFecha;
	private String idCamarero;
	private String idMesa;
	private double total;
	private String restaurante;
	private int promocion ;
	private int numPers;
	
	public Cobro(ArrayList<Producto> cobro, String idMesa, String idCamarero, double total,
				String restaurante,int promocion, int numPers) {
		this.cobro = cobro;
		this.idCamarero = idCamarero;
		this.idMesa = idMesa;
		horaEnvioYFecha = new FechaYHora();
		this.total = total;
		this.restaurante = restaurante;
		this.promocion = promocion;
		this.numPers = numPers;
		enviarCobroAImpresora();
	}

	public ArrayList<Producto> getComanda() {
		return cobro;
	}

	public FechaYHora getHoraEnvioYFecha() {
		return horaEnvioYFecha;
	}

	public String getIdCamarero() {
		return idCamarero;
	}
	
	public String getIdMesa(){
		return idMesa;
	}
	
	public void enviarCobroAImpresora(){
		Iterator<Producto> itProductos = cobro.iterator();
		String comida = "";
		String bebida = "";
		try{
			//busco en la base de datos Equivalencia, el numero asociado al restaurante
			Operaciones operacion = new Operaciones("Equivalencia_Restaurantes.db");
			ResultSet resultados = operacion.consultar("select * from Restaurantes where Restaurante=" + restaurante);
			
			String abreviatura = resultados.getString("Abreviatura");
			String numero = resultados.getString("Numero");
					
			String textoQR = numero;
			String textoACamarero = idMesa.substring(1) + "/";
				
			while(itProductos.hasNext()){
				Producto producto = itProductos.next();
				comida +=  "Nombre: " + producto.getNombre() + " -->" + "Precio: " + producto.getPrecio() + "€";
				textoQR += "@" + producto.getId().substring(abreviatura.length());//le quitamos fh o v
				textoACamarero += "@" + producto.getId() ;
				//Rellenamos los extras si es un plato
				if(producto instanceof Plato){
					textoACamarero += "+" + ((Plato)producto).getExtrasMarcados().replace(",","+");
				}else textoACamarero += "+ No configurable" ;

				//Rellenamos las observaciones
				if(producto.getObservaciones().equals("")){//Si es vacio metemos una barra baja
					textoACamarero += "*_";
				}else textoACamarero += "*" + producto.getObservaciones();
				textoACamarero +=  "*" 	+ producto.getNombre() +"*"
										+ producto.getPrecio() + "*"
										+ numPers + "*"
										+ producto.getIdUnico() +  "*"
										+ "1" + "*"//FIXME pasar idCam
										+ horaEnvioYFecha ;	
			}
			
			//Simulamos el envio por NFC
			mandarCuentaNFC(textoACamarero);
			
			boolean comidaBool = false;
			boolean bebidaBool = false;
			if(!comida.equals("")){
				comidaBool = true;
			}
			if(!bebida.equals("")){
				bebidaBool = true;
			}
			
			String textoAImprimir = "";
			//cuando lea "Nombre: " saltara de linea
			textoAImprimir = 	restaurante.toUpperCase() + " ------ NFCook" +
								"Nombre: " + "Mesa: " + idMesa +  ".  Le atendió " + idCamarero + 
								"Nombre: " + "Fecha y hora: " + horaEnvioYFecha + "Nombre: ";
			
			if (comidaBool && bebidaBool){
				textoAImprimir += comida + bebida; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
			}else if(comidaBool){//no hay bebida
				textoAImprimir += comida ; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
			}else{//no hay comida
				textoAImprimir += bebida; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
			}
			if (promocion == 1){//2x1
				
				textoAImprimir += "Nombre: " + separador(80) +
						"Nombre: " + "Subtotal: " + total ;
				//aplicamos la promocion 2x1. Cobramos los platos mas caros. Bebidas y platos por separado
				total = Math.rint(aplica2x1(cobro)*100)/100;
				textoAImprimir += "Nombre: " + "¡¡¡Aplicado 2x1!!!" + "Nombre: "+
						"Total: " + total + " €" + "Nombre: " + "Gracias por su visita";
				
			}else if  (promocion == 2){//30%
				textoAImprimir += "Nombre: " + separador(80) +
						"Nombre: " + "Subtotal: " + total ;
				total = Math.rint((total*0.7)*100)/100;
				textoAImprimir += "Nombre: " + "¡¡¡Aplicado el 30%!!!" + "Nombre: "+
						"Total: " + total + " €" + "Nombre: " + "Gracias por su visita";
			}else{//No promo
				textoAImprimir += "Nombre: " + separador(80) +
						"Nombre: " + "Total: " + total + " €" + "Nombre: " + "Gracias por su visita";
				
			} 			
			QRCodeJava.generaQR(textoQR);
			Image img = loadImage("ArchivoQR/QR_Code.PNG");
			Imprimir.imprime(textoAImprimir,img);
		
		}catch(SQLException e) {
            System.out.println("Mensaje:"+e.getMessage());
            System.out.println("Estado:"+e.getSQLState());
            System.out.println("Codigo del error:"+e.getErrorCode());
            JOptionPane.showMessageDialog(null, ""+e.getMessage());
        }
		
	}
	
	public String calculaPuntos(int carPorLinea, String restoDeTexto){
		int aux = restoDeTexto.length() % carPorLinea; //ocupadas
		aux = carPorLinea - aux + 2;
		String result = "";
		for (int i = 0 ; i < aux ; i++){
			result += ".";
		}
		return result;
	}
	
	public String separador(int n){
		String res = "";
		for(int i = 0; i < n; i++){
			res += "-";
		}
		return res;
	}
	
	public static BufferedImage loadImage(String ref) {
		BufferedImage bimg = null;
		try {

			bimg = ImageIO.read(new File(ref));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bimg;
	}

	
	public double aplica2x1(ArrayList<Producto> cobro){
		double result = 0;
		//Separo los platos de las bebidas
		PriorityQueue<Plato> platos = new PriorityQueue<Plato>(1,new ComparadorPrecio());
		PriorityQueue<Bebida> bebidas = new PriorityQueue<Bebida>(1,new ComparadorPrecio());
		for(int i = 0; i< cobro.size();i++){
			if (cobro.get(i) instanceof Plato){
				platos.add((Plato) cobro.get(i));
			}else{
				bebidas.add((Bebida) cobro.get(i));
				}
		}
		int auxPlatos = platos.size()/2;
		int auxBebidas = bebidas.size()/2;
		Producto p ;
		
		if(platos.size() % 2 == 0 && bebidas.size() % 2 == 0){//Hay platos y bebidas pares. Cobramos la mitad (los mas caros)
			for(int i =0; i < auxPlatos; i++){
				p = platos.remove();
				result += p.getPrecio();			
			}
			for(int i =0; i < auxBebidas; i++){
				p = bebidas.remove();
				result += p.getPrecio();			
			}
		}else if(platos.size() % 2 != 0 && bebidas.size() % 2 != 0){//Hay platos y bebidas impares. Cobramos la mitad mas uno de los platos y la mitad menos uno de las bebidas
			for(int i =0; i <= auxPlatos ; i++){
				p = platos.remove();
				result += p.getPrecio();				
			}
			for(int i =0; i <= auxBebidas ; i++){
				p = bebidas.remove();
				result += p.getPrecio();		
			}
		}else if(platos.size() % 2 == 0 && bebidas.size() % 2 != 0){//Platos pares bebidas impares. Mitad de los platos y mitad mas uno de las bebidas
			for(int i =0; i < auxPlatos; i++){
				p = platos.remove();
				result += p.getPrecio();				
			}
			for(int i =0; i <= auxBebidas; i++){
				p = bebidas.remove();
				result += p.getPrecio();			
			}
		}else{//Platos impares bebidas pares. Mitad mas uno de los platos y mitad de las bebidas
			for(int i =0; i <= auxPlatos; i++){
				p = platos.remove();
				result += p.getPrecio();				
			}
			for(int i =0; i < auxBebidas; i++){
				p = bebidas.remove();
				result += p.getPrecio();			
			}
		}
		return result;
		
	}
	
	
	
	
	/**
	 * Compara productos por precio
	 * @author Guille
	 *
	 */
	public class ComparadorPrecio implements Comparator<Object>{

		@Override
		public int compare(Object arg0, Object arg1) {
			if(arg0 instanceof Producto && arg1 instanceof Producto){
				if (((Producto)arg0).getPrecio() < ((Producto)arg1).getPrecio()){
					return 1;
				}
				if (((Producto)arg0).getPrecio() > ((Producto)arg1).getPrecio()){
					return -1;
				}
				//cuestan igual
				return 0;
			}
			return 0;
		}
		
	}
	
	/**
	 * Metodo encargado de mandar por mail la cuenta para que el dispositivo receptor la reciba y la pueda coger el camarero.
	 * Esto simula al receptor NFC que habrá en la realidad
	 * 
	 * @author Abel
	 */
	public void mandarCuentaNFC(String cuenta){
		Mail m = new Mail();
        m.setUser("nfcookapp@gmail.com");// username 
        m.setPass("Macarrones");// password

        String[] toArr = {"nfcookapp@gmail.com"}; 
        m.setTo(toArr); 
        m.setFrom("nfcookapp@gmail.com"); 
        m.setSubject("CUENTA"); 
        m.setBody(cuenta); 

        try { 
        	if(m.send()) { 
        		System.out.println("Cuenta sincronizada correctamente."); 
        	} else { 
        		System.out.println("Cuenta no sincronizada."); //Si usuario y enviante no coinciden 
        	} 
        }catch(Exception e) { 
        	System.out.println("Error al sincronizar la cuenta"); //Si ha habido fallos 
        } 
    }
}





