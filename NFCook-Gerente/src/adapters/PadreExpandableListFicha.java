package adapters;

/**
 * Clase encargada de contener todos los campos necesarios que mostrará la expandable list de la ficha de un
 * empleado. 
 * 
 * Los padres serán el tipo de información y los hijos serán los campos que almacena dicho tipo de información.
 * 
 * @author Abel
 *
 */
public class PadreExpandableListFicha {
	
	private String tipoDato;
	private HijoExpandableListFicha datos;
	
	public PadreExpandableListFicha(String tipoDato, HijoExpandableListFicha datos){
		this.tipoDato = tipoDato;
		this.datos = datos;
	}
	
	public String getTipoDato(){
		return tipoDato;
	}
	
	public HijoExpandableListFicha getDatos(){
		return datos;
	}

}

