package dk.g4.st25.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class Database {
    private final Connection connection;

    static private final Database db = new Database(); // singleton pattern

    private Database() {
        String url;
        String user;
        String password;

        // Environment variables
        Dotenv env;
        try { // in testing the working directory is swapped to this module
            env = Dotenv.load();
        } catch (Exception e) {
            env = Dotenv.configure().directory("../").load();
        }

        url = env.get("DB_URL");
        user = env.get("DB_USER");
        password = env.get("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new RuntimeException("Missing one or more required environment variables: DB_URL, DB_USER, DB_PASSWORD. Make sure .env is configured correctly.");
        }

        // Database connection
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connecting to database", e); // db will not work without a connection
        }
        initialize();
    }

    private void initialize() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    Drop TABLE IF EXISTS storage;
           
                    CREATE TABLE storage (
                        id SERIAL PRIMARY KEY,
                        item varchar UNIQUE NOT NULL,
                        amount int NOT NULL CHECK ( amount >= 0 )
                    );
           """);
            System.out.println("Storage table created.");
        } catch (SQLException e) {
            System.err.println("Error creating storage table: " + e.getMessage());
        }

        // Insert all items from DronePart enum
        int defaultAmount = 100;
        System.out.println("Inserting default items:");
        for (DronePart item : DronePart.values()) insertItem(item.getItemName(), defaultAmount);
    }

    private void insertItem(String itemName, int amount) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO storage (item, amount) VALUES (?, ?)")) {

            preparedStatement.setString(1, itemName);
            preparedStatement.setInt(2, amount);
            preparedStatement.execute();

            System.out.printf("new row - Item: %s | Amount: %s%n", itemName, amount);

        } catch (SQLException e) {
            System.err.println("Error inserting new item into storage: " + e.getMessage());
        }
    }

    public void addAmount(DronePart part, int amount) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE storage SET amount = amount+? WHERE item = ?")) {

            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, part.getItemName());
            preparedStatement.execute();

            System.out.printf("%s-amount += %d%n", part.getItemName(), amount);
        } catch (SQLException e) {
            System.err.println("Error changing amount: " + e.getMessage());
        }
    }

    public void setAmount(DronePart part, int amount) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE storage SET amount = ? WHERE item = ?")) {

            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, part.getItemName());
            preparedStatement.execute();

            System.out.printf("%s-amount = %d%n", part.getItemName(), amount);
        } catch (SQLException e) {
            System.err.println("Error changing amount: " + e.getMessage());
        }
    }

    public int getAmount(DronePart part) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT amount FROM storage WHERE item = ?")) {
            preparedStatement.setString(1, part.getItemName());
            ResultSet result = preparedStatement.executeQuery();
            result.next(); // first column of request is a bool, second column is the amount
            return result.getInt("amount");
        } catch (SQLException e) {
            System.err.println("Error reading amount: " + e.getMessage());
            return -1;
        }
    }

    static public Database getDB() {
        return db;
    }

    public void reset() {
        initialize();
    }

    public static void main(String[] args) {
        Database db = Database.getDB();
        db.addAmount(DronePart.PROPELLER, -5);
        System.out.println("Dronepart amount: " + db.getAmount( DronePart.PROPELLER));
        db.setAmount(DronePart.PROPELLER, 10);
        System.out.println("Dronepart amount: " + db.getAmount(DronePart.PROPELLER));
    }
}
