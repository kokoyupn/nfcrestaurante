package com.example.nfcook_camarero;

import fragments.PantallaHistoricoFragment;
import fragments.PantallaMesasFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class InicializarPantallasCamarero extends Activity implements TabContentFactory, OnTabChangeListener {
	
	// Tabs con las mesas y el histórico
	private TabHost tabs;
	private View tabContentView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_tabs);
        
        inicializarTabs();
		cargarTabs();
	}
	
	// Metodo encargado de inicializar los tabs inferiores
    private void inicializarTabs(){
    	// Creamos los tabs inferiores y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
		// Les hacemos oyentes
		tabs.setOnTabChangedListener(this);
        tabs.setup();
    }
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al usuario
    private void cargarTabs(){
    	// Creamos el tab1 --> Mesas
        TabHost.TabSpec spec = tabs.newTabSpec("tabMesas");
        // Hacemos referencia a su layout correspondiente
        spec.setContent(R.id.tab1);
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator("MESAS", null);
        // Lo añadimos
        tabs.addTab(spec);
        
        // Creamos el tab2 --> Histórico
        spec = tabs.newTabSpec("tabHistorico");
        spec.setContent(R.id.tab2);
        spec.setIndicator("HISTÓRICO", null);
        tabs.addTab(spec);
        
        for(int i=0;i<=tabs.getChildCount();i++) 
		{ 
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }
        
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		if(tabs.getCurrentTabTag().equals("tabMesas")){
			// Cargamos en el fragment la pantalla de bienvenida del restaurante
			Fragment fragmentPantallaMesas = new PantallaMesasFragment();
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPantallaMesas);
	        m.commit();
		}else if(tabId.equals("tabHistorico")){
			// Cargamos en el fragment la pantalla de bienvenida del restaurante
			Fragment fragmentPantallaHistorico = new PantallaHistoricoFragment();
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPantallaHistorico);
	        m.commit();
	       //OTROS TIPOS POR SI LOS NECESITO @ WITE
		}/*else if(tabId.equals("tabCuenta")){
			// Descamarcamos el tab superior activado para evitar confusiones
			desmarcarTabSuperiorActivo();
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            
			Fragment fragmentCuenta = new CuentaFragment();
			((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	        m.addToBackStack("Cuenta");
	        m.commit();
		}else if(tabId.equals("tabCalculadora")){
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
			// Vemos si se ha sincronizado algún pedido para poder utilizar la calculadora
			if(hayAlgunPedidoSincronizado()){				
				lanzarVentanaEmergenteParaIndicarNumeroComensales();
			}else{
				lanzarVentanaEmergenteAvisoSeNecesitaMinimoUnPedido();
			}
		}*/
	}

	public View createTabContent(String tag) {
		return null;
	}
}
