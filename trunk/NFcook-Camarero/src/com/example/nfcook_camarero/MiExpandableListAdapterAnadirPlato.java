package com.example.nfcook_camarero;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fragments.PantallaMesasFragment;
import junit.framework.Assert;


import adapters.ContenidoListMesa;
import adapters.HijoExpandableListEditar;
import adapters.MiExpandableListAdapterEditar;
import adapters.PadreExpandableListEditar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MiExpandableListAdapterAnadirPlato extends BaseExpandableListAdapter{
	private LayoutInflater inflater;
    private ArrayList<PadreExpandableListAnadirPlato> padresExpandableList;
    private Context context;
    private ArrayList<PlatoView> platos;
    
    private static MiExpandableListAdapterEditar adapterExpandableListEditarExtras;
	private static ExpandableListView expandableListEditarExtras;
	private AutoCompleteTextView actwObservaciones;
    
    public MiExpandableListAdapterAnadirPlato(Context context, ArrayList<PadreExpandableListAnadirPlato> padres){
    	padresExpandableList = padres;
        inflater = LayoutInflater.from(context);
        this.context=context;
    }

	public Object getChild(int groupPosition, int childPosition) {
		return padresExpandableList.get(groupPosition).getHijo();
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	
			final int groupPositioMarcar = groupPosition;
		
			convertView = inflater.inflate(R.layout.contenido_hijo_lista_anadir_plato, parent,false);
			GridView gridViewAnadir = (GridView) convertView.findViewById(R.id.gridViewAnadirPlato);
			
			ArrayList<String> idHijos = padresExpandableList.get(groupPosition).getHijo().getIds();
			ArrayList<String> imgHijos = padresExpandableList.get(groupPosition).getHijo().getNumImagenes();
			ArrayList<String> nombreHijos = padresExpandableList.get(groupPosition).getHijo().getNombrePl();
			ArrayList<Double> precioHijos = padresExpandableList.get(groupPosition).getHijo().getPrecio();

			platos = new ArrayList<PlatoView>();
			//Recorremos con una variable que indica la posicion, porque necesitariamos tres iteradores.
			//Los tres ArrayList tienen el mismo tamaño
			int pos = 0; 
			while(pos < nombreHijos.size()){
				
				ImageView img = new ImageView(convertView.getContext());				
			    img.setImageResource(getDrawable(convertView.getContext(),imgHijos.get(pos)));
			
				//traer las cosas de platoView				
				PlatoView plato= new PlatoView(nombreHijos.get(pos),imgHijos.get(pos),idHijos.get(pos),precioHijos.get(pos));
	    		platos.add(plato);
	    		
	    		pos++;
			}
			
			/////   OPERACIONES PARA QUE SE EXPANDA EL GRIDVIEW COMPLETO AL PINCHAR EN UN PADRE
			///////////////////////////////////////////////////////////////////////////////////
			
		    final int spacingDp = 10; //espacio entre celdas en dp's
		    final int colWidthDp = 100; //tamaño de cada columna en dp's
		    final int rowHeightDp = 115; //tamaño de cada fila en dp's

		    // pasamos los dp's a pixeles
		    final float COL_WIDTH = convertView.getResources().getDisplayMetrics().density * colWidthDp;
		    final float ROW_HEIGHT = convertView.getResources().getDisplayMetrics().density * rowHeightDp;
		    final float SPACING = convertView.getResources().getDisplayMetrics().density * spacingDp;

		    // calculamos el número de filas y de columnas en nuestro gridView
		    final int colCount = (int)Math.floor((parent.getWidth() - (2 * SPACING)) / (COL_WIDTH + SPACING));
		    final int rowCount = (int)Math.ceil((platos.size() + 0d) / colCount);

		    // calculamos ahora la altura total del grid
		    final int GRID_HEIGHT = Math.round(rowCount * (ROW_HEIGHT + SPACING));

		    // set the height of the current grid
		    gridViewAnadir.getLayoutParams().height = GRID_HEIGHT;
			
			//Llamamos al adapter para que muestre en la pantalla los cambios realizados
			AnadirPlatoAdapter adapterAnadir;
			adapterAnadir = new AnadirPlatoAdapter(context, platos);
			gridViewAnadir.setAdapter(adapterAnadir);

			gridViewAnadir.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	//sacará ventana emergente       	
	            	String idPlatoPulsado = padresExpandableList.get(groupPositioMarcar).getIdPlato(position);
	            	
	            	AlertDialog.Builder ventanaEmergente = new AlertDialog.Builder(context);
	  	    		ventanaEmergente.setNegativeButton("Cancelar", null);
	  				onClickBotonAceptarAlertDialog(ventanaEmergente, groupPositioMarcar, position);
	  				View vistaAviso = LayoutInflater.from(context).inflate(R.layout.ventana_emergente_editar_anadir_plato, null);
	  				expandableListEditarExtras = (ExpandableListView) vistaAviso.findViewById(R.id.expandableListViewExtras);
	  				actwObservaciones = (AutoCompleteTextView) vistaAviso.findViewById(R.id.autoCompleteTextViewObservaciones);
	  				TextView encabezadoDialog = (TextView) vistaAviso.findViewById(R.id.textViewEditarAnadirPlato);
	  				encabezadoDialog.setText("Añadir Plato");
	  				TextView tituloPlato = (TextView) vistaAviso.findViewById(R.id.textViewTituloPlatoEditarYAnadir);
	  				tituloPlato.setText(padresExpandableList.get(groupPositioMarcar).getNombrePlato(position));
	  				cargarExpandableListAnadirExtras(idPlatoPulsado);
	  				ventanaEmergente.setView(vistaAviso);
	  				ventanaEmergente.show();

	                }
	        });
		
	    return convertView;
	
		}

	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	public Object getGroup(int groupPosition) {
		return padresExpandableList.get(groupPosition);
	}

	public int getGroupCount() {
		return padresExpandableList.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_anadir_plato, parent,false);
        }
 
        TextView textViewPadrePlato = (TextView) convertView.findViewById(R.id.textViewTipo);
        
        textViewPadrePlato.setText(getGroup(groupPosition).toString());
            
        return convertView;

	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
	
	/**
	 * 
	 * @param context
	 * @param name
	 * @return devuelve el entero que corresponde al drawable con nombre name
	 */
	public static int getDrawable(Context context, String name)
	{
	    Assert.assertNotNull(context);
	    Assert.assertNotNull(name);

	    return context.getResources().getIdentifier(name,
	            "drawable", context.getPackageName());
	}
	
	
	public void cargarExpandableListAnadirExtras(String idPlatoPulsado){
		HandlerGenerico sqlMiBase=new HandlerGenerico(context, "/data/data/com.example.nfcook_camarero/databases/", "MiBase.db");
		SQLiteDatabase dbMiBase= sqlMiBase.open();
  		String[] campos = new String[]{"Extras"};
  		String[] datos = new String[]{idPlatoPulsado};
  		
  		Cursor cursor = dbMiBase.query("Restaurantes",campos,"Id =?",datos,null,null,null); 
  		cursor.moveToFirst();
  		
  		String extrasPlato = cursor.getString(0);
  		  		
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
						for(int j=0; j<elementosExtra.length;j++)
						{
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
						Toast.makeText(context,"Error en el formato de extra en la BD", Toast.LENGTH_SHORT).show();
					}
				}
		        // Creamos el adapater para adaptar la lista a la pantalla.
		    	adapterExpandableListEditarExtras = new MiExpandableListAdapterEditar(context, categoriasExtras,0);
		        expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);  
  		}else{
  			//Actualizamos el adapter a null, ya que es static, para saber que este plato no tiene extras.
  			adapterExpandableListEditarExtras = null;
  			expandableListEditarExtras.setVisibility(ExpandableListView.INVISIBLE);
  		}
	}
	
	protected void onClickBotonAceptarAlertDialog(final Builder ventanaEmergente,final int groupPositionMarcar, final int position) {
		
		
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
		    		HandlerGenerico sqlMesas = null;
		    		SQLiteDatabase dbMesas = null;
		    		try{
		    			sqlMesas=new HandlerGenerico(context, "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
		    			dbMesas = sqlMesas.open();
		    		}catch(SQLiteException e){
		    		 	Toast.makeText(context,"NO EXISTE BASE DE DATOS MESA",Toast.LENGTH_SHORT).show();
		    		}
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
		        	String idPlato = padresExpandableList.get(groupPositionMarcar).getIdPlato(position);
		        	String nombrePlato = padresExpandableList.get(groupPositionMarcar).getNombrePlato(position);
		        	double precioPlato = padresExpandableList.get(groupPositionMarcar).getPrecioPlato(position);
		        	
		        	plato.put("NumMesa",AnadirPlatos.getNumMesa());
		        	plato.put("IdCamarero",AnadirPlatos.getIdCamarero());
		        	plato.put("IdPlato", idPlato);
		        	plato.put("Observaciones", observaciones);
		        	plato.put("Extras", nuevosExtrasMarcados);
		        	plato.put("FechaHora", formatteDate + " " + formatteHour);
		        	plato.put("Nombre", nombrePlato);
		        	plato.put("Precio", precioPlato);
		        	plato.put("Personas",AnadirPlatos.getNumPersonas());
		        	plato.put("IdUnico", idUnico);
		        	plato.put("Sincro", 0);
		        	dbMesas.insert("Mesas", null, plato);
		        	dbMesas.close();
		        	
		        	
		        	//Añadimos una unidad a las veces que se ha pedido el plato
		        	Mesa.actualizarNumeroVecesPlatoPedido(idPlato);
		        	
		        	AnadirPlatos.actualizaTopPedidos(padresExpandableList);
		        	
		        	Mesa.pintarBaseDatosMiFav();
		        	
		        	Mesa.actualizaListPlatos();
		        	
		    	}else{
		    		adapterExpandableListEditarExtras.expandeTodosLosPadres();
					Toast.makeText(context,"¡Plato mal configurado!", Toast.LENGTH_SHORT).show();
		    	}				
			}
			
		});
	}
	
	//FIXME
	/**
	 * Expande los padres que ya estuviesen expandidos al principio. De esta forma cuando hagamos una
	 *  modificación en la lista la encontraremos en el mismo estado, pero con esos elementos modificados.
	 */
	public void expandePadres(){
		for(int i=0;i<padresExpandableList.size();i++){
			if(padresExpandableList.get(i).isExpandido()){
				AnadirPlatos.expandeGrupo(i);
			}
		}
	}
	
	public static void actualizaExpandableList() {
		expandableListEditarExtras.setAdapter(adapterExpandableListEditarExtras);
	}
	
	//FIXME
	public void actualizaPadresExpandableList(ArrayList<PadreExpandableListAnadirPlato> p) {
		padresExpandableList = p;
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		expandableListEditarExtras.expandGroup(groupPositionMarcar);
	}
	
	@Override
	public void onGroupExpanded(int groupPosition){
		padresExpandableList.get(groupPosition).setExpandido(true);
	}
	
	@Override
	public void onGroupCollapsed(int groupPosition){
		padresExpandableList.get(groupPosition).setExpandido(false);
	}
	
	
	
}
