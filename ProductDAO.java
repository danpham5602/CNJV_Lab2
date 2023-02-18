package Ex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements Repository<Product, Integer> {

    private String serverUrl;
    private String user;
    private String password;

    public ProductDAO(String serverUrl, String user, String password) {
        this.serverUrl = serverUrl;
        this.user = user;
        this.password = password;
        try (Connection conn = getConnection()) {
            init(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.serverUrl + "/ProductManagement", this.user, this.password);
    }

    public void init(Connection conn) throws Exception {
        createDatabaseIfNotExist(conn);
        createProductTableIfNotExist(conn);
    }

    private void createProductTableIfNotExist(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT (ID INT NOT NULL AUTO_INCREMENT, NAME VARCHAR(20), PRICE INT, PRIMARY KEY (ID))";
        try (PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.executeUpdate();
        }
    }

    public void createDatabaseIfNotExist(Connection conn) throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS ProductManagement";
        try (PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.executeUpdate();
        }
    }

    @Override
    public Integer add(Product item) {
        Integer id = null;
        String sql = "INSERT INTO PRODUCT (NAME,PRICE) VALUES (?,?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setInt(2, item.getPrice());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<Product> readAll() {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("ID"));
                product.setName(resultSet.getString("NAME"));
                product.setPrice(resultSet.getInt("PRICE"));
                results.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Product read(Integer id) {
        Product result = null;
        String sql = "SELECT * FROM PRODUCT WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = new Product();
                    result.setId(resultSet.getInt("ID"));
                    result.setName(resultSet.getString("NAME"));
                    result.setPrice(resultSet.getInt("PRICE"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
