package adapters;

import java.util.ArrayList;

import fragments.ContenidoTabSuperiorCategoriaBebidas;
import fragments.ContenidoTabsSuperioresFragment;
import usuario.InicializarRestaurante;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
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

        public ArrayList<Fragment> fragments;
        
        public PagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments) {
                super(fragmentManager);
                this.fragments = fragments;
        }

		@SuppressWarnings("unchecked")
		@Override
        public Fragment getItem(int position) {
			
			if(position<5){
				Fragment f = new ContenidoTabsSuperioresFragment();
				ArrayList<Fragment> fl = new ArrayList<Fragment>();
				fl = (ArrayList<Fragment>) fragments.clone();
				f = fl.get(position);
				
				return f;
			}else{
				return new ContenidoTabSuperiorCategoriaBebidas();
			}
        }
        
        @Override
        public int getCount() {
                return this.fragments.size();
        }
       
        @Override
        public int getItemPosition(Object object)
        {
        	 return POSITION_NONE;
        }

}