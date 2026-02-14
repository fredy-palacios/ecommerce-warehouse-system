package com.fredypalacios.dao;

import com.fredypalacios.dao.base.AbstractDAO;
import com.fredypalacios.enums.ProductStatus;
import com.fredypalacios.model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

public class ProductDAO extends AbstractDAO<Product, Integer> {

    public ProductDAO() {
        super();
    }

    public ProductDAO(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    protected Product mapRow(ResultSet resultSet) throws SQLException {
        return new Product(
            resultSet.getInt("id"),
            resultSet.getString("sku"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getDouble("price"),
            resultSet.getInt("stock"),
            resultSet.getInt("reserved_stock"),
            resultSet.getInt("min_stock"),
            resultSet.getString("location"),
            ProductStatus.valueOf(resultSet.getString("status")),
            resultSet.getInt("category_id"),
            resultSet.getTimestamp("last_update").toLocalDateTime()
        );
    }

    @Override
    public boolean create(Product product) throws SQLException {
        String sql = """
            INSERT INTO products (sku, name, description, price, stock, reserved_stock, min_stock, location, status, category_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, product.sku());
            preparedStatement.setString(2, product.name());
            preparedStatement.setString(3, product.description());
            preparedStatement.setDouble(4, product.price());
            preparedStatement.setInt(5, product.stock());
            preparedStatement.setInt(6, product.reservedStock());
            preparedStatement.setInt(7, product.minStock());
            preparedStatement.setString(8, product.location());
            preparedStatement.setString(9, product.status().name());
            preparedStatement.setInt(10, product.categoryId());
        }) > 0;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = """
            UPDATE products
            SET sku = ?, name = ?, description = ?, price = ?, stock = ?, reserved_stock = ?, min_stock = ?,
                location = ?, status = ?, category_id = ?, last_update = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, product.sku());
            preparedStatement.setString(2, product.name());
            preparedStatement.setString(3, product.description());
            preparedStatement.setDouble(4, product.price());
            preparedStatement.setInt(5, product.stock());
            preparedStatement.setInt(6, product.reservedStock());
            preparedStatement.setInt(7, product.minStock());
            preparedStatement.setString(8, product.location());
            preparedStatement.setString(9, product.status().name());
            preparedStatement.setInt(10, product.categoryId());
            preparedStatement.setInt(11, product.id());
        }) > 0;
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = """
        DELETE FROM products WHERE id = ?
        """;
        return executeUpdate(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
    }

    @Override
    public Product findById(Integer id) throws SQLException {
        String sql = """
        SELECT * FROM products WHERE id = ?
        """;
        return executeQueryForOne(sql, preparedStatement -> preparedStatement.setInt(1, id));
    }

    @Override
    public List<Product> findAll() throws SQLException {
        return executeQueryForList("""
            SELECT * FROM products ORDER BY name
            """);
    }

    public Product findBySku(String sku) throws SQLException {
        String sql = """
            SELECT * FROM products WHERE sku = ?
            """;
        return executeQueryForOne(sql, preparedStatement -> preparedStatement.setString(1, sku));
    }

    public List<Product> findByCategory(String categoryId) throws SQLException {
        String sql = """
            SELECT * FROM products WHERE category_id = ? ORDER BY name
            """;
        return executeQueryForList(sql, preparedStatement -> preparedStatement.setString(1, categoryId));
    }

    public List<Product> findLowStockProducts() throws SQLException {
        String sql = """
            SELECT * FROM products WHERE stock <= min_stock ORDER BY stock ASC
            """;
        return executeQueryForList(sql);
    }

    public boolean updateStock(int id, int newStock) throws SQLException {
        String sql = """
            UPDATE products
            SET stock = ?,
            status = CASE
                WHEN ? = 0 THEN 'OUT_OF_STOCK'
                WHEN ? <= min_stock THEN 'LOW_STOCK'
                ELSE 'AVAILABLE'
            END,
            last_update = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setInt(1, newStock);
            preparedStatement.setInt(2, newStock);
            preparedStatement.setInt(3, newStock);
            preparedStatement.setInt(4, id);
        }) > 0;
    }
}