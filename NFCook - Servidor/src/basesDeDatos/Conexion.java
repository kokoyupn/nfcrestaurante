/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package basesDeDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Write a description of class Conexion here.
 * 
 */
public class Conexion{
	Connection conexion;
	Statement consulta;
	public String ruta;

    /**
     * Constructor for objects of class Conexion
     */
    public Conexion(String nombreDB)
    {
        ruta = "BasesDeDatosTPV/" + nombreDB;
    }
    public void conectar(){
		try{
			Class.forName("org.sqlite.JDBC");
	    }catch (ClassNotFoundException e) {
	    	System.err.println("Error relacionado con la base de datos");
	    }
		try{
            conexion = DriverManager.getConnection("jdbc:sqlite:"+ruta);
            consulta = conexion.createStatement();
		} catch (SQLException e) {
	    	System.err.println("Error relacionado con la base de datos");
        }
	}
    
}
