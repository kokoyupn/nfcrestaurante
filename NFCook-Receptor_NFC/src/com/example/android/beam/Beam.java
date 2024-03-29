/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.beam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
@SuppressLint({ "HandlerLeak", "NewApi" })
public class Beam extends Activity implements CreateNdefMessageCallback,
        OnNdefPushCompleteCallback{
    NfcAdapter mNfcAdapter;
    TextView mInfoText;
    private static final int MESSAGE_SENT = 1;
    private boolean mandado = false;
    
    private final String USUARIO = "nfcookapp@gmail.com";
    private final String CONTRANESA = "Macarrones";
    
    private String cuentas = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mInfoText = (TextView) findViewById(R.id.textView1);
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mInfoText = (TextView) findViewById(R.id.textView1);
            Toast.makeText(Beam.this, "NFC no est� disponible en este dispositivo.", Toast.LENGTH_LONG).show(); 
        } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }


    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
	@SuppressLint("NewApi")
	@Override
    public NdefMessage createNdefMessage(NfcEvent event) {
		//Creamos un objeto de escucha
		final EscucharCuenta escuchaCuentas = new EscucharCuenta(USUARIO, CONTRANESA);
		//Creamos un hilo para poder escuchar la cuenta.
		Thread hiloCuenta = new Thread(new Runnable() {
		    public void run() {
		    	try {
					escuchaCuentas.esperaYprocesaCuentas();
				} catch (Exception e) {
					e.printStackTrace();
				}		    }
		  });
		//Corremos el hilo
		hiloCuenta.start();
		//Esperamos hasta que termine
		while(hiloCuenta.isAlive()){}
		//Recogemos la cuenta
    	cuentas = escuchaCuentas.recogeTodasLasCuentas();
        NdefMessage msg = new NdefMessage(NdefRecord.createMime(
                "application/recoger", cuentas.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
             );
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
        mandado = false;
    }

    /** This handler receives a message from onNdefPushComplete */
    @SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Mensaje enviado!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        
        if(!mandado){
        	// Mandamos el mail con el pedido que nos ha llegado
        	mandarMailPedido(new String(msg.getRecords()[0].getPayload()));
        	mandado = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If NFC is not available, we won't be needing this menu
        if (mNfcAdapter == null) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void mandarMailPedido(String pedido){
    	Mail m = new Mail();
        m.setUser("nfcookapp@gmail.com");// username 
        m.setPass("Macarrones");// password

        String[] toArr = {"nfcookapp@gmail.com"}; 
        m.setTo(toArr); 
        m.setFrom("nfcookapp@gmail.com"); 
        m.setSubject("PEDIDO");
        
        //pedido = "0&1/@fh8+Poco hecha, Roquefort, Patatas Fritas*_*PLATO ESTRELLA: Director`s Choice*10.85*5*2*idCamarero*fechaHora";
        m.setBody(pedido); 

        try { 
          if(m.send()) { 
            Toast.makeText(Beam.this, "Pedido sincronizado correctamente.", Toast.LENGTH_LONG).show(); 
          } else { 
            Toast.makeText(Beam.this, "Pedido no sincronizado.", Toast.LENGTH_LONG).show();//Si usuario y enviante no coinciden 
          } 
        } catch(Exception e) { 
          Toast.makeText(Beam.this, "Error al sincronizar el pedido", Toast.LENGTH_LONG).show(); //Si ha habido fallos 
        } 
    }
}
