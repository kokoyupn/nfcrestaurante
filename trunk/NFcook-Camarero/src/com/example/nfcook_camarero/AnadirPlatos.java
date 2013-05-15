package com.example.nfcook_camarero;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fragments.PantallaMesasFragment;
import adapters.HijoExpandableListEditar;
import adapters.MiCursorAdapterBuscadorPlatos;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
	private static HandlerGenerico sqlMiBaseFav;
	private AutoCompleteTextView buscador;
	private boolean noSeleccionadoAutoCompleteTextView;
	private SQLiteDatabase dbMiBase, dbBuscador;
	private static SQLiteDatabase dbMiBaseFav;
	public static Activity actividad;
	
	private static String numMesa;
	private static String idCamarero;
	private static String numPersonas; 
	
	ArrayList<PadreExpandableListAnadirPlato> padres;
	
	private static ExpandableListView expandableListEditarExtras;
	private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private AutoCompleteTextView actwObservaciones;
	private static String restaurante;
	
	private Parcelable mstate;
	
	//private ArrayList<InfoPlato> platosAñadidos; //aqui vaos guardando los platos que ha añadido para luego pasarselos a la pantalla de Mesa 
	//cuando añade un plato se añade a la base de datos de mesas
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		noSeleccionadoAutoCompleteTextView = false;
        super.onCreate(savedInstanceState);
        actividad = this;
        
        
        setContentView(R.layout.expandable_list_anadir_plato);
        
        // Recogemos ActionBar
        ActionBar actionbar = getActionBar();
    	actionbar.setTitle(" AÑADIR PLATO");
        
    	// atras en el action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
    	
        Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		restaurante = bundle.getString("Restaurante");
        
		cargarBarraDeBusqueda();
        crearExpandableList();
	}
	
	//  para el atras del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){       
    	finish();
		return false;
    }
	
	public void crearExpandableList() {	  
		padres = new ArrayList<PadreExpandableListAnadirPlato>();
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
    	//solo los platos del restaurante que corresponda
    	
    	String[] datos = new String[]{restaurante};

   		Cursor cPMiBase = dbMiBase.query("Restaurantes", infoTipoPlato, "Restaurante=?" ,datos,null, null,null);
   		
   		ArrayList<String> tipoSinRepe = new ArrayList<String>();//arrayList para meter los tipos sin repeticion
   		ArrayList<String> categoriaSinRepe = new ArrayList<String>();//arrayList para meter las categorias sin repeticion
   		
   		//FIXME Rober 2/5/2013 --------------------------
   		generarTopPedidos(padres);
   		//Rober 2/5/2013 --------------------------
   		
   		while(cPMiBase.moveToNext()){
   			String tipoPlato = cPMiBase.getString(0);
   			
   			if((!tipoSinRepe.contains(tipoPlato)) && (!tipoPlato.equals(""))){
   				tipoSinRepe.add(tipoPlato);
	   			//Sacamos los platos con tipoPlato igual al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoPlato = new String[]{"Id","Foto","Nombre","Precio"};
	   	    	
	   	    	String[] info = new String[]{tipoPlato,restaurante};
	   	    	

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
    	//leemos los platos del restaurante correspondiente
    
    	String[] datosCat = new String[]{restaurante,""};

   		Cursor cPMiBaseCat = dbMiBase.query("Restaurantes", infoTipoPlatoCat, "Restaurante=? AND TipoPlato=?" ,datosCat,null, null,null);
   		
   		while(cPMiBaseCat.moveToNext()){
   			String categoriaPlato = cPMiBaseCat.getString(0);
   			
   			if(!categoriaSinRepe.contains(categoriaPlato) && !categoriaPlato.equals("Bebidas")){
   				categoriaSinRepe.add(categoriaPlato);
	   			//Sacamos los platos con categoriaPlato igual al del padre de la base de datos MiBase.db. Seran los hijos
	   	    	String[] infoPlato = new String[]{"Id","Foto","Nombre","Precio"};
	   	
	   	    	String[] info = new String[]{categoriaPlato,restaurante};
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
	
	
	 public static void generarTopPedidos(ArrayList<PadreExpandableListAnadirPlato> padres) {
		 try{
			sqlMiBaseFav = new HandlerGenerico(actividad, "/data/data/com.example.nfcook_camarero/databases/", "MiBaseFav.db");
			dbMiBaseFav = sqlMiBaseFav.open();
			
			String[] campos = new String[]{"Id","Foto","Nombre","Precio","VecesPedido"};
	    	String[] restricciones = new String[]{restaurante};
	    	
	    	Cursor platosVeces = dbMiBaseFav.query("Restaurantes", campos, "Restaurante=?",restricciones,null, null,null);
	   		
	    	ArrayList<String> idHijos= new ArrayList<String>();
	   		ArrayList<String> numImags= new ArrayList<String>();
	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   		ArrayList<Double> precio= new ArrayList<Double>();
	   		
	   		ArrayList<Integer> vecesPedidoLosTops= new ArrayList<Integer>();//Array que guarda el numero de veces que se han pedido los mas pedidos
	    	
	   		while(platosVeces.moveToNext()){
	    		
	    		String veces = platosVeces.getString(4);
	    		if( veces == null )//Si ese plato no ha sido pedido nunca, no habra nada y sera null y petará
	    			veces = "0";
	    		
	    		boolean seguir=true;
	    		int i=0;
	    		while(seguir && i<6){
	    				//Rellenas cuando aun hay nulls en el array de los mas pedidos y el valor de la DB es >0
	    				if(vecesPedidoLosTops.size() < 6){
			    			if( Integer.parseInt(veces)>0 ){//Compruebo solo el idHijos porque si este es null, los demas tambien lo seran
			    				idHijos.add(platosVeces.getString(0));
					   			numImags.add(platosVeces.getString(1));
					   			nombrePlatos.add(platosVeces.getString(2));
					   			precio.add(platosVeces.getDouble(3));
					   			
					   			vecesPedidoLosTops.add( Integer.parseInt(veces) );//Vas añadiendo en las mismas posiciones que es los otros arrays
					   		}	
					   		//En caso de que la posicion sea vacia, lo añadas o no, tienes que cambiar de elemento de la DB	
			    			seguir=false;
			    			
			    		//Aqui ya es cuando tienes que comparar
			    		}else{
			    			
			    			//Calculo el minimo
		    				int minimo = Integer.MAX_VALUE;
		    				int posMin = 0;
		    				for (int j=0;j<vecesPedidoLosTops.size();j++){
		    					System.out.println("LLEGA");
		    					if(vecesPedidoLosTops.get(j) < minimo){
		    						System.out.println("vecesPedidoLosTops de: "+j+" "+vecesPedidoLosTops.get(j));
		    						minimo = vecesPedidoLosTops.get(j);
		    						posMin = j;
		    					}
		    				}
		    				
			    			if( Integer.parseInt(veces)>minimo ){
			    				idHijos.set(posMin, platosVeces.getString(0));
					   			numImags.set(posMin,platosVeces.getString(1));
					   			nombrePlatos.set(posMin,platosVeces.getString(2));
					   			precio.set(posMin,platosVeces.getDouble(3));
					   			
					   			vecesPedidoLosTops.set(posMin, Integer.parseInt(veces) );//Vas añadiendo en las mismas posiciones que es los otros arrays
					   		}
			    			
			    			seguir=false;
			    		}
	    		}
	   		}
	    	
	   	platosVeces.close();//Se cierra el cursor para que no de problemas
	  
		HijoExpandableListAnadirPlato hijosTop = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos,precio);
   		PadreExpandableListAnadirPlato top = new PadreExpandableListAnadirPlato("TOP Pedidos", hijosTop);
   		padres.add(top);
   		
	   	
		}catch(Exception e){
			 System.out.println("Error en generarTopPedidos");
		}
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
		    			" WHERE Restaurante ='"+ restaurante +"' and nombre LIKE '%" +""+ "%' ", null);
		    buscador.setAdapter(new MiCursorAdapterBuscadorPlatos(getApplicationContext(), c, CursorAdapter.NO_SELECTION, restaurante));
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
		
		 String[] datos = new String[]{restaurante, nombrePlato};
		 
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
	
	public static void expandeGrupo(int pos) {
		expandableListAnadirPlato.expandGroup(pos);
	}
	
	protected void onClickBotonAceptarAlertDialog(final Builder ventanaEmergente, final String nombrePlato) {
		
		
		ventanaEmergente.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				boolean bienEditado = true;
		    	String observaciones = "";
		    	String nuevosExtrasMarcados = "";
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
		    		String[] datos = new String[]{restaurante, nombrePlato};
		    		
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
		        	int idUnico = PantallaMesasFragment.getIdUnico();
		        	PantallaMesasFragment.getInstanciaClase().setUltimoIdentificadorUnico();
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
		        	plato.put("Sincro", 0);
		        	dbMesas.insert("Mesas", null, plato);
		        	dbMesas.close();
		        	
		        	//Añadimos una unidad a las veces que se ha pedido el plato
		        	Mesa.actualizarNumeroVecesPlatoPedido(cursor.getString(0));
		        	
		        	//crearExpandableList();//Esto iba pero se quedaba cerrado
		        	actualizaTopPedidos(padres);
		        	
		        	Mesa.pintarBaseDatosMiFav();
		        	
		        	Mesa.actualizaListPlatos();
		        	
		        	
		    	}else{
		    		adapterExpandableListEditarExtras.expandeTodosLosPadres();
					Toast.makeText(getApplicationContext(),"¡Plato mal configurado!", Toast.LENGTH_SHORT).show();
		    	}				
			}
			
		});	
	}
	
	public static void actualizaTopPedidos(ArrayList<PadreExpandableListAnadirPlato> padresExpandableList) {
		 try{
			sqlMiBaseFav = new HandlerGenerico(actividad, "/data/data/com.example.nfcook_camarero/databases/", "MiBaseFav.db");
			dbMiBaseFav = sqlMiBaseFav.open();
			
			String[] campos = new String[]{"Id","Foto","Nombre","Precio","VecesPedido"};
	    	String[] restricciones = new String[]{restaurante};
	    	
	    	Cursor platosVeces = dbMiBaseFav.query("Restaurantes", campos, "Restaurante=?",restricciones,null, null,null);
	   		
	    	ArrayList<String> idHijos= new ArrayList<String>();
	   		ArrayList<String> numImags= new ArrayList<String>();
	   		ArrayList<String> nombrePlatos= new ArrayList<String>();
	   		ArrayList<Double> precio= new ArrayList<Double>();
	   		
	   		ArrayList<Integer> vecesPedidoLosTops= new ArrayList<Integer>();//Array que guarda el numero de veces que se han pedido los mas pedidos
	    	
	   		while(platosVeces.moveToNext()){
	    		
	    		String veces = platosVeces.getString(4);
	    		if( veces == null )//Si ese plato no ha sido pedido nunca, no habra nada y sera null y petará
	    			veces = "0";
	    		
	    		boolean seguir=true;
	    		int i=0;
	    		while(seguir && i<6){
	    				//Rellenas cuando aun hay nulls en el array de los mas pedidos y el valor de la DB es >0
	    				if(vecesPedidoLosTops.size() < 6){
			    			if( Integer.parseInt(veces)>0 ){//Compruebo solo el idHijos porque si este es null, los demas tambien lo seran
			    				idHijos.add(platosVeces.getString(0));
					   			numImags.add(platosVeces.getString(1));
					   			nombrePlatos.add(platosVeces.getString(2));
					   			precio.add(platosVeces.getDouble(3));
					   			
					   			vecesPedidoLosTops.add( Integer.parseInt(veces) );//Vas añadiendo en las mismas posiciones que es los otros arrays
					   		}	
					   		//En caso de que la posicion sea vacia, lo añadas o no, tienes que cambiar de elemento de la DB	
			    			seguir=false;
			    			
			    		//Aqui ya es cuando tienes que comparar
			    		}else{
			    			
			    			//Calculo el minimo
		    				int minimo = Integer.MAX_VALUE;
		    				int posMin = 0;
		    				for (int j=0;j<vecesPedidoLosTops.size();j++){
		    					System.out.println("LLEGA");
		    					if(vecesPedidoLosTops.get(j) < minimo){
		    						System.out.println("vecesPedidoLosTops de: "+j+" "+vecesPedidoLosTops.get(j));
		    						minimo = vecesPedidoLosTops.get(j);
		    						posMin = j;
		    					}
		    				}
		    				
			    			if( Integer.parseInt(veces)>minimo ){
			    				idHijos.set(posMin, platosVeces.getString(0));
					   			numImags.set(posMin,platosVeces.getString(1));
					   			nombrePlatos.set(posMin,platosVeces.getString(2));
					   			precio.set(posMin,platosVeces.getDouble(3));
					   			
					   			vecesPedidoLosTops.set(posMin, Integer.parseInt(veces) );//Vas añadiendo en las mismas posiciones que es los otros arrays
					   		}
			    			
			    			seguir=false;
			    		}
	    		}
	   		}
	    	
	   	platosVeces.close();//Se cierra el cursor para que no de problemas
	  
		HijoExpandableListAnadirPlato hijosTop = new HijoExpandableListAnadirPlato(idHijos,numImags,nombrePlatos,precio);
  		PadreExpandableListAnadirPlato top = new PadreExpandableListAnadirPlato("TOP Pedidos", hijosTop);
  		top.setExpandido(padresExpandableList.get(0).isExpandido());
  		padresExpandableList.set(0, top);
  		
  		adapterExpandableListAnadirPlato = new MiExpandableListAdapterAnadirPlato(actividad, padresExpandableList);
		
  		//Para que vuelva a abrir los padres que estuvieran expandidos, no hace falta el exandePadres()
  		Parcelable state = expandableListAnadirPlato.onSaveInstanceState();
  		expandableListAnadirPlato.setAdapter(adapterExpandableListAnadirPlato);
  		expandableListAnadirPlato.onRestoreInstanceState(state);
  		//adapterExpandableListAnadirPlato.expandePadres();
  		
  		}catch(Exception e){
			 System.out.println("Error en generarTopPedidos");
		}
		 
		 
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	    mstate = expandableListAnadirPlato.onSaveInstanceState();
	    state.putParcelable("listState", mstate);
	}
	
	protected void onRestoreInstanceState(Bundle state) {
	    super.onRestoreInstanceState(state);
	    mstate = state.getParcelable("listState");
	}
	
	
}
 