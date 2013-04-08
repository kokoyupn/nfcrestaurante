package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook.R;

/**
 * Clase encargada de implementar el adapter de la lista de platos que saldrá en el caso de que la categoría
 * de platos que hayamos seleccionado tenga un único tipo de platos. La información que se mostrará será
 * el nombre del plato, junto con una breve descripción y si tiene imágen también saldrá.
 * 
 * @author Abel
 *
 */

public class MiListTabsSuperioresCategoriasAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<PadreListTabsSuperioresCategorias> informacionPlatosLista;
	
	public MiListTabsSuperioresCategoriasAdapter(Context context, ArrayList<PadreListTabsSuperioresCategorias> informacionPlatosLista) {
		this.inflater = LayoutInflater.from(context);
		this.informacionPlatosLista = informacionPlatosLista;
	}
	
	public int getCount() {
		return informacionPlatosLista.size();
	}
	
	public PadreListTabsSuperioresCategorias getItem(int position) {
		return informacionPlatosLista.get(position);
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
		PadreListTabsSuperioresCategorias infoPlato = informacionPlatosLista.get(position);
		if (convertView == null) {
			if(infoPlato.getTieneImagen()){
				convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_con_imagen, null);
			}else{
				convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_sin_imagen, null);
			}
		}
		
		if(infoPlato.getTieneImagen()){
			// Damos valor a los textview correspondientes del layout
			TextView textViewNombrePlato = (TextView) convertView.findViewById(R.id.textViewNombrePlatoListaTabsSuperiorConImagen);
			textViewNombrePlato.setText(infoPlato.getNombrePlato());
			TextView textViewDescripcionPlato = (TextView) convertView.findViewById(R.id.textViewDescripcionPlatoListaTabsSuperiorConImagen);
			textViewDescripcionPlato.setText(infoPlato.getDescripcionBreve());
			ImageView imageViewPlato = (ImageView) convertView.findViewById(R.id.imageViewPlatoListaTabsSuperiorConImagen);
			imageViewPlato.setImageResource(infoPlato.getImagenPlato());
			
		}else{
			// Damos valor a los textview correspondientes del layout
			TextView textViewNombrePlato = (TextView) convertView.findViewById(R.id.textViewNombrePlatoListaTabsSuperiorSinImagen);
			textViewNombrePlato.setText(infoPlato.getNombrePlato());
			TextView textViewDescripcionPlato = (TextView) convertView.findViewById(R.id.textViewDescripcionPlatoListaTabsSuperiorSinImagen);
			textViewDescripcionPlato.setText(infoPlato.getDescripcionBreve());
		}
		
		return convertView;
	}
}

