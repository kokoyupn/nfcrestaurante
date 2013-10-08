package nfcook.mensajes;

public class MensajeFichero extends Mensaje{
	
	private String nombre;
	private String ruta;
	
	public MensajeFichero(String nombre, String ruta) {
		super(Asunto.FICHERO);
		this.nombre = nombre;
		this.ruta = ruta;
	}

	public String getNombre() {
		return nombre;
	}

	public String getRuta() {
		return ruta;
	}

}
