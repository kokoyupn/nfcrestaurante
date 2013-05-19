package fragments;

import java.util.ArrayList;

import usuario.DescripcionPlato;

import com.example.nfcook.R;

import adapters.MiGridViewBebidasAdapter;
import adapters.PadreGridViewBebidas;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerDB;

/**
 * Clase encargada de mostrar el contenido de la penstaña bebidas con una interfaz distinta
 * a las demás categorías, con el fin de facilitar al usuario la elección de las mismas.
 * 
 * @author Abel
 *
 */
public class ContenidoTabSuperiorCategoriaBebidas extends Fragment{

	private static HandlerDB sql, sqlPedido;
	private static SQLiteDatabase db, dbPedido;
	
	private static String tipoTab, restaurante;
	
	private static Activity activity;
	
	private static GridView gridViewBebidas;
	private static MiGridViewBebidasAdapter adapterGridViewBebidas;
    private static ArrayList<PadreGridViewBebidas> bebidas;
    private static double total;
    
    private static View vistaTabCategoriaBebida;
    
    public static void setRestaurante(String res){
    	restaurante = res;
    }
    
    public static String getRestaurante(){
    	return restaurante;
    }
    
    public static void setTipoTab(String tipo){
    	tipoTab = tipo;
    }
    
    public static double getTotal(){
		return Math.rint(total*100)/100;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vistaTabCategoriaBebida = inflater.inflate(R.layout.tab_superior_categoria_bebidas, container, false);
			
		activity = getActivity();
		
		/*
		 * Este if se encarga de que el usaurio no pierda las bebidas una vez haya ya entrado
		 * en la pestaña bebidas e incluso haber seleccionado alguna. Si entra por el if 
		 * quiere decir que no ha seleccionado ninguna bebida o bien ha sincronizado ya
		 * las bebidas que quería.
		 * 
		 * Sirve para reiniciar las unidades de las bebidas a 0.
		 */
		if(bebidas == null){
			// Cargamos las bebidas que haya en la base de datos
			cargarBebidas();
			total = 0;
			
			// Precargamos la pantalla bebida si hubiera ya seleccionado bebidas en pedido
			hayBebidasEnPedido();
		}
		
		// Aplicamos el adapater que hemos creado sobre el gridView
		aplicarAdapter();
   
    	return vistaTabCategoriaBebida;
	}
    
	// Importamos la base de datos de los restaurantes
    public static void importarBaseDatos() {
        try{
     	   sql = new HandlerDB(activity); 
     	   db = sql.open();
         }catch(SQLiteException e){
         	Toast.makeText(activity,"ERROR AL ABRIR LA BD DE PLATOS EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
         }
	}

	public static void cargarBebidas(){
		try{
			// Creamos el arrayList de bebidas
			bebidas = new ArrayList<PadreGridViewBebidas>();
			PadreGridViewBebidas bebida;
			
			// Importamos la base de datos
			importarBaseDatos();
			
    		String[] camposSacar = new String[]{"Id","Nombre","Foto","Precio"};
	    	String[] datosQueCondicionan = new String[]{restaurante,tipoTab};
    		Cursor cP = db.query("Restaurantes", camposSacar, "Restaurante=? AND Categoria=?",datosQueCondicionan,null, null,null);
	    	
    	    // Recorremos todos los registros
    	    while(cP.moveToNext()){
    	    	bebida = new PadreGridViewBebidas(cP.getString(0),cP.getString(1),cP.getString(2),cP.getDouble(3));
    	    	bebidas.add(bebida);
    	    }
    	    
    	    // Cerramos la base de datos de los platos
    	 	sql.close();
	    }catch(SQLiteException e){
	        Toast.makeText(activity,"NO EXISTEN DATOS DEL RESTAURANTE SELECCIONADO",Toast.LENGTH_SHORT).show();	
	    }   
    }
	
	public void aplicarAdapter(){
		//Mostramos el total gastado en bebidas en su textview
		TextView textViewBebidas = (TextView) vistaTabCategoriaBebida.findViewById(R.id.textViewTotalPagarBebidas);
		textViewBebidas.setText(getTotal() + " €");
		
		// Creamos el adapater de la lista que mostrará la cuenta
		gridViewBebidas = (GridView) vistaTabCategoriaBebida.findViewById(R.id.gridViewBebidas);
        adapterGridViewBebidas = new MiGridViewBebidasAdapter(this.getActivity().getApplicationContext(), bebidas);
        gridViewBebidas.setAdapter(adapterGridViewBebidas);
	}	
	
	public static void anyadirBebida(int pos){
		try{
			// Abrimos la base de datos de pedido
			sqlPedido = new HandlerDB(vistaTabCategoriaBebida.getContext(),"Pedido.db"); 
	     	dbPedido = sqlPedido.open();
	     	
	     	// Cargamos la info del plato
		    PadreGridViewBebidas bebida = bebidas.get(pos);
	     	
		    // Metemos la info de la bebida en la base de datos de pedido
			ContentValues plato = new ContentValues();
	    	plato.put("Restaurante", restaurante);
	    	plato.put("Id", bebida.getIdPlato());
	    	plato.put("Plato", bebida.getNombre());
	    	plato.put("Observaciones", (String) null);
	    	plato.put("Extras", (String) null);
	    	plato.put("PrecioPlato", bebida.getPrecioUnidad());
	    	plato.put("IdHijo", DescripcionPlato.getIdentificadorUnicoHijoPedido() + "");
	    	dbPedido.insert("Pedido", null, plato);
	    	
	    	// Aumentamos el identificador único de pedido
	    	DescripcionPlato.sumaIdentificadorUnicoHijoPedido();
	    		    	
	    	// Cerramos la base de datos de pedido
			sql.close();
			
			// Aumentamos el precio total de bebidas y el número de unidades de bebida
			bebidas.get(pos).anyadeUnidad();
			sumarTotal(pos);
			
	    }catch(SQLiteException e){
	         Toast.makeText(vistaTabCategoriaBebida.getContext(),"ERROR AL ABRIR LA BD DE PEDIDO A LA HORA DE INSERTAR UNA BEBIDA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
	    }
	}
	
	public static void eliminarBebida(int pos){
		if(bebidas.get(pos).getUnidades() > 0){
			try{
				// Abrimos la base de datos de pedido
				sqlPedido = new HandlerDB(vistaTabCategoriaBebida.getContext(),"Pedido.db"); 
			 	dbPedido = sqlPedido.open();
			 	
			 	// Cargamos la info del plato
			    PadreGridViewBebidas bebida = bebidas.get(pos);
			 	String[] camposSacar = new String[]{"IdHijo"};
				String[] datosQueCondicionan = new String[]{restaurante,bebida.getIdPlato()};
				Cursor cP = dbPedido.query("Pedido", camposSacar, "Restaurante=? AND Id=?",datosQueCondicionan,null, null,null);
		
				// Miramos a ver si existe algun elemento, debe exisitir
			    if(cP.moveToNext()){
			    	String idBebida = cP.getString(0);
			    	String[] camposDelete = {bebida.getIdPlato(),idBebida};
					dbPedido.delete("Pedido", "Id = ? AND IdHijo =?", camposDelete);
			    }
				
				sql.close();
				// Disminuimos el precio total de bebidas y el número de unidades de esa bebida
				restarTotal(pos);
				bebidas.get(pos).eliminaUnidad();
		    }catch(SQLiteException e){
		         	Toast.makeText(vistaTabCategoriaBebida.getContext(),"ERROR AL ABRIR LA BD DE PEDIDO A LA HORA DE ELIMINAR UNA BEBIDA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
		    }
		}
	}
	
	public static void actualizaGridView(){
		// Actualizamos el total gastado en bebidas en su textview
		TextView textViewBebidas = (TextView) vistaTabCategoriaBebida.findViewById(R.id.textViewTotalPagarBebidas);
		textViewBebidas.setText(getTotal() + " €");
		
		// Actualizamos el adapter		
		adapterGridViewBebidas.notifyDataSetChanged();
	}
	
	public static void sumarTotal(int pos){
		total += bebidas.get(pos).getPrecioUnidad();
	}
	
	public static void restarTotal(int pos){
		if(total > 0){
			total -= bebidas.get(pos).getPrecioUnidad();
		}
	}
	
	/*
	 * Metodo encargado de reiniciar los contadores de bebidas seleccionadas y el precio total.
	 * Este método se llamará en el momento en que se produzca la sincronización, mientras
	 * tanto las undiades de bebidas que hemos seleccionado permanecerán intactas.
	 */
	public static void reiniciarPantallaBebidas(){
		bebidas = null;
		total = 0;
					
		// Cargamos las bebidas que haya en la base de datos
		cargarBebidas();
	}
	
	public static void reiniciarPantallaBebidasCambioRestaurante(){
		bebidas = null;
		total = 0;
	}
	
	public static void hayBebidasEnPedido(){
		boolean encontrado;
		int numBebidas = bebidas.size();
		int i;
		try{
			// Abrimos la base de datos de pedido
			sqlPedido = new HandlerDB(activity,"Pedido.db"); 
		 	dbPedido = sqlPedido.open();
		 	
		 	// Cargamos los id de todos los platos que hay por si hubiera alguna bebida
		 	String[] camposSacarPedido = new String[]{"Id"};
			String[] datosQueCondicionanPedido = new String[]{restaurante};
			Cursor cP = dbPedido.query("Pedido", camposSacarPedido, "Restaurante=?", datosQueCondicionanPedido,null, null,null);
	
			// Abrimos la base de datos de los platos
	    	sql = new HandlerDB(activity); 
	     	db = sql.open();
	     	
			// Miramos para cada plato si es bebida
		    while(cP.moveToNext()){		     	
		     	String[] camposSacarPlato = new String[]{"Id","Nombre","Precio"};
		    	String[] datosQueCondicionanPlato = new String[]{cP.getString(0),restaurante,tipoTab};
	    		Cursor cPlato = db.query("Restaurantes", camposSacarPlato, "Id=? AND Restaurante=? AND Categoria=?",datosQueCondicionanPlato,null, null,null);
	    		
	    		// Miramos si efectivamente era una bebida
	    		if(cPlato.moveToNext()){
	    			// Miramos en que posición está la bebida para sumarle unidades
	    			encontrado = false;
	    			i = 0;
	    			while(!encontrado && i<numBebidas){
	    				if(bebidas.get(i).getIdPlato().equals(cPlato.getString(0))){
	    					encontrado = true;
	    				}else{
	    					i++;
	    				}
	    			}
	    			
	    			// Salimos con la pos en dónde esta la bebida y actualizamos la misma
	    			bebidas.get(i).anyadeUnidad();
	    			sumarTotal(i);
	    			
	    		} // Fin if es bebida
		    } // Fin platos cuenta
		    
		    // Cerramos las bases de datos de pedido y de platos
		    sql.close();
		    sqlPedido.close();
		    
	    }catch(SQLiteException e){
	         	Toast.makeText(activity,"ERROR AL INTENTAR CARGAR BEBIDAS DE LA BD CUENTA EN LA PANTALLA BEBIDAS",Toast.LENGTH_SHORT).show();
	    }
	}
	
	public static void eliminarBebidaDesdePedido(String idPlato){
		if(bebidas == null){
			// Cargamos las bebidas que haya en la base de datos
			cargarBebidas();
			total = 0;
			
			// Precargamos la pantalla bebida si hubiera ya seleccionado bebidas en pedido
			hayBebidasEnPedido();
		}
		
		boolean encontrado = false;
		int numBebidas = bebidas.size();
		int i = 0;
		
		// Buscamos la bebida para quitarle una unidad
		while(i<numBebidas && !encontrado){
			if(bebidas.get(i).getIdPlato().equals(idPlato)){
				encontrado = true;
			}else{
				i++;
			}
		}
		
		// Una vez encontrado le quitamos una unidad para que se vea luego reflejado
		bebidas.get(i).eliminaUnidad();
		
		// Actualizamos el total gastado en bebidas
		restarTotal(i);
	}
	
}
