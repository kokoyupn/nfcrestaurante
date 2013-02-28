package fragments;

import com.example.nfcook.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class MyTabsListener  implements TabListener{
	private Fragment fragment;
	private String nombreTab;
 
	public MyTabsListener(Fragment fragment, String nombreTab) {
		this.fragment = fragment;
		this.nombreTab = nombreTab;
	}
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.RelativeLayout1, fragment, nombreTab);
	}
 
	public void onTabSelected(Tab tab, FragmentTransaction ft) {       
		ft.replace(R.id.RelativeLayout1, fragment, nombreTab);
	}
 
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}
}