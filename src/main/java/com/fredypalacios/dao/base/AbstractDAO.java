package com.fredypalacios.dao.base;

import com.fredypalacios.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractDAO<T, ID> implements GenericDAO<T,ID> {

    private final Supplier<Connection> connectionSupplier;

    public AbstractDAO() {
        this(() -> DatabaseConnection.getConnection());
    }

    protected AbstractDAO(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    protected Connection getConnection() throws SQLException {
        return connectionSupplier.get();
    }

    // Maps a ResultSet row to an entity
    protected abstract T mapRow(ResultSet resultSet) throws SQLException;

    protected int executeUpdate(String sql, SQLConsumer consumer) throws SQLException {
        try(
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            consumer.accept(preparedStatement);
            return preparedStatement.executeUpdate();
        }
    }

    protected T executeQueryForOne(String sql, SQLConsumer consumer) throws SQLException {
        try(
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            consumer.accept(preparedStatement);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    protected List<T> executeQueryForList(String sql, SQLConsumer consumer) throws SQLException {
        List<T> result = new ArrayList<>();
        try(
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
           consumer.accept(preparedStatement);
           try(ResultSet resultSet = preparedStatement.executeQuery()) {
               while (resultSet.next()) {
                   result.add(mapRow(resultSet));
               }
           }
        }
        return result;
    }

    protected List<T> executeQueryForList(String sql) throws SQLException {
        return executeQueryForList(sql, ps -> {});
    }

    protected boolean exists(String tableName, ID id) throws SQLException {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE id = ?", tableName);
        try (
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setObject(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }
 }