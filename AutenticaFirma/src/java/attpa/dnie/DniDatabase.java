package attpa.dnie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DniDatabase {

    //declaracion de variables
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/dniauth";
    Connection conn;
    
    /**
     * Constructor de la clase
     */
    public DniDatabase() {
        cargarDriver();
    }
    
    /**
     * Carga los drivers de acceso a BBDD de la libreria Mysql
     */
    private void cargarDriver() {
        try {
            Class.forName(driver).newInstance();
	} catch (Exception e) {
            System.out.println(e.getMessage());
	}
    }
    
    /**
     * Crea una conexion con la base de datos indicada.
     */
    public void getConnection() {
        try {
            conn = DriverManager.getConnection(url, "root", "");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
	}
    }
    /**
     * 
     * @param dni dni del usuario que queremos su clave de sesion.
     * @return clave de sesion del usuario deseado.
     */
    public String cogerClave(String dni){
        
        //sentencia sql
        String sql = "select password from users where dni = ?;";
        
        //declaracion de conexion
        ResultSet rs = null;
        String clave = null;
        
        //creamos la conexion.
        getConnection();
        
        try{
            //preparamos la sentencia sql antes de ejecutarla
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dni);
            
            //ejecutamos y guardamos el resultado de la sentencia sql
            rs = stmt.executeQuery();
            
            while(rs.next()){
                //recibimos la clave
                clave = rs.getString(1);
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return clave;
    }

}
