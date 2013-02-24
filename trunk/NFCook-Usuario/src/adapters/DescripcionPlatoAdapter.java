package adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usuario.DescripcionPlato;

import com.example.nfcook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class DescripcionPlatoAdapter extends BaseExpandableListAdapter {
	private List<Map<String,String>> padres;
	public List<List<Map<String, RadioGroup>>> hijos;
	private LayoutInflater l_inflater;
	private Context context;
	private DescripcionPlato des;

	public DescripcionPlatoAdapter(DescripcionPlato des, Context context, List<Map<String,String>> padres, List<List<Map<String, RadioGroup>>> hijos) {
		this.padres = padres;
		this.hijos = new ArrayList<List<Map<String, RadioGroup>>>();
		for(int i=0;i<hijos.size();i++){
			List<Map<String, RadioGroup>> listaHijo = hijos.get(i);
			RadioGroup rg = listaHijo.get(0).get("NOMBRE");
			RadioGroup nuevoRg = new RadioGroup(context);
			for(int j=0;j<rg.getChildCount();j++){
				View rb = rg.getChildAt(j);
				if (rb instanceof RadioButton) {
					RadioButton raux = new RadioButton(context);
					raux.setText(((RadioButton)rb).getText());
					nuevoRg.addView(raux);
		        }
			}
			Map<String, RadioGroup> hij = new HashMap<String, RadioGroup>();
			hij.put("NOMBRE", nuevoRg);
			List<Map<String, RadioGroup>> nuevalistaHijo = new ArrayList<Map<String, RadioGroup>>();
			nuevalistaHijo.add(hij);
			this.hijos.add(listaHijo);
		}
		this.l_inflater = LayoutInflater.from(context);
		this.context = context;
		this.des = des;
	}

	public int getGroupCount() {
		return padres.size();
	}

	public Object getGroupItem(int position) {
		return padres.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView itemText;
		if (convertView == null)
		{
			convertView = l_inflater.inflate(R.layout.padre_lista_expandible_rg, null);
			itemText = (TextView) convertView.findViewById(R.id.textViewPadre);
			convertView.setTag(itemText);
		}else
			itemText = (TextView) convertView.getTag();
		
		itemText.setText(padres.get(groupPosition).get("NOMBRE"));
		
		return convertView;
		
	}

	public Object getChild(int groupPosition, int childPosition) {
		return hijos.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		RadioGroup rg;
		
		if (convertView == null)
		{
			convertView = l_inflater.inflate(R.layout.hijo_lista_expandible_rg, null);
			rg = (RadioGroup) convertView.findViewById(R.id.radioGroup1);
			convertView.setTag(rg);
		}else{
			rg = (RadioGroup) convertView.getTag();
		}

		rg.removeAllViews();
		int j = hijos.get(groupPosition).get(0).get("NOMBRE").getChildCount();
		int i=0;
		while(i<j)
		{			
			View rb = hijos.get(groupPosition).get(0).get("NOMBRE").getChildAt(i);
			if (rb instanceof RadioButton) {
				final RadioButton raux = new RadioButton(context);
				raux.setText(((RadioButton)rb).getText());
				raux.setTextSize(14);
				if(des.seleccionado[groupPosition] == ((RadioButton)rb).getText())
					raux.setChecked(true);
					rg.addView(raux);
					raux.setOnClickListener(new OnClickListener (){
					public void onClick(View v) {
						des.seleccionado[groupPosition] = raux.getText().toString();
					}
				});
	        }
			i++;
		}
		
		Map<String, RadioGroup> platoActual = new HashMap<String, RadioGroup>();
		platoActual.put("NOMBRE",rg);
		List<Map<String, RadioGroup>> listaPlatosActual = new ArrayList<Map<String, RadioGroup>>();  
		listaPlatosActual.add(platoActual);
		des.setPlatoHijo(groupPosition,listaPlatosActual);
		return convertView;	
	}

	public int getChildrenCount(int groupPosition) {
		return hijos.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		return padres.get(groupPosition);
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public boolean areAllItemsEnabled(){
        return true;
    }
	
	
}
