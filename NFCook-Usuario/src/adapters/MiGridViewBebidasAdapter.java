package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook.R;

import fragments.ContenidoTabSuperiorCategoriaBebidas;

/**
 * Configura el adapter del gridview de la pantalla selección de bebidas.
 * 
 * -Atributos-
 * inflater             : necesario para poder recoger los XML pertenecientes a dichas listas.
 * context				: contexto de la actividad que lo crea
 * bebidas				: información de todas las bebidas que hay que mostrar. La información
 * 						  incluye el nombre, precio, número de unidades seleccionadas, etc.
 * @author abel
 *
 */
public class MiGridViewBebidasAdapter extends BaseAdapter{
	private ArrayList<PadreGridViewBebidas> bebidas;
	private LayoutInflater l_Inflater;
	private Context context;

	public MiGridViewBebidasAdapter(Context context, ArrayList<PadreGridViewBebidas> bebidas) {
		this.bebidas = bebidas;
		this.l_Inflater = LayoutInflater.from(context);
		this.context = context;
	}

	public int getCount() {
		return bebidas.size();
	}

	public Object getItem(int arg0) {
		return bebidas.get(arg0);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.padre_grid_bebidas, null);
		}
			
		// Obtenemos todos los textview para darles valor
		TextView textViewNombre = (TextView) convertView.findViewById(R.id.textViewNombreBebida);
		TextView textViewUnidades = (TextView) convertView.findViewById(R.id.textViewUnidadesBebida);
		TextView textViewTotal = (TextView) convertView.findViewById(R.id.textViewPrecioBebida);
		
		// Damos valor a cada textview
		textViewNombre.setText(bebidas.get(position).getNombre());
		textViewUnidades.setText("Uds: " + bebidas.get(position).getUnidades());
		textViewTotal.setText(bebidas.get(position).getPrecioTotal() + " €");
		
		// Asignamos la imagen de la bebida
		ImageView imagenBebida = (ImageView) convertView.findViewById(R.id.imageViewBebida);
		imagenBebida.setImageResource(context.getResources().getIdentifier(bebidas.get(position).getRutaFoto(),"drawable",context.getPackageName()));	

		// Guardamos la posición de cada uno
		final int pos = position;
		
		// Hacemos oyente a la imagen de añadir bebida
		ImageView imagenAnyadirBebida = (ImageView) convertView.findViewById(R.id.imageViewAnyadirBebida);
		imagenAnyadirBebida.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				ContenidoTabSuperiorCategoriaBebidas.anyadirBebida(pos);
				ContenidoTabSuperiorCategoriaBebidas.actualizaGridView();
			}});
		
		// Hacemos oyente a la imagen de añadir bebida
		ImageView imagenEliminarBebida = (ImageView) convertView.findViewById(R.id.imageViewEliminarBebida);
		imagenEliminarBebida.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				ContenidoTabSuperiorCategoriaBebidas.eliminarBebida(pos);
				ContenidoTabSuperiorCategoriaBebidas.actualizaGridView();
			}});
		
		return convertView;
	}
}
