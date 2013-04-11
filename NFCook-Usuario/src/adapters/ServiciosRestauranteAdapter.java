package adapters;

import java.util.ArrayList;

import com.example.nfcook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ServiciosRestauranteAdapter extends BaseAdapter{
	ArrayList<String> imagenes;
	private LayoutInflater l_Inflater;
	
	public ServiciosRestauranteAdapter(Context context, ArrayList<String> imagenes) {
		this.imagenes = imagenes;
		this.l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return imagenes.size();
	}

	public Object getItem(int position) {
		return imagenes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = l_Inflater.inflate(R.layout.imagen_generica, parent,false);
		ImageView img = (ImageView) convertView.findViewById(R.id.imageViewGenerica);				
		img.setImageResource(getDrawable(convertView.getContext(),imagenes.get(position)));
    	
		return convertView;
	}

	public int getDrawable(Context context, String name)
	{
	    return context.getResources().getIdentifier(name,
	            "drawable", context.getPackageName());
	}
}
