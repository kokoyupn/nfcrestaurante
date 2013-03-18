package com.example.nfcook_camarero;

 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class InicialCamarero extends Activity{
	private GridView gridviewCam;
	private InicialCamareroAdapter adapterCam;
    private static ArrayList<MesaView> mesas;
    private String idCamarero;
    private String nombre;
    private String numeroMesaAEditar;
    private int precio;
    private static int idUnico = 0;
    
    private ArrayList<InfoPlato> datos; //Lo que nos llega del chip
    
    private HandlerGenerico sqlMesas, sqlMiBase;
	private SQLiteDatabase dbMesas, dbMiBase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Quitamos barra de notificaciones
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.inicial_camarero); 
        //creamos la lista de mesas
        mesas = new ArrayList<MesaView>();
        gridviewCam = (GridView) findViewById(R.id.gridViewInicial); 
        // Obtenemos el idCamarero de la pantalla anterior
        Bundle bundle = getIntent().getExtras();
        idCamarero = bundle.getString("usuario");
        
        //Creamos un datos (lo que nos llega del chip) para probarlo
        ArrayList<String> extras = new ArrayList<String>();
        extras.add("Barbacoa");
       // extras.add("poco hecha");
	    InfoPlato info = new InfoPlato();
	    info.setExtras(extras);
	    info.setObservaciones("Sin pepinillo");
	    info.setIdPlato("fh4");
	   
	    ArrayList<String> extras2 = new ArrayList<String>();
	    extras2.add("Poco hecha");
	    extras2.add("Roquefort");
	    extras2.add("Patata Asada");
	    InfoPlato info2 = new InfoPlato();
	    info2.setExtras(extras2); 
	    info2.setObservaciones("Sin sal");
	    info2.setIdPlato("fh11");
	    
	    InfoPlato info3 = new InfoPlato();
	    info3.setExtras(new ArrayList<String>());
	    info3.setObservaciones("");
	    info3.setIdPlato("fh41");
	    
	    InfoPlato info4 = new InfoPlato();
	    info4.setExtras(new ArrayList<String>());
	    info4.setObservaciones("Sabrosón");
	    info4.setIdPlato("fh42");
	   
	    datos = new  ArrayList<InfoPlato>(); 
	    datos.add(info);
	    datos.add(info2);
	    datos.add(info3);
	    datos.add(info4);
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
        	//Consultamos de la base de datos Mesas lo que necesitamos para representarlo
        	String[] infoMesa = new String[]{"NumMesa","Personas"};
        	Cursor cPMesas = dbMesas.query("Mesas", infoMesa, null,null,null, null,null);
    	
        	//añadir mesas que ya tiene
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

        ordenaMesas();
        //Llamamos al adapter para que muestre en la pantalla los cambios realizados
        adapterCam= new InicialCamareroAdapter(this, mesas);
        gridviewCam.setAdapter(adapterCam);
        //creamos el oyente del gridView
        gridviewCam.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	//nos llevara a la pantalla siguiente          	
            	MesaView pulsada= mesas.get(position);
            //	dbMesas.close();
            	Intent intent = new Intent(InicialCamarero.this,Mesa.class);
            	//Le pasamos a la siguiente pantalla el numero de la mesa que se ha pulsado
        		intent.putExtra("NumMesa", pulsada.getNumMesa());
        		intent.putExtra("IdCamarero",idCamarero);
        		intent.putExtra("Personas", pulsada.getNumPersonas());
        		//Lanzamos la actividad
        		startActivity(intent);
                }
        });
        
     //establecimiento del oyente de dejar pulsada una mesa   
        gridviewCam.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				//Guardamos el número de la mesa pulsada
				numeroMesaAEditar = mesas.get(position).getNumMesa();
				//Preparamos los elementos que tendrá la lista
				final CharSequence[] items = {"Sincronizar", "Editar nº mesa", "Editar nº personas","Eliminar mesa"};

				AlertDialog.Builder ventEmergente = new AlertDialog.Builder(InicialCamarero.this);
				ventEmergente.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if (item == 0){
				    		Toast.makeText(getApplicationContext(), "Disponible Próximamente", Toast.LENGTH_SHORT).show();
				    	//----------------- onClickListener de editar número de mesa --------------------------------
				    	}else if(item == 1){
				    		LayoutInflater factory = LayoutInflater.from(InicialCamarero.this);
				    		final View textEntryView = factory.inflate(R.layout.alert_dialog_edit, null);
				    		final TextView tituloMesa = (TextView) textEntryView.findViewById(R.id.textViewEditar);
				    		final EditText numMesa = (EditText) textEntryView.findViewById(R.id.editTextEditar);
				    		tituloMesa.setText("Indica el nuevo número de mesa: ");
				    		numMesa.setText("", TextView.BufferType.EDITABLE);
				    		//Creación y configuración de la ventana emergente
				    		AlertDialog.Builder ventEmergEditMesa = new AlertDialog.Builder(InicialCamarero.this);
				    		ventEmergEditMesa.setNegativeButton("Cancelar", null);
				    		ventEmergEditMesa.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
				    			
				    			public void onClick(DialogInterface dialog, int whichButton) {
				    					String numeroMesa = numMesa.getText().toString();
				    					if(numeroMesa.equals("")){
				    	        			Toast.makeText(InicialCamarero.this, "Introduce la mesa", Toast.LENGTH_LONG).show();           			
				    	        		}else{//ha introducido la mesa
				    	        			//Creamos una mesa aux para buscarla en la base de datos y ver si ya existe previamente
				    	        			MesaView mesaBuscarRepe = new MesaView(getApplicationContext());
				    	        			mesaBuscarRepe.setNumMesa(numeroMesa);
				    	        			if (existeMesa(mesaBuscarRepe)){// si ya existe la mesa no la metemos y sacamos un mensaje
				    	        				Toast.makeText(InicialCamarero.this, "Esa mesa ya está siendo atendida", Toast.LENGTH_LONG).show();
				    	        			}else{//la mesa no existe. La introducimos     				
				    	            			gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
				    	            			Iterator<MesaView> it = mesas.iterator();
				    	            			MesaView mesaAux = new MesaView(InicialCamarero.this);
				    	            			while(it.hasNext()){
				    	            				MesaView mesaAntigua = it.next();
				    	            				if (mesaAntigua.getNumMesa() == numeroMesaAEditar)
				    	            					mesaAux = mesaAntigua;
				    	            			}
				    	            			mesas.remove(mesaAux);
				    	                    	MesaView mesaNueva = new MesaView(InicialCamarero.this);
				    	                    	mesaNueva.setNumMesa(numeroMesa);
				    	                    	mesaNueva.setNumPersonas(mesaAux.getNumPersonas());
				    	                    	mesas.add(mesaNueva);
				    	                    	
				    	                    	Toast.makeText(getApplicationContext(),
				    	                    			"Mesa '"+numeroMesaAEditar+"' cambiada a '"+numeroMesa+"' correctamente", Toast.LENGTH_LONG).show();
				    	                    	
				    	                    	//Ordenamos y refrescamos
				    	                        ordenaMesas();
				    	                    	adapterCam = new InicialCamareroAdapter(InicialCamarero.this, mesas);
				    	                    	gridviewCam.setAdapter(adapterCam);  
				    	                    	
				    	                    	//Modificamos el campo NumMesa de la base de datos de cada plato
				    	                    	ContentValues valores = new ContentValues();
				    	                    	valores.put("NumMesa", numeroMesa);
				    	                    	String[] info = {numeroMesaAEditar};
				    	                    	
				    	                    	dbMesas.update("Mesas", valores, "NumMesa=?", info);
				    	                    }//fin de existe mesa
				    	        		}//fin de ha introducido la mesa
				    			}//cierra onClick de aceptar
				    		});//cierra el oyente de aceptar
				    		ventEmergEditMesa.setView(textEntryView);
				    		ventEmergEditMesa.show();
				    		//----------------- onClickListener de editar número de personas --------------------------------
				    	}else if(item == 2){
				    		LayoutInflater factory = LayoutInflater.from(InicialCamarero.this);
				    		final View textEntryView = factory.inflate(R.layout.alert_dialog_edit, null);
				    		final TextView tituloPersonas = (TextView) textEntryView.findViewById(R.id.textViewEditar);
				    		final EditText numPersonas = (EditText) textEntryView.findViewById(R.id.editTextEditar);
				    		tituloPersonas.setText("Indica el nuevo número de personas: ");
				    		numPersonas.setText("", TextView.BufferType.EDITABLE);
				    		//Limitamos a 99 el máximo de personas en la mesa
				    		InputFilter[] filterArray = new InputFilter[1];
				    		filterArray[0] = new InputFilter.LengthFilter(2);
				    		numPersonas.setFilters(filterArray);
				    		//Creación y configuración de la ventana emergente
				    		AlertDialog.Builder ventEmergEditMesa = new AlertDialog.Builder(InicialCamarero.this);
				    		ventEmergEditMesa.setNegativeButton("Cancelar", null);
				    		ventEmergEditMesa.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
				    			
				    			public void onClick(DialogInterface dialog, int whichButton) {
				    				//Comprobamos que ha introducido un numero porque si no a la hora de ordenar 
				    				//puede mezclar numeros y letras y no es valido
				    				if(!esNumero(numPersonas.getText().toString())){
				    					Toast.makeText(InicialCamarero.this, "Las personas han de ser un número", Toast.LENGTH_LONG).show();           			
				            		}else{
				    					String numeroPersonas = numPersonas.getText().toString();
				    					if(numeroPersonas.equals("")){
				    	        			Toast.makeText(InicialCamarero.this, "Introduce las personas", Toast.LENGTH_LONG).show();           			
				    	        		}else{//ha introducido las personas
				    	            			gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
				    	            			Iterator<MesaView> it = mesas.iterator();
				    	            			MesaView mesaAux = new MesaView(InicialCamarero.this);
				    	            			while(it.hasNext()){
				    	            				MesaView mesaAntigua = it.next();
				    	            				if (mesaAntigua.getNumMesa() == numeroMesaAEditar)
				    	            					mesaAux = mesaAntigua;
				    	            			}
				    	                    	mesaAux.setNumPersonas(numeroPersonas);
				    	                    	
				    	                    	Toast.makeText(getApplicationContext(),
				    	                    			"Ahora hay "+numeroPersonas+" personas en la mesa "+numeroMesaAEditar, Toast.LENGTH_LONG).show();
				    	                    	ordenaMesas();
				    	                    	adapterCam = new InicialCamareroAdapter(InicialCamarero.this, mesas);
				    	                    	gridviewCam.setAdapter(adapterCam);  
				    	                    	
				    	                    	//Modificamos el campo NumMesa de la base de datos de cada plato
				    	                    	ContentValues valores = new ContentValues();
				    	                    	valores.put("Personas", numeroPersonas);
				    	                    	String[] info = {numeroMesaAEditar};
				    	                    	
				    	                    	dbMesas.update("Mesas", valores, "NumMesa=?", info);
				    	        		}//fin de ha introducido numero de personas
				    				}//cierra en numero
				    			}//cierra onClick de aceptar
				    		});//cierra el oyente de aceptar
				    		ventEmergEditMesa.setView(textEntryView);
				    		ventEmergEditMesa.show();
				    	}else if (item == 3){
							 AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
				             alert.setMessage("¿Seguro que quieres eliminar esta mesa? "); //mensaje            
				             alert.setNegativeButton("Cancelar", null);
				             alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
				               	public void onClick(DialogInterface dialog, int whichButton) {
					                //eliminamos la mesa de la lista de MesaView		
				               		Iterator<MesaView> it = mesas.iterator();
	    	            			MesaView mesaAux = new MesaView(InicialCamarero.this);
	    	            			while(it.hasNext()){
	    	            				MesaView mesaAntigua = it.next();
	    	            				if (mesaAntigua.getNumMesa() == numeroMesaAEditar)
	    	            					mesaAux = mesaAntigua;
	    	            			}
	    	                    	mesas.remove(mesaAux);
					            	//Eliminar un registro
					             	String[] args = new String[]{numeroMesaAEditar};
					             	dbMesas.execSQL("DELETE FROM Mesas WHERE NumMesa=?", args);
					                
					             	//Ordenamos y refrescamos  
					                ordenaMesas();
					             	adapterCam = new InicialCamareroAdapter(InicialCamarero.this, mesas);
					             	gridviewCam.setAdapter(adapterCam);
				               	}
				             });//fin onclick aceptar
				             alert.show();
				    	} //fin else item 3
				    }
				});
			    ventEmergente.show();
			    return true;
			}
				
		});
        
	}//fin del oncreate
	
    /**
     * 
     * @param mesa
     * @return true si mesa está en el array mesas, false si no está
     */
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
 
    
   public void onAniadirClick(View v){
	   	LayoutInflater factory = LayoutInflater.from(this);

		//cargamos el xml creado para este alertDialog
		final View textEntryView = factory.inflate(R.layout.alert_dialog_view, null);
		//Obtenemos los campos
		final EditText numPersonas = (EditText) textEntryView.findViewById(R.id.editTextNumPersonas);
		final EditText numMesa = (EditText) textEntryView.findViewById(R.id.editTextNumMesa);
		final TextView tituloMesa = (TextView) textEntryView.findViewById(R.id.textViewNumMesa);
		final TextView tituloPersonas = (TextView) textEntryView.findViewById(R.id.textViewNumPersonas);
		//Obligamos a que el teclado sea sólo numérico para la comodidad del camarero
		numPersonas.setInputType(InputType.TYPE_CLASS_NUMBER);
		//Damos valor a los campos		
		numPersonas.setText("", TextView.BufferType.EDITABLE);
		numMesa.setText("", TextView.BufferType.EDITABLE);
		//Limitamos a 99 el número de personas por mesa
		InputFilter[] filterArrayP = new InputFilter[1];
		filterArrayP[0] = new InputFilter.LengthFilter(2);
		numPersonas.setFilters(filterArrayP);
		tituloMesa.setText("Elige el número de mesa:");
		tituloPersonas.setText("Elige el número de personas:");
		//Construimos el AlertDialog y le metemos la vista que hemos personalizado
		AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
		alert.setView(textEntryView);
		alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
			
			public void onClick(DialogInterface dialog, int whichButton) {
					String numeroMesa = numMesa.getText().toString();
					String numeroPersonas = numPersonas.getText().toString();
					if(numeroMesa.equals("")){
	        			Toast.makeText(InicialCamarero.this, "Introduce la mesa", Toast.LENGTH_LONG).show();           			
	        		}else if(numeroPersonas.equals("")){
	        			Toast.makeText(InicialCamarero.this, "Introduce el número de personas", Toast.LENGTH_LONG).show(); 
	        		}else if(!esNumero(numPersonas.getText().toString())){
	        			Toast.makeText(InicialCamarero.this, "La cantidad de personas ha de ser un número", Toast.LENGTH_LONG).show();
	        		}else{//ha introducido la mesa y numero de personas
	        			//Creamos una mesa aux para buscarla en la base de datos y ver si ya existe previamente
	        			MesaView mesaBuscarRepe = new MesaView(getApplicationContext());
	        			mesaBuscarRepe.setNumMesa(numeroMesa);
	        			if (existeMesa(mesaBuscarRepe)){// si ya existe la mesa no la metemos y sacamos un mensaje
	        				Toast.makeText(InicialCamarero.this, "Esa mesa ya está siendo atendida", Toast.LENGTH_LONG).show();
	        			}else{//la mesa no existe. La introducimos     				
	            			gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
	                    	MesaView mesa2 = new MesaView(InicialCamarero.this);
	                    	mesa2.setNumMesa(numeroMesa);
	                    	mesa2.setNumPersonas(numeroPersonas);
	                    	mesas.add(mesa2);
	                    	//Ordenamos y refrescamos
	                        ordenaMesas();
	                    	adapterCam = new InicialCamareroAdapter(InicialCamarero.this, mesas);
	                    	gridviewCam.setAdapter(adapterCam);
	                    	
	                    	//Sacamos la fecha a la que el camarero ha introducido la mesa
	                    	Calendar cal = new GregorianCalendar();
	                        Date date = cal.getTime();
	                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	                        String formatteDate = df.format(date);
	                        //Sacamos la hora a la que el camarero ha introducido la mesa
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
	                    	for(int j = 0; j < datos.size(); j++ ){
	                    		//concatenamos los extras para guardarlos en string separados por comas
		                    	String extr = "";
		                    	for (int i = 0; i < datos.get(j).getExtras().size(); i++ ){
		                    		extr= extr + datos.get(j).getExtras().get(i) + ",";
		                    	}
		                    	//si habia extras, quitamos la ultima coma
		                    	if (extr.length() > 0)
		                    		extr= extr.substring(0,extr.length()-1);
		                    	
		                    	//Sacamos el nombre del plato y el precio de la base de datos MiBase.db
		                    	String[] infoMesa2 = new String[]{"Nombre","Precio"};
		                       	String[] info = new String[]{datos.get(j).getIdPlato()};
		                   		Cursor cPMiBase = dbMiBase.query("Restaurantes", infoMesa2, "Id=?" ,info,null, null,null);
		                   		
		                   		cPMiBase.moveToNext();
		                   		if(cPMiBase.getCount() > 0){
		                   			nombre=cPMiBase.getString(0);
		                   			precio=cPMiBase.getInt(1);
		                   		}
		                    	//Insertamos el plato en la tabla Platos
		                    	ContentValues registro = new ContentValues();
		                    	registro.put("NumMesa", numeroMesa);
		                    	registro.put("IdCamarero", idCamarero);
		                    	registro.put("IdPlato", "" + datos.get(j).getIdPlato());
		                    	registro.put("Observaciones",  "" + datos.get(j).getObservaciones());
		                    	registro.put("Extras", extr);
		                    	registro.put("FechaHora", formatteDate + " " + formatteHour);
		                    	registro.put("Nombre", nombre);
		                    	registro.put("Precio", precio);
		                    	registro.put("Personas", numeroPersonas);
		                    	registro.put("IdUnico", idUnico);
		                    	//aumentamos el idUnico
		                    	idUnico ++;
		                	    //insertamos el registro en la base de datos
		                	   	dbMesas.insert("Mesas", null, registro);
		                	   
	                    	}//fin de relleno de la base de datos con lo que nos viene del chip
	                    }//fin de existe mesa
	        		}//fin de ha introducido la mesa y numero de personas
			}//cierra onClick de aceptar
		});//cierra el oyente de aceptar

		alert.setNegativeButton("Cancelar", null);

		alert.show();
   }
     

	public void onClickEliminarMesa(View v) {
			 //Creamos el AlertDialog con la vista por defecto
             AlertDialog.Builder alert = new AlertDialog.Builder(InicialCamarero.this);
             alert.setMessage("Introduce el número de la mesa que desea borrar: "); //mensaje
             final EditText input = new EditText(InicialCamarero.this); //creamos un Edit Text
             alert.setView(input); //añadimos el edit text a la vista del AlertDialog
             //añadimos los botones
             alert.setNegativeButton("Cancelar", null);
             alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { 
               	public void onClick(DialogInterface dialog, int whichButton) {
                 		String value =input.getText().toString();
                 		gridviewCam = (GridView) findViewById(R.id.gridViewInicial);
                 
                     	//Creo una mesa aux para buscarla en la base de datos para ver si existe
            			MesaView buscaMesa = new MesaView(getApplicationContext());
            			buscaMesa.setNumMesa(value);
            			if (!existeMesa(buscaMesa)){// si no existe la mesa mostramos un mensaje
            				Toast.makeText(InicialCamarero.this, "Esa mesa no existe", Toast.LENGTH_LONG).show();
            			}else{//eliminamos la mesa
	                        //Eliminar un registro
	                     	String[] args = new String[]{value};
	                     	dbMesas.execSQL("DELETE FROM Mesas WHERE NumMesa=?", args);
	                        //Lo eliminamos tambien de la lista de mesas	
	                     	Boolean enc = false;
	                     	Iterator<MesaView> it = mesas.iterator();
	                     	while (it.hasNext() && !enc){
	                     		MesaView atratar = it.next();
	                     		if(atratar.getNumMesa().equals(value)){
	                     			enc=true;
	                     			mesas.remove(atratar);
	                     		}
	                     	}
	                     	//Ordenamos y refrescamos  
	                        ordenaMesas();
	                     	adapterCam = new InicialCamareroAdapter(InicialCamarero.this, mesas);
	                     	gridviewCam.setAdapter(adapterCam);
	               		}
                     }
                 });
             alert.show();
          }	
	
	
	public void ordenaMesas(){		
		//en mesasnumero guardamos las mesas que son un numero
		ArrayList<MesaView> mesasNumero = new ArrayList<MesaView>();
		ArrayList<MesaView> mesasNumeroAux = new ArrayList<MesaView>();
		//en mesastexto guardamos las mesas que son texto
		ArrayList<MesaView> mesasTexto = new ArrayList<MesaView>();
		ArrayList<MesaView> mesasTextoAux = new ArrayList<MesaView>();
		
		for(int i = 0; i < mesas.size(); i++){
			if (esNumero(mesas.get(i).getNumMesa())) mesasNumero.add(mesas.get(i));
			else mesasTexto.add(mesas.get(i));
		}
		//recorremos el array  mesasNumero y lo ordenamos
		while(mesasNumero.size() > 0){
			MesaView mesaMin = buscaMinNumero(mesasNumero);
			mesasNumero.remove(mesaMin);
			mesasNumeroAux.add(mesaMin);
		}
		mesasNumero = mesasNumeroAux;
		
		//recorremos el array  mesasTexto y lo ordenamos
				while(mesasTexto.size() > 0){
					MesaView mesaMin = buscaMinTexto(mesasTexto);
					mesasTexto.remove(mesaMin);
					mesasTextoAux.add(mesaMin);
				}
		mesasTexto = mesasTextoAux;
		//concatenamos los dos arrayList. Primero numeros y luego textos
		for(int i = 0; i < mesasTexto.size(); i++){
			mesasNumero.add(mesasTexto.get(i));
		}
		mesas = mesasNumero;
	}
	 
	public MesaView buscaMinNumero(ArrayList<MesaView> arrayMesas){
		//recorremos todas las mesas buscando la que tiene menor numero de mesa
		MesaView mesaMin = arrayMesas.get(0);//Pongo el primero
		int i = 1;
		while (i < arrayMesas.size())
		{
			if(Integer.parseInt(arrayMesas.get(i).getNumMesa()) < Integer.parseInt(mesaMin.getNumMesa())) {
				mesaMin = arrayMesas.get(i);
			}
			i++;
		}
		
	
		return mesaMin;
	}
	
	public MesaView buscaMinTexto(ArrayList<MesaView> arrayMesas){
		//recorremos todas las mesas buscando la que tiene menor numero de mesa
		MesaView mesaMin = arrayMesas.get(0);//Pongo el primero
		int i = 1;
		while (i < arrayMesas.size())
		{
			if(arrayMesas.get(i).getNumMesa().compareTo(mesaMin.getNumMesa()) == -1) {
				mesaMin = arrayMesas.get(i);
			}
			i++;
		}
		return mesaMin;
	}
	
	/**
	 * 
	 * @param cadena
	 * @return true si es numero, false si no lo es
	 */
	private boolean esNumero(String cadena){
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	private static void eliminarDeArray(final String numeroMesa){
		Boolean enc = false;
     	Iterator<MesaView> it = mesas.iterator();
     	while (it.hasNext() && !enc){
     		MesaView atratar = it.next();
     		if(atratar.getNumMesa().equals(numeroMesa)){
     			enc=true;
     			mesas.remove(atratar);
     		}
     	}	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent = new Intent (getApplication(),Historico.class);
            startActivity(intent);
            return true;
        }
    
    public static int getIdUnico(){
    	idUnico++;
    	return idUnico;
    }
	
	
}

