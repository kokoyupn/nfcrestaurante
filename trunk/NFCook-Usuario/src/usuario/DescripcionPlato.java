package usuario;

import java.util.ArrayList;








import baseDatos.HandlerDB;

import com.example.nfcook.R;

import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.MiGridViewCalculadoraAdapter;
import adapters.MiGridViewRepartirPlatoCalculadoraAdapter;
import adapters.MiGridViewSeleccionarIngredientesPlato;
import adapters.PadreExpandableListEditar;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DescripcionPlato extends Activity {
	
	private static ArrayList<Boolean> ingredientesMarcado;
	private ArrayList<String> ingredientes;
	
	private static int identificadorUnicoHijoPedido;
	 
	private int cantidad;
	double precioPlato;
	private String restaurante, nombrePlato, idPlato;
	private TextView textViewPrecio;
	private EditText editTextUnidades;
	private static ExpandableListView expandableListExtras;
	private static MiExpandableListAdapterEditar adapterExpandableListExtras;
	private static boolean pulsado;

	public HandlerDB sql,sqlPedido, sqlMiBase;
	public SQLiteDatabase db,dbPedido, dbMiBase;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" SELECCIÓN DE PLATO");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
    	//	Variables expandir
    	pulsado=false;
    	
    	setContentView(R.layout.descripcion_del_plato);
    	
    	TextView t=(TextView)findViewById(R.id.descripcionPlatoeditar);
		LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,t.getLineHeight()*2);//ancho,largo);
		a.setMargins(52, 0, 5, 0);
		t.setLayoutParams(a);
               
        cargarUltimoIdentificadorUnicoHijoPedido();

        editTextUnidades = (EditText) findViewById(R.id.editTextunidades);
        textViewPrecio= (TextView) findViewById(R.id.textViewPrecio);
        TextView textViewNombre = (TextView) findViewById(R.id.nombrePlato);
        TextView textViewDescripcion= (TextView) findViewById(R.id.descripcionPlatoeditar);
        ImageView imageViewPlato = (ImageView) findViewById(R.id.imagenPlato);
        
        Button botonEditar = (Button) findViewById(R.id.botonOpcionEditar);
        
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
        String[] campo1=new String[]{"Extras","Precio","Foto","Descripcion","Id","Ingredientes"};
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
        String ingredientesBusqueda = cursor.getString(5);
        
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
        
        editTextUnidades.addTextChangedListener(new TextWatcher() {
			
			public void afterTextChanged(Editable s) {
				try{
					cantidad = Integer.parseInt(editTextUnidades.getText().toString());
					textViewPrecio.setText(Math.rint(cantidad*precioPlato*100)/100 + " €");
				}catch(Exception e){
					cantidad = 1;
		         	Toast.makeText(getApplicationContext(),"Introduce una cantidad valida.",Toast.LENGTH_SHORT).show();
				}
				
				
	        }
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				  
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				  
			}
		});
        
        textViewPrecio.setText(precioPlato +" €");
        textViewNombre.setText(nombrePlato);
        textViewDescripcion.setText(descripcionPlato);
        
        cargarEstrella();
        
        ingredientes = new ArrayList<String>();
        ingredientesMarcado = new ArrayList<Boolean>();
  
    	if (!ingredientesBusqueda.equals("")){
    		String[] tokens = ingredientesBusqueda.split("%");
    		for (int i=0; i<tokens.length; i++){
    			ingredientes.add(tokens[i]);
    			ingredientesMarcado.add(true);
    		}
    	}
        
        
        ImageView botonIngredientes = (ImageView) findViewById(R.id.botonIngredientes);
        botonIngredientes.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {
				
				if(ingredientes.size() == 0){
		    		Toast.makeText(getApplicationContext(), "Ingredientes no disponibles", Toast.LENGTH_SHORT).show();
				}else{
			
					/*************PREPARAMOS EL LAYOUT DE LA VENTANA EMERGENTE**************/
					AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(DescripcionPlato.this);           
					// Creamos la vista que tendrá la ventana de elección de ingredientes
					View vistaVentanaEmergente = LayoutInflater.from(DescripcionPlato.this).inflate(R.layout.ventana_emergente_elegir_ingredientes, null);
					// Añadimos el botón Aceptar
					ventanaEmergente.setNegativeButton("Aceptar", null);
					
					GridView gridViewEleccionIngredientes = (GridView) vistaVentanaEmergente.findViewById(R.id.gridViewElegirIngredientes);
					MiGridViewSeleccionarIngredientesPlato miGridViewSeleccionarIngredientesPlato = new MiGridViewSeleccionarIngredientesPlato(DescripcionPlato.this, ingredientes, ingredientesMarcado );
					gridViewEleccionIngredientes.setAdapter(miGridViewSeleccionarIngredientesPlato);
					
					
					// Aplicamos la vista a la ventana y la lanzamos
					ventanaEmergente.setView(vistaVentanaEmergente);
					ventanaEmergente.show();
					/********************FIN VENTANA EMERGENTE REPARTO*********************/
				}
			}
		});
        
        ImageView botonConfirmar = (ImageView) findViewById(R.id.imageConfirmarPlato);
        botonConfirmar.setVisibility(Button.VISIBLE);
        
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
	
	private void cargarEstrella() {
		sqlMiBase = new HandlerDB(getApplicationContext(),"MiBase.db"); 
     	dbMiBase=sqlMiBase.open();
		
     	String[] camposSacar = new String[]{"Favorito"};
    	String[] camposCondicionanConsulta = new String[]{restaurante, idPlato};
		Cursor cP = dbMiBase.query("Restaurantes", camposSacar, "Restaurante=? AND Id=?", camposCondicionanConsulta,null, null,null);
		
		if (cP.moveToNext()){
			if (cP.getString(0).equals("star_si")){
				ImageView imagenStarSi = (ImageView) findViewById(R.id.imageViewStarSi);
				imagenStarSi.setVisibility(ImageView.VISIBLE);
				ImageView imagenStarNo = (ImageView) findViewById(R.id.imageViewStarNo);
				imagenStarNo.setVisibility(ImageView.INVISIBLE);
			} 
		}
		
        dbMiBase.close();
		
	}
	
	public void onClickStarNo(View v){
		ImageView imagenStarSi = (ImageView) findViewById(R.id.imageViewStarSi);
		imagenStarSi.setVisibility(ImageView.VISIBLE);
		ImageView imagenStarNo = (ImageView) findViewById(R.id.imageViewStarNo);
		imagenStarNo.setVisibility(ImageView.INVISIBLE);
		Toast.makeText(getApplicationContext(),"Plato añadido a Mis Favoritos", Toast.LENGTH_SHORT).show();
		
		sqlMiBase = new HandlerDB(getApplicationContext(),"MiBase.db"); 
     	dbMiBase=sqlMiBase.open();
		
     	ContentValues platoEditado = new ContentValues();
    	platoEditado.put("Favorito", "star_si");
		String[] camposUpdate = {restaurante, idPlato};
		dbMiBase.update("Restaurantes", platoEditado, "Restaurante=? AND Id=?", camposUpdate);
        
        dbMiBase.close();
        
        Intent intent = new Intent();
        intent.putExtra("Origen", "Favoritos");
        setResult(RESULT_OK, intent);
	}
	
	public void onClickStarSi(View v){
		ImageView imagenStarSi = (ImageView) findViewById(R.id.imageViewStarSi);
		imagenStarSi.setVisibility(ImageView.INVISIBLE);
		ImageView imagenStarNo = (ImageView) findViewById(R.id.imageViewStarNo);
		imagenStarNo.setVisibility(ImageView.VISIBLE);
		Toast.makeText(getApplicationContext(),"Plato eliminado de Mis Favoritos", Toast.LENGTH_SHORT).show();
	
		sqlMiBase = new HandlerDB(getApplicationContext(),"MiBase.db"); 
     	dbMiBase=sqlMiBase.open();
		
     	ContentValues platoEditado = new ContentValues();
    	platoEditado.put("Favorito", "star_no");
		String[] camposUpdate = {restaurante, idPlato};
		dbMiBase.update("Restaurantes", platoEditado, "Restaurante=? AND Id=?", camposUpdate);
		
        dbMiBase.close();
        
        Intent intent = new Intent();
        intent.putExtra("Origen", "Favoritos");
        setResult(RESULT_OK, intent);
	}
	 
    public void onClickConfirmar(View boton){
    	boolean bienEditado = true;
    	String observaciones = null;
    	String nuevosExtrasMarcados = null;
    	String extrasBinarios = null;

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
    		Toast.makeText(getApplicationContext(),"Todos sus platos han sido confirmados", Toast.LENGTH_SHORT).show();
    		this.finish();
    	}else{
    		adapterExpandableListExtras.expandeTodosLosPadres();
    		Toast.makeText(getApplicationContext(),"Termine de configurar su plato antes", Toast.LENGTH_SHORT).show();
    	}
    }
    
    
    //  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
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
		
		TextView t=(TextView)findViewById(R.id.descripcionPlatoeditar);
		ImageView image=(ImageView) findViewById(R.id.imageflecha);
		
      if (!pulsado){
			pulsado=true;

			LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			a.setMargins(52, 0, 5, 0);
			t.setLayoutParams(a);
			image.setImageResource(R.drawable.flecha_arriba);
			
			}
		else{
			//t.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,50));
			
			LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,t.getLineHeight()*2);//ancho,largo);
			a.setMargins(52, 0, 5, 0);
			t.setLayoutParams(a);
			image.setImageResource(R.drawable.flecha_abajo);
			pulsado= false;
		}
		
    }
}
 