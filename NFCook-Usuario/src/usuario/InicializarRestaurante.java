package usuario;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import fragments.ContenidoTabSuperiorCategoriaBebidas;
import fragments.CuentaFragment;
import fragments.MiTabsSuperioresListener;
import fragments.PantallaInicialRestaurante;
import fragments.PedidoFragment;
import fragments.ContenidoTabsSuperioresFragment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Esta clase es la encargada de cargar toda la información del restaurante que hemos seleccionado.
 * 
 * Carga los tabs superiores con las distintas categorías que ofreza el restaurante.
 * Carga el logo y el texto de bienvenida del restaurante.
 * Carga los tabs inferiores, que serán comunes para todos los restaurantes.
 * 
 * @author Abel
 *
 */
public class InicializarRestaurante extends Activity implements TabContentFactory, OnTabChangeListener{
	
	private ImageView imagenRestaurante;
	private String restaurante;
	
	// Base de datos
	private HandlerDB sql;
	private SQLiteDatabase db;
	
	// Actionbar para los tabs superiores con las categrías de los platos
	private static ActionBar actionbar;
	
	// Tabs inferiores con las funcionalidades de la aplicacion
	private TabHost tabs;
	// Vista de los tabs inferiores
	private View tabInferiorContentView;
	
	// Para volver cuando lanzamos una actividad
	private static boolean seleccionadoTabSuperior;
	private static int postabSuperiorPulsado;
	
	// global para poder acceder a el y trabajar con la ayuda
	private Fragment fragmentPantallaInicioRes;
	private String anteriorTabPulsado;
	
	private int numComensales;
		
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
		
		// Descamarcamos el tab superior activado para evitar confusiones
		desmarcarTabSuperiorActivo();
		
		// Cerramos la base de datos
		sql.close();
		
		// Inicializamos y cargamos Tabs inferiores
		inicializarTabsInferiores();
		cargarTabsInferiores();
    }
    
    /* Metodo encargado de implementar el botón back.
     * De la pantalla de navegación de platos, si se pulsa back, volverá a la pantalla 
     * de selección de restaurantes.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Si pulsamos el botón back
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if(anteriorTabPulsado.equals("tabInicio")){
    			if (((PantallaInicialRestaurante) fragmentPantallaInicioRes).comprobarImagenActiva()){
    				// Cargamos en el fragment la pantalla de bienvenida del restaurante
    				fragmentPantallaInicioRes = new PantallaInicialRestaurante();
    				((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
    				FragmentTransaction m = getFragmentManager().beginTransaction();
    				m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
    				m.commit();
    				return false;
    			}
    			else finish();
    		} else finish();
        }
		return super.onKeyDown(keyCode, event);
 
    }
     
    // Importamos la base de datos
    private void importarBaseDatatos(){
        try{
        	sql = new HandlerDB(getApplicationContext()); 
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
    	Stack<String> tipos = new Stack<String>();
    	
    	// Obtenemos las distintas categorías de platos que hay
    	try{
    		String[] campos = new String[]{"Categoria"};
	    	String[] datos = new String[]{restaurante};
	    	Cursor c = db.query("Restaurantes", campos, "Restaurante=?", datos,null, null,null);
	    	
	    	// Recorremos todos los registros
	    	while(c.moveToNext()){
	    		String tipo = c.getString(0);
	    		if(!tipos.contains(tipo))
	    			tipos.add(tipo);
	    	} 

	    }catch(SQLiteException e){
	        Toast.makeText(getApplicationContext(),"ERROR BASE DE DATOS -> TABS",Toast.LENGTH_SHORT).show();	
	    }
    	
    	Stack<String> pilaTipos = new Stack<String>();
    	
    	// Solo sirve para mostrar bien los nombres de los tabs (para que salgan primero los principales...)
    	while (!tipos.isEmpty()){
    		pilaTipos.add(tipos.pop());
    	}
    	
    	// Vamos añadiendo cada categoría a los tabs
    	String tipoTab="";
    	int i = 0;
    	while (!pilaTipos.isEmpty()){
    		// Sacamos el nombre de la categoría
    		tipoTab = pilaTipos.pop();
    		// Creamos el tab
    		ActionBar.Tab tab = actionbar.newTab().setText(tipoTab);
    		// Creamos el fragment de cada tab y le metemos el restaurante al que pertenece
    		// Miramos si se trata de la categoría bebidas que tendrá un fragment distinto
    		if(tipoTab.toLowerCase().equals("bebidas")){
    			Fragment tabFragment = new ContenidoTabSuperiorCategoriaBebidas();
	    		ContenidoTabSuperiorCategoriaBebidas.setTipoTab(tipoTab);
	    		ContenidoTabSuperiorCategoriaBebidas.setRestaurante(restaurante);
	    		// Hacemos oyente al tab
	    		tab.setTabListener(new MiTabsSuperioresListener(tabFragment, tipoTab, this, i));
    		}else{
	    		Fragment tabFragment = new ContenidoTabsSuperioresFragment();
	    		((ContenidoTabsSuperioresFragment) tabFragment).setcategoriaTab(tipoTab);
	    		((ContenidoTabsSuperioresFragment) tabFragment).setRestaurante(restaurante);
	    		// Hacemos oyente al tab
	    		tab.setTabListener(new MiTabsSuperioresListener(tabFragment,tipoTab, this, i));
    		}
    		// Añadimos dicha categoría como tab
    		actionbar.addTab(tab, i, true);
    		i++;
    	}
    	
    	// Marcamos el tab primero para situar el foco en el primer tab
    	actionbar.selectTab(actionbar.getTabAt(0));
    	
    	// Ponemos el título a la actividad
    	actionbar.setTitle(" INICIO");
	}
	
	/*
	 *  Metodo encargado de desmarcar el tab superior activado para evitarle líos
	 *  al usuario.
	 */
	public void desmarcarTabSuperiorActivo(){
		/*
		 * La única forma de descmarcar el tab activo superior del action bar, es llamando
		 * al siguiente metodo con null que sabemos que va a rebentar y hacemos un try
		 * catch para controlarlo y que funcione tal y como queremos.
		 */
		try{
            actionbar.selectTab(null);
         }catch (Exception e){
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
    	// Creamos el tab1 --> Inicio
        TabHost.TabSpec spec = tabs.newTabSpec("tabInicio");
        // Hacemos referencia a su layout correspondiente
        spec.setContent(R.id.tab1);
        // Preparamos la vista del tab con el layout que hemos preparado
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabInicio"));
        // Lo añadimos
        tabs.addTab(spec);
        
        // Creamos el tab2 --> Pedido a sincronizar
        spec = tabs.newTabSpec("tabPedidoSincronizar");
        spec.setContent(R.id.tab2);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabPedidoSincronizar"));
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Cuenta
        spec = tabs.newTabSpec("tabCuenta");
        spec.setContent(R.id.tab3);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabCuenta"));
        tabs.addTab(spec);
        
        // Creamos el tab4 --> Calculadora
        spec = tabs.newTabSpec("tabCalculadora");
        spec.setContent(R.id.tab4);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabCalculadora"));
        tabs.addTab(spec);
        
        /*
         * Creamos un tab falso para que cada vez que pulsemos en cualquiera de los tabs
         * inferiores, redirigimos como tab pulsado al falso, de esta forma conseguimos
         * que cada vez que pulsemos en cada tab inferior entre en el método onchanged.
         */
        // Creamos el tab5 --> tabFalso
        spec = tabs.newTabSpec("tabFalso");
        spec.setContent(R.id.tab5);
        spec.setIndicator(prepararTabView(getApplicationContext(),"tabFalso"));
        tabs.addTab(spec);
        
        // Seleccionamos momentaneamente el tab falso para ocultarlo
        tabs.setCurrentTabByTag("tabFalso");
        // Lo ocultamos, consiguiendo que esté pero no lo veamos, justo lo que queremos
        tabs.getCurrentTabView().setVisibility(View.GONE);
        
        // para ayuda
        anteriorTabPulsado = "tabInicio";
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
    	if(nombreTab.equals("tabInicio")){
     		textoTab.setText("INICIO");
     		imagenTab.setImageResource(getResources().getIdentifier("inicio","drawable",this.getPackageName()));
     	}else if(nombreTab.equals("tabPedidoSincronizar")){
    		textoTab.setText("PEDIDO");
    		imagenTab.setImageResource(getResources().getIdentifier("pedido","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabCuenta")){
    		textoTab.setText("CUENTA");
    		imagenTab.setImageResource(getResources().getIdentifier("pagar","drawable",this.getPackageName()));
    	}else if(nombreTab.equals("tabCalculadora")){
    		textoTab.setText("CALCULADORA");
    		imagenTab.setImageResource(getResources().getIdentifier("calculadora","drawable",this.getPackageName()));
    	}
    	return tabInferiorContentView;
    }
    
    // Metodo encargado de definir la acción de cada tab cuando sea seleccionado
	public void onTabChanged(String tabId) {
		if(tabs.getCurrentTabTag().equals("tabInicio")){
			// Descamarcamos el tab superior activado para evitar confusiones
			desmarcarTabSuperiorActivo();
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;
            
            //anterior tab pulsado
            anteriorTabPulsado = "tabInicio";

    		/*
			 * TODO Completar funcionalidad de la pantalla de bienvenida
			 */
			// Cargamos en el fragment la pantalla de bienvenida del restaurante
			fragmentPantallaInicioRes = new PantallaInicialRestaurante();
			((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
	        m.commit();
		}else if(tabId.equals("tabPedidoSincronizar")){
			// Descamarcamos el tab superior activado para evitar confusiones
			desmarcarTabSuperiorActivo();
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;

            //anterior tab pulsado
            anteriorTabPulsado = "tabPedidoSincronizar";
            
			Fragment fragmentPedido = new PedidoFragment();
			PedidoFragment.setRestaurante(restaurante);
	        FragmentTransaction m = getFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPedido);
	        m.addToBackStack("Pedido");
	        m.commit();
		}else if(tabId.equals("tabCuenta")){
			// Descamarcamos el tab superior activado para evitar confusiones
			desmarcarTabSuperiorActivo();
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;
            
            //anterior tab pulsado
            anteriorTabPulsado = "tabCuenta";
            
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
		}
	}

	// Metodo encargado de devolver el contenido de cada tab
	public View createTabContent(String tag) {
        return tabInferiorContentView;
	}
	
	// Metodo encargado de lanzar la ventana emergente para indicar el número de comesales
	public void lanzarVentanaEmergenteParaIndicarNumeroComensales(){
		numComensales = 0;
		
        // Creamos y lanzamos la ventana emergente para conocer el nº de comensales
		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(InicializarRestaurante.this); 
        // Creamos su vista
        View vistaVentanaEmergente = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ventana_emergente_seleccionar_numero_comensales, null);
        final EditText editTextNumeroPersonas = (EditText) vistaVentanaEmergente.findViewById(R.id.editTextNumComensalesCalculadora);
        // Hacemos oyente al campo que recoge el número de comensales
        editTextNumeroPersonas.addTextChangedListener(new TextWatcher() {
			
			public void afterTextChanged(Editable s) {
	        	// Intentamos hacer la conversion a entero
				try{
					String a = editTextNumeroPersonas.getText().toString();
	          	  	numComensales = Integer.parseInt(a);
				}catch(Exception e){
					numComensales = 0;
		         	Toast.makeText(getApplicationContext(),"Introduce un número de comensales válido.",Toast.LENGTH_SHORT).show();
				}
	        }
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				  
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				  
			}
		});
        
        //Limitamos a 99 el máximo de comensales
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(2);
		editTextNumeroPersonas.setFilters(filterArray);
        
        ventanaEmergente.setNegativeButton("Cancelar", null);
        // Si selecciona sobre aceptar, lanzamos la pantalla calculadora
        ventanaEmergente.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
          	public void onClick(DialogInterface dialog, int whichButton) {
          		if(numComensales > 0){
    				// Descamarcamos el tab superior activado para evitar confusiones
    				desmarcarTabSuperiorActivo();
    				
    				// Lanzamos la actividad de una forma específica para conocer cuando acaba
    				Intent intent = new Intent(getApplicationContext(), Calculadora.class);
					intent.putExtra("numeroComensales", numComensales);
					intent.putExtra("restaurante", restaurante);
					startActivityForResult(intent, 0);
          		}else{
		         	Toast.makeText(getApplicationContext(),"Introduce un número de comensales válido.",Toast.LENGTH_SHORT).show();
          		}
          	}
        });
        
        // Aplicamos la vista y la mostramos
		ventanaEmergente.setView(vistaVentanaEmergente);
		ventanaEmergente.show();
	}
	
	/*
	 * Metodo encargado de implementar la acción correspondiente de cuando la calculadora
	 * termine. En nuestro caso si antes estuviese marcado un tab superior lo marcamos, para
	 * no liar al usuario.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(seleccionadoTabSuperior){
			actionbar.selectTab(actionbar.getTabAt(postabSuperiorPulsado));
		}	
	}
	
	// Metodo encargado de decirnos si se ha sincronizado algún pedido o no
	public boolean hayAlgunPedidoSincronizado(){
		try{
			// Importamos la base de datos de cuenta
			HandlerDB sqlCuenta = new HandlerDB(getApplicationContext(),"Cuenta.db"); 
			SQLiteDatabase dbCuenta = sqlCuenta.open();
			String[] camposCuenta = new String[]{"Id"};//Campos que quieres recuperar
			Cursor cursorCuenta = dbCuenta.query("Cuenta", camposCuenta, null, null,null, null,null);
			// Miramos a ver si tiene al menos algún elemento
			if(cursorCuenta.moveToFirst()){
				return true;
			}else{
				return false;
			}
	    }catch(SQLiteException e){
	     	Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS DE CUENTA",Toast.LENGTH_SHORT).show();
	     	return false;
	    }
	}
	
	// Metodo encargado de lanzar la ventana emergente para indicar el número de comesales
	public void lanzarVentanaEmergenteAvisoSeNecesitaMinimoUnPedido(){
        // Creamos y lanzamos la ventana emergente para conocer el nº de comensales
		final AlertDialog ventanaEmergente = new AlertDialog.Builder(InicializarRestaurante.this).create(); 
        // Creamos su vista
        View vistaVentanaEmergente = LayoutInflater.from(getApplicationContext()).inflate(R.layout.aviso_seccion_no_disponible, null);
        
        // Sacamos los campos y les damos valor
        ImageView imageViewCalculadora = (ImageView) vistaVentanaEmergente.findViewById(R.id.imageViewConstruccion);  
        imageViewCalculadora.setImageResource(getResources().getIdentifier("calculadora","drawable",this.getPackageName()));
        TextView textViewMensaje = (TextView) vistaVentanaEmergente.findViewById(R.id.textViewInformacionAviso);  
        textViewMensaje.setText("Debe haber realizado mínimo algún pedido para poder disfrutar de esta utilidad.");
        
        // Aplicamos la vista y la mostramos
		ventanaEmergente.setView(vistaVentanaEmergente);
		ventanaEmergente.show();
		
		//Crea el timer para que el mensaje solo aparezca durante 3,5 segundos
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				ventanaEmergente.dismiss(); 
				t.cancel(); 
			}
		}, 3500);	
	}
	
	public static void setSeleccionadoTabSuperior(boolean seleccionado){
		seleccionadoTabSuperior = seleccionado;
	}
	
	public static void setPosTabSuperior(int posTab){
		postabSuperiorPulsado = posTab;
	}
		
	public static void marcaTab(int pos){
		actionbar.selectTab(actionbar.getTabAt(0));
	}
}