package fragments;

import usuario.InicializarRestaurante;

import com.example.nfcook.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	
		String restauranteAux = restaurante;
		if(restaurante.equals("Foster")) 
			restauranteAux = "Foster's Hollywood";
		bienvenida.setText("Bienvenido a \n" + restauranteAux);
		
		/*
		//listener de las imagenes
		ponerOnclickMapas();
		ponerOnclickListaMapas();*/
		ponerOnClickAyuda();
		ponerOnClickInformacionAyuda();
		
		return vista;
    }
	/*
	private void ponerOnclickMapas() {
		
		Button botonMapas = (Button) vista.findViewById(R.id.buttonMapas);
		botonMapas.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), Mapas.class);
		    	startActivity(intent);
			}
		});
	}

	private void ponerOnclickListaMapas() {
		
		Button botonMapas = (Button) vista.findViewById(R.id.buttonListaMapas);
		botonMapas.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ListaMapas.class);
		    	startActivity(intent);
			}
		});
	}
*/
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
	
	
	private void ponerOnClickAyuda() {
		
		ImageView ayuda = (ImageView) vista.findViewById(R.id.imageViewAyuda);
		ayuda.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				FrameLayout frameLayoutBienvenida = (FrameLayout) vista.findViewById(R.id.FrameLayoutBienvenida);

				ImageView ayuda = (ImageView) vista.findViewById(R.id.imageViewAyuda);
				ayuda.setVisibility(ImageView.INVISIBLE);
						
				FrameLayout.MarginLayoutParams params = (MarginLayoutParams) frameLayoutBienvenida.getLayoutParams();
				params.setMargins(0, 0, 0, 0);
				frameLayoutBienvenida.setLayoutParams(params);
				
				ImageView imagenInfoAyuda = (ImageView) vista.findViewById(R.id.imageViewInformacionAyuda);
				imagenInfoAyuda.setVisibility(ImageView.VISIBLE);
				
			}
		});
	}

	private void ponerOnClickInformacionAyuda() {
		
		ImageView informacionAyuda = (ImageView) vista.findViewById(R.id.imageViewInformacionAyuda);
		informacionAyuda.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Cargamos en el fragment la pantalla de bienvenida del restaurante
				Fragment fragmentPantallaInicioRes = new PantallaInicialRestaurante();
				((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
				FragmentTransaction m = getFragmentManager().beginTransaction();
				m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
				m.commit();	
			}
		});
	}
	
}
