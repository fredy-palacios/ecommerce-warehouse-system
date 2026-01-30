package com.fredypalacios.dao.base;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T, ID> {
    boolean create(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(ID id) throws SQLException;
    T findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;

}