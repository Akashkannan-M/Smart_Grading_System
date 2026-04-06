import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptVerify {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Passwords defined in DataInitializer.java
        String[][] tests = {
            // { label, plainPassword, storedHash }
            { "HOD001  (01011980@hod)",     "01011980@hod",     "$2a$10$vvvn716ObyhWpG3zMY3NYuazabI6SnlhE1dRdbIGcDci7eKEntEXa" },
            { "CC001   (01011985@cc)",      "01011985@cc",      "$2a$10$Hq/l.bfcTbOk2nMpLtJ6ae0kPVPe5OotSh6me43eCP2jCxbnRUwCi" },
            { "STF001  (01011990@staff)",   "01011990@staff",   "$2a$10$ZGJfeZB8K7stdYwHsg/wpemc5V5Qk/Q9MN6kwgtBtvMIkriVeiqHK" },
            { "Student1(01012003@student)", "01012003@student", "$2a$10$S4D1GbNL5MJ2zVhgU0Sg5ePU1yPyxMJWc/wgGU8ihCrnxb9te4ZGy" },
        };

        System.out.println("=== BCrypt Verification Results ===\n");
        for (String[] t : tests) {
            boolean matches = encoder.matches(t[1], t[2]);
            System.out.printf("%-35s  password='%-22s'  match=%s%n", t[0], t[1], matches ? "✅ YES" : "❌ NO");
        }
    }
}
