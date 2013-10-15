package adapters;

import java.util.ArrayList;

import com.example.nfcook_gerente.R;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;


/**
 * 
 * @author roberto
 * 
 * Esta clase contiene el adater para contenido de la lista con 
 * todos los empleados de un restaurante.
 *
 */

public class MiEmpleadosAdapter extends BaseAdapter{
	private ArrayList<PadreListaEmpleados> contenido;
	private Activity activity;
	private Context context;
	 
	 public MiEmpleadosAdapter(Activity activity, ArrayList<PadreListaEmpleados> contenido) {
		 this.activity = activity;
		 this.context = activity.getApplicationContext();
		 this.contenido = contenido;
	}
	 
	public int getCount() {
		return contenido.size();
	}
	
	public Object getItem(int pos) {
		return contenido.get(pos);
	}
	
	public long getItemId(int pos) {
		return 0;//contenido.get(pos).getDni();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vista = convertView;
        
	    if(convertView == null) {
	    	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        vista = inflater.inflate(R.layout.contenido_lista_empleados, null);
	    }
	             
	    PadreListaEmpleados empleado = contenido.get(position);
	     
	    ImageView foto = (ImageView) vista.findViewById(R.id.imagenEmpleado);
	    foto.setImageResource(context.getResources().getIdentifier(contenido.get(position).getFoto(),"drawable",context.getPackageName()));	
		
	    TextView nombre = (TextView) vista.findViewById(R.id.nombre);
	    nombre.setText(empleado.getNombre()+" "+empleado.getApellido1()+" "+empleado.getApellido2());
	    
	    TextView puesto = (TextView) vista.findViewById(R.id.puesto);
	    puesto.setText("Puesto: " + empleado.getPuesto());
	    
	    TextView idEmpleado = (TextView) vista.findViewById(R.id.idEmpleado);
	    idEmpleado.setText("Numero de empleado: " + empleado.getIdEmpleado());
	    
	   return vista;
	}
}

	


