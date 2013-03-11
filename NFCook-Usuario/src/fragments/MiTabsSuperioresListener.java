package fragments;

import usuario.InicializarRestaurante;

import com.example.nfcook.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class MiTabsSuperioresListener  implements TabListener{
	private Fragment fragment;
	private String nombreTab;
 
	public MiTabsSuperioresListener(Fragment fragment, String nombreTab) {
		this.fragment = fragment;
		this.nombreTab = nombreTab;
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.FrameLayoutPestanas, fragment, nombreTab);
		InicializarRestaurante.setPulsadoTabSuperior(true);
	}
 
	public void onTabSelected(Tab tab, FragmentTransaction ft) {       
		ft.replace(R.id.FrameLayoutPestanas, fragment, nombreTab);
		InicializarRestaurante.setPulsadoTabSuperior(true);
	}
 
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}
}