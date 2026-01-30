package com.fredypalacios.dao;

import com.fredypalacios.dao.base.AbstractDAO;
import com.fredypalacios.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends AbstractDAO<User, Integer> {


    @Override
    protected User mapRow(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public boolean create(User entity) throws SQLException {
        return false;
    }

    @Override
    public boolean update(User entity) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Integer integer) throws SQLException {
        return false;
    }

    @Override
    public User findById(Integer integer) throws SQLException {
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        return List.of();
    }
}
