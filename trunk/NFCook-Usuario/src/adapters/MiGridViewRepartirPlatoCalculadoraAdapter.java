package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.nfcook.R;

/**
 * Clase encargada de implementar el adapter de la ventana emergente que sale cuando
 * pinchamos sobre la imágen de un plato para proceder a repartirlo.
 * 
 * En concreto, se trata de un gridview de checkbox con el nombre de cada de los comensales.
 * Los checkbox que marquemos serán los comensales a los que se les asignará dicho plato
 * y se le cargará a su cuenta.
 * 
 * @author Abel
 *
 */
public class MiGridViewRepartirPlatoCalculadoraAdapter extends BaseAdapter{

	private ArrayList<PadreGridViewCalculadora> personas;
	private LayoutInflater l_Inflater;
	private ArrayList<ArrayList<Boolean>> personasMarcadoPlato; 
	private int posPlato;
	
	public MiGridViewRepartirPlatoCalculadoraAdapter(Context context, ArrayList<PadreGridViewCalculadora> personas, ArrayList<ArrayList<Boolean>> personasMarcadoPlato, int posPlato) {
		this.personas = personas;
		this.l_Inflater = LayoutInflater.from(context);
		this.personasMarcadoPlato = personasMarcadoPlato;
		this.posPlato = posPlato;
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
				if(checkBoxNombrePersona.isChecked()){
					MiViewPagerAdapter.marcaCheckBox(posPlato,pos);
				}else{
					MiViewPagerAdapter.desmarcaCheckBox(posPlato,pos);
				}
			}
		});
		
		// Damos valor
		if(personasMarcadoPlato.get(posPlato).get(pos)){
			checkBoxNombrePersona.setChecked(true);
		}else{
			checkBoxNombrePersona.setChecked(false);
		}
		checkBoxNombrePersona.setText(personas.get(position).getNombre());

		return convertView;
	}

}
