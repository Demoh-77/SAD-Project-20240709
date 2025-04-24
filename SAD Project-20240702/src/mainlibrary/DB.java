/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author bikash
 */
public class DB {
//Removed hardcoded Username and Password.
    public static String connection = "jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false";
    
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Implemented logic to load Username and Password from launch.json file instead.
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            String dbUrl = System.getenv("DB_URL");


            String finalDbUrl = (dbUrl != null && !dbUrl.isEmpty()) ? dbUrl : connection;

            Properties props = new Properties();
            props.put("user", dbUser); // Use username from launch.json.
            props.put("password", dbPassword); // Use password from launch.json.
            props.put("useUnicode", "true");
            props.put("useServerPrepStmts", "false");
            props.put("characterEncoding", "UTF-8");

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(finalDbUrl, props);

        // Added proper error logging.
       } catch (ClassNotFoundException e) {
           System.out.println(e);
       } catch (SQLException e) {
           System.out.println(e);
       } catch (Exception e) {
           System.out.println(e);
       }
       return con;
   }
}
