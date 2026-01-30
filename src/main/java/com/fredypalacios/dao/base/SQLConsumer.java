package com.fredypalacios.dao.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// To pass lambdas with SQL exceptions
@FunctionalInterface
public interface SQLConsumer {
    void accept(PreparedStatement preparedStatement) throws SQLException;
}