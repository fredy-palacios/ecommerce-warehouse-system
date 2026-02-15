package com.fredypalacios.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import com.fredypalacios.dao.base.AbstractDAO;
import com.fredypalacios.enums.UserRole;
import com.fredypalacios.model.User;

public class UserDAO extends AbstractDAO<User, Integer> {

    public UserDAO() {
        super();
    }

    public UserDAO(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    protected User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getInt("id"),
            resultSet.getString("username"),
            resultSet.getString("password"),
            resultSet.getString("email"),
            resultSet.getString("full_name"),
            UserRole.valueOf(resultSet.getString("role")),
            resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }

    @Override
    public boolean create(User user) throws SQLException {
        String sql = """
            INSERT INTO users (username, password, email, full_name, role)
            VALUES (?, ?, ?, ?, ?)
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.setString(4, user.fullName());
            preparedStatement.setString(5, user.role().name());
        }) > 0;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = """ 
            UPDATE users
            SET username = ?, email = ?, full_name = ?, role = ?
            WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.setString(4, user.fullName());
            preparedStatement.setString(5, user.role().name());
            preparedStatement.setInt(6, user.id());
        }) > 0;
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = """
            DELETE FROM users WHERE id = ?
            """;
        return executeUpdate(sql, preparedStatement -> preparedStatement.setInt(1,id)) > 0;
    }

    @Override
    public User findById(Integer id) throws SQLException {
        String sql = """
            SELECT * FROM users WHERE id = ?
            """;
        return executeQueryForOne(sql, preparedStatement -> preparedStatement.setInt(1, id));
    }

    @Override
    public List<User> findAll() throws SQLException {
        return executeQueryForList("""
            SELECT * FROM users ORDER BY id
            """);
    }

    public User findByUserName(String username) throws SQLException {
        String sql = """
            SELECT * FROM users WHERE username = ?
            """;
        return executeQueryForOne(sql, preparedStatement -> preparedStatement.setString(1, username));
    }

    public List<User> findByRole(UserRole role) throws SQLException {
        String sql = """
            SELECT * FROM users WHERE role = ? ORDER BY username
            """;
        return executeQueryForList(sql, preparedStatement -> preparedStatement.setString(1, role.name()));
    }
}
