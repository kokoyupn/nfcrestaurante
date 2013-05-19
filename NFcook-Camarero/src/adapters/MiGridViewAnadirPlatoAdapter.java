package adapters;

import java.util.ArrayList;

import com.example.nfcook_camarero.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MiGridViewAnadirPlatoAdapter extends BaseAdapter {
	private ArrayList<InformacionPlato> platos;
	private LayoutInflater l_Inflater;
	private Context context;
	

	public MiGridViewAnadirPlatoAdapter(Context context, ArrayList<InformacionPlato> platos) {
		this.platos = platos;
		this.context = context;
		this.l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return platos.size();
	}

	public Object getItem(int position) {
		return platos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView nombrePl;
		ImageView imagenPlato;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.imagen_plato, null);
			//convertView.setLayoutParams (new GridView.LayoutParams (200, 200));
			
			nombrePl = (TextView) convertView.findViewById(R.id.textViewNombrePlato);
			nombrePl.setText(platos.get(position).getNombrePlato());
			convertView.setTag(nombrePl);
			
			imagenPlato = (ImageView) convertView.findViewById(R.id.imageViewPlato);
			imagenPlato.setImageResource(MiExpandableListAnadirPlatoAdapter.getDrawable(context,platos.get(position).getImagen()));
			
		} else {
			nombrePl = (TextView) convertView.getTag();
			nombrePl.setText(platos.get(position).getNombrePlato());
			
			imagenPlato = (ImageView) convertView.findViewById(R.id.imageViewPlato);
			imagenPlato.setImageResource(MiExpandableListAnadirPlatoAdapter.getDrawable(context,platos.get(position).getImagen()));
			
		}
		return convertView;
	}
	
}