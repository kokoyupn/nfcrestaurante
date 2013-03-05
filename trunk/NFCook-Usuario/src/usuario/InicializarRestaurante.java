package usuario;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.example.nfcook.R;

import fragments.CuentaFragment;
import fragments.MiTabsSuperioresListener;
import fragments.PantallaInicialRestaurante;
import fragments.PedidoFragment;
import fragments.ContenidoTabsSuperioresFragment;

import baseDatos.Handler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

public class InicializarRestaurante extends Activity implements TabContentFactory, OnTabChangeListener{
	
	private ImageView imagenRestaurante;
	private String restaurante;
	
	// Base de datos
	private Handler sql;
	private SQLiteDatabase db;
	
	// Actionbar para los tabs superiores con las categrías de los platos
	private ActionBar actionbar;
	
	// Tabs inferiores con las funcionalidades de la aplicacion
	private TabHost tabs;
	// Vista de los tabs inferiores
	private View tabInferiorContentView;
	// Array de booleanos para indicar cuando si se ha pulsado ya ese tab
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedor_tabs);

        // Cargamos el logo del restaurante en el layout y asignamos el nombre del restaurante
        imagenRestaurante = (ImageView)findViewById(R.id.imageViewLogoRestaurante);
        Bundle bundle = getIntent().getExtras();
	    imagenRestaurante.setImageResource(bundle.getInt("logoRestaurante"));
		restaurante = bundle.getString("nombreRestaurante");
		 
		// Importamos la base de datos
		importarBaseDatatos();
	 
		// Inicializamos y cargamos Tabs superiores que contendrá el actionBar
		inicializarActionBarConTab();
		cargarTabsSuperiores();
		
		// Cerramos la base de datos
		sql.close();
		
		// Inicializamos y cargamos Tabs inferiores
		inicializarTabsInferiores();
		cargarTabsInferiores();
		
		// Cargamos en el fragment la pantalla de bienvenida del restaurante
		lanzarPantallaBienvenida();
    }
    
    /* Metodo encargado de implementar el botón back.
     * De la pantalla de navegación de platos, si se pulsa back, volverá a la pantalla 
     * de selección de restaurantes.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si pulsamos el botón back
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }
     
    // Importamos la base de datos
    private void importarBaseDatatos(){
        try{
        	sql = new Handler(getApplicationContext()); 
        	db = sql.open();
        }catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        }	
	}
    
    // Metodo encargado de inicializar el actionBar y ponerlo en modo tabs
    private void inicializarActionBarConTab(){
    	// Recogemos ActionBar
        actionbar = getActionBar();
        
        // Seleccionamos el modo tabs en el actionBar
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }
    
    // Metodo encargado crear los tabs superiores con la informacion referente a las categorias del restaurante
    private void cargarTabsSuperiores(){
    	Set<String> tipos = new HashSet<String>();
    	Iterator<String> it;
    	
    	// Obtenemos las distintas categorías de platos que hay
    	try{
    		String[] campos = new String[]{"Categoria"};
	    	String[] datos = new String[]{restaurante};
	    	Cursor c = db.query("Restaurantes", campos, "Restaurante=?", datos,null, null,null);
	    	
	    	// Recorremos todos los registros
	    	while(c.moveToNext()){
	    		String tipo = c.getString(0);
	    		tipos.add(tipo);
	    	} 

	    }catch(SQLiteException e){
	        Toast.makeText(getApplicationContext(),"ERROR BASE DE DATOS -> TABS",Toast.LENGTH_SHORT).show();	
	    }
    	
    	Stack<String> pilaTipos = new Stack<String>();
    	
    	// Solo sirve para mostrar bien los nombres de los tabs (para que salgan primero los principales...)
    	it = tipos.iterator();
    	while (it.hasNext()){
    		pilaTipos.add(it.next());
    	}
    	
    	// Vamos añadiendo cada categoría a los tabs
    	String tipoTab="";
    	while (!pilaTipos.isEmpty()){
    		// Sacamos el nombre de la categoría
    		tipoTab = pilaTipos.pop();
    		// Creamos el tab
    		ActionBar.Tab tab = actionbar.newTab().setText(tipoTab);
    		// Creamos el fragment de cada tab y le metemos el restaurante al que pertenece
    		Fragment tabFragment = new ContenidoTabsSuperioresFragment();
    		((ContenidoTabsSuperioresFragment) tabFragment).setTipoTab(tipoTab);
    		((ContenidoTabsSuperioresFragment) tabFragment).setRestaurante(restaurante);
    		// Hacemos oyente al tab
    		tab.setTabListener(new MiTabsSuperioresListener(tabFragment,tipoTab));
    		// Añadimos dicha categoría como tab
    		actionbar.addTab(tab);
    	}
	}
    
    // Metodo encargado de inicializar los tabs inferiores
    private void inicializarTabsInferiores(){
    	// Creamos los tabs inferiores y los inicializamos
		tabs = (TabHost)findViewById(android.R.id.tabhost);
		// Les hacemos oyentes
		tabs.setOnTabChangedListener(this);
        tabs.setup();
    }
    
    // Metodo encargado crear los tabs inferiores con las funcionalidades que ofrecemos al usuario
    private void cargarTabsInferiores(){
    	// Creamos el tab1 --> Promociones
        TabHost.TabSpec spec = tabs.newTabSpec("tabPromociones");
        // Hacemos referencia a su layout correspondiente
        spec.setContent(R.id.tab1);
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabPromociones"));
        // Lo añadimos
        tabs.addTab(spec);
        
        // Creamos el tab2 --> Pedido a sincronizar
        spec=tabs.newTabSpec("tabPedidoSincronizar");
        spec.setContent(R.id.tab2);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabPedidoSincronizar"));
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Cuenta
        spec=tabs.newTabSpec("tabCuenta");
        spec.setContent(R.id.tab3);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabCuenta"));
        tabs.addTab(spec);
        
        // Creamos el tab4 --> Calculadora
        spec=tabs.newTabSpec("tabCalculadora");
        spec.setContent(R.id.tab4);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabCalculadora"));
        tabs.addTab(spec);
        
        int numeroTabs = tabs.getTabWidget().getChildCount();
        for(int i=0; i<numeroTabs; i++){
            tabs.getTabWidget().getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
            	/* Implementamos la acción de cada tab cuando se pinche en él
            	 * Es distinto del onTabChange, puesto que auío entra si pulsamos 
            	 * sobre un tab ya seleccionado.
            	 */
            	public boolean onTouch(View v, MotionEvent event){
            		// Cuando pulsamos el tab
            		if(event.getAction()==MotionEvent.ACTION_UP){
	                	if(tabs.getCurrentTabTag().equals("tabPromociones")){
	                		/*
	            			 * TODO Hacer su layout y su funcionalidad
	            			 */
	        				Log.i("touch", "Promociones");
	        			}else if(tabs.getCurrentTabTag().equals("tabPedidoSincronizar")){
	        				Log.i("touch", "Pedido a Sinccronizar");
	        				Fragment fragmentPedido = new PedidoFragment();
	        		        FragmentTransaction m = getFragmentManager().beginTransaction();
	        		        m.replace(R.id.FrameLayoutPestanas, fragmentPedido);
	        		        m.commit();
	        			}else if(tabs.getCurrentTabTag().equals("tabCuenta")){
	        				Log.i("touch", "Cuenta");
	        				Fragment fragmentCuenta = new CuentaFragment();
	        		        FragmentTransaction m = getFragmentManager().beginTransaction();
	        		        m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	        		        m.commit();
	        			}else if(tabs.getCurrentTabTag().equals("tabCalculadora")){
	        				/*
	        				 * TODO Hacer su layout y su funcionalidad
	        				 */
	        				Log.i("touch", "Calculadora");
	        			}
            		}
            		return false;
                }
            });
        }       
    }
    
    // Metodo encargado de preparar las vistas de cada tab inferior
    private View prepararTabView(Context context, String nombreTab){
    	// Cargamos el layout
    	tabInferiorContentView = LayoutInflater.from(context).inflate(R.layout.tabs_inferiores, null);
    	// Cargamos el icono del tab
    	ImageView imagenTab = (ImageView)tabInferiorContentView.findViewById(R.id.imageViewTabInferior);
    	// Cargamos el titulo del tab
    	TextView textoTab = (TextView)tabInferiorContentView.findViewById(R.id.textViewTabInferior);
    	textoTab.setTextColor(Color.BLACK);
    	// Asignamos el título e icono para cada tab
    	if(nombreTab.equals("tabPromociones")){
    		textoTab.setText("Promociones");
    		imagenTab.setImageResource(getResources().getIdentifier("ofertas","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabPedidoSincronizar")){
    		textoTab.setText("Pedido a \nSincronizar");
    		imagenTab.setImageResource(getResources().getIdentifier("pedido","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabCuenta")){
    		textoTab.setText("Cuenta");
    		imagenTab.setImageResource(getResources().getIdentifier("pagar","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabCalculadora")){
    		textoTab.setText("Calculadora");
    		imagenTab.setImageResource(getResources().getIdentifier("calculadora","drawable",this.getPackageName()));
    	}
    	return tabInferiorContentView;
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		if(tabId.equals("tabPromociones")){
			/*
			 * TODO Hacer su layout y su funcionalidad
			 */
			Log.i("changed", "Promociones");
		}else if(tabId.equals("tabPedidoSincronizar")){
			Log.i("changed", "Pedido a Sinccronizar");
			Fragment fragmentPedido = new PedidoFragment();
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPedido);
	        m.addToBackStack("Pedido");
	        m.commit();
		}else if(tabId.equals("tabCuenta")){
			Log.i("changed", "Cuenta");
			Fragment fragmentCuenta = new CuentaFragment();
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	        m.addToBackStack("Cuenta");
	        m.commit();
		}else if(tabId.equals("tabCalculadora")){
			/*
			 * TODO Hacer su layout y su funcionalidad
			 */
			Log.i("changed", "Calculadora");
		}
	}

	// Metodo encargado de devolver el contenido de cada tab
	public View createTabContent(String tag) {
        return tabInferiorContentView;
	}
	
	// Metodo encargado de lanzar la pantalla de bienvenida
	public void lanzarPantallaBienvenida(){
		// Mostramos la pantalla inicial del restaurante con su logo y un mensaje de bienvenida
		Fragment fragmentPantallaInicioRes = new PantallaInicialRestaurante();
		((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
        FragmentTransaction m = getFragmentManager().beginTransaction();
        m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
        m.addToBackStack("Pantalla Bienvenida");
        m.commit();
	}
}