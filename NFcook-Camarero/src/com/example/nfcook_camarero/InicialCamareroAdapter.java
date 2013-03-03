package com.example.nfcook_camarero;


import java.util.ArrayList;
import java.util.Iterator;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class InicialCamareroAdapter extends BaseAdapter {
	private ArrayList<MesaView> mesas;
	private LayoutInflater l_Inflater;
	

	public InicialCamareroAdapter(Context context, ArrayList<MesaView> mesas) {
		this.mesas = mesas;
		this.l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mesas.size();
	}

	public Object getItem(int position) {
		return mesas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textNumMesa;
		TextView textPersonas;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.imagen_mesa, null);
			convertView.setLayoutParams (new GridView.LayoutParams (110, 135));
			textNumMesa = (TextView) convertView.findViewById(R.id.textViewMesa);
			textPersonas = (TextView) convertView.findViewById(R.id.textViewPersonas);
			//coger el numMesa y el numPersonas de las mesas
			textNumMesa.setText(mesas.get(position).getNumMesa().toString());
			textPersonas.setText(mesas.get(position).getNumPersonas().toString());
			
			convertView.setTag(textNumMesa);
			convertView.setTag(textPersonas);
		} else {
			textNumMesa = (TextView) convertView.getTag();
			textPersonas = (TextView) convertView.getTag();
		
			textNumMesa.setText(mesas.get(position).getNumMesa().toString());
			textPersonas.setText(mesas.get(position).getNumPersonas().toString());
		}
		return convertView;
	}
	
}