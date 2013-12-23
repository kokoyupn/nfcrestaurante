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
	private ArrayList<PadreListaEmpleados> empleados;
	private Activity activity;
	private Context context;
	 
	 public MiEmpleadosAdapter(Activity activity, ArrayList<PadreListaEmpleados> contenido) {
		 this.activity = activity;
		 this.context = activity.getApplicationContext();
		 this.empleados = contenido;
	}
	 
	@Override
	public int getCount() {
		return empleados.size();
	}
	
	@Override
	public Object getItem(int pos) {
		return empleados.get(pos);
	}
	
	@Override
	public long getItemId(int pos) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vista = convertView;
        
	    if(convertView == null) {
	    	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        vista = inflater.inflate(R.layout.contenido_lista_empleados, null);
	    }
	             
	    PadreListaEmpleados empleado = empleados.get(position);
	     
	    ImageView foto = (ImageView) vista.findViewById(R.id.imagenEmpleado);
	    foto.setImageResource(context.getResources().getIdentifier(empleados.get(position).getFoto(),"drawable",context.getPackageName()));	
		
	    TextView nombre = (TextView) vista.findViewById(R.id.nombre);
	    nombre.setText(empleado.getNombre()+" "+empleado.getApellido1()+" "+empleado.getApellido2());
	    
	    TextView puesto = (TextView) vista.findViewById(R.id.puesto);
	    puesto.setText(empleado.getPuesto());
	    
	   return vista;
	}
}

	


