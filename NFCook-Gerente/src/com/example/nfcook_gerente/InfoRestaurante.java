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
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import fragments.InformacionRestauranteFragment;


/**
 * @author: Alejandro Moran
 * 
 * Esta clase contendr� toda la informaci�n de un restaurante.
 * 
 * Se accede a ella al seleccionar un restaurante, 
 * en la lista inicial.
 * 
**/
public class InfoRestaurante extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

	// Tabs: Informacion, Empleados, Estadisticas y Clasificacion de platos
	private TabHost tabs;
	private ViewPager miViewPager;
	private MiViewPagerAdapter miPagerAdapter;
	private long anteriorPulsacion;
	private ArrayList<Fragment> listFragments;
	private Bundle bundleInfoRestaurante;
	
	
	class TabFactory implements TabContentFactory {
		 
        private final Context mContext;
 
        public TabFactory(Context context) {
            mContext = context;
        }
 
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
    	actionbar.setTitle(" MEN� PRINCIPAL");
        
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);*/
        
        // Recogemos la informaci�n para pasarla como argumento m�s adelante
        bundleInfoRestaurante = getIntent().getExtras(); // Nombre, tel�fono, calle, logo, idRestaurante

        inicializarTabs();
        cargarTabs();
		
		inicializarViewPager();
		
		if (savedInstanceState != null)
            tabs.setCurrentTabByTag(savedInstanceState.getString("tab"));
        else 
        	tabs.setCurrentTab(0); 
        
	}
	
	// onBackPressed() al pulsar atras que haga algo, quitado
	
	// Metodo encargado de inicializar los tabs
    private void inicializarTabs(){
    	// Creamos los tabs y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
    }
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al gerente
    private void cargarTabs(){
    	// Creamos el tab1 --> Informaci�n
        TabSpec spec = tabs.newTabSpec("tabInfo");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("INFORMACI�N", null);
        tabs.addTab(spec);

        // Creamos el tab2 --> Empleados
        spec = tabs.newTabSpec("tabEmpleados");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("EMPLEADOS", null);
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Estadisticas
        spec = tabs.newTabSpec("tabEstadisticas");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("ESTAD�STICAS", null);
        tabs.addTab(spec);
        
        // Creamos el tab4 --> Clasificaci�n de platos
        spec = tabs.newTabSpec("tabClasificacionPlatos");
        spec.setContent(new TabFactory(this));
        spec.setIndicator("CLASIFICACI�N DE PLATOS", null);
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
        // Guardar en "tab" la pesta�a seleccionada.
        instanceState.putString("tab", tabs.getCurrentTabTag());
        super.onSaveInstanceState(instanceState);
	}
    
    public void inicializarViewPager() {
    	 
    	/* listFragments es una lista donde est�n todos los Fragments que 
         * se van a usar en el ViewPager. En este caso va a tener 2 elementos.
         */
    	 
        listFragments = new ArrayList<Fragment>();
        // A�adir todos los fragmentos implementados en otras clases x.class

        // pasarle los argumentos necesarios a cada fragment
        listFragments.add(Fragment.instantiate(InfoRestaurante.this, InformacionRestauranteFragment.class.getName(), bundleInfoRestaurante));  
        //listFragments.add(Fragment.instantiate(this, PantallaEstadisticasFragment.class.getName()));
        miPagerAdapter  = new MiViewPagerAdapter(super.getSupportFragmentManager(), listFragments);
      
        miViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        miViewPager.setAdapter(miPagerAdapter);
        miViewPager.setOnPageChangeListener(this);
        
    }
    
    // Metodo encargado de definir la acci�n de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		// Cuando se pulsa en la pesta�a
        
		int pos = tabs.getCurrentTab(); // Obtener que pesta�a ha sido pulsada
        miViewPager.setCurrentItem(pos); // Seleccionar la p�gina en el ViewPager.
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