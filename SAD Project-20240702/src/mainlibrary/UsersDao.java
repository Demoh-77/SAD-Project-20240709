/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainlibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author bikash
 */
public class UsersDao {

    public static boolean validate(String name, String password) {
        boolean status = false;
        // Added a Prepared Statement "PS" with placeholders to prevent SQL Injection vulnerability.
        String select = "select UserPass from Users where UserName=?";
        try (Connection con = DB.getConnection();
            PreparedStatement PS = con.prepareStatement(select))
        {
            PS.setString(1, name);
       
            try (ResultSet rs = PS.executeQuery())
            {
                if (rs.next())
                {
                String storedHash = rs.getString("UserPass");
                // Uses PasswordZone to check password against stored hash.
                if (PasswordZone.checkPassword(password, storedHash))
                {
                    status = true; // Password matches
                }
                }
            }
         
        // Added proper error logging when validation fails.
        } catch (Exception e) {
            System.out.println(e);
        }

        return status;
    }

    public static boolean CheckIfAlready(String UserName) {
        boolean status = false;
        // Added a Prepared Statement "PS" with placeholders to prevent SQL Injection vulnerability.
        String select = "select UserPass from Users where UserName = ?";
        try (Connection con = DB.getConnection();
            PreparedStatement PS = con.prepareStatement(select))
        {
            PS.setString(1, UserName);
       
            try (ResultSet rs = PS.executeQuery())
            {
                status = rs.next();
            }
         
        // Added proper error logging when confirmation fails.
        } catch (SQLException e) {
            System.out.println(e);
        }

        return status;
 
    }

    public static int AddUser(String User, String UserPass, String UserEmail, String Date) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
 
        int status = 0;
        // Added a Prepared Statement "PS" with placeholders to prevent SQL Injection vulnerability
        String select = "insert into Users(UserPass, RegDate, UserName, Email) values(?, ?, ?, ?)";
        try (Connection con = DB.getConnection();
            PreparedStatement PS = con.prepareStatement(select))
        {
            // Hashes Password before storing and store in "hashedPassword" string.
            String hashedPassword = PasswordZone.hashedPassword(UserPass);
 
            PS.setString(1, hashedPassword);
            PS.setString(2, Date);
            PS.setString(3, User);
            PS.setString(4, UserEmail);
            status = PS.executeUpdate();
         
        // Added proper error logging when adding user fails.
        } catch (SQLException e) {
            System.out.println(e);  
        }   
        return status;
 
    }
 

}
