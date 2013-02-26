package com.example.nfcook_camarero;

import java.util.ArrayList;




import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


//PRUEBA-----------------------------
public class VentanaMesa {
	private View vista;
	//private View v;//La vista de la que vienes, para coger la base de datos de ahi
	private int numMesa;
	private SQLiteDatabase dbc;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.ventana_mesa, container, false);
		
		TextView mesa = (TextView)vista.findViewById(R.id.numMesa);
		mesa.setText("Numero de Mesa: "+"5"/*Se pondra con un set desde fuera*/);
		mesa.setTextSize(20);
		
		//No se si es asi:
		try{
			//sqlc=new HandlerCamarero(getApplicationContext());
			//System.out.println("BIEN HECHO LO DEL GETCONTEXT");
			dbc= SQLiteDatabase.openDatabase("/data/data/com.example.nfcook/databases/Pedido.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
		}catch(Exception e){
			System.out.println("CATCH");
		}
		if(dbc!=null){
			System.out.println("BIEN!!!!");
			dbc.execSQL("CREATE TABLE Pedido(NumeroMesa TEXT,Nombre TEXT,Observaciones TEXT,Extras TEXT,Precio INTEGER)");
		}
		
	
  	    
  	    //Meter registros base de datos
  	    ContentValues a = new ContentValues();
	   	a.put("NumeroMesa", 5);
	   	a.put("Nombre", "Bacon & Cheese Fries");
	   	a.put("Observaciones", "Observaciones");
	   	a.put("Extras", "Extras");
	   	a.put("Precio", 8.35);
	   
	   	dbc.insert("Pedido", null, a);

  	    //----------------------------------
  	    

  	    
  	  	String[] campos = new String[]{"Nombre","Observaciones","Extras","Precio"};
  	  	String[] nmesa = new String[]{"numMesa"};
  	    Cursor c = dbc.query("Pedido", campos, "NumeroMesa=5",nmesa,null, null,null);
  	    
        /*
		TableLayout tableLayout = (TableLayout)vista.findViewById(R.id.tabla);
        
		float precioAcumulado=0;
		
		for(int i=0;i<c.getCount();i++){
			TableRow fila = (TableRow)inflater.inflate(R.layout.mesarow,tableLayout,false);
       
			CheckBox cb=(CheckBox)fila.findViewById(R.id.CheckBox1);
        
			TextView nombre = (TextView)fila.findViewById(R.id.nombre);
			nombre.setText(c.getString(2));
			nombre.setTextSize(14);
        
			TextView observaciones = (TextView)fila.findViewById(R.id.observaciones);
			observaciones.setText(c.getString(4));
			observaciones.setTextSize(14);
			
			TextView extras = (TextView)fila.findViewById(R.id.extras);
			extras.setText(c.getString(5));
			extras.setTextSize(14);
			
			TextView precio = (TextView)fila.findViewById(R.id.precioCada);
			precio.setText(c.getString(6));
			precioAcumulado=precioAcumulado+(c.getFloat(6));
			precio.setTextSize(14);
        
			Button editar=(Button)fila.findViewById(R.id.editar);
        
			tableLayout.addView(fila);
		}
        
		TextView precioTotal = (TextView)vista.findViewById(R.id.precioTodo);
		precioTotal.setText("Precio");
		precioTotal.setTextSize(14);
		
		TextView valorPrecio = (TextView)vista.findViewById(R.id.valorPrecio);
		valorPrecio.setText(Float.toString(precioAcumulado));
		valorPrecio.setTextSize(14);
		
		Button cobrar=(Button)vista.findViewById(R.id.button1);
		Button aniadirBebida=(Button)vista.findViewById(R.id.button2);
		Button aniadirPlato=(Button)vista.findViewById(R.id.button3);
		Button eliminar=(Button)vista.findViewById(R.id.button4);
		
        */

		return vista;
    }
	
	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setParametros(int nM, View vista){
		numMesa=nM;
		//v=vista;
		
	}
}


