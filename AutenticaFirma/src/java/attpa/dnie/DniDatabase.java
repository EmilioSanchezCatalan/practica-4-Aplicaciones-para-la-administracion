package attpa.dnie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DniDatabase {

    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/dniauth";
    Connection conn;

    public DniDatabase() {
        cargarDriver();
    }

    private void cargarDriver() {
        try {
            Class.forName(driver).newInstance();
	} catch (Exception e) {
            System.out.println(e.getMessage());
	}
    }
    public void getConnection() {
        try {
            conn = DriverManager.getConnection(url, "root", "");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
	}
    }
    public String cogerClave(String dni){
        String sql = "select password from users where dni = ?;";
        ResultSet rs = null;
        String clave = null;
        getConnection();
        try{
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dni);
            rs = stmt.executeQuery();
            while(rs.next()){
                clave = rs.getString(1);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return clave;
    }

}
