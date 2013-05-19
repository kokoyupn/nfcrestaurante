package fragments;

import usuario.InicializarRestaurante;

import com.example.nfcook.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.widget.TabHost;

public class MiTabsSuperioresListener  implements TabListener{
	private Fragment fragment;
	private String nombreTab;
	private Activity activity;
	private int posTab;
 
	public MiTabsSuperioresListener(Fragment fragment, String nombreTab, Activity activity, int posTab) {
		this.fragment = fragment;
		this.nombreTab = nombreTab;
		this.activity = activity;
		this.posTab = posTab;
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.FrameLayoutPestanas, fragment, nombreTab);
	}
 
	public void onTabSelected(Tab tab, FragmentTransaction ft) {       
		ft.replace(R.id.FrameLayoutPestanas, fragment, nombreTab);
		
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = activity.getActionBar();
    	actionbar.setTitle(" CONFIGURE SU MENÚ...");
    	
    	// Marcamos a true seleccionado tabSuperior
    	//InicializarRestaurante.setSeleccionadoTabSuperior(true);
        // Guardamos la pos del tab por si seleccionamos la calculadora
    	InicializarRestaurante.setPosTabSuperior(posTab);
    	
    	/*
		 *  Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
		 *  ya no lo esté.
		 */
    	TabHost tabs = ((TabHost) activity.findViewById(android.R.id.tabhost));
        tabs.getTabWidget().getChildAt(InicializarRestaurante.getTabInferiorSeleccionado()).setBackgroundColor(Color.parseColor("#c38838"));
   	}
 
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}
}