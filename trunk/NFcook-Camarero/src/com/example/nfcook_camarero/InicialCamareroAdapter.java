package com.example.nfcook_camarero;


import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;



public class InicialCamareroAdapter extends ArrayAdapter<MesaView>{

    Context context; 
    int layoutResourceId;    
    ArrayList<MesaView> mesas ;
    
    public InicialCamareroAdapter(Context context, int layoutResourceId, ArrayList<MesaView> mesas) {
        super(context, layoutResourceId, mesas);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.mesas = mesas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MesaHolder holder = null;
        
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            row.setLayoutParams (new GridView.LayoutParams (110, 110));
            row.setPadding(8,8,8,8);
            
            holder = new MesaHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.textViewMesa);
            holder.txtPersonas = (TextView)row.findViewById(R.id.textViewPersonas);
            
            row.setTag(holder);
        }else {
            holder = (MesaHolder)row.getTag();
        }
        String m = mesas.get(position).getNumMesa().toString();
        holder.txtTitle.setText(m);
        String p = mesas.get(position).getNumPersonas().toString();
        holder.txtPersonas.setText(p);
        
        return row;
    }
    
    static class MesaHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtPersonas;
    }
}