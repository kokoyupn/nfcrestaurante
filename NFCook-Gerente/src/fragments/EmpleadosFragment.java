package fragments;

import java.util.ArrayList;

import adapters.MiCursorAdapterBuscadorEmpleados;
import adapters.MiEmpleadosAdapter;
import adapters.PadreListaEmpleados;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.example.nfcook_gerente.InicializarDatosEmpleado;
import com.example.nfcook_gerente.R;


/**
 * 
 * @author roberto
 *
 * Clase con el contenido del tab referente a los empleados.
 * 
 * Contiene un buscador de empleados y una lista con todos ellos, 
 * en la que aparece su foto, nombre, puesto e identificador.
 *
 */

public class EmpleadosFragment extends Fragment{
	
	private View vista;
	
	private HandlerGenerico sqlEmpleados;
	private static SQLiteDatabase dbEmpleados;
	private HandlerGenerico sqlBuscador;
	private SQLiteDatabase dbBuscador;
	
	private AutoCompleteTextView buscador;
		 
	private ListView listaEmpleados;
	private MiEmpleadosAdapter adapterListaEmpleados;
	private ArrayList<PadreListaEmpleados> empleados;

	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		vista = inflater.inflate(R.layout.empleados, container, false);
		
		// Importamos la base de datos con la información de los empleados
		importarBaseDatosEmpleados();
		// Inicializamos el buscador de los camareros
		inicializarBuscadorCamareros();
		// Cargamos la list view que mostrará la información de los empleados
		cargarListViewEmpleados();
		
		return vista;
	}

	private void cargarListViewEmpleados() {
		listaEmpleados = (ListView)vista.findViewById(R.id.listaEmpleados);
		empleados = obtenerElementos();//Devuelve arraylist
		adapterListaEmpleados = new MiEmpleadosAdapter( getActivity(), empleados);
		listaEmpleados.setAdapter(adapterListaEmpleados);
		
		listaEmpleados.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	//nos llevara a la pantalla siguiente          	
            	PadreListaEmpleados pulsado= empleados.get(position);
                //dbMesas.close();
            	Intent intent = new Intent(EmpleadosFragment.this.getActivity(), InicializarDatosEmpleado.class);
            	//Le pasamos a la siguiente pantalla el id del empleado que ha pulsado para luego consultar en la BD
            	intent.putExtra("IdEmpleado",pulsado.getIdEmpleado());
        		//Lanzamos la actividad
        		startActivity(intent);
                }
        });	
	}

	/**
	 * Obtiene los elementos del ArrayList, en funcion de la base de datos y del contenido 
	 * de la mesa actual, que servirá para confeccionar el adapter de la ListView.
	 * 
	 * @return un ArrayList con los empleados.
	 */
	private ArrayList<PadreListaEmpleados> obtenerElementos() {
		
		ArrayList<PadreListaEmpleados> elementos = null;
		try{
			String[] campos = new String[]{"Foto","Nombre","Apellido1","Apellido2","Puesto","IdEmpleado"};
			Cursor c = dbEmpleados.query("Empleados",campos,  null,null, null, null, null);

			elementos = new ArrayList<PadreListaEmpleados>();
			if(c.getCount() > 0){
				do{
					c.moveToNext();
			    	elementos.add(new PadreListaEmpleados(c.getString(0) ,c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5)));
			    	System.out.println(c.getCount());
				}while(!c.isLast());
			}
		}catch(Exception e){
			System.out.println("Error en EmpleadosFragment al cargar los empleados de la base de datos");
		}
		return elementos;
	}

	private void importarBaseDatosEmpleados(){
		try{
			sqlEmpleados = new HandlerGenerico(getActivity().getApplicationContext(),"/data/data/com.example.nfcook_gerente/databases/","Empleados.db"); 
			dbEmpleados = sqlEmpleados.open();
		}catch(SQLiteException e){
			Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
		}	
	}
	
	
	public void inicializarBuscadorCamareros(){
		 try{
				sqlBuscador=new HandlerGenerico(getActivity(), "/data/data/com.example.nfcook_gerente/databases/", "Empleados.db");
				dbBuscador= sqlBuscador.open();
			}catch(SQLiteException e){
				System.out.println("CATCH");
				Toast.makeText(getActivity(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
			}
		 
		 	buscador = (AutoCompleteTextView)vista.findViewById(R.id.autoCompleteTextViewBuscadorEmpleados);
			Cursor c =  dbBuscador.rawQuery("SELECT IdEmpleado AS _id,Nombre AS Item" + 
		    			" FROM Empleados" + 
		    			" WHERE Nombre LIKE '%" + "%' or Apellido1 LIKE '%"+"%'", null);
		
			buscador.setAdapter(new MiCursorAdapterBuscadorEmpleados(getActivity(), c, CursorAdapter.NO_SELECTION));
		    buscador.setThreshold(2);
			
			buscador.setOnItemClickListener(new OnItemClickListener() {
				   public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
					   //Es lo que vamos a mostrar en la barra de busqueda una vez pinchada una sugerencia.
					   Cursor c = (Cursor) arg0.getAdapter().getItem(position);
					   buscador.setText("");
					   String idEmpleado = c.getString(0);
					   
					   //Accedes a otra pantalla con los datos del empleado
					   Intent intent = new Intent(EmpleadosFragment.this.getActivity(), InicializarDatosEmpleado.class);
					   //Le pasamos a la siguiente pantalla el el id del empleado
					   intent.putExtra("IdEmpleado",idEmpleado);					   
					   //Lanzamos la actividad
					   startActivity(intent);
				    }  
				 });
	 }
}
