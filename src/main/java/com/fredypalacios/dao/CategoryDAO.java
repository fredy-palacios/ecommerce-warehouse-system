package com.fredypalacios.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import com.fredypalacios.dao.base.AbstractDAO;
import com.fredypalacios.model.Category;

public class CategoryDAO extends AbstractDAO<Category, Integer> {

    public CategoryDAO() {
        super();
    }

    public CategoryDAO(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    protected Category mapRow(ResultSet resultSet) throws SQLException {
        return new Category(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getInt("active") == 1
        );
    }

    @Override
    public boolean create(Category category) throws SQLException {
        String sql = """
            INSERT INTO categories(name, description, active)
            VALUES (?, ?, ?)
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, category.name());
            preparedStatement.setString(2, category.description());
            preparedStatement.setInt(3, category.active() ? 1 : 0);
        }) > 0;
    }

    @Override
    public boolean update(Category category) throws SQLException {
        String sql = """
            UPDATE categories SET name = ?, description = ?, active = ? WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, category.name());
            preparedStatement.setString(2, category.description());
            preparedStatement.setInt(3, category.active() ? 1 : 0);
            preparedStatement.setInt(4, category.id());
        }) > 0;
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = """
            DELETE FROM categories WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
    }

    @Override
    public Category findById(Integer id) throws SQLException {
        String sql = """
            SELECT * FROM categories WHERE id = ?
            """;
        return executeQueryForOne(sql, preparedStatement -> preparedStatement.setInt(1, id));
    }

    @Override
    public List<Category> findAll() throws SQLException {
        return executeQueryForList("SELECT * FROM categories ORDER BY name");
    }

    public List<Category> findAllActive() throws SQLException {
        String sql = """
            SELECT * FROM categories WHERE active = 1 ORDER BY name
            """;
        return executeQueryForList(sql);
    }
}