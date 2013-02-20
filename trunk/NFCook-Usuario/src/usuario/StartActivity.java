package usuario;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.example.nfcook.R;

import fragments.CuentaFragment;
import fragments.MyTabsListener;
import fragments.PantallaInicialRestaurante;
import fragments.PedidoFragment;
import fragments.TabsFragment;

import baseDatos.Handler;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class StartActivity extends Activity {
	public static Context appContext;
	private ImageView img;
	
	private Handler sql;
	private SQLiteDatabase db;
	
	private ActionBar actionbar;
	
	private String restaurante;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE | Window.FEATURE_ACTION_BAR);
        //this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        //Quitamos barra de notificaciones
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.pestanas);

	    img = (ImageView)findViewById(R.id.imageView1);
	    Bundle bundle = getIntent().getExtras();
		img.setImageResource(bundle.getInt("logoRestaurante"));
		restaurante = bundle.getString("nombreRestaurante");
		 
		//Importamos Base de Datos
		importarBaseDatatos();
	 
		//Inicializamos y cargamos TABs
		inicializarActionBarTAB();
		cargarTABs();	
		
		// Mostramos la pantalla inicial del restaurante con su logo y un mensaje de bienvenida
		Fragment fragmentPantallaInicioRes= new PantallaInicialRestaurante();
		((PantallaInicialRestaurante)fragmentPantallaInicioRes).setImagen(bundle.getInt("logoRestaurante"));
		((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
        FragmentTransaction m = getFragmentManager().beginTransaction();
        m.replace(R.id.RelativeLayout1, fragmentPantallaInicioRes);
        m.addToBackStack("DescripcionPlato");
        m.commit();
    }
    
    public void setRestaurante(String res){
    	restaurante = res;
    }
    
    private void cargarTABs(){
    	Set<String> tipos = new HashSet<String>();
    	Iterator<String> it;
    	
    	try{
    		String[] campos = new String[]{"Categoria"};//Campos que quieres recuperar
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
    	
    	String tipoTab="";
    	while (!pilaTipos.isEmpty()){
    		tipoTab = pilaTipos.pop();
    		ActionBar.Tab tab = actionbar.newTab().setText(tipoTab);
    		Fragment tabFragment = new TabsFragment();
    		((TabsFragment) tabFragment).setTipoTab(tipoTab);
    		((TabsFragment) tabFragment).setRestaurante(restaurante);
    		tab.setTabListener(new MyTabsListener(tabFragment));
    		actionbar.addTab(tab);
    
    	}
	}
    
    private void importarBaseDatatos(){
        try{
        	sql = new Handler(getApplicationContext()); 
        	db = sql.open();
        }catch(SQLiteException e){
         	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        }	
	}
    
    private void inicializarActionBarTAB(){
    	//Recogemos ActionBar
        actionbar = getActionBar();

        //Modo TABS
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }
    
    
    // FUNCIONALIDAD BOTONES INFERIORES
    public void OnClickSugerencias (View boton){
    	// Rellenar
    }
    
    
    // Falta meterle al metodo la información referente al pedido en donde la tengamos guardada
    public void OnClickCuenta (View boton){	
    	Fragment fragmentCuenta = new CuentaFragment();
    	String [] Ejemplatos = {};
    	//String [] Ejemplatos = {"Beicon & Cheese Fries", "8.35","BBQ Egg Burguer","10.85","New York Cheese Cake","5.35","New York Cheese Cake","5.35","New York Cheese Cake","5.35"};
	    ((CuentaFragment)fragmentCuenta).platos = Ejemplatos;
        FragmentTransaction m = getFragmentManager().beginTransaction();
        m.replace(R.id.RelativeLayout1, fragmentCuenta);
        m.addToBackStack("Seleccion de platos");
        m.commit();
    }
    
    // Falta meterle al metodo la información referente al pedido en donde la tengamos guardada
    public void OnClickPedido (View boton){
    	Fragment fragmentPedido = new PedidoFragment();
    	//String [] Ejemplatos = {"Hamburguesa Director", "50","Ensalada cesar","16","Hamburguesa de pollo","20","Habichuelas con lacon","25","Sopitas de la abuela","250"};
    	String [] Ejemplatos = {"Beicon & Cheese Fries", "8.35","BBQ Egg Burguer","10.85","New York Cheese Cake","5.35","New York Cheese Cake","5.35","New York Cheese Cake","5.35"};
    	
	    ((PedidoFragment)fragmentPedido).platos = Ejemplatos;
        FragmentTransaction m = getFragmentManager().beginTransaction();
        m.replace(R.id.RelativeLayout1, fragmentPedido);
        m.addToBackStack("Seleccion de platos");
        m.commit();
    }
    
    public void OnClickReparto (View boton){
    	// Rellenar
    }
}