package usuario;

import java.util.ArrayList;

import com.example.nfcook.R;

import fragments.PedidoFragment;

import baseDatos.HandlerDB;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.MiGridViewSeleccionarIngredientesPlato;
import adapters.PadreExpandableListEditar;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class DescripcionPlatoEditar extends Activity {
    	
    private static ExpandableListView expandableListPedidoEditar;
	private static MiExpandableListAdapterEditar adapterExpandableListPedidoEditar;
			
	private static ArrayList<Boolean> ingredientesMarcado;
	private ArrayList<String> ingredientes;
	
	private String idPlato, nombrePlato, idHijo;
	
	public HandlerDB sql,sqlPedido;
	public SQLiteDatabase db,dbPedido;
	
	private static boolean pulsado;


	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" EDICIÓN DE PLATO");
    	
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
    	pulsado=false;
    	
        setContentView(R.layout.descripcion_del_plato_editar);
        
        TextView textViewDescripcion =(TextView)findViewById(R.id.descripcionEditar);
		LayoutParams a =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,textViewDescripcion.getLineHeight()*2);//ancho,largo);
		a.setMargins(52, 0, 5, 0);
		textViewDescripcion.setLayoutParams(a);
		
        TextView textViewNombrePlato= (TextView) findViewById(R.id.nombrePlato);
        ImageView imgeViewPlato = (ImageView) findViewById(R.id.imagenPlato);
        

        // Obtenemos el nombre del plato y el restaurante de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        idPlato = bundle.getString("idPlato");
        String extras = bundle.getString("extras");
        String ingredientesStr = bundle.getString("ingredientes");
        idHijo = bundle.getString("idHijo");
     
        // Importamos la base de datos para su posterior lectura
        importarBaseDatatos();

        // Hacemos una consulta en la base de datos sobre el plato seleccionado   
        String[] campos =new String[]{"Extras","Precio","Foto","Descripcion","Nombre","Ingredientes"};
        String[] datos = new String[]{idPlato};
        Cursor cursor =db.query("Restaurantes",campos,"Id =?",datos,null,null,null);  
      
    	expandableListPedidoEditar = (ExpandableListView) findViewById(R.id.expandableExtras);
    	        
    	cursor.moveToFirst();
        String nombreImagen = cursor.getString(2);
        String descripcion = cursor.getString(3);
        nombrePlato = cursor.getString(4);
        String extrasBusqueda = cursor.getString(0);
        String ingredientesBusqueda = cursor.getString(5);
    	
        if (!extrasBusqueda.equals("")){
            String[] tokens = extrasBusqueda.split("/");
            String[] extrasSeleccionados = extras.split(",");
	        // Creamos los padres de la lista, serán las distintas categorías de extras
	        ArrayList<PadreExpandableListEditar> categoriasExtras =  new ArrayList<PadreExpandableListEditar>();
	        for(int i= 0; i< tokens.length ;i++){	
	        	String[] nombreExtra = null;
	        	String extraSeleccionadoPradreI = extrasSeleccionados[i];
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
						if(extraSeleccionadoPradreI.contains(elementosExtra[j])){
							extrasMarcados[j] = true;
						}else{
							extrasMarcados[j] = false;
						}
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
	        // Creamos el adapater para adaptar la lista a la pantalla.
	    	adapterExpandableListPedidoEditar = new MiExpandableListAdapterEditar(this, categoriasExtras,true);
	        expandableListPedidoEditar.setAdapter(adapterExpandableListPedidoEditar);
        }else{
        	//Actualizamos el adapter a null, ya que es static, para saber que este plato no tiene extras.
        	adapterExpandableListPedidoEditar = null;
        	expandableListPedidoEditar.setVisibility(ExpandableListView.INVISIBLE);
        }
        
        // Cargamos la imagen del plato
        imgeViewPlato.setImageResource(getResources().getIdentifier(nombreImagen,"drawable",this.getPackageName()));	
       
        // Damos el texto a los textviews
    	textViewNombrePlato.setText(nombrePlato);
    	textViewDescripcion.setText(descripcion);
    	
    	ingredientes = new ArrayList<String>();
        ingredientesMarcado = new ArrayList<Boolean>();
        String[] ingredientesSeleccionados = ingredientesStr.split(",");
        
    	if (!ingredientesBusqueda.equals("")){
    		
    		if (ingredientesStr.equals("Con todos los ingredientes")){
    			String[] tokens = ingredientesBusqueda.split("%");
        		for (int i=0; i<tokens.length; i++){
        			ingredientes.add(tokens[i]);
        			ingredientesMarcado.add(true);
        		}
    		} else {
    			String[] tokens = ingredientesBusqueda.split("%");
        		for (int i=0; i<tokens.length; i++){
        			ingredientes.add(tokens[i]);
        			
        			int j = 0, espacio = 0; // espacios es para comparar sin espacios
        			boolean marcado = false;
        			while(!marcado && j <ingredientesSeleccionados.length){        		
        				if (tokens[i].toLowerCase().equals(ingredientesSeleccionados[j].substring(4 + espacio)))
        					marcado = true;
        				else 
        					j++;
        				if (j == 1) espacio = 1;
        			}
        			if (marcado)
        				ingredientesMarcado.add(false);
        			else 
        				ingredientesMarcado.add(true);
        		}
    		}
    		
    	}
        
        
        ImageView botonIngredientes = (ImageView) findViewById(R.id.botonIngredientes);
        botonIngredientes.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {
				
				if(ingredientes.size() == 0){
		    		Toast.makeText(getApplicationContext(), "Ingredientes no disponibles", Toast.LENGTH_SHORT).show();
				}else{
			
					/*************PREPARAMOS EL LAYOUT DE LA VENTANA EMERGENTE**************/
					AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(DescripcionPlatoEditar.this);           
					// Creamos la vista que tendrá la ventana de elección de ingredientes
					View vistaVentanaEmergente = LayoutInflater.from(DescripcionPlatoEditar.this).inflate(R.layout.ventana_emergente_elegir_ingredientes, null);
					// Añadimos el botón Aceptar
					ventanaEmergente.setNegativeButton("Aceptar", null);
					
					GridView gridViewEleccionIngredientes = (GridView) vistaVentanaEmergente.findViewById(R.id.gridViewElegirIngredientes);
					MiGridViewSeleccionarIngredientesPlato miGridViewSeleccionarIngredientesPlato = new MiGridViewSeleccionarIngredientesPlato(DescripcionPlatoEditar.this, ingredientes, ingredientesMarcado );
					gridViewEleccionIngredientes.setAdapter(miGridViewSeleccionarIngredientesPlato);
										
					// Aplicamos la vista a la ventana y la lanzamos
					ventanaEmergente.setView(vistaVentanaEmergente);
					ventanaEmergente.show();
					/********************FIN VENTANA EMERGENTE REPARTO*********************/
				}
			}
		});
    	
	}
	
	public void onClickConfirmarEditar(View v){
		importarBaseDatatosPedido();
		String nuevosExtrasMarcados = null;
		String nuevosExtrasBinarios = null;
		String ingredientesBinarios = null;
		String ingredientesStr = null;
		
		
		if(adapterExpandableListPedidoEditar != null){ // El plato tiene extras
			nuevosExtrasMarcados = adapterExpandableListPedidoEditar.getExtrasMarcados();
			nuevosExtrasBinarios = adapterExpandableListPedidoEditar.getExtrasBinarios();
		}
	
		if(ingredientesMarcado.size() > 0){
			ingredientesStr = "";
			ingredientesBinarios = "";
			for(int i=0; i<ingredientesMarcado.size(); i++){
				if(ingredientesMarcado.get(i)) // == true
					ingredientesBinarios += "1";
				else{						   // == false
					ingredientesBinarios += "0";
					ingredientesStr += ingredientes.get(i) + ", sin ";
				}
			}
			if (ingredientesStr.equals("")){
				ingredientesStr = "Con todos los ingredientes";
			} else {
				ingredientesStr = "Sin " + ingredientesStr.substring(0, ingredientesStr.length()-6).toLowerCase();
			}    			
		}
		
    	ContentValues platoEditado = new ContentValues();
    	platoEditado.put("Id", idPlato);
    	platoEditado.put("Plato", nombrePlato);
    	platoEditado.put("Extras", nuevosExtrasMarcados);
    	platoEditado.put("ExtrasBinarios", nuevosExtrasBinarios);    	
    	platoEditado.put("Ingredientes", ingredientesStr);
    	platoEditado.put("IngredientesBinarios", ingredientesBinarios);
    	String[] camposUpdate = {idPlato,idHijo};
    	dbPedido.update("Pedido", platoEditado, "Id =? AND IdHijo=?", camposUpdate);
        PedidoFragment.actualizaExpandableListPedidoEditada();
        this.finish();
	}
    	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }
	
	public static void actualizaExpandableList() {
		adapterExpandableListPedidoEditar.notifyDataSetChanged();
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListPedidoEditar.expandGroup(groupPositionMarcar);
	}
    
    private void importarBaseDatatos() {
    	try{
        	sql=new HandlerDB(getApplicationContext()); 
         	db=sql.open();
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        } 
	}
    
    private void importarBaseDatatosPedido() {
		 try{
	     	   sqlPedido=new HandlerDB(getApplicationContext(),"Pedido.db"); 
	     	   dbPedido=sqlPedido.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS PEDIDO USUARIO",Toast.LENGTH_SHORT).show();
	      		
	         }
		
	}
    
    public void onclickLayout(View v) {
		
		TextView t=(TextView)findViewById(R.id.descripcionEditar);
		ImageView image=(ImageView) findViewById(R.id.imageViewEditar);
		
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
