package fragments;

import java.util.ArrayList;

import com.example.nfcook.R;
import adapters.ListaMapasAdapter;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import usuario.DescripcionRestaurante;
import usuario.Mapas;
import usuario.Restaurante;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListaMapasFragment extends Fragment {
	private View vista;
	private ArrayList<Restaurante> restaurantes;
	private String restaurante;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.lista_mapas, container, false);
		
		hacerCopiaRestaurantes(Mapas.getRestaurantes());
		
		ListView listView = (ListView) vista.findViewById(R.id.listViewMapas);
		ListaMapasAdapter adapter = new ListaMapasAdapter(getActivity().getApplicationContext(), restaurantes, restaurante, getActivity());
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				String restaurante = Mapas.getRestaurantes().get(pos).getNombre();
				Intent intent = new Intent(getActivity().getApplicationContext(), DescripcionRestaurante.class);
				intent.putExtra("nombreRestaurante", restaurante);
				startActivity(intent);				
			}
		});
		
		return vista;
	}
	
	public void hacerCopiaRestaurantes(ArrayList<Restaurante> restaurantes){
		// Cremos el array de los restaurantes
		this.restaurantes = new ArrayList<Restaurante>();
		
		Restaurante res, nuevoRes;
		for(int i=0; i<restaurantes.size();i++){
			res = restaurantes.get(i);
			nuevoRes = new Restaurante(res.getNombre(), res.getMarcador(), res.getDistancia(), res.getDireccion(), res.getTelefono(), res.getHorario(), res.getURL(), res.isTakeAway(), res.isDelivery(), res.isMenuMediodia(), res.isMagia(), res.isCumpleanios());
			this.restaurantes.add(nuevoRes);
		}
		
	}
	
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
}
