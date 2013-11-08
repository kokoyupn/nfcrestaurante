package com.example.nfcook_gerente;

import java.util.ArrayList;

import adapters.PagerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import fragments.ClasificacionPlatosFragment;
import fragments.EmpleadosFragment;
import fragments.InformacionRestauranteFragment;
import fragments.IngresosFragment;


/**
 * @author: Alejandro Moran
 * 
 * Esta clase contendrá toda la información de un restaurante.
 * 
 * Se accede a ella al seleccionar un restaurante, 
 * en la lista inicial.
 * 
**/
public class InicializarInformacionRestaurante extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

	// Tabs: Informacion, Empleados, Ingresos y Clasificacion de platos
	private TabHost tabs;
	private View tabContentView;
	private ViewPager miViewPager;
	private PagerAdapter miPagerAdapter;
	private ArrayList<Fragment> listFragments;
	private Bundle bundleInfoRestaurante;
	private boolean sinInfo;
	
	private int ultimoTabSeleccionado;
	
	
    class TabFactory implements TabContentFactory {
   	 
        private final Context mContext;
 
        public TabFactory(Context context) {
            mContext = context;
        }
 
        @Override
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

        sinInfo = bundleInfoRestaurante.getBoolean("sinInfo");
        
        inicializarTabs();
        cargarTabsEInicializarViewPages();
        
		// Marcamos el tab de informacion como inicialo el de empleados si se han seleccionado varios restaurantes
        tabs.setCurrentTab(0);
        ultimoTabSeleccionado = 0;
	    tabs.getTabWidget().getChildAt(ultimoTabSeleccionado).setBackgroundColor(Color.parseColor("#CAE0CD"));
	}
	
	
	// Metodo encargado de inicializar los tabs
    private void inicializarTabs(){
    	// Creamos los tabs y los inicializamos
		tabs = (TabHost)findViewById(R.id.tabhost);

		// Los hacemos oyentes
        tabs.setup();  
    }
    
    // Metodo encargado crear los tabs superiores con las funcionalidades que ofrecemos al gerente
    private void cargarTabsEInicializarViewPages(){
    	// Creamos la lista que contendra todos los fragments a cargar en funcion del tab
    	listFragments = new ArrayList<Fragment>();
    	TabHost.TabSpec spec = null;
    	
    	if(!sinInfo){
	    	// Creamos el tab1 --> Información
    		spec = tabs.newTabSpec("tabInformacion");     
	        // Hacemos referencia a su layout correspondiente
	        spec.setContent(R.id.tab1);
	        // Preparamos la vista del tab con el layout que hemos preparado
	        spec.setIndicator(prepararTabView(getApplicationContext(),"tabInformacion"));
	        // Lo añadimos
	        tabs.addTab(spec);
			// Creamos el fragment de cada tab
	        listFragments.add(Fragment.instantiate(this, InformacionRestauranteFragment.class.getName(), bundleInfoRestaurante));  
    	}
    	
        // Creamos el tab2 --> Empleados
        spec = tabs.newTabSpec("tabEmpleados");
        if(sinInfo)
        	spec.setContent(R.id.tab1);
        else 
	        spec.setContent(R.id.tab2);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabEmpleados"));
        tabs.addTab(spec);
        //TODO Cambiar la instacia en funcion de lo que tenga que cargar el fragment
        listFragments.add(Fragment.instantiate(this, EmpleadosFragment.class.getName(), bundleInfoRestaurante));
   
        // Creamos el tab3 --> Ingresos
        spec = tabs.newTabSpec("tabIngresos");
        if(sinInfo)
        	spec.setContent(R.id.tab2);
        else 
	        spec.setContent(R.id.tab3);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabIngresos"));
        tabs.addTab(spec);
        //TODO Cambiar la instacia en funcion de lo que tenga que cargar el fragment
        listFragments.add(Fragment.instantiate(this, IngresosFragment.class.getName(), bundleInfoRestaurante));
     
        // Creamos el tab4 --> Clasificación de platos
        spec = tabs.newTabSpec("tabClasificacionPlatos");
        if(sinInfo)
        	spec.setContent(R.id.tab3);
        else 
	        spec.setContent(R.id.tab4);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabClasificacionPlatos"));
        tabs.addTab(spec);
        //TODO Cambiar la instacia en funcion de lo que tenga que cargar el fragment
        listFragments.add(Fragment.instantiate(this, ClasificacionPlatosFragment.class.getName()));

     	// Ponemos el fondo a cada uno de los tabs
     	for(int i=0;i<listFragments.size();i++){
	        tabs.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#A9CBAD"));
     	}
     	
     	// Aqui creamos el viewPager y el pagerAdapter para el correcto slide entre pestañas
     	inicializarViewPager();

		// Hacemos oyente al tabhost
        tabs.setOnTabChangedListener(this);
    }
    
    // Metodo encargado de preparar las vistas de cada tab 
    private View prepararTabView(Context context, String nombreTab){
    	// Cargamos el layout
    	tabContentView = LayoutInflater.from(context).inflate(R.layout.tabs, null);
    	// Cargamos el icono del tab
    	//ImageView imagenTab = (ImageView)tabContentView.findViewById(R.id.imageViewTabInferior);
    	// Cargamos el titulo del tab
    	TextView textoTab = (TextView)tabContentView.findViewById(R.id.textViewTabInferior);
    	textoTab.setTextColor(Color.BLACK);
    	// Asignamos el título e icono para cada tab
    	if(nombreTab.equals("tabInformacion")){
     		textoTab.setText("  INFORMACIÓN  ");
     		//TODO Si queremos poner una imagen en el tab. OJO porque tiene tam 0dp x 0dp en el layout
     		//imagenTab.setImageResource(getResources().getIdentifier("inicio","drawable",this.getPackageName()));
     	}else if(nombreTab.equals("tabEmpleados")){
    		textoTab.setText("  EMPLEADOS  ");
     		//TODO Si queremos poner una imagen en el tab. OJO porque tiene tam 0dp x 0dp en el layout
    		//imagenTab.setImageResource(getResources().getIdentifier("pedido","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabIngresos")){
    		textoTab.setText("  INGRESOS  ");
     		//TODO Si queremos poner una imagen en el tab. OJO porque tiene tam 0dp x 0dp en el layout
    		//imagenTab.setImageResource(getResources().getIdentifier("pagar","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabClasificacionPlatos")){
    		textoTab.setText("  CLASIFICACIÓN  \nDE PLATOS");
     		//TODO Si queremos poner una imagen en el tab. OJO porque tiene tam 0dp x 0dp en el layout
    		//imagenTab.setImageResource(getResources().getIdentifier("calculadora","drawable",this.getPackageName()));
    	}
    	return tabContentView;
    }
    
    @Override
	protected void onSaveInstanceState(Bundle instanceState) {
        // Guardar en "tab" la pestaña seleccionada.
        instanceState.putString("tab", tabs.getCurrentTabTag());
        super.onSaveInstanceState(instanceState);
	}
    
    public void inicializarViewPager() {
    	 
    	/* listFragments es una lista donde están todos los Fragments que 
         * se van a usar en el ViewPager.
         */
    	miPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), listFragments);
        miViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        miViewPager.setAdapter(miPagerAdapter);
        
        // Indicamos cuál es el número máximo de páginas que puede haber
        miViewPager.setOffscreenPageLimit(listFragments.size());
        miViewPager.setOnPageChangeListener(this);
        
        // Lo hacemos visible
        miViewPager.setVisibility(View.VISIBLE);
    }
    
    @Override
	public void onTabChanged(String tabId) {
		int tabSeleccionado = tabs.getCurrentTab();
		
		// Cambiamos el fondo del tab que hubiese marcado ya que se ha desmarcado
	    tabs.getTabWidget().getChildAt(ultimoTabSeleccionado).setBackgroundColor(Color.parseColor("#A9CBAD"));
		
		// Actualizamos el ultimo tab seleccionado y cambiamos al tab y fondo seleccionado
		ultimoTabSeleccionado = tabSeleccionado;
      	tabs.setCurrentTabByTag(tabId);
        tabs.getTabWidget().getChildAt(ultimoTabSeleccionado).setBackgroundColor(Color.parseColor("#CAE0CD"));

		// Seleccionar la página en el ViewPager.
        miViewPager.setCurrentItem(tabSeleccionado);
    
        // Realizamos la transicion
        Fragment f = new Fragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameLayoutContenedorTabs, f);
        ft.commit();
        
        //Ponemos el título a la actividad
		//Recogemos ActionBar
//		ActionBar actionbar = getActionBar();
//		actionbar.setTitle(" INFORMACIÓN DEL RESTAURANTE...");		
	}
    
	public View createTabContent(String tag) {
		return null;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int pos) {
		// TODO Auto-generated method stub
		tabs.setCurrentTab(pos);
	}
	
	@Override
	public void onPause () {
		super.onPause();
		Log.v("pause", "onPause"); 
	}
}
