package adapters;

import java.util.ArrayList;

import com.example.nfcook_gerente.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author: Alejandro Moran
 * 
 * Esta clase contiene el adapter para la información 
 * detallada de un restaurante seleccionado.
 * 
 * **/
public class InfoRestauranteAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<PadreInfoRestaurante> contenido;
	
	public InfoRestauranteAdapter(Activity activity, ArrayList<PadreInfoRestaurante> contenido) {
		this.activity = activity;
		this.contenido = contenido;
	}
	
	public int getCount() {
		return contenido.size();
	}
	
	public Object getItem(int pos) {
		return contenido.get(pos);
	}
	
	@Override
	public long getItemId(int position) {
		return contenido.get(position).getId();
	}
	
	
	@Override
	public View getView(int posicion, View convertView, ViewGroup padre) {
		View vista = convertView;
		
		if (vista == null){
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vista = inflater.inflate(R.layout.contenido_lista_informacion, null);
		} 
		
		PadreInfoRestaurante lista = contenido.get(posicion);
        
	    TextView nombre = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombre.setText(lista.getNombreRestaurante());
	    
	    // añadir a la lista imagen del logo del restaurante o de la fachada
	    
	    return vista;
	    
	}

}
