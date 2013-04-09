package tpv;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoricoComandasMesas {
	
	private HashMap<String, ArrayList<Comanda>> historicoMesas; // La clave es el idMesa y contiene todas las comandas de esa mesa.

	
	public HistoricoComandasMesas(){
		historicoMesas = new HashMap<String, ArrayList<Comanda>>();
	}
	
	public void añadirComandaPorMesa(String idMesa, Comanda comanda){
		if(historicoMesas.containsKey(idMesa)){
			ArrayList<Comanda> comandasMesa = historicoMesas.get(idMesa);
			comandasMesa.add(comanda);
		}else{
			ArrayList<Comanda> comandasMesas = new ArrayList<Comanda>();
			comandasMesas.add(comanda);
		}
	}

}
