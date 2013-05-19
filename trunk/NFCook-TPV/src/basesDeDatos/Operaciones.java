/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package basesDeDatos;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import sockets.ClienteFichero;
import tpv.FechaYHora;

public class Operaciones extends Conexion{
	private String nombreDB;
	
	/**
     * Constructor for objects of class Operaciones
     * @param string 
     */
    public Operaciones(String nombreDB)
    {
        super(nombreDB);
        this.nombreDB = nombreDB;
    }
    
    public boolean insertar(String sql, boolean socket){
        boolean valor = true;
        if (socket) // enviamos la consulta de insercion por socket 
        	enviarConsultaSocket(sql);
        conectar();
        try {
            consulta.executeUpdate(sql);
        } catch (SQLException e) {
                valor = false;
                JOptionPane.showMessageDialog(null, e.getMessage());
            }      
        finally{  
            try{  
                 consulta.close();  
                 conexion.close();  
             }catch (Exception e){                 
                 e.printStackTrace();  
             }  
        }
        return valor;
    }
    public ResultSet consultar(String sql){
    	conectar();
        ResultSet resultado = null;
        try {
            resultado = consulta.executeQuery(sql);

        } catch (SQLException e) {
                System.out.println("Mensaje:"+e.getMessage());
                System.out.println("Estado:"+e.getSQLState());
                System.out.println("Codigo del error:"+e.getErrorCode());
                JOptionPane.showMessageDialog(null, ""+e.getMessage());
            }
        return resultado;
    }
    
    public void cerrarBaseDeDatos(){
    	try{
    		consulta.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void enviarConsultaSocket(String sql){
        ClienteFichero.enviaConsulta(nombreDB, sql);
    }
    
    
    public void ficharEntrar(String idCamarero, FechaYHora hora){
    	insertar("insert into Ficha values('"+idCamarero
                +"','"+ hora.getDia()
                +"','"+ hora.getHora()
                +"','"+ "-"
                +"','"+ "-"
                +"')", true);
    }
    
    public void ficharSalir(String idCamarero, FechaYHora hora){
		insertar("UPDATE Ficha SET horaSalida='"+
				hora.getHora() +
				"' where dia='" +
				hora.getDia() +
				"' and idCamarero='" +
				idCamarero + "'", true);
    }
    
    public void ficharParada(String idCamarero, FechaYHora hora){
		insertar("UPDATE Ficha SET horaParada='"+
				hora.getHora() +
				"' where dia='" +
				hora.getDia() +
				"' and idCamarero='" +
				idCamarero + "'", true);
    }



	public boolean camareroFichadoEntrar(String idCamarero, FechaYHora fechaYHora) {
    	try{
			ResultSet resultados = consultar("select dia from Ficha where dia='" + fechaYHora.getDia() + "' and idCamarero='" + idCamarero + "'");
	    	if(resultados.next()){
	    		return true;
	    	}else{
	    		return false;
	    	}
    	}catch(SQLException e){
    		e.printStackTrace();
    		return true;
    	}
	}



	public boolean camareroFichadoSalir(String idCamarero, FechaYHora fechaYHora) {
		try{
			ResultSet resultados = consultar("select horaSalida from Ficha where dia='" + fechaYHora.getDia() + "' and idCamarero='" + idCamarero + "'");
	    	if(resultados.next()){
	    		if(resultados.getString("horaSalida").equals("-")){
	    			return false;
	    		}else{
		    		return true;
	    		}
	    	}else{
	    		return false;
	    	}
    	}catch(SQLException e){
    		e.printStackTrace();
    		return true;
    	}
	}



	public boolean camareroFichadoParada(String idCamarero, FechaYHora fechaYHora) {
		try{
			ResultSet resultados = consultar("select horaParada from Ficha where dia='" + fechaYHora.getDia() + "' and idCamarero='" + idCamarero + "'");
	    	if(resultados.next()){
	    		if(resultados.getString("horaParada").equals("-")){
	    			return false;
	    		}else{
		    		return true;
	    		}
	    	}else{
	    		return false;
	    	}
    	}catch(SQLException e){
    		e.printStackTrace();
    		return true;
    	} 
	}
	
	
	
	
	public void actualizaFav(String idProd, int cantidadNueva){
		insertar("UPDATE Restaurantes SET CantidadPedido='"+
				cantidadNueva +
				"' where Id='" +
				idProd + "'" , true);
    }
	
	
	
/* 
 * 							-EJEMPLOS-
 * 
    public void guardarUsuario(Persona persona){
        insertar("insert into Persona values("+persona.getId()
                    +",'"+persona.getPrimer_nombre()
                    +"','"+persona.getSegundo_nombre()
                    +"','"+persona.getPrimer_apellido()
                    +"','"+persona.getSegundo_apellido()+"')");
    } 
  
    public void totalPersonas(DefaultTableModel tableModel){
        ResultSet resultado = null;
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String sql = "select * from Persona";
        try {
            resultado = consultar(sql);
            if(resultado != null){
                int numeroColumna = resultado.getMetaData().getColumnCount();
                for(int j = 1;j <= numeroColumna;j++){
                    tableModel.addColumn(resultado.getMetaData().getColumnName(j));
                }
                while(resultado.next()){
                    Object []objetos = new Object[numeroColumna];
                    for(int i = 1;i <= numeroColumna;i++){
                        objetos[i-1] = resultado.getObject(i);
                    }
                    tableModel.addRow(objetos);
                }
            }
        }catch(SQLException e){
        }

        finally
     {
         try
         {
             consulta.close();
             conexion.close();
             if(resultado != null){
                resultado.close();
             }
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
     }
    }
  */  
}