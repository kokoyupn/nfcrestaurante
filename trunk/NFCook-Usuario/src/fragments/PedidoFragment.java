
package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import usuario.SincronizarPedidoBeamNFC;
import usuario.SincronizarPedidoNFC;
import usuario.SincronizarPedidoQR;
import adapters.HijoExpandableListPedido;
import adapters.MiExpandableListAdapterPedido;
import adapters.PadreExpandableListPedido;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
	private AlertDialog ventanaEmergenteElegirSincronizacion;
	private View vistaVentanaEmergenteElegirSincronizacion;
	
	private static HandlerDB sqlPedido;
	private static SQLiteDatabase dbPedido;
	
	/**
	 * TODO variable para poder usar sin tarjetas. ELIMINAR
	 */
	NfcAdapter adapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		vistaConExpandaleList = inflater.inflate(R.layout.pedido, container, false);
        importarBaseDatatos();
        crearExpandableList();
        actualizarPrecioPedido();
        crearVentanaEmergenteElegirSincronizacion();
        ponerOnClickSincronizar();
        ponerOnClickSincronizarPedidoNFC();
        ponerOnClickSincronizarPedidoBeam();
        ponerOnClickSincronizarPedidoQR();
        
        /**
    	 * TOFIX variable para poder usar sin tarjetas. ELIMINAR
    	 */
        adapter = NfcAdapter.getDefaultAdapter(vistaConExpandaleList.getContext());
        
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
		    		if(!HijoExpandableListPedido.existeHijoIgualEnArray(hijos, unHijo)){
			    		hijos.add(unHijo);
		    		}
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
	
	/**
	 * Crea una ventana emergente que muestra los tipos de sincronizacion de pedido
	 * disponibles.
	 */
	private void crearVentanaEmergenteElegirSincronizacion(){
		vistaVentanaEmergenteElegirSincronizacion = LayoutInflater.from(vistaConExpandaleList.getContext()).inflate(R.layout.ventana_emergente_elegir_sincronizacion, null);
		ventanaEmergenteElegirSincronizacion = new AlertDialog.Builder(vistaConExpandaleList.getContext()).create();
		ventanaEmergenteElegirSincronizacion.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancelar", 
				new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {
						ventanaEmergenteElegirSincronizacion.dismiss();
					}
		});
		ventanaEmergenteElegirSincronizacion.setView(vistaVentanaEmergenteElegirSincronizacion);
	}
	
	/**
	 * Crea el onClick la la imagen botonSincronizar.
	 * Compruena si las bases de datos estan vacias para permitir o no sincronizar.
	 * Si se puede abre una ventana emergente para elegir el metodo de sincronizacion.
	 */
	private void ponerOnClickSincronizar() {
		ImageView botonSincronizar = (ImageView) vistaConExpandaleList.findViewById(R.id.imageSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonSincronizar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!baseDeDatosPedidoyCuentaVacias()){
		    		ventanaEmergenteElegirSincronizacion.show();
				} 
				else Toast.makeText(vistaConExpandaleList.getContext(),"No puedes sincronizar si no has configurado un pedido",Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	/**
	 * Crea el onClick la la imagen botonNFC.
	 * Si el adapter es null significa que el dispositivo no tiene NFC, entonces no puede
	 * sincronizar con este metodo y lanza un mensaje. Si no es null, se abre la ventana
	 * para sincronizar por NFC.
	 */
	private void ponerOnClickSincronizarPedidoNFC() {
		ImageView botonNFC = (ImageView) vistaVentanaEmergenteElegirSincronizacion.findViewById(R.id.imageNFCSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonNFC.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// cierro la ventana emergente
				ventanaEmergenteElegirSincronizacion.dismiss();
				if (adapter != null) {
					/**
					 * TODO para poder usar sin tarjetas. ELIMINAR
					 */
					if (!adapter.isEnabled()){
						Toast.makeText(vistaConExpandaleList.getContext(),"NFC desactivado. Esta opción existe solo para probrar las cosas. Luego ELIMINAR",Toast.LENGTH_LONG).show();
						enviarPedidoACuenta();
						Fragment fragmentCuenta = new CuentaFragment();
					    ((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
					    FragmentTransaction m = getFragmentManager().beginTransaction();
					    m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
					    m.commit();	
					    //Toast.makeText(vistaConExpandaleList.getContext(),"Tu dispositivo no tiene NFC. Prueba a sincronizar tu pedido por QR.",Toast.LENGTH_LONG).show();
					} else {
						// abro la ventana para sincronizar con NFC
						Intent intent = new Intent(getActivity(),SincronizarPedidoNFC.class);
						intent.putExtra("Restaurante", restaurante);
						startActivityForResult(intent, 0);
					}
				} else {
					/**
					 * TODO para poder usar sin tarjetas. ELIMINAR
					 */
					Toast.makeText(vistaConExpandaleList.getContext(),"No tienes NFC. Esta opción existe solo para probrar las cosas. Luego ELIMINAR",Toast.LENGTH_LONG).show();
					enviarPedidoACuenta();
					Fragment fragmentCuenta = new CuentaFragment();
				    ((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
				    FragmentTransaction m = getFragmentManager().beginTransaction();
				    m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
				    m.commit();	
				    //Toast.makeText(vistaConExpandaleList.getContext(),"Tu dispositivo no tiene NFC. Prueba a sincronizar tu pedido por QR.",Toast.LENGTH_LONG).show();
				}
			} 
		});
	}
	
	/**
	 * Entra cuando regresa de una actividad lanzada con startActivityForResult (onClick de NFC y QR).
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// compruebo si se escribio en la tag (resul_ok) para ir a Cuenta. Si no se queda en Pedido
		if (Activity.RESULT_OK == resultCode){
			Fragment fragmentCuenta = new CuentaFragment();
	        ((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	        m.commit();	
		} 	
	}
	
	
	/**
	 * TODO para poder usar sin tarjetas. ELIMINAR
	 */
	private void enviarPedidoACuenta(){
		HandlerDB sqlCuenta = null;
		sqlPedido = null;
		SQLiteDatabase dbCuenta = null;
		dbPedido = null;
		
		try{
			sqlPedido = new HandlerDB(getActivity(), "Pedido.db");
			dbPedido = sqlPedido.open();
		}catch(SQLiteException e){
         	Toast.makeText(getActivity(),"NO EXISTE BASE DE DATOS PEDIDO: SINCRONIZAR NFC (cargarBaseDeDatosCuenta)",Toast.LENGTH_SHORT).show();
		}
		try{
			sqlCuenta = new HandlerDB(getActivity(), "Cuenta.db");
			dbCuenta = sqlCuenta.open();
		}catch(SQLiteException e){
         	Toast.makeText(getActivity(),"NO EXISTE BASE DE DATOS CUENTA: SINCRONIZAR NFC",Toast.LENGTH_SHORT).show();
		}	
		//Campos que quieres recuperar
		String[] campos = new String[]{"Id","Plato","Observaciones","Extras","PrecioPlato","Restaurante","IdHijo"};
		String[] datosRestaurante = new String[]{restaurante};	
		Cursor cursorPedido = dbPedido.query("Pedido", campos, "Restaurante=?", datosRestaurante,null, null,null);
    	
    	while(cursorPedido.moveToNext()){
    		ContentValues platoCuenta = new ContentValues();
        	platoCuenta.put("Id", cursorPedido.getString(0));
        	platoCuenta.put("Plato", cursorPedido.getString(1));
        	platoCuenta.put("Observaciones", cursorPedido.getString(2));
        	platoCuenta.put("Extras", cursorPedido.getString(3));
        	platoCuenta.put("PrecioPlato",cursorPedido.getDouble(4));
        	platoCuenta.put("Restaurante",cursorPedido.getString(5));
        	platoCuenta.put("IdHijo", cursorPedido.getString(6));
    		dbCuenta.insert("Cuenta", null, platoCuenta);
    	}
		
		try{
			dbPedido.delete("Pedido", "Restaurante=?", datosRestaurante);	
		}catch(SQLiteException e){
         	Toast.makeText(getActivity(),"ERROR AL BORRAR BASE DE DATOS PEDIDO",Toast.LENGTH_SHORT).show();
		}
		
		// Reinciamos la pantalla bebidas, porque ya hemos sincronizado el pedido
		ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas((Activity) vistaConExpandaleList.getContext());
		
		sqlCuenta.close();
		sqlPedido.close();	
	}	
	
	/**
	 * Crea el onClick la imagen botonBeam
	 */
	private void ponerOnClickSincronizarPedidoBeam() {
		ImageView botonBeam = (ImageView) vistaVentanaEmergenteElegirSincronizacion.findViewById(R.id.imageBeamSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonBeam.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// cierro la ventana emergente
				ventanaEmergenteElegirSincronizacion.dismiss();
				//abro la ventana para sincronizar con Beam
				Intent intent = new Intent(getActivity(),SincronizarPedidoBeamNFC.class);
				intent.putExtra("Restaurante", restaurante);
				startActivityForResult(intent, 0);
			}
		});
		
	}
	
	/**
	 * Crea el onClick la imagen botonQR
	 */
	private void ponerOnClickSincronizarPedidoQR() {
		ImageView botonQR = (ImageView) vistaVentanaEmergenteElegirSincronizacion.findViewById(R.id.imageQRSincronizar);
		
		//OnClick boton borrar de cada hijo.
		botonQR.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// abro ventana para sincronizar con QR
				Intent intent = new Intent(getActivity(),SincronizarPedidoQR.class);
				intent.putExtra("Restaurante", restaurante);
				startActivityForResult(intent,0);
				// cierro la ventana emergente
				ventanaEmergenteElegirSincronizacion.dismiss();
				
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
		adapterExpandableListPedido.notifyDataSetChanged();
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
