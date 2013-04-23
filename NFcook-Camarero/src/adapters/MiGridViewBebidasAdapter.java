package adapters;

import java.util.ArrayList;

//import usuario.DescripcionPlato;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook_camarero.AnadirBebida;
import com.example.nfcook_camarero.R;

//import fragments.ContenidoTabSuperiorCategoriaBebidas;

/**
 * Configura el adapter del gridview de la pantalla selección de bebidas.
 * 
 * -Atributos-
 * activity: Actividad que llama a esta pantalla.
 * context: Contexto de esta actividad.
 * bebidas: Información de todas las bebidas que hay que mostrar. La información iluye el nombre, precio, número de unidades seleccionadas, etc.
 * 
 * @author Rober
 *
 */
public class MiGridViewBebidasAdapter extends BaseAdapter{
	private Activity activity;
	
	private ArrayList<PadreGridViewBebidas> bebidas;
	
	private Context context;

	public MiGridViewBebidasAdapter(Activity activity, ArrayList<PadreGridViewBebidas> bebidas) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.bebidas = bebidas;
		
	}

	public int getCount() {
		return bebidas.size();
	}

	public Object getItem(int arg0) {
		return bebidas.get(arg0);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vista=convertView;
        
	    if(convertView == null) {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vista = inflater.inflate(com.example.nfcook_camarero.R.layout.padre_grid_bebidas, null);
	    }
			
		// Obtenemos todos los textview para darles valor
		TextView textViewNombre = (TextView) vista.findViewById(R.id.textViewNombreBebida);
		TextView textViewUnidades = (TextView) vista.findViewById(R.id.textViewUnidadesBebida);
		TextView textViewTotal = (TextView) vista.findViewById(R.id.textViewPrecioBebida);
		
		// Damos valor a cada textview
		textViewNombre.setText(bebidas.get(position).getNombre());
		textViewUnidades.setText("Uds: " + bebidas.get(position).getUnidades());
		textViewTotal.setText(bebidas.get(position).getPrecioTotal() + " €");
		
		// Asignamos la imagen de la bebida
		ImageView imagenBebida = (ImageView) vista.findViewById(R.id.imageViewBebida);
		imagenBebida.setImageResource(context.getResources().getIdentifier(bebidas.get(position).getRutaFoto(),"drawable",context.getPackageName()));	
		
		// Guardamos la posición de cada uno
		final int pos = position;
		
		// Hacemos oyente a la imagen de añadir bebida
		ImageView imagenAnyadirBebida = (ImageView) vista.findViewById(R.id.imageViewAnyadirBebida);
		imagenAnyadirBebida.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				bebidas.get(pos).anyadeUnidad();
				AnadirBebida.actualizaGridView();				
			}});
		
		// Hacemos oyente a la imagen de añadir bebida
		ImageView imagenEliminarBebida = (ImageView) vista.findViewById(R.id.imageViewEliminarBebida);
		imagenEliminarBebida.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(bebidas.get(pos).getUnidades() > 0){
					bebidas.get(pos).eliminaUnidad();
					AnadirBebida.actualizaGridView();
				}
			}});
		
		return vista;
	}
	
	
	
	
		
	public void setUltimoIdentificadorUnicoHijoPedido(int identificadorUnicoHijoPedido){
		//Almacenamos la posicion del restaurante de la lista
		SharedPreferences preferencia = context.getSharedPreferences("Identificador_Unico", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("identificadorUnicoHijoPedido", identificadorUnicoHijoPedido);
		editor.commit(); //Para que surta efecto el cambio
	}
	
}
