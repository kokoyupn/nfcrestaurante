package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook.R;

/**
 * Clase encargada de implementar el adapter de la lista expandible de platos que saldrá en el caso
 * de que la categoría de platos que hayamos seleccionado tenga varios tipos de platos (Ejem
 * categoría principal y dentro tenemos: hamburguesas, pastas, etc). El padre sería el tipo y los hijos
 * el conjunto de platos que agrupe.
 * 
 * @author Abel
 *
 */
public class MiExpandableListTabsSuperioresCategoriasAdapter extends BaseExpandableListAdapter{	
	private LayoutInflater inflater;
    private ArrayList<PadreExpandableListTabsSuperioresCategorias> tiposPlatoEnCategoria;
    
    public MiExpandableListTabsSuperioresCategoriasAdapter(Context context, ArrayList<PadreExpandableListTabsSuperioresCategorias> tiposPlatoEnCategoria){
    	this.tiposPlatoEnCategoria = tiposPlatoEnCategoria;
        inflater = LayoutInflater.from(context);
    }

	public PadreListTabsSuperioresCategorias getChild(int groupPosition, int childPosition) {
		return tiposPlatoEnCategoria.get(groupPosition).getPlatoEnTipo(childPosition);
	}
	
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		PadreListTabsSuperioresCategorias infoPlato = tiposPlatoEnCategoria.get(groupPosition).getPlatoEnTipo(childPosition);
		// Miramos si el plato tiene imágen para cargar una vista u otra
		if(infoPlato.getTieneImagen()){
			convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_con_imagen, null);
		}else{
			convertView = inflater.inflate(R.layout.padre_lista_tabs_superiores_categorias_sin_imagen, null);
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
	
	/**
	 * Número de hijos dentro de un padre.
	 * @param groupPosition
	 * @return
	 */
	public int getChildrenCount(int groupPosition) {
		return tiposPlatoEnCategoria.get(groupPosition).getNumPlatosEnTipo();
	}

	/**
	 * Devuelve un padre pada una posicion.
	 * @param groupPosition
	 * @return
	 */	
	public Object getGroup(int groupPosition) {
		return tiposPlatoEnCategoria.get(groupPosition);
	}

	/**
	 * Número de padres en una lista.
	 * @return
	 */
	public int getGroupCount() {
		return tiposPlatoEnCategoria.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	/**
	 * La llamada a este método se produce cada vez que se muestra la pantalla de la lista.
	 * Configura la vista de cada uno de los padres de la lista.
	 * @param groupPosition
	 * @param isExpanded
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_lista_expandible_tabs_superiores_categorias, parent,false);
        }
		
		TextView textViewTipoPlatos = (TextView) convertView.findViewById(R.id.textViewTipoPlatoListaExpandibleTabsSuperiorConImagen);
		textViewTipoPlatos.setText(tiposPlatoEnCategoria.get(groupPosition).getTipoPlato());
		return convertView;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}

