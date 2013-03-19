package generar_bd;

import java.text.SimpleDateFormat;
import java.util.Date;

import baseDatos.Handler;

import com.example.nfcook.R;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;

public class BaseDatosMesas extends Activity {	   
	public Handler sql;
	public SQLiteDatabase db;
	public Integer idUnico;

	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_generada);
        idUnico = 0;
        
        // Importamos la base de datos donde vamos a hacer la carga de los platos pedidos
        try{
        	sql=new Handler(getApplicationContext(),"Mesas.db"); 
        	db=sql.open();
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
        }
	//Insertamos el plato en la tabla Platos
	ContentValues registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh1");
	registro.put("Observaciones",  "Sin pepinillo");
	registro.put("Extras", "");
	registro.put("FechaHora", "2013-03-19" + "13:45:23");
	registro.put("Nombre", "Bacon & Cheese Fries");
	registro.put("Precio", 8.35);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++ ;
   	
   	registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh1");
	registro.put("Observaciones",  "Sin pepinillo");
	registro.put("Extras", "Frambuesa");
	registro.put("FechaHora", "2013-03-19" + "13:45:23");
	registro.put("Nombre", "Bacon & Cheese Fries");
	registro.put("Precio", 8.35);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
   	registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh4");
	registro.put("Observaciones",  "Sin salsa");
	registro.put("Extras", "Frambuesa");
	registro.put("FechaHora", "2013-03-19" + "13:45:23");
	registro.put("Nombre", "Mini Corn Dogs");
	registro.put("Precio", 7.1);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
   	registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh37");
	registro.put("Observaciones",  "Muy fria");
	registro.put("Extras", "");
	registro.put("FechaHora", "2013-03-19" + "13:45:23");
	registro.put("Nombre", "CocaCola");
	registro.put("Precio", 3);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
   	registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh37");
	registro.put("Observaciones",  "Muy fria");
	registro.put("Extras", "");
	registro.put("FechaHora", "2013-03-19" + "13:45:23");
	registro.put("Nombre", "CocaCola");
	registro.put("Precio", 3);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
  	registro = new ContentValues();
	registro.put("NumMesa", "5");
	registro.put("IdCamarero", "23");
	registro.put("IdPlato", "fh4");
	registro.put("Observaciones",  "Con mucha salsa");
	registro.put("Extras", "Ranchera");
	registro.put("FechaHora", "2013-03-18" + "16:25:13");
	registro.put("Nombre", "Mini Corn Dogs");
	registro.put("Precio", 7.1);
	registro.put("Personas", "1");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
   	registro = new ContentValues();
	registro.put("NumMesa", "1");
	registro.put("IdCamarero", "22");
	registro.put("IdPlato", "fh38");
	registro.put("Observaciones",  "Sin gas");
	registro.put("Extras", "");
	registro.put("FechaHora", "2013-03-18" + "16:25:13");
	registro.put("Nombre", "Fanta de naranja");
	registro.put("Precio", 3);
	registro.put("Personas", "3");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
  	registro = new ContentValues();
	registro.put("NumMesa", "6a");
	registro.put("IdCamarero", "21");
	registro.put("IdPlato", "fh11");
	registro.put("Observaciones",  "Sin queso");
	registro.put("Extras", "Al punto,Barbacoa,Patata Asada,Ensalada Tomate y Lechuga");
	registro.put("FechaHora", "2013-03-19" + "23:45:23");
	registro.put("Nombre", "PLATO ESTRELLA:Director's Choice");
	registro.put("Precio",10.85);
	registro.put("Personas", "2");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
  	registro = new ContentValues();
	registro.put("NumMesa", "6a");
	registro.put("IdCamarero", "21");
	registro.put("IdPlato", "fh27");
	registro.put("Observaciones",  "");
	registro.put("Extras", "");
	registro.put("FechaHora", "2013-03-19" + "23:45:23");
	registro.put("Nombre", "Yaki Soft Tacos");
	registro.put("Precio", 9.95);
	registro.put("Personas", "2");
	registro.put("IdUnico", idUnico);
   	db.insert("Mesas", null, registro);
   	idUnico ++;
   	
	}
}