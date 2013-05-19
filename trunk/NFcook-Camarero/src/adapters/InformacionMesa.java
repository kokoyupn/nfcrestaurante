package adapters;
import com.example.nfcook_camarero.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InformacionMesa extends LinearLayout{
 
    private TextView numMesa;
    private TextView numPersonas;
    private String numM; // atributo que indica el numero de la mesa en formato String para facilitar accesos
    private String numP;  // atributo que indica el numero de personasen una mesa en formato String para facilitar accesos
    
    public InformacionMesa(Context context) {
        super(context);
        inflate(context, R.layout.imagen_mesa, this);    
    }

    public void setNumMesa(String num) {
    	numMesa =(TextView)findViewById(R.id.textViewMesa);
    	numMesa.setText(num);
    	numM=num;
    }
    public String getNumMesa(){
    	return numM;
    }
    
    public void setNumPersonas(String num) {
    	numPersonas =(TextView)findViewById(R.id.textViewPersonas);
    	numPersonas.setText(num);
    	numP=num;
    }
    
    public String getNumPersonas(){
    	return numP;
    }

	public void setImageResource(InformacionMesa mesaView) {
		numMesa = mesaView.numMesa;
		numPersonas = mesaView.numPersonas;
		numM = mesaView.numM;
		numP = mesaView.numP;		
	}
}
