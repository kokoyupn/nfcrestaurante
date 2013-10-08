package nfcook.mensajes;

public class MensajeAviso implements Mensaje{
	
	private static final long serialVersionUID = 1L;
	
	private Tipo aviso;
	private String idMesa;
	
	public MensajeAviso(Tipo aviso, String idMesa) {
		this.aviso = aviso;
		this.idMesa = idMesa;
	}

	public Tipo getAviso() {
		return aviso;
	}

	public String getIdMesa() {
		return idMesa;
	}
	
}
