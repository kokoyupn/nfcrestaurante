package com.example.nfcook_gerente;

import java.util.ArrayList;

import adapters.MiViewPagerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import fragments.InformacionRestauranteFragment;


/**
 * @author: Alejandro Moran
 * 
 * Esta clase contendrá toda la información de un restaurante.
 * 
 * Se accede a ella al seleccionar un restaurante, 
 * en la lista inicial.
 * 
**/
public class InfoRestaurante extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

	// Tabs: Informacion, Empleados, Estadisticas y Clasificacion de platos
	private TabHost tabs;
	private View tabContentView;
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
		//Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_tabs);

        // Recogemos la información para pasarla como argumento más adelante
        bundleInfoRestaurante = getIntent().getExtras(); // Nombre, teléfono, calle, logo, idRestaurante

		inicializarViewPager();

        inicializarTabs();
        cargarTabs();
		
		
		if (savedInstanceState != null)
            tabs.setCurrentTabByTag("tabInfo");
        else 
        	tabs.setCurrentTab(0); 
	
        tabs.setCurrentTabByTag("tabInfo");

	}
	
	
	// Metodo encargado de inicializar los tabs
    private void inicializarTabs(){
    	// Creamos los tabs y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
		// Los hacemos oyentes
        tabs.setup();
       
    }
    
    // Metodo encargado crear los tabs superiores con las funcionalidades que ofrecemos al gerente
    private void cargarTabs(){
    	
    	// Creamos el tab1 --> Información
        TabSpec spec = tabs.newTabSpec("tabInfo");
        spec.setContent(R.id.tab1);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabInfo"));
        tabs.addTab(spec);

        // Creamos el tab2 --> Empleados
        spec = tabs.newTabSpec("tabEmpleados");
        spec.setContent(R.id.tab2);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabEmpleados"));
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Estadisticas
        spec = tabs.newTabSpec("tabEstadisticas");
        spec.setContent(R.id.tab3);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabEstadisticas"));
        tabs.addTab(spec);
        
        // Creamos el tab4 --> Clasificación de platos
        spec = tabs.newTabSpec("tabClasificacionPlatos");
        spec.setContent(R.id.tab4);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabClasificacionPlatos"));
        tabs.addTab(spec);
        
        /*
         * Creamos un tab falso para que cada vez que pulsemos en cualquiera de los tabs
         * inferiores, redirigimos como tab pulsado al falso, de esta forma conseguimos
         * que cada vez que pulsemos en cada tab inferior entre en el método onchanged.
         */
        // Creamos el tab5 --> tabFalso
//        
//        spec = tabs.newTabSpec("tabFalso");
//        spec.setContent(R.id.tab5);
//        spec.setIndicator(prepararTabView(getApplicationContext(),"tabFalso"));
//        tabs.addTab(spec);
//        
//        // Seleccionamos momentaneamente el tab falso para ocultarlo
//        tabs.setCurrentTabByTag("tabFalso");
//        // Lo ocultamos, consiguiendo que esté pero no lo veamos, justo lo que queremos
//        tabs.getCurrentTabView().setVisibility(View.GONE);
        
        // para ayuda
        // anteriorTabPulsado = "tabInicio";
        
        miViewPager.setVisibility(View.GONE);

    	// Este "for" hace que los tabs de arriba tengan el foco activado para que los tabs
		// aparezcan en la pantalla siempre en vez de salir escondidos

		for(int i=0;i<listFragments.size();i++){
            tabs.getTabWidget().getChildAt(i).setFocusableInTouchMode(true);

	        tabs.getTabWidget().getChildAt(i).setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					miViewPager.setVisibility(View.VISIBLE);
//					usandoTabsInferiores = false;
//					cambiarBackgroundTabsSuperiores();
//					
//					seleccionadoTabSuperior = true;
					// Obtener la pestaña actual
					int postabSuperiorPulsado = tabs.getCurrentTab(); 
					// Seleccionar la página en el ViewPager.
			        miViewPager.setCurrentItem(postabSuperiorPulsado);
					
			        // Obtener la pestaña actual
//					int pos = tabs.getCurrentTab();
//					
//					//miPagerAdapter.notifyDataSetChanged();
//			        if (pos == listFragments.size()-1 && listFragments.get(listFragments.size()-1).getView() != null)
//			        	((ContenidoTabSuperiorCategoriaBebidas) listFragments.get(listFragments.size()-1)).actualizar();
//			        					
					Fragment f = new Fragment();
			        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			        ft.replace(R.id.FrameLayoutPestanas, f);
			        ft.commit();
			        						       
			        
//			        tabs.getTabWidget().getChildAt(InicializarRestaurante.getTabInferiorSeleccionado()).setBackgroundColor(Color.parseColor("#c38838"));
				}
			});
		}
    	
			// Hacemos oyente al tabhost
	        tabs.setOnTabChangedListener(new OnTabChangeListener() {
				
				public void onTabChanged(String tabId) {
					
					miViewPager.setVisibility(View.VISIBLE);
//					usandoTabsInferiores = false;
//					cambiarBackgroundTabsSuperiores();
//
//					seleccionadoTabSuperior = true;
					// Obtener la pestaña actual
					int postabSuperiorPulsado = tabs.getCurrentTab(); 
					// Seleccionar la página en el ViewPager.
			        miViewPager.setCurrentItem(postabSuperiorPulsado);
			        
			        // Obtener la pestaña actual
//					int pos = tabsSuperiores.getCurrentTab();
//			        
//			        if (pos == 0 && listFragments.get(0).getView() != null)
//			        	((ContenidoTabsSuperioresFragment) listFragments.get(0)).actualizar();
//			        else if (pos == listFragments.size()-1 && listFragments.get(listFragments.size()-1).getView() != null)
//			        	((ContenidoTabSuperiorCategoriaBebidas) listFragments.get(listFragments.size()-1)).actualizar();
		        
			      	tabs.setCurrentTabByTag(tabId);

			        Fragment f = new Fragment();
			        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			        ft.replace(R.id.FrameLayoutPestanas, f);
			        ft.commit();
			        
					// Ponemos el título a la actividad
			        // Recogemos ActionBar
//			        ActionBar actionbar = getActionBar();
//			    	actionbar.setTitle(" CONFIGURE SU MENÚ...");
//			  
//			        tabs.getTabWidget().getChildAt(InicializarRestaurante.getTabInferiorSeleccionado()).setBackgroundColor(Color.parseColor("#c38838"));
				}
			});
    	
 
    }
    
    // Metodo encargado de preparar las vistas de cada tab 
    private View prepararTabView(Context context, String nombreTab){
    	// Cargamos el layout
    	tabContentView = LayoutInflater.from(context).inflate(R.layout.tabs, null);
    	// Cargamos el icono del tab
    	ImageView imagenTab = (ImageView)tabContentView.findViewById(R.id.imageViewTabInferior);
    	// Cargamos el titulo del tab
    	TextView textoTab = (TextView)tabContentView.findViewById(R.id.textViewTabInferior);
    	textoTab.setTextColor(Color.BLACK);
    	// Asignamos el título e icono para cada tab
    	if(nombreTab.equals("tabInfo")){
     		textoTab.setText("INFORMACION");
     		imagenTab.setImageResource(getResources().getIdentifier("inicio","drawable",this.getPackageName()));
     	}else if(nombreTab.equals("tabEmpleados")){
    		textoTab.setText("EMPLEADOS");
    		imagenTab.setImageResource(getResources().getIdentifier("pedido","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabEstadisticas")){
    		textoTab.setText("ESTADISTICAS");
    		imagenTab.setImageResource(getResources().getIdentifier("pagar","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabClasificacionPlatos")){
    		textoTab.setText("CLASIFICACION DE PLATOS");
    		imagenTab.setImageResource(getResources().getIdentifier("calculadora","drawable",this.getPackageName()));
    	}
    	return tabContentView;
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

        // pasarle los argumentos necesarios a cada fragment
        listFragments.add(Fragment.instantiate(InfoRestaurante.this, InformacionRestauranteFragment.class.getName(), bundleInfoRestaurante));  
        //listFragments.add(Fragment.instantiate(this, PantallaEstadisticasFragment.class.getName()));
        miPagerAdapter  = new MiViewPagerAdapter(super.getSupportFragmentManager(), listFragments);
      
        miViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        miViewPager.setAdapter(miPagerAdapter);
        miViewPager.setOnPageChangeListener(this);
       
        
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
//	public void onTabChanged(String tabId) {
//		// Cuando se pulsa en la pestaña
//	    
//		int pos = tabs.getCurrentTab(); // Obtener que pestaña ha sido pulsada
//        miViewPager.setCurrentItem(pos); // Seleccionar la página en el ViewPager.
//
//        if(tabs.getCurrentTabTag().equals("tabInfo")){
//
//			if (miViewPager != null)
//				miViewPager.setVisibility(View.GONE);
//			/*
//			 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
//			 * ya no lo esté.
//			 */
//		    tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#c38838"));
//		    /*
//		     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
//		     * usuario vea cual está seleccionado.
//		     */
//		    tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#906d35"));
//		    //tabInferiorSeleccionado = 0;
//		    
//			// Marcamos el tab falso
//            tabs.setCurrentTabByTag("tabFalso");
//            // Marcamos a falso selccionado tabSuperior
//            //seleccionadoTabSuperior = false;  
//            //anterior tab pulsado
//            //anteriorTabPulsado = "tabInicio";
//
//			// Cargamos en el fragment la pantalla de bienvenida del restaurante
//            InformacionRestauranteFragment fInfo = new InformacionRestauranteFragment();
//
//	        FragmentTransaction m = getSupportFragmentManager().beginTransaction();
//	        m.replace(R.id.FrameLayoutPestanas, fInfo);
//	        m.commit();
//	        
////	        Fragment f = new Fragment();
////	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
////	        ft.replace(R.id.FrameLayoutPestanas, f);
////	        ft.commit();
////	        
//		}
//	}

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


	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}
}
