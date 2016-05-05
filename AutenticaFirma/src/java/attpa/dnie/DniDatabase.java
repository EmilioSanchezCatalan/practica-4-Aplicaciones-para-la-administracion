package attpa.dnie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DniDatabase {

    protected String url = "jdbc:mysql://localhost:3306/";
    protected String dbName = "dniauth";
    protected String driver = "com.mysql.jdbc.Driver";

    public DniDatabase() {
    }
    
 
    public String connectToAndQueryDatabase(String username, String password) {

        String result = "";
        Connection conn = null;

        try {
            Class.forName(driver).newInstance();
        } catch (InstantiationException e1) {
            result = "Error: " + e1.getMessage();
            
        } catch (IllegalAccessException e1) {
            result = "Error: " + e1.getMessage();
           
        } catch (ClassNotFoundException e1) {
            result = "Error: " + e1.getMessage();
           
        }

        try {
            conn = DriverManager
                    .getConnection(url + dbName, username, password);
        } catch (SQLException e1) {
            result = "Error: " + e1.getMessage();
           
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

            while (rs.next()) {

                String name = rs.getString("user");
                String dni = rs.getString("dni");
                result = result + " " + name + " " + dni + "<br />";
                System.out.println(result);
            }
        } catch (SQLException e) {
            result = "Error: " + e.getMessage();
            
        } finally {

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
               
            }

        }

        return result;
    }

}
