package fragments;

import com.example.nfcook.R;

import adapters.ListaMapasAdapter;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import usuario.DescripcionRestaurante;
import usuario.Mapas;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListaMapasFragment extends Fragment {
	private View vista;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.lista_mapas, container, false);
		
		ListView listView = (ListView) vista.findViewById(R.id.listViewMapas);
		ListaMapasAdapter adapter = new ListaMapasAdapter(getActivity().getApplicationContext(), Mapas.getRestaurantes());
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				String rest = Mapas.getRestaurantes().get(pos).getNombre();
				Intent intent = new Intent(getActivity().getBaseContext(),DescripcionRestaurante.class);
				intent.putExtra("nombreRestaurante", rest);
		    	startActivity(intent);
				
			}
		});
		
		return vista;
	}
}
