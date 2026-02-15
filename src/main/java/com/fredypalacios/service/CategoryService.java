package com.fredypalacios.service;

import java.sql.SQLException;
import java.util.List;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.model.Category;
import com.fredypalacios.utils.InputValidator;
import com.fredypalacios.utils.ValidationException;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public CategoryService() {
        this(new CategoryDAO());
    }

    public boolean create(String name, String description) throws ValidationException, SQLException {
        String validName = InputValidator.validateString(name, "Category name", 2, 100, false);
        String validDescription = InputValidator.validateString(description, "Description", 0, 255, true);

        Category category = new Category(validName, validDescription);
        return categoryDAO.create(category);
    }

    public boolean update(Category category) throws SQLException {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return categoryDAO.update(category);
    }

    public boolean delete(int id) throws SQLException {
        return categoryDAO.delete(id);
    }

    public Category findById(int id) throws SQLException {
        return categoryDAO.findById(id);
    }

    public List<Category> findAll() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Category> findAllActive() throws SQLException {
        return categoryDAO.findAllActive();
    }

    public boolean toggleActive(int id) throws SQLException {
        Category category = findById(id);
        if (category == null) {
            return false;
        }

        Category updated = new Category(
                category.id(),
                category.name(),
                category.description(),
                !category.active()
        );

        return categoryDAO.update(updated);
    }
}