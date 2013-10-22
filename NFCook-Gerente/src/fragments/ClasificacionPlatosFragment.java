package fragments;

import java.util.ArrayList;

import com.example.nfcook_gerente.PlatoClasificacion;
import com.example.nfcook_gerente.R;
import com.example.nfcook_gerente.R.color;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class ClasificacionPlatosFragment extends Fragment {

	private TableLayout tablaClasificacion;
	private ArrayList<PlatoClasificacion> platosFavoritosOrdenados;
	private View vista;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    vista = inflater.inflate(R.layout.clasificacion_platos, container, false);

		/* Consultaremos la base de datos de favoritos y la utilizaremos para mostrar los datos
		* Habrá que mirar solo por el restaurante recibido
		*/
		platosFavoritosOrdenados = new ArrayList<PlatoClasificacion>();
		
		platosFavoritosOrdenados.add(new PlatoClasificacion("Foster", "Hollywood Combo", "fh2", 12, 50));
		platosFavoritosOrdenados.add(new PlatoClasificacion("Foster", "Bacon Cheese Fries", "fh1", 9, 40));

		// Tabla de clasificacion de platos
		tablaClasificacion = (TableLayout) vista.findViewById(R.id.tableLayout);
		
		completaTablaPlatos("Demanda");
		
		return vista;
	}
	
public void onClickDemanda(View vista){
		
		// Aqui ordenaremos los platos en funcion de su demanda
		
		completaTablaPlatos("Demanda");
	}
	
	public void onClickFacturacion(View vista){
		
		// Aqui ordenaremos los platos en funcion de su facturacion
		
		completaTablaPlatos("Facturacion");
	}

	/* 
	 * Este metodo sera el encargado de completar la tabla con los platos ordenados 
	 * en funcion de la cantidad de facturacion o de la demanda de pedidos
	*/
	private void completaTablaPlatos(String tipo) {
		
		for (int plato=0; plato<platosFavoritosOrdenados.size(); plato++){
			
			PlatoClasificacion platoNuevo = platosFavoritosOrdenados.get(plato);
			
			// Vamos creando TableRow por cada plato
			TableRow rowPlato = new TableRow(getActivity());
		    LinearLayout.LayoutParams layoutP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		    rowPlato.setPadding(5, 10, 5, 10);
		    rowPlato.setLayoutParams(layoutP);
				

		    	String posicion = "" + (plato+1);
		    	
				TextView posPlato = aplicaLayoutTexto(posicion);
				
				TextView nombrePlato = aplicaLayoutTexto(platoNuevo.getNombre());
				
				TextView tipoPlato = aplicaLayoutTexto(tipo); // Elegimos el tipo por el que se ordenaran los platos				
				
				ImageView fotoPlato = new ImageView(getActivity());
				int imagen = getResources().getIdentifier(platoNuevo.getFoto(), "drawable", getActivity().getPackageName());
			    fotoPlato.setImageResource(imagen);
//			    LinearLayout.LayoutParams layoutImage = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				layoutImage.weight = (float) 0.2;
//			    fotoPlato.setLayoutParams(layoutImage);
			    fotoPlato.setBackgroundResource(getResources().getIdentifier("cell_style", "color", getActivity().getPackageName()));
			    
			rowPlato.addView(posPlato);
			rowPlato.addView(nombrePlato);
			rowPlato.addView(tipoPlato);
			rowPlato.addView(fotoPlato);

			// Añadimos la fila a la tabla
	        tablaClasificacion.addView(rowPlato, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		}
		
	}
	
	public TextView aplicaLayoutTexto(String texto){
		TextView text = new TextView(getActivity());
//		LinearLayout.LayoutParams layoutText = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		layoutText.weight = (float) 0.2;
//		layoutText.height = 20;
//    	text.setLayoutParams(layoutText);
		text.setBackgroundResource(getResources().getIdentifier("cell_style", "color", getActivity().getPackageName()));
		text.setGravity(1); // 1 = CENTER_HORIZONTAL
		text.setTextColor(color.blanco);
		text.setText(texto);

		return text;
	}
}
