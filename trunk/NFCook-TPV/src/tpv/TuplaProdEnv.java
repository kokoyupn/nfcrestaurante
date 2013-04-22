package tpv;

public class TuplaProdEnv {

	private Producto prod;
	private boolean enviado;
	
	public TuplaProdEnv(Producto prod, boolean enviado) {
		this.prod = prod;
		this.enviado = enviado;
	}

	public Producto getProd() {
		return prod;
	}

	public void setProd(Producto prod) {
		this.prod = prod;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	
	
}
