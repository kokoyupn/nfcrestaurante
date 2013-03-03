package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;

import baseDatos.Handler;

import com.example.nfcook.R;

import adapters.HijoExpandableListPedido;
import adapters.MiExpandableListAdapterPedido;
import adapters.PadreExpandableListPedido;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class PedidoFragment extends Fragment{
	/*Atributos estaticos para poder tener acceso a ellos en los metodos estaticos de la clase y asi
	 * poder actualizar la lista desde otras clases*/
	private static MiExpandableListAdapterPedido  adapterExpandableListPedido;
	private static ExpandableListView expandableListPedido;
	private static Context context;
	private static View vistaConExpandaleList;
	
	private float total;
	
	private static Handler sqlPedido;
	private static SQLiteDatabase dbPedido;
	
	private Button botonEliminar; // Para poder pasarlo al adapter y  hacerlo visible e invisible al marcarun CheckBox
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		vistaConExpandaleList = inflater.inflate(R.layout.expandable_list_pedido, container, false);
        importarBaseDatatos();
        crearExpandableList();
        return vistaConExpandaleList;
	}
   
	public void crearExpandableList() {
		try{
			String[] campos = new String[]{"Plato"};//Campos que quieres recuperar
	    	Cursor c = getDbPedido().query("Pedido", campos, null, null,null, null,null);

			Set<String> conjuntoNombresPadres = new HashSet<String>();
	    	while(c.moveToNext()){
	    		conjuntoNombresPadres.add(c.getString(0));
	    	}
	    	
	    	context = vistaConExpandaleList.getContext(); // Necesitamos context para lanzar una actividad en el boton editar
			ArrayList<PadreExpandableListPedido> padres = new ArrayList<PadreExpandableListPedido>();
			Iterator<String> iteradorConjunto = conjuntoNombresPadres.iterator();
	    	while(iteradorConjunto.hasNext()){
		    	ArrayList<HijoExpandableListPedido> hijos = new ArrayList<HijoExpandableListPedido>();
	    		String[] camposBusquedaObsExt = new String[]{"Extras","Observaciones","PrecioPlato", "Id","IdHijo"};
	    		String nombrePlato = iteradorConjunto.next();
	    		String idPadre = "";
		    	String[] datos = new String[]{nombrePlato};
		    	Cursor cursor = getDbPedido().query("Pedido", camposBusquedaObsExt, "Plato=?", datos,null, null,null);
		    	double precio = 0; //Para sumar todos los platos hijos de un padre
		    	while(cursor.moveToNext()){
		    		idPadre = cursor.getString(3);
		    		precio +=cursor.getDouble(2); 
		    		HijoExpandableListPedido unHijo = new HijoExpandableListPedido(cursor.getString(1), cursor.getString(0), cursor.getDouble(2), cursor.getString(4));
		    		hijos.add(unHijo);
		    	}
		    	PadreExpandableListPedido unPadre = new PadreExpandableListPedido(nombrePlato, hijos, precio, idPadre);
		    	padres.add(unPadre);
	    	}
			expandableListPedido = (ExpandableListView) vistaConExpandaleList.findViewById(R.id.expandableListPedido);
			adapterExpandableListPedido = new MiExpandableListAdapterPedido(vistaConExpandaleList.getContext(), padres, this);
			expandableListPedido.setAdapter(adapterExpandableListPedido);
	    }catch(SQLiteException e){
	        Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();
	    		
	    }   
	}
	
	public static void actualizaExpandableListPedidoEditada(){
		importarBaseDatatos();
		adapterExpandableListPedido.actualizaHijoEditado(getDbPedido());
		actualizaExpandableList();
		adapterExpandableListPedido.expandePadres();
	}

	private static void importarBaseDatatos() {
		 try{
	     	   sqlPedido=new Handler(vistaConExpandaleList.getContext(),"Pedido.db"); 
	     	   dbPedido = sqlPedido.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTE BASE DE DATOS PEDIDO USUARIO",Toast.LENGTH_SHORT).show();
	      		
	         }
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public static void actualizaExpandableList() {
		expandableListPedido.setAdapter(adapterExpandableListPedido);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListPedido.expandGroup(groupPositionMarcar);
	}

	public static SQLiteDatabase getDbPedido() {
		return dbPedido;
	}
	
}
