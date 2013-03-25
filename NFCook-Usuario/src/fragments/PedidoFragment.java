
package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import usuario.SincronizarPedidoBeamNFC;
import usuario.SincronizarPedidoNFC;
import usuario.SincronizarPedidoNFC.SincronizarPedidoBackgroundAsyncTask;
import adapters.HijoExpandableListPedido;
import adapters.MiExpandableListAdapterPedido;
import adapters.PadreExpandableListPedido;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerDB;

import com.example.nfcook.R;

public class PedidoFragment extends Fragment{
	/*Atributos estaticos para poder tener acceso a ellos en los metodos estaticos de la clase y asi
	 * poder actualizar la lista desde otras clases*/
	private static MiExpandableListAdapterPedido  adapterExpandableListPedido;
	private static ExpandableListView expandableListPedido;
	private static View vistaConExpandaleList;
	
	private static String restaurante;
	
	private float total;
	
	private static HandlerDB sqlPedido;
	private static SQLiteDatabase dbPedido;
	
		public class SincronizarPedidoNFCBackgroundAsyncTask extends AsyncTask<Void, Void, Void> {
	  
			  /**
			   * Se ejecuta antes de doInBackground.
			   */
			  @Override
			  protected void onPreExecute() { 
		      }
			
			  /**
			   * Ejecuta en segundo plano
			   * Lanza la actividad sincronizar pedido
			   */
			  @Override
			  protected Void doInBackground(Void... params) {	
				  Intent intent = new Intent(getActivity(),SincronizarPedidoNFC.class);
			      intent.putExtra("Restaurante", restaurante);
			      startActivity(intent);
				  return null;
			  }
			  
			  /**
			   * Se ejecuta cuando termina doInBackground,
			   * Abre cuentaFragment
			   * FIXME No se actualiza cuenta hasta que pulsas de nuevo a ella pero si que hace la 
			   * transicion bien.
			   */
			  @Override
			  protected void onPostExecute(Void result) {
				  Fragment fragmentCuenta = new CuentaFragment();
	              ((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
	              FragmentTransaction m = getFragmentManager().beginTransaction();
	              m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	              m.commit();
			  }
		
			}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		vistaConExpandaleList = inflater.inflate(R.layout.pedido, container, false);
        importarBaseDatatos();
        crearExpandableList();
        actualizarPrecioPedido();
        ponerOnClickSincronizarPedidoNFC();
        ponerOnClickSincronizarPedidoBeam();
        ponerOnClickSincronizarPedidoQR();
        return vistaConExpandaleList;
	}
  	
	
	private void actualizarPrecioPedido() {
		TextView textViewPrecioTotalPedido = (TextView) vistaConExpandaleList.findViewById(R.id.textViewTotalPedido);
		textViewPrecioTotalPedido.setText(Math.rint(adapterExpandableListPedido.getPrecioTotalPedido()*100)/100 + "€");
	}


	public void crearExpandableList() {
		try{
			String[] campos = new String[]{"Plato"};//Campos que quieres recuperar
			String[] datosRestaurante = new String[]{restaurante};	
	    	Cursor c = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);

			Set<String> conjuntoNombresPadres = new HashSet<String>();
	    	while(c.moveToNext()){
	    		conjuntoNombresPadres.add(c.getString(0));
	    	}
	    	
			ArrayList<PadreExpandableListPedido> padres = new ArrayList<PadreExpandableListPedido>();
			Iterator<String> iteradorConjunto = conjuntoNombresPadres.iterator();
	    	while(iteradorConjunto.hasNext()){
		    	ArrayList<HijoExpandableListPedido> hijos = new ArrayList<HijoExpandableListPedido>();
	    		String[] camposBusquedaObsExt = new String[]{"Extras","Observaciones","PrecioPlato", "Id","IdHijo"};
	    		String nombrePlato = iteradorConjunto.next();
	    		String idPadre = "";
		    	String[] datos = new String[]{restaurante, nombrePlato};
		    	Cursor cursor = dbPedido.query("Pedido", camposBusquedaObsExt, "Restaurante=? AND Plato=?", datos,null, null,null);
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
	        Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTEN DATOS DEL PEDIDO",Toast.LENGTH_SHORT).show();
	    }   
	}
	
	public static void actualizaExpandableListPedidoEditada(){
		importarBaseDatatos();
		adapterExpandableListPedido.actualizaHijoEditado(dbPedido);
		actualizaExpandableList();
		adapterExpandableListPedido.expandePadres();
	}

	private static void importarBaseDatatos() {
		 try{
			 sqlPedido=new HandlerDB(vistaConExpandaleList.getContext(),"Pedido.db"); 
	     	 dbPedido = sqlPedido.open();
	     }catch(SQLiteException e){
	    	 Toast.makeText(vistaConExpandaleList.getContext(),"NO EXISTE BASE DE DATOS PEDIDO USUARIO",Toast.LENGTH_SHORT).show(); 		
	     }
	}
	
	private boolean baseDeDatosPedidoyCuentaVacias() {
		try{
			HandlerDB sqlPedido=new HandlerDB(getActivity(),"Pedido.db"); 
			SQLiteDatabase dbPedido = sqlPedido.open();
			
			String[] camposPedido = new String[]{"Id"};//Campos que quieres recuperar
			Cursor cursorPedido = dbPedido.query("Pedido", camposPedido, null, null,null, null,null);
			if(!cursorPedido.moveToFirst()){
				sqlPedido.close();
				return true;
			}else{
				sqlPedido.close();
				return false;
			}
        }catch(SQLiteException e){
         	Toast.makeText(getActivity(),"Base de Datos Pedido vacía",Toast.LENGTH_SHORT).show();
         	return true;
        }
	}
	
	private void ponerOnClickSincronizarPedidoNFC() {
		ImageView botonNFC = (ImageView) vistaConExpandaleList.findViewById(R.id.imageButtonNFCSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonNFC.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!baseDeDatosPedidoyCuentaVacias()){
					new SincronizarPedidoNFCBackgroundAsyncTask().execute();
				} 
				else Toast.makeText(vistaConExpandaleList.getContext(),"No puedes sincronizar si no has configurado un pedido",Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	private void ponerOnClickSincronizarPedidoBeam() {
		ImageView botonBeamNFC = (ImageView) vistaConExpandaleList.findViewById(R.id.imageButtonBeamSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonBeamNFC.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!baseDeDatosPedidoyCuentaVacias()){
					Intent intent = new Intent(getActivity(),SincronizarPedidoBeamNFC.class);
				    intent.putExtra("Restaurante", restaurante);
				    startActivity(intent);
				} 
				else Toast.makeText(vistaConExpandaleList.getContext(),"No puedes sincronizar si no has configurado un pedido",Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	private void ponerOnClickSincronizarPedidoQR() {
		ImageView botonQR = (ImageView) vistaConExpandaleList.findViewById(R.id.imageButtonQRSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonQR.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!baseDeDatosPedidoyCuentaVacias()){
					
				} 
				else Toast.makeText(vistaConExpandaleList.getContext(),"No puedes sincronizar si no has configurado un pedido",Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public static void actualizaExpandableList() {
		TextView textViewPrecioTotalPedido = (TextView) vistaConExpandaleList.findViewById(R.id.textViewTotalPedido);
		textViewPrecioTotalPedido.setText(Math.rint(adapterExpandableListPedido.getPrecioTotalPedido()*100)/100 + "€");
		expandableListPedido.setAdapter(adapterExpandableListPedido);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListPedido.expandGroup(groupPositionMarcar);
	}

	public static SQLiteDatabase getDbPedido() {
		return dbPedido;
	}
	
	public static void setRestaurante(String res){
		restaurante = res;
	}
	
	public static String getRestaurante(){
		return restaurante;
	}
	
}
