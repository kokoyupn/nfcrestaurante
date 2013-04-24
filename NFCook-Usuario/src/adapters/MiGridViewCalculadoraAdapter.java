package adapters;

import java.util.ArrayList;

import usuario.Calculadora;

import com.example.nfcook.R;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Clase encargada de implementar el adapter del gridview que mostrará la información de 
 * todos los comensales.
 * 
 * La información de los comensales se guarda en el array list personas.
 * 
 * También guardamos la información de todos los platos que se han consumido para poder
 * ir asignándoselos a los comensales.
 * 
 * En este adapter es también dónde se aplica el adapter de la lista de platos que ha
 * consumido cada persona. De esta forma una vez que repartamos un plato y haya que actualizar
 * el total a pagar de algunos comensales y la lista de platos de los mismos, hay que hacer
 * un set adapter sobre esta clase para que la información se vea actualizada.
 * 
 * @author Abel
 *
 */
public class MiGridViewCalculadoraAdapter extends BaseAdapter{
	private static ArrayList<PadreGridViewCalculadora> personas;
	private LayoutInflater l_Inflater;
	private Context context;
	private ArrayList<InformacionPlatoCantidad> platos;
	
	// Atributo para evitar conflictos en los edittext de los nombres de usuario
	private int edit = -1;
	
	public MiGridViewCalculadoraAdapter(Context context, ArrayList<PadreGridViewCalculadora> pers) {
		personas = pers;
		this.l_Inflater = LayoutInflater.from(context);
		this.context = context;
	}
	
	public static ArrayList<PadreGridViewCalculadora> getPersonas(){
		return personas;
	}
	
	public static void anyadirPlatoPersona(int posPersona, String idPlatoEnPedido, String nombrePlato, double precio, int numPersonas){
		personas.get(posPersona).anadePlato(idPlatoEnPedido, nombrePlato, precio, numPersonas);
	}
	
	public static void reajustarPrecioPlato(int posPersona, String idPlatoEnPedido, int numPersonas){
		personas.get(posPersona).reajustaTotalPersona(idPlatoEnPedido, numPersonas);
	}
	
	public static void quitarPlatoPersona(int posPersona, String idPlatoEnPedido){
		personas.get(posPersona).quitaPlato(idPlatoEnPedido);
	}

	public int getCount() {
		return personas.size();
	}

	public Object getItem(int arg0) {
		return personas.get(arg0);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.padre_grid_calculadora, null);
		}
		
		// Obtenemos todos los campos de texto para darles valor
		final EditText editTextNombrePersona = (EditText) convertView.findViewById(R.id.editTextNombrePersonaCalculadora);
		TextView textViewTotal = (TextView) convertView.findViewById(R.id.textViewTotalPagarPersonaCalculadora);
		final ListView listaPlatosPersona = (ListView) convertView.findViewById(R.id.listViewPlatosPersona);
		
		/*
		 *  Hacemos oyente al campo de texto del nombre de la persona, por si lo cambia
		 *  que se vea reflejado a la hora de que salgan los nombres de los comensales
		 *  en el reparto de cada plato.
		 */		
		editTextNombrePersona.addTextChangedListener(new TextWatcher() {
			
			public void afterTextChanged(Editable s) {
	        	
	        }
	          
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Actualizamos el nombre
				/*
				 * FIXME Problemas con el nombre del primer usuario que se pone el último
				 */
				if(pos == edit){
					if(!personas.get(personas.size()-1).getNombre().equals(editTextNombrePersona.getText().toString())){
						personas.get(pos).setNombre(editTextNombrePersona.getText().toString());
					}
				}
			}
		});
		
		editTextNombrePersona.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//Se invoca cada vez que pinchamos sobre el o salimos de el
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){ // entramos de el
					edit = pos;
				}
			}
		});
		
		//oyente para cuando se pulse el boton Ok
		editTextNombrePersona.setOnEditorActionListener(new OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					editTextNombrePersona.clearFocus();
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(editTextNombrePersona.getWindowToken(), 0);
					return true;
	            }
	            return false;
				
				
			}
		});
		
		//Limitamos a 11 el máximo caracteres para el nombre de la persona
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(11);
		editTextNombrePersona.setFilters(filterArray);
		
		// Damos valor a los campos que aparecerán por pantalla
		editTextNombrePersona.setText(personas.get(position).getNombre());
		textViewTotal.setText(personas.get(position).getTotal() + " €");
		
		// Vemos los platos consumidos por el usuario
		int numPlatos = personas.get(position).getNumPlatos();
		platos = new ArrayList<InformacionPlatoCantidad>();
		for (int i=0; i<numPlatos; i++){
			platos.add(personas.get(position).getPlato(i));
		}
		
		// Metodo encargado de que la lista de platos de cada comensal tenga scroll
		listaPlatosPersona.setOnTouchListener(new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            v.getParent().requestDisallowInterceptTouchEvent(true);
	            return false;
	        }
	    });
				
		/*
		 * Metodo encargado de implementar el oyente de la lista de platos de cada persona
		 * para mostrarle un mensaje informativo de lo que paga del total del plato.
		 */
		listaPlatosPersona.setOnItemClickListener(new ListView.OnItemClickListener()
    	{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
		        Toast.makeText(context,"Te toca pagar " + personas.get(pos).getPlato(arg2).getPrecioPagar() + " €, de " +  personas.get(pos).getPlato(arg2).getPrecioPlato() + " € que cuesta el plato.",Toast.LENGTH_SHORT).show();
			}
    	});
		
		// Aplicamos el adapter sobre la lista		
		final MiListPlatosPersonaCalculadora miListAdaper = new MiListPlatosPersonaCalculadora(context, platos);
		listaPlatosPersona.setAdapter(miListAdaper);

		// Implementamos el oyente de eliminar un usuario de la pantalla calculadora
		final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewEliminarPersonaCalculadora);
		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(context,personas.get(pos).getNombre() + ", se ha eliminado con éxito",Toast.LENGTH_SHORT).show();
				// Actualizamos la información de los comensales que compartían plato con él
				actualizaInfoPersonasCompartianPlatos(pos);
				// Eliminamos a la persona
				personas.remove(pos);
				// Aplicamos el adapeter del gridview personas para que se vea actualizada la información
				Calculadora.actualizaGridViewPersonas();
				// Eliminamos la persona
				Calculadora.eliminaPersona(pos);
				MiViewPagerAdapter.personaEliminada(pos);
				
				/*
				 * FIXME Problema nombre primer usuario le pone el del último
				 */
				edit = -1;
			}
		});
		
		return convertView;
	}
	
	/*
	 * Metodo encargado de sacar los id´s únicos en pedido de cada uno de los platos
	 * que tiene un usuario que vamos a eliminar para actualizar la información de 
	 * aquellos usuarios que compartían esos platos con él.
	 */
	public void actualizaInfoPersonasCompartianPlatos(int posPersona){
		int numPlatosPersona = personas.get(posPersona).getNumPlatos();
		int numPersonas = personas.size();
		String idPlatoEnPedido;
		// Recorremos todos los platos del usuario para ver el id de cada uno
		for(int i=0; i<numPlatosPersona; i++){
			// Sacamos el idUnicoPedido de cada uno de los platos
			idPlatoEnPedido = personas.get(posPersona).dameIdPlatoEnPedido(i);
			// Recorremos el resto de usuarios
			for(int j=0; j<numPersonas; j++){
				// Recorremos los platos de cada uno de ellas menos de él mismo
				if(j != posPersona){
					int numPlatos = personas.get(j).getNumPlatos();
					for(int k=0; k<numPlatos; k++){
						// Si esa persona comparte el plato con él
						if(idPlatoEnPedido.equals(personas.get(j).dameIdPlatoEnPedido(k))){
							// Reajustamos el precio total de esa persona
							reajustarPrecioPlato(j, idPlatoEnPedido, personas.get(j).dameNumeroPersonasCompartidoPlato(k) - 1);
						}
					}
				}
			}
		}
	}

}
