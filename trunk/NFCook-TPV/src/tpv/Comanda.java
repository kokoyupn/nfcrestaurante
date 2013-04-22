package tpv;

import java.util.ArrayList;
import java.util.Iterator;

public class Comanda {
	
	private ArrayList<Producto> comanda;
	private FechaYHora horaEnvioYFecha;
	private String idCamarero;
	private String idMesa;
	
	public Comanda(ArrayList<Producto> comanda, String idMesa, String idCamarero) {
		this.comanda = comanda;
		this.idCamarero = idCamarero;
		this.idMesa = idMesa;
		horaEnvioYFecha = new FechaYHora();
		enviarComandaAImpresora();
	}

	public ArrayList<Producto> getComanda() {
		return comanda;
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
	
	public void enviarComandaAImpresora(){
		Iterator<Producto> itProductos = comanda.iterator();
		String comida = "Mesa: " + idMesa;
		String bebida = "Mesa: " + idMesa;
		while(itProductos.hasNext()){
			Producto producto = itProductos.next();
			if(producto instanceof Plato){
				comida +=  ((Plato) producto).toString();
			}else{
				bebida += ((Bebida) producto).toString();
			}
		}
//		boolean comidaBool = false;
//		boolean bebidaBool = false;
		if(!comida.equals("Mesa: " + idMesa)){
			Imprimir.imprime(comida,null);
			//comidaBool = true;
		}
//		if(!bebida.equals("Mesa: " + idMesa)){
//			//Imprimir.imprime(bebida); //Las bebidas no van a cocina
//			//bebidaBool = true;
//		}
//		if (comidaBool && bebidaBool){
//			Imprimir.imprime(comida + bebida); //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
//		}else if(comidaBool){//no hay bebida
//			Imprimir.imprime(comida ); //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
//		}else{//no hay comida
//			Imprimir.imprime( bebida); //Para que salga todo en la misma impresion. Quitar cuando separemos en dos impresoras
//		}
	}
	
}
