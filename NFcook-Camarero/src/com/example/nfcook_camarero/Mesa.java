package com.example.nfcook_camarero;

import java.util.ArrayList;
import java.util.Iterator;

import adapters.MiListAdapterMesa;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import adapters.MiListAdapterMesa;
import adapters.ContenidoListMesa;

/**
 * Añade los componentes de cada pedido a la mesa
 * 
 * -Atributos-
 * numMesa : Indica el numero de la mesa actual.
 * dbPedido : Base de datos con todos los pedidos de todas las mesas.
 * dbHistorico : Base de datos donde se almacenan todos los platos pedidos una vez cobrada esa mesa.
 * platos : Componente ListView que mostrará los platos de la mesa actual.
 * elemLista : ArrayList con los platos de la mesa actual, que se utiliza para crear el adapter del componente ListView.
 * adapter : Objeto de la clase ListaMesaAdapter.
 * precioTotal : TextView que contiene el precio total de los pedidos de la mesa actual.
 * 
 * @author Rober
 */


public class Mesa extends Activity {
	private String numMesa="6";//FIXME introducir desde fuera
	private SQLiteDatabase dbPedido,dbHistorico;
	private ListView platos;
	private ArrayList<ContenidoListMesa> elemLista;
	private MiListAdapterMesa adapter;
	private TextView precioTotal;
	private int indicePulsado;
	private ArrayList<MesaView> listaDeMesas;
	private Activity actividad;
	
	String rutaPedido="/data/data/com.example.nfcook_camarero/databases/Mesas.db";
	String rutaHistorico="/data/data/com.example.nfcook_camarero/databases/Historico.db";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pedidomesa);
		actividad = this;
		
		TextView mesa = (TextView)findViewById(R.id.numeroDeMesa);
		mesa.setText("Mesa: "+ String.valueOf(numMesa) );
		
		
		try{
			dbPedido= SQLiteDatabase.openDatabase(rutaPedido, null, SQLiteDatabase.OPEN_READWRITE);
			dbHistorico= SQLiteDatabase.openDatabase(rutaHistorico, null, SQLiteDatabase.OPEN_READWRITE);
			
			//Añadir platos a la ListView----------------------------------------------------
	  	  	platos = (ListView)findViewById(R.id.listaPlatos);
		    elemLista = obtenerElementos();
	         
	  	    adapter = new MiListAdapterMesa(this, elemLista);
	  	     
	  	    precioTotal = (TextView)findViewById(R.id.precioTotal);
	  	    precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
	  	     
	  	    platos.setAdapter(adapter); 
	  	    
	  	    platos.setOnItemClickListener(new OnItemClickListener() {
	  	    	public void onItemClick(AdapterView<?> arg0, View vista,int posicion, long id) {
	  	    		//Ir a editar el plato
	  	    	}
	  	    });
	  	    
	  	    platos.setOnItemLongClickListener(new OnItemLongClickListener(){
	  	    	public boolean onItemLongClick(AdapterView<?> l, View v, int position, long id) {
	  	    		indicePulsado=position;
					DragShadowBuilder myShadow = new DragShadowBuilder(v);
					//ClipData info = ClipData.newPlainText("posicion", Integer.toString(position));
					v.startDrag(null,myShadow,v,0);
					
					return true;					
				}});
	  	    
	  	   platos.setOnDragListener(new OnDragListener() {
		    	public boolean onDrag(View view, DragEvent event) {
		    		//Las acciones se realizan al soltar el elemento de la lista arrastrado.
		    		if(event.getAction()==DragEvent.ACTION_DRAG_ENDED){
		    			
		    			ContenidoListMesa platoSeleccionado = (ContenidoListMesa) adapter.getItem(indicePulsado);
	    				String identificador = Integer.toString(platoSeleccionado.getId());
	    				try{
	    					dbPedido.delete("Mesas", "IdUnico=?",new String[]{identificador});
	    				}catch(Exception e){
	    					System.out.println("Error borrar de la base pedido en ondrag");
	    				}
	    				
	    				adapter.deletePosicion(indicePulsado);
	    				platos.setAdapter(adapter);
	    				
	    				//Recalculamos el precio(será cero ya que no quedan platos en la lista)
	            		precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
		    		}
		    	    return true;
		    	}
			});
	  	}catch(Exception e){
			System.out.println("Error lectura base de datos de Pedido");
		}
		
		
		//Boton Cobrar--------------------------------------------------------------------
		Button cobrar = (Button)findViewById(R.id.botonCobrar);
		cobrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		String[] numeroDeMesa = new String[]{numMesa};
        		    Cursor filasPedido = dbPedido.query("Mesas", null, "NumMesa=?", numeroDeMesa,null, null, null);
            		Cursor filasHistorico = dbHistorico.query("Historico", null, null,null, null,null, null);
            		
            		while(filasPedido.moveToNext()){
            			//Añades los platos a la base de datos del historico y borras de la lista de platos
            			ContentValues nuevo = new ContentValues();
            			int plato=0;
            			
            			for (int i=0;i<filasPedido.getColumnCount();i++){
            				for (int j=0;j<filasPedido.getColumnCount();j++){
            					if(filasPedido.getColumnName(i).equals(filasHistorico.getColumnName(j))){
            						nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));
            						
	            					if(filasPedido.getColumnName(i).equals("IdUnico")){	
	            						plato = Integer.parseInt(filasPedido.getString(i));
	            						adapter.deleteId(plato);
	            					}
	            				}
            				}
            			}
            			/*FIXME No borrar por si las bases de historico y pedido tienen las mismas columnas
            			for (int i=0;i<filasPedido.getColumnCount();i++){
            				nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));
            				if(filasPedido.getColumnName(i).equals("IdUnico")){	
        						plato = Integer.parseInt(filasPedido.getString(i));
        						adapter.deleteId(plato);
        					}
            			}*/
            			dbHistorico.insert("Historico", null, nuevo);
	            	}
            		
            		//Carga el adapter sin los platos borrados
            		platos.setAdapter(adapter); 
            		
            		//Recalculamos el precio(será cero ya que no quedan platos en la lista)
            		precioTotal.setText(Float.toString(adapter.getPrecio())+" €");
            		
            		//Borra de la base de datos los platos de esta mesa
            		dbPedido.delete("Mesas", "NumMesa=numMesa", null);
            		
            		//Se borra la mesa y se vuelve a la pantalla anterior.
            		Intent intent = new Intent(actividad, InicialCamarero.class);
            		Log.d("sdsd","sdsd");//Aqui si q llega
            		intent.putExtra("mesas", borrarMesaActual());//Casca aqui porque listaDeMesas es vacio
            		Log.d("sdsd","sdsd");
            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton cobrar");
            	}
            }
        });
		//Boton Cobrar--------------------------------------------------------------------
		
		
		//Boton AñadirPlato---------------------------------------------------------------
		Button aniadirPlato = (Button)findViewById(R.id.aniadirPlato);
		aniadirPlato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		Intent intent = new Intent(actividad, AnadirPlatos.class);
            		intent.putExtra("numMesa", numMesa);
            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Error funcionalidad de boton AñadirPlato");
            		
            	}
            }
        });
		//Boton AñadirPlato---------------------------------------------------------------
		
		
		//Boton AñadirBebida--------------------------------------------------------------
		Button aniadirBebida = (Button)findViewById(R.id.aniadirBebida);
		aniadirBebida.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
            		dbPedido.openDatabase(rutaPedido, null,SQLiteDatabase.OPEN_READWRITE);
            		String[] campos = new String[]{"IdCamarero","Personas"};
        		    String[] numeroDeMesa = new String[]{numMesa};
        		    
        		    Cursor c = dbPedido.query("Mesas",campos, "NumMesa=?",numeroDeMesa, null,null, null);
        		    c.moveToNext();
        		    
        		    Intent intent = new Intent(actividad, AnadirBebidas.class);
            		intent.putExtra("numMesa", numMesa);
            		intent.putExtra("idCamarero",c.getString(0));
            		intent.putExtra("personasMesa", c.getString(1));
            		
            		startActivity(intent);
            		
            	}catch(Exception e){
            		System.out.println("Catch funcionalidad de boton AñadirBebida");
            		
            	}
            }
        });
		//Boton AñadirBebida--------------------------------------------------------------
	}
	
	
	
	/**
	 * Obtiene los elementos del aArrayList, en funcion de la base de datos y del contenido 
	 * de la mesa actual, que servirá para confeccionar el adapter de la ListView.
	 * 
	 * @return un ArrayList con los elementos de la mesa actual.
	 */
	private ArrayList<ContenidoListMesa> obtenerElementos() {
		ArrayList<ContenidoListMesa> elementos=null;
		try{
			String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio","IdUnico"};
		    String[] numeroDeMesa = new String[]{numMesa};
		    
		    Cursor c = dbPedido.query("Mesas",campos, "NumMesa=?",numeroDeMesa, null,null, null);
		    
		    elementos = new ArrayList<ContenidoListMesa>();
		     
		    while(c.moveToNext())
		    	elementos.add(new ContenidoListMesa(c.getString(0) ,c.getString(2),c.getString(1),Float.parseFloat(c.getString(3)),c.getInt(4)));
		    	
		    return elementos;
		    
		}catch(Exception e){
			System.out.println("Error en obtenerElementos");
			return elementos;
		}
	}
	
	private ArrayList<MesaView> borrarMesaActual(){
		System.out.println("llega");
		Iterator<MesaView> it = listaDeMesas.iterator();
		boolean encontrado=false;
		int i=0;
		
		System.out.println("llega");
		while(it.hasNext() && !encontrado){
			MesaView actual = it.next();
			System.out.println("Numero de mesa actual "+actual.getNumMesa());
			if(actual.getNumMesa().equals(numMesa)){
				System.out.println("Borra la mesa: "+actual.getNumMesa());
				listaDeMesas.remove(i);
				encontrado = true;
			}
			i++;
		}
		return listaDeMesas;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
