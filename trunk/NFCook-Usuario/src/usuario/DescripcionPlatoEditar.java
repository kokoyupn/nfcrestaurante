package usuario;

import java.util.ArrayList;

import com.example.nfcook.R;

import fragments.PedidoFragment;

import baseDatos.Handler;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import adapters.PadreExpandableListPedido;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DescripcionPlatoEditar extends Activity {
		
	private AutoCompleteTextView actwObservaciones;
    	
    private static ExpandableListView expandableListPedidoEditar;
	private static MiExpandableListAdapterEditar adapterExpandableListPedidoEditar;
			
	private String observaciones, idPlato, nombrePlato;
	private String idHijo;
	
	public Handler sql,sqlPedido;
	public SQLiteDatabase db,dbPedido;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.descripcion_plato);
       
        TextView textViewPrecio= (TextView) findViewById(R.id.textViewPrecio);
        TextView textViewNombrePlato= (TextView) findViewById(R.id.nombrePlato);
        TextView textViewDescripcion= (TextView) findViewById(R.id.descripcionPlato);
        ImageView imgeViewPlato = (ImageView) findViewById(R.id.imagenPlato);
        Button botonConfirmar = (Button) findViewById(R.id.botonOpcion);
        Button botonEditar = (Button) findViewById(R.id.botonOpcionEditar);
        botonConfirmar.setVisibility(Button.INVISIBLE);
        botonEditar.setVisibility(Button.VISIBLE);

        actwObservaciones = (AutoCompleteTextView)findViewById(R.id.AutoCompleteTextViewOpciones);
      

        // Obtenemos el nombre del plato y el restaurante de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        idPlato = bundle.getString("idPlato");
        String extras = bundle.getString("extras");
        observaciones = bundle.getString("observaciones");
        idHijo = bundle.getString("idHijo");
     
        // Importamos la base de datos para su posterior lectura
        importarBaseDatatos();

        // Hacemos una consulta en la base de datos sobre el plato seleccionado   
        String[] campos =new String[]{"Extras","Precio","Foto","Descripcion","Nombre"};
        String[] datos = new String[]{idPlato};
        Cursor cursor =db.query("Restaurantes",campos,"Id =?",datos,null,null,null);  
      
    	expandableListPedidoEditar = (ExpandableListView) findViewById(R.id.expandableExtras);
    	        
    	cursor.moveToFirst();
		double precio = cursor.getDouble(1);
        String nombreImagen = cursor.getString(2);
        String descripcion = cursor.getString(3);
        nombrePlato = cursor.getString(4);
        String extrasBusqueda = cursor.getString(0);
    	
        if (extras != null){
            String[] tokens = extrasBusqueda.split("/");
            String[] extrasSeleccionados = extras.split(",");
	        // Creamos los padres de la lista, serán las distintas categorías de extras
	        ArrayList<PadreExpandableListEditar> categoriasExtras =  new ArrayList<PadreExpandableListEditar>();
	        for(int i= 0; i< tokens.length ;i++)
	        {	
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
    	textViewPrecio.setText("P.V.P.       "+ precio +" €");
    	textViewNombrePlato.setText(nombrePlato);
    	textViewDescripcion.setText(descripcion);
        		
    	
    	//No necesitamos el spiner en esta pantalla
        Spinner spinnerCantidad = (Spinner) findViewById(R.id.idCantidad);
        spinnerCantidad.setVisibility(Spinner.INVISIBLE);
                
	}
	
	public void onClickConfirmarEditar(View v){
		importarBaseDatatosPedido();
		String nuevosExtrasMarcados = null;
		if(adapterExpandableListPedidoEditar != null){ // El plato tiene extras
			nuevosExtrasMarcados = adapterExpandableListPedidoEditar.getExtrasMarcados();
		}
    	
    	String observacionesNuevas;
    	if(!actwObservaciones.getText().toString().equals("")){
    		observacionesNuevas = actwObservaciones.getText().toString();
    	}else{
    		observacionesNuevas = this.observaciones;
    	}
    	ContentValues platoEditado = new ContentValues();
    	platoEditado.put("Id", idPlato);
    	platoEditado.put("Plato", nombrePlato);
    	platoEditado.put("Extras", nuevosExtrasMarcados);
    	platoEditado.put("Observaciones", observacionesNuevas);
        String[] camposUpdate = {idPlato,idHijo};
        dbPedido.update("Pedido", platoEditado, "Id =? AND IdHijo=?", camposUpdate);
        PedidoFragment.actualizaExpandableListPedidoEditada();
        this.finish();
	}
    	
	public static void actualizaExpandableList() {
		expandableListPedidoEditar.setAdapter(adapterExpandableListPedidoEditar);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListPedidoEditar.expandGroup(groupPositionMarcar);
	}
    
    private void importarBaseDatatos() {
    	try{
        	sql=new Handler(getApplicationContext()); 
         	db=sql.open();
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        } 
	}
    
    private void importarBaseDatatosPedido() {
		 try{
	     	   sqlPedido=new Handler(getApplicationContext(),"Pedido.db"); 
	     	   dbPedido=sqlPedido.open();
	         }catch(SQLiteException e){
	         	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS PEDIDO USUARIO",Toast.LENGTH_SHORT).show();
	      		
	         }
		
	}
    
}
