package nfcook.mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Asunto asunto;
	
	protected Mensaje(Asunto asunto){
		this.asunto = asunto;
	}
	
}
