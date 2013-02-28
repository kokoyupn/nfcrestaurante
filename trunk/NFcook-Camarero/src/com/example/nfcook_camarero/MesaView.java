package com.example.nfcook_camarero;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MesaView extends LinearLayout{
 
	private TextView numMesa;
	private String numM; // atributo que indica el numero de la mesa en formato String para facilitar accesos
    private TextView numPersonas;
    private String numP;
    
    public MesaView(Context context) {
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
    	return numM;
    }
}
