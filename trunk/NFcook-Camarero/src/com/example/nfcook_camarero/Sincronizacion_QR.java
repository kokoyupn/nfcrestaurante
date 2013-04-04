package com.example.nfcook_camarero;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;



public class Sincronizacion_QR extends Activity {


	 
	
	String numMesa;
	String idCamarero;
	String numPersonas;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_qr);
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
		
		//Lectura QR
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE","QR_CODE_MODE");
			startActivityForResult(intent,0);

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
           if (resultCode == RESULT_OK) {
              String contents = intent.getStringExtra("SCAN_RESULT");
              // Handle successful scan
             Toast toast1 = Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_SHORT);
         
                toast1.show();
           } else if (resultCode == RESULT_CANCELED) {
              // Handle cancel
           }
        }
	}
	
}