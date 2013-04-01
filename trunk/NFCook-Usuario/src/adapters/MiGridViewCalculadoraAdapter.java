package adapters;

import java.util.ArrayList;

import com.example.nfcook.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
	MiListPlatosPersonaCalculadora miListAdaper;
	ListView listaPlatosPersona;
	
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
		listaPlatosPersona = (ListView) convertView.findViewById(R.id.listViewPlatosPersona);
		
		/*
		 *  Hacemos oyente al campo de texto del nombre de la persona, por si lo cambia
		 *  que se vea reflejado a la hora de que salgan los nombres de los comensales
		 *  en el reparto de cada plato.
		 */		
		editTextNombrePersona.addTextChangedListener(new TextWatcher() {
			
			public void afterTextChanged(Editable s) {
	        	// Comprobamos que el nombre no mide más de 13 caracteres
	        	if(editTextNombrePersona.getText().toString().length() <= 13){
	        		personas.get(pos).setNombre(editTextNombrePersona.getText().toString());
	        	}else{
	        		// Si tiene más de 13 caracteres el nombre de la persona le avisamo y no dejamos escribir más
	        		Toast.makeText(context,"El nombre de la persona no puede tener más de 13 caracteres.",Toast.LENGTH_SHORT).show();
	        		String nombre = editTextNombrePersona.getText().toString().substring(0, 13);
	        		editTextNombrePersona.setText(nombre);
	        	}
	        }
	          
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				  
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				  
			}
		});
		
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
		miListAdaper = new MiListPlatosPersonaCalculadora(context, platos);
		listaPlatosPersona.setAdapter(miListAdaper);
		
		return convertView;
	}

}
