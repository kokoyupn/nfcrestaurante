package adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfcook_gerente.R;

/**
 * Esta clase es el adapter para la lista de la pantalla general de gerente
 * 
 * @author Guille
 *
 */

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
	public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();
	    	convertView = l_Inflater.inflate(R.layout.contenido_lista_restaurantes,null);
			holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkRestaurante));
            convertView.setTag(holder);
	    }else{
            holder = (Holder) convertView.getTag(); 
        }
        
	    //Obtenemos el restaurante para poder sacar posteriormente los datos         
	    final PadreListRestaurantes unRestaurante = restaurantes.get(position);
	  	    
	    TextView nombre = (TextView) convertView.findViewById(R.id.nombreRestaurante);
	    nombre.setText(unRestaurante.getNombreRestaurante());
	    
	    TextView calle = (TextView) convertView.findViewById(R.id.calle);
	    calle.setText(unRestaurante.getCalle());	
	    
	    holder.getCheckBox().setTag(unRestaurante.getIdRestaurante());
        holder.getCheckBox().setChecked(unRestaurante.isSelected());       
        holder.getCheckBox().setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked)
            {
                if (unRestaurante.getIdRestaurante() == (Integer)view.getTag())
                {
                	unRestaurante.setSelected(isChecked);
                }
            }
        });
	    
        
        
        final CheckBox check = (CheckBox) convertView.findViewById(R.id.checkRestaurante);
	    //ponemos visible o invisible el checkbox y desplazamos para que quede bien esteticamente
	    if (unRestaurante.isCheckVisibles()){
	    	check.setVisibility(0);
	    	//desplazamos a la derecha
	    	calle.setPadding(80, 0, 0, 0);
	    	nombre.setPadding(80, 0, 0, 0);

	    }else{
	    	//El 4 significa invisible
	    	check.setVisibility(4);
	    	//desplazamos a la izquierda
	    	calle.setPadding(0, 0, 0, 0);
	    	nombre.setPadding(0, 0, 0, 0);
	    }      
	    
	    //obtenemos la imagen y la cargamos en su lugar
	    ImageView imagen = (ImageView) convertView.findViewById(R.id.imagenRestaurante);    
	    int img = context.getResources().getIdentifier(unRestaurante.getImagen(),"drawable", context.getPackageName());
	    imagen.setImageResource(img); 

	    return convertView;
	    }
	
	
	
	
	
	class Holder
	{
	    CheckBox checkBox;
	 
	    public CheckBox getCheckBox()
	    {
	        return checkBox;
	    }
	 
	    public void setCheckBox(CheckBox checkBox)
	    {
	        this.checkBox = checkBox;
	    }   
	 
	}
	
	
}
