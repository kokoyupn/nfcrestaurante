package adapters;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Configura los padres del adapter de la ExpandableList de la pantalla de edición de un plato,
 * cada padre sera el titulo de una categoria de extra.
 * 
 * -Atributos-
 * categoriaExtra : almacena el titulo de la categoría de extras a mostrar.
 * idPlato        : campo necesario para hacer modificaciones sobre la base de datos.
 * expandido      : indica si estaba expandido o no, de esta forma podremos mostrarlo así tras una edición.
 * hijosExtras    : conjunto de extras de una categoria.
 * 
 * @author Prado
 *
 */
public class PadreExpandableListEditar {
	private String categoriaExtra;
	private String idPlato;
	private boolean expandido;
	private ArrayList<HijoExpandableListEditar> hijosExtras;
	
	public PadreExpandableListEditar(String idPlato, String categoriaExtra,ArrayList<HijoExpandableListEditar> hijosExtras) {
		this.idPlato = idPlato;
		this.categoriaExtra = categoriaExtra;
		this.hijosExtras = hijosExtras;
	}

	public HijoExpandableListEditar getHijoAt(int childPosition) {
		return hijosExtras.get(childPosition);
	}

	public int getSize() {
		return hijosExtras.size();
	}

	public String getCategoriaExtra() {
		return categoriaExtra;
	}

	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}

	public boolean isExpandido() {
		return expandido;
	}
	
	public String getExtrasMarcados(){
		Iterator<HijoExpandableListEditar> it = hijosExtras.iterator();
		String extras = "";
		while(it.hasNext()){
			HijoExpandableListEditar unHijo = it.next();
			extras += unHijo.getExtraMarcado();
		}
		return extras;
	}
	
}
