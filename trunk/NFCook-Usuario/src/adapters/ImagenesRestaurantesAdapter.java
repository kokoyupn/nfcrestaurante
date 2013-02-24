package adapters;

import java.util.ArrayList;

import com.example.nfcook.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImagenesRestaurantesAdapter extends BaseAdapter {
	private ArrayList<Integer> restaurantes;
	private LayoutInflater l_Inflater;

	public ImagenesRestaurantesAdapter(Context context, ArrayList<Integer> restaurantes) {
		this.restaurantes = restaurantes;
		this.l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return restaurantes.size();
	}

	public Object getItem(int position) {
		return restaurantes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView itemImage;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.imag_restaurantes, null);
			itemImage = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(itemImage);
		} else {
			itemImage = (ImageView) convertView.getTag();
		}
		
		itemImage.setImageResource(restaurantes.get(position));
		
		return convertView;
	}

}
