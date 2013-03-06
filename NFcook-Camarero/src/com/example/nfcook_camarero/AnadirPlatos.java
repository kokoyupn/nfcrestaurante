package com.example.nfcook_camarero;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class AnadirPlatos extends Activity{
	
	/*Atributos estaticos para poder tener acceso a ellos en los metodos estaticos de la clase y asi
	 * poder actualizar la lista desde otras clases*/
	//private static MiExpandableListAdapterPedido  adapterExpandableListPedido;
	private static MiExpandableListAdapterAnadirPlato  adapterExpandableListAnadirPlato;
	private static ExpandableListView expandableListAnadirPlato;
	private static Context context;
	private static View vistaConExpandaleList;
	private HandlerGenerico sqlMiBase;
	private SQLiteDatabase dbMiBase;
	private String numeroMesa;
	//private ArrayList<InfoPlato> platosAñadidos; //aqui vaos guardando los platos que ha añadido para luego pasarselos a la pantalla de Mesa 
	//cuando añade un plato se añade a la base de datos de mesas

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.expandable_list_anadir_plato); 
        crearExpandableList();
        
     // Obtenemos el numro de mesa de la pantalla anterior
//        Bundle bundle = getIntent().getExtras();
//        numeroMesa = bundle.getString("numMesa");
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
    	String[] infoMesa2 = new String[]{"TipoPlato"};
   		Cursor cPMiBase = dbMiBase.query("Restaurantes", infoMesa2, null ,null,null, null,null);
   		
   		ArrayList<String> tipoSinRepe = new ArrayList<String>();//arrayList para meter los tipos sin repeticion
   		while(cPMiBase.moveToNext()){
   			String tipoPlato = cPMiBase.getString(0);
   			if(!tipoSinRepe.contains(tipoPlato)){
   				tipoSinRepe.add(tipoPlato);
	   			//Sacamos os platos con tipoPlato=al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoMesa = new String[]{"Id","Foto","Nombre"};
	   	    	String[] info = new String[]{tipoPlato};
	   	   		Cursor cPMiBase2 = dbMiBase.query("Restaurantes", infoMesa, "TipoPlato=?" ,info,null, null,null);
	   	   		
	   	   		
	   	   		ArrayList<String> idHijos= new ArrayList<String>();
	   	   		ArrayList<Integer> numImags= new ArrayList<Integer>();
	   	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   	   		int aux=0;//capado para las pruebas
	   	   		while(cPMiBase2.moveToNext() && aux < 6){
	   	   			idHijos.add(cPMiBase2.getString(0));
	   	   			numImags.add(Integer.parseInt(cPMiBase2.getString(1)));
	   	   			nombrePlatos.add(cPMiBase2.getString(2));
	   	   			aux++;
	   	   		}
   			
	   	   		HijoExpandableListAnadirPlato unHijo = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos);
	   	   		PadreExpandableListAnadirPlato unPadre = new PadreExpandableListAnadirPlato(tipoPlato, unHijo);
	   	   		padres.add(unPadre);
   			}//fin de esta
   		}
   		expandableListAnadirPlato = (ExpandableListView) findViewById(R.id.expandableListPlatos);
		adapterExpandableListAnadirPlato = new MiExpandableListAdapterAnadirPlato(getApplicationContext(), padres);
		expandableListAnadirPlato.setAdapter(adapterExpandableListAnadirPlato);
		
	}
}
