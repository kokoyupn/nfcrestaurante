package fragments;

import java.util.ArrayList;

import baseDatos.HandlerDB;

import com.example.nfcook.R;

import adapters.MiListCuentaAdapter;
import adapters.PadreListCuenta;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CuentaFragment extends Fragment{
	private View vista;
	private double total;
	private String restaurante;
	
	private HandlerDB sqlCuenta;
	private SQLiteDatabase dbCuenta;
	
	private ArrayList<PadreListCuenta> cuenta;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.cuenta, container, false);
		
		// Cargamos la estructura desde la que mostraremos en la listview
		cargarCuenta();
		// Creamos la listview y le aplicamos el adpater
		crearListView();

		return vista;
    }
	
	public void crearListView(){
		ListView listaCuenta =  (ListView) vista.findViewById(R.id.listViewCuenta);
		
		// Creamos el adapater de la lista que mostrará la cuenta
		MiListCuentaAdapter adapaterListaCuenta = new MiListCuentaAdapter(this.getActivity().getApplicationContext(), cuenta);
		listaCuenta.setAdapter(adapaterListaCuenta);
		
		// Oyente de la lista
		listaCuenta.setOnItemClickListener(new ListView.OnItemClickListener()
    	{
			/*
			 * TODO Queda pendiente si aplicamos alguna función en el oyente de la lista o se deja así.
			 * (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
				// Simplemente nos mostrará un mensaje con el precio unitario del producto
				 Toast.makeText(getActivity().getApplicationContext(),"El precio unitario del producto es: " + cuenta.get(arg2).getPrecioUnidad() + " €",Toast.LENGTH_SHORT).show();	
			}
    	});	
	}
	
	public void cargarCuenta(){
		// Importamos la base de datos
        sqlCuenta  =new HandlerDB(this.getActivity().getApplicationContext(),"Cuenta.db"); 
     	dbCuenta = sqlCuenta.open();
     	
     	// Creamos la estructura cuenta
     	cuenta = new ArrayList<PadreListCuenta>();
     	
     	// Cargamos los datos de la bd
     	try{
			String[] campos = new String[]{"Plato, PrecioPlato"};//Campos que quieres recuperar
			String[] datos = new String[]{restaurante};	    	
			Cursor c = dbCuenta.query("Cuenta", campos, "Restaurante=?", datos,null, null,null);
	    	int i, numPlatos = 0;
	    	boolean anadido;
	    	PadreListCuenta padre;
	    	
	    	while(c.moveToNext()){
	    		i = 0;
	    		anadido = false;
	    		// Miramos si el plato ya está añadido
	    		while(i<numPlatos && !anadido){
	    			// Si está añadido, actualizamos el precio y la cantidad
	    			if (cuenta.get(i).getPlato().equals(c.getString(0))){
	    				anadido = true;
	    				cuenta.get(i).actualizaPrecioTotal(c.getDouble(1));
	    				cuenta.get(i).actualizaCantidad();
	    			}else{
	    				i++;
	    			}
	    		}
	    		
	    		// Si no estaba añadido, lo añadimos
	    		if(!anadido){
	    			padre = new PadreListCuenta(c.getString(0), c.getDouble(1));
	    			cuenta.add(padre);
	    			numPlatos++;
	    		}
	    		
	    		// Aumentamos el total de la cuenta
	    		total += c.getDouble(1);
	    	}
	    	
	    	// Cerramos la base de datos
	    	sqlCuenta.close();
	    	
	    	// Mostramos el total de la cuenta
	    	TextView totalCuenta = (TextView)vista.findViewById(R.id.textViewTotalCuenta);
	    	totalCuenta.setText(getTotal() + " €");
	    	
	    }catch(SQLiteException e){
	        Toast.makeText(getActivity().getApplicationContext(),"ERROR EN LA BASE DE DATOS CUENTA",Toast.LENGTH_SHORT).show();	
	    }   
	}
	
	public double getTotal() {
		return Math.rint(total*100)/100;
	}
	
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
	
}
