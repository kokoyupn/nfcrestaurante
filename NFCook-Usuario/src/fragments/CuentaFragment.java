package fragments;

import com.example.nfcook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CuentaFragment extends Fragment{
	private View vista;
	private float total;
	
	public String[] platos; // Atributo de prueba para ver que funciona todo.
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.cuenta, container, false);
		
		TableLayout tableLayout = (TableLayout)vista.findViewById(R.id.tableLayoutCuenta);
        TableRow fila = (TableRow)inflater.inflate(R.layout.tablerowcuenta,tableLayout,false);
       
        TextView descripcion = (TextView)fila.findViewById(R.id.textViewDescripcionCuenta);
        TextView precio = (TextView)fila.findViewById(R.id.textViewPrecioCuenta);
        
        descripcion.setText("Descripcion");
        descripcion.setTextSize(20);
        precio.setText("Precio");
        precio.setTextSize(20);
        
        tableLayout.addView(fila);
        
        View v = (View)inflater.inflate(R.layout.separador,tableLayout,false);
        tableLayout.addView(v);
        
        total=0;
        
        for (int j = 0; j<platos.length; j = j+2){
        	total += (float) Double.parseDouble(platos[j+1]);
       
            TableRow f1 = (TableRow)inflater.inflate(R.layout.tablerowcuenta,tableLayout,false);
            
            TextView c1 =(TextView)f1.findViewById(R.id.textViewDescripcionCuenta);
            c1.setText(platos[j]);
            c1.setTextSize(12);
            c1.setPadding(5, 0, 0, 0);
            
            TextView c2 =(TextView)f1.findViewById(R.id.textViewPrecioCuenta);
            c2.setText(platos[j+1]+" €");
            c2.setTextSize(12);
            tableLayout.addView(f1);
        }
       
        TableRow f2 = (TableRow)inflater.inflate(R.layout.tablerowcuenta,tableLayout,false);
        
        TextView t1 =(TextView)f2.findViewById(R.id.textViewDescripcionCuenta);
        t1.setText("TOTAL A PAGAR:");
        t1.setTextSize(18);
        t1.setPadding(5, 10, 0, 0);
        
        TextView t2 =(TextView)f2.findViewById(R.id.textViewPrecioCuenta);
        t2.setText(total+" €");
        t2.setTextSize(18);
       
        tableLayout.addView(f2);

		return vista;
    }
	
	public float getTotal() {
		return total;
	}
	
}
