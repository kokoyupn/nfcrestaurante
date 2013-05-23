package fragments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import usuario.InicializarRestaurante;
import usuario.RecogerCuentaNFC;
import usuario.RecogerCuentaQR;
import baseDatos.HandlerDB;

import com.example.nfcook.R;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

import adapters.MiListCuentaAdapter;
import adapters.PadreListCuenta;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CuentaFragment extends Fragment{
	private View vista;
	private double total;
	private String restaurante;
	
	private HandlerDB sqlCuenta;
	private SQLiteDatabase dbCuenta;
	
	private ArrayList<PadreListCuenta> cuenta;
	
	private AlertDialog ventanaEmergenteElegirRecogerCuenta;
	private View vistaVentanaEmergenteRecogerCuenta;
	
	private NfcAdapter adapter;
	
	private static final int REQUEST_PAYPAL_CHECKOUT = 2;
	private static boolean paypalInicializado;
	
	private static boolean botonPayPalPulsado;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vista = inflater.inflate(R.layout.cuenta, container, false);
		botonPayPalPulsado = false;
		// Ponemos el título a la actividad
        // Recogemos ActionBar
        ActionBar actionbar = getActivity().getActionBar();
    	actionbar.setTitle(" CUENTA");
    	
		// Cargamos la estructura desde la que mostraremos en la listview
		cargarCuenta();
		// Creamos la listview y le aplicamos el adpater
		crearListView();
		
		crearVentanaEmergenteElegirSincronizacion();
	    ponerOnClickPayPal();
	    ponerOnClickSincronizar();
	    ponerOnClickSincronizarPedidoNFC();
	    ponerOnClickSincronizarPedidoQR();

	    // me devuelve null si no tiene NFC, si no, me devuelve el adapter nfc del dispositivo
        adapter = NfcAdapter.getDefaultAdapter(vista.getContext());
	    
		return vista;
    }
	
	public void crearListView(){
		ListView listaCuenta =  (ListView) vista.findViewById(R.id.listViewCuenta);
		
		// Creamos el adapater de la lista que mostrará la cuenta
		MiListCuentaAdapter adapaterListaCuenta = new MiListCuentaAdapter(this.getActivity().getApplicationContext(), cuenta);
		listaCuenta.setAdapter(adapaterListaCuenta);
		
		// Oyente de la lista
		listaCuenta.setOnItemClickListener(new ListView.OnItemClickListener()
    	{
			/*
			 * TODO Queda pendiente si aplicamos alguna función en el oyente de la lista o se deja así.
			 * (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
				// Simplemente nos mostrará un mensaje con el precio unitario del producto
				 Toast.makeText(getActivity().getApplicationContext(),"El precio unitario del producto es: " + cuenta.get(arg2).getPrecioUnidad() + "€",Toast.LENGTH_SHORT).show();	
			}
    	});	
	}
	
	public void cargarCuenta(){
		// Importamos la base de datos
        sqlCuenta  =new HandlerDB(this.getActivity().getApplicationContext(),"Cuenta.db"); 
     	dbCuenta = sqlCuenta.open();
     	
     	// Creamos la estructura cuenta
     	cuenta = new ArrayList<PadreListCuenta>();
     	
     	// Cargamos los datos de la bd
     	try{
			String[] campos = new String[]{"Plato, PrecioPlato"};//Campos que quieres recuperar
			String[] datos = new String[]{restaurante};	    	
			Cursor c = dbCuenta.query("Cuenta", campos, "Restaurante=?", datos,null, null,null);
	    	int i, numPlatos = 0;
	    	boolean anadido;
	    	PadreListCuenta padre;
	    	
	    	while(c.moveToNext()){
	    		i = 0;
	    		anadido = false;
	    		// Miramos si el plato ya está añadido
	    		while(i<numPlatos && !anadido){
	    			// Si está añadido, actualizamos el precio y la cantidad
	    			if (cuenta.get(i).getPlato().equals(c.getString(0))){
	    				anadido = true;
	    				cuenta.get(i).actualizaPrecioTotal(c.getDouble(1));
	    				cuenta.get(i).actualizaCantidad();
	    			}else{
	    				i++;
	    			}
	    		}
	    		
	    		// Si no estaba añadido, lo añadimos
	    		if(!anadido){
	    			padre = new PadreListCuenta(c.getString(0), c.getDouble(1));
	    			cuenta.add(padre);
	    			numPlatos++;
	    		}
	    		
	    		// Aumentamos el total de la cuenta
	    		total += c.getDouble(1);
	    	}
	    	
	    	// Cerramos la base de datos
	    	sqlCuenta.close();
	    	
	    	// Mostramos el total de la cuenta
	    	TextView totalCuenta = (TextView)vista.findViewById(R.id.textViewTotalCuenta);
	    	totalCuenta.setText(getTotal() + " €");
	    	
	    }catch(SQLiteException e){
	        Toast.makeText(getActivity().getApplicationContext(),"ERROR EN LA BASE DE DATOS CUENTA",Toast.LENGTH_SHORT).show();	
	    }   
	}
	
	public double getTotal() {
		return Math.rint(total*100)/100;
	}
	
	public void setRestaurante(String restaurante){
		this.restaurante = restaurante;
	}
	
	
	/**
	 * Crea una ventana emergente que muestra los tipos de sincronizacion de pedido
	 * disponibles.
	 */
	private void crearVentanaEmergenteElegirSincronizacion(){
		vistaVentanaEmergenteRecogerCuenta = LayoutInflater.from(vista.getContext()).inflate(R.layout.ventana_emergente_recoger_cuenta, null);
		ventanaEmergenteElegirRecogerCuenta = new AlertDialog.Builder(vista.getContext()).create();
		ventanaEmergenteElegirRecogerCuenta.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancelar", 
				new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {
						ventanaEmergenteElegirRecogerCuenta.dismiss();
					}
		});
		ventanaEmergenteElegirRecogerCuenta.setView(vistaVentanaEmergenteRecogerCuenta);
	}
	
	/**
	 * Crea el onClick la papelera para borrar todo el pedido.
	 */
	private void ponerOnClickPayPal() {
		ImageView botonPayPal = (ImageView) vista.findViewById(R.id.imagePagarPayPal);
		
		botonPayPal.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (!botonPayPalPulsado){
					botonPayPalPulsado = true;
					if (!paypalInicializado)
						inicializarPayPal();
					lanzarActivityPayPal();
				}
			}
		});
	}
	
		private void lanzarActivityPayPal(){
		
			PayPalPayment newPayment = new PayPalPayment(); 
			newPayment.setSubtotal(new BigDecimal(total)); 
			newPayment.setCurrencyType("Eur"); 
			newPayment.setRecipient("nfcookapp@gmail.com"); 
			newPayment.setMerchantName("NFCook");					
						
			
			Intent checkoutIntent = PayPal.getInstance().checkout(newPayment, this.getActivity() /*, new ResultDelegate()*/);
				    // Use the android's startActivityForResult() and pass in our
				    // Intent.
				    // This will start the library.
			this.startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);
			//Crea el timer para que el mensaje solo aparezca durante 3,5 segundos
			final Timer t = new Timer();
			t.schedule(new TimerTask() {
				public void run() { 
					botonPayPalPulsado = false;
					t.cancel(); 
				}
			}, 3000);
	}


	/**
	 * Crea el onClick la la imagen botonSincronizar.
	 * Compruena si las bases de datos estan vacias para permitir o no sincronizar.
	 * Si se puede abre una ventana emergente para elegir el metodo de sincronizacion.
	 */
	private void ponerOnClickSincronizar() {
		ImageView botonRecogerCuenta = (ImageView) vista.findViewById(R.id.imageRecogerCuenta);
		
		botonRecogerCuenta.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				ventanaEmergenteElegirRecogerCuenta.show();	
			}
		});
		
	}
	
	/**
	 * Crea el onClick la la imagen botonNFC.
	 * Si el adapter es null significa que el dispositivo no tiene NFC, entonces no puede
	 * sincronizar con este metodo y lanza un mensaje. Si no es null, se abre la ventana
	 * para sincronizar por NFC.
	 */
	private void ponerOnClickSincronizarPedidoNFC() {
		ImageView botonNFC = (ImageView) vistaVentanaEmergenteRecogerCuenta.findViewById(R.id.imageNFCSincronizar);
		
		botonNFC.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// cierro la ventana emergente
				ventanaEmergenteElegirRecogerCuenta.dismiss();
				if (adapter != null) {					
					// abro la ventana para sincronizar con NFC
					Intent intent = new Intent(getActivity(),RecogerCuentaNFC.class);
					intent.putExtra("Restaurante", restaurante);
					startActivityForResult(intent, 0);
				} else Toast.makeText(vista.getContext(),"Tu dispositivo no tiene NFC. Prueba a sincronizar tu pedido por QR.",Toast.LENGTH_LONG).show();
			} 
		});
	}
	
	/**
	 * Entra cuando regresa de una actividad lanzada con startActivityForResult (onClick de NFC y QR).
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		InicializarRestaurante.cargarTabCuenta();
		Fragment fragmentCuenta = new CuentaFragment();
		((CuentaFragment) fragmentCuenta).setRestaurante(restaurante);
		FragmentTransaction m = getFragmentManager().beginTransaction();
		m.replace(R.id.FrameLayoutPestanas, fragmentCuenta);
		m.commitAllowingStateLoss();
		if (requestCode == REQUEST_PAYPAL_CHECKOUT) PayPalActivityResult(requestCode,resultCode,data);
	}
	
	/**
	 * Crea el onClick la imagen botonQR
	 */
	private void ponerOnClickSincronizarPedidoQR() {
		ImageView botonQR = (ImageView) vistaVentanaEmergenteRecogerCuenta.findViewById(R.id.imageQRSincronizar);
		
		botonQR.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// abro ventana para sincronizar con QR
				Intent intent = new Intent(getActivity(),RecogerCuentaQR.class);
				intent.putExtra("Restaurante", restaurante);
				startActivityForResult(intent,0);
				// cierro la ventana emergente
				ventanaEmergenteElegirRecogerCuenta.dismiss();
				
			}
		});
		
	}
	public void inicializarPayPal() {
		
		Toast.makeText(getActivity(), "Por favor, espere mientras se carga PayPal", Toast.LENGTH_LONG).show();
		
		PayPal pp = PayPal.getInstance();

		if (pp == null) {  // Test to see if the library is already initialized

			// This main initialization call takes your Context, AppID, and target server
			pp = PayPal.initWithAppID(this.getActivity(), "APP-80W284485P519543T", PayPal.ENV_SANDBOX);

			// Required settings:
	
			// Set the language for the library
			pp.setLanguage("es_ES");
	
			// Some Optional settings:
	
			// Sets who pays any transaction fees. Possible values are:
			// FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY
			pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
	
			// true = transaction requires shipping
			pp.setShippingEnabled(false);
		}
		
		paypalInicializado = true;
	}
	
	
	public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (resultCode) {
		// The payment succeeded
		case Activity.RESULT_OK:
			Toast.makeText(this.getActivity(), "El pago fue realizado con éxito",1).show();
		break;

		// The payment was canceled
		case Activity.RESULT_CANCELED:
			Toast.makeText(this.getActivity(), "El pago fue cancelado",1).show();
		break;

		// The payment failed, get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
		case PayPalActivity.RESULT_FAILURE:
			Toast.makeText(this.getActivity(), "Error al procesar el pago",1).show();
		}
		}
	
}
