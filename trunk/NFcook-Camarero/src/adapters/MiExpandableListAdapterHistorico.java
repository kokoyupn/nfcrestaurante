package adapters;

import java.util.ArrayList;
import java.util.Iterator;


import com.example.nfcook_camarero.R;
import com.example.nfcook_camarero.Historico;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Configura el adapter de la ExpandableList de la pantalla correspondiente a historico.
 * 
 * -Atributos-
 * inflater             : necesario para poder recoger los XML pertenecientes a dichas listas.
 * padresExpandableList : ArrayList de padres de la lista (elementos sin expandir).
 * @author Busy
 *
 */
public class MiExpandableListAdapterHistorico extends BaseExpandableListAdapter {
 
    private LayoutInflater inflater;
    private ArrayList<PadreExpandableListHistorico> padresExpandableList;
    
    
  

	public MiExpandableListAdapterHistorico(Context context,
			ArrayList<PadreExpandableListHistorico> padres) {
		padresExpandableList = padres;
        inflater = LayoutInflater.from(context);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return padresExpandableList.get(groupPosition).getHijoAt(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		String camarero = padresExpandableList.get(groupPosition).getHijoAt(childPosition).getCamarero();
		String hora = padresExpandableList.get(groupPosition).getHijoAt(childPosition).getHora();
		String precioPedido = Math.rint(padresExpandableList.get(groupPosition).getHijoAt(childPosition).getPrecio()*100)/100 + "€";
		
		//Cargamos diferente el layout en función de los campos a mostrar.
		
		convertView = inflater.inflate(R.layout.hijos_historico, parent,false);
		TextView textViewPrecio = (TextView) convertView.findViewById(R.id.precioHijoHistorico);			 
		TextView textViewHora = (TextView) convertView.findViewById(R.id.horaHijoHistorico);
		TextView textViewCamarero = (TextView) convertView.findViewById(R.id.camareroHijoHistorico);
		
			
			
		textViewPrecio.setText(precioPedido);
		textViewHora.setText(hora);
		textViewCamarero.setText(camarero);
		
		
		return convertView;
		
		
	}	
	
	/**
	 * Expande los padres que ya estuviesen expandidos en un inicio. De esta forma cuando hagamos una
	 *  modificación en la lista la encontraremos en el mismo estado, pero con esos elementos modificados.
	 */
	public void expandePadres(){
		for(int i=0;i<padresExpandableList.size();i++){
			if(padresExpandableList.get(i).isExpandido()){
				Historico.expandeGrupoLista(i);
			}
		}
	}
	
	
	
	public int getChildrenCount(int groupPosition) {
		return padresExpandableList.get(groupPosition).getSize();
	}

	public Object getGroup(int groupPosition) {
		return padresExpandableList.get(groupPosition);
	}

	public int getGroupCount() {
		return padresExpandableList.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padres_historico, parent,false);
        }
 
        TextView textViewPadreMesa = (TextView) convertView.findViewById(R.id.NumMesaHistorico);
        TextView textViewPadrePrecioMesa = (TextView) convertView.findViewById(R.id.preciomesaHistorico);
        
        textViewPadreMesa.setText(getGroup(groupPosition).toString());
        textViewPadrePrecioMesa.setText(Math.rint(((PadreExpandableListHistorico) this.getGroup(groupPosition)).getPrecio()*100)/100 + "€");
        
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
	
	public double getPrecioTotalPedido(){
		Iterator<PadreExpandableListHistorico> itPadres = padresExpandableList.iterator();
		double precioTotal = 0;
		while(itPadres.hasNext()){
			PadreExpandableListHistorico unPadre = itPadres.next();
			precioTotal+=unPadre.getPrecio();
		}
		return precioTotal;
	}

	
	
}
