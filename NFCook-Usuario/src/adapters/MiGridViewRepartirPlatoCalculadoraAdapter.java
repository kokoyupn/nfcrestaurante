package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.nfcook.R;

public class MiGridViewRepartirPlatoCalculadoraAdapter extends BaseAdapter{

	private ArrayList<PadreGridViewCalculadora> personas;
	private LayoutInflater l_Inflater;
	private boolean[][] marcados; 
	private int posPlato;
	//private Context context;
	//private ArrayList<String> platos;
	
	public MiGridViewRepartirPlatoCalculadoraAdapter(Context context, ArrayList<PadreGridViewCalculadora> personas, boolean[][] marcados, int posPlato) {
		this.personas = personas;
		this.l_Inflater = LayoutInflater.from(context);
		this.marcados = marcados;
		this.posPlato = posPlato;
		
		//this.context = context;
	}

	public int getCount() {
		return personas.size();
	}

	public Object getItem(int arg0) {
		return personas.get(arg0);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.padre_grid_reparto_platos_calculadora, null);
		}
		
		final int pos = position;
		// Obtenemos todos los campos de texto para darles valor
		final CheckBox checkBoxNombrePersona = (CheckBox) convertView.findViewById(R.id.checkBoxPersonaRepartoCalculadora);
		checkBoxNombrePersona.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.i("ONCLICK CHECKBOX","BIENNNNN");
				if(checkBoxNombrePersona.isChecked()){
					MiViewPagerAdapter.marcaCheckBox(posPlato,pos);
				}else{
					MiViewPagerAdapter.desmarcaCheckBox(posPlato,pos);
				}
			}
		});
		
		// Damos valor
		if(marcados[posPlato][pos]){
			checkBoxNombrePersona.setChecked(true);
		}else{
			checkBoxNombrePersona.setChecked(false);
		}
		checkBoxNombrePersona.setText(personas.get(position).getNombre());

		return convertView;
	}

}
