package auxiliares;

public class ObjetoAuxGrafica implements Comparable<ObjetoAuxGrafica>{
	
	
	private String tiempo;
	private double importe;
	
	public ObjetoAuxGrafica(String tiempo, String importe) {
		this.tiempo = tiempo;
		this.importe = Double.parseDouble(importe);
	}
	
	public void sumaImporte(String cantidad){
		importe = importe + Double.parseDouble(cantidad);
	}
	
	public String getTiempo(){
		return tiempo;
	}

	public Number getImporte() {
		return importe;
	}

	@Override
	public int compareTo(ObjetoAuxGrafica otro) {
		return tiempo.compareTo(otro.tiempo);
				
	}

}
