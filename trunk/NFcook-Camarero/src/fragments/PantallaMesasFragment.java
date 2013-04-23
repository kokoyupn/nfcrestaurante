package fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import com.example.nfcook_camarero.Borrar_tarjeta;
import com.example.nfcook_camarero.HandlerGenerico;
import com.example.nfcook_camarero.InfoPlato;
import com.example.nfcook_camarero.InicialCamareroAdapter;
import com.example.nfcook_camarero.Mesa;
import com.example.nfcook_camarero.MesaView;
import com.example.nfcook_camarero.R;
import com.example.nfcook_camarero.Sincronizacion_BeamNfc;
import com.example.nfcook_camarero.Sincronizacion_LecturaNfc;
import com.example.nfcook_camarero.Sincronizacion_QR;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PantallaMesasFragment extends Fragment {
	
	//Necesaria para poder almacenar el ultimo idUnico;
	private static PantallaMesasFragment propiaInstancia;
	
	private View vista;
	private GridView gridviewCam;
    private static ArrayList<MesaView> mesas;
    private static String idCamarero;
    private String nombre;
    private static String numeroMesaAEditar;
    private static String numeroPersonas;
    private double precio;
    private static int idUnico = 0;
    private ArrayList<InfoPlato> datos; //Lo que nos llega del chip
    //Ventana emergente para la sincronizacion
    private AlertDialog ventanaEmergenteSincronizacion;
    private HandlerGenerico sqlMesas, sqlMiBase;
	private SQLiteDatabase dbMesas, dbMiBase;
	
	private static String restaurante;
    /*NFC*/
	Context ctx;
	Intent intent;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	vista = inflater.inflate(R.layout.inicial_camarero, container, false);

        
    	
        //Quitamos barra de titulo de la aplicacion
        //this.getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //creamos la lista de mesas
        mesas = new ArrayList<MesaView>();
        gridviewCam = (GridView) vista.findViewById(R.id.gridViewInicial); 
        // Obtenemos el idCamarero de la pantalla anterior
        Bundle bundle = getActivity().getIntent().getExtras();
        idCamarero = bundle.getString("usuario");
        
        //Restablecemos el ultimo idUnico que se introdujo
        propiaInstancia = this;
        cargarUltimoIdentificadorUnico();
        
        
        restaurante=bundle.getString("Restaurante");
        //restaurante="VIPS";
        
	   //Para importar la base de Assets
        try{
			sqlMesas = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db" );
			
			dbMesas= sqlMesas.open();
			}catch(SQLiteException e){
			System.out.println("CATCH");
			Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
		}
        try{
        	//Consultamos de la base de datos Mesas lo que necesitamos para representarlo
        	String[] infoMesa = new String[]{"NumMesa","Personas"};
        	Cursor cPMesas = dbMesas.query("Mesas", infoMesa, null,null,null, null,null);
    	
        	//añadir mesas que ya tiene
        	while(cPMesas.moveToNext()){
        		MesaView mesa1= new MesaView(this.getActivity());
        		mesa1.setNumMesa(cPMesas.getString(0));
        		mesa1.setNumPersonas(cPMesas.getString(1));
        		if(!existeMesa(mesa1))
        			mesas.add(mesa1);
        	}
        }catch(SQLiteException e){
        	Toast.makeText(getActivity().getApplicationContext(),"ERROR BASE DE DATOS -> MESAS",Toast.LENGTH_SHORT).show();	
        }

        ordenaMesas();
        //Llamamos al adapter para que muestre en la pantalla los cambios realizados
        InicialCamareroAdapter adapterCam= new InicialCamareroAdapter(this.getActivity().getApplicationContext(), mesas);
        gridviewCam.setAdapter(adapterCam);
        //creamos el oyente del gridView
        gridviewCam.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	//nos llevara a la pantalla siguiente          	
            	MesaView pulsada= mesas.get(position);
            //	dbMesas.close();
            	Intent intent = new Intent(PantallaMesasFragment.this.getActivity(),Mesa.class);
            	//Le pasamos a la siguiente pantalla el numero de la mesa que se ha pulsado
        		intent.putExtra("NumMesa", pulsada.getNumMesa());
        		intent.putExtra("IdCamarero",idCamarero);
        		intent.putExtra("Personas", pulsada.getNumPersonas());
        		intent.putExtra("Restaurante", restaurante);
        		//Lanzamos la actividad
        		startActivity(intent);
                }
        });
        //Contexto para iniciar la actividad en el boton de sincronizar
        ctx=this.getActivity();
        
        
        // establecimiento del oyente de añadir mesa
        LinearLayout botonAnadirMesa = (LinearLayout) vista.findViewById(R.id.LinearLayoutAniadirMesa);
        botonAnadirMesa.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onAniadirClick(vista);
			}
		});
        
        
     // establecimiento del oyente de eliminar mesa
        LinearLayout botonEliminarMesa = (LinearLayout) vista.findViewById(R.id.LinearLayoutEliminarMesa);
        botonEliminarMesa.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickEliminarMesa(vista);
			}
		});
        
     //establecimiento del oyente de dejar pulsada una mesa   
        gridviewCam.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				//Guardamos el número de la mesa pulsada y el numero de personas
				numeroMesaAEditar = mesas.get(position).getNumMesa();
				numeroPersonas = mesas.get(position).getNumPersonas();
				//Preparamos los elementos que tendrá la lista
				//final CharSequence[] items = {"Cobrar","Sincronizacion NFC","Sincronizacion Beam","Codigo QR", "Editar nº mesa", "Editar nº personas","Eliminar mesa"};
				final CharSequence[] items = {"Cobrar","Sincronizacion","Editar nº mesa", "Editar nº personas","Eliminar mesa","Borrar Tag"};
				AlertDialog.Builder ventEmergente = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
				ventEmergente.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
				    	//------------------- Cobrar Mesa ------------------------------------
				    	if (item == 0){
				    		//Toast.makeText(getApplicationContext(), "Hacer cobrar mesa", Toast.LENGTH_SHORT).show();
				    		//Boton Cobrar--------------------------------------------------------------------
				    		View vistaAviso = LayoutInflater.from(PantallaMesasFragment.this.getActivity()).inflate(R.layout.alert_dialog_cobrar, null);
				    		TextView texto= (TextView) vistaAviso.findViewById(R.id.textViewCobrar);
				    		texto.setText("Cobrar");
				    		AlertDialog.Builder alert = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
				            //alert.setMessage("¿Seguro que quieres cobrar y cerrar esta mesa? "); //mensaje            
				    		TextView mensaje= (TextView) vistaAviso.findViewById(R.id.textViewMensaje);
				    		mensaje.setText("¿Estas seguro que quieres cobrar y cerrar esta mesa?");
				    		 alert.setNegativeButton("Cancelar", null);
				             alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
				               	public void onClick(DialogInterface dialog, int whichButton) {
				                	try{
				                		HandlerGenerico sqlHistorico = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Historico.db");
				                		SQLiteDatabase dbHistorico= sqlHistorico.open();
				            			
				                		String[] numeroDeMesa = new String[]{numeroMesaAEditar};
				            		    Cursor filasPedido = dbMesas.query("Mesas", null, "NumMesa=?", numeroDeMesa,null, null, null);
				                		Cursor filasHistorico = dbHistorico.query("Historico", null, null,null, null,null, null);
				                		
				                		
				                		while(filasPedido.moveToNext()){
				                			//Añades los platos a la base de datos del historico y borras de la lista de platos
				                			ContentValues nuevo = new ContentValues();
				                			
				                			for (int i=0;i<filasPedido.getColumnCount();i++){
				                				for (int j=0;j<filasHistorico.getColumnCount();j++){
				                					if(filasPedido.getColumnName(i).equals(filasHistorico.getColumnName(j))){
				                						nuevo.put(filasPedido.getColumnName(i), filasPedido.getString(i));				
				    	            				}
				                				}
				                			}
				                			dbHistorico.insert("Historico", null, nuevo);
				    	            	}
				                		
				                		//Borra de la base de datos los platos de esta mesa
				                		String[] args = new String[]{numeroMesaAEditar};
				                     	dbMesas.execSQL("DELETE FROM Mesas WHERE NumMesa=?", args);
				                		
				                		
				                		eliminarDeArray(numeroMesaAEditar);
				                		//refrescamos  
				                        
				                     	InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
				                     	gridviewCam.setAdapter(adapterCam);
		
				                			
				                		
				                	}catch(Exception e){
				                		Toast.makeText(getActivity().getApplicationContext(), "Error al cobrar", Toast.LENGTH_SHORT).show();
				                	}
				               	}
				             });//fin onclick aceptar
				             alert.setView(vistaAviso);
				             alert.show();
				    		//Boton Cobrar--------------------------------------------------------------------
				    		
				    	//------------------ Sincronizar -----------------------------------
				    	}else if (item == 1){
				    		//Sincronizacion
				    		View vistaAviso = LayoutInflater.from(PantallaMesasFragment.this.getActivity()).inflate(R.layout.alert_sincronizacion, null);
				    		ventanaEmergenteSincronizacion = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity()).create();
				    		ventanaEmergenteSincronizacion.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancelar",new DialogInterface.OnClickListener() {
				    			
				    			public void onClick(DialogInterface dialog, int which) {
				    				ventanaEmergenteSincronizacion.dismiss();
				    			}
				    		});
				    		ventanaEmergenteSincronizacion.setView(vistaAviso);
				    		ventanaEmergenteSincronizacion.show();
				    		/*Declaro los metodos para los on click de los botones*/
				    		ImageView imagenNfc= (ImageView)ventanaEmergenteSincronizacion.findViewById(R.id.imageNFC);  
				    		imagenNfc.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										intent = new Intent(ctx,Sincronizacion_LecturaNfc.class);
										intent.putExtra("NumMesa", numeroMesaAEditar);
										intent.putExtra("IdCamarero",idCamarero);
										intent.putExtra("Personas", numeroPersonas);
										intent.putExtra("Restaurante", restaurante);
										ventanaEmergenteSincronizacion.dismiss();
										
										startActivity(intent);
										
									}
				    				}
				    				);
				    		ImageView imagenQr= (ImageView)ventanaEmergenteSincronizacion.findViewById(R.id.imageQR);  
				    		imagenQr.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										intent = new Intent(ctx,Sincronizacion_QR.class);
										intent.putExtra("NumMesa", numeroMesaAEditar);
										intent.putExtra("IdCamarero",idCamarero);
										intent.putExtra("Personas", numeroPersonas);
										intent.putExtra("Restaurante", restaurante);
										ventanaEmergenteSincronizacion.dismiss();
										
										startActivity(intent);
										
									}
				    				}
				    				);
				    		ImageView imagenBeam= (ImageView)ventanaEmergenteSincronizacion.findViewById(R.id.imageBeam);  
				    		imagenBeam.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										intent = new Intent(ctx,Sincronizacion_BeamNfc.class);
								    	intent.putExtra("Restaurante", restaurante);
								    	ventanaEmergenteSincronizacion.dismiss();
										
										startActivity(intent);
										
									}
				    				}
				    				);
	
				    		
				    	//----------------- onClickListener de editar número de mesa --------------------------------
				    	}else if(item == 2){
				    		LayoutInflater factory = LayoutInflater.from(PantallaMesasFragment.this.getActivity());
				    		final View textEntryView = factory.inflate(R.layout.alert_dialog_edit, null);
				    		final TextView tituloVentana = (TextView) textEntryView.findViewById(R.id.textViewTituloEditar);
				    		final TextView tituloMesa = (TextView) textEntryView.findViewById(R.id.textViewEditar);
				    		final EditText numMesa = (EditText) textEntryView.findViewById(R.id.editTextEditar);
				    		tituloVentana.setText("Editar Mesa");
				    		tituloMesa.setText("Indica el nuevo número de mesa: ");
				    		numMesa.setText("", TextView.BufferType.EDITABLE);
				    		//Limitamos a 4 caracteres como máximo en la mesa
				    		InputFilter[] filterArray = new InputFilter[1];
				    		filterArray[0] = new InputFilter.LengthFilter(4);
				    		numMesa.setFilters(filterArray);
				    		//Creación y configuración de la ventana emergente
				    		AlertDialog.Builder ventEmergEditMesa = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
				    		ventEmergEditMesa.setNegativeButton("Cancelar", null);
				    		ventEmergEditMesa.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
				    			
				    			public void onClick(DialogInterface dialog, int whichButton) {
				    					String numeroMesa = numMesa.getText().toString();
				    					if(numeroMesa.equals("")){
				    	        			Toast.makeText(PantallaMesasFragment.this.getActivity(), "Introduce la mesa", Toast.LENGTH_LONG).show();           			
				    	        		}else{//ha introducido la mesa
				    	        			//Creamos una mesa aux para buscarla en la base de datos y ver si ya existe previamente
				    	        			MesaView mesaBuscarRepe = new MesaView(getActivity().getApplicationContext());
				    	        			mesaBuscarRepe.setNumMesa(numeroMesa);
				    	        			if (existeMesa(mesaBuscarRepe)){// si ya existe la mesa no la metemos y sacamos un mensaje
				    	        				Toast.makeText(PantallaMesasFragment.this.getActivity(), "Esa mesa ya está siendo atendida", Toast.LENGTH_LONG).show();
				    	        			}else{//la mesa no existe. La introducimos     				
				    	            			gridviewCam = (GridView) getActivity().findViewById(R.id.gridViewInicial);
				    	            			Iterator<MesaView> it = mesas.iterator();
				    	            			MesaView mesaAux = new MesaView(PantallaMesasFragment.this.getActivity());
				    	            			while(it.hasNext()){
				    	            				MesaView mesaAntigua = it.next();
				    	            				if (mesaAntigua.getNumMesa() == numeroMesaAEditar)
				    	            					mesaAux = mesaAntigua;
				    	            			}
				    	            			mesas.remove(mesaAux);
				    	                    	MesaView mesaNueva = new MesaView(PantallaMesasFragment.this.getActivity());
				    	                    	mesaNueva.setNumMesa(numeroMesa);
				    	                    	mesaNueva.setNumPersonas(mesaAux.getNumPersonas());
				    	                    	mesas.add(mesaNueva);
				    	                    	
				    	                    	Toast.makeText(getActivity().getApplicationContext(),
				    	                    			"Mesa '"+numeroMesaAEditar+"' cambiada a '"+numeroMesa+"' correctamente", Toast.LENGTH_LONG).show();
				    	                    	
				    	                    	//Ordenamos y refrescamos
				    	                        ordenaMesas();
				    	                        InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
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
				    	}else if(item == 3){
				    		LayoutInflater factory = LayoutInflater.from(PantallaMesasFragment.this.getActivity());
				    		final View textEntryView = factory.inflate(R.layout.alert_dialog_edit, null);
				    		final TextView tituloVentana = (TextView) textEntryView.findViewById(R.id.textViewTituloEditar);
				    		final TextView tituloPersonas = (TextView) textEntryView.findViewById(R.id.textViewEditar);
				    		final EditText numPersonas = (EditText) textEntryView.findViewById(R.id.editTextEditar);
				    		tituloVentana.setText("Editar Personas");
				    		tituloPersonas.setText("Indica el nuevo número de personas: ");
				    		numPersonas.setText("", TextView.BufferType.EDITABLE);
				    		//Limitamos a 99 el máximo de personas en la mesa
				    		InputFilter[] filterArray = new InputFilter[1];
				    		filterArray[0] = new InputFilter.LengthFilter(2);
				    		numPersonas.setFilters(filterArray);
				    		//Creación y configuración de la ventana emergente
				    		AlertDialog.Builder ventEmergEditMesa = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
				    		ventEmergEditMesa.setNegativeButton("Cancelar", null);
				    		ventEmergEditMesa.setPositiveButton("Aceptar", new  DialogInterface.OnClickListener() { // si le das al aceptar
				    			
				    			public void onClick(DialogInterface dialog, int whichButton) {
				    				//Comprobamos que ha introducido un numero porque si no a la hora de ordenar 
				    				//puede mezclar numeros y letras y no es valido
				    				if(!esNumero(numPersonas.getText().toString())){
				    					Toast.makeText(PantallaMesasFragment.this.getActivity(), "Las personas han de ser un número", Toast.LENGTH_LONG).show();           			
				            		}else{
				    					String numeroPersonas = numPersonas.getText().toString();
				    					if(numeroPersonas.equals("")){
				    	        			Toast.makeText(PantallaMesasFragment.this.getActivity(), "Introduce las personas", Toast.LENGTH_LONG).show();           			
				    	        		}else{//ha introducido las personas
				    	            			gridviewCam = (GridView) getActivity().findViewById(R.id.gridViewInicial);
				    	            			Iterator<MesaView> it = mesas.iterator();
				    	            			MesaView mesaAux = new MesaView(PantallaMesasFragment.this.getActivity());
				    	            			while(it.hasNext()){
				    	            				MesaView mesaAntigua = it.next();
				    	            				if (mesaAntigua.getNumMesa() == numeroMesaAEditar)
				    	            					mesaAux = mesaAntigua;
				    	            			}
				    	                    	mesaAux.setNumPersonas(numeroPersonas);
				    	                    	
				    	                    	Toast.makeText(getActivity().getApplicationContext(),
				    	                    			"Ahora hay "+numeroPersonas+" personas en la mesa "+numeroMesaAEditar, Toast.LENGTH_LONG).show();
				    	                    	ordenaMesas();
				    	                    	InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
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
				    	//---------Eliminar Mesa------
				    	}else if (item == 4){
							 
				    	    View vistaAviso = LayoutInflater.from(PantallaMesasFragment.this.getActivity()).inflate(R.layout.alert_dialog_cobrar, null);
				    		TextView texto= (TextView) vistaAviso.findViewById(R.id.textViewCobrar);
				    		texto.setText("Eliminar Mesa");
				    		
				    		AlertDialog.Builder alert = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
				            TextView mensaje= (TextView) vistaAviso.findViewById(R.id.textViewMensaje);
					    	mensaje.setText("¿Seguro que quieres eliminar esta mesa?");
				             alert.setNegativeButton("Cancelar", null);
				             alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
				               	public void onClick(DialogInterface dialog, int whichButton) {
					                //eliminamos la mesa de la lista de MesaView		
				               		Iterator<MesaView> it = mesas.iterator();
	    	            			MesaView mesaAux = new MesaView(PantallaMesasFragment.this.getActivity());
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
					                InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
					             	gridviewCam.setAdapter(adapterCam);
				               	}
				             });//fin onclick aceptar
				             alert.setView(vistaAviso);
				             alert.show();
				    	} //fin else item 4
				    	else if (item == 5){
				    		intent = new Intent(ctx,Borrar_tarjeta.class);
				    		intent.putExtra("Restaurante",restaurante);
				    		startActivity(intent);
				    	}//fin else item5
				    }
				});
			    ventEmergente.show();
			    return true;
			}
		});
        return vista;
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
	   	LayoutInflater factory = LayoutInflater.from(this.getActivity());

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
		//Limitamos a 4 caracteres como máximo en la mesa
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(4);
		numMesa.setFilters(filterArray);
		//Limitamos a 99 el número de personas por mesa
		InputFilter[] filterArrayP = new InputFilter[1];
		filterArrayP[0] = new InputFilter.LengthFilter(2);
		numPersonas.setFilters(filterArrayP);
		tituloMesa.setText("Nº de mesa:");
		tituloPersonas.setText("Nº de personas:");
		//Construimos el AlertDialog y le metemos la vista que hemos personalizado
		AlertDialog.Builder alert = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
		alert.setView(textEntryView);
		alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { // si le das al aceptar
			
			public void onClick(DialogInterface dialog, int whichButton) {
					String numeroMesa = numMesa.getText().toString();
					String numeroPersonas = numPersonas.getText().toString();
					if(numeroMesa.equals("")){
	        			Toast.makeText(PantallaMesasFragment.this.getActivity(), "Introduce la mesa", Toast.LENGTH_LONG).show();           			
	        		}else if(numeroPersonas.equals("")){
	        			Toast.makeText(PantallaMesasFragment.this.getActivity(), "Introduce el número de personas", Toast.LENGTH_LONG).show(); 
	        		}else if(!esNumero(numPersonas.getText().toString())){
	        			Toast.makeText(PantallaMesasFragment.this.getActivity(), "La cantidad de personas ha de ser un número", Toast.LENGTH_LONG).show();
	        		}else{//ha introducido la mesa y numero de personas
	        			//Creamos una mesa aux para buscarla en la base de datos y ver si ya existe previamente
	        			MesaView mesaBuscarRepe = new MesaView(getActivity().getApplicationContext());
	        			mesaBuscarRepe.setNumMesa(numeroMesa);
	        			if (existeMesa(mesaBuscarRepe)){// si ya existe la mesa no la metemos y sacamos un mensaje
	        				Toast.makeText(PantallaMesasFragment.this.getActivity(), "Esa mesa ya está siendo atendida", Toast.LENGTH_LONG).show();
	        			}else{//la mesa no existe. La introducimos     				
	            			gridviewCam = (GridView) getActivity().findViewById(R.id.gridViewInicial);
	                    	MesaView mesa2 = new MesaView(PantallaMesasFragment.this.getActivity());
	                    	mesa2.setNumMesa(numeroMesa);
	                    	mesa2.setNumPersonas(numeroPersonas);
	                    	mesas.add(mesa2);
	                    	//Ordenamos y refrescamos
	                        ordenaMesas();
	                        InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
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
	                			sqlMiBase=new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
	                			dbMiBase= sqlMiBase.open();
	                		}catch(SQLiteException e){
	                			System.out.println("CATCH");
	                			Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE LA BASE DE DATOS",Toast.LENGTH_SHORT).show();
	                		}
	                       	
	                    	//rellenamos la base de datos con lo que nos ha venido del chip que esta en datos
	                    	/*for(int j = 0; j < datos.size(); j++ ){
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
		                   			precio=cPMiBase.getDouble(1);
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
	                    	*/
	                    }//fin de existe mesa
	        		}//fin de ha introducido la mesa y numero de personas
			}//cierra onClick de aceptar
		});//cierra el oyente de aceptar

		alert.setNegativeButton("Cancelar", null);

		alert.show();
   }
     

	public void onClickEliminarMesa(View v) {
		//cargamos el xml creado para este alertDialog
			LayoutInflater factory = LayoutInflater.from(this.getActivity());
			final View textEntryView = factory.inflate(R.layout.alert_dialog_eliminar, null);
			//Obtenemos los campos
			final EditText mesaBorrar = (EditText) textEntryView.findViewById(R.id.editTextElimMesa);
			final TextView elimMesa = (TextView) textEntryView.findViewById(R.id.textViewElimMesa);
			//Damos valor a los campos		
			mesaBorrar.setText("", TextView.BufferType.EDITABLE);
			elimMesa.setText("Nº de mesa a borrar:");
			//Construimos el AlertDialog y le metemos la vista que hemos personalizado
			AlertDialog.Builder alert = new AlertDialog.Builder(PantallaMesasFragment.this.getActivity());
			alert.setView(textEntryView);
		
             //añadimos los botones
             alert.setNegativeButton("Cancelar", null);
             alert.setPositiveButton("Aceptar",new  DialogInterface.OnClickListener() { 
               	public void onClick(DialogInterface dialog, int whichButton) {
                 		String value = mesaBorrar.getText().toString();
                 		gridviewCam = (GridView) getActivity().findViewById(R.id.gridViewInicial);
                 
                     	//Creo una mesa aux para buscarla en la base de datos para ver si existe
            			MesaView buscaMesa = new MesaView(getActivity().getApplicationContext());
            			buscaMesa.setNumMesa(value);
            			if (!existeMesa(buscaMesa)){// si no existe la mesa mostramos un mensaje
            				Toast.makeText(PantallaMesasFragment.this.getActivity(), "Esa mesa no existe", Toast.LENGTH_LONG).show();
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
	                        InicialCamareroAdapter adapterCam = new InicialCamareroAdapter(PantallaMesasFragment.this.getActivity(), mesas);
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
	
	static void eliminarDeArray(final String numeroMesa){
		Boolean enc = false;
     	Iterator<MesaView> it = mesas.iterator();
     	while (it.hasNext() && !enc){
     		MesaView atratar = it.next();
     		if(atratar.getNumMesa().equals(numeroMesa)){
     			enc=true;
     			mesas.remove(atratar);
     		}
     	}	}
	/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }*/
	
	private void cargarUltimoIdentificadorUnico() {
		//SharedPreferences nos permite recuperar datos aunque la aplicacion se haya cerrado
      	SharedPreferences ultimoId = getActivity().getSharedPreferences("Identificador_Unico", 0);
      	idUnico = ultimoId.getInt("identificadorUnico", 0); // 0 es lo que devuelve si no hubiese nada con esa clave		
	}
	
	public void setUltimoIdentificadorUnico(){
		//Almacenamos la posicion del restaurante de la lista
		SharedPreferences preferencia = getActivity().getSharedPreferences("Identificador_Unico", 0);
		SharedPreferences.Editor editor = preferencia.edit();
		editor.putInt("identificadorUnico", idUnico);
		editor.commit(); //Para que surja efecto el cambio
	}
    
    public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent = new Intent (getActivity().getApplication(),PantallaHistoricoFragment.class);
            startActivity(intent);
            return true;
        }
    
    public static int getIdUnico(){
    	idUnico++;
    	return idUnico;
    }
	
    //---Metodos estaticos para poder acceder a estos datos desde otra actividades
    public static String dameMesa()
    {
    	return numeroMesaAEditar;
    }
    public static String dameCamarero()
    {
    	return idCamarero;
    }
    public static String dameNumPersonas()
    {
    	return numeroPersonas;
    }

    public static String dameRestaurante()
    {
    	return restaurante;
    }
	public static PantallaMesasFragment getInstanciaClase() {
		return propiaInstancia;
	}
}
