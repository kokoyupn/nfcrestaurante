package usuario;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import android.widget.RatingBar;
import usuario.Mail;


import fragments.ContenidoTabSuperiorCategoriaBebidas;
import fragments.CuentaFragment;
import fragments.PantallaInicialRestaurante;
import fragments.PedidoFragment;
import fragments.ContenidoTabsSuperioresFragment;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import adapters.PagerAdapter;
import alertDialogPersonalizado.ActionItem;
import alertDialogPersonalizado.QuickAction;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;
import facebook.FacebookPublicarYLogin;

/**
 * Esta clase es la encargada de cargar toda la información del restaurante que hemos seleccionado.
 * 
 * Carga los tabs superiores con las distintas categorías de la carta que ofreza el restaurante.
 * Carga el logo y el texto de bienvenida del restaurante.
 * Carga los tabs inferiores, que serán comunes para todos los restaurantes.
 * 
 * @author Abel
 *
 */
@SuppressLint("NewApi")
public class InicializarRestaurante extends FragmentActivity 
									implements OnTabChangeListener, OnPageChangeListener{
	
	private ImageView imagenRestaurante;
	private String restaurante;
	
	//Valoración
	public Builder ventanaEmergente;
	private float resultado=0;
	private String mensaje="";
	private RatingBar ambiente,calidad,servicio,limpieza,precio;
	private TextView mediaTexto;
	private AutoCompleteTextView comentarios;
	
	// Base de datos
	private HandlerDB sql;
	private SQLiteDatabase db;
	
	// Actionbar para los tabs superiores con las categrías de los platos
	private static ActionBar actionbar;
	
	// Tabs inferiores con las funcionalidades de la aplicacion
	private static TabHost tabs;
	public static TabHost tabsSuperiores;
	// Vista de los tabs inferiores y superiores
	private View tabInferiorContentView;
	private View tabSuperiorContentView;
	
	// Para volver cuando lanzamos una actividad
	private static boolean seleccionadoTabSuperior;
	private static int postabSuperiorPulsado;
	
	// global para poder acceder a el y trabajar con la ayuda
	private Fragment fragmentPantallaInicioRes;
	private static String anteriorTabPulsado;
	private static int tabInferiorSeleccionado;
	
	
	// Atributos para poder deslizar el dedo horizontalmente entre pestañas
	public static ViewPager miViewPager;
	private PagerAdapter miPagerAdapter;
	
	// Vector en el que guardaremos todos los fragments generados
	private ArrayList<Fragment> listFragments;
	private ArrayList<Fragment> listFragmentsCopia;
	private ArrayList<Fragment> listFragmentsVacios;
	
	private int numComensales;
	
	//Menu emergente Redes Soaciales
	// ID Acciones
	private static final int ID_FACEBOOK = 1;
	private static final int ID_TWITER = 2;
	private QuickAction quickAction;
	
	//Twiter
	private static final String CONSUMER_KEY = "e4G6cmP3VA7YNlB9sl9Ug";
	private static final String CONSUMER_SECRET = "9ZZ1H7kJQ4WSpWMquzvKgYWbEBmcWasYeyuCFE3RZ8";
	private static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	private static final String CALLBACK_URL = "app://IniciarRestaurante";
	
	private TextView campoTweet;
	private ImageView botonEnviarTweet;
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	private AccessToken accessToken;
	private AlertDialog ventanaEmergenteTwitter;
	private AlertDialog ventanaEmergenteAutentificarTwitter;

	private boolean tabsSuperioresCompletos;
	private ArrayList<String> listNombresTabsSuperiores;
	public static boolean usandoTabsInferiores;
	
	//Es necesario para poder invocar a su onActivityResult manualmente en el de esta clase
	private Fragment fragmentPedido; 
	private Fragment fragmentCuenta;
	
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
        
        listFragments = new ArrayList<Fragment>();
        listFragmentsCopia = new ArrayList<Fragment>();
        listFragmentsVacios = new ArrayList<Fragment>();
        // Cargamos el logo del restaurante en el layout y asignamos el nombre del restaurante
        imagenRestaurante = (ImageView)findViewById(R.id.imageViewLogoRestaurante);
        Bundle bundle = getIntent().getExtras();
	    imagenRestaurante.setImageResource(bundle.getInt("logoRestaurante"));
		restaurante = bundle.getString("nombreRestaurante");
		
		// Importamos la base de datos
		importarBaseDatatos();
		
		// Variables que es necesario inicializar antes de crear los tabs inferiores
		tabsSuperioresCompletos = false;
		usandoTabsInferiores = true;
		listNombresTabsSuperiores = new ArrayList<String>();
		
		// Inicializamos y cargamos Tabs inferiores
		inicializarTabsInferiores();
		cargarTabsInferiores();
	 
		inicializarActionBar();
		
		// Inicializamos y cargamos Tabs superiores
		inicializarTabsSuperiores();
		cargarTabsSuperiores();
		
		// Este if es necesario para el correcto funcionamiento del ViewPager
		if (savedInstanceState != null)
            tabsSuperiores.setCurrentTabByTag(savedInstanceState.getString("tabSup"));
        else 
        	tabsSuperiores.setCurrentTab(0); 
		
		// Cerramos la base de datos
		sql.close();
		
		// Descamarcamos el tab superior activado para evitar confusiones
		//desmarcarTabSuperiorActivo();
		
		// Seleccionamos el tab inicio para que salga una vez selecciones el restaurante
        tabs.setCurrentTabByTag("tabInicio");
	    tabInferiorSeleccionado = 0;
	  
    }
    
    protected void onSaveInstanceState(Bundle instanceState) {
        // Guardar en "tabSup" la pestaña seleccionada.
        instanceState.putString("tabSup", tabsSuperiores.getCurrentTabTag());
        super.onSaveInstanceState(instanceState);
	}
    
    /* Metodo encargado de implementar el botón back.
     * De la pantalla de navegación de platos, si se pulsa back, volverá a la pantalla 
     * de selección de restaurantes.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
    	tabsSuperiores = null;
		tabs = null;
    	tabInferiorSeleccionado = 0;
    	// Si pulsamos el botón back
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if(anteriorTabPulsado.equals("tabInicio")){
    			if (((PantallaInicialRestaurante) fragmentPantallaInicioRes).comprobarImagenActiva()){
    				// Cargamos en el fragment la pantalla de bienvenida del restaurante
    				fragmentPantallaInicioRes = new PantallaInicialRestaurante();
    				((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
    				FragmentTransaction m = getSupportFragmentManager().beginTransaction();
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
    
    // Metodo encargado de inicializar el actionBar
    private void inicializarActionBar(){
    	// Recogemos ActionBar
        actionbar = getActionBar();
        
        // Seleccionamos el modo tabs en el actionBar
        //actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        // atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
    
    
    // para meter botones en el action bar
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.redes_sociales, menu);
        inflater.inflate(R.menu.enviar_valoraciones, menu);
        ImageView botonRedesSociales = (ImageView) menu.findItem(R.id.icono_redes_sociales).getActionView();
        botonRedesSociales.setBackgroundResource(R.drawable.compartir_redes_sociales);
        botonRedesSociales.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                crearPopupMenuRedesSociales();
                quickAction.show(v);
            }
        });
        return true;
    }
    
    public void crearPopupMenuRedesSociales(){
    	ActionItem facebook = new ActionItem(ID_FACEBOOK, null, getResources().getDrawable(R.drawable.logofb));
		ActionItem twitter = new ActionItem(ID_TWITER, null, getResources().getDrawable(R.drawable.logotw));
		
		quickAction = new QuickAction(this);
		
		//add action items into QuickAction
        quickAction.addActionItem(facebook);
		quickAction.addActionItem(twitter);
        
        //Set listener for action item clicked
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				
				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_FACEBOOK) {
					Intent intent = new Intent(getApplicationContext(), FacebookPublicarYLogin.class);
			    	startActivity(intent);
				} else if (actionId == ID_TWITER) {
					autorizaTwitter();
					//SharedPreferences autorizacionTwitter = getSharedPreferences("twiter", 0);
		    		//String userKey = autorizacionTwitter.getString("user_key", "vacio");
		    		//String userSecret = autorizacionTwitter.getString("user_secret", "vacio");
		    		//if(userSecret.equals("vacio") || userKey.equals("vacio")){
		    		//	autorizaTwitter();
		    		//}else{
		    		//	accessToken = new AccessToken(userKey, userSecret);
		        	//	lanzarVentanaEmergenteTwitter();
		    		//}
				}
			}
		}); 	
    }
    
    // para el atras y botones del action bar
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId()==R.id.icono_redes_sociales){
    		//Hecho en el onCreateOptionMenu, sino no puedo tener acceso a la vista del boton
    		// codigo de prado
    		return true;
    	}
    	else if (item.getItemId()==R.id.icono_enviar_valoraciones){
    		
    		/**
    		 *  valoraciones via email.
    		 * 
    		 * Las valoraciones se divide principalmente en las siguientes partes:
    		 * 
    		 * 1- Cinco secciones con sus correspondientes RatingBar y su nota media para que puedan calcular la valoración definitiva del restaurante.
    		 * 
    		 * 2- AutoCompleteTextView para añadir comentarios.
    		 * 
    		 * 3- Botón enviar en el que se crea la clase mail que cuando realiza la acción enviar necesita crear un nuevo hilo debido a su tamaño . * 
    		 * 
    		 * @author Busy
    		 *
    		 */
    		
    		//Creación y configuración de la ventana emergente
        	ventanaEmergente = new AlertDialog.Builder(InicializarRestaurante.this);
            View vistaAviso = LayoutInflater.from(InicializarRestaurante.this).inflate(R.layout.valoracion, null);
            
        	//Anadimos los botones de enviar y cancelar al alert Dialog
            onClickBotonAceptarAlertDialog(ventanaEmergente);
            onClickBotonCancelarAlertDialog(ventanaEmergente);
            ventanaEmergente.setView(vistaAviso);
            ventanaEmergente.show();
        	
            //Recuperamos los valores de nuestras rating bar en variables globales para que se vayan actualizando
            RatingBar ambienteEstrellas = (RatingBar) vistaAviso.findViewById(R.id.AmbienteStars);
            RatingBar calidadEstrellas = (RatingBar) vistaAviso.findViewById(R.id.CalidadStars);
            RatingBar limpiezaEstrellas = (RatingBar) vistaAviso.findViewById(R.id.LimpiezaStars);
            RatingBar precioEstrellas = (RatingBar) vistaAviso.findViewById(R.id.PrecioStars);
            RatingBar servicioEstrellas = (RatingBar) vistaAviso.findViewById(R.id.ServicioStars);
            TextView resultadoTexto = (TextView) vistaAviso.findViewById(R.id.resultadoText);
            AutoCompleteTextView comentariosText=(AutoCompleteTextView) vistaAviso.findViewById(R.id.ComentariosText);
            
            ambiente=ambienteEstrellas;
            calidad=calidadEstrellas;
            limpieza=limpiezaEstrellas;
            precio=precioEstrellas;
            servicio=servicioEstrellas;
            mediaTexto=resultadoTexto;
            comentarios=comentariosText;
            actualizaValorMedio();
            
            // dejar
            return true;
    	}
    	else {
    		// atras
    		tabsSuperiores = null;
    		tabs = null;
    		tabInferiorSeleccionado = 0;
        	if(anteriorTabPulsado.equals("tabInicio")){
        		if (((PantallaInicialRestaurante) fragmentPantallaInicioRes).comprobarImagenActiva()){
        			// Cargamos en el fragment la pantalla de bienvenida del restaurante
        			fragmentPantallaInicioRes = new PantallaInicialRestaurante();
        			((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
        			FragmentTransaction m = getSupportFragmentManager().beginTransaction();
        			m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
        			m.commit();
        		}
        		else finish();
        	} else finish();
        	return true;
    	}
		
   }
   
    public void actualizaValorMedio() {
   	 
    	ActualizaValorEstrellas(ambiente);
    	ActualizaValorEstrellas(calidad);
    	ActualizaValorEstrellas(limpieza);
    	ActualizaValorEstrellas(precio);
    	ActualizaValorEstrellas(servicio);
    	
    }
    
    public void ActualizaValorEstrellas(RatingBar rating){
    	rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				setResultado((ambiente.getRating()+calidad.getRating()+limpieza.getRating()+precio.getRating()+servicio.getRating())/5);
    			mediaTexto.setText(String.valueOf(resultado)+" / "+ambiente.getNumStars());
     
				
			}
    	});

    	
    	
    }
	public void obtenerResultado(){
		
		mensaje+="Valoracion"+("\n")+("\n");
		mensaje+="Ambiente: "+ambiente.getRating()+("\n"); 
		mensaje+="Calidad: "+calidad.getRating()+("\n");
        mensaje+="Limpieza: "+limpieza.getRating()+("\n");
        mensaje+="Calidad/Precio: "+precio.getRating()+("\n");
        mensaje+="Servicio: "+servicio.getRating()+("\n");
       
        mensaje+="Nota Media : "+ mediaTexto.getText() +("\n")+("\n");
    	
        mensaje+= "Comentarios" +("\n")+("\n");
        Editable g=comentarios.getText();
    	mensaje+=g.toString();
    }
  
	
	public void onClickBotonAceptarAlertDialog(Builder ventanaEmergente){
	
		
		ventanaEmergente.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				   	  
					obtenerResultado();
					
					Mail m = new Mail();
		            m.setUser("nfcookapp@gmail.com");// username 
		            m.setPass("Macarrones");// password

		            String[] toArr = {"nfcookapp@gmail.com"}; 
		            m.setTo(toArr); 
		            m.setFrom("nfcookapp@gmail.com"); 
		            m.setSubject("Valoracion Restaurante"); 
		            m.setBody(mensaje); 

		            try { 
		              //m.addAttachment("/sdcard/filelocation"); //archivos adjuntos

		              if(m.send()) { 
		                Toast.makeText(InicializarRestaurante.this, "Email enviado correctamente.", Toast.LENGTH_LONG).show(); 
		              } else { 
		                Toast.makeText(InicializarRestaurante.this, "Email no enviado.", Toast.LENGTH_LONG).show();//Si usuario y enviante no coinciden 
		              } 
		            } catch(Exception e) { 
		              Toast.makeText(InicializarRestaurante.this, "Error en el envio del email", Toast.LENGTH_LONG).show(); //Si ha habido fallos 
		            } 
		          }

		        }); 
		      } 
	
	public void onClickBotonCancelarAlertDialog(AlertDialog.Builder ventanaEmergente){
		ventanaEmergente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}
	public float getResultado() {
		return resultado;
	}



	public void setResultado(float f) {
		this.resultado = f;
	}
	
	private void inicializarTabsSuperiores(){
    	// Creamos los tabs inferiores y los inicializamos
		tabsSuperiores = (TabHost)findViewById(R.id.tabhostSuperior);
		tabsSuperiores.setup();
    }
    
    // Metodo encargado crear los tabs superiores con la informacion referente a las categorias del restaurante
	private void cargarTabsSuperiores(){
		// Utilizaremos el TabSpec para añadir cada pestaña superior
		TabHost.TabSpec spec; 
		
    	Stack<String> tipos = new Stack<String>();
    	
    	// Obtenemos las distintas categorías de platos que hay
    	try{
    		String[] campos = new String[]{"Categoria"};
	    	String[] datos = new String[]{restaurante};
	    	Cursor c = db.query("Restaurantes", campos, "Restaurante=?", datos,null, null,null);
	    	
	    	tipos.add("Mis\nFavoritos");
	    	
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
    	while (!pilaTipos.isEmpty()){
    		// Sacamos el nombre de la categoría
    		tipoTab = pilaTipos.pop();
    		// Creamos el tab
            spec = tabsSuperiores.newTabSpec(tipoTab);
            // Hacemos referencia a su layout correspondiente
            spec.setContent(R.id.tab1Sup);
            // Preparamos la vista del tab con el layout que hemos preparado
            spec.setIndicator(prepararTabViewSuperior(getApplicationContext(),tipoTab));
            // Lo añadimos
            tabsSuperiores.addTab(spec);
    		// Creamos el fragment de cada tab y le metemos el restaurante al que pertenece
    		// Miramos si se trata de la categoría bebidas que tendrá un fragment distinto
    		if(tipoTab.toLowerCase().equals("bebidas")){
    			Fragment tabFragment = new ContenidoTabSuperiorCategoriaBebidas();
	    		ContenidoTabSuperiorCategoriaBebidas.setTipoTab(tipoTab);
	    		ContenidoTabSuperiorCategoriaBebidas.setRestaurante(restaurante);
	    		listFragments.add(tabFragment);
	    		listFragmentsCopia.add(tabFragment);
    		}else{
	    		Fragment tabFragment = new ContenidoTabsSuperioresFragment();
	    		((ContenidoTabsSuperioresFragment) tabFragment).setcategoriaTab(tipoTab);
	    		((ContenidoTabsSuperioresFragment) tabFragment).setRestaurante(restaurante);
	    		listFragments.add(tabFragment);
	    		listFragmentsCopia.add(tabFragment);
	    	}
    		listFragmentsVacios.add(new Fragment());
    		listNombresTabsSuperiores.add(tipoTab);
    	}
    	tabsSuperioresCompletos = true;
    	
    	// Este "for" hace que los tabs de arriba tengan el foco activado para que los tabs
		// aparezcan en la pantalla siempre en vez de salir escondidos
		for (int i = 0; i < listFragments.size(); i++) {
            tabsSuperiores.getTabWidget().getChildAt(i).setFocusableInTouchMode(true);
        }
    
    	// Determinamos el ancho de cada tab superior (225dp)
    	for(int i=0;i<listFragments.size();i++){
        tabsSuperiores.getTabWidget().getChildAt(i).setLayoutParams(new
               LinearLayout.LayoutParams(225,100));
    	}
    	
    	// Aqui creamos el viewPager y el pagerAdapter para el correcto slide entre pestañas
    	miPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), listFragments);
        miViewPager = (ViewPager) super.findViewById(R.id.viewPagerTabsSuperiores);
        miViewPager.setAdapter(miPagerAdapter);
        // Indica cuál es el número máximo de páginas que puede haber
        miViewPager.setOffscreenPageLimit(listFragments.size());
        miViewPager.setOnPageChangeListener(this);
        miViewPager.setOnTouchListener(new OnTouchListener() {
			// Método para que estando predominando un fragment que corresponde a un tab
        	// inferior, no sea posible hacer slide del fragment correspondiente a un tab
        	// superior que hay escondido debajo del primer fragment mencionado 
			public boolean onTouch(View v, MotionEvent event) {
				if (usandoTabsInferiores)
					return true;
				else
					return false;
			}
		});
        
        postabSuperiorPulsado = 0;
        
        miViewPager.setVisibility(View.GONE);
        
        for(int i=0;i<listFragments.size();i++){
	        tabsSuperiores.getTabWidget().getChildAt(i).setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					miViewPager.setVisibility(View.VISIBLE);
					usandoTabsInferiores = false;
					cambiarBackgroundTabsSuperiores();
					
					seleccionadoTabSuperior = true;
					// Obtener la pestaña actual
					postabSuperiorPulsado = tabsSuperiores.getCurrentTab(); 
					// Seleccionar la página en el ViewPager.
			        miViewPager.setCurrentItem(postabSuperiorPulsado);
					
			        // Obtener la pestaña actual
					int pos = tabsSuperiores.getCurrentTab();
					
					//miPagerAdapter.notifyDataSetChanged();
			        if (pos == listFragments.size()-1 && listFragments.get(listFragments.size()-1).getView() != null)
			        	((ContenidoTabSuperiorCategoriaBebidas) listFragments.get(listFragments.size()-1)).actualizar();
			        					
					Fragment f = new Fragment();
			        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			        ft.replace(R.id.FrameLayoutPestanas, f);
			        ft.commit();
			        			        
			        // Ponemos el título a la actividad
			        // Recogemos ActionBar
			        ActionBar actionbar = getActionBar();
			    	actionbar.setTitle(" CONFIGURE SU MENÚ...");
			        
			        tabs.getTabWidget().getChildAt(InicializarRestaurante.getTabInferiorSeleccionado()).setBackgroundColor(Color.parseColor("#c38838"));
				}
			});
        }
    	
    	// Hacemos oyente al tabhost
		tabsSuperiores.setOnTabChangedListener(new OnTabChangeListener() {
			
			public void onTabChanged(String tabId) {
				
				miViewPager.setVisibility(View.VISIBLE);
				usandoTabsInferiores = false;
				cambiarBackgroundTabsSuperiores();

				seleccionadoTabSuperior = true;
				// Obtener la pestaña actual
				postabSuperiorPulsado = tabsSuperiores.getCurrentTab(); 
				// Seleccionar la página en el ViewPager.
		        miViewPager.setCurrentItem(postabSuperiorPulsado);
		        
		        // Obtener la pestaña actual
				int pos = tabsSuperiores.getCurrentTab();
		        
		        if (pos == 0 && listFragments.get(0).getView() != null)
		        	((ContenidoTabsSuperioresFragment) listFragments.get(0)).actualizar();
		        else if (pos == listFragments.size()-1 && listFragments.get(listFragments.size()-1).getView() != null)
		        	((ContenidoTabSuperiorCategoriaBebidas) listFragments.get(listFragments.size()-1)).actualizar();
		        
		      	tabsSuperiores.setCurrentTabByTag(tabId);
		        Fragment f = new Fragment();
		        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		        ft.replace(R.id.FrameLayoutPestanas, f);
		        ft.commit();
		        
				// Ponemos el título a la actividad
		        // Recogemos ActionBar
		        ActionBar actionbar = getActionBar();
		    	actionbar.setTitle(" CONFIGURE SU MENÚ...");
		  
		        tabs.getTabWidget().getChildAt(InicializarRestaurante.getTabInferiorSeleccionado()).setBackgroundColor(Color.parseColor("#c38838"));
			}
		});
    	
		
    	// Ponemos el título a la actividad
    	actionbar.setTitle(" INICIO");
	}
	
	// Método que indica si está el tabFalsoSup creado en los tabs superiores
	public boolean existeTabFalsoSup() {
		if (tabsSuperiores != null && tabsSuperioresCompletos){
			if (listNombresTabsSuperiores.get(listNombresTabsSuperiores.size()-1).equals("tabFalsoSup"))
				return true;
			else
				return false;
		}else
			return false;
	}
	
	public void cambiarBackgroundTabsSuperiores()
	{
		if(tabsSuperiores != null && listFragments != null){
			for (int i=0;i<listFragments.size();i++)
			{
				tabsSuperiores.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_sup_no_seleccionado);
			}
			if (!usandoTabsInferiores)
				tabsSuperiores.getTabWidget().getChildAt(tabsSuperiores.getCurrentTab()).setBackgroundResource(R.drawable.tab_sup_seleccionado);
		}
	}
    
	 // Metodo encargado de preparar las vistas de cada tab inferior
    private View prepararTabViewSuperior(Context context, String nombreTab){
    	// Cargamos el layout
    	tabSuperiorContentView = LayoutInflater.from(context).inflate(R.layout.tabs_superiores, null);
    	// Cargamos el layout que va a contener la imagen de fondo del tab
    	LinearLayout linLayout = (LinearLayout) tabSuperiorContentView.findViewById(R.id.linearLayoutContenidoTabsSuperiores);
    	linLayout.setBackgroundResource(R.drawable.tab_sup_no_seleccionado);
    	// Cargamos el titulo del tab
    	TextView textoTab = (TextView)tabSuperiorContentView.findViewById(R.id.textViewTabSuperior);
    	textoTab.setTextColor(Color.WHITE);
    	// Asignamos el título e icono para cada tab
    	textoTab.setText(nombreTab);
    	return tabSuperiorContentView;
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
        spec.setIndicator(prepararTabViewInferior(getApplicationContext(),"tabInicio"));
        // Lo añadimos
        tabs.addTab(spec);
        
        // Creamos el tab2 --> Pedido a sincronizar
        spec = tabs.newTabSpec("tabPedidoSincronizar");
        spec.setContent(R.id.tab2);
        spec.setIndicator(prepararTabViewInferior(getApplicationContext(),"tabPedidoSincronizar"));
        tabs.addTab(spec);
        
        // Creamos el tab3 --> Cuenta
        spec = tabs.newTabSpec("tabCuenta");
        spec.setContent(R.id.tab3);
        spec.setIndicator(prepararTabViewInferior(getApplicationContext(),"tabCuenta"));
        tabs.addTab(spec);
        
        // Creamos el tab4 --> Calculadora
        spec = tabs.newTabSpec("tabCalculadora");
        spec.setContent(R.id.tab4);
        spec.setIndicator(prepararTabViewInferior(getApplicationContext(),"tabCalculadora"));
        tabs.addTab(spec);
        
        /*
         * Creamos un tab falso para que cada vez que pulsemos en cualquiera de los tabs
         * inferiores, redirigimos como tab pulsado al falso, de esta forma conseguimos
         * que cada vez que pulsemos en cada tab inferior entre en el método onchanged.
         */
        // Creamos el tab5 --> tabFalso
        spec = tabs.newTabSpec("tabFalso");
        spec.setContent(R.id.tab5);
        spec.setIndicator(prepararTabViewInferior(getApplicationContext(),"tabFalso"));
        tabs.addTab(spec);
        
        // Seleccionamos momentaneamente el tab falso para ocultarlo
        tabs.setCurrentTabByTag("tabFalso");
        // Lo ocultamos, consiguiendo que esté pero no lo veamos, justo lo que queremos
        tabs.getCurrentTabView().setVisibility(View.GONE);
        
        // para ayuda
        anteriorTabPulsado = "tabInicio";
    }
    
    // Metodo encargado de preparar las vistas de cada tab inferior
    private View prepararTabViewInferior(Context context, String nombreTab){
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
			usandoTabsInferiores = true;
			cambiarBackgroundTabsSuperiores();
			if (miViewPager != null)
				miViewPager.setVisibility(View.GONE);
			/*
			 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
			 * ya no lo esté.
			 */
		    tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#c38838"));
		    /*
		     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
		     * usuario vea cual está seleccionado.
		     */
		    tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#906d35"));
		    tabInferiorSeleccionado = 0;
		    
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;  
            //anterior tab pulsado
            anteriorTabPulsado = "tabInicio";

			// Cargamos en el fragment la pantalla de bienvenida del restaurante
			fragmentPantallaInicioRes = new PantallaInicialRestaurante();
			((PantallaInicialRestaurante)fragmentPantallaInicioRes).setRestaurante(restaurante);
	        FragmentTransaction m = getSupportFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPantallaInicioRes);
	        m.commit();
	        
	        Fragment f = new Fragment();
	        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        ft.replace(R.id.linearLayoutContenidoTabsSuperiores, f);
	        ft.commit();
		}else if(tabId.equals("tabPedidoSincronizar")){
			usandoTabsInferiores = true;
			cambiarBackgroundTabsSuperiores();
			if (miViewPager != null)
				miViewPager.setVisibility(View.GONE);
			/*
			 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
			 * ya no lo esté.
			 */
		    tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#c38838"));
		    /*
		     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
		     * usuario vea cual está seleccionado.
		     */
		    tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#906d35"));
		    tabInferiorSeleccionado = 1;
		    
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;

            //anterior tab pulsado
            anteriorTabPulsado = "tabPedidoSincronizar";
            
			fragmentPedido = new PedidoFragment();
			PedidoFragment.setRestaurante(restaurante);
	        FragmentTransaction m = getSupportFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentPedido);
	        m.addToBackStack("Pedido");
	        m.commit();
		}else if(tabId.equals("tabCuenta")){
			usandoTabsInferiores = true;
			cambiarBackgroundTabsSuperiores();
			if (miViewPager != null)
				miViewPager.setVisibility(View.GONE);
			/*
			 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
			 * ya no lo esté.
			 */
		    tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#c38838"));
		    /*
		     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
		     * usuario vea cual está seleccionado.
		     */
		    tabs.getTabWidget().getChildAt(tabs.getCurrentTab()).setBackgroundColor(Color.parseColor("#906d35"));
		    tabInferiorSeleccionado = 2;
		    
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
            // Marcamos a falso selccionado tabSuperior
            seleccionadoTabSuperior = false;
            
            //anterior tab pulsado
            anteriorTabPulsado = "tabCuenta";
            
			fragmentCuenta = new CuentaFragment();
			((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
	        FragmentTransaction m = getSupportFragmentManager().beginTransaction();
	        m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
	        m.addToBackStack("Cuenta");
	        m.commit();
		}else if(tabId.equals("tabCalculadora")){			
			// Marcamos el tab falso
            tabs.setCurrentTabByTag("tabFalso");
			// Vemos si se ha sincronizado algún pedido para poder utilizar la calculadora
			if(hayAlgunPedidoSincronizado()){
				/*
				 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
				 * ya no lo esté.
				 */
			    tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#c38838"));
			    /*
			     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
			     * usuario vea cual está seleccionado.
			     */
			    tabs.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#906d35"));
				lanzarVentanaEmergenteParaIndicarNumeroComensales();
			}else{
				lanzarVentanaEmergenteAvisoSeNecesitaMinimoUnPedido();
			}
		}
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
        
        ventanaEmergente.setNegativeButton("Cancelar", new  DialogInterface.OnClickListener() { // si le das al aceptar
          	public void onClick(DialogInterface dialog, int whichButton) {
          		/*
				 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
				 * ya no lo esté.
				 */
			    tabs.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#c38838"));
			    /*
			     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
			     * usuario vea cual está seleccionado.
			     */
			    if(!seleccionadoTabSuperior){
			    	tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#906d35"));
			    }
          	}
        });
        // Si selecciona sobre aceptar, lanzamos la pantalla calculadora
        ventanaEmergente.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
          	public void onClick(DialogInterface dialog, int whichButton) {
          		if(numComensales > 0){
    				
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
        
        // por si le da a atras
        ventanaEmergente.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					/* Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
					 * ya no lo esté.
					 */
				    tabs.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#c38838"));
				    /* Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
				     * usuario vea cual está seleccionado.
				     */
				    if(!seleccionadoTabSuperior){
				    	tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#906d35"));
				    }
				}
				return false;
			}
        });
        
        // por si toca fuera de la pantalla
        ventanaEmergente.setOnDismissListener(new OnDismissListener() {
			
			public void onDismiss(DialogInterface dialog) {
				/* Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
				 * ya no lo esté.  */
				tabs.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#c38838"));
				/* Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
				* usuario vea cual está seleccionado. */
				if(!seleccionadoTabSuperior){
				 	tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#906d35"));
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
		if(data != null){
		    String origen = data.getExtras().getString("Origen");
		    if(origen != null){
		    	if (origen.equals("Favoritos")){
			        if (tabsSuperiores.getCurrentTab() == 0 && listFragments.get(0).getView() != null){
			        	((ContenidoTabsSuperioresFragment) listFragments.get(0)).onActivityResult(requestCode, resultCode, data);
			        }
				}else if (origen.equals("Pedido")){
					((PedidoFragment)fragmentPedido).onActivityResult(requestCode, resultCode, data);
				}else if(origen.equals("Cuenta")){
					((CuentaFragment)fragmentCuenta).onActivityResult(requestCode, resultCode, data);
				}else if (origen.equals("Calculadora")) {
					tabs.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#c38838"));
					tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#906d35"));
				}
		    }
		}
	}
	
	// Metodo encargado de decirnos si se ha sincronizado algún pedido o no
	public boolean hayAlgunPedidoSincronizado(){
		try{
			// Importamos la base de datos de cuenta
			HandlerDB sqlCuenta = new HandlerDB(getApplicationContext(),"Cuenta.db"); 
			SQLiteDatabase dbCuenta = sqlCuenta.open();
			String[] camposCuenta = new String[]{"Id"};
	    	String[] datos = new String[]{restaurante};
			Cursor cursorCuenta = dbCuenta.query("Cuenta", camposCuenta, "Restaurante = ?", datos, null, null, null);
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
	
	public static void cargarTabCuenta(){
		/*
		 * Cambiamos el fondo del tab inferior que estuviese seleccionado para que ahora 
		 * ya no lo esté.
		 */
	    tabs.getTabWidget().getChildAt(tabInferiorSeleccionado).setBackgroundColor(Color.parseColor("#c38838"));
	    /*
	     * Cambiamos el fondo del tab inferior que acabamos de selccionar para que el 
	     * usuario vea cual está seleccionado.
	     */
	    tabs.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#906d35"));
	    tabInferiorSeleccionado = 2;
	    
		// Marcamos el tab falso
        tabs.setCurrentTabByTag("tabFalso");
        // Marcamos a falso selccionado tabSuperior
        seleccionadoTabSuperior = false;
        //anterior tab pulsado
        anteriorTabPulsado = "tabCuenta";
	}
	
	public void bloquearSlideTabsSuperiores()
	{
		if(!seleccionadoTabSuperior)
		{
			miViewPager.setEnabled(true);
		}
	}
	
	/*public static void setSeleccionadoTabSuperior(boolean seleccionado){
		seleccionadoTabSuperior = seleccionado;
	}*/
	
	public static void setPosTabSuperior(int posTab){
		postabSuperiorPulsado = posTab;
	}
	
	public static int getTabInferiorSeleccionado(){
		return tabInferiorSeleccionado;
	}

	
	public void lanzarVentanaEmergenteTwitter(){
		
        View vistaTwiter = LayoutInflater.from(InicializarRestaurante.this).inflate(R.layout.twiter, null);
		campoTweet = (TextView) vistaTwiter.findViewById(R.id.textViewTweet);
		botonEnviarTweet = (ImageView) vistaTwiter.findViewById(R.id.botonAnadirTweet);

		botonEnviarTweet.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				enviaTweet();
				ventanaEmergenteTwitter.dismiss();
			}
		});
		
		ventanaEmergenteTwitter = new AlertDialog.Builder(InicializarRestaurante.this).create();
        ventanaEmergenteTwitter.setView(vistaTwiter);
        ventanaEmergenteTwitter.show();
	}
	
	private void autorizaTwitter() {
		try {
			StrictMode.enableDefaults();
			httpOauthConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			httpOauthprovider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACK_URL);
	    	
			WebView webView = new WebView(this){
				@Override
			    public boolean onCheckIsTextEditor()
			    {
			        return true;
			    }

			    @Override
			    public boolean onTouchEvent(MotionEvent ev)
			    {
			        switch (ev.getAction())
			        {
			            case MotionEvent.ACTION_DOWN:
			            case MotionEvent.ACTION_UP:
			                if (!hasFocus())
			                    requestFocus();
			            break;
			        }

			        return super.onTouchEvent(ev);
			    }
			};
			webView.getSettings().setJavaScriptEnabled(true);
					    
			webView.setWebViewClient(new WebViewClient() {  
	        	
	        	@Override  
	            public void onPageFinished(WebView view, String url)  {  
	        		
	        		Uri uri = Uri.parse(url);
	        		
	        		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
	        			
	        			view.setVisibility(View.INVISIBLE);
	        			 
	        	        String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
	        	 
	        	        try {
	        	            // this will populate token and token_secret in consumer
	        	 
	        	            httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
	        	            String userKey = httpOauthConsumer.getToken();
	        	            String userSecret = httpOauthConsumer.getTokenSecret();
	        	            accessToken = new AccessToken(userKey, userSecret);
	        	 
	        	            // Save user_key and user_secret in user preferences and return
	        	            //SharedPreferences settings = getBaseContext().getSharedPreferences("twiter", 0);
	        	            //SharedPreferences.Editor editor = settings.edit();
	        	            //editor.putString("user_key", userKey);
	        	            //editor.putString("user_secret", userSecret);
	        	            //editor.commit();
	        	            
	        	            ventanaEmergenteAutentificarTwitter.dismiss();
	        	            lanzarVentanaEmergenteTwitter();
	        	            	        	 
	        	        } catch (Exception e) {
	        	 
	        	        }	  		      
	        		}
	        	}	
	        });
			webView.loadUrl(authUrl);
			
			ventanaEmergenteAutentificarTwitter = new AlertDialog.Builder(InicializarRestaurante.this).create();
			ventanaEmergenteAutentificarTwitter.setView(webView);
			ventanaEmergenteAutentificarTwitter.show();
			
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void enviaTweet() {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);
			twitter.updateStatus(campoTweet.getText().toString());

			Toast.makeText(getApplicationContext(), "¡Tweet enviado!", Toast.LENGTH_SHORT).show();
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}


	public void onPageScrollStateChanged(int arg0) {
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	public void onPageSelected(int pos) {
		tabsSuperiores.setCurrentTab(pos);
	}

}