package adapters;

import java.util.ArrayList;

import com.example.nfcook_gerente.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase encargada de implementar el adapter de la lista expandible de platos que saldrá en el caso
 * de que la categoría de platos que hayamos seleccionado tenga varios tipos de platos (Ejem
 * categoría principal y dentro tenemos: hamburguesas, pastas, etc). El padre sería el tipo y los hijos
 * el conjunto de platos que agrupe.
 * 
 * @author Abel
 *
 */
public class MiExpandableListAdapterFicha extends BaseExpandableListAdapter{	
	private LayoutInflater inflater;
	private Context context;
    private ArrayList<PadreExpandableListFicha> datosFicha;
    
    public MiExpandableListAdapterFicha(Context context, ArrayList<PadreExpandableListFicha> datosFicha){
        inflater = LayoutInflater.from(context);
    	this.context = context;
    	this.datosFicha = datosFicha;
    }

	@Override
	public HijoExpandableListFicha getChild(int groupPosition, int childPosition) {
		return datosFicha.get(groupPosition).getDatos();
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ImageView imageView;
		TextView textView;
		HijoExpandableListFicha datos = datosFicha.get(groupPosition).getDatos();
		
		// Datos Personales
		if(datos instanceof HijoExpandableListFichaDatosPersonales){
			convertView = inflater.inflate(R.layout.hijo_lista_ficha_datos_personales, null);
			
			// Foto
			imageView = (ImageView) convertView.findViewById(R.id.imageViewFotoFicha);
			imageView.setImageResource(convertView.getResources().getIdentifier(((HijoExpandableListFichaDatosPersonales) datos).getFoto(),"drawable", context.getPackageName()));
			// Nombre
			textView = (TextView) convertView.findViewById(R.id.textViewNombre);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getNombre());
			// Sexo
			textView = (TextView) convertView.findViewById(R.id.textViewSexo);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getSexo());
			// Fecha de nacimiento
			textView = (TextView) convertView.findViewById(R.id.textViewFechaNacimiento);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getFechaNacimiento());
			// DNI
			textView = (TextView) convertView.findViewById(R.id.textViewDNI);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getDni());
			// Estado civil
			textView = (TextView) convertView.findViewById(R.id.textViewEstadoCivil);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getEstadoCivil());
			// Nacionaliad
			textView = (TextView) convertView.findViewById(R.id.textViewNacionalidad);
			textView.setText(((HijoExpandableListFichaDatosPersonales) datos).getNacionalidad());
			
		// Datos de Contacto
		}else if(datos instanceof HijoExpandableListFichaDatosContacto){
			convertView = inflater.inflate(R.layout.hijo_lista_ficha_datos_contacto, null);

			// Direccion
			textView = (TextView) convertView.findViewById(R.id.textViewDireccion);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getDireccion());
			// CP
			textView = (TextView) convertView.findViewById(R.id.textViewCP);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getCp());
			// Municipio y ciudad
			textView = (TextView) convertView.findViewById(R.id.textViewMunicipioCiudad);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getMunicipioCiudad());
			// Pais
			textView = (TextView) convertView.findViewById(R.id.textViewPais);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getPais());
			// Telefono
			textView = (TextView) convertView.findViewById(R.id.textViewTelefono);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getTelefono());
			// Mail
			textView = (TextView) convertView.findViewById(R.id.textViewMail);
			textView.setText(((HijoExpandableListFichaDatosContacto) datos).getMail());
	
		// Datos Laborales
		}else if(datos instanceof HijoExpandableListFichaDatosLaborales){
			convertView = inflater.inflate(R.layout.hijo_lista_ficha_datos_laborales, null);

			// Puesto
			textView = (TextView) convertView.findViewById(R.id.textViewPuesto);
			textView.setText(((HijoExpandableListFichaDatosLaborales) datos).getPuesto());
			// Horas al día
			textView = (TextView) convertView.findViewById(R.id.textViewHorasAlDia);
			textView.setText("Jornada de " + ((HijoExpandableListFichaDatosLaborales) datos).getHorasAlDia() + " horas al día");
			// Salario
			textView = (TextView) convertView.findViewById(R.id.textViewSalario);
			textView.setText("Sueldo de " + ((HijoExpandableListFichaDatosLaborales) datos).getSalario() + " € al mes");
			// Tipo de Contrato
			textView = (TextView) convertView.findViewById(R.id.textViewTipoContrato);
			textView.setText("Contrato " + ((HijoExpandableListFichaDatosLaborales) datos).getTipoContrato());
			// Finalización de contrato
			textView = (TextView) convertView.findViewById(R.id.textViewFinalizacionContrato);
			textView.setText("Vencimiento de ontrato el " + ((HijoExpandableListFichaDatosLaborales) datos).getFinalizacionContrato());
			
		// Otros datos
		}else if(datos instanceof HijoExpandableListFichaOtrosDatos){
			convertView = inflater.inflate(R.layout.hijo_lista_ficha_otros_datos, null);

			// Otros datos
			textView = (TextView) convertView.findViewById(R.id.textViewOtrosDatos);
			textView.setText(((HijoExpandableListFichaOtrosDatos) datos).getOtrosDatos());
		}
		
		return convertView;
	}
	
	/**
	 * Número de hijos dentro de un padre.
	 * @param groupPosition
	 * @return
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	/**
	 * Devuelve un padre pada una posicion.
	 * @param groupPosition
	 * @return
	 */	
	@Override
	public Object getGroup(int groupPosition) {
		return datosFicha.get(groupPosition);
	}

	/**
	 * Número de padres en una lista.
	 * @return
	 */
	@Override
	public int getGroupCount() {
		return datosFicha.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	/**
	 * La llamada a este método se produce cada vez que se muestra la pantalla de la lista.
	 * Configura la vista de cada uno de los padres de la lista.
	 * @param groupPosition
	 * @param isExpanded
	 * @param convertView
	 * @param parent
	 * @return
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.padre_lista_expandible_ficha, parent, false);
        }
		
		TextView textViewTipoDatos = (TextView) convertView.findViewById(R.id.textViewPadreExpandableListFicha);
		textViewTipoDatos.setText(datosFicha.get(groupPosition).getTipoDato());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}

