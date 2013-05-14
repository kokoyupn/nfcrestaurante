package usuario;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.example.nfcook.R;
import fragments.ListaMapasFragment;

@SuppressWarnings("deprecation")
public class InicializarMapas extends TabActivity{
	// Tabs con las distintas categorías de mapas
	private static TabHost tabs;
	private Fragment fragmentListaMapas;
	private String restaurante;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_tabs_mapas);
        
        Bundle bundle = getIntent().getExtras();
		restaurante = bundle.getString("nombreRestaurante");
        
     	// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" MAPAS");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Inicializamos y cargamos Tabs
		inicializarTabs();
		cargarTabs();
    }
	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }
	
	
	
	// Metodo encargado de inicializar los tabs
    private void inicializarTabs(){
    	// Creamos los tabs inferiores y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
		// Les hacemos oyentes
        tabs.setOnTabChangedListener(new OnTabChangeListener() {
			
        	// Metodo encargado de definir la acción de cada tab cuando sea seleccionado
           	public void onTabChanged(String tabId) {
           		if(tabs.getCurrentTabTag().equals("tabMapa")){
           			/*
           			 * FIXME Hace una pequeña transicion por debajo que no queda bien, pero
           			 * ya se arreglará más adelante.
           			 */
           			FragmentTransaction m = getFragmentManager().beginTransaction();
        	        if(fragmentListaMapas != null){
        	        	m.remove(fragmentListaMapas);
        	        	m.commit();
        	        }
           		}else if(tabs.getCurrentTabTag().equals("tabListaMapa")){
           			fragmentListaMapas = new ListaMapasFragment();
           			((ListaMapasFragment)fragmentListaMapas).setRestaurante(restaurante);
        	        FragmentTransaction m = getFragmentManager().beginTransaction();
        	        m.replace(R.id.FrameLayoutMapas, fragmentListaMapas);
        	        m.commit();
           		}
           	}
           	
		});

        
       
        tabs.setup();
    }
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al usuario
    private void cargarTabs(){
    	// Creamos el tab1 --> Inicio
        TabHost.TabSpec spec = tabs.newTabSpec("tabMapa");
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator("Mapas",null);
        // Creamos su funcionalidad
        Intent intent = new Intent().setClass(this, Mapas.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtra("nombreRestaurante", bundle.getString("nombreRestaurante"));
        spec.setContent(intent);
        // Lo añadimos
        tabs.addTab(spec);
       
        
        // Creamos el tab2 --> Promociones
        spec = tabs.newTabSpec("tabListaMapa");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Lista",null);
        tabs.addTab(spec);
        
        // Seleccionamos el tab Mapa de inicio
        tabs.setCurrentTabByTag("tabMapa");
        
        // Cambiamos el color de la letra de los tabs de mapas
        for(int i=0; i<=tabs.getChildCount(); i++){
        	TextView textViewTituloTab = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        	textViewTituloTab.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
    
    public static void marcarTab(int pos){
        tabs.setCurrentTab(pos);
    }
}
