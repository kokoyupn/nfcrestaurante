
package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import usuario.InicializarRestaurante;
import usuario.SincronizarPedidoBeamNFC;
import usuario.SincronizarPedidoNFC;
import usuario.SincronizarPedidoQR;
import adapters.HijoExpandableListPedido;
import adapters.MiExpandableListAdapterPedido;
import adapters.PadreExpandableListPedido;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
	private static View vistaConExpandableList;
	
	private static String restaurante;
	private float total;
	private AlertDialog ventanaEmergenteElegirSincronizacion;
	private View vistaVentanaEmergenteElegirSincronizacion;
	
	private static HandlerDB sqlPedido;
	private static SQLiteDatabase dbPedido;
	
	private NfcAdapter adapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		vistaConExpandableList = inflater.inflate(R.layout.pedido, container, false);
        
		// Ponemos el t�tulo a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActivity().getActionBar();
    	actionbar.setTitle(" PEDIDO");
    	
		importarBaseDatatos();
        crearExpandableList();
        actualizarPrecioPedido();
        crearVentanaEmergenteElegirSincronizacion();
        ponerOnClickPapelera();
        ponerOnClickSincronizar();
        ponerOnClickSincronizarPedidoNFC();
        ponerOnClickSincronizarPedidoBeam();
        ponerOnClickSincronizarPedidoQR();
        
        // me devuelve null si no tiene NFC, si no, me devuelve el adapter nfc del dispositivo
        adapter = NfcAdapter.getDefaultAdapter(vistaConExpandableList.getContext());
        
        return vistaConExpandableList;
	}
  	
	
	private void actualizarPrecioPedido() {
		TextView textViewPrecioTotalPedido = (TextView) vistaConExpandableList.findViewById(R.id.textViewTotalPedido);
		textViewPrecioTotalPedido.setText(Math.rint(adapterExpandableListPedido.getPrecioTotalPedido()*100)/100 + " �");
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
	    		String[] camposBusquedaObsExt = new String[]{"Extras","Ingredientes","PrecioPlato", "Id","IdHijo"};
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
			expandableListPedido = (ExpandableListView) vistaConExpandableList.findViewById(R.id.expandableListPedido);
			adapterExpandableListPedido = new MiExpandableListAdapterPedido(vistaConExpandableList.getContext(), padres, this);
			expandableListPedido.setAdapter(adapterExpandableListPedido);
	    }catch(SQLiteException e){
	        Toast.makeText(vistaConExpandableList.getContext(),"NO EXISTEN DATOS DEL PEDIDO",Toast.LENGTH_SHORT).show();
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
			 sqlPedido=new HandlerDB(vistaConExpandableList.getContext(),"Pedido.db"); 
	     	 dbPedido = sqlPedido.open();
	     }catch(SQLiteException e){
	    	 Toast.makeText(vistaConExpandableList.getContext(),"NO EXISTE BASE DE DATOS PEDIDO USUARIO",Toast.LENGTH_SHORT).show(); 		
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
         	Toast.makeText(getActivity(),"Base de Datos Pedido vac�a",Toast.LENGTH_SHORT).show();
         	return true;
        }
	}
	
	/**
	 * Crea una ventana emergente que muestra los tipos de sincronizacion de pedido
	 * disponibles.
	 */
	private void crearVentanaEmergenteElegirSincronizacion(){
		vistaVentanaEmergenteElegirSincronizacion = LayoutInflater.from(vistaConExpandableList.getContext()).inflate(R.layout.ventana_emergente_elegir_sincronizacion, null);
		ventanaEmergenteElegirSincronizacion = new AlertDialog.Builder(vistaConExpandableList.getContext()).create();
		ventanaEmergenteElegirSincronizacion.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancelar", 
				new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {
						ventanaEmergenteElegirSincronizacion.dismiss();
					}
		});
		ventanaEmergenteElegirSincronizacion.setView(vistaVentanaEmergenteElegirSincronizacion);
	}
	
	/**
	 * Crea el onClick la papelera para borrar todo el pedido.
	 */
	private void ponerOnClickPapelera() {
		ImageView botonPapelera = (ImageView) vistaConExpandableList.findViewById(R.id.imagePapelera);
		
		botonPapelera.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//creo el alert dialog que se mostrara al pulsar en el boton back
		    	AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(vistaConExpandableList.getContext());
				onClickBotonAceptarAlertDialog(ventanaEmergente);
				onClickBotonCancelarAlertDialog(ventanaEmergente);
				View vistaAviso = LayoutInflater.from(vistaConExpandableList.getContext()).inflate(R.layout.aviso_continuar_pedido, null);
				//modifico el texto a mostrar
				TextView textoAMostar = (TextView) vistaAviso.findViewById(R.id.textViewInformacionAviso);
				textoAMostar.setText("�Desea eliminar todo su pedido?");
				ventanaEmergente.setView(vistaAviso);
				ventanaEmergente.show();
			}
		});
	}

	/**
	 * boton NO del alert dialog. No hace nada pero por debajo cierra el dialog.
	 * @param ventanaEmergente
	 */
	private void onClickBotonCancelarAlertDialog(Builder ventanaEmergente) {
		ventanaEmergente.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
			public void onClick(DialogInterface dialog, int which) {
					
			}
		});
	}
			
	/**
	* boton SI del alert dialog. Finaliza la actividad y cierra el dialog por debajo. 
	* @param ventanaEmergente
	*/
	private void onClickBotonAceptarAlertDialog(Builder ventanaEmergente) {
		ventanaEmergente.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					
			public void onClick(DialogInterface dialog, int which) {
				try{
					HandlerDB sqlPedido = new HandlerDB(vistaConExpandableList.getContext(),"Pedido.db"); 
					SQLiteDatabase dbPedido = sqlPedido.open();
					dbPedido.delete("Pedido", null, null);
					sqlPedido.close();
					
					importarBaseDatatos();
			        crearExpandableList();
			        actualizarPrecioPedido();
			        ContenidoTabSuperiorCategoriaBebidas.reiniciarPantallaBebidas();
					
				}catch(SQLiteException e){
		         	Toast.makeText(vistaConExpandableList.getContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
		        }		
			}
		});
	}

	
	/**
	 * Crea el onClick la la imagen botonSincronizar.
	 * Comprueba si las bases de datos estan vacias para permitir o no sincronizar.
	 * Si se puede abre una ventana emergente para elegir el metodo de sincronizacion.
	 */
	private void ponerOnClickSincronizar() {
		ImageView botonSincronizar = (ImageView) vistaConExpandableList.findViewById(R.id.imageSincronizar);
		
		botonSincronizar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!baseDeDatosPedidoyCuentaVacias()){
		    		ventanaEmergenteElegirSincronizacion.show();
				} 
				else Toast.makeText(vistaConExpandableList.getContext(),"No puedes sincronizar si no has configurado un pedido",Toast.LENGTH_SHORT).show();
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
		
		botonNFC.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// cierro la ventana emergente
				ventanaEmergenteElegirSincronizacion.dismiss();
				if (adapter != null) {					
					// abro la ventana para sincronizar con NFC
					Intent intent = new Intent(getActivity(),SincronizarPedidoNFC.class);
					intent.putExtra("Restaurante", restaurante);
					startActivityForResult(intent, 0);
				} else Toast.makeText(vistaConExpandableList.getContext(),"Tu dispositivo no tiene NFC. Prueba a sincronizar tu pedido por QR.",Toast.LENGTH_LONG).show();
			} 
		});
	}
	
	/**
	 * Entra cuando regresa de una actividad lanzada con startActivityForResult (onClick de NFC y QR).
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//Por culpa del bug de goole tenemos que invocarlo manualmente desde onActivityResult en  InicializarRestaurante
		// compruebo si se escribio en la tag (resul_ok) para ir a Cuenta. Si no se queda en Pedido
		if (Activity.RESULT_OK == resultCode){
			InicializarRestaurante.cargarTabCuenta();
			Fragment fragmentCuenta = new CuentaFragment();
			((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
			FragmentTransaction m = getFragmentManager().beginTransaction();
			m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
			m.commitAllowingStateLoss();
		} 	
	}
	
	/**
	 * Crea el onClick la imagen botonBeam
	 */
	private void ponerOnClickSincronizarPedidoBeam() {
		ImageView botonBeam = (ImageView) vistaVentanaEmergenteElegirSincronizacion.findViewById(R.id.imageBeamSincronizar);
		
		botonBeam.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				// cierro la ventana emergente
				ventanaEmergenteElegirSincronizacion.dismiss();
				if (adapter != null) {		
					//abro la ventana para sincronizar con Beam
					Intent intent = new Intent(getActivity(),SincronizarPedidoBeamNFC.class);
					intent.putExtra("Restaurante", restaurante);
					startActivityForResult(intent, 0);
				} else Toast.makeText(vistaConExpandableList.getContext(),"Tu dispositivo no tiene NFC. Prueba a sincronizar tu pedido por QR.",Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
	/**
	 * Crea el onClick la imagen botonQR
	 */
	private void ponerOnClickSincronizarPedidoQR() {
		ImageView botonQR = (ImageView) vistaVentanaEmergenteElegirSincronizacion.findViewById(R.id.imageQRSincronizar);
		
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
		TextView textViewPrecioTotalPedido = (TextView) vistaConExpandableList.findViewById(R.id.textViewTotalPedido);
		textViewPrecioTotalPedido.setText(Math.rint(adapterExpandableListPedido.getPrecioTotalPedido()*100)/100 + " �");
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
