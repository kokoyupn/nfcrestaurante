package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import baseDatos.HandlerGenerico;

import com.example.nfcook_camarero.PedidoHistorico;
import com.example.nfcook_camarero.R;

import adapters.HijoExpandableListHistorico;
import adapters.MiExpandableListHistoricoAdapter;
import adapters.PadreExpandableListHistorico;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PantallaHistoricoFragment extends Fragment {

	private View vista;
	
	private HandlerGenerico sql;
	private SQLiteDatabase db;

	private static ExpandableListView ExpandableListHistorico;
	private MiExpandableListHistoricoAdapter AdapterHistorico; 
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		importarBaseDatatos();
		vista = inflater.inflate(R.layout.historico, container, false);
		LinearLayout fl = (LinearLayout) vista.findViewById(R.id.LinearLayoutHist);
		fl.setBackgroundColor(Color.rgb(191, 239, 255));
		crearExpandableList();
		return vista;
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
				
				ArrayList<HijoExpandableListHistorico> hijos = new ArrayList<HijoExpandableListHistorico>();
				
				String mesaConsulta=iteradorConjuntoMesas.next();
				
				String[] campos2 = new String[]{"FechaHora"};//Campos que quieres recuperar
				Cursor c2 = db.query(true,"Historico",campos2,"NumMesa=?",new String[]{mesaConsulta},null,null,null, null);
				Set<String> conjuntoPedidos = new HashSet<String>();
				while(c2.moveToNext()){
					conjuntoPedidos.add(c2.getString(0));
				}
				
				Iterator<String> iteradorConjuntoPedidos = conjuntoPedidos.iterator();

				while(iteradorConjuntoPedidos.hasNext()){

					String[] camposBusquedaObsExt = new String[]{"IdCamarero","sum(Precio)"};
					String Pedido = iteradorConjuntoPedidos.next();
					String[] datos = new String[]{mesaConsulta,Pedido};
					Cursor cursor = db.query("Historico", camposBusquedaObsExt, "NumMesa=? AND FechaHora=?", datos,null, null,null);
//					Double precioPedido=0.0;
//					while(cursor.moveToNext()){
//						//IdCamarero=
//						precioPedido+=cursor.getDouble(1);
//					}
//					cursor.moveToFirst();
//					String IdCamarero=cursor.getString(0);
					
					cursor.moveToFirst();
					String IdCamarero=cursor.getString(0);
					Double precioPedido=cursor.getDouble(1);
					
					HijoExpandableListHistorico unHijo = new HijoExpandableListHistorico(IdCamarero,Pedido,precioPedido);
					hijos.add(unHijo);
				}
				Cursor cursor=db.query("Historico",new String[]{"sum(Precio)"},"NumMesa=?",new String[]{mesaConsulta},null,null,null);
				cursor.moveToFirst();
				PadreExpandableListHistorico unPadre = new PadreExpandableListHistorico(mesaConsulta,cursor.getDouble(0),hijos);//precio);
				padres.add(unPadre);
			}

			ExpandableListHistorico = (ExpandableListView) vista.findViewById(R.id.expandableListHistorico);
			AdapterHistorico = new MiExpandableListHistoricoAdapter(this.getActivity().getApplicationContext(), padres);
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
			sql = new HandlerGenerico(getActivity().getApplicationContext(),"/data/data/com.example.nfcook_camarero/databases/","Historico.db"); 
			db = sql.open();
		}catch(SQLiteException e){
			Toast.makeText(getActivity().getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
		}	
	}
}
