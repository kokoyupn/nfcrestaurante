package adapters;

import java.util.ArrayList;
import java.util.Iterator;

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
		while(!marcados[posicion]){
			posicion++;
		}
		return nombresExtras.get(posicion);
	}
	

}
