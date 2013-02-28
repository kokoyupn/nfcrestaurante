package fragments;

import com.example.nfcook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PantallaInicialRestaurante extends Fragment{
	
	private View vista;
	private ImageView logo;
	private int imagen;
	private String restaurante;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.pestanas, container, false);
		logo = (ImageView)vista.findViewById(R.id.imageView1);
		logo.setImageResource(imagen);
		
		TextView bienvenida = (TextView)vista.findViewById(R.id.textViewBienvenidarestaurante);
		// Mal introducido el nombre del Foster en la base de datos
		if(restaurante.equals("Foster"))
			restaurante = "Foster's Hollywood";
		bienvenida.setText("Bienvenidos a \n"+restaurante);
		bienvenida.setTextSize(25);
		return vista;
    }
	
	public void setImagen(int imagen){
		this.imagen = imagen;
	}
	
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
}
