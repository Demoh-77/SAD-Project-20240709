package mainlibrary;

import java.sql.*;


public class LibrarianDao {

    public static int save(String name, String password, String email, String address, String city, String contact) {
        int status = 0;
        try {

            Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement("insert into librarian(name,password,email,address,city,contact) values(?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setString(5, city);
            ps.setString(6, contact);
            status = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }

    public static int delete(int id) {
        int status = 0;
        try {
            Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement("delete from Librarian where id=?");
            ps.setInt(1, id);
            status = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return status;
    }

    public static boolean validate(String name, String password) {
        boolean status = false;
        // Added a Prepared Statement "PS" with placeholders to prevent SQL Injection vulnerability.
        String select = "select Password from Librarian where UserName = ?";
        try (Connection con = DB.getConnection();
            PreparedStatement PS = con.prepareStatement(select))
        {
            PS.setString(1, name);
            
            try (ResultSet rs = PS.executeQuery())
            {
                if (rs.next())
                {
                String storedHash = rs.getString("Password");
                // Uses PasswordZone to check password against stored hashed ones.
                if (PasswordZone.checkPassword(password, storedHash))
                {
                    status = true; // Password matches
                }
                }
            }
         
        // Added proper error logging when validation fails
        } catch (Exception e) {
            System.out.println(e);
        }                  
       
        return status;
    }
}
