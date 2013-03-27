package adapters;


import baseDatos.HandlerDB;

import com.example.nfcook.R;

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

public class MiCursorAdapterBuscadorPlatos extends CursorAdapter{
	
	private HandlerDB sql;
	private SQLiteDatabase db;
	private String restaurante;
	Context context;
	
	public MiCursorAdapterBuscadorPlatos(Context context, Cursor c, int flags,String restaurante) {
		super(context, c, flags);
		this.context = context;
		this.restaurante = restaurante;
		// TODO Auto-generated constructor stub
	}

	@Override
   public void bindView(View view, Context arg1, Cursor cursor) {
     String item = createItem(cursor);
     ((TextView ) view).setText(item);  
   }

   @Override
   public View newView(Context context, Cursor cursor, ViewGroup parent) {
     final LayoutInflater inflater = LayoutInflater.from(context);
     final TextView view = (TextView) inflater.inflate(R.layout.textview_buscador_platos, parent, false);
        
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
     
     currentCursor = db.rawQuery("SELECT Id AS _id, Nombre AS item" +
       " FROM Restaurantes" + 
       " WHERE Restaurante ='"+ restaurante+"' and Nombre LIKE '%" +args+ "%' ", null);
  
     return currentCursor;
   }
 
   private String createItem(Cursor cursor){
     String item = cursor.getString(1);
     return item;
   }
   
   private void importarBaseDatatos(){
       try{
       	sql = new HandlerDB(context); 
       	db = sql.open();
       }catch(SQLiteException e){
        	Toast.makeText(context,"NO EXISTE",Toast.LENGTH_SHORT).show();
       }	
	}
}
