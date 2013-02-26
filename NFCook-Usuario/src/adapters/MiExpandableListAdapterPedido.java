package adapters;

import java.util.ArrayList;


import com.example.nfcook.R;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MiExpandableListAdapterPedido extends BaseExpandableListAdapter {
 
 
    private LayoutInflater inflater;
    private ArrayList<PadreExpandableListPedido> arrayPadres;
    private Button botonEliminar;
    
    public MiExpandableListAdapterPedido(Context context, ArrayList<PadreExpandableListPedido> padres, Button botonEliminar){
    	arrayPadres = padres;
        inflater = LayoutInflater.from(context);
        this.botonEliminar = botonEliminar;
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
		 TextView textViewPrecio = (TextView) convertView.findViewById(R.id.textViewPrecioPedidoHijo);
		 
		 textViewPrecio.setText(arrayPadres.get(groupPosition).getHijoAt(childPosition).getPrecio() + "€");

		 textViewExtras.setText(arrayPadres.get(groupPosition).getHijoAt(childPosition).getExtras());
		 textViewObservaciones.setText(arrayPadres.get(groupPosition).getHijoAt(childPosition).getObservaciones());
		 
		 CheckBox checkHijo = (CheckBox) convertView.findViewById(R.id.checkBoxPedidoMarcar);
		 checkHijo.setOnClickListener( new View.OnClickListener() {
			    public void onClick(View v) {
			    	arrayPadres.get(groupPositionMarcar).getHijoAt(childPositionMArcar).setCheck();
			    	if(algunHijoMarcado()){
			    		botonEliminar.setVisibility(Button.VISIBLE);
			    	}else{
			    		botonEliminar.setVisibility(Button.INVISIBLE);
			    	}
			    }
		 			});
		 
	     return convertView;
	}
	
	private boolean algunHijoMarcado(){
		int posicionPadre = 0;
		boolean marcado = false;
		while(posicionPadre<arrayPadres.size() && !marcado){
			marcado = arrayPadres.get(posicionPadre).algunHijoMarcado();
			posicionPadre++;
		}
		return marcado;
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
			convertView = inflater.inflate(R.layout.contenido_padre_lista_pedido, parent,false);
        }
 
        TextView textViewPadrePlato = (TextView) convertView.findViewById(R.id.textViewPlatoPadre);
        TextView textViewPadrePrecio = (TextView) convertView.findViewById(R.id.textViewPrecioTotalPadre);
        
        textViewPadrePlato.setText(getGroup(groupPosition).toString());
        int precio = (int)(((PadreExpandableListPedido) this.getGroup(groupPosition)).getPrecio() * 100); 
        double valorConDosDecimales = precio/100.0;
        textViewPadrePrecio.setText(valorConDosDecimales + "€");
        
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
