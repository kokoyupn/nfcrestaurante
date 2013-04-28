package adapters;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.nfcook_camarero.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * Configura el adapter de la List de la pantalla del pedido de la mesa.
 * 
 * -Atributos-
 * activity: Actividad que lo lanza.
 * contenido : ArrayList con los platos que tiene pedidos la mesa en concreto.
 * 
 * @author Rober
 *
 */

public class MiListAdapterMesa extends BaseAdapter {
	 private Activity activity;
	 protected ArrayList<ContenidoListMesa> contenido;
	 
	 public MiListAdapterMesa(Activity activity, ArrayList<ContenidoListMesa> contenido) {
		 this.activity = activity;
		 this.contenido = contenido;
	}
	 
	public int getCount() {
		return contenido.size();
	}
	
	public Object getItem(int pos) {
		return contenido.get(pos);
	}
	
	public long getItemId(int pos) {
		return contenido.get(pos).getId();
	}
	
	public View getView(int posicion, View convertView, ViewGroup padre) {
		View vista=convertView;
        
	    if(convertView == null) {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vista = inflater.inflate(R.layout.contenido_lista_mesa, null);
	    }
	             
	    ContenidoListMesa lista = contenido.get(posicion);
	         
	    TextView nombre = (TextView) vista.findViewById(R.id.nombrePlato);
	    nombre.setText(lista.getNombre());
	    
	    TextView unidades = (TextView) vista.findViewById(R.id.unidades);
	    unidades.setText("Uds: " + lista.getCantidad());
	    
	    TextView precio = (TextView) vista.findViewById(R.id.precio);
	    double aux = lista.getPrecio();
	    precio.setText(Double.toString( Math.rint(aux*100)/100 )+" "+"€");

	    TextView extras = (TextView) vista.findViewById(R.id.extras);
	    extras.setText(lista.getExtras());
	         
	    TextView observaciones = (TextView) vista.findViewById(R.id.observaciones);
	    observaciones.setText(lista.getObservaciones());    
	    
	    return vista;
	    }
	
	public void deleteId(int id){
		Iterator<ContenidoListMesa> it = contenido.iterator();
		System.out.println("entra");
		boolean encontrado = false;
		int pos = 0;
		while(it.hasNext() && !encontrado){
			ContenidoListMesa m = it.next();
			
			if(m.getId() == id)
				encontrado = true;
			else
				pos++;
		}
		
		if(encontrado)
			contenido.remove(pos);
	}
	
	
	public double getPrecio() {
		double precio = 0;
		for (int i=0;i<contenido.size();i++)
			precio = precio + contenido.get(i).getPrecio();
		
		System.out.println(precio);
		return precio;
	}
	
	
	public void deletePosicion(int posicion){
		contenido.get(posicion).restaCantidad();
		contenido.get(posicion).eliminaId();
		if(contenido.get(posicion).getCantidad()==0)
			contenido.remove(posicion);
	}
	
	

	public String getExtrasMarcados(int posicion) {
		return contenido.get(posicion).getExtras();
	}

	public String getIdPlato(int posicion) {
		return contenido.get(posicion).getIdPlato();
	}
	
	public int getIdPlatoUnico(int posicion) {
		return contenido.get(posicion).getId();
	}

	public String getNombrePlato(int posicion) {
		return contenido.get(posicion).getNombre();
	}

	public String getObservacionesPlato(int posicion) {
		return contenido.get(posicion).getObservaciones();	
	}

	public void setExtras(int posicion,String extras) {
		contenido.get(posicion).setExtras(extras);
	}

	public void setObservaciones(int posicion, String observacionesNuevas) {
		contenido.get(posicion).setObservaciones(observacionesNuevas);		
	}

	public void addPlato(ContenidoListMesa platoNuevo) {
		contenido.add(platoNuevo);
	}

	public View getViewOnScreen(int itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
