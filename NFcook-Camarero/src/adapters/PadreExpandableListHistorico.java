package adapters;
import java.util.ArrayList;

/**
 * Configura los padres del adapter de la ExpandableList de la pantalla historico,cada padre sera una mesa
 * correspondiente a varios pedidos realizados.
 * 
 * -Atributos-
 * numMesa                 : campo necesario para ordenar la pantalla historico en función del numero de mesa.
 * precio                  : indica el precio total de una mesa,se han podido pedir varios pedidos iguales.
 * 
 * @author Busy
 *
 */
public class PadreExpandableListHistorico {
    private String numMesa;
    private double precio;
    private boolean expandido;
    private ArrayList<HijoExpandableListHistorico> hijos;
    
    
    public PadreExpandableListHistorico(String numMesa, double precio, ArrayList<HijoExpandableListHistorico> hijos) {
		this.numMesa = numMesa;
		this.precio = precio;
		this.hijos=hijos;
	}
    
    public boolean isExpandido() {
		return expandido;
	}
	
    public int getSize(){
    	return hijos.size();
    }
    
	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}
    
    public HijoExpandableListHistorico getHijoAt(int childPosition) {
		return hijos.get(childPosition);
	}

	public String getnumMesa() {
        return numMesa;
    }
 
    
	public double getPrecio() {
		return precio;
	}

	
    
	public void addHijo(HijoExpandableListHistorico nuevoHijo) {
		hijos.add(nuevoHijo);
	}

	
	
	
}