package com.example.nfcook_camarero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	EditText usuario;
	EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_main);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
   public void  onClickBotonEntrar(View boton)
   {
	   usuario = (EditText) findViewById(R.id.editTextUsuario);
	   password = (EditText) findViewById(R.id.editTextPass);
	
	   Intent intent = new Intent(this, InicialCamarero.class);
	   intent.putExtra("usuario", usuario.getText().toString());
	   startActivity(intent);
   }
}
