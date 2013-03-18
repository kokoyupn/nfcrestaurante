package adapters;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.nfcook_camarero.Mesa;
import com.example.nfcook_camarero.MiExpandableListAdapterAnadirPlato;
import com.example.nfcook_camarero.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Configura el adapter de la ExpandableList de la pantalla edición de un plato.
 * En esta lista realmente solo vamos a tener un hijo por cada padre, que sera el RadioGroup contenedor
 * de los extras, perolo he implementado de tal forma que si en un futuro fuese necesario añadir nuevos hijos
 * no fuese necesario hacer modificaciones en el codigo.
 * 
 * -Atributos-
 * inflater  : necesario para poder recoger los XML pertenecientes a dichas listas.
 * padresExpandableList : ArrayList de padres de la lista (elementos sin expandir).
 * context : necesario para poder crear los RadioButtons para cada extra.
 * 
 * @author Prado
 *
 */
public class MiExpandableListAdapterEditar extends BaseExpandableListAdapter {
	
	private LayoutInflater inflater;
    private ArrayList<PadreExpandableListEditar> padresExpandableList;
    private Context context;
    private boolean esEditar;
    
    public MiExpandableListAdapterEditar(Context context, ArrayList<PadreExpandableListEditar> arrayCategorias, boolean esEditar){
    	this.context =  context;
    	this.padresExpandableList = arrayCategorias;
        inflater = LayoutInflater.from(context);
        this.esEditar = esEditar;
    }

	public MiExpandableListAdapterEditar() {
		this.context =  null;
    	this.padresExpandableList = null;
        inflater = null;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return padresExpandableList.get(groupPosition).getHijoAt(childPosition);
	}
	
	
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	/**
	 * La llamada a este método se produce cada vez que se expande un padre de la lista.
	 * Configura la vista de cada uno de los hijos de la lista.
	 * @param groupPosition : Posicion del padre en la Lista.
	 * @param childPosition : Posicion del hijo en la lista.
	 * @param isLastChild   : Nos dice si es el ultimo hijo de la lista.
	 * @param convertView   : View perteneciente al hijo, en caso de ser null seria la primera vez que lo mostramos.
	 * @param parent        : View perteneciente al padre.
	 * @return              : Devolvemos la vista modificada.
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null){	
			convertView = inflater.inflate(R.layout.hijo_lista_editar_y_anadir_plato, parent,false); // XML contenedor de los view a mostrar por cada hijo. 
		}	
		/*
		 * Necesitamos que sean final para que por cada hijo mostrado de la lista guardemos en que posición
		 * se encuentra, de esta forma podremos saber que RadioGroup modificar.
		 */
		final int groupPositionMarcar = groupPosition; 
		final int childPositionMarcar = childPosition;
		
		RadioGroup radioGroupExtras = (RadioGroup) convertView.findViewById(R.id.radioGroupEditarPlato);
		//Eliminamos todos los RadioButtons pertenecientes al RadioGroup por si se hubiesen modificado añadirlos de nuevo.
		radioGroupExtras.removeAllViews();
		
		//Recogemos el hijo situado en dicha posición de la lista.
		HijoExpandableListEditar hijoExpandableList = padresExpandableList.get(groupPosition).getHijoAt(childPosition);
		
		for(int i=0; i<hijoExpandableList.getSize();i++){
			RadioButton radioButtonExtra = new RadioButton(context);
			radioButtonExtra.setTextColor(Color.BLACK);
			radioButtonExtra.setText(hijoExpandableList.getExtraAt(i));
			//Almacenamos la posición de cada RadioButon para poder marcarlo en un futuro.
			final int posicionRadioButton = i;
			
			if(hijoExpandableList.isChecked(i)){
				radioButtonExtra.setChecked(true);
			}
			
			radioButtonExtra.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					padresExpandableList.get(groupPositionMarcar).getHijoAt(childPositionMarcar).setCheck(posicionRadioButton);
					if(esEditar){
						Mesa.actualizaExpandableList();
					}else{
						MiExpandableListAdapterAnadirPlato.actualizaExpandableList();
					}
					for(int i=0;i<padresExpandableList.size();i++){
						if(padresExpandableList.get(i).isExpandido()){
							if(esEditar){
								Mesa.expandeGrupoLista(i);
							}else{
								MiExpandableListAdapterAnadirPlato.expandeGrupoLista(i);
							}
						}
					}
				}
			});
			radioGroupExtras.addView(radioButtonExtra);
		}
	    return convertView;
	}

	/**
	 * Número de hijos dentro de un padre.
	 * @param groupPosition
	 * @return
	 */
	public int getChildrenCount(int groupPosition) {
		return padresExpandableList.get(groupPosition).getSize();
	}

	/**
	 * Devuelve un padre pada una posicion.
	 * @param groupPosition
	 * @return
	 */
	
	public Object getGroup(int groupPosition) {
		return padresExpandableList.get(groupPosition);
	}

	/**
	 * Número de padres en una lista.
	 * @return
	 */
	public int getGroupCount() {
		return padresExpandableList.size();
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
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_lista_expandible_rg, parent,false);
        }
		
		TextView textViewNombreCategoria = (TextView) convertView.findViewById(R.id.textViewPadre);
		textViewNombreCategoria.setText(padresExpandableList.get(groupPosition).getCategoriaExtra());
        
        if(padresExpandableList.get(groupPosition).isExpandido()){
        	if(esEditar){
        		Mesa.expandeGrupoLista(groupPosition);
        	}else{
        		MiExpandableListAdapterAnadirPlato.expandeGrupoLista(groupPosition);
        	}
        }
		return convertView;

	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }
	
	@Override
	public void onGroupExpanded(int groupPosition){
		padresExpandableList.get(groupPosition).setExpandido(true);
	}
	
	@Override
	public void onGroupCollapsed(int groupPosition){
		padresExpandableList.get(groupPosition).setExpandido(false);
	}
	/**
	 * Devuelve en forma de String los extras que estaban marcados.
	 * @return
	 */
	public String getExtrasMarcados(){
		Iterator<PadreExpandableListEditar> it = padresExpandableList.iterator();
		String extras = "";
		while(it.hasNext()){
			PadreExpandableListEditar unPadre = it.next();
			if(!extras.equals("")){
				extras += ", " + unPadre.getExtrasMarcados();				
			}
			else{
				String extrasMarcados = unPadre.getExtrasMarcados();
				if(extrasMarcados == null){
					return null;
				}else{
					extras += unPadre.getExtrasMarcados();	
				}
			}
		}
		return extras;
		
	}
	
	public void expandeTodosLosPadres(){
		Iterator<PadreExpandableListEditar> it = padresExpandableList.iterator();
		while(it.hasNext()){
			PadreExpandableListEditar  unPadre = it.next();
			unPadre.setExpandido(true);
		}
		//DescripcionPlato.actualizaExpandableList();
		for(int i = 0; i<padresExpandableList.size(); i++){
			//DescripcionPlato.expandeGrupoLista(i);
		}
	}
	
}
