package com.example.nfcook_gerente;

import java.util.ArrayList;

import adapters.MiViewPagerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import fragments.PantallaInformacionFragment;

public class InicializarGerente extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {
	
	// Tabs con las mesas y el histórico
	private TabHost tabs;
	private ViewPager miViewPager;
	private MiViewPagerAdapter miPagerAdapter;
	private long anteriorPulsacion;
	private ArrayList<Fragment> listFragments;
	
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
        /* ActionBar explota
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" MENÚ PRINCIPAL");
        
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);*/
        
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
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al gerente
    private void cargarTabs(){
    	// Creamos el tab1 --> Información
        TabSpec spec = tabs.newTabSpec("tabInfo");
        // Hacemos referencia a su layout correspondiente
        spec.setContent(new TabFactory(this));
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator("INFORMACIÓN", null);
        // Lo añadimos
        tabs.addTab(spec);

        // Creamos el tab2 --> Estadísticas
        spec = tabs.newTabSpec("tabEstadisticas");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("ESTADÍSTICAS", null);
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Clasificación Platos
        spec = tabs.newTabSpec("tabClasificacionPlatos");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("CLASIFICACIÓN DE PLATOS", null);
        tabs.addTab(spec);
        
        for(int i=0;i<=tabs.getChildCount();i++) 
		{ 
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
        // Los hacemos oyentes
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
    	 
        listFragments = new ArrayList<Fragment>();
        // Añadir todos los fragmentos implementados en otras clases x.class

        listFragments.add(Fragment.instantiate(this, PantallaInformacionFragment.class.getName()));  
        //listFragments.add(Fragment.instantiate(this, PantallaEstadisticasFragment.class.getName()));
        miPagerAdapter  = new MiViewPagerAdapter(super.getSupportFragmentManager(), listFragments);
      
        miViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        miViewPager.setAdapter(miPagerAdapter);
        miViewPager.setOnPageChangeListener(this);
        
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		// Cuando se pulsa en la pestaña
        
		int pos = tabs.getCurrentTab(); // Obtener que pestaña ha sido pulsada
        miViewPager.setCurrentItem(pos); // Seleccionar la página en el ViewPager.
        /*
        if (pos == 1 && listFragments.get(1).getView() != null)
        	((PantallaHistoricoFragment) listFragments.get(1)).actualizar();
        */
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
