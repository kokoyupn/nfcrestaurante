package com.example.nfcook_camarero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.provider.Settings;


public class Sincronizacion_BeamNfc extends Activity {

	

	 
	String numMesa;
	String idCamarero;
	String numPersonas;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sincronizacion_beam_nfc);
		
		//El numero de la mesa se obtiene de la pantalla anterior
		Bundle bundle = getIntent().getExtras();
		numMesa = bundle.getString("NumMesa");
		numPersonas = bundle.getString("Personas");
		idCamarero = bundle.getString("IdCamarero");
	}
	


 
	/*Menu que usaremos para activar el NFC y el sbeam*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nfc, menu);
        
        return true;
    }
    
   
	public boolean onOptionsItemSelected(MenuItem item) {
         Intent intent;
            switch (item.getItemId()) {
            case R.id.menu_nfc:
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.menu_sbeam:
                 intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
              
                startActivity(intent);
                return true;
           
            default:
                return super.onOptionsItemSelected(item);
        }
        }


	
    
  
}