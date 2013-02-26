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
	private MiExpandableListAdapterPedido  adapterExpandableListPedido;
	private ExpandableListView expandableListPedido;
	
	private float total;
	
	private Handler sqlPedido;
	private SQLiteDatabase dbPedido;
	
	private Button botonEliminar; // Para poder pasarlo al adapter y  hacerlo visible e invisible al marcarun CheckBox
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View vistaConExpandaleList = inflater.inflate(R.layout.expandable_list_pedido, container, false);
		botonEliminar = (Button) vistaConExpandaleList.findViewById(R.id.buttonEliminarPedido);
		botonEliminar.setVisibility(Button.INVISIBLE);
		botonEliminar.setOnClickListener( new View.OnClickListener() {
		    public void onClick(View v) {
		    	adapterExpandableListPedido.eliminarHijosMarcados();
				expandableListPedido.setAdapter(adapterExpandableListPedido);
		    }
		      });
        importarBaseDatatos(vistaConExpandaleList);
        crearExpandableList(vistaConExpandaleList);
        return vistaConExpandaleList;
	}
   
	public void crearExpandableList(View vistaConExpandaleList) {
		try{
			
			String[] campos = new String[]{"Plato"};//Campos que quieres recuperar
	    	Cursor c = dbPedido.query("Pedido", campos, null, null,null, null,null);

			Set<String> conjuntoNombresPadres = new HashSet<String>();
	    	while(c.moveToNext()){
	    		conjuntoNombresPadres.add(c.getString(0));
	    	}

			ArrayList<PadreExpandableListPedido> padres = new ArrayList<PadreExpandableListPedido>();
			Iterator<String> iteradorConjunto = conjuntoNombresPadres.iterator();
	    	while(iteradorConjunto.hasNext()){
		    	ArrayList<HijoExpandableListPedido> hijos = new ArrayList<HijoExpandableListPedido>();
	    		String[] camposBusquedaObsExt = new String[]{"Extras","Observaciones","PrecioPlato"};
	    		String nombrePlato = iteradorConjunto.next();
		    	String[] datos = new String[]{nombrePlato};
		    	Cursor cursor = dbPedido.query("Pedido", camposBusquedaObsExt, "Plato=?", datos,null, null,null);
		    	double precio = 0; //Para sumar todos los platos hijos de un padre
		    	while(cursor.moveToNext()){
		    		precio +=cursor.getDouble(2); 
		    		HijoExpandableListPedido unHijo = new HijoExpandableListPedido(cursor.getString(1), cursor.getString(0), cursor.getDouble(2));
		    		hijos.add(unHijo);
		    	}
		    	PadreExpandableListPedido unPadre = new PadreExpandableListPedido(nombrePlato, hijos, precio);
		    	padres.add(unPadre);
	    	}
			expandableListPedido = (ExpandableListView) vistaConExpandaleList.findViewById(R.id.expandableListPedido);
			adapterExpandableListPedido = new MiExpandableListAdapterPedido(vistaConExpandaleList.getContext(),padres, botonEliminar);
			expandableListPedido.setAdapter(adapterExpandableListPedido);
	    }catch(SQLiteException e){
	        Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();
	    		
	    }   
	}
	
	public void onClickEliminarPedido(View boton){
		//Falta eliminar de base de datos
		adapterExpandableListPedido.eliminarHijosMarcados();
		adapterExpandableListPedido.notifyAll();
	}

	private void importarBaseDatatos(View vistaConExpandaleList) {
		 try{
	     	   sqlPedido=new Handler(vistaConExpandaleList.getContext(),"Pedido.db"); 
	     	   dbPedido=sqlPedido.open();
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
	
}
