package usuario;

import java.util.ArrayList;

import com.example.nfcook.R;

import adapters.ListaMapasAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;


public class ListaMapas extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//if(restaurantes == null)
		//	restaurantes = new ArrayList<Restaurante>();
		//Restaurante r = new Restaurante("MI nabo", 1.999, 2.4);
		//restaurantes.add(r);
		setContentView(R.layout.lista_mapas);
		
		ListView listView = (ListView) findViewById(R.id.listViewMapas);
		ListaMapasAdapter adapter = new ListaMapasAdapter(getApplicationContext(), Mapas.getRestaurantes());
		listView.setAdapter(adapter);
	}
}
