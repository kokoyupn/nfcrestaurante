package com.example.nfcook_camarero;


import java.util.ArrayList;

import adapters.MiCursorAdapterBuscadorPlatos;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AnadirPlatos extends Activity{
	
	/*Atributos estaticos para poder tener acceso a ellos en los metodos estaticos de la clase y asi
	 * poder actualizar la lista desde otras clases*/
	private static MiExpandableListAdapterAnadirPlato  adapterExpandableListAnadirPlato;
	private static ExpandableListView expandableListAnadirPlato;
	private HandlerGenerico sqlMiBase, sqlBuscador;
	private AutoCompleteTextView buscador;
	private SQLiteDatabase dbMiBase, dbBuscador;
	//private ArrayList<InfoPlato> platosAñadidos; //aqui vaos guardando los platos que ha añadido para luego pasarselos a la pantalla de Mesa 
	//cuando añade un plato se añade a la base de datos de mesas

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.expandable_list_anadir_plato); 
        cargarBarraDeBusqueda();
        crearExpandableList();
    }
		
	
	public void crearExpandableList() {	  
	
		ArrayList<PadreExpandableListAnadirPlato> padres = new ArrayList<PadreExpandableListAnadirPlato>();
		//De momento leer todos los platos para probar de MiBase.db
		
		 //abrimos la base de datos MiBase.db
        try{
			sqlMiBase=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
			dbMiBase= sqlMiBase.open();
		}catch(SQLiteException e){
			System.out.println("CATCH");
			Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		}
      //Sacamos el TipoPlato de la base de datos MiBase.db. Seran los padres
    	String[] infoTipoPlato = new String[]{"TipoPlato"};
    	//solo leemos los platos de Foster
    	String[] datos = new String[]{"Foster"};
   		Cursor cPMiBase = dbMiBase.query("Restaurantes", infoTipoPlato, "Restaurante=?" ,datos,null, null,null);
   		
   		ArrayList<String> tipoSinRepe = new ArrayList<String>();//arrayList para meter los tipos sin repeticion
   		while(cPMiBase.moveToNext()){
   			String tipoPlato = cPMiBase.getString(0);
   			if(!tipoSinRepe.contains(tipoPlato)){
   				tipoSinRepe.add(tipoPlato);
	   			//Sacamos los platos con tipoPlato=al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoPlato = new String[]{"Id","Foto","Nombre","Precio"};
	   	    	String[] info = new String[]{tipoPlato,"Foster"};
	   	   		Cursor cPMiBase2 = dbMiBase.query("Restaurantes", infoPlato, "TipoPlato=? AND Restaurante=?",info,null, null,null);
	   	   		
	   	   		
	   	   		ArrayList<String> idHijos= new ArrayList<String>();
	   	   		ArrayList<String> numImags= new ArrayList<String>();
	   	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   	   		ArrayList<Float> precio= new ArrayList<Float>();
	   	   		while(cPMiBase2.moveToNext() ){
	   	   			idHijos.add(cPMiBase2.getString(0));
	   	   			numImags.add(cPMiBase2.getString(1));
	   	   			nombrePlatos.add(cPMiBase2.getString(2));
	   	   			precio.add((float)cPMiBase2.getInt(3));
	   	   		}
   			
	   	   		HijoExpandableListAnadirPlato unHijo = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos,precio);
	   	   		PadreExpandableListAnadirPlato unPadre = new PadreExpandableListAnadirPlato(tipoPlato, unHijo);
	   	   		padres.add(unPadre);
   			}//fin de esta
   		}
   		expandableListAnadirPlato = (ExpandableListView) findViewById(R.id.expandableListPlatos);
		adapterExpandableListAnadirPlato = new MiExpandableListAdapterAnadirPlato(getApplicationContext(), padres);
		expandableListAnadirPlato.setAdapter(adapterExpandableListAnadirPlato);
		
	}
	
	
	 public void cargarBarraDeBusqueda(){
		 try{
				sqlBuscador=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
				dbBuscador= sqlBuscador.open();
			}catch(SQLiteException e){
				System.out.println("CATCH");
				Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
			}
		 
		 
		 
			buscador = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewBuscadorPlatos); 
		    Cursor c =  dbBuscador.rawQuery("SELECT Id AS _id, nombre AS item" + 
		      " FROM Restaurantes" + 
		      " WHERE Restaurante ='"+ "foster" +"' and nombre LIKE '%" +""+ "%' ", null);
			
			buscador.setAdapter(new MiCursorAdapterBuscadorPlatos(getApplicationContext(), c, CursorAdapter.NO_SELECTION, "Foster"));
			buscador.setThreshold(2);
			
			buscador.setOnItemClickListener(new OnItemClickListener() {
		
				   public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
					   //Es lo que vamos a mostrar en la barra de busqueda una vez pinchada una sugerencia.
					   Cursor c = (Cursor) arg0.getAdapter().getItem(position);
					   buscador.setText("");
					   Intent intent = new Intent(getApplicationContext(),MainActivity.class);
					   startActivity(intent);
				    }
				      
				 });
	    }
	
	
	
	
}
 