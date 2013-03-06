package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.Iterator;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MiExpandableListAdapterAnadirPlato extends BaseExpandableListAdapter{
	private LayoutInflater inflater;
    private ArrayList<PadreExpandableListAnadirPlato> padresExpandableList;
    private Context context;
    ArrayList<PlatoView> platos;
    
    public MiExpandableListAdapterAnadirPlato(Context context, ArrayList<PadreExpandableListAnadirPlato> padres){
    	padresExpandableList = padres;
        inflater = LayoutInflater.from(context);
        this.context=context;
    }

	public Object getChild(int groupPosition, int childPosition) {
		return padresExpandableList.get(groupPosition).getHijo();
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
			convertView = inflater.inflate(R.layout.contenido_hijo_lista_anadir_plato, parent,false);
			
			GridView gridViewAnadir = (GridView) convertView.findViewById(R.id.gridViewAnadirPlato);			 
			
			ArrayList<String> idHijos = padresExpandableList.get(groupPosition).getHijo().getIds();
			ArrayList<Integer> imgHijos = padresExpandableList.get(groupPosition).getHijo().getNumImagenes();
			ArrayList<String> nombreHijos = padresExpandableList.get(groupPosition).getHijo().getNombrePl();

			platos = new ArrayList<PlatoView>();
			//Recorremos con una variable que indica la posicion, porque necesitariamos tres iteradores.
			//Los tres ArrayList tienen el mismo tamaño
			int pos = 0; 
			while(pos < nombreHijos.size()){
				
				ImageView img = new ImageView(convertView.getContext());
				img.setImageResource(imgHijos.get(pos));
			
				//traer las cosas de platoView
				
				PlatoView plato1= new PlatoView(context);
				plato1.setNombrePlato(nombreHijos.get(pos));
				plato1.setImagenPlato(imgHijos.get(pos));
				plato1.setIdPlato(idHijos.get(pos));
	    		platos.add(plato1);
	    		
	    		pos++;
			}
			
			//Llamamos al adapter para que muestre en la pantalla los cambios realizados
			AnadirPlatoAdapter adapterAnadir;
			adapterAnadir = new AnadirPlatoAdapter(context, platos);
			gridViewAnadir.setAdapter(adapterAnadir);

			gridViewAnadir.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	//sacará ventana emergente         	
	            	String pulsado= platos.get(position).getIdPlato();
	            	Toast.makeText(context,pulsado,Toast.LENGTH_SHORT).show();	
	                }
	        });
		
	    return convertView;
	
		}
	
		
	/**
	 * Expande los padres que ya estuviesen expandidos en un inicio. De esta forma cuando hagamos una
	 *  modificación en la lista la encontraremos en el mismo estado, pero con esos elementos modificados.
	 */
/*	public void expandePadres(){
		for(int i=0;i<padresExpandableList.size();i++){
			if(padresExpandableList.get(i).isExpandido()){
				PedidoFragment.expandeGrupoLista(i);
			}
		}
	}*/

	public int getChildrenCount(int groupPosition) {
		return 1;
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

	
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_anadir_plato, parent,false);
        }
 
        TextView textViewPadrePlato = (TextView) convertView.findViewById(R.id.textViewTipo);
        
        textViewPadrePlato.setText(getGroup(groupPosition).toString());
            
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
	

	
}
