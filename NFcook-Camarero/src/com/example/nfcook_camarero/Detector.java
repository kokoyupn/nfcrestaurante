package com.example.nfcook_camarero;

import adapters.ContenidoListMesa;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class Detector extends SimpleOnGestureListener {
    private ListView list;
    private static int itemId;
    private HandlerGenerico sqlMesas;
    private SQLiteDatabase dbMesas;
    private static boolean seleccionado;
    

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try{
	    	System.out.println("Entra");
	        seleccionado=false;
	        
	        if (e2.getX() - e1.getX() > 10){
	        	itemId = Mesa.getPlatos().pointToPosition((int) e1.getX(), (int) e1.getY());
	        	System.out.println("Entra if y posicion:"+itemId);
	        	View v = Mesa.getPlatos().getChildAt(itemId);
	        	v.setBackgroundColor(Color.GRAY);
	            Button delete = (Button) v.findViewById(R.id.boton_borrar);
	            delete.setVisibility(1);
	            seleccionado=true;
	            
	            delete.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View view) {
	                	seleccionado=false;
	                	
	                	ContenidoListMesa platoSeleccionado = (ContenidoListMesa)Mesa.getAdapter().getItem(itemId);
	    				String identificador = Integer.toString(platoSeleccionado.getId());
	    				try{
	    					sqlMesas=new HandlerGenerico(Mesa.getContext(), "/data/data/com.example.nfcook_camarero/databases/", "Mesas.db");
	    					dbMesas= sqlMesas.open();
	    					
	    					dbMesas.delete("Mesas", "IdUnico=?",new String[]{identificador});
	    				}catch(Exception e){
	    					System.out.println("Error borrar de la base pedido en ondrag");
	    				}
	    				
	    				Mesa.getAdapter().deletePosicion(itemId);
	    				Mesa.getPlatos().setAdapter(Mesa.getAdapter());
	    				
	    				//Recalculamos el precio(será cero ya que no quedan platos en la lista)
	            		Mesa.getPrecioTotal().setText(Double.toString( Math.rint( Mesa.getAdapter().getPrecio()*100/100 )) +" €");
	                }
	            });
	            //if (showDeleteButton(e1))
	                return true;
	        }
        }catch(Exception e){
        	System.out.println("Error onFling");
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    /*private boolean showDeleteButton(MotionEvent e1) {
    	System.out.println("Entra1");
        int pos = list.pointToPosition((int)e1.getX(), (int)e1.getY());
        System.out.println("Entra2");
        return showDeleteButton(pos);
    }*/

    /*private boolean showDeleteButton(int pos) {
        View child = list.getChildAt(pos);
        if (child != null){
            Button delete = (Button) child.findViewById(R.id.boton_borrar);
            if (delete != null)
                if (delete.getVisibility() == View.INVISIBLE)
                    delete.setVisibility(View.VISIBLE);
                else
                    delete.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }*/

	public static boolean getSeleccionado() {
		return seleccionado;
	}

	public static int getitemId() {
		return itemId;
	}

	public static void setSeleccionado(boolean b) {
		seleccionado = b;
		
	}



	
}
