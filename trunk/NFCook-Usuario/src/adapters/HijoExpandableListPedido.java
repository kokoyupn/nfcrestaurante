package adapters;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Configura los hijos del adapter de la ExpandableList de la pantalla de pedido, cada hijo sera un plato
 * configurado.
 * 
 * -Atributos-
 * observaciones : almacena el texto escrito de un usuario.
 * extras        : almacena los extras seleccionados por el usuario.
 * id            : campo necesario para hacer modificaciones sobre la base de datos.
 * precio        : precio correspondiente a ese plato.
 * 
 * @author Prado
 *
 */
public class HijoExpandableListPedido {
	private String ingredientes, extras, id;
	private double precio;
	private int numeroDeConfiguraciones;
	private double precioUnidad;
	private ArrayList<String> idsUnicos; // Si hay mas de una configuracion igual necesitamos guardar sus ids unicos para despues poder modificar la base de datos.
	
	public HijoExpandableListPedido(String ingredientes, String extras, double precio, String id) {
		this.ingredientes = ingredientes;
		this.extras = extras;
		this.precio = precio;
		precioUnidad = precio;
		this.id = id;
		numeroDeConfiguraciones = 1;
		idsUnicos = new ArrayList<String>();
		idsUnicos.add(id);
	}


	public String getIngredientes() {
		return ingredientes;
	}


	public String getExtras() {
		return extras;
	}

	public double getPrecio() {
		return precio;
	}

	public String getId() {
		return id;
	}

	public int getNumeroDeConfiguraciones() {
		return numeroDeConfiguraciones;
	}
	
	public void incrementaNumeroDeConfiguraciones(){
		numeroDeConfiguraciones++;
		precio +=precioUnidad;
	}
	
	public void decrementaNumeroDeConfiguraciones(){
		numeroDeConfiguraciones--;
		precio -=precioUnidad;

	}


	public void setExtrasIng(String extras, String ingredientes) {
		this.extras = extras;
		this.ingredientes = ingredientes;
	}

	public static boolean existeHijoIgualEnArray(ArrayList<HijoExpandableListPedido> hijos, HijoExpandableListPedido hijoAbuscar) {
		Iterator<HijoExpandableListPedido> itHijos = hijos.iterator();
		while(itHijos.hasNext()){
			HijoExpandableListPedido hijoEnArray = itHijos.next();
			if(hijoEnArray.equals(hijoAbuscar)){
				hijoEnArray.incrementaNumeroDeConfiguraciones();
				hijoEnArray.idsUnicos.add(hijoAbuscar.id);
				return true;
			}
		}
		return false;
	}
	
	public String getPrimerIdUnicoParaModificar(){
		String idUnico = idsUnicos.get(0);
		return idUnico;
	}
	
	public void eliminaPrimerIdUnico(){
		if(idsUnicos.size()>1){
			id = idsUnicos.get(1); // El idUnico general siempre es el primero del array
			idsUnicos.remove(0);
		}
	}
	
	public boolean equals(Object obj){
		if(obj == this) return true;
		if(!(obj instanceof HijoExpandableListPedido)) return false;
		HijoExpandableListPedido hijo = (HijoExpandableListPedido) obj;
		
		Boolean extrasIdem = false;
		Boolean obsIdem = false;
		
		if(extras == null && hijo.extras == null){
			extrasIdem = true;
		}else{
			extrasIdem = extras.equals(hijo.extras);
		}
		if(ingredientes == null && hijo.ingredientes == null){
			obsIdem = true;
		}else if (ingredientes != null && hijo.ingredientes != null){
			obsIdem = ingredientes.equals(hijo.ingredientes);
		}
		
		return extrasIdem && obsIdem;
	}


	public double getPrecioUnidad() {
		return precioUnidad;
	}
	
}
