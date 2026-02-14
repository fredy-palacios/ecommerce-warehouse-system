package com.fredypalacios.service;

import java.sql.SQLException;
import java.util.List;

import static com.fredypalacios.utils.ConsoleColors.*;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.dao.ProductDAO;
import com.fredypalacios.dao.UserDAO;
import com.fredypalacios.enums.ProductStatus;
import com.fredypalacios.model.Category;
import com.fredypalacios.model.Product;
import com.fredypalacios.model.User;

public class DashboardService {
    private final ProductDAO productDAO;
    private final UserDAO userDAO;
    private final CategoryDAO categoryDAO;

    public DashboardService() {
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public void showStatistics() throws SQLException {
        List<Product> products = productDAO.findAll();
        List<Product> lowStock = productDAO.findLowStockProducts();
        List<User> users = userDAO.findAll();
        List<Category> categories = categoryDAO.findAll();

        double totalValue = products.stream()
                .mapToDouble(product -> product.price() * product.stock())
                .sum();

        long available = products.stream()
                .filter(product -> product.status() == ProductStatus.AVAILABLE)
                .count();

        long outOfStock = products.stream()
                .filter(product -> product.status() == ProductStatus.OUT_OF_STOCK)
                .count();

        System.out.println(infoBg(" INVENTORY "));
        System.out.println(info("  • Total products:     ") + success(products.size() + " items"));
        System.out.println(info("  • Available:          ") + success(available + " items"));
        System.out.println(info("  • Out of stock:       ") + error(outOfStock + " items"));
        System.out.println(info("  • Inventory value:    ") + success(String.format("$%.2f", totalValue)));
        System.out.println();

        System.out.println(warningBg(" ALERTS "));
        System.out.println(warning("  • Low stock:          ") + errorBg(" " + lowStock.size() + " "));
        System.out.println();

        System.out.println(infoBg(" OTHERS "));
        System.out.println(info("  • Categories:         ") + success(categories.size() + ""));
        System.out.println(info("  • Users:              ") + success(users.size() + ""));
    }
}
