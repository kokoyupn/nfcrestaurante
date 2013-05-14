package usuario;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.nfcook.R;

import adapters.ServiciosRestauranteAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DescripcionRestaurante extends Activity {
	private Restaurante restaurante;
	private ArrayList<Restaurante> restaurantes;
	private String nombreRestaurante;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descripcion_del_restaurante);
		
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" INFORMACIÓN RESTAURANTE");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		nombreRestaurante = bundle.getString("nombreRestaurante");
		
		restaurantes = Mapas.getRestaurantes();
		boolean enc=false;
		Iterator<Restaurante> it = restaurantes.iterator();
	    while(!enc && it.hasNext())
	    {
	    	Restaurante restAct = it.next();
	    	if(nombreRestaurante.equals(restAct.getNombre()))
	    	{
	    		enc=true;
	    		restaurante = restAct;
	    	}
	    }
	    
	    /*
	     * FIXME
	     */
	    ImageView logoRestaurante = (ImageView) findViewById(R.id.imageViewLogoDescRest);
	    String ruta;
	    if(nombreRestaurante.contains("Foster")){
	    	 ruta = "foster_mapa";
	    }else{
	    	ruta = "vips_mapa";
	    }
	    logoRestaurante.setImageResource(getResources().getIdentifier(ruta,"drawable",getPackageName()));
	    
	    TextView tituloRest = (TextView) findViewById(R.id.textViewNombreRest);
	    tituloRest.setText(restaurante.getNombre());
	    TextView direccion = (TextView) findViewById(R.id.textViewDireccionRest);
	    direccion.setText(restaurante.getDireccion());
	    
	    TextView telf = (TextView) findViewById(R.id.textViewTelefonoRest);
	    telf.setTextColor(Color.rgb(065, 105, 225));
	    telf.setText(restaurante.getTelefono());	
	    SpannableString telfSubrayado = new SpannableString(telf.getText());
		telfSubrayado.setSpan(new UnderlineSpan(), 0, telfSubrayado.length(), 0);
		telf.setText(telfSubrayado);

	    TextView horario = (TextView) findViewById(R.id.textViewHorarioRest);
	    horario.setText(restaurante.getHorario());
	
	    TextView url = (TextView) findViewById(R.id.textViewURLRest);
	    url.setTextColor(Color.rgb(065, 105, 225));
	    url.setText(restaurante.getURL());	
	    SpannableString urlSubrayado = new SpannableString(url.getText());
	    urlSubrayado.setSpan(new UnderlineSpan(), 0, urlSubrayado.length(), 0);
	    url.setText(urlSubrayado);
	    
	    GridView gridViewServ = (GridView) findViewById(R.id.gridViewServiciosRest);
	    ArrayList<String> servicios = new ArrayList<String>();
	    if(restaurante.isTakeAway())
	    	servicios.add("foster_take_away");
	    if(restaurante.isDelivery())
	    	servicios.add("foster_a_domicilio");
	    if(restaurante.isMenuMediodia())
	    	servicios.add("foster_menu_mediodia");
	    if(restaurante.isMagia())
	    	servicios.add("foster_magia");
	    //if(restaurante.isCumpleanios())
	    //	servicios.add("foster_cumpleanios");
	    gridViewServ.setAdapter(new ServiciosRestauranteAdapter(getApplicationContext(), servicios));
	}
	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }
	
}
