package adapters;
import java.util.ArrayList;

/**
 * Configura los padres del adapter de la ExpandableList de la pantalla pedido,cada padre sera el nombre
 * correspondiente a un plato.
 * 
 * -Atributos-
 * titulo                  : almacena el nombre de un plato.
 * idPlato                 : campo necesario para hacer modificaciones sobre la base de datos.
 * precio                  : indica el dinero total a gastar en ese plato, se han podido pedir varios platos iguales.
 * expandido               : indica si estaba expandido o no, de esta forma podremos mostrarlo así tras una edición.
 * configuracionesPlato    : conjunto de diferentes configuraciones acerca de un plato.
 * 
 * @author Prado
 *
 */
public class PadreExpandableListPedido {
    private String titulo;
    private String idPlato;
    private double precio;
    private boolean expandido;
    private ArrayList<HijoExpandableListPedido> configuracionesPlato;
 
    
    
    public PadreExpandableListPedido(String titulo, ArrayList<HijoExpandableListPedido> configuracionesPlato, double precio, String idPlato) {
		this.titulo = titulo;
		this.configuracionesPlato = configuracionesPlato;
		this.precio = precio;
		this.idPlato = idPlato;
	}

	public String getTitle() {
        return titulo;
    }
 
    public void setTitle(String titulo) {
        this.titulo = titulo;
    }
    
    public String getIdPlato() {
		return idPlato;
	}

	public double getPrecio() {
		return precio;
	}

	public ArrayList<HijoExpandableListPedido> getArrayChildren() {
        return configuracionesPlato;
    }
    
    public HijoExpandableListPedido getHijoAt(int i) {
        return configuracionesPlato.get(i);
    }
 
    public void setArrayChildren(ArrayList<HijoExpandableListPedido> arrayHijos) {
        this.configuracionesPlato = arrayHijos;
    }
    
    public int getSize(){
    	return configuracionesPlato.size();
    }
    
    public String toString(){
    	return titulo;
    }
    

	public boolean isExpandido() {
		return expandido;
	}
	
	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}

	public boolean eliminaHijo(int posicionHijo) {
		if(configuracionesPlato.get(posicionHijo).getNumeroDeConfiguraciones() == 1){
			configuracionesPlato.remove(posicionHijo);
		}else{
			configuracionesPlato.get(posicionHijo).decrementaNumeroDeConfiguraciones();
		}
		if(configuracionesPlato.isEmpty()){
			return true; //Si la lista de hijos es vacia avisamos para eliminar el padre el padre
		}
		return false;
	}

	public void addHijo(HijoExpandableListPedido nuevoHijo) {
		configuracionesPlato.add(nuevoHijo);
	}

	public void restaAlPrecioUnaUnidad(int posicionHijo) {
		precio -=configuracionesPlato.get(posicionHijo).getPrecioUnidad();		
	}
	
}