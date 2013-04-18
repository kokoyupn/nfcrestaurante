package tpv;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FechaYHora{
	
	private int dia;
	private int mes;
	private int annio;
	
	private int hora;
	private int minuto;
	private int segundo;
	
	
	public FechaYHora(){

		Calendar calendario = new GregorianCalendar();
		hora =calendario.get(Calendar.HOUR_OF_DAY);
		minuto = calendario.get(Calendar.MINUTE);
		segundo = calendario.get(Calendar.SECOND);
		
		dia = calendario.get(Calendar.DATE);
		mes = calendario.get(Calendar.MONTH)+1;
		annio = calendario.get(Calendar.YEAR);
		
		
	}
	
	public String toString(){
		return dia + "/" + mes + "/" + annio + "--"+ hora + ":" + minuto + ":" + segundo;
	}
	
	public String getDia(){
		return  dia + "/" + mes + "/" + annio;
	}
	
	public String getHora(){
		return  hora + ":" + minuto + ":" + segundo;
	}

}
