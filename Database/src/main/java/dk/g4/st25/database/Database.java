package dk.g4.st25.database;

import dk.g4.st25.common.util.DronePart;
import dk.g4.st25.common.util.Order;
import dk.g4.st25.common.util.Product;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    Drop TABLE IF EXISTS drones CASCADE;
           
                    CREATE TABLE drones (
                        id SERIAL PRIMARY KEY,
                        name varchar UNIQUE NOT NULL
                    );
           """);
            System.out.println("Drones table created.");
        } catch (SQLException e) {
            System.err.println("Error creating drones table: " + e.getMessage());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    Drop TABLE IF EXISTS orders CASCADE;
           
                    CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        orderName varchar NOT NULL UNIQUE,
                        typeID int NOT NULL,
                        amount int NOT NULL CHECK ( amount >= 0 ),
                        FOREIGN KEY (typeID) REFERENCES drones(id)
                    );
           """);
            System.out.println("Orders table created.");
        } catch (SQLException e) {
            System.err.println("Error creating orders table: " + e.getMessage());
        }

        // Insert all items from DronePart enum
        int defaultAmount = 100;
        System.out.println("Inserting default items:");
        for (DronePart item : DronePart.values()) insertItem(item.getItemName(), defaultAmount);

        // Insert default drone types
        insertDroneType("Kamikaze");
        insertDroneType("Transport");
        insertDroneType("Recon");
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

    public void insertOrder(String orderName, int typeID, int amount) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO orders (orderName, typeID, amount) VALUES (?,?,?)")) {

            preparedStatement.setString(1, orderName);
            preparedStatement.setInt(2, typeID);
            preparedStatement.setInt(3, amount);
            preparedStatement.execute();

            System.out.printf("new row - Order: %s | Type: %d | Amount: %d%n", orderName, typeID,  amount);

        } catch (SQLException e) {
            System.err.println("Error inserting new order: " + e.getMessage());
        }
    }

    public List<Order> getOrders() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM orders")) {
            ResultSet result = preparedStatement.executeQuery();

            List<Order> orders = new ArrayList<>();
            while (result.next()) {
                // Get columns
                int id = result.getInt("id");
                String orderName = result.getString("orderName");
                int typeID = result.getInt("typeID");
                String type = getDroneTypes().get(typeID-1);
                int amount = result.getInt("amount");

                // Create and add order
                Product product = new Product(typeID, type);
                Order order = new Order(id, orderName, product, amount);
                orders.add(order);

                System.out.println("Order: " + id +", "+ orderName +", "+ type +", "+ amount);
            }
            return orders;
        } catch (SQLException e) {
            System.err.println("Error reading orders: " + e.getMessage());
            return null;
        }
    }

    public void deleteOrder(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM orders WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            int rowAffectedID = preparedStatement.executeUpdate();
            System.out.println("Deleted row - " + rowAffectedID);
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }
    }

    public void insertDroneType(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO drones (name) VALUES (?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.execute();

            System.out.printf("new row - Type: %s%n", name);

        } catch (SQLException e) {
            System.err.println("Error inserting new drone-type: " + e.getMessage());
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

    public List<String> getDroneTypes() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM drones")) {
            List<String> types = new ArrayList<>();
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                String name = result.getString("name");
                types.add(name);
            }
            return types;
        } catch (SQLException e) {
            System.err.println("Error reading drone types: " + e.getMessage());
            return null;
        }
    }

    public static Database getDB() {
        return db;
    }

    public void reset() {
        initialize();
    }

    public static void main(String[] args) {
        Database db = Database.getDB();
        db.insertOrder("Order1", 1, 10);
        db.insertOrder("Order2", 2, 10);
        db.insertOrder("Order3", 3, 10);
        db.getOrders();
        db.deleteOrder(1);
        db.getOrders();
    }
}
