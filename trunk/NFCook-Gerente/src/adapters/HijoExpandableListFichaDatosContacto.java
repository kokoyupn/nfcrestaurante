package adapters;

public class HijoExpandableListFichaDatosContacto extends HijoExpandableListFicha{
	
	private String direccion;
	private String cp;
	private String municipioCiudad;
	private String pais;
	private String telefono;
	private String mail;
	
	public HijoExpandableListFichaDatosContacto(String direccion, String cp, String municipioCiudad, String pais, String telefono, String mail,
			String nacionalidad) {
//		this.direccion = direccion;
//		this.cp = cp;
//		this.municipioCiudad = municipioCiudad;
//		this.pais = pais;
//		this.telefono = telefono;
//		this.mail = mail;
//		this.nacionalidad = nacionalidad;
		
		this.direccion = "C/ General Pardiñas Nº70, 6ºB drcha";
		this.cp = "28006";
		this.municipioCiudad = "Madrid, Madrid";
		this.pais = "España";
		this.telefono = "619892842";
		this.mail = "abelino_091@hotmail.com";
	}

	public String getDireccion() {
		return direccion;
	}

	public String getCp() {
		return cp;
	}

	public String getMunicipioCiudad() {
		return municipioCiudad;
	}

	public String getPais() {
		return pais;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getMail() {
		return mail;
	}

}
