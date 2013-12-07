package adapters;

public class HijoExpandableListFichaDatosLaborales extends HijoExpandableListFicha{
	
	private String puesto;
	private int horasAlDia;
	private float salario;
	private String tipoContrato; 
	private String finalizacionContrato;
	
	public HijoExpandableListFichaDatosLaborales(String puesto, int horasAlDia, float salario, String tipoContrato,
			String finalizacionContrato) {
//		this.puesto = puesto;
//		this.horasAlDia = horasAlDia;
//		this.salario = salario;
//		this.tipoContrato = tipoContrato;
//		this.finalizacionContrato = finalizacionContrato;
		
		this.puesto = "Gerente";
		this.horasAlDia = 8;
		this.salario = 2300;
		this.tipoContrato = "Fijo";
		this.finalizacionContrato = "22/09/2015";
	}

	public String getPuesto() {
		return puesto;
	}

	public int getHorasAlDia() {
		return horasAlDia;
	}

	public float getSalario() {
		return salario;
	}

	public String getTipoContrato() {
		return tipoContrato;
	}

	public String getFinalizacionContrato() {
		return finalizacionContrato;
	}

}
