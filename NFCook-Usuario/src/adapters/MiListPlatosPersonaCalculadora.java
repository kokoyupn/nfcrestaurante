package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nfcook.R;

/**
 * Clase encargada de implementar el adapter de la lista de platos que tiene cada comensal
 * en la pantalla calculadora.
 * 
 * @author Abel
 *
 */
public class MiListPlatosPersonaCalculadora extends BaseAdapter {
        private ArrayList<InformacionPlatoCantidad> platos;
        private LayoutInflater l_Inflater;

        public MiListPlatosPersonaCalculadora(Context context, ArrayList<InformacionPlatoCantidad> platos) {
        	this.platos = platos;
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
        	if (convertView == null) {
        		convertView = l_Inflater.inflate(R.layout.padre_lista_platos_calculadora, null);
        	}
        	
        	// Sacamos los textview y les damos valor
        	TextView textViewNombrePlato = (TextView) convertView.findViewById(R.id.textViewNombrePlatoCalculadora);
        	textViewNombrePlato.setText(platos.get(position).getNombrePlato());
        	
        	TextView textViewPorcionPlato = (TextView) convertView.findViewById(R.id.textViewPorcionPlatoCalculadora);
        	textViewPorcionPlato.setText(platos.get(position).getPorcion());
                
        	return convertView;
        }
}
