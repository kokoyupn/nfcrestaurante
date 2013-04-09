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
				comida += ((Plato) producto).toString() + "\n";
			}else{
				bebida += ((Bebida) producto).toString() + "\n";
			}
		}
		if(!comida.equals("Mesa: " + idMesa)){
			//TODO enviar a la impresiora.
		}
		if(!bebida.equals("Mesa: " + idMesa)){
			//TODO enviar a la impresora.
		}
	}
	
}
