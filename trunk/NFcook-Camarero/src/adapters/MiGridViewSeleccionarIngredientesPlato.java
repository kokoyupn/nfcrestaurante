package adapters;

import java.util.ArrayList;

import com.example.nfcook_camarero.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;


/**
 * Clase encargada de implementar el adapter de la ventana emergente que sale cuando
 * pinchamos sobre el botón "Ingredientes" para verlos y/o modificarlos.
 * 
 * En concreto, se trata de un gridview de checkbox con el nombre de cada uno de los ingredientes
 * del plato.
 * Los checkbox que marquemos serán los ingredientes que incluirá dicho plato.
 * 
 * @author Alex Villapalos
 *
 */
public class MiGridViewSeleccionarIngredientesPlato extends BaseAdapter{

	private ArrayList<String> ingredientes;
	private LayoutInflater l_Inflater;
	private ArrayList<Boolean> ingredientesMarcado;
	
	public MiGridViewSeleccionarIngredientesPlato(Context context, ArrayList<String> ingredientes, ArrayList<Boolean> ingredienteMarcado) {
		this.ingredientes = ingredientes;
		this.l_Inflater = LayoutInflater.from(context);
		this.ingredientesMarcado = ingredienteMarcado;
	}

	public int getCount() {
		return ingredientes.size();
	}

	public Object getItem(int arg0) {
		return ingredientes.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.padre_grid_seleccion_ingrediente, null);
		}
		
		final int pos = position;
		// Obtenemos todos los campos de texto para darles valor
		final CheckBox checkBoxNombreIngrediente = (CheckBox) convertView.findViewById(R.id.checkBoxIngrediente);
		checkBoxNombreIngrediente.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(checkBoxNombreIngrediente.isChecked()){
					ingredientesMarcado.set(pos, true);
				}else{
					ingredientesMarcado.set(pos, false);
				}
			}
		});
		
		// Damos valor
		if(ingredientesMarcado.get(pos)){
			checkBoxNombreIngrediente.setChecked(true);
		}else{
			checkBoxNombreIngrediente.setChecked(false);
		}
		checkBoxNombreIngrediente.setText(ingredientes.get(position));

		return convertView;
	}

}

