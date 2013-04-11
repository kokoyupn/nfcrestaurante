package adapters;

import java.util.ArrayList;

import com.example.nfcook.R;

import usuario.Restaurante;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListaMapasAdapter extends BaseAdapter {
	private ArrayList<Restaurante> restaurantes;
	private LayoutInflater l_Inflater;
	
	public ListaMapasAdapter(Context context, ArrayList<Restaurante> r)
	{
		this.l_Inflater = LayoutInflater.from(context);
		restaurantes = r;
	}
	
	public int getCount() {
		return restaurantes.size();
	}

	public Object getItem(int position) {
		return restaurantes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
    	if (convertView == null) {
    		convertView = l_Inflater.inflate(R.layout.interior_list_view_mapas, null);        
        }
    	Restaurante restActual = restaurantes.get(position);
    	TextView nombreRest = (TextView) convertView.findViewById(R.id.textViewListaMapaNombre);
    	nombreRest.setText(restActual.getNombre());
    	TextView distancia = (TextView) convertView.findViewById(R.id.textViewListaMapaDistancia);
    	if (restActual.getDistancia() >= 1){
    		double distEnKm = Math.rint((restActual.getDistancia()*100))/100;
        	distancia.setText("A " + distEnKm + " km");
    	}else{
    		int distEnMetros = (int) Math.rint((restActual.getDistancia()*1000));
    		distancia.setText("A " + distEnMetros + " m");
    	}
        return convertView;
	}
}