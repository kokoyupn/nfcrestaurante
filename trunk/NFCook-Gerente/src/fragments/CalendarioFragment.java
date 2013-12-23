package fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import baseDatos.HandlerGenerico;

import com.example.nfcook_gerente.R;


/**
 * @author Alejandro Moran
 * 
 * Esta clase contiene toda la funcionalidad del fragment del Calendario.
 * Donde se podra ver los dias trabajados de un empleado, asi como las horas de cada dia.
 * 
 * Como el Widget de Calendario de Android no deja modificar las celdas de tipo Dia,
 * creamos un calendario manualmente. 
 * Utilizando gridView para los dias y botones para avanzar o volver un mes.
 * El adapter del gridView para los dias esta dentro de esta clase.
 * 
 * Carga la info de las bases de datos: Empleados.db y Empleado.db.
 * 
 * */
public class CalendarioFragment extends Fragment {

	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private static final String dateTemplate = "MMMM yyyy";
	
	private HandlerGenerico sqlEmpleados,sqlEmpleado;
	private SQLiteDatabase dbEmpleados,dbEmpleado;
	private String idEmpleado = "1";
	
	private View vista;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    vista = inflater.inflate(R.layout.calendar_view_layout, container, false);
	    // TODO recibir el idEmpleado por bundleInfo
//	    Bundle bundleInfo = getActivity().getIntent().getExtras();
//	    idEmpleado = bundleInfo.getString(key)
		
		importarBaseDatos();
		
		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);

		prevMonth = (ImageView) vista.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
	        	onClickPrevMonth(); 	        	
	        }
	        }
		);
		currentMonth = (Button) vista.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

		nextMonth = (ImageView) vista.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
	        	onClickNextMonth(); 	        	
	        }
	        }
		);

		calendarView = (GridView) vista.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getActivity().getApplicationContext(), R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	    return vista;
	}
	
	 
	private void setGridCellAdapterToDate(int month, int year)
		{
			adapter = new GridCellAdapter(getActivity().getApplicationContext(), R.id.calendar_day_gridcell, month, year);
			_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}

 
	public void onClickPrevMonth() {
		if (month <= 1)
			{
				month = 12;
				year--;
			}
		else
			{
				month--;
			}
		setGridCellAdapterToDate(month, year);
	}
	
	
	public void onClickNextMonth() {
		if (month > 11)
			{
				month = 1;
				year++;
			}
		else
			{
				month++;
			}
		setGridCellAdapterToDate(month, year);
	}
	

	@Override
	public void onDestroy()
		{
			super.onDestroy();
		}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener
		{
		
			private final Context _context;
			private final List<String> list;
			private static final int DAY_OFFSET = 1;
			private final String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Augosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
			private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
			@SuppressWarnings("unused")
			private final int month, year;
			private int daysInMonth;
			private int currentDayOfMonth, currentMonthOfYear;
			private int currentWeekDay;
			private Button gridcell;
			private TextView num_events_per_day;
			@SuppressWarnings("rawtypes")
			private final HashMap eventsPerMonthMap;

			// Days in Current Month
			public GridCellAdapter(Context context, int textViewResourceId, int month, int year)
				{
					super();
					this._context = context;
					this.list = new ArrayList<String>();
					this.month = month;
					this.year = year;

					Calendar calendar = Calendar.getInstance();
					setCurrentMonthOfYear(calendar.get(Calendar.MONTH));
					setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
					setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
					
					// Print Month
					printMonth(month, year);

					// Find Number of Events
					eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
				}
			private String getMonthAsString(int i)
				{
					return months[i];
				}

			private int getNumberOfDaysOfMonth(int i)
				{
					return daysOfMonth[i];
				}

			@Override
			public String getItem(int position)
				{
					return list.get(position);
				}

			@Override
			public int getCount()
				{
					return list.size();
				}

			/**
			 * Prints Month
			 * 
			 * @param mm
			 * @param yy
			 */
			@SuppressWarnings("unused")
			private void printMonth(int mm, int yy)
				{
					// The number of days to leave blank at
					// the start of this month.
					int trailingSpaces = 0;
					int daysInPrevMonth = 0;
					int prevMonth = 0;
					int prevYear = 0;
					int nextMonth = 0;
					int nextYear = 0;

					int currentMonth = mm - 1;
					String currentMonthName = getMonthAsString(currentMonth);
					daysInMonth = getNumberOfDaysOfMonth(currentMonth);


					// Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
					GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

					if (currentMonth == 11)
						{
							prevMonth = currentMonth - 1;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
							nextMonth = 0;
							prevYear = yy;
							nextYear = yy + 1;
						}
					else if (currentMonth == 0)
						{
							prevMonth = 11;
							prevYear = yy - 1;
							nextYear = yy;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
							nextMonth = 1;
						}
					else
						{
							prevMonth = currentMonth - 1;
							nextMonth = currentMonth + 1;
							nextYear = yy;
							prevYear = yy;
							daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
						}

					// Compute how much to leave before before the first day of the
					// month.
					// getDay() returns 0 for Sunday.
					int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
					
					// Sumamos 6 en caso de que sea Domingo, porque cal.get(Calendar.DAY_OF_WEED) devuelve 0 en caso de que sea Domingo, 
					// ya que nuestro calendario es diferente. Restamos uno para que quede en orden: Mon=0, Tue=1, Wed=2, ..., Sun=6
					if (currentWeekDay == 0)
						currentWeekDay += 6;
					else
						currentWeekDay --;
					
					trailingSpaces = currentWeekDay;

					int horasPorContrato = getHorasContrato();
					
					if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1)
						{
							++daysInMonth;
						}

					// Trailing Month days
					for (int i = 0; i < trailingSpaces; i++)
						{
							list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
						}

					// Current Month Days
					for (int i = 1; i <= daysInMonth; i++)
						{
							if (i == getCurrentDayOfMonth() && (currentMonth == getCurrentMonthOfYear())){	// Dia actual del mes, cambiamos WHITE por BLUE si ese dia no ha trabajado
								if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("GREEN")) {
									list.add(String.valueOf(i) + "-GREEN" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								
								}else if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("RED")){
									list.add(String.valueOf(i) + "-RED" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								
								}else if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("WHITE")) {
									list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								}
							} else {
								if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("GREEN")) {
									list.add(String.valueOf(i) + "-GREEN" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								
								}else if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("RED")){
									list.add(String.valueOf(i) + "-RED" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								
								}else if (calendarFunctionality(horasPorContrato, i, currentMonth + 1, yy).equals("WHITE")) {
									list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
								}
							}
					}
					// Leading Month days
					for (int i = 1; i < list.size() % 7; i++) {
							if (calendarFunctionality(horasPorContrato, i, nextMonth + 1, yy).equals("GREEN")) {
								list.add(String.valueOf(i) + "-GREEN" + "-" + getMonthAsString(nextMonth) + "-" + yy);
							
							} else if (calendarFunctionality(horasPorContrato, i, nextMonth + 1, yy).equals("RED")){
								list.add(String.valueOf(i) + "-RED" + "-" + getMonthAsString(nextMonth) + "-" + yy);
							
							} else if (calendarFunctionality(horasPorContrato, i, nextMonth + 1, yy).equals("WHITE")) {
								list.add(String.valueOf(i) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + yy);
							}
					}
				}

			/**
			 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
			 * ALL entries from a SQLite database for that month. Iterate over the
			 * List of All entries, and get the dateCreated, which is converted into
			 * day.
			 * 
			 * @param year
			 * @param month
			 * @return
			 */
			@SuppressWarnings("rawtypes")
			private HashMap findNumberOfEventsPerMonth(int year, int month)
				{
					HashMap map = new HashMap<String, Integer>();
					// DateFormat dateFormatter2 = new DateFormat();
					//						
					// String day = dateFormatter2.format("dd", dateCreated).toString();
					//
					// if (map.containsKey(day))
					// {
					// Integer val = (Integer) map.get(day) + 1;
					// map.put(day, val);
					// }
					// else
					// {
					// map.put(day, 1);
					// }
					return map;
				}

			@Override
			public long getItemId(int position)
				{
					return position;
				}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
				{
					View row = convertView;
					if (row == null)
						{
							LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
						}

					// Get a reference to the Day gridcell
					gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
					gridcell.setOnClickListener(this);

					// ACCOUNT FOR SPACING

					String[] day_color = list.get(position).split("-");
					String theday = day_color[0];
					String themonth = day_color[2];
					String theyear = day_color[3];
					if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null))
						{
							if (eventsPerMonthMap.containsKey(theday))
								{
									num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
									Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
									num_events_per_day.setText(numEvents.toString());
								}
						}

					// Set the Day GridCell
					gridcell.setText(theday);
					if (Integer.parseInt(theday) < 10) {
						theday = "0" + theday;
					}
					gridcell.setTag(theday + "/" + getNumberOfMonth(themonth) + "/" + theyear);

					// Segun el atributo que tenga el dia, asignamos un stilo a su gridCell
					if (day_color[1].equals("GREY"))
						{
							gridcell.setTextColor(Color.LTGRAY);
						}
					if (day_color[1].equals("WHITE"))
						{
							gridcell.setTextColor(Color.rgb(00, 33, 66));	// #003366 es el azul marino para los dias
						}
					if (day_color[1].equals("BLUE"))
						{
							gridcell.setTextColor(Color.rgb(00, 33, 66));
							gridcell.setTextSize(26);
						}
					if (day_color[1].equals("GREEN"))
						{
							//gridcell.setBackgroundResource(drawable.calendar_tile_small_green);
							gridcell.setTextColor(Color.rgb(82, 136, 41)); //(43, 167, 58)); //(45, 191, 63));
						}
					if (day_color[1].equals("RED"))
						{
							//gridcell.setBackgroundResource(drawable.calendar_tile_small_redline);
							gridcell.setTextColor(Color.RED);
						}
					
					return row;
				}

			
			public int getNumberOfMonth(String month){
				if (month.equals("Enero")){
					return 1;
				}else if (month.equals("Febrero")){
					return 2;
				}else if (month.equals("Marzo")){
					return 3;
				}else if (month.equals("Abril")){
					return 4;
				}else if (month.equals("Mayo")){
					return 5;
				}else if (month.equals("Junio")){
					return 6;
				}else if (month.equals("Julio")){
					return 7;
				}else if (month.equals("Augosto")){
					return 8;
				}else if (month.equals("Septiembre")){
					return 9;
				}else if (month.equals("Octubre")){
					return 10;
				}else if (month.equals("Noviembre")){
					return 11;
				}else if (month.equals("Diciembre")){
					return 12;
				}else
					return 0;
				
			}
			@Override
			public void onClick(View view)
				{
					String date_month_year = (String) view.getTag();

					try {
							String[] campos1 = new String[]{"IdEmpleado", "Entrada", "Salida", "EntradaParada", "SalidaParada"};
			     		    String[] datos1 = new String[]{idEmpleado, date_month_year};
			    			Cursor cu = dbEmpleado.query("Empleado",campos1, "IdEmpleado=? AND Fecha=?",datos1, null, null,null);

			    			if(cu.moveToNext() != false){
				    			
				    			SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
			    				Date entrada = formatoFecha.parse(cu.getString(1));
			    				Date salida = formatoFecha.parse(cu.getString(2));
			    				Date entradaParada = formatoFecha.parse(cu.getString(3));
			    				Date salidaParada = formatoFecha.parse(cu.getString(4));
			
			    				long diferencia = (salida.getTime() - entrada.getTime()) - (salidaParada.getTime() - entradaParada.getTime()); 
			    				int diasReales = (int) (diferencia / (1000*60*60*24));  
			    				int horasReales = (int) ((diferencia - (1000*60*60*24*diasReales)) / (1000*60*60)); 

			    				int horasContrato = getHorasContrato();
			    			
			    		    	Toast.makeText(getActivity().getApplicationContext(),"Horas trabajadas: " + horasReales + " de " + horasContrato + " de contrato", Toast.LENGTH_LONG).show();	

			    			} else {
			    		    	Toast.makeText(getActivity().getApplicationContext(),"No ha trabajado este dia", Toast.LENGTH_SHORT).show();	

			    			}
					} catch (ParseException e) {
							e.printStackTrace();
			            	System.out.print("Error al mostrar las horas despues de hacer click en un dia");
					}
				}
			
			public void setCurrentMonthOfYear(int currentMonthOfYear){
				this.currentMonthOfYear = currentMonthOfYear;
			}
			
			public int getCurrentMonthOfYear(){
				return currentMonthOfYear;
			}
			
			public int getCurrentDayOfMonth()
				{
					return currentDayOfMonth;
				}

			private void setCurrentDayOfMonth(int currentDayOfMonth)
				{
					this.currentDayOfMonth = currentDayOfMonth;
				}
			public void setCurrentWeekDay(int currentWeekDay)
				{
					this.currentWeekDay = currentWeekDay;
				}
			public int getCurrentWeekDay()
				{
					return currentWeekDay;
				}
			
			public int getHorasContrato(){
				try {
					// Horas reales del empleado por contrato
	            	String[] campos = new String[]{"HorasContrato"};
	     		    String[] datos = new String[]{idEmpleado};
	    			Cursor c = dbEmpleados.query("Empleados",campos, "idEmpleado=?",datos, null, null,null);
	    		    c.moveToNext();
	    		    int horasContrato = Integer.parseInt(c.getString(0));
	    		    return horasContrato;
	    		    
				}catch(Exception e){
	            	System.out.print("Error en obtener las horas por contrato del empleado");
					return 0;
				}
			}

			public String calendarFunctionality(int horasContrato, int dayOfMonth, int actualMonth, int actualYear){
				try {
	    		    // Horas que trabaja el empleado en realidad
	    		    String dia = null;
	    		    String mes = null;
	    		    
	    		    if(dayOfMonth < 10){
	    		    	dia = "0" + Integer.toString(dayOfMonth); 
	    		    }else{
	    		    	dia = Integer.toString(dayOfMonth);
	    		    }
	    		    
	    		    if(actualMonth < 10){
	    		    	mes = "0" + Integer.toString(actualMonth);    		    	
	    		    }else{
	    		    	mes = Integer.toString(actualMonth);
	    		    }
	    		    
	    		    String fecha = dia + "/" + mes + "/" + actualYear;
	    		    String[] campos1 = new String[]{"IdEmpleado", "Entrada", "Salida", "EntradaParada", "SalidaParada"};
	     		    String[] datos1 = new String[]{idEmpleado, fecha};
	    			Cursor cu = dbEmpleado.query("Empleado",campos1, "IdEmpleado=? AND Fecha=?",datos1, null, null,null);
	    			
	    			if(cu.moveToNext() != false){
		    			
		    			SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
	    				Date entrada = formatoFecha.parse(cu.getString(1));
	    				Date salida = formatoFecha.parse(cu.getString(2));
	    				Date entradaParada = formatoFecha.parse(cu.getString(3));
	    				Date salidaParada = formatoFecha.parse(cu.getString(4));
	
	    				long diferencia = (salida.getTime() - entrada.getTime()) - (salidaParada.getTime() - entradaParada.getTime()); 
	    				int diasReales = (int) (diferencia / (1000*60*60*24));  
	    				int horasReales = (int) ((diferencia - (1000*60*60*24*diasReales)) / (1000*60*60)); 

	    				if(horasReales >= horasContrato){
	    					return "GREEN";
	    					 
	    				}else {
	    					return "RED";
	    				}
		    		    
	    			}else {
			    		 return "WHITE";  
	    			}
			
			}
			catch(Exception e){
            	System.out.print("Error en la resta de horas del calendario");
            	return "WHITE";
			}
			
		}

	}

	public void importarBaseDatos(){
		 try{
				sqlEmpleados = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleados.db");
				dbEmpleados = sqlEmpleados.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH EMPLEADOS");
		 }
		 try{
				sqlEmpleado = new HandlerGenerico(getActivity().getApplicationContext(), "/data/data/com.example.nfcook_gerente/databases/", "Empleado.db");
				dbEmpleado = sqlEmpleado.open();
		 }catch(SQLiteException e){
				System.out.println("CATCH EMPLEADO");
		 }
	}

}
