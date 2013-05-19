package com.example.nfcook_camarero;

import java.util.ArrayList;

import fragments.PantallaHistoricoFragment;
import fragments.PantallaMesasFragment;
import adapters.MiViewPagerAdapter;
import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class InicializarCamarero extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {
	
	// Tabs con las mesas y el histórico
	private TabHost tabs;
	private ViewPager miViewPager;
	private MiViewPagerAdapter miPagerAdapter;
	private long anteriorPulsacion;
	
	 /**
     * A simple factory that returns dummy views to the Tabhost
     * @author mwho
     */
    class TabFactory implements TabContentFactory {
 
        private final Context mContext;
 
        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }
 
        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
 
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_tabs);
        
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" MENÚ PRINCIPAL");
        
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
        inicializarTabs();
		cargarTabs();
		
		inicializarViewPager();
		
		if (savedInstanceState != null)
            tabs.setCurrentTabByTag(savedInstanceState.getString("tab"));
        else 
        	tabs.setCurrentTab(0); 
	}
		
	public void onBackPressed() {
	    long tiempoActual = System.currentTimeMillis();
	    if(tiempoActual - anteriorPulsacion > 2000){
	    	Toast.makeText(getApplicationContext(),"Pulse de nuevo para salir",Toast.LENGTH_SHORT).show();	
	    	anteriorPulsacion = tiempoActual;
	    } else{
	        super.onBackPressed();
	    }
	}
	
	/*Metodo que realiza la accion del boton introducido en el ActionBar (Sincronizar)*/
    public boolean onOptionsItemSelected(MenuItem item) {
    	finish();
    	return false;	
    }
	
	// Metodo encargado de inicializar los tabs inferiores
    private void inicializarTabs(){
    	// Creamos los tabs inferiores y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
    }
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al usuario
    private void cargarTabs(){
    	// Creamos el tab1 --> Mesas
        TabHost.TabSpec spec = tabs.newTabSpec("tabMesas");
        // Hacemos referencia a su layout correspondiente
        spec.setContent(new TabFactory(this));
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator("MESAS", null);
        // Lo añadimos
        tabs.addTab(spec);

        // Creamos el tab2 --> Histórico
        spec = tabs.newTabSpec("tabHistorico");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("HISTÓRICO", null);
        tabs.addTab(spec);
        
        for(int i=0;i<=tabs.getChildCount();i++) 
		{ 
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
        // Les hacemos oyentes
     	tabs.setOnTabChangedListener(this);
    }
    
    protected void onSaveInstanceState(Bundle instanceState) {
        // Guardar en "tab" la pestaña seleccionada.
        instanceState.putString("tab", tabs.getCurrentTabTag());
        super.onSaveInstanceState(instanceState);
	}
    
    public void inicializarViewPager() {
    	 
    	/* listFragments es una lista donde están todos los Fragments que 
         * se van a usar en el ViewPager. En este caso va a tener 2 elementos.
         */
        ArrayList<Fragment> listFragments = new ArrayList<Fragment>();
        // Añadir todos los fragmentos implementados en otras clases x.class
        Bundle arguments = new Bundle();
        arguments.putString("Restaurante", "Foster");
        listFragments.add(Fragment.instantiate(this, PantallaMesasFragment.class.getName(),arguments));
        listFragments.add(Fragment.instantiate(this, PantallaHistoricoFragment.class.getName()));
        miPagerAdapter  = new MiViewPagerAdapter(super.getSupportFragmentManager(), listFragments);
      
        miViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        miViewPager.setAdapter(miPagerAdapter);
        miViewPager.setOnPageChangeListener(this);
        //miViewPager.setOffscreenPageLimit(2);
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		// Cuando se pulsa en la pestaña
        int pos = tabs.getCurrentTab(); // Obtener que pestaña ha sido pu
        miViewPager.setCurrentItem(pos); // Seleccionar la página en el ViewPager.
	}

	public View createTabContent(String tag) {
		return null;
	}

	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public void onPageSelected(int pos) {
		// TODO Auto-generated method stub
		tabs.setCurrentTab(pos);
	}
}
