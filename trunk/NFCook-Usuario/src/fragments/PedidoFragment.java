package fragments;

import com.example.nfcook.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PedidoFragment extends Fragment{
	private float total;
	private View vista;
	private TableLayout tableLayout;
	
	public String[] platos; // Atributo de prueba para ver que funciona todo.
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.pedido, container, false);
             
        tableLayout= (TableLayout)vista.findViewById(R.id.tableLayoutPedido);
      
        TableRow fila = (TableRow)inflater.inflate(R.layout.tablerowpedido,tableLayout,false);
       
        TextView descripcion =(TextView)fila.findViewById(R.id.textViewDescripcionPedido);
        TextView precio =(TextView)fila.findViewById(R.id.textViewPrecioPedido);
        
        CheckBox c = (CheckBox) fila.findViewById(R.id.checkBox1);
        c.setVisibility(CheckBox.INVISIBLE);
        
        descripcion.setText("Descripcion");
        descripcion.setTextSize(20);
        precio.setText("Precio");
        precio.setTextSize(20);
        
        tableLayout.addView(fila);
       
        
        View v =(View)inflater.inflate(R.layout.separador,tableLayout,false);
        tableLayout.addView(v);
        
        total=0;
        for (int j = 0; j<platos.length; j = j+2){
       	            
           total += (float) Double.parseDouble(platos[j+1]);
            
            TableRow f1 = (TableRow)inflater.inflate(R.layout.tablerowpedido,tableLayout,false);
            
            TextView c1 =(TextView)f1.findViewById(R.id.textViewDescripcionPedido);
            c1.setText(platos[j]);
            c1.setTextSize(12);
            c1.setPadding(5, 0, 0, 0);
            
            TextView c2 =(TextView)f1.findViewById(R.id.textViewPrecioPedido);
            c2.setText(platos[j+1]+" €");
            c2.setTextSize(12);
            tableLayout.addView(f1);
        }
        
        final TextView textTotal =(TextView) vista.findViewById(R.id.total);
        textTotal.setText("TOTAL A PAGAR: "+ total);  
      
        final Button eliminar = (Button) vista.findViewById(R.id.eliminar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	for (int i=2;i<tableLayout.getChildCount();i++){
           		TableRow r = (TableRow)tableLayout.getChildAt(i);
           		if (((CheckBox) r.getChildAt(2)).isChecked()){
           			String s=((TextView) r.getChildAt(1)).getText().toString();
           			s=s.substring(0, s.length()-2);
           			total-=(float)Double.parseDouble(s);
                   	 tableLayout.removeViewAt(i);
                   	 i--;
                    }
           		
            	}
            	textTotal.setText("TOTAL A PAGAR: "+ total);
            }
        }); 
        
        return vista;    
   }
   
	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}
	
}
