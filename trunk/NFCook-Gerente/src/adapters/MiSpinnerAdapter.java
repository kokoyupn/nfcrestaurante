package adapters;

import java.util.ArrayList;

import com.example.nfcook_gerente.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MiSpinnerAdapter extends ArrayAdapter<ContenidoSpinnerHijo>{
	 private Context context;
	 private ArrayList<ContenidoSpinnerHijo> datosSpinner;
	 
	  
	
	public MiSpinnerAdapter( Context context, ArrayList<ContenidoSpinnerHijo> datosSpinner) {
		super(context, R.layout.contenido_spinner_padre, datosSpinner);
		this.datosSpinner = datosSpinner;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vista=convertView;
		if(convertView == null) {
			vista = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contenido_spinner_padre,null);
			//vista = l_Inflater.inflate(R.layout.contenido_spinner_padre,null);
	    }
	    TextView texto = (TextView) vista.findViewById(R.id.textViewSpinnerPadre);
	    texto.setText(datosSpinner.get(position).getDato()+"");    
    
	    return vista; 
	} 
	
	  
	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
    {
        View row = convertView;
        if (row == null) 
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.contenido_spinner_hijo, parent, false);
        }
 
        if (row.getTag() == null) 
        {
            ListaHolder listaHolder = new ListaHolder();
            listaHolder.setTextView((TextView) row.findViewById(R.id.textViewSpinnerHijo));
            row.setTag(listaHolder);
        }
 
        //rellenamos el layout con los datos de la fila que se está procesando
        ContenidoSpinnerHijo lista = datosSpinner.get(position);          
        ((ListaHolder) row.getTag()).getTextView().setText(lista.getDato()+"");
 
        return row;
    }
	
	private class ListaHolder
    {
 
        private TextView textView;

        public TextView getTextView() 
        {
            return textView;
        }
 
        public void setTextView(TextView textView) 
        {
            this.textView = textView;
        }
 
    } 
 
}
