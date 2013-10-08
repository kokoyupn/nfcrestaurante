package nfcook.mensajes;

public abstract class Mensaje {
	
	protected Asunto asunto;
	
	protected Mensaje(Asunto asunto){
		this.asunto = asunto;
	}
	
}
