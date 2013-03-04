package usuario;

import java.util.ArrayList;


import com.example.nfcook.R;

import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.Handler;

public class DescripcionPlato extends Activity {
	
	private static int identificadorUnicoHijoPedido;
	 
	private int cantidad;
	double precioPlato;
	private String restaurante, nombrePlato, idPlato;
	private AutoCompleteTextView actwObservaciones;
	private TextView textViewPrecio;
	private EditText editTextUnidades;
	private static ExpandableListView expandableListExtras;
	private static MiExpandableListAdapterEditar adapterExpandableListExtras;
	
	public Handler sql,sqlPedido;
	public SQLiteDatabase db,dbPedido;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.descripcion_del_plato);
        
        editTextUnidades = (EditText) findViewById(R.id.editTextunidades);
        textViewPrecio= (TextView) findViewById(R.id.textViewPrecio);
        TextView textViewNombre= (TextView) findViewById(R.id.nombrePlato);
        TextView textViewDescripcion= (TextView) findViewById(R.id.descripcionPlato);
        ImageView imageViewPlato = (ImageView) findViewById(R.id.imagenPlato);
        actwObservaciones = (AutoCompleteTextView)findViewById(R.id.AutoCompleteTextViewOpciones);
        
        Button botonConfirmar = (Button) findViewById(R.id.botonOpcion);
        Button botonEditar = (Button) findViewById(R.id.botonOpcionEditar);
        botonConfirmar.setVisibility(Button.VISIBLE);
        botonEditar.setVisibility(Button.INVISIBLE);
     
        // Importamos la base de datos para su posterior lectura
        try{
        	sql=new Handler(getApplicationContext()); 
         	db=sql.open();
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        } 
     // Obtenemos el nombre del plato y el restaurante de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        nombrePlato = bundle.getString("nombrePlato");
        restaurante =bundle.getString("nombreRestaurante");
        // Hacemos una consulta en la base de datos sobre el plato seleccionado   
        String[] campo1=new String[]{"Extras","Precio","Foto","Descripcion","Id"};
        String[] datos = new String[]{restaurante, nombrePlato};
        Cursor cursor =db.query("Restaurantes",campo1,"Restaurante =? AND Nombre=?",datos,null,null,null);  
  
        expandableListExtras = (ExpandableListView) findViewById(R.id.expandableExtras);
        
        //String[] campo1=new String[]{"Extras","Precio","Foto","Descripcion","Id"};
        
        cursor.moveToFirst();
        String extrasBusqueda = cursor.getString(0);
        precioPlato = cursor.getDouble(1);
        String imagePlato = cursor.getString(2);
        String descripcionPlato = cursor.getString(3);
        idPlato = cursor.getString(4);
        
        if (!extrasBusqueda.equals("")){
            String[] tokens = extrasBusqueda.split("/");
	        // Creamos los padres de la lista, serán las distintas categorías de extras
	        ArrayList<PadreExpandableListEditar> categoriasExtras =  new ArrayList<PadreExpandableListEditar>();
	        for(int i= 0; i< tokens.length ;i++)
	        {	
	        	String[] nombreExtra;
				try{
					nombreExtra = tokens[i].split(":");
					
					String categoriaExtraPadre = nombreExtra[0];
					
					// Creamos los hijos, serán la variedad de extras
					String[] elementosExtra = null;

					elementosExtra = nombreExtra[1].split(",");
					
					ArrayList<HijoExpandableListEditar> variedadExtrasListaHijos = new ArrayList<HijoExpandableListEditar>();
					ArrayList<String> extrasHijo = new ArrayList<String>();
					boolean[] extrasMarcados = new boolean[elementosExtra.length];
					for(int j=0; j<elementosExtra.length;j++)
					{
						extrasMarcados[j] = false;
						extrasHijo.add(elementosExtra[j]);
					}
					HijoExpandableListEditar extrasDeUnaCategoria = new HijoExpandableListEditar(extrasHijo, extrasMarcados);
					// Añadimos la información del hijo a la lista de hijos
					variedadExtrasListaHijos.add(extrasDeUnaCategoria);
					PadreExpandableListEditar padreCategoriaExtra = new PadreExpandableListEditar(idPlato,categoriaExtraPadre, variedadExtrasListaHijos);
					// Añadimos la información del padre a la lista de padres
					categoriasExtras.add(padreCategoriaExtra);
				}catch(Exception e){
					Toast.makeText(getApplicationContext(),"Error en el formato de extra en la BD", Toast.LENGTH_SHORT).show();
				}
			}
	        // Creamos el adapater para adaptar la lista a la pantalla
	        adapterExpandableListExtras = new MiExpandableListAdapterEditar(this, categoriasExtras,false);
	        expandableListExtras.setAdapter(adapterExpandableListExtras);
        }else{
        	//Actualizamos el adapter a null, ya que es static, para saber que este plato no tiene extras.
	        adapterExpandableListExtras = null;
        	expandableListExtras.setVisibility(ExpandableListView.INVISIBLE);
        }
        		

        // Cargamos la imagen del plato
        imageViewPlato.setImageResource(getResources().getIdentifier(imagePlato,"drawable",this.getPackageName()));	
        
        // Damos el texto a los textviews
        editTextUnidades.setText("1");
        cantidad = 1;
        editTextUnidades.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			//Se invoca cada vez que pinchamos sobre el o salimos de el
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){ // salimos de el
					cantidad = Integer.parseInt(editTextUnidades.getText().toString());
					textViewPrecio.setText(cantidad*precioPlato + " €");
				}
				
				
			}
		});
        textViewPrecio.setText(precioPlato +" €");
        textViewNombre.setText(nombrePlato);
        textViewDescripcion.setText(descripcionPlato);
        
	}
	
	public void onClickBotonMenos(View v){
		if(cantidad != 1){
			cantidad--;
			editTextUnidades.setText(cantidad + "");
			textViewPrecio.setText(cantidad*precioPlato + " €");
		}
		
	}
	
	public void onClickBotonMas(View v){
		cantidad++;
		editTextUnidades.setText(cantidad + "");
		textViewPrecio.setText(cantidad*precioPlato + " €");
	}
	 
    public void onClickConfirmar(View boton){
    	boolean bienEditado = true;
    	String observaciones = null;
    	String nuevosExtrasMarcados = null;
    	if(!actwObservaciones.getText().toString().equals("")){
        	observaciones = actwObservaciones.getText().toString();
    	}
    	if(adapterExpandableListExtras!=null){ //Es un plato con extras
    		nuevosExtrasMarcados = adapterExpandableListExtras.getExtrasMarcados();
    		if(nuevosExtrasMarcados == null){
    			bienEditado = false;
    		}
    	}
    	if(bienEditado){
    		sqlPedido=new Handler(getApplicationContext(),"Pedido.db"); 
         	dbPedido=sqlPedido.open();
        	ContentValues plato = new ContentValues();
        	plato.put("Id", idPlato);
        	plato.put("Plato", nombrePlato);
        	plato.put("Observaciones", observaciones);
        	plato.put("Extras", nuevosExtrasMarcados);
        	plato.put("PrecioPlato",precioPlato);
        	plato.put("IdHijo", identificadorUnicoHijoPedido + "");
        	identificadorUnicoHijoPedido++;
    		dbPedido.insert("Pedido", null, plato);
    		dbPedido.close();
    		Toast.makeText(getApplicationContext(),"Plato Nº " + cantidad + " confirmado", Toast.LENGTH_SHORT).show();
        	if(cantidad == 1){
        		this.finish();
        	}else{
        		cantidad--;
        	}
    	}else{
    		adapterExpandableListExtras.expandeTodosLosPadres();
    		Toast.makeText(getApplicationContext(),"Termine de configurar su plato antes", Toast.LENGTH_SHORT).show();
    	}
    }

    public static void actualizaExpandableList() {
		expandableListExtras.setAdapter(adapterExpandableListExtras);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListExtras.expandGroup(groupPositionMarcar);
	}
    
    
}
 