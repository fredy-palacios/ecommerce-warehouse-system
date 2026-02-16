package com.fredypalacios.service;

import java.sql.SQLException;
import java.util.List;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.dao.ProductDAO;
import com.fredypalacios.model.Category;
import com.fredypalacios.model.Product;
import com.fredypalacios.utils.InputValidator;
import com.fredypalacios.utils.ValidationException;

public class ProductService {
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public ProductService(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }

    public ProductService() {
        this(new ProductDAO(), new CategoryDAO());
    }

    public List<Product> findAll() throws SQLException {
        return productDAO.findAll();
    }

    public Product findById(int id) throws SQLException {
        return productDAO.findById(id);
    }

    public Product findBySku(String sku) throws SQLException {
        if (sku == null || sku.isBlank()) {
            return null;
        }
        return productDAO.findBySku(sku);
    }

    public List<Product> findLowStockProducts() throws SQLException {
        return productDAO.findLowStockProducts();
    }

    public boolean create(
        String sku, String name, String description, double price,
        int stock, int minStock, String location, int categoryId
    ) throws ValidationException, SQLException {

        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new ValidationException("Category does not exist");
        }

        if (!category.active()) {
            throw new ValidationException("Category is inactive");
        }

        String validSku = InputValidator.validateSKU(sku);
        String validName = InputValidator.validateString(
                name, "Product name", 2, 100, false
        );
        String validDescription = InputValidator.validateString(
                description, "Description", 0, 255, true
        );
        double validPrice = InputValidator.validatePrice(price);
        int validStock = InputValidator.validateStock(stock);
        int validMinStock = InputValidator.validateStock(minStock);
        String validLocation = InputValidator.validateString(
                location, "Location", 0, 20, true
        );

        Product product = new Product(
            validSku,
            validName,
            validDescription,
            validPrice,
            validStock,
            validMinStock,
            validLocation,
            categoryId
        );

        return productDAO.create(product);
    }

    public boolean update(Product product) throws SQLException {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productDAO.update(product);
    }

    public boolean updateStock(int productId, int newStock) throws ValidationException, SQLException {
        int validStock = InputValidator.validateStock(newStock);

        Product product = findById(productId);
        if (product == null) {
            return false;
        }

        return productDAO.updateStock(productId, validStock);
    }

    public boolean delete(int id) throws SQLException {
        return productDAO.delete(id);
    }

    public List<Product> getLowStockProducts() throws SQLException {
        return findLowStockProducts();
    }
}