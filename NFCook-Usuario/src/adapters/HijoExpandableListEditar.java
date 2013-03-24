package adapters;

import java.util.ArrayList;

/**
 * Configura los hijos del adapter de la ExpandableList de la pantalla de edición de un plato, cada hijo sera
 * un RadioGroup con los extras posibles.
 * 
 * -Atributos-
 * nombresExtras : ArrayList con los nombres de los extras correspondientes a un padre de la lista.
 * marcados      : Array de booleanos que indica que extra está marcado en la lista.
 * 
 * @author Prado
 */
public class HijoExpandableListEditar {
	
	private ArrayList<String> nombresExtras;
	private boolean[] marcados; 

	public HijoExpandableListEditar(ArrayList<String> nombresExtras, boolean[] marcados) {
		this.nombresExtras = nombresExtras;
		this.marcados = marcados;
	}

	public int getSize() {
		return nombresExtras.size();
	}

	public String getExtraAt(int posicionExtra) {
		return nombresExtras.get(posicionExtra);
	}

	public boolean isChecked(int posicion) {
		return marcados[posicion];
	}
	
	public void setCheck(int posicion){
		for(int i = 0; i<marcados.length;i++){
			marcados[i] = false;
		}
		marcados[posicion] = true;
	}
	
	public String getExtraMarcado(){
		int posicion = 0;
		while( posicion < marcados.length && !marcados[posicion]){
			posicion++;
		}
		if(posicion == marcados.length){
			return null;
		} else{
			return nombresExtras.get(posicion);
		}
	}
	
	/**
	 * Devuelve en forma de String los extras marcados con 1 o 0 segun esten marcados o no
	 * @return
	 */
	public String getExtrasBinarios(){
		String extrasBinarios = "";
		int posicion = 0;
		while(posicion < marcados.length){
			if (marcados[posicion])
				extrasBinarios += "1"; 
			else 
				extrasBinarios += "0";
			posicion++;
		}
		return extrasBinarios;
	}
	

}
