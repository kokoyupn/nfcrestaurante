package com.example.nfcook_camarero;
import java.util.ArrayList;  
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.nfcook_camarero.R;



import adapters.HijoExpandableListHistorico;
import adapters.MiExpandableListAdapterHistorico;
import adapters.PadreExpandableListHistorico;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;  
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class Historico extends Activity {  

	private HandlerGenerico sql;
	private SQLiteDatabase db;

	private static ExpandableListView ExpandableListHistorico;
	private MiExpandableListAdapterHistorico AdapterHistorico; 
	//private static View vistaConExpandaleList;



	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		importarBaseDatatos();
		crearExpandableList();
		super.onCreate(savedInstanceState);  

		ExpandableListHistorico=new ExpandableListView(this);
		setContentView(ExpandableListHistorico);
	}

	public void crearExpandableList() {
		try{
			String[] campos = new String[]{"NumMesa"};//Campos que quieres recuperar
			Cursor c = db.query(true,"Historico",campos,null,null,null,null,null, null);
			Set<String> conjuntoMesas = new HashSet<String>();
			while(c.moveToNext()){
				conjuntoMesas.add(c.getString(0));
			}

			final ArrayList<PadreExpandableListHistorico> padres = new ArrayList<PadreExpandableListHistorico>();
			Iterator<String> iteradorConjuntoMesas = conjuntoMesas.iterator();
			while(iteradorConjuntoMesas.hasNext()){

				String[] campos2 = new String[]{"FechaHora"};//Campos que quieres recuperar
				Cursor c2 = db.query(true,"Historico",campos,null,null,null,null,null, null);
				Set<String> conjuntoPedidos = new HashSet<String>();
				while(c2.moveToNext()){
					conjuntoPedidos.add(c2.getString(0));
				}

				ArrayList<HijoExpandableListHistorico> hijos = new ArrayList<HijoExpandableListHistorico>();
				Iterator<String> iteradorConjuntoPedidos = conjuntoPedidos.iterator();
				String mesaConsulta=iteradorConjuntoMesas.next();
				while(iteradorConjuntoPedidos.hasNext()){

					String[] camposBusquedaObsExt = new String[]{"FechaHora","IdCamarero","sum(Precio)"};
					String Pedido = iteradorConjuntoPedidos.next();
					String[] datos = new String[]{mesaConsulta, Pedido};
					Cursor cursor = db.query("Historico", camposBusquedaObsExt, "NumMesa=? AND FechaHora=?", datos,null, null,null);
					HijoExpandableListHistorico unHijo = new HijoExpandableListHistorico(cursor.getString(1), cursor.getString(0), cursor.getDouble(2));
					hijos.add(unHijo);
				}
				double precio= db.query("Historico",new String[]{"sum(Precio)"},"NumMesa=?",new String[]{mesaConsulta},null,null,null).getDouble(0);
				PadreExpandableListHistorico unPadre = new PadreExpandableListHistorico(mesaConsulta,precio);
				padres.add(unPadre);
			}

			ExpandableListHistorico = (ExpandableListView) findViewById(R.layout.historico);
			AdapterHistorico = new MiExpandableListAdapterHistorico(this.getApplicationContext(), padres);
			ExpandableListHistorico.setAdapter(AdapterHistorico);

			ExpandableListHistorico.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


				public boolean onChildClick(ExpandableListView arg0, View arg1,
						int arg2, int arg3, long arg4) {
					Intent intent = new Intent(arg1.getContext(), PedidoHistorico.class);

					intent.putExtra("mesa", padres.get(arg2).getnumMesa());
					intent.putExtra("hora", padres.get(arg2).getHijoAt(arg3).getHora());

					startActivity(intent); 
					return false;
				}
			});
		}catch(SQLiteException e){

		}   
	}

	public static void expandeGrupoLista(int groupPositionMarcar) {
		ExpandableListHistorico.expandGroup(groupPositionMarcar);
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