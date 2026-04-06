
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class db_check {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:6543/postgres", "postgres.qlkngifzengppsjxegmh", "Akash!314325");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, emp_id, reg_no, role FROM users");
            while (rs.next()) {
                System.out.println("User: " + rs.getString("name") + ", EmpID: " + rs.getString("emp_id") + ", RegNo: " + rs.getString("reg_no") + ", Role: " + rs.getString("role"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
