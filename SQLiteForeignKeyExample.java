import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteForeignKeyExample {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:test.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Foreign Keys standardmäßig prüfen aus (meistens so bei SQLite)
            System.out.println("Pragma foreign_keys before enabling:");
            var rs1 = stmt.executeQuery("PRAGMA foreign_keys;");
            System.out.println(rs1.getInt(1));  // 0 = aus, 1 = an

            // Foreign Key Constraints aktivieren
            stmt.execute("PRAGMA foreign_keys = ON;");

            System.out.println("Pragma foreign_keys after enabling:");
            var rs2 = stmt.executeQuery("PRAGMA foreign_keys;");
            System.out.println(rs2.getInt(1));  // sollte 1 sein

            // Beispiel Tabellen mit Foreign Key Constraint anlegen
            stmt.execute("DROP TABLE IF EXISTS child;");
            stmt.execute("DROP TABLE IF EXISTS parent;");

            stmt.execute("CREATE TABLE parent (id INTEGER PRIMARY KEY);");
            stmt.execute("CREATE TABLE child (id INTEGER PRIMARY KEY, parent_id INTEGER, " +
                         "FOREIGN KEY (parent_id) REFERENCES parent(id));");
            
            // Einfügen in parent
            stmt.execute("INSERT INTO parent (id) VALUES (1);");

            // Korrektes Einfügen in child - Referenz auf parent ID 1
            stmt.execute("INSERT INTO child (id, parent_id) VALUES (1, 1);");

            // Falsches Einfügen - Referenz auf nicht vorhandene parent ID 2
            stmt.execute("INSERT INTO child (id, parent_id) VALUES (2, 2);");

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
}

