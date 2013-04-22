package fragments;

import com.example.nfcook.R;

import usuario.InicializarMapas;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase encargada de implementar la pantalla de bienvenida del restaurante.
 * 
 * @author Abel
 *
 */
public class PantallaInicialRestaurante extends Fragment{
	
	private View vista;
	private ImageView logo;
	private String restaurante;

	@SuppressLint("DefaultLocale")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.inicio_restaurante, container, false);
		
		// Cargamos el mensaje de bienvenida
		TextView textViewMensajeBienvenida = (TextView)vista.findViewById(R.id.textViewBienvenidaRestaurante);
		
		// Cargamos el logo del restaurante
		logo = (ImageView)vista.findViewById(R.id.imageViewLogoRestaurantePantallaInicial);
		String ruta = "logo_" + restaurante.toLowerCase() + "_inicio";
		logo.setImageResource(getActivity().getResources().getIdentifier(ruta,"drawable",getActivity().getPackageName()));
		
		/*
		 * FIXME El nombre del resaturaurante Foster�s Hollywood aparece como Foster en la base de
		 * datos, si alg�n d�a desaparece este restaurante podremos quitar el siguiente if.
		 */
		String nombreRestaurante = restaurante;
		if(restaurante.equals("Foster")) 
			nombreRestaurante = "Foster's Hollywood";
		textViewMensajeBienvenida.setText("Bienvenido a \n" + nombreRestaurante);
		
		// Implementamos las acciones de los oyentes de las im�genes
		oyenteMapas();
		oyentePromociones();
		oyenteAyuda();
		oyenteImagenExplicativaAyuda();
		
		 // Vemos si es la primera vez que corremos la aplicaci�n para ayudar al usuario
        if(primeraVezIniciada()){
        	marcarAplicacionComoInicializada();
        	// Lanzamos la ayuda
			ImageView imageViewAyuda = (ImageView) vista.findViewById(R.id.imageViewAyudaPantallaInicial);
			imageViewAyuda.performClick();
        }
		
		return vista;
    }

	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
	
	public void oyenteMapas(){
		ImageView imageViewMapas = (ImageView) vista.findViewById(R.id.imageViewMapas);
		imageViewMapas.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),InicializarMapas.class);
		    	startActivity(intent);
			}
		});
	}
	
	public void oyentePromociones(){
		ImageView imageViewPromociones = (ImageView) vista.findViewById(R.id.imageViewPromociones);
		imageViewPromociones.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Fragment fragmentPedido = new PromocionesFragment();
				((PromocionesFragment) fragmentPedido).setRestaurante(restaurante);
				FragmentTransaction m = getFragmentManager().beginTransaction();
				m.replace(R.id.FrameLayoutPestanas, fragmentPedido);
				m.commit();
			}
		});
	}
	
	public void oyenteAyuda(){
		ImageView imageViewAyuda = (ImageView) vista.findViewById(R.id.imageViewAyudaPantallaInicial);
		imageViewAyuda.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				FrameLayout frameLayoutBienvenida = (FrameLayout) vista.findViewById(R.id.FrameLayoutBienvenida);

				ImageView imageViewAyuda = (ImageView) vista.findViewById(R.id.imageViewAyudaPantallaInicial);
				imageViewAyuda.setVisibility(ImageView.INVISIBLE);
						
				FrameLayout.MarginLayoutParams params = (MarginLayoutParams) frameLayoutBienvenida.getLayoutParams();
				params.setMargins(0, 0, 0, 0);
				frameLayoutBienvenida.setLayoutParams(params);
				
				ImageView imagenInfoAyuda = (ImageView) vista.findViewById(R.id.imageViewInformacionAyuda);
				imagenInfoAyuda.setVisibility(ImageView.VISIBLE);
			}
		});
	}

	public void oyenteImagenExplicativaAyuda(){
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
	
	public boolean primeraVezIniciada(){
		// Vemos si ya ha sido iniciada la aplicacion alguna vez
		SharedPreferences iniciada = getActivity().getSharedPreferences("Iniciada", 0);
		return iniciada.getInt("PrimeraVez", -1) == -1;
	}
	
	public void marcarAplicacionComoInicializada(){
		// Marcamos con 0 que la aplicaci�n ha sido inicializada
		SharedPreferences preferencia = getActivity().getSharedPreferences("Iniciada", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("PrimeraVez", 0);
		editor.commit();
	}
}
