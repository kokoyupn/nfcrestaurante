package com.example.nfcook_camarero;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adapters.ContenidoListMesa;
import adapters.HijoExpandableListEditar;
import adapters.MiCursorAdapterBuscadorPlatos;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AnadirPlatos extends Activity{
	
	/*Atributos estaticos para poder tener acceso a ellos en los metodos estaticos de la clase y asi
	 * poder actualizar la lista desde otras clases*/
	private static MiExpandableListAdapterAnadirPlato  adapterExpandableListAnadirPlato;
	private static ExpandableListView expandableListAnadirPlato;
	private HandlerGenerico sqlMiBase, sqlBuscador;
	private AutoCompleteTextView buscador;
	private boolean noSeleccionadoAutoCompleteTextView;
	private SQLiteDatabase dbMiBase, dbBuscador;
	
	private static String numMesa;
	private static String idCamarero;
	private static String numPersonas; 
	
	private static ExpandableListView expandableListEditarExtras;
	private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private AutoCompleteTextView actwObservaciones;
	
	//private ArrayList<InfoPlato> platosAñadidos; //aqui vaos guardando los platos que ha añadido para luego pasarselos a la pantalla de Mesa 
	//cuando añade un plato se añade a la base de datos de mesas
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		noSeleccionadoAutoCompleteTextView = false;
        super.onCreate(savedInstanceState);  
        
      //Quitamos barra de titulo de la aplicacion
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.expandable_list_anadir_plato);
        
        Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
        
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
   		ArrayList<String> categoriaSinRepe = new ArrayList<String>();//arrayList para meter las categorias sin repeticion
   		while(cPMiBase.moveToNext()){
   			String tipoPlato = cPMiBase.getString(0);
   			
   			if((!tipoSinRepe.contains(tipoPlato)) && (!tipoPlato.equals(""))){
   				tipoSinRepe.add(tipoPlato);
	   			//Sacamos los platos con tipoPlato igual al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoPlato = new String[]{"Id","Foto","Nombre","Precio"};
	   	    	String[] info = new String[]{tipoPlato,"Foster"};
	   	   		Cursor cPMiBase2 = dbMiBase.query("Restaurantes", infoPlato, "TipoPlato=? AND Restaurante=?",info,null, null,null);
	   	   		
	   	   		
	   	   		ArrayList<String> idHijos= new ArrayList<String>();
	   	   		ArrayList<String> numImags= new ArrayList<String>();
	   	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   	   		ArrayList<Double> precio= new ArrayList<Double>();
	   	   		while(cPMiBase2.moveToNext() ){
	   	   			idHijos.add(cPMiBase2.getString(0));
	   	   			numImags.add(cPMiBase2.getString(1));
	   	   			nombrePlatos.add(cPMiBase2.getString(2));
	   	   			precio.add(cPMiBase2.getDouble(3));
	   	   		}
   			
	   	   		HijoExpandableListAnadirPlato unHijo = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos,precio);
	   	   		PadreExpandableListAnadirPlato unPadre = new PadreExpandableListAnadirPlato(tipoPlato, unHijo);
	   	   		padres.add(unPadre);
   			}//fin de esta
   		}
   		
   		//Ha cargado todos los platos que tienen tipo
   		//Si tipo es vacio miramos la categoria
   		//Sacamos la categoria de la base de datos MiBase.db. Seran los padres
    	String[] infoTipoPlatoCat = new String[]{"Categoria"};
    	//solo leemos los platos de Foster
    	String[] datosCat = new String[]{"Foster",""};
   		Cursor cPMiBaseCat = dbMiBase.query("Restaurantes", infoTipoPlatoCat, "Restaurante=? AND TipoPlato=?" ,datosCat,null, null,null);
   		
   		while(cPMiBaseCat.moveToNext()){
   			String categoriaPlato = cPMiBaseCat.getString(0);
   			
   			if(!categoriaSinRepe.contains(categoriaPlato)){
   				categoriaSinRepe.add(categoriaPlato);
	   			//Sacamos los platos con categoriaPlato igual al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoPlato = new String[]{"Id","Foto","Nombre","Precio"};
	   	    	String[] info = new String[]{categoriaPlato,"Foster"};
	   	   		Cursor cPMiBaseCat2 = dbMiBase.query("Restaurantes", infoPlato, "Categoria=? AND Restaurante=?",info,null, null,null);
	   	   		
	   	   		ArrayList<String> idHijos= new ArrayList<String>();
	   	   		ArrayList<String> numImags= new ArrayList<String>();
	   	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   	   		ArrayList<Double> precio= new ArrayList<Double>();
	   	   		while(cPMiBaseCat2.moveToNext() ){
	   	   			idHijos.add(cPMiBaseCat2.getString(0));
	   	   			numImags.add(cPMiBaseCat2.getString(1));
	   	   			nombrePlatos.add(cPMiBaseCat2.getString(2));
	   	   			precio.add(cPMiBaseCat2.getDouble(3));
	   	   		}
   			
	   	   		HijoExpandableListAnadirPlato unHijo = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos,precio);
	   	   		PadreExpandableListAnadirPlato unPadre = new PadreExpandableListAnadirPlato(categoriaPlato, unHijo);
	   	   		padres.add(unPadre);
   			}//fin de esta
   		}
   	
   		expandableListAnadirPlato = (ExpandableListView) findViewById(R.id.expandableListPlatos);
		adapterExpandableListAnadirPlato = new MiExpandableListAdapterAnadirPlato(AnadirPlatos.this, padres);
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
					   String nombrePlato = c.getString(1);
					   //sacará ventana emergente
					   AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(AnadirPlatos.this);
					   ventanaEmergente.setNegativeButton("Cancelar", null);
					   onClickBotonAceptarAlertDialog(ventanaEmergente, nombrePlato);
					   View vistaAviso = LayoutInflater.from(AnadirPlatos.this).inflate(R.layout.ventana_emergente_editar_anadir_plato, null);
					   expandableListEditarExtras = (ExpandableListView) vistaAviso.findViewById(R.id.expandableListViewExtras);
					   actwObservaciones = (AutoCompleteTextView) vistaAviso.findViewById(R.id.autoCompleteTextViewObservaciones);
					   TextView encabezadoDialog = (TextView) vistaAviso.findViewById(R.id.textViewEditarAnadirPlato);
					   encabezadoDialog.setText("Añadir Plato");
					   TextView tituloPlato = (TextView) vistaAviso.findViewById(R.id.textViewTituloPlatoEditarYAnadir);
					   tituloPlato.setText(nombrePlato);
					   cargarExpandableListAnadirExtras(nombrePlato);
					   ventanaEmergente.setView(vistaAviso);
					   ventanaEmergente.show();
				    }
				      
				 });
	 }
	 
	 protected void cargarExpandableListAnadirExtras(String nombrePlato) {
		 HandlerGenerico sqlMiBase=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
		 SQLiteDatabase dbMiBase= sqlMiBase.open();
		 
		 String[] campos = new String[]{"Extras","Id"};
		 String[] datos = new String[]{"Foster",nombrePlato};
		 Cursor cursor = dbMiBase.query("Restaurantes",campos,"Restaurante=? AND Nombre=?",datos,null,null,null);
		 
		 cursor.moveToFirst();
		 String extrasPlato = cursor.getString(0);
		 String idPlatoPulsado = cursor.getString(1);
		 
  		 if(!extrasPlato.equals("")){
  			 String[] tokens = extrasPlato.split("/");
  			 ArrayList<PadreExpandableListEditar> categoriasExtras =  new ArrayList<PadreExpandableListEditar>();
  			 for(int i= 0; i< tokens.length ;i++){
  				 String[] nombreExtra = null;
  				 try{
  					 nombreExtra = tokens[i].split(":");
  					 String categoriaExtraPadre = nombreExtra[0];
  					 // Creamos los hijos, serán la variedad de extras
  					 String[] elementosExtra = null;
  					 elementosExtra = nombreExtra[1].split(",");
  					 ArrayList<HijoExpandableListEditar> variedadExtrasListaHijos = new ArrayList<HijoExpandableListEditar>();
  					 ArrayList<String> extrasHijo = new ArrayList<String>();
  					 boolean[] extrasPulsados = new boolean[elementosExtra.length];
  					 for(int j=0; j<elementosExtra.length;j++){
  						 extrasPulsados[j] = false;
  						 extrasHijo.add(elementosExtra[j]);
  						 }
  					 HijoExpandableListEditar extrasDeUnaCategoria = new HijoExpandableListEditar(extrasHijo, extrasPulsados);
  					 // Añadimos la información del hijo a la lista de hijos
  					 variedadExtrasListaHijos.add(extrasDeUnaCategoria);
  					 PadreExpandableListEditar padreCategoriaExtra = new PadreExpandableListEditar(idPlatoPulsado, categoriaExtraPadre, variedadExtrasListaHijos);
  					 if(i==0){//Expandimos el primer padre por estetica
						padreCategoriaExtra.setExpandido(true);
  					 }
  					 // Añadimos la información del padre a la lista de padres
  					 categoriasExtras.add(padreCategoriaExtra);
  				 }catch(Exception e){
					Toast.makeText(getApplicationContext(),"Error en el formato de extra en la BD", Toast.LENGTH_SHORT).show();
				 }
  			 }
  			 // Creamos el adapater para adaptar la lista a la pantalla.
  			 adapterExpandableListEditarExtras = new MiExpandableListAdapterEditar(getApplicationContext(), categoriasExtras,2);
  			 expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);  
  		 }else{
  			 //Actualizamos el adapter a null, ya que es static, para saber que este plato no tiene extras.
  			 adapterExpandableListEditarExtras = null;
  			 expandableListEditarExtras.setVisibility(ExpandableListView.INVISIBLE);
  		}
	}

	public static String getNumMesa() {
		return numMesa;	
	}
	 
	public static String getIdCamarero() {
		return idCamarero;
	}
	
	public static String getNumPersonas() {
		 return numPersonas;
	}
	
	public static void actualizaExpandableList() {
		expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListEditarExtras.expandGroup(groupPositionMarcar);
	}
	
	protected void onClickBotonAceptarAlertDialog(final Builder ventanaEmergente, final String nombrePlato) {
		
		
		ventanaEmergente.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				boolean bienEditado = true;
		    	String observaciones = null;
		    	String nuevosExtrasMarcados = null;
		    	if(!actwObservaciones.getText().toString().equals("")){
		        	observaciones = actwObservaciones.getText().toString();
		    	}
		    	if(adapterExpandableListEditarExtras!=null){ //Es un plato con extras
		    		nuevosExtrasMarcados = adapterExpandableListEditarExtras.getExtrasMarcados();
		    		if(nuevosExtrasMarcados == null){
		    			bienEditado = false;
		    		}
		    	}
		    	if(bienEditado){
		    		HandlerGenerico sqlMesas = null, sqlRestaurate = null;
		    		SQLiteDatabase dbMesas = null, dbRestaurante = null;
		    		try{
		    			sqlMesas=new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
		    			dbMesas = sqlMesas.open();
		    		}catch(SQLiteException e){
		    		 	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS MESA",Toast.LENGTH_SHORT).show();
		    		}
		    		try{
		    			sqlRestaurate =new HandlerGenerico(getApplicationContext(), "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
		    			dbRestaurante = sqlRestaurate.open();
		    		}catch(SQLiteException e){
		    		 	Toast.makeText(getApplicationContext(),"NO EXISTE BASE DE DATOS MiBase(Restaurante)",Toast.LENGTH_SHORT).show();
		    		}
		    		
		    		String[] campos = new String[]{"Id","Precio"};
		      		String[] datos = new String[]{"Foster",nombrePlato};
		      		
		      		Cursor cursor = dbMiBase.query("Restaurantes",campos,"Restaurante=? AND Nombre=?",datos,null,null,null); 
		      		cursor.moveToFirst();
		    		
		    		//Sacamos la fecha a la que el camarero ha introducido la mesa
                	Calendar cal = new GregorianCalendar();
                    Date date = cal.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String formatteDate = df.format(date);
                    //Sacamos la hora a la que el camarero ha introducido la mesa
                    Date dt = new Date();
                    SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
                    String formatteHour = dtf.format(dt.getTime());
                    
		        	ContentValues plato = new ContentValues();
		        	int idUnico = InicialCamarero.getIdUnico();
		        	plato.put("NumMesa", numMesa);
		        	plato.put("IdCamarero", idCamarero);
		        	plato.put("IdPlato", cursor.getString(0));
		        	plato.put("Observaciones", observaciones);
		        	plato.put("Extras", nuevosExtrasMarcados);
		        	plato.put("FechaHora", formatteDate + " " + formatteHour);
		        	plato.put("Nombre", nombrePlato);
		        	plato.put("Precio",cursor.getDouble(1));
		        	plato.put("Personas",numPersonas);
		        	plato.put("IdUnico", idUnico);
		        	dbMesas.insert("Mesas", null, plato);
		        	dbMesas.close();
		        	ContenidoListMesa platoNuevo = new ContenidoListMesa(nombrePlato,nuevosExtrasMarcados, observaciones, cursor.getDouble(1),idUnico,cursor.getString(0));
		        	Mesa.actualizaListPlatos(platoNuevo);
		    	}else{
		    		adapterExpandableListEditarExtras.expandeTodosLosPadres();
					Toast.makeText(getApplicationContext(),"¡Plato mal configurado!", Toast.LENGTH_SHORT).show();
		    	}				
			}
			
		});	
	}
	
}
 