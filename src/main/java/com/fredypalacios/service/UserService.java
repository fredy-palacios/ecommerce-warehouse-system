package com.fredypalacios.service;

import java.sql.SQLException;
import java.util.List;

import com.fredypalacios.dao.UserDAO;
import com.fredypalacios.enums.UserRole;
import com.fredypalacios.model.User;
import com.fredypalacios.utils.InputValidator;
import com.fredypalacios.utils.PasswordHasher;
import com.fredypalacios.utils.ValidationException;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserService() {
        this(new UserDAO());
    }

    public List<User> findByRole(UserRole role) throws SQLException {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return userDAO.findByRole(role);
    }

    public boolean create(
        String username,
        String password,
        String email,
        String fullName,
        UserRole role
    ) throws ValidationException, SQLException {

        String validUsername = InputValidator.validateUsername(username);
        String validPassword = InputValidator.validatePassword(password);
        String validEmail = InputValidator.validateEmail(email);
        String validFullName = InputValidator.validateFullName(fullName);

        if (role == null) {
            throw new ValidationException("Role cannot be null");
        }

        String hashedPassword = PasswordHasher.hash(validPassword);

        User user = new User(validUsername, hashedPassword, validEmail, validFullName, role);
        return userDAO.create(user);
    }

    public boolean update(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userDAO.update(user);
    }

    public boolean delete(int id) throws SQLException {
        return userDAO.delete(id);
    }

    public User findById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }

    public User findByUsername(String username) throws SQLException {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userDAO.findByUserName(username);
    }

    public boolean updatePassword(int userId, String newPassword) throws ValidationException, SQLException {
        String validPassword = InputValidator.validatePassword(newPassword);

        User user = findById(userId);
        if (user == null) {
            return false;
        }

        String hashedPassword = PasswordHasher.hash(validPassword);

        User updated = new User(
            user.id(),
            user.username(),
            hashedPassword,
            user.email(),
            user.fullName(),
            user.role(),
            user.createdAt()
        );
        return userDAO.update(updated);
    }
}