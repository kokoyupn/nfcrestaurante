package com.example.nfcook_camarero;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlatoView extends LinearLayout{

	private ImageView imagenPlato;
    private TextView nombrePlato;
    private String nombreP; 
    private String Imagen;
    private String idPlato;
    private Context contexto;
    
    public PlatoView(Context context) {
        super(context);
        contexto = context;
        inflate(context, R.layout.imagen_plato, this);    
    }

    public void setNombrePlato(String nom) {
    	nombreP=nom;
    	nombrePlato =(TextView)findViewById(R.id.textViewNombrePlato);
    	nombrePlato.setText(nom);
    }
    public String getNombrePlato(){
    	return nombreP;
    }
    
    public void setImagenPlato(String img) {
    	imagenPlato = (ImageView)findViewById(R.id.imageViewPlato);
    	imagenPlato.setImageResource(MiExpandableListAdapterAnadirPlato.getDrawable(contexto, img));
    	Imagen = img;
    }


	public String getImagen() {
		return Imagen;
	}

	public void setImageResource(PlatoView platoView) {
    	imagenPlato = platoView.imagenPlato;
    	nombrePlato = platoView.nombrePlato;
    	nombreP = platoView.nombreP;
		
	}
    
	public void setIdPlato(String id) {
    	idPlato = id;
    }
    public String getIdPlato(){
    	return idPlato;
    }
       
    
}
