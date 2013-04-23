package usuario;

import java.util.ArrayList;


import baseDatos.HandlerDB;

import com.example.nfcook.R;

import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private static boolean pulsado;
	private static int ancho, largo;
	public HandlerDB sql,sqlPedido;
	public SQLiteDatabase db,dbPedido;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" SELECCIÓN DE PLATO");
    	//	Variables expandir
    	pulsado=false;
    	
    	setContentView(R.layout.descripcion_del_plato);
               
        cargarUltimoIdentificadorUnicoHijoPedido();

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
        	sql=new HandlerDB(getApplicationContext()); 
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
					if(i==0){//Expandimos el primer padre por estetica
						padreCategoriaExtra.setExpandido(true);
					}
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
					textViewPrecio.setText(Math.rint(cantidad*precioPlato*100)/100 + " €");
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
			textViewPrecio.setText(Math.rint(cantidad*precioPlato*100)/100 + " €");
		}
		
	}
	
	public void onClickBotonMas(View v){
		cantidad++;
		editTextUnidades.setText(cantidad + "");
		textViewPrecio.setText(Math.rint(cantidad*precioPlato*100)/100 + " €");
	}
	 
    public void onClickConfirmar(View boton){
    	boolean bienEditado = true;
    	String observaciones = null;
    	String nuevosExtrasMarcados = null;
    	String extrasBinarios = null;
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
    		if(adapterExpandableListExtras!=null)
    			extrasBinarios = adapterExpandableListExtras.getExtrasBinarios();
    		
    		sqlPedido=new HandlerDB(getApplicationContext(),"Pedido.db"); 
         	dbPedido=sqlPedido.open();
    		while(cantidad>0){
            	ContentValues plato = new ContentValues();
            	plato.put("Restaurante", restaurante);
            	plato.put("Id", idPlato);
            	plato.put("Plato", nombrePlato);
            	plato.put("Observaciones", observaciones);
            	plato.put("Extras", nuevosExtrasMarcados);
            	plato.put("ExtrasBinarios", extrasBinarios);
            	plato.put("PrecioPlato",precioPlato);
            	plato.put("IdHijo", identificadorUnicoHijoPedido + "");
            	identificadorUnicoHijoPedido++;
        		dbPedido.insert("Pedido", null, plato);
        		cantidad--;
    		}
    		dbPedido.close();
    		setUltimoIdentificadorUnicoHijoPedido();
    		Toast.makeText(getApplicationContext(),"Todos sus platos han sido confirmados.", Toast.LENGTH_SHORT).show();
    		this.finish();
    	}else{
    		adapterExpandableListExtras.expandeTodosLosPadres();
    		Toast.makeText(getApplicationContext(),"Termine de configurar su plato antes.", Toast.LENGTH_SHORT).show();
    	}
    }

    public static void actualizaExpandableList() {
    	adapterExpandableListExtras.notifyDataSetChanged();
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListExtras.expandGroup(groupPositionMarcar);
	}
	
	public static int getIdentificadorUnicoHijoPedido(){
		return identificadorUnicoHijoPedido;
	}
    
	public static void sumaIdentificadorUnicoHijoPedido(){
		identificadorUnicoHijoPedido++;
	}
	
	private void cargarUltimoIdentificadorUnicoHijoPedido() {
		//SharedPreferences nos permite recuperar datos aunque la aplicacion se haya cerrado
      	SharedPreferences ultimoId = getSharedPreferences("Identificador_Unico", 0);
      	identificadorUnicoHijoPedido = ultimoId.getInt("identificadorUnicoHijoPedido", 0); // 0 es lo que devuelve si no hubiese nada con esa clave		
	}
	
	public void setUltimoIdentificadorUnicoHijoPedido(){
		//Almacenamos la posicion del restaurante de la lista
		SharedPreferences preferencia = getSharedPreferences("Identificador_Unico", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("identificadorUnicoHijoPedido", identificadorUnicoHijoPedido);
		editor.commit(); //Para que surja efecto el cambio
	}
    
	
	public void onClickDescripcion(View v)
    {
		
		TextView t=(TextView)findViewById(R.id.descripcionPlato);
		ImageView image=(ImageView) findViewById(R.id.imageflecha);
		
    	ancho=t.getLayoutParams().width;
		largo=t.getLayoutParams().height;
		
		if (!pulsado){
			pulsado=true;
			//ancho=t.getLayoutParams().width;
			//largo=t.getLayoutParams().height;
			LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			a.setMargins(52, 0, 5, 0);
			t.setLayoutParams(a);
			image.setImageResource(R.drawable.flecha_arriba);
			
			}
		else{
			//t.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,50));
			LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,50);//ancho,largo);
			a.setMargins(52, 0, 5, 0);
			t.setLayoutParams(a);
			image.setImageResource(R.drawable.flecha_abajo);
			pulsado= false;
		}
		
    }
}
 