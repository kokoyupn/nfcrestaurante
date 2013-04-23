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
	/*
	 * FIXME Quitar cuando añadamos a la base de datos el campo oreintación de cada foto.
	 */
	private String restaurante;
	
	public MiListTabsSuperioresCategoriasAdapter(Context context, ArrayList<PadreListTabsSuperioresCategorias> informacionPlatosLista, String restaurante) {
		this.inflater = LayoutInflater.from(context);
		this.informacionPlatosLista = informacionPlatosLista;
		this.restaurante = restaurante;
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
			/*
			 * FIXME Quitar cuando se introduzca el nuevo campo a la base de datos, orientación
			 * imágen.
			 */
			if(infoPlato.getTieneImagen()){
				if(restaurante.equals("Foster")){
					convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_con_imagen_horizontal, null);
				}else{
					convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_con_imagen_vertical, null);
				}
			}else{
				convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_sin_imagen, null);
			}
		}
		
		if(infoPlato.getTieneImagen()){
			/*
			 * FIXME Hay que añadir un campo extra a la base de datos de platos, para indicar
			 * la posición de la foto (horizontal, vertical).
			 */
			ImageView imageViewPlatoFoster;
			ImageView imageViewPlatoVips;
			if(restaurante.equals("Foster")){
				// Damos valor a los textview correspondientes del layout
				TextView textViewNombrePlato = (TextView) convertView.findViewById(R.id.textViewNombrePlatoListaTabsSuperiorConImagenHorizontal);
				textViewNombrePlato.setText(infoPlato.getNombrePlato());
				TextView textViewDescripcionPlato = (TextView) convertView.findViewById(R.id.textViewDescripcionPlatoListaTabsSuperiorConImagenHorizontal);
				textViewDescripcionPlato.setText(infoPlato.getDescripcionBreve());
				
				imageViewPlatoFoster = (ImageView) convertView.findViewById(R.id.imageViewPlatoListaTabsSuperiorConImagenHorizontal);
				imageViewPlatoFoster.setImageResource(infoPlato.getImagenPlato());
			}else{
				// Damos valor a los textview correspondientes del layout
				TextView textViewNombrePlato = (TextView) convertView.findViewById(R.id.textViewNombrePlatoListaTabsSuperiorConImagenVertical);
				textViewNombrePlato.setText(infoPlato.getNombrePlato());
				TextView textViewDescripcionPlato = (TextView) convertView.findViewById(R.id.textViewDescripcionPlatoListaTabsSuperiorConImagenVertical);
				textViewDescripcionPlato.setText(infoPlato.getDescripcionBreve());
				
				imageViewPlatoVips = (ImageView) convertView.findViewById(R.id.imageViewPlatoListaTabsSuperiorConImagenVertical);
				imageViewPlatoVips.setImageResource(infoPlato.getImagenPlato());
			}
			/*************************************************************************/
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

