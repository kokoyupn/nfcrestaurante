package usuario;

import java.util.ArrayList;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import adapters.InfomacionPlatoPantallaReparto;
import adapters.MiGridViewCalculadoraAdapter;
import adapters.MiViewPagerAdapter;
import adapters.PadreGridViewCalculadora;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase encargada de implementar la funcionalidad de la calculadora.
 * 
 * La calculadora se divide principalmente en las siguientes partes:
 * 
 * 1- Sección de promociones vigentes, por si los comensales disponen de alguna promoción
 * para que puedan calcular el precio definitivo aplicando la promoción.
 * 
 * 2- GridView con información de los comensales. Nos aparecerá su nombre, el total que
 * va a tener que pagar y una lista resumen de los platos que ha consumido cada uno con
 * la porción correspondiente de los mismos.
 * 
 * 3- Lista de imágenes con los platos que han consumido durante la estancia. El usuario
 * podrá ir viendo los platos que ha consumido simplemente deslizando el dedo sobre la 
 * imágen central y si hace click sobre la misma saldrá una ventana emergente para que 
 * diga quien ha consumido dicho plato y así este se le sume a cada comensal en la parte
 * que le toca. En la ventana emergente saldrá la imágen del plato para que el usuario 
 * pueda identificar de forma sencilla de que plato se trata y el precio que se va a pagar
 * por el mismo. * 
 * 
 * @author Abel
 *
 */
public class Calculadora extends Activity{
	private static GridView gridViewPersonas;
	private static MiGridViewCalculadoraAdapter adapterGridViewCalculadora;
    private static ArrayList<PadreGridViewCalculadora> personas;
    public static ArrayList<Boolean> nombresPersona;
    private String restaurante;
    
    private ArrayList<InfomacionPlatoPantallaReparto> platos;
    
    private static int numPersonas;
        
	public void onCreate(Bundle savedInstanceState) {   
       
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculadora);
        
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
        // Ponemos el título a la actividad
    	actionbar.setTitle(" CALCULADORA");
    	// Cambiamos el fondo al ActionBar
    	actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B45F04")));
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
        
        // Recogemos el número de comensales que vendrá de la ventana emergente anterior
        Bundle bundle = getIntent().getExtras();
		numPersonas = bundle.getInt("numeroComensales");
		restaurante = bundle.getString("restaurante");
        
        // creamos el arraylist de las imagenes
    	platos = new ArrayList<InfomacionPlatoPantallaReparto>();
    	
    	/*****************************CARGAMOS EL SPINNER******************************/
    	Spinner spinnerPromociones = (Spinner) findViewById(R.id.spinnerPromociones);
    	ArrayList<String> promociones = new ArrayList<String>();
    	/*
    	 * FIXME Actualmente no vamos a poner promociones, simplemente será para que 
    	 * en un futuro se lean de la bd y se añadan aquí.
    	 * 
    	 * Por otro lado el Adapter no está redefinido, utilizamos el básico que para
    	 * lo que vamos a hacer ahora nos vale.
    	 */
    	// Metemos las promociones que haya en el restaurante
    	promociones.add("Seleccione promoción...");
    	
    	// Como ahora no hay promociones lo deshabilitamos
    	/*
    	 * FIXME El día que haya promociones hay que habilitarlo
    	 */
    	spinnerPromociones.setClickable(false);
    	    	
    	// Creamos el adapter por defecto y se lo aplicamos
    	ArrayAdapter<String> adapterSpinnerPromociones = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, promociones);
    	adapterSpinnerPromociones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerPromociones.setAdapter(adapterSpinnerPromociones);
    	
    	/********************************AÑADIR PERSONA********************************/
    	ImageView imageViewAnyadirPersona = (ImageView) findViewById(R.id.imageViewAnyadirComensalCalculadora);
    	// Implementamos el oyente
    	imageViewAnyadirPersona.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Creamos y añadimos la persona
				int huecoLibre = buscaPrimeraPosPersonaLibre();
				PadreGridViewCalculadora persona = new PadreGridViewCalculadora(buscaPrimeraPosPersonaLibre(), huecoLibre - 1);
        		personas.add(persona);
				if(huecoLibre == nombresPersona.size() + 1){
		        	nombresPersona.add(true);
	        	}else{
	        		nombresPersona.set(huecoLibre-1, true);
	        	}
	        	// Aumentamos el numero de personas
				numPersonas++;
	        	// Aplicamos el adapter para que aparezca la persona
	        	actualizaGridViewPersonas();
	        	// Actualizamos el adapter del viewpager
	        	MiViewPagerAdapter.nuevaPersonaAnyadida();
		        Toast.makeText(getApplicationContext(),"Nuevo comensal creado con éxito.",Toast.LENGTH_SHORT).show();
			}
		});

        /********************************CARGAR PERSONA********************************/
        // Creamos la lista personas
        personas = new ArrayList<PadreGridViewCalculadora>();
        nombresPersona = new ArrayList<Boolean>();
        gridViewPersonas = (GridView) findViewById(R.id.gridViewCalculadora);
        
        // Creamos las personas
        PadreGridViewCalculadora persona;
        for(int i=0; i<numPersonas; i++){
        	persona = new PadreGridViewCalculadora(i+1, i);
        	nombresPersona.add(true);
        	personas.add(persona);
        }
        
		// Creamos el adapater del gridView para que se muestren las personas
        adapterGridViewCalculadora = new MiGridViewCalculadoraAdapter(this, personas, nombresPersona);
        gridViewPersonas.setAdapter(adapterGridViewCalculadora);
        
        /**************************INFORMACION DE LOS PLATOS******************************/
        
        // Sacamos la imformación de los platos que haya en cuenta.db
        try{
			// Abrimos la base de datos de cuenta
        	HandlerDB sqlCuenta = new HandlerDB(this.getApplicationContext(),"Cuenta.db"); 
        	SQLiteDatabase dbCuenta = sqlCuenta.open();
        	
        	// Sacamos el id de todos los platos de cuenta
        	String[] camposSacar = new String[]{"Id","Restaurante", "IdHijo"};
	    	String[] datosQueCondicionan = new String[]{restaurante};
    		Cursor cP = dbCuenta.query("Cuenta", camposSacar, "Restaurante = ?", datosQueCondicionan, null, null,null);
	    	
	    	// Importamos la base de datos de los platos
        	HandlerDB sqlPlatos = new HandlerDB(this.getApplicationContext()); 
        	SQLiteDatabase dbPlatos = sqlPlatos.open();
        	
    	    // Recorremos todos los platos que hubiera en cuenta
    	    while(cP.moveToNext()){
        		// Sacamos la información de ese plato para luego mostrarla (foto, nombre...)
            	String[] camposSacarPlato = new String[]{"Foto", "Nombre", "Precio"};
    	    	String[] datosQueCondicionanPlato = new String[]{cP.getString(1),cP.getString(0)};
        		Cursor cPlato = dbPlatos.query("Restaurantes", camposSacarPlato, "Restaurante=? AND Id=?",datosQueCondicionanPlato,null, null,null);

        		InfomacionPlatoPantallaReparto infoPlato;
        		int imagenPlato;
        		while(cPlato.moveToNext()){
        			imagenPlato = getResources().getIdentifier(cPlato.getString(0),"drawable",this.getPackageName());
        			infoPlato = new InfomacionPlatoPantallaReparto(cP.getString(2), cPlato.getString(1),imagenPlato,cPlato.getDouble(2));
        			platos.add(infoPlato);
        		}
    	    }
	    		    	
	    	// Cerramos las bases de datos de cuenta y platos
        	sqlCuenta.close();
        	sqlPlatos.close();
        	
        	// Damos valor a las imágenes que acompañan a la imágen deslizable
        	// Traemos las imágenes del layout que mostrarán el plato anterior y el siguiente
  			ImageView imgIzq = (ImageView)findViewById(R.id.imageViewIzquierdaCalculadora);
			ImageView imgDer = (ImageView)findViewById(R.id.imageViewDerechaCalaculadora);

  			int numPlatos = platos.size();
  			// Si solo hay un plato ocultamos las dos imágenes de los lados
  			if(numPlatos <= 1){
  				imgIzq.setVisibility(ImageView.INVISIBLE);
  				imgDer.setVisibility(ImageView.INVISIBLE);
  			}else if(numPlatos>1){
  				// Ocultamos la imágen de la izquierda hasta que corra una
  				imgIzq.setVisibility(ImageView.INVISIBLE);
  				// Desolcultamos la imágen de la derecha y le damos valor
  				imgDer.setVisibility(ImageView.VISIBLE);
  				imgDer.setImageResource(platos.get(1).getFotoPlato());
  			}
        	
        	// Creamos la imagen que se desliza y va pasando y aplicamos su adapter (ViewPager)
        	MiViewPagerAdapter miImagenDeslizanteAdapter = new MiViewPagerAdapter(this, platos, personas.size());
      	  	ViewPager imagenDeslizante = (ViewPager) findViewById(R.id.imagenDeslizanteCalculadora);
	      	imagenDeslizante.setAdapter(miImagenDeslizanteAdapter);
	      	imagenDeslizante.setCurrentItem(0);
	      	
	      	// Creamos su oyente
	      	imagenDeslizante.setOnPageChangeListener(new OnPageChangeListener(){
	      		
      	  		public void onPageScrollStateChanged(int arg0) {	      				
	      		}
	
	      		public void onPageScrolled(int arg0, float arg1, int arg2) {	      				
	      		}
	
	      		/*
	      		 * Metodo encargado de implementar la acción correspondiente cuando
	      		 * deslicemos el dedo sobre la imágen central y se cambie la imágen.
	      		 * (non-Javadoc)
	      		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	      		 */
	      		public void onPageSelected(int arg0) {
	      			// Traemos las imágenes del layout que mostrarán el plato anterior y el siguiente
	      			ImageView imgIzq = (ImageView)findViewById(R.id.imageViewIzquierdaCalculadora);
    				ImageView imgDer = (ImageView)findViewById(R.id.imageViewDerechaCalaculadora);

	      			int numPlatos = platos.size();
      				// Miramos si estamos en la primera imágen
      				if(arg0 == 0){
      					imgIzq.setVisibility(ImageView.INVISIBLE);
      					imgDer.setVisibility(ImageView.VISIBLE);
      					imgDer.setImageResource(platos.get(arg0+1).getFotoPlato());
      				// Si nos encontramos en la última imágen ocultamos la derecha
      				}else if(arg0 == numPlatos-1){
      					imgDer.setVisibility(ImageView.INVISIBLE);
      					imgIzq.setVisibility(ImageView.VISIBLE);
      					imgIzq.setImageResource(platos.get(arg0-1).getFotoPlato());
      				/*
      				 * Si no nos encontramos en ninguno de los casos anteriores, le damos
      				 * valor a la imágen izquierda con la anteroir imágen vista y la
      				 * imágen derecha con el plato siguiente que vamos a ver.
      				 */
      				}else{
      					imgIzq.setVisibility(ImageView.VISIBLE);
      					imgDer.setVisibility(ImageView.VISIBLE);
      					imgIzq.setImageResource(platos.get(arg0-1).getFotoPlato());
      					imgDer.setImageResource(platos.get(arg0+1).getFotoPlato());
      				}
      				
      				// Damos valor al campo texto del nombre del plato activo
      	  			TextView textViewNombrePlatoDeslizante = (TextView) findViewById(R.id.textViewNombrePlatoDeslizante);
      	  			textViewNombrePlatoDeslizante.setText(platos.get(arg0).getNombrePlato());
	      		}
	      		 
	      	 });
	      	
	      	// Damos valor al campo texto del nombre del plato activo
	      	if(platos.size() > 0){
	      		TextView textViewNombrePlatoDeslizante = (TextView) findViewById(R.id.textViewNombrePlatoDeslizante);
	      		textViewNombrePlatoDeslizante.setText(platos.get(0).getNombrePlato());
	      	}else{
		         Toast.makeText(getApplicationContext(),"Debe haber realizado algún pedido para poder utilizar esta utilizad.",Toast.LENGTH_SHORT).show();
	      	}
  			
	    }catch(SQLiteException e){
	         Toast.makeText(getApplicationContext(),"ERROR AL ABRIR LA BD DE CUENTA EN LA PANTALLA CALCULADORA",Toast.LENGTH_SHORT).show();
	    }
	
        /******************************** AYUDA ********************************/
        
        ImageView imageViewBotonAyuda = (ImageView) findViewById(R.id.imageViewLogoDescRest);
    	// Implementamos el oyente
    	imageViewBotonAyuda.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageView imageViewInfoAyuda = (ImageView) findViewById(R.id.imageViewInfoAyuda);
			    imageViewInfoAyuda.setVisibility(ImageView.VISIBLE);
			}
		});
    	
    	
    	ImageView imageViewInfoAyuda = (ImageView) findViewById(R.id.imageViewInfoAyuda);
    	// Implementamos el oyente
    	imageViewInfoAyuda.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageView imageViewInfoAyuda = (ImageView) findViewById(R.id.imageViewInfoAyuda);
				imageViewInfoAyuda.setVisibility(ImageView.INVISIBLE);
			}
		});
    	
    	// Vemos si es la primera vez que corremos la aplicación para ayudar al usuario
        if(primeraVezIniciada()){
        	marcarCalculadoraComoInicializada();
        	// Lanzamos la ayuda
			ImageView imageViewAyuda = (ImageView) findViewById(R.id.imageViewLogoDescRest);
			imageViewAyuda.performClick();
        }     
	}
	
	public static void eliminaPersona(int posPersona){
		numPersonas--;	
	}
	
	public static void actualizaGridViewPersonas(){
		// Actualizamos el adapter
		adapterGridViewCalculadora.notifyDataSetChanged();
	}
	
	@Override
    public void onBackPressed() {
		// Si está la ayuda lanzada la quitamos
		ImageView imageViewInfoAyuda = (ImageView) findViewById(R.id.imageViewInfoAyuda);
		if(imageViewInfoAyuda.getVisibility() == 0){
			imageViewInfoAyuda.performClick();
		}else{
	        // Creamos y lanzamos la ventana emergente
			AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(Calculadora.this); 
	        // Creamos su vista, aprovechando un layout existente
	        View vistaVentanaEmergente = LayoutInflater.from(getApplicationContext()).inflate(R.layout.aviso_continuar_pedido, null); 
	        // Sacamos el campo texto informativo y le damos valor
	        TextView textViewInformacion = (TextView) vistaVentanaEmergente.findViewById(R.id.textViewInformacionAviso);
	        textViewInformacion.setText("¿Está seguro que desea cerrar la calculadora?. Se perderá toda la configuración realizada.");
	        ventanaEmergente.setNegativeButton("Cancelar", null);
	        // Si selecciona sobre aceptar, lanzamos la pantalla calculadora
	        ventanaEmergente.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
	          	public void onClick(DialogInterface dialog, int whichButton) {
	          		Intent intent = new Intent();
	                intent.putExtra("Origen", "Calculadora");
	                setResult(RESULT_OK, intent);
	          		finish();
	          	}
	        });
	        // Aplicamos la vista y la mostramos
			ventanaEmergente.setView(vistaVentanaEmergente);
			ventanaEmergente.show();
		}

        return;
    } 
	
	
//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	// Si está la ayuda lanzada la quitamos
    	ImageView imageViewInfoAyuda = (ImageView) findViewById(R.id.imageViewInfoAyuda);
    	if(imageViewInfoAyuda.getVisibility() == 0){
    		imageViewInfoAyuda.performClick();
    	}else{
    		// Creamos y lanzamos la ventana emergente para conocer el nº de comensales
    		AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(Calculadora.this); 
    		// Creamos su vista, aprovechando un layout existente
    		View vistaVentanaEmergente = LayoutInflater.from(getApplicationContext()).inflate(R.layout.aviso_continuar_pedido, null); 
    		// Sacamos el campo texto informativo y le damos valor
    		TextView textViewInformacion = (TextView) vistaVentanaEmergente.findViewById(R.id.textViewInformacionAviso);
    		textViewInformacion.setText("¿Está seguro que desea cerrar la calculadora?. Se perderá toda la configuración realizada.");
    		ventanaEmergente.setNegativeButton("Cancelar", null);
    		// Si selecciona sobre aceptar, lanzamos la pantalla calculadora
    		ventanaEmergente.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
    		       	public void onClick(DialogInterface dialog, int whichButton) {
    		      		finish();
    		      	}
    		});
    		// Aplicamos la vista y la mostramos
    		ventanaEmergente.setView(vistaVentanaEmergente);
    		ventanaEmergente.show();
    	}
    	return false;
    }
	
	
	public boolean primeraVezIniciada(){
		// Vemos si ya ha sido iniciada la aplicacion alguna vez
		SharedPreferences iniciada = getSharedPreferences("Calculadora", 0);
		return iniciada.getInt("Iniciada", -1) == -1;
	}
	
	public void marcarCalculadoraComoInicializada(){
		// Marcamos con 0 que la aplicación ha sido inicializada
		SharedPreferences preferencia = getSharedPreferences("Calculadora", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("Iniciada", 0);
		editor.commit();
	}
	
	public int buscaPrimeraPosPersonaLibre(){
		boolean encontradoHueco = false;
		int i = 0;
		int num = nombresPersona.size();
		while(i<num && !encontradoHueco){
			// Vemos si hay alguna persona en esa posicion
			if(nombresPersona.get(i)){
				i++;
			}else{
				encontradoHueco = true;
			}
		}
		return i + 1;
	}
}
