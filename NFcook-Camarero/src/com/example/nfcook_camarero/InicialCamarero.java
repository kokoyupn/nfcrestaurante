package com.example.nfcook_camarero;

 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;


public class InicialCamarero extends Activity{
	private GridView gridviewCam;
	private InicialCamareroAdapter adapterCam;
    private ArrayList<MesaView> mesas;
    private String idCamarero;
    private String nombre;
    private int precio;
    
    private ArrayList<InfoPlato> datos; //Lo que nos llega del chip
    
    private HandlerGenerico sqlMesas, sqlMiBase;
	private SQLiteDatabase dbMesas, dbMiBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicial_camarero);  
        mesas = new ArrayList<MesaView>();
        gridviewCam = (GridView) findViewById(R.id.gridViewInicial); 
        // Obtenemos el idCamarero de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        idCamarero = bundle.getString("usuario");
        
        //Creamos un datos (lo que nos llega del chip) para probarlo
       ArrayList<String> extras = new ArrayList<String>();
       extras.add("salsa barbacoa");
       extras.add("poco hecha");
       InfoPlato info = new InfoPlato();
       info.setExtras(extras);
       info.setObservaciones("Sin pepinillo");
       info.setIdPlato("V10");
       
       ArrayList<String> extras2 = new ArrayList<String>();
       extras2.add("salsa ranchera");
       extras2.add("poco hecha");
       extras2.add("guacamole");
       InfoPlato info2 = new InfoPlato();
       info2.setExtras(extras2); 
       info2.setObservaciones("Sin sal");
       info2.setIdPlato("V14");
       
       datos = new  ArrayList<InfoPlato>(); 
       datos.add(info);
       datos.add(info2);
       //fin de creacion de datos
       
      //Para importar la base de Assets
        try{
			sqlMesas = new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db" );
			
			dbMesas= sqlMesas.open();
			}catch(SQLiteException e){
			System.out.println("CATCH");
			Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		}
        try{
        	String[] infoMesa = new String[]{"NumMesa","Personas"};
        	Cursor cPMesas = dbMesas.query("Mesas", infoMesa, null,null,null, null,null);
    	
        	//a�adir mesas que ya tiene
        	while(cPMesas.moveToNext()){
        		MesaView mesa1= new MesaView(this);
        		mesa1.setNumMesa(cPMesas.getString(0));
        		mesa1.setNumPersonas(cPMesas.getString(1));
        		if(!existeMesa(mesa1))
        			mesas.add(mesa1);
        	}
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"ERROR BASE DE DATOS -> MESAS",Toast.LENGTH_SHORT).show();	
        }

        adapterCam= new InicialCamareroAdapter(this,R.layout.imagen_mesa, mesas);
        gridviewCam.setAdapter(adapterCam);

        gridviewCam.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	//nos llevara a la pantalla siguiente          	
            	MesaView pulsada= mesas.get(position);
            	dbMesas.close();
            	Intent intent = new Intent(InicialCamarero.this,Mesa.class);//ir a VentanaMesa
        		intent.putExtra("NumMesa", pulsada.getNumMesa());
        		Log.i("numMesa", pulsada.getNumMesa());
        		startActivity(intent);
                }
        });
        
        // onLongClick bot�n a�adir
        Button botonAniadir = (Button) findViewById(R.id.aniadirMesa);
        botonAniadir.setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                onAniadirClick(v);
                return true;
            }
        });   
       
        
        
		}//fin del oncreate
	
    public boolean existeMesa(MesaView mesa){
    	boolean enc = false;
    	int i=0;
    	while(!enc && i<mesas.size()){
    		if(mesas.get(i).getNumMesa().equals(mesa.getNumMesa()))
    			enc = true;
    		i++;
    	}
    	return enc;
    }
 
	
        public void onAniadirClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
            alert.setMessage("Introduce el n�mero de la mesa: "); //mensaje
            final EditText input = new EditText(InicialCamarero.this); //creas un Edit Text
            int maxLength = 10; //si quieres ponerle caracteristicas al EditText
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            input.setFilters(FilterArray);      //por ejemplo maximo 10 caracteres
            alert.setView(input); //a�ades el edit text a la vista del AlertDialog
            
            alert.setPositiveButton("Cancelar", null);
            alert.setNegativeButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
            	@SuppressLint("SimpleDateFormat")
				public void onClick(DialogInterface dialog, int whichButton) {
            		Editable value =input.getText();
            		if(value.toString().equals("")){
            			Toast.makeText(InicialCamarero.this, "Introduce la mesa", Toast.LENGTH_LONG).show();           			
            		}else{
            			//Creo una mesa aux para buscarla en la base de datos para ver si ya existe previamente
            			MesaView mesaBuscarRepe = new MesaView(getApplicationContext());
            			mesaBuscarRepe.setNumMesa(""+value);
            			if (existeMesa(mesaBuscarRepe)){// si ya existe la mesa no la metemos y sacamos un mensaje
            				Toast.makeText(InicialCamarero.this, "Esa mesa ya est� sendo atendida", Toast.LENGTH_LONG).show();
            			}else{
	            			gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
	                    	MesaView mesa2 = new MesaView(InicialCamarero.this);
	                    	mesa2.setNumMesa("" + value);	
	                    	mesas.add(mesa2);
	                    	adapterCam = new InicialCamareroAdapter(InicialCamarero.this,R.layout.imagen_mesa, mesas);
	                    	gridviewCam.setAdapter(adapterCam);
	                    	
	                    	//Sacamos la hora a la que el camarero ha introducido la mesa
	                    	Calendar cal = new GregorianCalendar();
	                        Date date = cal.getTime();
	                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	                        String formatteDate = df.format(date);
	                        
	                        Date dt = new Date();
	                        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
	                        String formatteHour = dtf.format(dt.getTime());
	                       
	                        //abrimos la base de datos MiBase.db
	                        try{
	                			sqlMiBase=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
	                			dbMiBase= sqlMiBase.open();
	                		}catch(SQLiteException e){
	                			System.out.println("CATCH");
	                			Toast.makeText(getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
	                		}
	                       	
	                    	//rellenamos la base de datos con lo que nos ha venido del chip que esta en datos
	                    	for(int j = 0; j < datos.size(); j++ ){//concatenamos los extras para guardarlos en string separados por comas
		                    	String extr = "";
		                    	for (int i = 0; i < datos.get(j).getExtras().size(); i++ ){
		                    		extr= extr + datos.get(j).getExtras().get(i) + ",";
		                    	}
		                    	//quitamos la ultima coma
		                    	extr= extr.substring(0,extr.length()-1);
		                    	
		                    	//Sacamos el nombre del plato y el precio de la base de datos MiBase.db
		                    	String[] infoMesa2 = new String[]{"Nombre","Precio"};
		                       	String[] info = new String[]{datos.get(j).getIdPlato()};
		                   		Cursor cPMiBase = dbMiBase.query("Restaurantes", infoMesa2, "Id=?" ,info,null, null,null);
		                    	
		                   		cPMiBase.moveToNext();
		                   		nombre=cPMiBase.getString(0);
		                   		precio=cPMiBase.getInt(1);
		                   		
		                    	//Insertamos el plato en la tabla Platos
		                    	ContentValues registro = new ContentValues();
		                    	registro.put("NumMesa", "" + value);
		                    	registro.put("IdCamarero", idCamarero);
		                    	registro.put("IdPlato", "" + datos.get(j).getIdPlato());
		                    	registro.put("Observaciones",  "" + datos.get(j).getObservaciones());
		                    	registro.put("Extras", extr);
		                    	registro.put("FechaHora", formatteDate + " " + formatteHour);
		                    	registro.put("Nombre", nombre);
		                    	registro.put("Precio", precio);
		                	   
		                	   	dbMesas.insert("Mesas", null, registro);
		                	   
	                    	}
	                    	//Mensaje para elegir el n�mero de personas
            				
            				AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
            	            alert.setMessage("�Cu�ntas personas van a sentarse?: "); //mensaje
            	            final EditText input = new EditText(InicialCamarero.this); //creas un Edit Text
            	            int maxLength = 10; //si quieres ponerle caracteristicas al EditText
            	            InputFilter[] FilterArray = new InputFilter[1];
            	            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            	            input.setFilters(FilterArray);      //por ejemplo maximo 10 caracteres
            	            alert.setView(input); //a�ades el edit text a la vista del AlertDialog
            	            alert.setPositiveButton("Cancelar", null);
            	            alert.setNegativeButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
            	            	@SuppressLint("SimpleDateFormat")
            					public void onClick(DialogInterface dialog, int whichButton) {
            	            		Editable value =input.getText();
            	            		
            	            	}
            	            });
            	            alert.show();
	                    }
            		}
                }
            });
            alert.show();
        }
     
        

	public void onClickEliminarMesa(View v) {
             AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
             alert.setMessage("Introduce el n�mero de la mesa que desea borrar: "); //mensaje
             final EditText input = new EditText(InicialCamarero.this); //creas un Edit Text
             int maxLength = 10; //si quieres ponerle caracteristicas al EditText
             InputFilter[] FilterArray = new InputFilter[1];
             FilterArray[0] = new InputFilter.LengthFilter(maxLength);
             input.setFilters(FilterArray);      //por ejemplo maximo 10 caracteres
             alert.setView(input); //a�ades el edit text a la vista del AlertDialog
             
             alert.setPositiveButton("Cancelar", null);
             alert.setNegativeButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
               	public void onClick(DialogInterface dialog, int whichButton) {
                 		Editable value =input.getText();
                 		gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
                     
                     	String val= ""+value;
                     	//Creo una mesa aux para buscarla en la base de datos para ver si existe
            			MesaView buscaMesa = new MesaView(getApplicationContext());
            			buscaMesa.setNumMesa(""+value);
            			if (!existeMesa(buscaMesa)){// si no existe la mesa mostramos un mensaje
            				Toast.makeText(InicialCamarero.this, "Esa mesa no existe", Toast.LENGTH_LONG).show();
            			}else{//eliminamos la mesa
	                        //Eliminar un registro
	                     	String[] args = new String[]{val};
	                     	dbMesas.execSQL("DELETE FROM Mesas WHERE NumMesa=?", args);
	                        //Lo eliminamos tambien de la lista de mesas	
	                     	Boolean enc = false;
	                     	Iterator<MesaView> it = mesas.iterator();
	                     	while (it.hasNext() && !enc){
	                     		MesaView atratar = it.next();
	                     		if(atratar.getNumMesa().equals(""+value)){
	                     			enc=true;
	                     			mesas.remove(atratar);
	                     		}
	                     	}
	                     	//Refrescamos                  	
	                     	adapterCam = new InicialCamareroAdapter(InicialCamarero.this,R.layout.imagen_mesa, mesas);
	                     	gridviewCam.setAdapter(adapterCam);
	               		}
                     }
                 });
             alert.show();
          }
	
}