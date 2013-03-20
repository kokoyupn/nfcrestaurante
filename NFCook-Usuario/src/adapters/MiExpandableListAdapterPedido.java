package adapters;

import java.util.ArrayList;
import java.util.Iterator;

import usuario.DescripcionPlatoEditar;


import baseDatos.HandlerDB;

import com.example.nfcook.R;

import fragments.ContenidoTabSuperiorCategoriaBebidas;
import fragments.PedidoFragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Configura el adapter de la ExpandableList de la pantalla correspondiente a un pedido.
 * 
 * -Atributos-
 * inflater             : necesario para poder recoger los XML pertenecientes a dichas listas.
 * padresExpandableList : ArrayList de padres de la lista (elementos sin expandir).
 * fragmentLista        : necesario para poder lanzar la actividad correspondiente a la edición de un plato.
 * idPlatoHijoEditado   : Si un plato ha sido editado, es necesario para poder realizar la busqueda en la base de datos Pedido y modificarlo en la lista.
 * idPlatoPadreEditado  : Si un plato ha sido editado, es necesario para poder realizar la busqueda en la base de datos Pedido y modificarlo en la lista.
 * posPadreEditado      : posición del padre donde se ha editado un hijo.
 * posHijoEditado       : posición del hijo que ha sido editado.
 * @author Prado
 *
 */
public class MiExpandableListAdapterPedido extends BaseExpandableListAdapter {
 
    private LayoutInflater inflater;
    private ArrayList<PadreExpandableListPedido> padresExpandableList;
    private Fragment fragmentLista;
    
    private String idPlatoHijoEditado;
    private String idPlatoPadreEditado;
    private int posPadreEditado;
    private int posHijoEditado;
    
    public MiExpandableListAdapterPedido(Context context, ArrayList<PadreExpandableListPedido> padres, Fragment fragmentLista){
    	padresExpandableList = padres;
        inflater = LayoutInflater.from(context);
        this.fragmentLista = fragmentLista;
        
        //Ningun plato editado en un inicio, se usaran para solo modificar ese elemento de la base de datos.
        idPlatoHijoEditado = null; 
        idPlatoPadreEditado = null;
        posPadreEditado = -1;
        posHijoEditado = -1;
    }

	public Object getChild(int groupPosition, int childPosition) {
		return padresExpandableList.get(groupPosition).getHijoAt(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		String extrasPlato = padresExpandableList.get(groupPosition).getHijoAt(childPosition).getExtras();
		String observacionesPlato = padresExpandableList.get(groupPosition).getHijoAt(childPosition).getObservaciones();
		String precioPlato = padresExpandableList.get(groupPosition).getHijoAt(childPosition).getPrecio() + "€";
		
		//Cargamos diferente el layout en función de los campos a mostrar.
		
		convertView = inflater.inflate(R.layout.hijo_lista_pedido, parent,false);
		TextView textViewPrecio = (TextView) convertView.findViewById(R.id.textViewPrecioPedidoHijo);			 
		TextView textViewExtras = (TextView) convertView.findViewById(R.id.textViewPedidoExtras);
		TextView textViewObservaciones = (TextView) convertView.findViewById(R.id.textViewPedidoObservaciones);
		
		if(extrasPlato==null && observacionesPlato==null){
			
			textViewExtras.setText("Sin guarnición");
			textViewObservaciones.setText("Sin observaciones");
		
		}else if(extrasPlato==null && observacionesPlato!=null){
			
			textViewExtras.setText("Sin guarnición");			
			textViewObservaciones.setText(observacionesPlato);
		
		}else{
			
			textViewExtras.setText(extrasPlato);
			if(observacionesPlato==null){
				textViewObservaciones.setText("Sin observaciones");
			}else{
				textViewObservaciones.setText(observacionesPlato);
			}
		}
		
		textViewPrecio.setText(precioPlato);
		
		/*
		 * Necesitamos que sean final para que por cada hijo mostrado de la lista guardemos en que posición
		 * se encuentra, de esta forma podremos tanto borrarlo como editarlo.
		 */
		final int groupPositionMarcar = groupPosition;
		final int childPositionMarcar = childPosition;
		
		Button botonBorrar = (Button) convertView.findViewById(R.id.buttonPedidoBorrar);
		Button botonEditar = (Button) convertView.findViewById(R.id.buttonPedidoEditar);
		
		//OnClick boton borrar de cada hijo.
		botonBorrar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String idPadre = padresExpandableList.get(groupPositionMarcar).getIdPlato();
				String idHijo = padresExpandableList.get(groupPositionMarcar).getHijoAt(childPositionMarcar).getId();
				if(padresExpandableList.get(groupPositionMarcar).eliminaHijo(childPositionMarcar)){ //Eliminamos el hijo y si la condición es cierta (no tiene ningun hijo) eliminamos el padre
					padresExpandableList.remove(groupPositionMarcar);
					PedidoFragment.actualizaExpandableList();
				}else{
					PedidoFragment.actualizaExpandableList();// No se puede sacar del if, el orden importa
					PedidoFragment.expandeGrupoLista(groupPositionMarcar);
				}
				String[] camposDelete = {idPadre,idHijo};
				PedidoFragment.getDbPedido().delete("Pedido", "Id = ? AND IdHijo =?", camposDelete);
				
				/* Vemos si se trata de una bebida para actualizar la pantalla pedido
				 * si fuera así.
				 */
				// Abrimos la base de datos de los platos
				HandlerDB sql = new HandlerDB(v.getContext()); 
				SQLiteDatabase db = sql.open();
		     	
				// Preparamos la consulta para el plato con ese id
			    String[] camposSacarPlato = new String[]{"Id","Categoria"};
			    String[] datosQueCondicionanPlato = new String[]{idPadre,PedidoFragment.getRestaurante()};
			    Cursor cPlato = db.query("Restaurantes", camposSacarPlato, "Id=? AND Restaurante=?",datosQueCondicionanPlato,null, null,null);
		    		
			    // Solo nos sacará un elemento, porque los id's son únicos
	    		if(cPlato.moveToNext()){
	    			// Miramos si efectivamente era una bebida
	    			if(cPlato.getString(1).toLowerCase().equals("bebidas")){
	    				ContenidoTabSuperiorCategoriaBebidas.eliminarBebidaDesdePedido(idPadre, fragmentLista.getActivity());	
	    			}
	    		}
				
				expandePadres();
			}
		});
			
		//OnClick botonEditar de cada hijo.
		botonEditar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
		        posPadreEditado = groupPositionMarcar;
		        posHijoEditado = childPositionMarcar;
		        
				String idPlato = idPlatoPadreEditado = padresExpandableList.get(groupPositionMarcar).getIdPlato();
				String idPlatoHijo = idPlatoHijoEditado = padresExpandableList.get(groupPositionMarcar).getHijoAt(childPositionMarcar).getId();
				String extras = padresExpandableList.get(groupPositionMarcar).getHijoAt(childPositionMarcar).getExtras();
				String observaciones = padresExpandableList.get(groupPositionMarcar).getHijoAt(childPositionMarcar).getObservaciones();
				
				Intent intent = new Intent(v.getContext(), DescripcionPlatoEditar.class);
				
				//Elementos a recoger en la actividad invocada.
				intent.putExtra("idPlato", idPlato);
		    	intent.putExtra("extras",extras);
		    	intent.putExtra("observaciones", observaciones);
		    	intent.putExtra("idHijo", idPlatoHijo);
		    	fragmentLista.startActivity(intent);
			}
		});
		
	    return convertView;
	}
	
	/**
	 * Expande los padres que ya estuviesen expandidos en un inicio. De esta forma cuando hagamos una
	 *  modificación en la lista la encontraremos en el mismo estado, pero con esos elementos modificados.
	 */
	public void expandePadres(){
		for(int i=0;i<padresExpandableList.size();i++){
			if(padresExpandableList.get(i).isExpandido()){
				PedidoFragment.expandeGrupoLista(i);
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
			convertView = inflater.inflate(R.layout.padre_lista_pedido, parent,false);
        }
 
        TextView textViewPadrePlato = (TextView) convertView.findViewById(R.id.textViewPlatoPadre);
        TextView textViewPadrePrecio = (TextView) convertView.findViewById(R.id.textViewPrecioTotalPadre);
        
        textViewPadrePlato.setText(getGroup(groupPosition).toString());
        textViewPadrePrecio.setText(Math.rint(((PadreExpandableListPedido) this.getGroup(groupPosition)).getPrecio()*100)/100 + "€");
        
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
		Iterator<PadreExpandableListPedido> itPadres = padresExpandableList.iterator();
		double precioTotal = 0;
		while(itPadres.hasNext()){
			PadreExpandableListPedido unPadre = itPadres.next();
			precioTotal+=unPadre.getPrecio();
		}
		return precioTotal;
	}

	/**
	 * Actualiza los campos del hijo editado haciendo una busqueda en la base de datos.
	 * @param dbPedido
	 */
	public void actualizaHijoEditado(SQLiteDatabase dbPedido) {
		String[] camposBusquedaObsExt = new String[]{"Extras","Observaciones"};
    	String[] datos = new String[]{idPlatoPadreEditado, idPlatoHijoEditado};
    	Cursor cursor = dbPedido.query("Pedido", camposBusquedaObsExt, "Id =? AND IdHijo =?", datos,null, null,null);
    	cursor.moveToFirst();
    	String extrasNuevos = cursor.getString(0);
    	String observacionesNuevas = cursor.getString(1);
    	padresExpandableList.get(posPadreEditado).getHijoAt(posHijoEditado).setExtrasObs(extrasNuevos,observacionesNuevas);
    	//Reseteamos la ediccion
    	idPlatoHijoEditado = null; 
        idPlatoPadreEditado = null;
        posPadreEditado = -1;
        posHijoEditado = -1;
    	
	}
	
}
