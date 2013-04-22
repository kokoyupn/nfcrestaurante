package fragments;

import com.example.nfcook.R;

import adapters.ListaMapasAdapter;
import android.os.Bundle;
import android.widget.ListView;
import usuario.Mapas;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListaMapasFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View vista = inflater.inflate(R.layout.lista_mapas, container, false);
		
		ListView listView = (ListView) vista.findViewById(R.id.listViewMapas);
		ListaMapasAdapter adapter = new ListaMapasAdapter(getActivity().getApplicationContext(), Mapas.getRestaurantes());
		listView.setAdapter(adapter);
		
		return vista;
	}
}
