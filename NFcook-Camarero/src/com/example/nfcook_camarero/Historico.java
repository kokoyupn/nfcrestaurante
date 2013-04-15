package com.example.nfcook_camarero;
 import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;  
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;  
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListAdapter;  
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;  
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
 public class Historico extends Activity {  
   private static final String MESA = "MESA";  
   private static final String PRECIO = "PRECIO";
   private static final String CAMARERO = "CAMARERO";
   private static final String HORA = "HORA";
   private static final String HORACOMPLETA = "HORACOMPLETA";
   
   private HandlerGenerico sql;
   private SQLiteDatabase db;
	
   private static ExpandableListView lista;
   private ExpandableListAdapter mAdapter;  
   
   private Map<String, String> padreActual;
   private List<Map<String, String>> hijo;
   private Map<String, String> hijoActual;
   private String ultimaMesaLeida="";
   private int precioMesa=0;
   private int precioPedido=0;
   private String ultimaHoraPedido="";
   private String camareroAnterior= "";
   private boolean nuevaMesa;
   
   @Override  
   public void onCreate(Bundle savedInstanceState) {  
     super.onCreate(savedInstanceState);  
     
     lista=new ExpandableListView(this);
     setContentView(lista);
     importarBaseDatatos();
     //String[] campos = new String[]{"NumMesa","IdCamarero","IdPlato","Observaciones","Extras","FechaHora","NombrePlato"};
 	 
 	 Cursor c = db.query("Historico", null,null,null,null,null,"NumMesa",null);
          
     final List<Map<String, String>> listaPadres = new ArrayList<Map<String, String>>();  
     final List<List<Map<String, String>>> listaHijos = new ArrayList<List<Map<String, String>>>();  
     
     
     while(c.moveToNext()){
       if (!c.getString(4).equals(ultimaMesaLeida)){
    	   ultimaMesaLeida=c.getString(4);
    	   precioMesa+=precioPedido;
    	   if (padreActual!=null){
    		   padreActual.put(PRECIO,  String.valueOf(precioMesa) + " €"); 
    		   listaHijos.add(hijo); 
    	   }
    	   padreActual = new HashMap<String, String>();  
           listaPadres.add(padreActual);  
           padreActual.put(MESA, "Mesa " + ultimaMesaLeida);
           precioMesa=0;
           hijo = new ArrayList<Map<String, String>>();  
           ultimaHoraPedido="";
           nuevaMesa=true;

       }
       
       if (!c.getString(1).equals(ultimaHoraPedido)){
    	  
    	   if (hijoActual!=null){
    		   hijoActual.put(CAMARERO, "Camarero: "+ camareroAnterior);  
    		   hijoActual.put(PRECIO, "Precio: " + precioPedido + " €");  
    	   }
    	   if (nuevaMesa){
    		   nuevaMesa=false;
    		   precioPedido=0;
    	   }
    	   camareroAnterior=c.getString(3);
    	   precioMesa+=precioPedido;
    	   precioPedido=0;
    	   ultimaHoraPedido=c.getString(1);
    	   hijoActual = new HashMap<String, String>();  
    	   hijo.add(hijoActual);  
    	   hijoActual.put(HORA, "Hora: " + ultimaHoraPedido.substring(ultimaHoraPedido.indexOf(" ")+1));  
    	   hijoActual.put(HORACOMPLETA, ultimaHoraPedido);  
    	    
       }  
       precioPedido+=c.getInt(8);
     }
     
     try{
    	 listaHijos.add(hijo); 
         hijoActual.put(CAMARERO, "Camarero: "+ camareroAnterior);
         hijoActual.put(PRECIO, "Precio: " + precioPedido + " €");
         precioMesa+=precioPedido;
         padreActual.put(PRECIO,  String.valueOf(precioMesa) + " €");
     }
    catch (Exception e){};
    
     mAdapter = new SimpleExpandableListAdapter(  
         this,  
         listaPadres,  
         R.layout.padres_historico,  
         new String[] { MESA,PRECIO },  
         new int[] { R.id.NumMesa, R.id.preciomesa },  
         listaHijos,  
         R.layout.hijos_historico,  
         new String[] { HORA, CAMARERO, PRECIO },  
         new int[] { R.id.hora, R.id.camarero, R.id.precio}
         );  
     lista.setAdapter(mAdapter);
     lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
		

		public boolean onChildClick(ExpandableListView arg0, View arg1,
				int arg2, int arg3, long arg4) {
		   	  Intent intent = new Intent(arg1.getContext(), PedidoHistorico.class);
		   	  
		   	  intent.putExtra("mesa", listaPadres.get(arg2).get(MESA).substring(5));
		   	  intent.putExtra("hora", listaHijos.get(arg2).get(arg3).get(HORACOMPLETA));
		   	  
	       	  startActivity(intent); 
			return false;
		}
	});
   
     
   }    
   
 
 
 private void importarBaseDatatos(){
     try{
     	sql = new HandlerGenerico(getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Historico.db"); 
     	db = sql.open();
     }catch(SQLiteException e){
      	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
     }	
	}
 }


