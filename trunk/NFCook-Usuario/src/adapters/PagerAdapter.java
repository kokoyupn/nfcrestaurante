package adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * 
 * @author Alejandro Villapalos
 * 
 * 
 * PagerAdapter es una clase que gestiona los Fragments para
 * que se mantengan en memoria como cuando se usa en pestañas como en
 * nuestro caso.
 * 
 * Tiene una lista de los Fragments que estamos usando.
 * 
 * Cuando el TabHost o el ViewPager necesite acceder a un Fragment
 * invocará a getItem.
 *
 */
public class PagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        
        public PagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments) {
                super(fragmentManager);
                this.fragments = fragments;
        }

		@Override
        public Fragment getItem(int position) {
                return this.fragments.get(position);
        }
        
        @Override
        public int getCount() {
                return this.fragments.size();
        }
}