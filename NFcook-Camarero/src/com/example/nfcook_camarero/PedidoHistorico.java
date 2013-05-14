package com.example.nfcook_camarero;

import java.util.ArrayList;

import adapters.ContenidoListPedidoHistorico;
import adapters.MiListAdapterPedidoHistorico;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Muestra los platos de un pedido concreto en el histórico.
 * 
 * -Atributos-
 * numMesa : Indica el numero de la mesa seleccionada.
 * dbHistorico : Base de datos donde se almacenan todos los platos pedidos una vez cobrada esa mesa.
 * platos : Componente ListView que mostrará los platos del pedido seleccionado.
 * elemLista : ArrayList con los platos del pedido seleccionado, que se utiliza para crear el adapter del componente ListView.
 * adapter : Objeto de la clase ListaMesaAdapter.
 * precioTotal : TextView que contiene el precio total del pedido.
 * hora: Indica la hora del pedido a mostrar.
 * 
 * @author Juan Diego y Álvaro
 */


public class PedidoHistorico extends Activity {

	private HandlerGenerico sqlHistorico;
	private String numMesa;
	private SQLiteDatabase dbHistorico;
	private static ListView platos;
	private ArrayList<ContenidoListPedidoHistorico> elemLista;
	private static MiListAdapterPedidoHistorico adapter;
	private static TextView precioTotal;
	private String hora;
	
	
	private static Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pedidohistorico);
		//Necesario para actualizar la lista de las mesas al añadir un plato
		context = PedidoHistorico.this;
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("mesa");
		hora = bundle.getString("hora");
		
		// Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" MESA "+ numMesa);
		
		//TextView mesa = (TextView)findViewById(R.id.textViewFechaHora);
		//mesa.setText("Fecha: " + hora.substring(0, 11) + " a las " + hora.substring(hora.indexOf(" ")+1));
		
		try{
			sqlHistorico=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Historico.db");
			dbHistorico= sqlHistorico.open();
			
			//Añadir platos a la ListView----------------------------------------------------
	  	  	platos = (ListView)findViewById(R.id.listaPlatosHistorico); 
		    elemLista = obtenerElementos();
	         
	  	    adapter = new MiListAdapterPedidoHistorico(this, elemLista);
	  	     
	  	    precioTotal = (TextView)findViewById(R.id.precioTotal);
	  	    precioTotal.setText(Double.toString( Math.rint(adapter.getPrecio()*100)/100 )+" €");
	  	     
	  	    platos.setAdapter(adapter);
	  	    
	  	 
	    
		}catch(Exception e){
			System.out.println("Error lectura base de datos de Historico");
		}
		

	}
	
	private ArrayList<ContenidoListPedidoHistorico> obtenerElementos() {
		ArrayList<ContenidoListPedidoHistorico> elementos=null;
		try{
			String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio"};
		    String[] consulta = new String[]{numMesa,hora};
		    
		    Cursor c = dbHistorico.query("Historico",campos, "NumMesa=? AND FechaHora=?",consulta, null,null, null);
		    
		    elementos = new ArrayList<ContenidoListPedidoHistorico>();
		     
		    while(c.moveToNext())
		    	elementos.add(new ContenidoListPedidoHistorico(c.getString(0) ,c.getString(2),c.getString(1),c.getDouble(3)));
		    	
		    return elementos;
		    
		}catch(Exception e){
			System.out.println("Error en obtenerElementos");
			return elementos;
		}
	}
	
	
	public static ListView getPlatos(){
		return platos;
	}

	public static TextView getPrecioTotal() {
		return precioTotal;
	}

	public static Context getContext() {
		return context;
	}
	
}
