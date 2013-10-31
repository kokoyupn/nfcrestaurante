package fragments;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.example.nfcook_gerente.PlatoClasificacion;

public class ClasificacionPlatosFragment extends Fragment {

	private TableLayout tablaClasificacion;
	private ArrayList<PlatoClasificacion> platosFavoritos;
	private TableRow rowPlato;
	private ImageView fotoPlato;
	private TextView posPlato, nombrePlato;
	private View vista;
	private Button botonFacturacion, botonDemanda;
	private HandlerGenerico sqlRestaurantes;
	private SQLiteDatabase dbRestaurantes;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    vista = inflater.inflate(R.layout.clasificacion_platos, container, false);

		/* Consultaremos la base de datos de favoritos y la utilizaremos para mostrar los datos
		* Habrá que mirar solo por el restaurante recibido
		*/
		platosFavoritos = new ArrayList<PlatoClasificacion>();
		
//		platosFavoritos.add(new PlatoClasificacion("Foster", "Hollywood Combo", "fh2", 12, 40)); // facturacion 480
//		platosFavoritos.add(new PlatoClasificacion("Foster", "Bacon Cheese Fries", "fh1", 9, 41)); // facturacion 369
//		platosFavoritos.add(new PlatoClasificacion("Foster", "Nachos San Fernando", "fh3", 10, 30)); // facturacion 300
//		platosFavoritos.add(new PlatoClasificacion("Foster", "Iberian Ribs", "fh9", 15, 25)); // facturacion 375
//		platosFavoritos.add(new PlatoClasificacion("Foster", "Director Choice", "fh11", 11, 55)); // facturacion 605
//		
		importarBaseDatos();

			// Tabla de clasificacion de platos
			tablaClasificacion = (TableLayout) vista.findViewById(R.id.tableLayout);
			
			botonDemanda = (Button) vista.findViewById(R.id.buttonDemanda);
		    botonDemanda.setOnClickListener(new View.OnClickListener() {
		        @Override
				public void onClick(View arg0) {
		        	onClickDemanda(false); 	        	
		        }
		    });
	
		    botonFacturacion = (Button) vista.findViewById(R.id.buttonFacturacion);
		    botonFacturacion.setOnClickListener(new View.OnClickListener() {
		        @Override
				public void onClick(View arg0) {
		        	onClickFacturacion(); 	        	
		        }
		    });

	    onClickDemanda(true);
	    
		return vista;
	}
	
	
	public void importarBaseDatos(){
		//Importamos la base de datos
		try {
			sqlRestaurantes = new HandlerGenerico(getActivity().getApplicationContext(),
					"/data/data/com.example.nfcook_gerente/databases/",
					"MiBaseFav.db"); 
			dbRestaurantes = sqlRestaurantes.open();
		} catch (SQLiteException e) {
			System.out.println("CATCH");
			Toast.makeText(getActivity().getApplicationContext(),
					"NO EXISTE LA BASE DE DATOS", Toast.LENGTH_SHORT).show(); 
		}
		
		//Leemos los datos de la base de datos
		try{
		String[] campos = new String[]{"Restaurante","Nombre","Foto","Precio","CantidadPedido"};
	   
		Cursor c = dbRestaurantes.query("Restaurantes", campos, null, null, null, null,null);
	    
		//Cargamos los datos en la los atributos correspondientes de la clase
		platosFavoritos = new ArrayList<PlatoClasificacion>();
		while(c.moveToNext()){
			platosFavoritos.add(new PlatoClasificacion(	c.getString(0),	// restaurante
														c.getString(1),	// nombre
														c.getString(2), // foto
														c.getInt(3), 	// precio
														c.getInt(4))); 	// cantidadPedido
														
		}
	   
		}catch(Exception e){
			System.out.println("Error en la carga de Clasificacion de Platos");
		}
		
	}
	
	
	public void onClickDemanda(boolean primeraVez){
		
		// Ordenaremos los platos en funcion de su demanda
		ordenaPlatos(0); // 0 = Demanda	
		
		// borramos desde 2 porque la primera son los botones y la segunda la cabecera de la tabla
		if (!primeraVez)
			tablaClasificacion.removeViews(2, platosFavoritos.size()); 
		
		completaTablaPlatos(platosFavoritos);
	    botonDemanda.setBackgroundResource(getResources().getIdentifier("ic_demandaclickado", "drawable", getActivity().getPackageName()));
	    botonFacturacion.setBackgroundResource(getResources().getIdentifier("ic_facturacion2", "drawable", getActivity().getPackageName()));
	    botonDemanda.refreshDrawableState();
		botonFacturacion.refreshDrawableState();
		
    	Toast.makeText(getActivity().getApplicationContext(),"Platos ordenados por demanda",Toast.LENGTH_SHORT).show();	

	}
	
	
	public void onClickFacturacion(){
		
		// Ordenaremos los platos en funcion de su facturacion
		ordenaPlatos(1); // 1 = Facturacion
		
		// borramos desde 2 porque la primera son los botones y la segunda la cabecera de la tabla
		tablaClasificacion.removeViews(2, platosFavoritos.size());
		
		completaTablaPlatos(platosFavoritos); 
		botonFacturacion.setBackgroundResource(getResources().getIdentifier("ic_facturacion2clickado", "drawable", getActivity().getPackageName()));
	    botonDemanda.setBackgroundResource(getResources().getIdentifier("ic_demanda", "drawable", getActivity().getPackageName()));
		botonDemanda.refreshDrawableState();
		botonFacturacion.refreshDrawableState();

		Toast.makeText(getActivity().getApplicationContext(),"Platos ordenados por facturación",Toast.LENGTH_SHORT).show();	
	}
	
	
	
	/*
	 * Este metodo ordenara los platos de la lista en funcion
	 * de si queremos el orden por Demanda (tipo = 0) o por Facturacion (tipo = 1).
	 * Usaremos el algoritmo de ordenacion por Seleccion.
	 * */
	public void ordenaPlatos(int tipo){
		
		int posMax = 0;
		
		for (int i=0; i<platosFavoritos.size()-1; i++) {
			posMax = i;
			for (int j=i+1; j<platosFavoritos.size(); j++) {
				if (tipo == 0){ // Demanda
					if(platosFavoritos.get(j).getCantidadPedido() > platosFavoritos.get(posMax).getCantidadPedido()){
						posMax = j;
					}
				}else { // Facturacion
					if (platosFavoritos.get(j).getFacturacion() > platosFavoritos.get(posMax).getFacturacion()){
						posMax = j;
					}
				}
			}
			// intercambiar
			if (i != posMax){
				PlatoClasificacion aux = platosFavoritos.get(i);
				platosFavoritos.set(i, platosFavoritos.get(posMax));
				platosFavoritos.set(posMax, aux);
			}
		}
	}
	

	/* 
	 * Este metodo sera el encargado de completar la tabla con los platos ordenados 
	 * en funcion de la cantidad de facturacion o de la demanda de pedidos.
	 * Si parametro 'tipo' == 0 -> Demanda, si 'tipo' == 1 -> Facturacion
	*/
	private void completaTablaPlatos(ArrayList<PlatoClasificacion> platosFavoritosOrdenados) {
		
		for (int plato=0; plato<platosFavoritosOrdenados.size(); plato++){
			
			PlatoClasificacion platoNuevo = platosFavoritosOrdenados.get(plato);
			
			// Vamos creando TableRow por cada plato
			rowPlato = new TableRow(getActivity());
			TableRow.LayoutParams layoutP = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			rowPlato.setLayoutParams(layoutP);

			    fotoPlato = new ImageView(getActivity());
				int imagen = getResources().getIdentifier(platoNuevo.getFoto(), "drawable", getActivity().getPackageName());
			    fotoPlato.setImageResource(imagen);
			    TableRow.LayoutParams layoutImagen = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.3f);
		    	fotoPlato.setLayoutParams(layoutImagen);
		    	fotoPlato.setBackgroundResource(getResources().getIdentifier("cell_style", "color", getActivity().getPackageName()));
		    	fotoPlato.setPadding(0, 10, 0, 10);
		    			    	
		    	String posicion = "" + (plato+1);
		    	
				posPlato = aplicaLayoutTexto(posicion);
				TableRow.LayoutParams layoutTextPos = new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.12f);
				posPlato.setLayoutParams(layoutTextPos);
		    	
				nombrePlato = aplicaLayoutTexto(platoNuevo.getNombre());
				TableRow.LayoutParams layoutTextNombre = new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.58f);
		    	nombrePlato.setLayoutParams(layoutTextNombre);
		    	
				
			rowPlato.addView(posPlato);
			rowPlato.addView(nombrePlato);
			rowPlato.addView(fotoPlato);

			// Añadimos la fila a la tabla
	        tablaClasificacion.addView(rowPlato, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		}		
	}
	
	/* Este metodo aplica el formato a los TextViews creados dinamicamente */
	public TextView aplicaLayoutTexto(String texto){
		TextView text = new TextView(getActivity());
		text.setBackgroundResource(getResources().getIdentifier("cell_style", "color", getActivity().getPackageName()));
		text.setGravity(Gravity.CENTER);
		text.setTextSize(17);
		text.setText(texto);

		return text;
	}


}
