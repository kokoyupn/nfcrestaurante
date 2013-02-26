package adapters;
import java.util.ArrayList;

public class PadreExpandableListPedido {
    private String titulo;
    private double precio;
    private ArrayList<HijoExpandableListPedido> arrayHijos;
 
    
    
    public PadreExpandableListPedido(String titulo, ArrayList<HijoExpandableListPedido> arrayHijos, double precio) {
		this.titulo = titulo;
		this.arrayHijos = arrayHijos;
		this.precio = precio;
	}

	public String getTitle() {
        return titulo;
    }
 
    public void setTitle(String titulo) {
        this.titulo = titulo;
    }
 
    public double getPrecio() {
		return precio;
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
				precio -= arrayHijos.get(i).getPrecio();
			}
		}
	}
	
	public boolean esPadreVacio(){
		return arrayHijos.size()==0;
	}

	public boolean algunHijoMarcado() {
		int posicionHijo = 0;
		boolean marcado = false;
		while(posicionHijo<arrayHijos.size() && !marcado){
			marcado = arrayHijos.get(posicionHijo).isCheck();
		}
		return marcado;
	}
}