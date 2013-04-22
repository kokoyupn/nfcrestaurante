package generar_bd;

import com.example.nfcook.R;
import com.example.nfcook.R.layout;

import baseDatos.Handler;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;
public class BaseDatosRestaurantes extends Activity {	   
	public Handler sql;
	public SQLiteDatabase db;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.base_datos_generada);
        
        // Importamos la base de datos donde vamos a hacer la carga de los platos de los restaurantes
        try{
        	sql=new Handler(getApplicationContext(),"MiBase.db"); 
        	db=sql.open();
        }catch(SQLiteException e){
        	Toast.makeText(getApplicationContext(),"NO EXISTE",Toast.LENGTH_SHORT).show();
     		
        } 
       
/***************************************************VIPS***************************************************/
        int idVips = 0;
        
        /****************************************DESAYUNOS************************************************/
    	ContentValues desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Americano");
    	desayunosVips.put("Breve", "El auténtico desayuno americano con nuestras famosas tortitas.");
    	desayunosVips.put("Foto", "v58");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
    					  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
       	desayunosVips.put("Precio", 3.95);
    	desayunosVips.put("Descripcion", "Dos de nuestras famosas tortitas, acompañadas de huevos revueltos o fritos, crujiente bacon y patatas recién fritas o `Home Fries´ (patatas caseras cocidas y fritas especiadas con óregano, ajo y pimentón).");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Churros con Chocolate o Café");
    	desayunosVips.put("Breve", "Churros recién hechos. Tómatelos con o sin azúcar espolvereada.");
    	desayunosVips.put("Foto", "v59");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");    	
    	desayunosVips.put("Precio", 2.95);
    	desayunosVips.put("Descripcion", "Crujientes churros recién hechos. Tómatelos como más te gusten, con o sin azúcar espolvoreada.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Croissant French Toast");
    	desayunosVips.put("Breve", "Ni la mejor de las fotos puede hacer justicia a su delicioso sabor. Advertencia: engancha");
    	desayunosVips.put("Foto", "v60");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");    	
    	desayunosVips.put("Precio", 3.95);
    	desayunosVips.put("Descripcion", "El auténtico `Croissant French Toast´ bañado en leche y huevo, acompañado de rojadas de fresa y plátano naturales con un toque de canela y caramelo.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "English Breakfast");
    	desayunosVips.put("Breve", "Nuestro English Breakfast ahora es más auténticamente English que nunca.");
    	desayunosVips.put("Foto", "v61");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
    	desayunosVips.put("Precio", 5.95);
    	desayunosVips.put("Descripcion", "El clásico desayuno inglés: Huevos revueltos o dos huevos fritos, bacon, patatas fritas o `Home Fries´, champiñones, tomate a la plancha, salchicha Lincolnshire y barrita de pan integral recién tostada. Acompañado de un zumo de naranja natural.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Ibérico");
    	desayunosVips.put("Breve", "Para los amantes de la textura, aroma y sabor singular de nuestro Jamón Ibérico.");
    	desayunosVips.put("Foto", "v62");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
        desayunosVips.put("Precio", 3.75);
    	desayunosVips.put("Descripcion", "Jamón Ibérico con nuestra receta de tomate natural en un mollete de Antequera recién tostado y aceite de oliva virgen extra Carbonell.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Ligero");
    	desayunosVips.put("Breve", "Si te cuidas y no quieres renunciar al sabor prueba nuestro desayuno más ligero. ");
    	desayunosVips.put("Foto", "v63");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
        desayunosVips.put("Precio", 2.95);
    	desayunosVips.put("Descripcion", "Pan de pan integral tostado con queso de Burgos, finas láminas de pavo y rodajas de tomate.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id","V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Tortitas con Frutas");
    	desayunosVips.put("Breve", "Si te gustan las tortitas y te consideras un amante de la fruta, no puedes dejar de darte este capricho.");
    	desayunosVips.put("Foto", "v64");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
        desayunosVips.put("Precio", 4.95);
    	desayunosVips.put("Descripcion", "Nuestras tortitas con fresas y kiwis en rodajas, cremosa leche condensada y sirope de fresa. ¡Tocarás el cielo!.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
    	desayunosVips = new ContentValues();
    	desayunosVips.put("Id", "V"+idVips);
    	desayunosVips.put("Restaurante", "VIPS");
    	desayunosVips.put("Categoria", "Desayunos");
    	desayunosVips.put("TipoPlato", "Desayuno");
    	desayunosVips.put("Nombre", "Yogur con frambuesa y mango");
    	desayunosVips.put("Breve", "Para activarte por las mañanas prueba nuestro Yogur Activia con Frambuesas y Mango.");
    	desayunosVips.put("Foto", "v65");
    	desayunosVips.put("Extras", "Complementos:Huevo o extra de salchicha Lincolnshire o `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Bacon ahumado,Zumo de naranja natural (25cl)");
        desayunosVips.put("Precio", 2.95);
    	desayunosVips.put("Descripcion", "Es un postre sano, ligero y refrescante. Una mezcla de Yogur Activia con mango, frambuesas y sirope de fresa.   Te encantará.");
    	db.insert("Restaurantes", null, desayunosVips);
    	idVips++;
    	
       	/*************************************************************************************************/

    	/*****************************************ENTRANTES***********************************************/
        ContentValues entrantesVips = new ContentValues();
        entrantesVips.put("Id", "V"+idVips);
        entrantesVips.put("Restaurante", "VIPS");
        entrantesVips.put("Categoria", "Entrante");
        entrantesVips.put("TipoPlato", "Picar");
        entrantesVips.put("Nombre", "Alitas Buffalo Style");
        entrantesVips.put("Breve", "Crujientes alitas de pollo marinadas");
        entrantesVips.put("Foto", "v1");
        entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 7.95);
        entrantesVips.put("Descripcion", "Crujientes alitas de pollo marinadas con la genuina salsa Buffalo, acompañadas de unas crudites de zanahoria y apio con una cremosa salsa de queso Gorgonzola.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Aros de Cebolla");
     	entrantesVips.put("Breve", "Crujientes aros de cebolla con un toque especial");
     	entrantesVips.put("Foto", "v2");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 6.50);
     	entrantesVips.put("Descripcion", "Crujientes y dorados aros de cebolla con un toque de Parmesano, oregano y cayena, acompañados de dos salsas, Blooming Onion y nuestro secreto mejor guardado: la salsa especial VIPS.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Croquetas");
     	entrantesVips.put("Breve", "Croquetas de Jamón Ibérico y Mozzarella empanadas con Panko.");
     	entrantesVips.put("Foto", "v3");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 7.25);
     	entrantesVips.put("Descripcion", "Crujientes croquetas de jamón Ibérico y Mozzarella empanadas con Panko, adornadas con tomate seco, cebolla crujiente y alcaparras fritas. Acompañadas de dos salsas: Barbacoa Ranch y salsa de tomate Concassé.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Flatbread de Pollo Barbacoa");
     	entrantesVips.put("Breve", "Puede parecer una pizza, ¡pero no lo es!. ");
     	entrantesVips.put("Foto", "v4");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 8.95);
     	entrantesVips.put("Descripcion", "Fina masa de pan crujiente recién horneado con pechuga de pollo a la Barbacoa, pimientos asados rojos y amarillos, ajetes y crema agria con jugo de lima.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "King Crab");
     	entrantesVips.put("Breve", "Para dipear con suaves tortillas de maíz. ");
     	entrantesVips.put("Foto", "v5");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 8.95);
     	entrantesVips.put("Descripcion", "Mezcla templada de carne de cangrejo del Atlántico Norte, quesos suaves y Mozzarella, pimiento rojo, pimienta y jugo de lima. Adornada con pico de gallo, ajetes, cilantro y acompañada de unas crujientes tortillas de harina de trigo.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Nachos Tex - Mex");
     	entrantesVips.put("Breve", "Crujientes tortillas de maíz para sumergir en una nueva mezcla de quesos y mucho más...");
     	entrantesVips.put("Foto", "v6");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 8.50);
     	entrantesVips.put("Descripcion", "Crujientes tortillas de maiz con una nueva mezcla de quesos con cebolla roja, chorizo picado, chiles jalapeños y nuestro toque especial de cilantro, ajetes y crema agria con jugo de lima.Si no te gustan picantes, puedes pedirlos sin chiles jalapeños.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Patatas VIPS");
     	entrantesVips.put("Breve", "Patatas sazonadas con salsa ali-oli y nuestra salsa secreta especial VIPS.");
     	entrantesVips.put("Foto", "v7");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 5.50);
     	entrantesVips.put("Descripcion", "Patatas sazonadas, fritas y doradas, servidas con salsa ali-oli y nuestra salsa secreta especial VIPS.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Quesadilla Chicken & Veg");
     	entrantesVips.put("Breve", "Dos tortillas de trigo rellenas de queso Cheddar y Mozzarella, pollo y verduras asadas.");
     	entrantesVips.put("Foto", "v8");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 8.95);
     	entrantesVips.put("Descripcion", "Dos tortillas de harina de trigo rellenas de queso Cheddar y Mozzarella, pollo asado, verduras asadas (calabacín, champiñón Portobello, pimientos y cebolla).Acompañadas de una crema de lima.");
     	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
     	
    	entrantesVips = new ContentValues();
     	entrantesVips.put("Id", "V"+idVips);
     	entrantesVips.put("Restaurante", "VIPS");
     	entrantesVips.put("Categoria", "Entrante");
     	entrantesVips.put("TipoPlato", "Picar");
     	entrantesVips.put("Nombre", "Quesadilla de Jamón y dos Quesos");
     	entrantesVips.put("Breve", "Dos tortillas de harina de trigo rellenas de jamón York y quesos Cheddar.");
     	entrantesVips.put("Foto", "v9");
     	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 6.95);
     	entrantesVips.put("Descripcion", "Dos tortillas de harina de trigo rellenas de jamón York, dos tipos de queso Cheddar fundidos, y adornadas con pico de gallo y salsa de tomate y guacamole.");
      	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
      	
    	entrantesVips = new ContentValues();
      	entrantesVips.put("Id", "V"+idVips);
      	entrantesVips.put("Restaurante", "VIPS");
      	entrantesVips.put("Categoria", "Entrante");
      	entrantesVips.put("TipoPlato", "Picar");
      	entrantesVips.put("Nombre", "Verduras Asadas con Hierbas Aromáticas");
      	entrantesVips.put("Breve", "Verduras asadas aromatizadas con aceite de hierbas y albahaca.");
      	entrantesVips.put("Foto", "v10");
      	entrantesVips.put("Extras", "");
        entrantesVips.put("Precio", 7.95);
      	entrantesVips.put("Descripcion", "Verduras asadas aromatizadas con un aceite de hierbas mezcladas con hojas frescas de albahaca.");
       	db.insert("Restaurantes", null, entrantesVips);
    	idVips++;
    	
       	/*************************************************************************************************/
       	
        /***************************************** ENSALADAS *********************************************/
    	ContentValues ensaladasVips = new ContentValues();
     	ensaladasVips.put("Id", "V"+idVips);
     	ensaladasVips.put("Restaurante", "VIPS");
     	ensaladasVips.put("Categoria", "Principal");
     	ensaladasVips.put("TipoPlato", "Ensalada");
     	ensaladasVips.put("Nombre", "Ensalada César");
     	ensaladasVips.put("Breve", "Ligera y sabrosa ensalada con pollo crujiente aderezada con nuestra clásica salsa César.");
     	ensaladasVips.put("Foto", "v39");
     	ensaladasVips.put("Extras", "Extras:Cebolla frita,Bacon ahumado,Queso,Bol Mayonesa,Pechuga de pollo crujiente");
        ensaladasVips.put("Precio", 9.50);
     	ensaladasVips.put("Descripcion", "Ligera y sabrosa ensalada a base de lechuga, queso Parmesano, croutons de pan y pollo crujiente. Aderezada con nuestra salsa César.");
     	db.insert("Restaurantes", null, ensaladasVips);
     	idVips++;
     	
     	ensaladasVips = new ContentValues();
     	ensaladasVips.put("Id", "V"+idVips);
     	ensaladasVips.put("Restaurante", "VIPS");
     	ensaladasVips.put("Categoria", "Principal");
     	ensaladasVips.put("TipoPlato", "Ensalada");
     	ensaladasVips.put("Nombre", "Ensalada Louisiana");
     	ensaladasVips.put("Breve", "Sabrosos dados de pollo con salsa de Bourbon y melaza.");
     	ensaladasVips.put("Foto", "v40");
     	ensaladasVips.put("Extras", "Extras:Cebolla frita,Bacon ahumado,Queso,Bol Mayonesa,Pechuga de pollo crujiente");
        ensaladasVips.put("Precio", 9.95);
     	ensaladasVips.put("Descripcion", "Sabrosos dados de pollo con salsa de Bourbon y melaza, con mezcla de lechugas frescas, pimiento rojo, bacon crujiente y cebolla frita.");
     	db.insert("Restaurantes", null, ensaladasVips);
     	idVips++;
     	
     	ensaladasVips = new ContentValues();
     	ensaladasVips.put("Id", "V"+idVips);
     	ensaladasVips.put("Restaurante", "VIPS");
     	ensaladasVips.put("Categoria", "Principal");
     	ensaladasVips.put("TipoPlato", "Ensalada");
     	ensaladasVips.put("Nombre", "Ensalada Siciliana");
     	ensaladasVips.put("Breve", "Ligera ensalada de tomates, con lechuga romana, rúcula, perlas de Mozzarella y aceitunas negras.");
     	ensaladasVips.put("Foto", "v41");
     	ensaladasVips.put("Extras", "Extras:Cebolla frita,Bacon ahumado,Queso,Bol Mayonesa,Pechuga de pollo crujiente");
        ensaladasVips.put("Precio", 8.95);
     	ensaladasVips.put("Descripcion", "Ensalada de tomates: natural, Cherry y seco, con una mezcla de lechuga romana y rúcula, perlas de Mozzarella y aceitunas negras, aderezada con una vinagreta de especias.");
     	db.insert("Restaurantes", null, ensaladasVips);
     	idVips++;
     	
     	ensaladasVips = new ContentValues();
     	ensaladasVips.put("Id", "V"+idVips);
     	ensaladasVips.put("Restaurante", "VIPS");
     	ensaladasVips.put("Categoria", "Principal");
     	ensaladasVips.put("TipoPlato", "Ensalada");
     	ensaladasVips.put("Nombre", "Ensalada Toscana");
     	ensaladasVips.put("Breve", "Nuestra ensalada templada con pollo a la plancha, bacon crujiente y mucho más.");
     	ensaladasVips.put("Foto", "v42");
     	ensaladasVips.put("Extras", "Extras:Cebolla frita,Bacon ahumado,Queso,Bol Mayonesa,Pechuga de pollo crujiente");
        ensaladasVips.put("Precio", 9.75);
     	ensaladasVips.put("Descripcion", "Ensalada templada con pollo a la plancha, bacon crujiente y queso Gorgonzola sobre una variada mezcla de lechugas.");
     	db.insert("Restaurantes", null, ensaladasVips);
     	idVips++;
     	
     	ensaladasVips = new ContentValues();
     	ensaladasVips.put("Id", "V"+idVips);
     	ensaladasVips.put("Restaurante", "VIPS");
     	ensaladasVips.put("Categoria", "Principal");
     	ensaladasVips.put("TipoPlato", "Ensalada");
     	ensaladasVips.put("Nombre", "Ensalada Varese");
     	ensaladasVips.put("Breve", "Mezcla de lechugas con quesos Gorgonzola, de Cabra y Parmesano.");
     	ensaladasVips.put("Foto", "v43");
     	ensaladasVips.put("Extras", "Extras:Cebolla frita,Bacon ahumado,Queso,Bol Mayonesa,Pechuga de pollo crujiente");
        ensaladasVips.put("Precio", 9.25);
     	ensaladasVips.put("Descripcion", "Ensalada templada con pollo a la plancha, bacon crujiente y queso Gorgonzola sobre una variada mezcla de lechugas.");
     	db.insert("Restaurantes", null, ensaladasVips);
     	idVips++;
       	
     	/*************************************************************************************************/
     	
     	/****************************************PRINCIPALES**********************************************/
       	ContentValues principalesVips = new ContentValues();
       	principalesVips.put("Id", "V"+idVips);
       	principalesVips.put("Restaurante", "VIPS");
       	principalesVips.put("Categoria", "Principal");
       	principalesVips.put("TipoPlato", "Plato Principal");
       	principalesVips.put("Nombre", "Costillas BBQ");
       	principalesVips.put("Breve", "Tiernas costillas de cerdo glaseadas con salsa barbacoa, patatas fritas y aros de cebolla.");
       	principalesVips.put("Foto", "v11");
       	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");
        principalesVips.put("Precio", 9.95);
       	principalesVips.put("Descripcion", "Tiernísimas costillas de cerdo glaseadas con nuestra salsa barbacoa, acompañadas de unas crujientes patatas fritas, aros de cebolla y nuestra salsa Barbacoa Chipotle. ¡Echa la salsa por encima de las costillas y disfrútalas!");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
 
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Flatbread de Pollo Barbacoa");
     	principalesVips.put("Breve", "Tiernas costillas de cerdo glaseadas con salsa barbacoa, patatas fritas y aros de cebolla.");
     	principalesVips.put("Foto", "v12");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 8.95);
     	principalesVips.put("Descripcion", "Fina masa de pan crujiente recién horneado con pechuga de pollo a la Barbacoa, pimientos asados rojos y amarillos, ajetes y crema agria con jugo de lima.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Langostinos Sriracha");
     	principalesVips.put("Breve", "Deliciosos langostinos con salsa Sriracha inspirado en los sabores de oriente.");
     	principalesVips.put("Foto", "v13");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 9.95);
     	principalesVips.put("Descripcion", "Deliciosos langostinos con salsa Sriracha sobre un lecho de arroz vaporizado con puré de cilantro. Acompañados con cebolla caramelizada, tirabeques al vapor, pimientos rojos asados y espárragos.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Lasagna de Verduras Asadas");
     	principalesVips.put("Breve", "Lasagna de verduras y crema de queso de Cabra con albahaca. Gratinada con bechamel y Parmesano.");
     	principalesVips.put("Foto", "v14");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 9.95);
     	principalesVips.put("Descripcion", "Láminas de pasta de trigo rellenas de verduras asadas y crema de queso de Cabra y albahaca, gratinadas con una suave bechamel y queso Parmesano. Acompáñalas con una ensalada de lechugas y dados de tomate o con dos rebanadas de pan de ajo.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Lomo Alto de Novillo Argentino");
     	principalesVips.put("Breve", "Lomo alto de novillo argentino con salsa Chimichurri y patatas VIPS.");
     	principalesVips.put("Foto", "v15");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 15.50);
     	principalesVips.put("Descripcion", "Jugoso y tierno lomo alto de novillo argentino acompañado con salsa Chimichurri y guarnición de patatas VIPS, espárragos, calabacín y tomate a la parrilla.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Lomo de Merluza");
     	principalesVips.put("Breve", "Lomo de merluza rebozado con mayonesa de lima.");
     	principalesVips.put("Foto", "v16");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 11.50);
     	principalesVips.put("Descripcion", "Lomo de Merluza rebozado con mayonesa de lima. Acompañado de una ensalada de lechugas frescas con pico de gallo aliñadas con una ligera salsa Ranch y crema de aceto.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Pasta Little Italy");
     	principalesVips.put("Breve", "Nuestras exclusivas `Meat Balls´ con pasta, aderezada salsa de tomate y Pesto, Parmesano y Mozzarella.");
     	principalesVips.put("Foto", "v17");
     	principalesVips.put("Extras","Complementos:Rodas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 11.95);
     	principalesVips.put("Descripcion", "Nuestras exclusivas Meat Balls con pasta y salsa de tomate. Acompañadas de queso Parmesano, Mozzarella, salsa Pesto y dos rebanadas de pan de ajo.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Pechuga de Pollo Villaroy");
     	principalesVips.put("Breve", "Dos pechugas de pollo cubiertas de bechamel, empanadas con huevo y patatas fritas.");
     	principalesVips.put("Foto", "v18");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 11.50);
     	principalesVips.put("Descripcion", "Dos pechugas de pollo cubiertas con salsa bechamel, empanadas y acompañadas de un huevo frito, mezcla de lechugas con tomate, patatas fritas y salsa de tomate Concassé.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Salteado de Pollo Oriental");
     	principalesVips.put("Breve", "Pechuga de pollo con salsa agridulce de soja y piña, pimientos rojos, brócoli, anacardos y arroz.");
     	principalesVips.put("Foto", "v19");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 10.95);
     	principalesVips.put("Descripcion", "Sabrosas tiras de pechuga de pollo con salsa agridulce de soja y piña, salteadas con pimientos, brócoli y anacardos. Servidas sobre una base de arroz blanco.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Verduras Asadas con Hierbas Aromáticas");
     	principalesVips.put("Breve", "Verduras asadas aromatizadas con aceite de hierbas y albahaca.");
     	principalesVips.put("Foto", "v20");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 7.95);
     	principalesVips.put("Descripcion", "Verduras asadas aromatizadas con un aceite de hierbas mezcladas con hojas frescas de albahaca.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	principalesVips = new ContentValues();
     	principalesVips.put("Id", "V"+idVips);
     	principalesVips.put("Restaurante", "VIPS");
     	principalesVips.put("Categoria", "Principal");
     	principalesVips.put("TipoPlato", "Plato Principal");
     	principalesVips.put("Nombre", "Pallarda de Ternera");
     	principalesVips.put("Breve", "Filete de ternera con verduras salteadas y rodajas de tomate a la plancha.");
     	principalesVips.put("Foto", "v21");
     	principalesVips.put("Extras","Complementos:Rodajas de tomate natural a la parrila,Cambia tus patatas fritas por `Home Fries´ (patatas caseras cocidas cada día y posteriormente fritas" +
				  " con nuestro toque de orégano y pimentón),Pan rústico o barrita integral,Huevo frito,Arroz,Verduras salteadas,Espárragos verdes a la parrilla");        principalesVips.put("Precio", 11.95);
     	principalesVips.put("Descripcion", "Tierno filete de ternera acompañado de verduras salteadas y rodajas de tomate a la plancha.");
     	db.insert("Restaurantes", null, principalesVips);
     	idVips++;
     	
     	/*************************************************************************************************/

     	/****************************************HAMBURGUESAS*********************************************/
     	ContentValues hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Bacon Eggburger");
     	hamburguesasVips.put("Breve", "La gran clásica acompaña de bacon, huevo y patatas fritas.");
     	hamburguesasVips.put("Foto", "v22");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 9.75);
     	hamburguesasVips.put("Descripcion", "La gran clásica acompaña de bacon, huevo y patatas fritas.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "BBQ Chicken Bistro");
     	hamburguesasVips.put("Breve", "¡Descubre todo el sabor de nuestra hamburguesa gourmet de pollo!");
     	hamburguesasVips.put("Foto", "v23");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 9.95);
     	hamburguesasVips.put("Descripcion", "Tierna pechuga de pollo a la plancha en nuestro pan brioche ligeramente tostado, " +
     			"servida con una deliciosa salsa BBQ Chipotle, crujiente cebolla frita, bacon, queso Cheddar fundido, " +
     			"cebolla roja, pepinillo suave, tomate natural,lechuga Batavia y mayonesa."+
     			"¡Acompáñala con una fresca mezcla de lechugas y dados de tomate o patatas fritas!");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Eggburger");
     	hamburguesasVips.put("Breve", "La gran clásica acompañada de huevo y patatas fritas.");
     	hamburguesasVips.put("Foto", "v24");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 7.50);
     	hamburguesasVips.put("Descripcion", "La gran clásica con queso fundido, acompañada de nuestras patatas fritas y un suculento huevo frito.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Francesa");
     	hamburguesasVips.put("Breve", "Carne a la plancha con queso Brie fundido, cebolla y mucho más.");
     	hamburguesasVips.put("Foto", "v25");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 10.75);
     	hamburguesasVips.put("Descripcion", "Jugosa carne 100% vacuno a la plancha con queso Brie fundido, cebolla crujiente, bacon y champiñón. Servida en pan brioche con una ligera mayonesa Dijón y patatas fritas.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Italian Deli");
     	hamburguesasVips.put("Breve", "¡Una delicatessen para los amantes del queso!");
     	hamburguesasVips.put("Foto", "v26");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 10.95);
     	hamburguesasVips.put("Descripcion", "Sabrosa hamburguesa, servida en un pan brioche ligeramente tostado, " +
     			"con exquisito queso Gorgonzola, bacon, cebolla roja, pepinillo suave, tomate natural, lechuga y " +
     			"salsa de queso Azul.¡Una delicatessen para los amantes del queso!");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Manhattan");
     	hamburguesasVips.put("Breve", "La hamburguesa que arrasa en la Gran Manzana. ¡Descúbrela!");
     	hamburguesasVips.put("Foto", "v27");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 9.75);
     	hamburguesasVips.put("Descripcion", "Deliciosa hamburguesa 100% vacuno con tomate fresco, queso fundido, finas rodajas de cebolla roja, lechuga Batavia y nuestra mayonesa de lima. Servida con nuestro pan brioche ligeramente tostado y salsa kétchup Heinz.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Pampera");
     	hamburguesasVips.put("Breve", "¡Disfruta de nuestra hamburguesa estrella!. Pan de mollete con cebolla, bacon y mucho más.");
     	hamburguesasVips.put("Foto", "v28");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 10.75);
     	hamburguesasVips.put("Descripcion", "Doble hamburguesa en pan de mollete crujiente con bacon, rúcula y queso Provolone fundido, acompañadas de cebolla confitada y mayonesa de Chimichurri.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Súper Bacon Cheeseburger");
     	hamburguesasVips.put("Breve", "Con queso fundido, bacon crujiente, acompañada de patatas fritas.");
     	hamburguesasVips.put("Foto", "v29");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
     	hamburguesasVips.put("Precio", 9.75);
     	hamburguesasVips.put("Descripcion", "Con queso fundido y dos lonchas de bacon crujiente, acompañada de patatas fritas.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "Súper Cheeseburger");
     	hamburguesasVips.put("Breve", "La gran clásica con queso fundido, acompañada de patatas fritas.");
     	hamburguesasVips.put("Foto", "v30");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 8.75);
     	hamburguesasVips.put("Descripcion", "La gran clásica con queso fundido, acompañada de nuestras patatas fritas.");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	hamburguesasVips = new ContentValues();
     	hamburguesasVips.put("Id", "V"+idVips);
     	hamburguesasVips.put("Restaurante", "VIPS");
     	hamburguesasVips.put("Categoria", "Principal");
     	hamburguesasVips.put("TipoPlato", "Hamburguesa");
     	hamburguesasVips.put("Nombre", "The Big Tower");
     	hamburguesasVips.put("Breve", "Aro de cebolla, huevo frito y mucho más.");
     	hamburguesasVips.put("Foto", "v31");
     	hamburguesasVips.put("Extras","Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas fritas,Patatas gajos,Ensalada verde/Complementos:Bacon ahumado,Huevo frito,Queso,Pan Brioche,Extra de patatas fritas o patatas gajo");
        hamburguesasVips.put("Precio", 10.95);
     	hamburguesasVips.put("Descripcion", "Jugosa hamburguesa en pan brioche ligeramente tostado, con un crujiente aro de cebolla, " +
     			"huevo frito,  cremoso queso Emmental, bacon, pepinillo suave, tomate natural y salsa especial VIPS." +
     			"¡Perfecta para disfrutar con patatas fritas y todo el sabor de nuestra salsa especial VIPS!");
     	db.insert("Restaurantes", null, hamburguesasVips);
     	idVips++;
     	
     	/*************************************************************************************************/
     	
     	/*********************************************SANDWICHES******************************************/
     	ContentValues sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "Focaccia Milano");
     	sadwichesVips.put("Breve", "¡Crujiente pehuga de pollo empanada en un pan Focaccia recién horneado!");
     	sadwichesVips.put("Foto", "v32");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
     					  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");
        sadwichesVips.put("Precio", 7.95);
     	sadwichesVips.put("Descripcion", "Crujiente pechuga de pollo empanada y sazonada con Parmesano, óregano y cayena, con " +
     			"Mozzarella fundida, una deliciosa salsa de tomate Concassé, cebolla roja a la plancha, mayonesa de " +
     			"Pesto y frescas hojas de albahaca y perejil en un pan de Focaccia recién horneado. Acompáñalo con " +
     			"tu guarnición favorita: ensalada o patatas fritas. ¡Nuevo Sandwich para hacerte la boca agua!");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "Fundy O`Clock");
     	sadwichesVips.put("Breve", "Jamón York y pavo cocido, acompañado de huevos revueltos y quesos Cheddar.");
     	sadwichesVips.put("Foto", "v33");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 7.95);
     	sadwichesVips.put("Descripcion", "Lonchas de jamón York y pavo cocido, acompañadas de huevos revueltos y una deliciosa mezca de quesos Cheddar fundidos.");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "Philly Steak");
     	sadwichesVips.put("Breve", "Si te gusta la ternera, ¡no podrás resistirse al nuevo Philly Steak!");
     	sadwichesVips.put("Foto", "v34");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 8.95);
     	sadwichesVips.put("Descripcion", "Jugosas láminas de ternera con un suave toque picante, queso Provolone fundido, " +
     			"cebolla roja a la plancha, tomate y fresca lechuga en un delicado pan campesino con mayonesa de lima. " +
     			"Acompáñalo con tu guarnición favorita; ensalada o patatas fritas. ¡Nuevo Sandwich para hacerte la boca agua!");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "Salmón Club");
     	sadwichesVips.put("Breve", "Nuestro nuevo sándwich de salmón noruego ahumado. ¡Exquisito!");
     	sadwichesVips.put("Foto", "v35");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 8.95);
     	sadwichesVips.put("Descripcion", "Crujiente pan de sándwich recién tostado, untado con una suave crema de queso con cebollino" +
     			"y alcaparras, láminas de salmón noruego ahumado, con cebolla roja, tomate y fresca lechuga Batavia. " +
     			"Servido con crudités de apio y zanahoria para mojar en una salsa de queso Gorgonzola suave o si lo" +
     			" prefieres pídelo con ensalada o patatas fritas.¡Nuevo Sandwich para hacerte la boca agua!");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "VIPS Club");
     	sadwichesVips.put("Breve", "Nuestro gran clásico renovado, ¡más irresistible que nunca!.");
     	sadwichesVips.put("Foto", "v36");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 8.75);
     	sadwichesVips.put("Descripcion", "Nuestro gran clásico renovado, más irresistible que nunca con sus tres pisos de pollo a la plancha, bacon crujiente, tomates frescos y mayonesa. Ahora con el doble de queso y jamón York, acompañados por un cremoso queso Emmental Suizo, lechuga Batavia y más patatas fritas.");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "VIPS Club Nature");
     	sadwichesVips.put("Breve", "Déjate sorprender con nuestra nueva versión del VIPS Club Nature, ahora con más pavo.");
     	sadwichesVips.put("Foto", "v37");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 9.25);
     	sadwichesVips.put("Descripcion", "Nueva versión del VIPS Club Nature ahora con más pavo a la plancha. Elaborado con pan " +
     			"integral, mayonesa, queso y bacon tostado sin grasa. Servido con `crudités´ crujientes de zanahoria y " +
     			"apio y una cremosa salsa de queso Gorgonzola.");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	sadwichesVips = new ContentValues();
     	sadwichesVips.put("Id", "V"+idVips);
     	sadwichesVips.put("Restaurante", "VIPS");
     	sadwichesVips.put("Categoria", "Principal");
     	sadwichesVips.put("TipoPlato", "Sandwich");
     	sadwichesVips.put("Nombre", "VIPS Roll Brooklyn");
     	sadwichesVips.put("Breve", "Nuestro original sándwich gigante enrollado.");
     	sadwichesVips.put("Foto", "v38");
     	sadwichesVips.put("Extras", "Complementos:Queso o bacon ahumado o Jamón York,Cambia tus patatas fritas por `Home Fries´ " +
				  "(patatas caseras cocidas y fritas escediadas con orégano y pimentón),Patatas fritas");        sadwichesVips.put("Precio", 9.25);
     	sadwichesVips.put("Descripcion", "Nuestro sándwich gigante enrollado con una nueva receta. Pollo a la parrilla, el triple de jamón York y cremoso queso fundido, bacon, suave pepinillo agridulce, cebolla caramelizada y mayonesa con mostaza.  Acompañado de patatas fritas y salsa de mostaza y miel.");
     	db.insert("Restaurantes", null, sadwichesVips);
     	idVips++;
     	
     	/*************************************************************************************************/
     	
     	/********************************************DULCES***********************************************/
     	ContentValues dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Brownie con Helado de Vainilla");
     	dulcesVips.put("Breve", "Bizcocho caliente de chocolate con nueces.");
     	dulcesVips.put("Foto", "v48");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 4.95);
     	dulcesVips.put("Descripcion", "Brownie con Helado de Vainilla");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Croissant French Toast");
     	dulcesVips.put("Breve", "Ni la mejor de las fotos puede hacer justicia a su delicioso sabor. Advertencia: engancha");
     	dulcesVips.put("Foto", "v49");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 3.95);
     	dulcesVips.put("Descripcion", "El auténtico `Croissant French Toast´ bañado en leche y huevo, acompañado de rojadas de fresa y plátano naturales con un toque de canela y caramelo. Servido con una bola de helado de vainilla.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "New York cheesecake");
     	dulcesVips.put("Breve", "Irresistible crema de queso, con una base de galleta y mermelada de fresas.");
     	dulcesVips.put("Foto", "v50");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 4.95);
     	dulcesVips.put("Descripcion", "Irresistible crema de queso, con una base de galleta y mermelada de fresas.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Tarta de chocolate con Toblerone");
     	dulcesVips.put("Breve", "Una combinación de una base de almendras con crema y mucho más... ¡Irresistible!");
     	dulcesVips.put("Foto", "v51");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 4.75);
     	dulcesVips.put("Descripcion", "Combina una irresistible base de almendras con una crema de chocolate y mousse de chocolate con leche, coronada por trozos de Toblerone y acompañada de una bola de helado de vainilla.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Tarta de queso con fresas");
     	dulcesVips.put("Breve", "Cremosa tarta de queso con sirope de fresa.");
     	dulcesVips.put("Foto", "v52");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 4.95);
     	dulcesVips.put("Descripcion", "Porción de tarta de queso y galleta, bañada en sirope de fresa y adornada con una fresa.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Tortitas con Frutas");
     	dulcesVips.put("Breve", "Si te gustan las tortitas y te consideras un amante de la fruta, no puedes dejar de darte este capricho.");
     	dulcesVips.put("Foto", "v53");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 4.95);
     	dulcesVips.put("Descripcion", "Nuestras tortitas con fresas y kiwis en rodajas, cremosa leche condensada y sirope de fresa. ¡Tocarás el cielo!.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Tortitas VIPS con Nata y Sirope");
     	dulcesVips.put("Breve", "Nuestras famosas tortitas a la plancha, acompañadas con nata y sirope.");
     	dulcesVips.put("Foto", "v54");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 2.95);
     	dulcesVips.put("Descripcion", "Nuestras famosas tortitas a la plancha, acompañadas con nata y sirope de chocolate, caramelo o fresa a tu gusto. ¿Podrás con las tres?.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Vasito Dulce de Leche");
     	dulcesVips.put("Breve", "Descubre nuestro nuevo vasito de Dulce de Leche. ¡Te encantará!");
     	dulcesVips.put("Foto", "v55");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 3.0);
     	dulcesVips.put("Descripcion", "Helado de dulce de leche, yoguar Activia, plátano y nata, servido con trocitos de cookies y sirope de dulce de leche.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Vasitos");
     	dulcesVips.put("Breve", "Date un capricho con nuestro nuevo vasito de Dulce de Leche o disfruta de los clásicos: Cheesecake, Tiramisú o Brownie.");
     	dulcesVips.put("Foto", "v56");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 3.0);
     	dulcesVips.put("Descripcion", "Date un capricho con nuestros vasitos de Cheesecake, Tiramisú, Brownie o el nuevo de Dulce de leche.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	dulcesVips = new ContentValues();
     	dulcesVips.put("Id", "V"+idVips);
     	dulcesVips.put("Restaurante", "VIPS");
     	dulcesVips.put("Categoria", "Dulces");
     	dulcesVips.put("TipoPlato", "Dulce");
     	dulcesVips.put("Nombre", "Yogur con Frambuesa y Mango");
     	dulcesVips.put("Breve", "Para activarte por las mañanas prueba nuestro Yogur Activia con Frambuesas y Mango.");
     	dulcesVips.put("Foto", "v57");
     	dulcesVips.put("Extras", "Complemento:Bola de helado artesano");
        dulcesVips.put("Precio", 3.95);
     	dulcesVips.put("Descripcion", "Es un postre sano, ligero y refrescante. Una mezcla de Yogur Activia con mango, frambuesas y sirope de fresa.    Te encantará.");
     	db.insert("Restaurantes", null, dulcesVips);
     	idVips++;
     	
     	/*************************************************************************************************/
     	     	
     	/***************************************BATIDOS Y HELADOS*****************************************/
     	ContentValues batidosVips = new ContentValues();
     	batidosVips.put("Id", "V"+idVips);
     	batidosVips.put("Restaurante", "VIPS");
     	batidosVips.put("Categoria", "Batidos y helados");
     	batidosVips.put("TipoPlato", "Batido");
     	batidosVips.put("Nombre", "Batido con Oreo");
     	batidosVips.put("Breve", "Delicioso batido de chocolate blanco y galletas Oreo.");
     	batidosVips.put("Foto", "v44");
     	batidosVips.put("Extras", "Complemento:Bola de helado artesano");
        batidosVips.put("Precio", 5.75);
     	batidosVips.put("Descripcion", "Delicioso batido de chocolate blanco y galletas Oreo, servido con nata.");
     	db.insert("Restaurantes", null, batidosVips);
     	idVips++;
     	
     	batidosVips = new ContentValues();
     	batidosVips.put("Id", "V"+idVips);
     	batidosVips.put("Restaurante", "VIPS");
     	batidosVips.put("Categoria", "Batidos y helados");
     	batidosVips.put("TipoPlato", "Batido");
     	batidosVips.put("Nombre", "Batidos VIPS con Nata y Sirope");
     	batidosVips.put("Breve", "¡Disfruta del nuevo sabor de dulce de leche!");
     	batidosVips.put("Foto", "v45");
     	batidosVips.put("Extras", "Complemento:Bola de helado artesano");
        batidosVips.put("Precio", 5.75);
     	batidosVips.put("Descripcion", "Todo un clásico de la casa renovado. Más dulce, cremoso y con mayor cantidad. 4 Bolas de helado a elegir entre: Chocolate, fresa, vainilla, chocolate blanco, yogur con arándanos y chocolate belga y nuestro nuevo sabor de dulce de leche. Leche, nata líquida azucarada y nata montada con un toque de sirope. ¡Disfrutarás hasta el último sorbo!.");
     	db.insert("Restaurantes", null, batidosVips);
     	idVips++;
     	
     	batidosVips = new ContentValues();
     	batidosVips.put("Id", "V"+idVips);
     	batidosVips.put("Restaurante", "VIPS");
     	batidosVips.put("Categoria", "Principal");
     	batidosVips.put("TipoPlato", "Helado");
     	batidosVips.put("Nombre", "Copa dulce de leche");
     	batidosVips.put("Breve", "Descubre la irresistible combinación de sabores de esta nueva Copa.");
     	batidosVips.put("Foto", "v46");
     	batidosVips.put("Extras", "Complemento:Bola de helado artesano");
        batidosVips.put("Precio", 5.95);
     	batidosVips.put("Descripcion", "Irresistible combinación de tres bolas de helado de dulce de leche, yogur Activia, plátano y nata, servida con trocitos de cookies y sirope de dulce de leche.");
     	db.insert("Restaurantes", null, batidosVips);
     	idVips++;
     	
     	batidosVips = new ContentValues();
     	batidosVips.put("Id", "V"+idVips);
     	batidosVips.put("Restaurante", "VIPS");
     	batidosVips.put("Categoria", "Principal");
     	batidosVips.put("TipoPlato", "Helado");
     	batidosVips.put("Nombre", "Copa homenaje al chocolate");
     	batidosVips.put("Breve", "Un homenaje para los amantes del chocolate.");
     	batidosVips.put("Foto", "v47");
     	batidosVips.put("Extras", "Complemento:Bola de helado artesano");
        batidosVips.put("Precio", 5.95);
     	batidosVips.put("Descripcion", "Delicioso brownie bañado en sirope de chocolate, con mousse, palitos de chocolate y dos bolas de helado de chocolate y chocolate belga.");
     	db.insert("Restaurantes", null, batidosVips);
     	idVips++;
     	
     	/*************************************************************************************************/
     		
     	/***************************************Cafes y chocolates****************************************/
     	ContentValues cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Café Bombón");
     	cafesChocolatesVips.put("Breve", "Leche condensada y café Espresso.");
     	cafesChocolatesVips.put("Foto", "v66");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 1.95);
     	cafesChocolatesVips.put("Descripcion", "Elaborado con deliciosa leche condensada y café Espresso.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Café Irlandés");
     	cafesChocolatesVips.put("Breve", "Café con un chorrito de whisky, azúcar moreno y nata batida.");
     	cafesChocolatesVips.put("Foto", "v67");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 4.75);
     	cafesChocolatesVips.put("Descripcion", "Mantenemos la receta original de 1942, el mejor café con un chorrito de whisky, azúcar moreno y nata batida.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Café Macchiato");
     	cafesChocolatesVips.put("Breve", "Espuma de leche y sirope de caramelo.");
     	cafesChocolatesVips.put("Foto", "v68");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 2.95);
     	cafesChocolatesVips.put("Descripcion", "Café con espuma de leche y una base de sirope de caramelo.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Café Mocca");
     	cafesChocolatesVips.put("Breve", "Sirope y mousse de chocolate con espuma de leche.");
     	cafesChocolatesVips.put("Foto", "v69");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 2.95);
     	cafesChocolatesVips.put("Descripcion", "Una perfecta combinación entre sirope y mousse de chocolate, adornada con espuma de leche. Dale tu toque espolvoreando cacao en polvo o canela.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Café Vienés");
     	cafesChocolatesVips.put("Breve", "Nata batida y sirope de chocolate.");
     	cafesChocolatesVips.put("Foto", "v70");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 2.95);
     	cafesChocolatesVips.put("Descripcion", "Servido con nata batida y sirope de chocolate.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Cafe");
     	cafesChocolatesVips.put("Nombre", "Cappuccino");
     	cafesChocolatesVips.put("Breve", "Espresso con leche y canela.");
     	cafesChocolatesVips.put("Foto", "v71");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 2.30);
     	cafesChocolatesVips.put("Descripcion", "Café Espresso con leche, adornado con una nube de espuma y canela en polvo.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Chocolate");
     	cafesChocolatesVips.put("Nombre", "Chocolate a la taza");
     	cafesChocolatesVips.put("Breve", "El mejor chocolate a la taza elaborado de forma tradicional.");
     	cafesChocolatesVips.put("Foto", "v72");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 1.95);
     	cafesChocolatesVips.put("Descripcion", "El mejor chocolate a la taza elaborado de forma tradicional.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	cafesChocolatesVips = new ContentValues();
     	cafesChocolatesVips.put("Id", "V"+idVips);
     	cafesChocolatesVips.put("Restaurante", "VIPS");
     	cafesChocolatesVips.put("Categoria", "Cafes y chocolates");
     	cafesChocolatesVips.put("TipoPlato", "Chocolate");
     	cafesChocolatesVips.put("Nombre", "Chocolate Praliné");
     	cafesChocolatesVips.put("Breve", "Chocolate a la taza, con crema de avellanas, nata montada y virutas de chocolate.");
     	cafesChocolatesVips.put("Foto", "v73");
     	cafesChocolatesVips.put("Extras", "Complemento:Bola de helado artesano");
        cafesChocolatesVips.put("Precio", 3.75);
     	cafesChocolatesVips.put("Descripcion", "Delicioso antojo de chocolate a la taza, con crema de avellanas, nata montada y virutas de chocolate negro.");
     	db.insert("Restaurantes", null, cafesChocolatesVips);
     	idVips++;
     	
     	/*************************************************************************************************/
     	
     	/****************************************BEBIDAS**************************************************/
     	ContentValues bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Agua Mineral");
     	bebidasVips.put("Descripcion", "Agua mineral");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "agua_fontbella");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Coca Cola");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "cocacola");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Coca Cola Zero");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "cocacola_zero");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Coca Cola Light");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "cocacola_light");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Fanta de Naranja");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "fanta_naranja");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Fanta de Limón");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "fanta_limon");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Sprite");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "sprite");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Nestea");
     	bebidasVips.put("Descripcion", "Bebida refrescante");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "nestea");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Cerveza Mahou");
     	bebidasVips.put("Descripcion", "Cerveza Mahou");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "cerveza_mahou");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
     	bebidasVips = new ContentValues();
     	bebidasVips.put("Id", "V"+idVips);
     	bebidasVips.put("Restaurante", "VIPS");
     	bebidasVips.put("Categoria", "Bebidas");
     	bebidasVips.put("TipoPlato", "");
     	bebidasVips.put("Nombre", "Cerveza San Miguel");
     	bebidasVips.put("Descripcion", "Cerveza San Miguel");
     	bebidasVips.put("Breve", "Bebida refrescante");
     	bebidasVips.put("Foto", "cerveza_san_miguel");
     	bebidasVips.put("Extras", "");
     	bebidasVips.put("Precio", 3.00);
     	db.insert("Restaurantes", null, bebidasVips);
     	idVips++;
     	
/**************************************************FIN VIPS*************************************************/
     	
     	
/***************************************************FOSTER**************************************************/
        int idFoster = 0; 
        
     	/****************************************ENTRANTES************************************************/
     	ContentValues n21 = new ContentValues();
     	n21.put("Id", "fh"+idFoster);
     	n21.put("Restaurante", "Foster");
     	n21.put("Categoria", "Entrante");
     	n21.put("TipoPlato", "");
     	n21.put("Nombre", "Bacon & Cheese Fries");
     	n21.put("Descripcion", "Patatas fritas acompañadas de salsa ranchera, bacon crujiente y una mezcla de quesos fundidos.");
     	n21.put("Breve", "Patatas fritas con salsa ranchera, bacon y quesos fundidos");
     	n21.put("Foto", "fh1");
     	n21.put("Extras", "");
     	n21.put("Precio", 8.35);
     	db.insert("Restaurantes", null, n21);
     	idFoster++;
     	
     	ContentValues n22 = new ContentValues();
     	n22.put("Id", "fh"+idFoster);
     	n22.put("Restaurante", "Foster");
     	n22.put("Categoria", "Entrante");
     	n22.put("TipoPlato", "");
     	n22.put("Nombre", "Hollywood Combo");
     	n22.put("Descripcion", "Combinación de alitas de pollo, fingers de queso, aros de cebolla y delicias de jalapeños, servidos con salsas ranchera y barbacoa. Para compartir");
     	n22.put("Breve", "Combinación de alitas de pollo, finguer de queso, aros de cebolla y jalapeños");
     	n22.put("Foto", "fh2");
     	n22.put("Extras", "");
     	n22.put("Precio", 11.55);
     	db.insert("Restaurantes", null, n22);
     	idFoster++;
     	
     	ContentValues n23 = new ContentValues();
     	n23.put("Id", "fh"+idFoster);
     	n23.put("Restaurante", "Foster");
     	n23.put("Categoria", "Entrante");
     	n23.put("TipoPlato", "");
     	n23.put("Nombre", "Nachos `San Fernando´");
     	n23.put("Descripcion", "Renovamos el clásico. Tiras de maíz cubiertas de quesos fundidos y chili con carne" +
     			"coronadas con crema agria, jalapeños y pico de gallo");
     	n23.put("Breve", "Tiras de maiz con queso fundido y chili con carne, crema agria y jalapeños");
     	n23.put("Foto", "fh3");
     	n23.put("Extras", "");
     	n23.put("Precio", 9.20);
     	db.insert("Restaurantes", null, n23);
     	idFoster++;
     	
     	ContentValues n24 = new ContentValues();
     	n24.put("Id", "fh"+idFoster);
     	n24.put("Restaurante", "Foster");
     	n24.put("Categoria", "Entrante");
     	n24.put("TipoPlato", "");
     	n24.put("Nombre", "Mini Corn Dogs");
     	n24.put("Descripcion", "Deliciosas mini salchichas envueltas en un crujiente rebozado de maíz," +
     			"con un ligero toque de especias americanas. Acompañadas de nuestra salsa BBQ-Ranch");
     	n24.put("Breve", "Mini salchichas envueltas en rebozado de maíz acompañadas de salsa BBQ-Ranch");
     	n24.put("Foto", "fh4");
     	n24.put("Extras", "Salsa:Ranchera,Barbacoa,Frambuesa,Mexicana");
     	n24.put("Precio", 7.10);
     	db.insert("Restaurantes", null, n24);
     	idFoster++;
     	
     	/*************************************************************************************************/

     	/****************************************ENSALADAS************************************************/
     	ContentValues n25 = new ContentValues();
     	n25.put("Id", "fh"+idFoster);
     	n25.put("Restaurante", "Foster");
     	n25.put("Categoria", "Ensaladas");
     	n25.put("TipoPlato", "");
     	n25.put("Nombre", "Mediterranean Salad");
     	n25.put("Descripcion", "Ensalada con queso de cabra, jamón serrano en tiras, tomate cherry y pasas, sobre una mezcla de lechugas. Aliñada con vinagreta ligeramente dulce con matices de vino.");
     	n25.put("Breve", "Ensalada con queso de cabra, jamón serrano, tomate cherry y pasas");
     	n25.put("Foto", "fh5");
     	n25.put("Extras", "");
     	n25.put("Precio", 9.85);
     	db.insert("Restaurantes", null, n25);
     	idFoster++;
     	
     	ContentValues n26 = new ContentValues();
     	n26.put("Id", "fh"+idFoster);
     	n26.put("Restaurante", "Foster");
     	n26.put("Categoria", "Ensaladas");
     	n26.put("TipoPlato", "");
     	n26.put("Nombre", "Chicken & Prime Salad");
     	n26.put("Descripcion", "Sobre una cama de espinacas y brotes baby, una pechuga de pollo empanada, acompañadas de tomates secos y nueces. " +
     			"Sugerencia: alíñala con salsa de mostaza y miel. O si lo prefieres con salsa Roquefort, salsa rosa o aceite y vinagre.");
     	n26.put("Breve", "Ensalada con pechuga de pollo empanada, tomates secos y nueces");
     	n26.put("Foto", "fh6");
     	n26.put("Extras", "Salsa:Mostaza-Miel,Roquefor,Rosa,Aceite-Vinagre");
     	n26.put("Precio", 9.85);
     	db.insert("Restaurantes", null, n26);
     	idFoster++;
     	
     	ContentValues n27 = new ContentValues();
     	n27.put("Id", "fh"+idFoster);
     	n27.put("Restaurante", "Foster");
     	n27.put("Categoria", "Ensaladas");
     	n27.put("TipoPlato", "");
     	n27.put("Nombre", "Foster`s Caesar Salad");
     	n27.put("Descripcion", "Reinventamos el clásico. Tierna lechuga romana con pechuga de pollo a la parrilla, croutons y ahora delicioso queso Grana Padano en lascas. Aderazada con salsa parmesana.");
     	n27.put("Breve", "Ensalda con pechuga de pollo a la parrilla, croutons, queso Grana Padano y salsa César");
     	n27.put("Foto", "fnd_fh");
     	n27.put("Extras", "");
     	n27.put("Precio", 9.85);
     	db.insert("Restaurantes", null, n27);
     	idFoster++;
     	
     	ContentValues n28 = new ContentValues();
     	n28.put("Id", "fh"+idFoster);
     	n28.put("Restaurante", "Foster");
     	n28.put("Categoria", "Ensaladas");
     	n28.put("TipoPlato", "");
     	n28.put("Nombre", "Santa Mónica Salad");
     	n28.put("Descripcion", "Mezcla de lechugas con trozos de pollo empanado, bacon crujiente, mezcla de quesos rallados, tomate y zanahoria. " +
     			"Servida con salsa Roquefort, salsa rosa, salsa de mostaza y miel o aceite y vinagre.");
     	n28.put("Breve", "Lechugas con trozos de pollo empanado, bacon, mezcla de quesos, tomate y zanahoria");
     	n28.put("Foto", "fnd_fh");
     	n28.put("Extras", "Salsa:Roquefort,Rosa,Mostaza-Miel,Aceite-Vinagre");
     	n28.put("Precio", 9.85);
     	db.insert("Restaurantes", null, n28);
     	idFoster++;
     	
     	/*************************************************************************************************/
     	
     	/*****************************************HAMBURGUESAS********************************************/
    	ContentValues bb1 = new ContentValues();
    	bb1.put("Id", "fh"+idFoster);
    	bb1.put("Restaurante", "Foster");
    	bb1.put("Categoria", "Principal");
    	bb1.put("TipoPlato", "Hamburguesa");
    	bb1.put("Nombre", "PLATO ESTRELLA: Director`s Choice");
    	bb1.put("Descripcion", "Con delicioso queso Cheddar fundido, bacon" +
       			"tomate en rodajas, mezclas de brotes baby," +
       			"cebolla morada y mayonesa.");
    	bb1.put("Breve", "Cheddar fundido, bacon, tomate, brotes baby, cebolla morada y mayonesa");
    	bb1.put("Foto", "fh11");
    	bb1.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Salsa:Roquefort,Barbacoa/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
    	bb1.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb1);
     	idFoster++;
     	
     	ContentValues bb2= new ContentValues();
     	bb2.put("Id", "fh"+idFoster);
     	bb2.put("Restaurante", "Foster");
     	bb2.put("Categoria", "Principal");
     	bb2.put("TipoPlato", "Hamburguesa");
     	bb2.put("Nombre", "Bacon Burguer");
     	bb2.put("Descripcion","Con delicioso queso Cheddar fundido, bacon, " +
       			"tomate en rodajas, mezcla de brotes baby y " +
       			"mayonesa.");
     	bb2.put("Breve", "Cheddar fundido, bacon, tomate, brotes baby, cebolla morada y mayonesa");
     	bb2.put("Foto", "fnd_fh");
     	bb2.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb2.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb2);
     	idFoster++;
     	
     	ContentValues bb3= new ContentValues();
     	bb3.put("Id", "fh"+idFoster);
     	bb3.put("Restaurante", "Foster");
     	bb3.put("Categoria", "Principal");
     	bb3.put("TipoPlato", "Hamburguesa");
     	bb3.put("Nombre", "Cheeseburguer");
     	bb3.put("Descripcion","Como debe servirse según los puristas. Delicioso " +
       			"queso Cheddar fundido, tomate en rodajas, mezcla " +
       			"de brotes baby y mayonesa.");
     	bb3.put("Breve", "Cheddar fundido, tomate, brotes baby, cebolla morada y mayonesa");
     	bb3.put("Foto", "fnd_fh");
     	bb3.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb3.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb3);
     	idFoster++;
     	
     	ContentValues bb4= new ContentValues();
     	bb4.put("Id", "fh"+idFoster);
     	bb4.put("Restaurante", "Foster");
     	bb4.put("Categoria", "Principal");
     	bb4.put("TipoPlato", "Hamburguesa");
     	bb4.put("Nombre", "Roquefort Burger");
     	bb4.put("Descripcion","Con queso Roquefort batido. " +
       			"Sobre la nueva y jugosa carne de hamburguesa, " +
       			"cremoso queso Roquefort batido. Servida en pan " +
       			"artesanal y acompañada con mezcla de brotes baby, " +
       			"cebolla morada y tomate en rodajas.");
     	bb4.put("Breve", "Roquefort batido, jugosa carne y brotes baby");
     	bb4.put("Foto", "fnd_fh");
     	bb4.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb4.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb4);
     	idFoster++;
     	
     	ContentValues bb5= new ContentValues();
     	bb5.put("Id", "fh"+idFoster);
     	bb5.put("Restaurante", "Foster");
     	bb5.put("Categoria", "Principal");
     	bb5.put("TipoPlato", "Hamburguesa");
     	bb5.put("Nombre", "Chiliburger");
     	bb5.put("Descripcion","Con chili con carne. " +
       			"Para los más atrevidos: nuevo pan artesanal, gruesa " +
       			"y jugosa carne de hamburguesa y chili con carne. Con " +
       			"mezcla de brotes baby, cebolla morada y tomate en rodajas.");
     	bb5.put("Breve", "Jugosa carne de hamburguesa y chili con carne");
     	bb5.put("Foto", "fnd_fh");
     	bb5.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb5.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb5);
     	idFoster++;
     	
     	ContentValues bb6= new ContentValues();
     	bb6.put("Id", "fh"+idFoster);
     	bb6.put("Restaurante", "Foster");
     	bb6.put("Categoria", "Principal");
     	bb6.put("TipoPlato", "Hamburguesa");
     	bb6.put("Nombre", "`All American´ Burger");
     	bb6.put("Descripcion","La más natural. " +
       			"Nuestra nueva carne de hamburguesa y pan " +
       			"artesanal, servida con la nueva mezcla de brotes baby, " +
       			"cebolla morada, tomate en rodajas y mayonesa.");
     	bb6.put("Breve", "Carne de hamburguesa y pan, con brotes baby, cebolla morada, tomate y mayonesa");
     	bb6.put("Foto", "fnd_fh");
     	bb6.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb6.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb6);
     	idFoster++;
     	
     	ContentValues bb7= new ContentValues();
     	bb7.put("Id", "fh"+idFoster);
     	bb7.put("Restaurante", "Foster");
     	bb7.put("Categoria", "Principal");
     	bb7.put("TipoPlato", "Hamburguesa");
     	bb7.put("Nombre", "Bar-B-Q Burger");
     	bb7.put("Descripcion","La más natural. " +
       			"Con salsa barbacoa. " +
       			"Nuestra deliciosa salsa barbacoa acompañando " +
       			"a la nueva carne de vacuno y al pan artesanal. " +
       			"Servida con mezcla de brotes baby, cebolla morada y " +
       			"tomate en rodajas.");
     	bb7.put("Breve", "Carne de hamburguesa y pan. Con salsa barbacoa, brotes baby, cebolla morada y tomate");
     	bb7.put("Foto", "fnd_fh");
     	bb7.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb7.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb7);
     	idFoster++;
     	
     	ContentValues bb8= new ContentValues();
     	bb8.put("Id", "fh"+idFoster);
     	bb8.put("Restaurante", "Foster");
     	bb8.put("Categoria", "Principal");
     	bb8.put("TipoPlato", "Hamburguesa");
     	bb8.put("Nombre", "BBQ Egg Burger");
     	bb8.put("Descripcion","Con huevo y bacon. " +
       			"220 grs.de jugosa carne de vacuno servida en pan artesanal " +
       			"con huevo, bacon, cebolla pochada y salsa barbacoa. " +
       			"Acompañada con mezcla de brotes baby, tomate en " +
       			"rodajas.");
     	bb8.put("Breve", "220 grs de carne, con huevo, bacon, cebolla pochada y salsa barbacoa");
     	bb8.put("Foto", "fh18");
     	bb8.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	bb8.put("Precio",10.85);
     	db.insert("Restaurantes", null, bb8);
     	idFoster++;
     	
     	ContentValues b9= new ContentValues();
     	b9.put("Id", "fh"+idFoster);
     	b9.put("Restaurante", "Foster");
       	b9.put("Categoria", "Principal");
       	b9.put("TipoPlato", "Hamburguesa");
       	b9.put("Nombre", "Philadelphia Onion Burger");
       	b9.put("Descripcion","Jugosa carne de vacuno, Philadelphia y cebolla caramelizada. " +
       			"Servida con tomate en rodajas, mezcla de brotes baby y " +
       			"mayonesa. La mezcla de sabores te sorprenderá.");
       	b9.put("Breve", "Carne de vacuno, Philadelphia y cebolla caramelizada");
     	b9.put("Foto", "fh19");
     	b9.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
       	b9.put("Precio",10.85);
     	db.insert("Restaurantes", null, b9);
     	idFoster++;
     	
     	ContentValues b10= new ContentValues();
     	b10.put("Id", "fh"+idFoster);
     	b10.put("Restaurante", "Foster");
       	b10.put("Categoria", "Principal");
       	b10.put("TipoPlato", "Hamburguesa");
       	b10.put("Nombre", "Mediterranean Burger");
       	b10.put("Descripcion","Selecta carne de vacuno, queso de cabra, tiras de jamón serrano, " +
       			"tomate en dados y un ligero toque de ajo frito.");
       	b10.put("Breve", "Carne de vacuno, queso de cabra y tiras de jamón serrano");
     	b10.put("Foto", "fnd_fh");
     	b10.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
       	b10.put("Precio",10.85);
     	db.insert("Restaurantes", null, b10);
     	idFoster++;
     	
     	ContentValues b11= new ContentValues();
     	b11.put("Id", "fh"+idFoster);
     	b11.put("Restaurante", "Foster");
       	b11.put("Categoria", "Principal");
       	b11.put("TipoPlato", "Hamburguesa");
       	b11.put("Nombre", "Director`s Chicken Burger");
       	b11.put("Descripcion","Filete de pechuga de pollo a la parrilla, servido en nuestro pan " +
       			"artesano de hamburguesa, con delicioso queso Cheddar fundido, " +
       			"bacon, mezcla de brotes baby, tomate y mayonesa.");
       	b11.put("Breve", "Pechuga de pollo, queso Cheddar fundido, bacon, brotes baby, tomate y mayonesa");
     	b11.put("Foto", "fnd_fh");
     	b11.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
       	b11.put("Precio",9.70);
     	db.insert("Restaurantes", null, b11);
     	idFoster++;
     	
     	/*************************************************************************************************/
     	
     	/***************************************COSTILLAS*************************************************/
     	ContentValues n29 = new ContentValues();
     	n29.put("Id", "fh"+idFoster);
     	n29.put("Restaurante", "Foster");
     	n29.put("Categoria", "Principal");
     	n29.put("TipoPlato", "Costillas");
     	n29.put("Nombre", "Foster`s Iberian Ribs");
     	n29.put("Descripcion", "Costillas de cerdo ibérico cocinadas al estilo " +
     			"Foster`s Hollywood. Servidas con salsa barbacoa. Costillar completo");
     	n29.put("Breve", "Costillas de cerdo ibérico con salsa barbacoa");
     	n29.put("Foto", "fh9");
     	n29.put("Extras", "Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n29.put("Precio", 15.85);
     	db.insert("Restaurantes", null, n29);
     	idFoster++;
     	
     	ContentValues n30 = new ContentValues();
     	n30.put("Id", "fh"+idFoster);
     	n30.put("Restaurante", "Foster");
     	n30.put("Categoria", "Principal");
     	n30.put("TipoPlato", "Costillas");
     	n30.put("Nombre", "National Star Ribs");
     	n30.put("Descripcion", "Jugosas costillas de cerdo ahumadas, " +
     			"especialmente seleccionadas para Foster`s Hollywood.");
     	n30.put("Breve", "Costillas de cerdo ahumadas con salsa a elegir");
     	n30.put("Foto", "fnd_fh");
     	n30.put("Extras", "Salsa:Barbacoa,Cajun,Barbacoa-Miel/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n30.put("Precio", 15.45);
     	db.insert("Restaurantes", null, n30);
     	idFoster++;
     	
     	/*************************************************************************************************/

     	/******************************************PARRILLA***********************************************/
     	ContentValues n31 = new ContentValues();
     	n31.put("Id", "fh"+idFoster);
     	n31.put("Restaurante", "Foster");
     	n31.put("Categoria", "Principal");
     	n31.put("TipoPlato", "Parrilla");
     	n31.put("Nombre", "The Newyorker");
     	n31.put("Descripcion", "Jugoso entrecotte sazonado y preparado en nuestra parrilla. " +
     			"Servido con dos tostadas de pan de ajo y salsa de champiñones.");
     	n31.put("Breve", "325 Grs de entrecotte sazonado con salsa de champiñones");
     	n31.put("Foto", "fh22");
     	n31.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n31.put("Precio", 16.75);
     	db.insert("Restaurantes", null, n31);
     	idFoster++;
     	
     	ContentValues n32 = new ContentValues();
     	n32.put("Id", "fh"+idFoster);
     	n32.put("Restaurante", "Foster");
     	n32.put("Categoria", "Principal");
     	n32.put("TipoPlato", "Parrilla");
     	n32.put("Nombre", "Solomillo Supreme");
     	n32.put("Descripcion", "Solomillo de primera calidad servido con dos lonchas de bacon y cebolla. " +
     			"Acompañado de dos tostadas de pan de ajo y salsa de champiñones");
     	n32.put("Breve", "250 Grs de solomillo con bacon, cebolla, pan de ajo y salsa de champiñones");
     	n32.put("Foto", "fh23");
     	n32.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n32.put("Precio", 18.65);
     	db.insert("Restaurantes", null, n32);
     	idFoster++;
     	
     	ContentValues n33 = new ContentValues();
     	n33.put("Id", "fh"+idFoster);
     	n33.put("Restaurante", "Foster");
     	n33.put("Categoria", "Principal");
     	n33.put("TipoPlato", "Parrilla");
     	n33.put("Nombre", "Salisbury Steaks");
     	n33.put("Descripcion", "El origen de la hamburguesa. " +
     			"Carne picada 100 % vacuno, cubierta de salsa de champiñón " +
     			"cebolla pochada y una mezcla de quesos fundidos. " +
     			"Servido con patatas fritas y ensalada de lechuga, tomate y jalapeños.");
     	n33.put("Breve", "Carne picada de vacuno con salsa de champiñón, cebolla pochada y quesos fundidos");
     	n33.put("Foto", "fnd_fh");
     	n33.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n33.put("Precio", 12.45);
     	db.insert("Restaurantes", null, n33);
     	idFoster++;
     	
     	ContentValues n34 = new ContentValues();
     	n34.put("Id", "fh"+idFoster);
     	n34.put("Restaurante", "Foster");
     	n34.put("Categoria", "Principal");
     	n34.put("TipoPlato", "Parrilla");
     	n34.put("Nombre", "Solomillo Kentucky");
     	n34.put("Descripcion", "Solomillo de cerdo a la parrilla con salsa " +
     			"bourbon, cebolla pochada y tomates cherry. " +
     			"Elige tu guarnición: arroz americano o ensalada Green & Cheese.");
     	n34.put("Breve", "Solomillo de cerdo con salsa bourbon, cebolla pochada y tomates cherry");
     	n34.put("Foto", "fnd_fh");
     	n34.put("Extras", "Carne:Poco hecha,Al punto,Muy hecha/Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n34.put("Precio", 14.60);
     	db.insert("Restaurantes", null, n34);
     	idFoster++;
     	
     	/*************************************************************************************************/

     	
     	/*********************************************TEX-MEX*********************************************/
     	ContentValues n35 = new ContentValues();
     	n35.put("Id", "fh"+idFoster);
     	n35.put("Restaurante", "Foster");
     	n35.put("Categoria", "Principal");
     	n35.put("TipoPlato", "Tex-mex");
     	n35.put("Nombre", "Fajitas El Paso");
     	n35.put("Descripcion", "De carne, pollo o combinadas. Servidas en una sartén caliente y cocinadas con " +
     			"cebolla y pimientos. Envuelve todo en las " +
     			"tortillas de harina de trigo y añade crema agria, " +
     			"guacamole, salsa mexicana,tomate y mezcla de quesos.");
     	n35.put("Breve", "Fajitas en una sartén caliente con cebolla y pimientos");
     	n35.put("Foto", "fh26");
     	n35.put("Extras", "");
     	n35.put("Precio", 14.50);
     	db.insert("Restaurantes", null, n35);
     	idFoster++;
     	
     	ContentValues n36 = new ContentValues();
     	n36.put("Id", "fh"+idFoster);
     	n36.put("Restaurante", "Foster");
     	n36.put("Categoria", "Principal");
     	n36.put("TipoPlato", "Tex-mex");
     	n36.put("Nombre", "Yaki Soft Tacos");
     	n36.put("Descripcion", "Tortillas de trigo rellenas de lechuga romana, " +
     			"salteado de verduras, pollo a la parrilla, piña y " +
     			"salsa teriyaki. Acompañadas de arroz basmati, salsa thai y gajos de lima.");
     	n36.put("Breve", "Tortillas de trigo con pollo a la parrilla, piña y salsa teriyaki");
     	n36.put("Foto", "fh27");
     	n36.put("Extras", "");
     	n36.put("Precio", 9.95);
     	db.insert("Restaurantes", null, n36);
     	idFoster++;
     	
     	ContentValues n37 = new ContentValues();
     	n37.put("Id", "fh"+idFoster);
     	n37.put("Restaurante", "Foster");
     	n37.put("Categoria", "Principal");
     	n37.put("TipoPlato", "Tex-mex");
     	n37.put("Nombre", "Burrito Pancho");
     	n37.put("Descripcion", "Dos tortillas de trigo a la plancha, cubiertas con " +
     			"salsa mexicana, mezclas de quesos fundidos y crema agria, rellenas a tu elección: pollo" +
     			" especiado, carne de vacuno o una combinación de ambas.");
     	n37.put("Breve", "Tortillas de trigo con salsa, mezcla de quesos y rellena de carne a tu elección");
     	n37.put("Foto", "fnd_fh");
     	n37.put("Extras", "");
     	n37.put("Precio", 9.45);
     	db.insert("Restaurantes", null, n37);
     	idFoster++;
     	
     	ContentValues n38 = new ContentValues();
     	n38.put("Id", "fh"+idFoster);
     	n38.put("Restaurante", "Foster");
     	n38.put("Categoria", "Principal");
     	n38.put("TipoPlato", "Tex-mex");
     	n38.put("Nombre", "Thai Chicken Enchilada");
     	n38.put("Descripcion", "Deliciosa tortilla roja elaborada con trigo y tomate, " +
     			"rellena de pollo a la parrilla, mezcla de " +
     			"quesos fundidos y salsa satay. Coronada con salsa de tomatillo verde ligeramente picante, " +
     			"crema agria, pico de gallo y aros de cebolla morada.");
     	n38.put("Breve", "Tortilla roja rellena de pollo, mezcla de quesos y salsa satay");
     	n38.put("Foto", "fnd_fh");
     	n38.put("Extras", "");
     	n38.put("Precio", 9.95);
     	db.insert("Restaurantes", null, n38);
     	idFoster++;
     	
     	/*************************************************************************************************/

     	/*******************************************POLLO*************************************************/
     	ContentValues n39 = new ContentValues();
     	n39.put("Id", "fh"+idFoster);
     	n39.put("Restaurante", "Foster");
     	n39.put("Categoria", "Principal");
     	n39.put("TipoPlato", "Pollo");
     	n39.put("Nombre", "Famous Hollywood Coquelet");
     	n39.put("Descripcion", "Delicioso pollo Coquelet elaborado a la parrilla con un ligero toque de salsa barbacoa. " +
     			"Servido con patatas fritas y mezcla de brotes baby, tomates secos, croutons y salsa parmesana.");
     	n39.put("Breve", "Pollo Coquelet con un ligero toque de salsa barbacoa");
     	n39.put("Foto", "fh30");
     	n39.put("Extras", "");
     	n39.put("Precio", 12.30);
     	db.insert("Restaurantes", null, n39);
     	idFoster++;
     	
     	ContentValues n40 = new ContentValues();
     	n40.put("Id", "fh"+idFoster);
     	n40.put("Restaurante", "Foster");
     	n40.put("Categoria", "Principal");
     	n40.put("TipoPlato", "Pollo");
     	n40.put("Nombre", "Cavatappi Chicken Pasta");
     	n40.put("Descripcion", "Exclusiva pasta Cavatappi con una deliciosa crema elaborada con nata, mezcla de quesos, " +
     			"cebolla caramelizada y salsa bufalo ligeramente picante. Servida con pechuga de pollo al estilo  " +
     			"cajun o a la parrilla y dos tostadas de pan de ajo.");
     	n40.put("Breve", "Pasta Cavatappi con pechuga de pollo, salsa y mezcla de quesos");
     	n40.put("Foto", "fh31");
     	n40.put("Extras", "");
     	n40.put("Precio", 10.55);
     	db.insert("Restaurantes", null, n40);
     	idFoster++;
     	
     	ContentValues n41 = new ContentValues();
     	n41.put("Id", "fh"+idFoster);
     	n41.put("Restaurante", "Foster");
     	n41.put("Categoria", "Principal");
     	n41.put("TipoPlato", "Pollo");
     	n41.put("Nombre", "Teriyaki Chicken Brochette");
     	n41.put("Descripcion", "Cinco brochetas de pechugas de pollo marinada " +
     			"en salsa whe sobre una cama de arroz americano. " +
     			"Las servimos con salsa de soja y sesamo.");
     	n41.put("Breve", "Brochetas de pollo con salsa whe en una cama de arroz americano");
     	n41.put("Foto", "fnd_fh");
     	n41.put("Extras", "");
     	n41.put("Precio", 10.55);
     	db.insert("Restaurantes", null, n41);
     	idFoster++;
     	
     	/*************************************************************************************************/
     	
     	/*******************************************SANDWICHES********************************************/
     	ContentValues n42 = new ContentValues();
     	n42.put("Id", "fh"+idFoster);
     	n42.put("Restaurante", "Foster");
     	n42.put("Categoria", "Principal");
     	n42.put("TipoPlato", "Sandwiches");
     	n42.put("Nombre", "Gourmet Haute Dog");
     	n42.put("Descripcion", "Gran Hot Dog. Salchicha XXL autenticamente americana " +
     			"servida con cebolla pochada, mezcla de quesos fundidos, pico de gallo, relish de  " +
     			"pepinillo y cebolla morada. Acompañada de ensalada de col picada y patatas fritas.");
     	n42.put("Breve", "Gran Hot Dog con cebolla pochada, mezcla de quesos y más");
     	n42.put("Foto", "fh33");
     	n42.put("Extras", "Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n42.put("Precio", 9.10);
     	db.insert("Restaurantes", null, n42);
     	idFoster++;
     	
     	ContentValues n43 = new ContentValues();
     	n43.put("Id", "fh"+idFoster);
     	n43.put("Restaurante", "Foster");
     	n43.put("Categoria", "Principal");
     	n43.put("TipoPlato", "Sandwiches");
     	n43.put("Nombre", "Foster`s Philly Sandwich");
     	n43.put("Descripcion", "Carne de vacuno asada y troceada con salsa de " +
     			"champiñones y cebolla pochada. Cubierta de " +
     			"una mezcla de quesos fundidos y servido en pan de ciabatta.");
     	n43.put("Breve", "Carne de vacuno con salsa de champiñones y cebolla pochada");
     	n43.put("Foto", "fh34");
     	n43.put("Extras", "Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n43.put("Precio", 9.50);
     	db.insert("Restaurantes", null, n43);
     	idFoster++;
     	
     	ContentValues n44 = new ContentValues();
     	n44.put("Id", "fh"+idFoster);
     	n44.put("Restaurante", "Foster");
     	n44.put("Categoria", "Principal");
     	n44.put("TipoPlato", "Sandwiches");
     	n44.put("Nombre", "Caesar Sandwich");
     	n44.put("Descripcion", "Delicioso pan de centeno relleno de lechuga " +
     			"romana, salsa parmesana, bacon crujiente y pechuga de pollo a la parrilla cortada en tiras.");
     	n44.put("Breve", "Pan de centeno, pollo a la parrilla, salsa parmesana y bacon crujiente");
     	n44.put("Foto", "fnd_fh");
     	n44.put("Extras", "Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n44.put("Precio", 9.50);
     	db.insert("Restaurantes", null, n44);
     	idFoster++;
     	
     	ContentValues n45 = new ContentValues();
     	n45.put("Id", "fh"+idFoster);
     	n45.put("Restaurante", "Foster");
     	n45.put("Categoria", "Principal");
     	n45.put("TipoPlato", "Sandwiches");
     	n45.put("Nombre", "Roll & Rock");
     	n45.put("Descripcion", "Envuelto en tortillas de harina de trigo, pavo en " +
     			"lonchas, queso Cheedar en tiras, tomate en dados, lechuga romana y salsa ranchera.");
     	n45.put("Breve", "Pavo en lonchas, queso Cheedar, tomate, lechuga y salsa ranchera");
     	n45.put("Foto", "fnd_fh");
     	n45.put("Extras", "Guarnicion:Patatas Fritas,Patata Asada,Ensalada Green&Cheese,Ensalada Tomate y Lechuga");
     	n45.put("Precio", 9.10);
     	db.insert("Restaurantes", null, n45);
     	idFoster++;
     	
     	/*************************************************************************************************/
     	
     	/******************************************POSTRES************************************************/
     	ContentValues n59 = new ContentValues();
     	n59.put("Id", "fh"+idFoster);
     	n59.put("Restaurante", "Foster");
     	n59.put("Categoria", "Postre");
     	n59.put("TipoPlato", "");
     	n59.put("Nombre", "New York Cheese Cake");
     	n59.put("Descripcion", "Desde NY City regresa la auténtica tarta de queso, " +
     			"elaborada con queso crema y acompañada de nuestra exclusiva salsa de frutos del bosque.");
     	n59.put("Breve", "Tarta de queso acompañada de salsa de frutos del bosque");
     	n59.put("Foto", "fh39");
     	n59.put("Extras", "");
     	n59.put("Precio", 5.35);
     	db.insert("Restaurantes", null, n59);
     	idFoster++;
     	
     	ContentValues n60 = new ContentValues();
     	n60.put("Id", "fh"+idFoster);
     	n60.put("Restaurante", "Foster");
     	n60.put("Categoria", "Postre");
     	n60.put("TipoPlato", "");
     	n60.put("Nombre", "Big Chocolate Cookie");
     	n60.put("Descripcion", "Desde Norteamérica llega esta deliciosa Cookie " +
     			"y Foster`s Hollywood la acompaña de helado de " +
     			"vainilla, chocolate caliente y nata. Born in the USA!");
     	n60.put("Breve", "Cookie con chocolate caliente, nata y helado de vainilla");
     	n60.put("Foto", "fh40");
     	n60.put("Extras", "");
     	n60.put("Precio", 5.35);
     	db.insert("Restaurantes", null, n60);
     	idFoster++;
     	
     	ContentValues n61 = new ContentValues();
     	n61.put("Id", "fh"+idFoster);
     	n61.put("Restaurante", "Foster");
     	n61.put("Categoria", "Postre");
     	n61.put("TipoPlato", "");
     	n61.put("Nombre", "`All Star´ Brownie");
     	n61.put("Descripcion", "La estrella de Foster`s Hollywood. Un dulce caliente de chocolate, " +
     			"acompañado de helado de vainilla, cubierto con nueces y nuestra salsa de " +
     			"chocolate caliente. Típicamente americano.");
     	n61.put("Breve", "Brownie con chocolate caliente, nata y helado de vainilla");
     	n61.put("Foto", "fh41");
     	n61.put("Extras", "");
     	n61.put("Precio", 5.35);
     	db.insert("Restaurantes", null, n61);
     	idFoster++;
     	
     	ContentValues n62 = new ContentValues();
     	n62.put("Id", "fh"+idFoster);
     	n62.put("Restaurante", "Foster");
     	n62.put("Categoria", "Postre");
     	n62.put("TipoPlato", "");
     	n62.put("Nombre", "Oreo Cookie Sundae");
     	n62.put("Descripcion", "Las famosas galletas Oreo llegan a Foster`s " +
     			"Hollywood y las combinamos con dos grandes " +
     			"bolas de helado de vainilla, salsa de chocolate y " +
     			"nata. A todos nos encanta!");
     	n62.put("Breve", "Gallega Oreo con helado de vainilla, salsa de chocolate y nata");
     	n62.put("Foto", "fh42");
     	n62.put("Extras", "");
     	n62.put("Precio", 5.35);
     	db.insert("Restaurantes", null, n62);
     	idFoster++;
     	
     	ContentValues n63 = new ContentValues();
     	n63.put("Id", "fh"+idFoster);
     	n63.put("Restaurante", "Foster");
     	n63.put("Categoria", "Postre");
     	n63.put("TipoPlato", "");
     	n63.put("Nombre", "Sweet Lolly Pops");
     	n63.put("Descripcion", "Déjate sorprender por los nuevos Lolly Pops de " +
     			"Foster`s Hollywood. Seis deliciosos bocados de " +
     			"tarta de queso y tarta de chocolate con " +
     			"diferentes y divertidas coberturas. Pruébalos!");
     	n63.put("Breve", "Lolly Pops de tarta de queso y tarta de chocolate");
     	n63.put("Foto", "fh43");
     	n63.put("Extras", "");
     	n63.put("Precio", 5.35);
     	db.insert("Restaurantes", null, n63);
     	idFoster++;
     	
     	ContentValues n64 = new ContentValues();
     	n64.put("Id", "fh"+idFoster);
     	n64.put("Restaurante", "Foster");
     	n64.put("Categoria", "Postre");
     	n64.put("TipoPlato", "");
     	n64.put("Nombre", "El Gran Milk Shake Americano");
     	n64.put("Descripcion", "Al estilo americano. Un delicioso batido " +
     			"cremoso y frío de diferentes sabores.");
     	n64.put("Breve", "Batido cremoso y frío de diferentes sabores");
     	n64.put("Foto", "fh44");
     	n64.put("Extras", "");
     	n64.put("Precio", 4.65);
     	db.insert("Restaurantes", null, n64);
     	idFoster++;
     	
     	/**********************************************BEBIDAS********************************************/
     	ContentValues n48 = new ContentValues();
     	n48.put("Id", "fh"+idFoster);
     	n48.put("Restaurante", "Foster");
     	n48.put("Categoria", "Bebidas");
     	n48.put("TipoPlato", "");
     	n48.put("Nombre", "Agua Mineral");
     	n48.put("Descripcion", "Agua mineral");
     	n48.put("Breve", "Bebida refrescante");
     	n48.put("Foto", "agua_fontbella");
     	n48.put("Extras", "");
     	n48.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n48);
     	idFoster++;
     	
     	ContentValues n49 = new ContentValues();
     	n49.put("Id", "fh"+idFoster);
     	n49.put("Restaurante", "Foster");
     	n49.put("Categoria", "Bebidas");
     	n49.put("TipoPlato", "");
     	n49.put("Nombre", "Coca Cola");
     	n49.put("Descripcion", "Bebida refrescante");
     	n49.put("Breve", "Bebida refrescante");
     	n49.put("Foto", "cocacola");
     	n49.put("Extras", "");
     	n49.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n49);
     	idFoster++;
     	
     	ContentValues n50 = new ContentValues();
     	n50.put("Id", "fh"+idFoster);
     	n50.put("Restaurante", "Foster");
     	n50.put("Categoria", "Bebidas");
     	n50.put("TipoPlato", "");
     	n50.put("Nombre", "Coca Cola Zero");
     	n50.put("Descripcion", "Bebida refrescante");
     	n50.put("Breve", "Bebida refrescante");
     	n50.put("Foto", "cocacola_zero");
     	n50.put("Extras", "");
     	n50.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n50);
     	idFoster++;
     	
     	ContentValues n51 = new ContentValues();
     	n51.put("Id", "fh"+idFoster);
     	n51.put("Restaurante", "Foster");
     	n51.put("Categoria", "Bebidas");
     	n51.put("TipoPlato", "");
     	n51.put("Nombre", "Coca Cola Light");
     	n51.put("Descripcion", "Bebida refrescante");
     	n51.put("Breve", "Bebida refrescante");
     	n51.put("Foto", "cocacola_light");
     	n51.put("Extras", "");
     	n51.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n51);
     	idFoster++;
     	
     	ContentValues n52 = new ContentValues();
     	n52.put("Id", "fh"+idFoster);
     	n52.put("Restaurante", "Foster");
     	n52.put("Categoria", "Bebidas");
     	n52.put("TipoPlato", "");
     	n52.put("Nombre", "Fanta de Naranja");
     	n52.put("Descripcion", "Bebida refrescante");
     	n52.put("Breve", "Bebida refrescante");
     	n52.put("Foto", "fanta_naranja");
     	n52.put("Extras", "");
     	n52.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n52);
     	idFoster++;
     	
     	ContentValues n53 = new ContentValues();
     	n53.put("Id", "fh"+idFoster);
     	n53.put("Restaurante", "Foster");
     	n53.put("Categoria", "Bebidas");
     	n53.put("TipoPlato", "");
     	n53.put("Nombre", "Fanta de Limón");
     	n53.put("Descripcion", "Bebida refrescante");
     	n53.put("Breve", "Bebida refrescante");
     	n53.put("Foto", "fanta_limon");
     	n53.put("Extras", "");
     	n53.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n53);
     	idFoster++;
     	
     	ContentValues n55 = new ContentValues();
     	n55.put("Id", "fh"+idFoster);
     	n55.put("Restaurante", "Foster");
     	n55.put("Categoria", "Bebidas");
     	n55.put("TipoPlato", "");
     	n55.put("Nombre", "Sprite");
     	n55.put("Descripcion", "Bebida refrescante");
     	n55.put("Breve", "Bebida refrescante");
     	n55.put("Foto", "sprite");
     	n55.put("Extras", "");
     	n55.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n55);
     	idFoster++;
     	
     	ContentValues n56 = new ContentValues();
     	n56.put("Id", "fh"+idFoster);
     	n56.put("Restaurante", "Foster");
     	n56.put("Categoria", "Bebidas");
     	n56.put("TipoPlato", "");
     	n56.put("Nombre", "Nestea");
     	n56.put("Descripcion", "Bebida refrescante");
     	n56.put("Breve", "Bebida refrescante");
     	n56.put("Foto", "nestea");
     	n56.put("Extras", "");
     	n56.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n56);
     	idFoster++;
     	
     	ContentValues n57 = new ContentValues();
     	n57.put("Id", "fh"+idFoster);
     	n57.put("Restaurante", "Foster");
     	n57.put("Categoria", "Bebidas");
     	n57.put("TipoPlato", "");
     	n57.put("Nombre", "Cerveza Mahou");
     	n57.put("Descripcion", "Cerveza Mahou");
     	n57.put("Breve", "Bebida refrescante");
     	n57.put("Foto", "cerveza_mahou");
     	n57.put("Extras", "");
     	n57.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n57);
     	idFoster++;
     	
     	ContentValues n58 = new ContentValues();
     	n58.put("Id", "fh"+idFoster);
     	n58.put("Restaurante", "Foster");
     	n58.put("Categoria", "Bebidas");
     	n58.put("TipoPlato", "");
     	n58.put("Nombre", "Cerveza San Miguel");
     	n58.put("Descripcion", "Cerveza San Miguel");
     	n58.put("Breve", "Bebida refrescante");
     	n58.put("Foto", "cerveza_san_miguel");
     	n58.put("Extras", "");
     	n58.put("Precio", 3.00);
     	db.insert("Restaurantes", null, n58);
     	idFoster++;
     	
/************************************************FIN FOSTER*************************************************/
        // Cerramos la base de datos
     	sql.close();
    }
 }