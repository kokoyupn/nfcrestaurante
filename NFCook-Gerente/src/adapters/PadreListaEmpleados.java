package adapters;



public class PadreListaEmpleados {
	
	private String foto;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String dni;
	private String puesto;
	private String idEmpleado;
	
	
	
	
	
	public PadreListaEmpleados(String foto,String nombre, String apellido1 , String apellido2, String puesto,String idEmpleado){
		this.foto = foto;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.puesto = puesto;
		this.idEmpleado = idEmpleado;
		
	}


	public String getFoto() {
		return foto;
	}


	public void setFoto(String foto) {
		this.foto = foto;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getApellido1() {
		return apellido1;
	}


	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}


	public String getApellido2() {
		return apellido2;
	}


	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}


	public String getDni() {
		return dni;
	}


	public void setDni(String dni) {
		this.dni = dni;
	}


	public String getPuesto() {
		return puesto;
	}
	
	public void setPuesto(String puesto){
		this.puesto = puesto;
	}


	public String getIdEmpleado() {
		return idEmpleado;
	}

}
