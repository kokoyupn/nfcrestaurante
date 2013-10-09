package adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nfcook_gerente.R;


public class ListInformacionAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<PadreListInformacion> contenido;
	
	public ListInformacionAdapter(Activity activity, ArrayList<PadreListInformacion> contenido) {
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
		
		PadreListInformacion lista = contenido.get(posicion);
        
	    TextView nombre = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombre.setText(lista.getNombreRestaurante());
	    
	    return vista;
	    
	}


}
