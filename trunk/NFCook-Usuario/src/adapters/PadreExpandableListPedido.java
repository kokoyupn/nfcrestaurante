package adapters;
import java.util.ArrayList;

public class PadreExpandableListPedido {
    private String titulo;
    private ArrayList<HijoExpandableListPedido> arrayHijos;
 
    
    
    public PadreExpandableListPedido(String titulo, ArrayList<HijoExpandableListPedido> arrayHijos) {
		this.titulo = titulo;
		this.arrayHijos = arrayHijos;
	}

	public String getTitle() {
        return titulo;
    }
 
    public void setTitle(String titulo) {
        this.titulo = titulo;
    }
 
    public ArrayList<HijoExpandableListPedido> getArrayChildren() {
        return arrayHijos;
    }
    
    public HijoExpandableListPedido getHijoAt(int i) {
        return arrayHijos.get(i);
    }
 
    public void setArrayChildren(ArrayList<HijoExpandableListPedido> arrayHijos) {
        this.arrayHijos = arrayHijos;
    }
    
    public int getSize(){
    	return arrayHijos.size();
    }
    
    public String toString(){
    	return titulo;
    }

	public void actualizaHijos() {
		for(int i = 0; i<arrayHijos.size();i++){
			if(arrayHijos.get(i).isCheck()){
				arrayHijos.remove(i);
			}
		}
		
	}
}