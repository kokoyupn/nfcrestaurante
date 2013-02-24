package adapters;

import java.util.ArrayList;


import com.example.nfcook.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MiExpandableListAdapterPedido extends BaseExpandableListAdapter {
 
 
    private LayoutInflater inflater;
    private ArrayList<PadreExpandableListPedido> arrayPadres;
 
    public MiExpandableListAdapterPedido(Context context, ArrayList<PadreExpandableListPedido> padres){
    	arrayPadres = padres;
        inflater = LayoutInflater.from(context);
    }

	public Object getChild(int groupPosition, int childPosition) {
		return arrayPadres.get(groupPosition).getHijoAt(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 if (convertView == null) {
			 convertView = inflater.inflate(R.layout.contenido_hijo_lista_pedido, parent,false);
		 }
		 final int groupPositionMarcar = groupPosition; //para poder usarlos en el onClick del CheckBox
		 final int childPositionMArcar = childPosition;
		 
		 TextView textViewExtras = (TextView) convertView.findViewById(R.id.textViewPedidoExtras);
		 TextView textViewObservaciones = (TextView) convertView.findViewById(R.id.textViewPedidoObservaciones);
		 
		 textViewExtras.setText(arrayPadres.get(groupPosition).getHijoAt(childPosition).getExtras());
		 textViewObservaciones.setText(arrayPadres.get(groupPosition).getHijoAt(childPosition).getObservaciones());
		 
		 CheckBox checkHijo = (CheckBox) convertView.findViewById(R.id.checkBoxPedidoMarcar);
		 checkHijo.setOnClickListener( new View.OnClickListener() {
			    public void onClick(View v) {
			    	arrayPadres.get(groupPositionMarcar).getHijoAt(childPositionMArcar).setCheck();
			    }
		 			});
		 
	     return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return arrayPadres.get(groupPosition).getSize();
	}

	public Object getGroup(int groupPosition) {
		return arrayPadres.get(groupPosition);
	}

	public int getGroupCount() {
		return arrayPadres.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_lista_expandible_rg, parent,false);
        }
 
        TextView textViewPadre = (TextView) convertView.findViewById(R.id.textViewPadre);
        
        textViewPadre.setText(getGroup(groupPosition).toString());
 
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

	public void eliminarHijosMarcados() {
		for(int i = 0; i<arrayPadres.size();i++){
			arrayPadres.get(i).actualizaHijos();
		}
		
	}
}
