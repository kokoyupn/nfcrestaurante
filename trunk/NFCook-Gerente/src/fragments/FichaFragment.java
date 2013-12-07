package fragments;

import java.util.ArrayList;

import adapters.HijoExpandableListFicha;
import adapters.HijoExpandableListFichaDatosContacto;
import adapters.HijoExpandableListFichaDatosLaborales;
import adapters.HijoExpandableListFichaDatosPersonales;
import adapters.HijoExpandableListFichaOtrosDatos;
import adapters.MiExpandableListAdapterFicha;
import adapters.PadreExpandableListFicha;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.nfcook_gerente.R;

public class FichaFragment extends Fragment{

	private View vista;
	private ArrayList<PadreExpandableListFicha> ficha;
	private ExpandableListView expandableListView;
	private MiExpandableListAdapterFicha miAdapterExpandableListFicha;

	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.ficha, container, false);
	   
		// Cargamos la estructura ficha, que contendrá toda la información que se mostrará en la Expandable List
	    prepararInformacionParaMostrar("");
	    
	    // Preparamos la expandble list con su adapter
	    prepararListView();
	    
	    // Dejamos abierto el primer hijo, es decir los datos personales por una cuestión estética
	    expandableListView.expandGroup(0, true);
	    
	    return vista;
	}

	private void prepararInformacionParaMostrar(String idEmpleado) {
		ficha = new ArrayList<PadreExpandableListFicha>();
		
		//TODO falta coger los datos reales del empleado de la BD
		
		HijoExpandableListFicha datos = new HijoExpandableListFichaDatosPersonales("","","","","","","");
		PadreExpandableListFicha tipoDato = new PadreExpandableListFicha("Datos Personales", datos);
		ficha.add(tipoDato);
		
		datos = new HijoExpandableListFichaDatosContacto("","","","","","","");
		tipoDato = new PadreExpandableListFicha("Datos de Contacto", datos);
		ficha.add(tipoDato);
		
		datos = new HijoExpandableListFichaDatosLaborales("",0,0,"","");
		tipoDato = new PadreExpandableListFicha("Datos Laborales", datos);
		ficha.add(tipoDato);
		
		datos = new HijoExpandableListFichaOtrosDatos("");
		tipoDato = new PadreExpandableListFicha("Otros datos", datos);
		ficha.add(tipoDato);
	}

	private void prepararListView() {
		expandableListView = (ExpandableListView) vista.findViewById(R.id.expandableListViewFicha);
    	miAdapterExpandableListFicha = new MiExpandableListAdapterFicha(getActivity().getApplicationContext(), ficha);
    	expandableListView.setAdapter(miAdapterExpandableListFicha);
    	
    	// Quitamos el oyente a la lista    
    	expandableListView.setOnItemClickListener(null);
	}
	
}