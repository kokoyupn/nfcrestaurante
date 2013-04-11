package usuario;

import com.example.nfcook.R;

import adapters.ListaMapasAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;


public class ListaMapas extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_mapas);
		
		ListView listView = (ListView) findViewById(R.id.listViewMapas);
		ListaMapasAdapter adapter = new ListaMapasAdapter(getApplicationContext(), Mapas.getRestaurantes());
		listView.setAdapter(adapter);
	}
}
