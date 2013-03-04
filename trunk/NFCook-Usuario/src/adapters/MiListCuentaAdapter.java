package adapters;

import java.util.ArrayList;

import com.example.nfcook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Configura el adapter de la List de la pantalla cuenta.
 * 
 * -Atributos-
 * inflater             : necesario para poder recoger los XML pertenecientes a dichas listas.
 * padresExpandableList : ArrayList de los padres de la lista, guardan el nombre del plato, el precio
 * 						  por unidad, el número de unidades y el total.
 * @author Abel
 *
 */
public class MiListCuentaAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	
	private ArrayList<PadreListCuenta> padresCuenta;
	
	public MiListCuentaAdapter(Context context, ArrayList<PadreListCuenta> padresCuenta) {
		this.inflater = LayoutInflater.from(context);
		this.padresCuenta = padresCuenta;
	}
	
	public int getCount() {
		return padresCuenta.size();
	}
	
	public PadreListCuenta getItem(int position) {
		return padresCuenta.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	/*
	 * Método encargado de generar la vista de cada padre de la listview de cuenta, que hemos diseñado
	 * a nuestro gusto.
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_lista_cuenta, null);
		}
		
		// Damos valor a los textview correspondientes del layout
		PadreListCuenta padre = padresCuenta.get(position);
		TextView nombrePlato =  (TextView) convertView.findViewById(R.id.textViewNombrePlatoCuenta);
		nombrePlato.setText(padre.getPlato());
		TextView precioTotal =  (TextView) convertView.findViewById(R.id.textViewPrecioPlatoCuenta);
		precioTotal.setText(padre.getPrecioTotal() + " €");
		TextView cantidad =  (TextView) convertView.findViewById(R.id.textViewUnidadesCuenta);
		cantidad.setText("Uds: " + padre.getCantidad());
		
		return convertView;
	}
}
