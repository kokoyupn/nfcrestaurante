package tpv;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class Cobro {

	private ArrayList<Producto> cobro;
	private FechaYHora horaEnvioYFecha;
	private String idCamarero;
	private String idMesa;
	private double total;
	private String restaurante;
	
	public Cobro(ArrayList<Producto> cobro, String idMesa, String idCamarero, double total , String restaurante) {
		this.cobro = cobro;
		this.idCamarero = idCamarero;
		this.idMesa = idMesa;
		horaEnvioYFecha = new FechaYHora();
		this.total = total;
		this.restaurante = restaurante;
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
		String textoQR = restaurante.toUpperCase() + " NFCook";

		while(itProductos.hasNext()){
			Producto producto = itProductos.next();
			if(producto instanceof Plato){
				comida +=  "Nombre: " + ((Plato) producto).getNombre() + " -->" + "Precio: " + producto.getPrecio() + "�";
				textoQR += "Nombre: " + ((Plato) producto).getNombre() + " -->" + producto.getPrecio() + "�";
			}else{
				bebida += "Nombre: " + ((Bebida) producto).getNombre() + " -->" + "Precio: " + producto.getPrecio() + "�";
				textoQR += "Nombre: " + ((Bebida) producto).getNombre() + " -->" + producto.getPrecio() + "�";
			}
		}
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
							"Nombre: " + "Mesa: " + idMesa +  ".  Le atendi� " + idCamarero + 
							"Nombre: " + "Fecha y hora: " + horaEnvioYFecha + "Nombre: ";
		
		if (comidaBool && bebidaBool){
			textoAImprimir += comida + bebida; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
		}else if(comidaBool){//no hay bebida
			textoAImprimir += comida ; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
		}else{//no hay comida
			textoAImprimir += bebida; //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
		}
		textoAImprimir += "Nombre: " + separador(80) +
				"Nombre: " + "Total: " + total + " �" + "Nombre: " + "Gracias por su visita";
		
		textoQR += "Nombre: " + "Total: " + total + " �";
		
		QRCodeJava.generaQR(textoQR);//Crea el archivo en C:\\hlocal\\QR_Code.PNG
		Image img = loadImage("ArchivoQR/QR_Code.PNG");
		//Image img = loadImage("C:\\Archivos de Guillermo\\Uni\\IS\\QR_Code.PNG");
		Imprimir.imprime(textoAImprimir,img);
		
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
	
}

