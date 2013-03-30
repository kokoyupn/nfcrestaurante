package baseDatos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Handler extends SQLiteOpenHelper {
	
	// Ruta por defecto de las bases de datos en el sistema Android
	private static String DB_PATH = "/data/data/com.example.nfcook/databases/";
	// Nombre de la base de datos con la que queramos trabajar
	private static String DB_NAME;
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	
	public Handler(Context context) {
		//el uno corresponde a la version de la base de datos;
		super(context, "MiBase.db", null, 1);
   	 	DB_NAME = "MiBase.db";
		this.myContext = context;
	}
		 
	public Handler(Context context,String DB_NAME) {
	    //el uno corresponde a la version de la base de datos;
	    super(context, DB_NAME, null, 1);
	    this.DB_NAME = DB_NAME;
	    this.myContext = context;
	}
     
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
	    try{
		    String myPath = DB_PATH + DB_NAME;
		    checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    }catch(SQLiteException e){
	    	//si llegamos aqui es porque la base de datos no existe todavía.
	    }
	    
	    if(checkDB != null){
	    	checkDB.close();
	    }	
	    
	    return checkDB != null ? true : false;	 
    }
    
	/* Copia base de datos desde la carpeta assets a la recién creada base de datos en la 
	 * carpeta de sistema, desde dónde podremos acceder a ella.
	 */
	private void copyDataBase() throws IOException{
		try{
			//Abrimos el fichero de base de datos como entrada a través del contexto accedemos a la carpeta assets
			InputStream myInput = myContext.getAssets().open(DB_NAME);
			
			//Ruta a la base de datos vacía recién creada
			String outFileName = DB_PATH + DB_NAME;
			
			//Abrimos la base de datos vacía como salida
			OutputStream myOutput = new FileOutputStream(outFileName);
			
			//Transferimos los bytes desde el fichero de entrada al de salida
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
		//Liberamos los streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		}catch(Exception e){
		}
	}
	 
	   
	 
	/*Crea una base de datos vacía en el sistema y la reescribe con nuestro fichero
	 * de base de datos.
	 */
	public void createDataBase() throws IOException{
		boolean dbExist = checkDataBase();
		if(dbExist){
			//la base de datos existe y no hacemos nada.
		}else{
			/*Llamando a este método se crea la base de datos vacía en la ruta por 
			 * defecto del sistema de nuestra aplicación por lo que podremos 
			 * sobreescribirla con nuestra base de datos.
			 */
			this.getReadableDatabase();
			try {
				copyDataBase();
			}catch (IOException e) {
				throw new Error("Error copiando Base de Datos");
			}
		}
	}
	  
	public SQLiteDatabase open() throws SQLException{
		if(myDataBase==null || (!(myDataBase.isOpen())) ){
			try {
				createDataBase(); //Si no existe crea la base de datos, si existe no hace nada
			}catch (IOException e) {
				throw new Error("Ha sido imposible crear la Base de Datos");
			}
			String myPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
		}
		return myDataBase;
	}	  
		  
	public void close(){
		myDataBase.close();
	}
	  
	@Override
	/*TODO
	 * Poner el if con el nombre de vuestra base de datos
	 */
	public void onCreate(SQLiteDatabase db) {//Se llama con el getReadableDatabase y 
											//getWritable si no existe la base aun, y se crea
		if(DB_NAME=="MiBase.db"){
		String query="CREATE TABLE Restaurantes(Id TEXT,Restaurante TEXT,Categoria TEXT," +
		"TipoPlato TEXT,Nombre TEXT,Descripcion TEXT,Breve TEXT,Foto TEXT,Extras TEXT,Precio INTEGER)";
			db.execSQL(query);
		}
		else if(DB_NAME=="Login.db"){
			String query="CREATE TABLE Camareros(Nombre TEXT,Contraseña TEXT)";
						db.execSQL(query);
		}else if(DB_NAME=="Mesas.db"){
			String query="CREATE TABLE Mesas(NumMesa TEXT,IdCamarero TEXT,IdPlato TEXT," +
			"Observaciones TEXT,Extras TEXT, FechaHora TEXT, Nombre TEXT, Precio INTEGER, Personas INTEGER, IdUnico INTEGER)";
			db.execSQL(query);
		}else if(DB_NAME=="Cuenta.db"){
			String query="CREATE TABLE Cuenta(Restaurante TEXT,Id TEXT, IdHijo TEXT, Plato TEXT," +
			"Observaciones TEXT,Extras TEXT, PrecioPlato INTEGER)";
			db.execSQL(query);
		}
	}
	
	  
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
		// TODO Auto-generated method stub
	}
}
