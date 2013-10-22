package adapters;

import com.example.nfcook_gerente.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

 /**
  * 
  * @author roberto
  * 
  * Esta clase contiene los metodos necesarios para el adapter 
  * del buscador de los empleados.
  *
  */

public class MiCursorAdapterBuscadorEmpleados extends CursorAdapter{
		
		private HandlerGenerico sql;
		private SQLiteDatabase db;
		private String idEmpleado;
		Context context;
		
		public MiCursorAdapterBuscadorEmpleados(Activity activity, Cursor c, int flags) {
			super(activity.getApplicationContext(), c, flags);
			this.context = activity.getApplicationContext();
			
			
		}

		@Override
	   public void bindView(View view, Context arg1, Cursor cursor) {
	     String item = createItem(cursor);
	     ((TextView ) view).setText(item);  
	   }

	   @Override
	   public View newView(Context context, Cursor cursor, ViewGroup parent) {
	     final LayoutInflater inflater = LayoutInflater.from(context);
	     final TextView view = (TextView) inflater.inflate(R.layout.textview_buscador_empleados, parent, false);
	        
	     String item = createItem(cursor);
	     view.setText(item);
	        
	     return view;
	   }

	   @Override
	   public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
	     Cursor currentCursor = null;
	         
	     if (getFilterQueryProvider() != null) {
	        return getFilterQueryProvider().runQuery(constraint);
	     }
	         
	     String args = "";
	     if (constraint != null) {
	        args = constraint.toString(); 
	        
	     }
	  
	     importarBaseDatatos();
	     
	     currentCursor = db.rawQuery("SELECT IdEmpleado AS _id,Nombre AS Item" +
	       " FROM Empleados" + 
	       " WHERE Nombre LIKE '%" +args+ "%'  or Apellido1 LIKE '%" +args+ "%' or Apellido2 LIKE '%" +args+ "%'", null);
	    // 
	     /** Ejemplo para usar el like con varias condiciones
	      * if (name.length() != 0) {

        	name = "%" + name + "%";
		    }
		    if (email.length() != 0) {
		        email = "%" + email + "%";
		    }
		    if (Phone.length() != 0) {
		        Phone = "%" + Phone + "%";
		    }
		    String selectQuery = " select * from tbl_Customer where Customer_Name like  '"
		            + name
		            + "' or Customer_Email like '"
		            + email
		            + "' or Customer_Phone like '"
		            + Phone
		            + "' ORDER BY Customer_Id DESC";
		
		    Cursor cursor = mDb.rawQuery(selectQuery, null);`
	      */
	  
	     return currentCursor;
	   }
	 
	   private String createItem(Cursor cursor){
	     String item = cursor.getString(1);
	     return item;
	   }
	   
	   private void importarBaseDatatos(){
	       try{
	       	sql = new HandlerGenerico(context, "/data/data/com.example.nfcook_gerente/databases/", "Empleados.db"); 
	       	db = sql.open();
	       }catch(SQLiteException e){
	        	Toast.makeText(context,"NO EXISTE",Toast.LENGTH_SHORT).show();
	       }	
		}
	}
