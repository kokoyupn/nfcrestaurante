package fragments;

import com.example.nfcook.R;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class MyTabsListener implements TabListener {
	public Fragment fragment;
 
	public MyTabsListener(Fragment fragment) {
		this.fragment = fragment;
	}
 
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.RelativeLayout1, fragment);
	}
 
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.RelativeLayout1, fragment);
	}
 
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
		
	}
}