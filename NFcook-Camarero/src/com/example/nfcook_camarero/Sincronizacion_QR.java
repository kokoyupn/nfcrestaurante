package com.example.nfcook_camarero;


import android.app.Activity;
import android.os.Bundle;



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

	}
	
}