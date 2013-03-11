package fragments;

import com.example.nfcook.R;

import android.annotation.SuppressLint;
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
	private String restaurante;
	
	@SuppressLint("DefaultLocale")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.inicio_restaurante, container, false);
		
		// Cargamos el logo del restaurante para la pantalla de bienvenida
		String nombreLogo = "logo_" + restaurante.toLowerCase() + "_inicio";
		logo = (ImageView)vista.findViewById(R.id.ImageViewLogoInicioRestaurante);
		logo.setImageResource(getResources().getIdentifier(nombreLogo,"drawable",this.getActivity().getPackageName()));
		
		TextView bienvenida = (TextView)vista.findViewById(R.id.textViewBienvenidaRestaurante);
		// Mal introducido el nombre del Foster en la base de datos
		if(restaurante.equals("Foster"))
			restaurante = "Foster's Hollywood";
		bienvenida.setText("Bienvenido a \n" + restaurante);
		return vista;
    }
	
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
}
