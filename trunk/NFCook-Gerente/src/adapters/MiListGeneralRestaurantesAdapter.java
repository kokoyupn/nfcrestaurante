package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook_gerente.R;


public class MiListGeneralRestaurantesAdapter extends BaseAdapter{

	 protected ArrayList<PadreListRestaurantes> restaurantes;
	 private Context context;
	 private LayoutInflater l_Inflater;
	 
	 public MiListGeneralRestaurantesAdapter(Context context, ArrayList<PadreListRestaurantes> restaurantes) {
		 this.restaurantes = restaurantes;
		 this.l_Inflater = LayoutInflater.from(context);
		 this.context = context;
	 }

	@Override
	public int getCount() {
		return restaurantes.size();
	}

	@Override
	public Object getItem(int position) {
		return restaurantes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return restaurantes.get(position).getIdRestaurante();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vista=convertView;
        
	    if(convertView == null) {
			vista = l_Inflater.inflate(R.layout.contenido_lista_restaurantes,null);
	    }
	             
	    final PadreListRestaurantes unRestaurante = restaurantes.get(position);
 	    
	    TextView nombre = (TextView) vista.findViewById(R.id.nombreRestaurante);
	    nombre.setText(unRestaurante.getNombreRestaurante());
	    
	    TextView calle = (TextView) vista.findViewById(R.id.calle);
	    calle.setText(unRestaurante.getCalle());	
	    
	    //ponemos visible o invisible el checkbox y desplazamos para que quede bien esteticamente
	    final CheckBox check = (CheckBox) vista.findViewById(R.id.checkRestaurante);
	    if (unRestaurante.isCheckVisibles()){
	    	check.setVisibility(0);
	    	//desplazamos a la derecha
	    	calle.setPadding(60, 0, 0, 0);
	    	nombre.setPadding(60, 0, 0, 0);

	    }else{
	    	//El 4 significa invisible
	    	check.setVisibility(4);
	    	//desplazamos a la izquierda
	    	calle.setPadding(0, 0, 0, 0);
	    	nombre.setPadding(0, 0, 0, 0);
	    }
	    
	    check.setOnClickListener(new OnClickListener() {
			
	    	@Override
			public void onClick(View v) {
	    		if (check.isChecked()){
	    	    	unRestaurante.setSelected(true);
	    	    }else unRestaurante.setSelected(false);
			}
		});
	    
	    ImageView imagen = (ImageView) vista.findViewById(R.id.imagenRestaurante);
	    
	    int img = context.getResources().getIdentifier(unRestaurante.getImagen(),"drawable", context.getPackageName());
	    
	    imagen.setImageResource(img);

	    return vista;
	    }
	
}
