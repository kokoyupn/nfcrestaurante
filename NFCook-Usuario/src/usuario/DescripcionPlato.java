package usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.nfcook.R;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.Handler;

public class DescripcionPlato extends Activity {
	 
	private int cantidad, numPlato;
	boolean carne, guarnicion;
	double precio;
	private String restaurante, nombrePlato, nombreImagen, descripcion;
	private ImageView img;
	private TextView tvPrecio, tvNombre, tvDescripcion; //tv3
	AutoCompleteTextView actw;
    
    private static final String NOMBRE = "NOMBRE";  
	private List<Map<String, String>> platoPadre = new ArrayList<Map<String, String>>();  
	public List<List<Map<String, RadioGroup>>> platoHijo = new ArrayList<List<Map<String, RadioGroup>>>();
	private ExpandableListView exp;
	private DescripcionPlatoAdapter dA;
	public String[] seleccionado;
	
	public Handler sql;
	public SQLiteDatabase db;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.descripcion_plato);
        
        carne = guarnicion = false;
        cantidad = 1;
        numPlato = 0;
       
        tvPrecio= (TextView) findViewById(R.id.textViewPrecio);
        tvNombre= (TextView) findViewById(R.id.nombrePlato);
        tvDescripcion= (TextView) findViewById(R.id.descripcionPlato);
        img = (ImageView) findViewById(R.id.imagenPlato);
        //tv3 = (TextView) findViewById(R.id.opcionesPlato); 
        actw = (AutoCompleteTextView)findViewById(R.id.AutoCompleteTextViewOpciones);
      

        // Obtenemos el nombre del plato y el restaurante de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        nombrePlato = bundle.getString("nombrePlato");
        restaurante =bundle.getString("nombreRestaurante");
     
        // Importamos la base de datos para su posterior lectura
        try{
        	sql=new Handler(getApplicationContext()); 
         	db=sql.open();
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        } 

        // Hacemos una consulta en la base de datos sobre el plato seleccionado   
        String[] campo1=new String[]{"Extras","Precio","Foto","Descripcion"};
        String[] datos = new String[]{restaurante, nombrePlato};
        Cursor c =db.query("Restaurantes",campo1,"Restaurante =? AND Nombre=?",datos,null,null,null);  
      
    	exp = (ExpandableListView) findViewById(R.id.expandableExtras);
        	
    	while (c.moveToNext()){
    		precio = c.getDouble(1);
            nombreImagen = c.getString(2);
            descripcion = c.getString(3);
            String extras = c.getString(0);
            if (!extras.equals("")){
	            String[] tokens = extras.split("/");
		        seleccionado = new String[tokens.length];
		        for(int i= 0; i< tokens.length ;i++)
		        {	
		        	String[] nombreExtra = null;
					try{
						nombreExtra = tokens[i].split(":");
					
						// Creamos los padres de la lista, serán las distintas categorías de extras
						Map<String, String> tipoPlatoActual = new HashMap<String, String>();  
						tipoPlatoActual.put(NOMBRE, nombreExtra[0]);
						platoPadre.add(tipoPlatoActual);  
						
						// Creamos los hijos, serán la variedad de extras
						String[] elementosExtra = null;
	
						elementosExtra = nombreExtra[1].split(",");
						List<Map<String, RadioGroup>> listaPlatosActual = new ArrayList<Map<String, RadioGroup>>();  
						Map<String, RadioGroup> platoActual = new HashMap<String, RadioGroup>();
						
						// Creamos los radio grupos y radio buttons para seleccionar un extra solo
						RadioGroup rg = new RadioGroup(this);
						for(int j=0; j<elementosExtra.length;j++)
						{
							RadioButton rb = new RadioButton(this);
							rb.setText(elementosExtra[j]);
							rg.addView(rb);
						}
				    	
						// Añadimos la información del hijo a la lista de hijos
						// IMPORTANTE -> Solo tenemos un elemento en la lista por cada lista de hijos
						platoActual.put(NOMBRE, rg);
				    	listaPlatosActual.add(platoActual);
				    	platoHijo.add(listaPlatosActual);
				    	
					}catch(Exception e){
						Toast.makeText(getApplicationContext(),"Error en el formato de extra en la BD", Toast.LENGTH_SHORT).show();
					}
				}	    	    	
            }else
	        	exp.setVisibility(ExpandableListView.INVISIBLE);
            	//tv3.setText("");
    	}
    	
        // Creamos el adapater para adaptar la lista a la pantalla
        dA = new DescripcionPlatoAdapter(this, this, platoPadre, platoHijo);
        exp.setAdapter(dA);
        		

        // Cargamos la imagen del plato
    	img.setImageResource(getResources().getIdentifier(nombreImagen,"drawable",this.getPackageName()));	
      
        // Damos el texto a los textviews
    	//tv3.setText("Elige el plato " + (numPlato+1));
    	tvPrecio.setText("P.V.P.       "+ precio +" €");
    	tvNombre.setText(nombrePlato);
    	tvDescripcion.setText(descripcion);
    	
    	//CREACION DEL SPINNER
        Spinner sp = (Spinner) findViewById(R.id.idCantidad);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.cantidades, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    	
    	sp.setOnItemSelectedListener(new OnItemSelectedListener(){
    		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    			cantidad=position+1;
    		}
         	
    		public void onNothingSelected(AdapterView<?> parentView){
    		}
         });        	    	
	}
	 
	 
    public void onClickConfirmar(View boton){
    	// Recorremos los RadioGroups para ver la selección del usuario
    	/*for(int i=0;i<platoPadre.size();i++){
    		int numHijos = platoHijo.get(i).get(0).get("NOMBRE").getChildCount();
    		RadioGroup rg = (RadioGroup) platoHijo.get(i).get(0).get("NOMBRE");
    		for(int j=0;j<numHijos;j++){
    			RadioButton rb = (RadioButton) rg.getChildAt(j);
        		String des = rb.getText().toString();
    			if(rb.isChecked())
    				Log.i("Checked","HIJO"+i+" selccionado: "+rb.getText().toString());
    		}
    	}*/
    	
    	if (seleccionado != null)
    		for(int i=0;i<seleccionado.length;i++){
    			Log.i("Checked","HIJO"+i+" selccionado: "+seleccionado[i]);
    		}


    	Toast.makeText(getApplicationContext(),"Plato Nº " + cantidad + " confirmado", Toast.LENGTH_SHORT).show();
    	if(cantidad == 1){
    		this.finish();
    	}else{
    		cantidad--;
    	}
    	
    	
    	
    	

    	/*
    	if(numPlato<cantidad){
    		if(numPlato+1==cantidad)
    			tv3.setText("Guacamole de banday" );
    			
    		else{
    			tv3.setText("Elige el plato " + (numPlato+2) );
    			actw.setText("");
    		Toast.makeText(getApplicationContext(),"Plato confirmado", Toast.LENGTH_SHORT).show();
    		}
    	}
    	if(numPlato<cantidad)
    		numPlato++;*/
    }
    
    public void setPlatoHijo(int pos, List<Map<String,RadioGroup>> lista){
    	platoHijo.set(pos,lista);
    }
    
    
}
 