package example.sql;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Starting");
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:55000/postgres",
                "postgres",
                "postgrespw"
        );

        double total = 0;
        System.out.println(total);
        for (int i = 0; i < 50; i++) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select random() as one from generate_series(1,500000);");

            while (rs.next()) {
                double j = rs.getDouble(1);
                total += j;
            }
        }
    }
}
