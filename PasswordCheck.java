import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PasswordCheck {
    public static void main(String[] args) throws Exception {
        String url  = "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:6543/postgres";
        String user = "postgres.qlkngifzengppsjxegmh";
        String pass = "Akash!314325";

        Connection conn = DriverManager.getConnection(url, user, pass);
        Statement stmt  = conn.createStatement();

        // Fetch the stored hashes for the key users
        ResultSet rs = stmt.executeQuery(
            "SELECT name, emp_id, reg_no, role, password FROM users " +
            "WHERE emp_id IN ('HOD001','CC001','STF001') OR reg_no = '814423104001' " +
            "ORDER BY emp_id NULLS LAST"
        );

        System.out.println("=== Stored Password Hashes ===");
        while (rs.next()) {
            System.out.printf("Name: %-15s | ID: %-15s | RegNo: %-15s | Role: %-8s%n" +
                              "  Hash: %s%n%n",
                rs.getString("name"),
                rs.getString("emp_id"),
                rs.getString("reg_no"),
                rs.getString("role"),
                rs.getString("password")
            );
        }
        conn.close();
    }
}
