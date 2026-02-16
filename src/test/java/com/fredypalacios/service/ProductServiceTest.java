package com.fredypalacios.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.dao.ProductDAO;
import com.fredypalacios.enums.ProductStatus;
import com.fredypalacios.model.Category;
import com.fredypalacios.model.Product;
import com.fredypalacios.utils.ValidationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @Mock
    private CategoryDAO categoryDAO;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("FindAll should return list of products")
    void findAll_shouldReturnProducts() throws SQLException {
        List<Product> mockProducts = Arrays.asList(
            new Product(
                1, "SKU-001", "Laptop", "Gaming laptop",
                1299.99, 10, 0, 5, "A-01",
                ProductStatus.AVAILABLE, 1, LocalDateTime.now()
            ),
            new Product(
                2, "SKU-002", "Mouse", "Wireless",
                29.99, 50, 0, 10, "A-02",
                ProductStatus.AVAILABLE, 1, LocalDateTime.now()
            )
        );
        when(productDAO.findAll()).thenReturn(mockProducts);

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SKU-001", result.get(0).sku());
        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("FindAll should return empty list when no products")
    void findAll_whenEmpty_shouldReturnEmptyList() throws SQLException {
        when(productDAO.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("FindById should return product when exists")
    void findById_whenExists_shouldReturnProduct() throws SQLException {
        Product mockProduct = new Product(
            1, "SKU-001", "Laptop", "Gaming",
            1299.99, 10, 0, 5, "A-01",
            ProductStatus.AVAILABLE, 1, LocalDateTime.now()
        );
        when(productDAO.findById(1)).thenReturn(mockProduct);

        Product result = productService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("SKU-001", result.sku());
        verify(productDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("FindById should return null when not exists")
    void findById_whenNotExists_shouldReturnNull() throws SQLException {
        when(productDAO.findById(999)).thenReturn(null);

        Product result = productService.findById(999);

        assertNull(result);
        verify(productDAO, times(1)).findById(999);
    }

    @Test
    @DisplayName("FindBySku should return product when exists")
    void findBySku_whenExists_shouldReturnProduct() throws SQLException {
        Product mockProduct = new Product(
            1, "SKU-001", "Laptop", "Gaming",
            1299.99, 10, 0, 5, "A-01",
            ProductStatus.AVAILABLE, 1, LocalDateTime.now()
        );
        when(productDAO.findBySku("SKU-001")).thenReturn(mockProduct);

        Product result = productService.findBySku("SKU-001");

        assertNotNull(result);
        assertEquals("SKU-001", result.sku());
        verify(productDAO, times(1)).findBySku("SKU-001");
    }

    @Test
    @DisplayName("FindBySku should return null when not exists")
    void findBySku_whenNotExists_shouldReturnNull() throws SQLException {
        when(productDAO.findBySku("NONEXISTENT")).thenReturn(null);

        Product result = productService.findBySku("NONEXISTENT");

        assertNull(result);
        verify(productDAO, times(1)).findBySku("NONEXISTENT");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("FindBySku with blank should return null")
    void findBySku_withBlank_shouldReturnNull(String sku) throws SQLException {
        Product result = productService.findBySku(sku);

        assertNull(result);
        verify(productDAO, never()).findBySku(any());
    }

    @Test
    @DisplayName("FindLowStockProducts should return products with low stock")
    void findLowStockProducts_shouldReturnLowStock() throws SQLException {
        List<Product> lowStockProducts = Arrays.asList(
            new Product(
                1, "SKU-001", "Product 1", "",
                99.99, 3, 0, 5, "A-01",
                ProductStatus.LOW_STOCK, 1, LocalDateTime.now()
            )
        );
        when(productDAO.findLowStockProducts()).thenReturn(lowStockProducts);

        List<Product> result = productService.findLowStockProducts();

        assertEquals(1, result.size());
        verify(productDAO, times(1)).findLowStockProducts();
    }

    @Test
    @DisplayName("GetLowStockProducts should delegate to findLowStockProducts")
    void getLowStockProducts_shouldDelegate() throws SQLException {
        List<Product> lowStockProducts = Collections.emptyList();
        when(productDAO.findLowStockProducts()).thenReturn(lowStockProducts);

        List<Product> result = productService.getLowStockProducts();

        assertNotNull(result);
        verify(productDAO, times(1)).findLowStockProducts();
    }

    @Test
    @DisplayName("Create should validate category exists")
    void create_shouldValidateCategoryExists() throws SQLException, ValidationException {
        Category category = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(category);
        when(productDAO.create(any(Product.class))).thenReturn(true);

        boolean result = productService.create(
            "SKU-001", "Laptop", "Gaming laptop",
            1299.99, 10, 5, "A-01", 1
        );

        assertTrue(result);
        verify(categoryDAO, times(1)).findById(1);
        verify(productDAO, times(1)).create(any(Product.class));
    }

    @Test
    @DisplayName("Create should throw exception if category not exists")
    void create_categoryNotExists_shouldThrowException() throws SQLException {
        when(categoryDAO.findById(999)).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> productService.create(
                "SKU-001", "Laptop", "Gaming",
                1299.99, 10, 5, "A-01", 999
            )
        );

        assertEquals("Category does not exist", exception.getMessage());
        verify(productDAO, never()).create(any());
    }

    @Test
    @DisplayName("Create should throw exception if category is inactive")
    void create_inactiveCategory_shouldThrowException() throws SQLException {
        Category inactiveCategory = new Category(1, "Electronics", "Devices", false);
        when(categoryDAO.findById(1)).thenReturn(inactiveCategory);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> productService.create(
                "SKU-001", "Laptop", "Gaming",
                1299.99, 10, 5, "A-01", 1
            )
        );

        assertEquals("Category is inactive", exception.getMessage());
        verify(productDAO, never()).create(any());
    }

    @Test
    @DisplayName("Create with invalid SKU should throw ValidationException")
    void create_withInvalidSKU_shouldThrowException() throws SQLException {
        Category category = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(category);

        assertThrows(
            ValidationException.class,
            () -> productService.create(
                "AB", "Product", "Desc",
                99.99, 10, 5, "A-01", 1
            )
        );

        verify(productDAO, never()).create(any());
    }

    @Test
    @DisplayName("Create with negative price should throw ValidationException")
    void create_withNegativePrice_shouldThrowException() throws SQLException {
        Category category = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(category);

        assertThrows(
            ValidationException.class,
            () -> productService.create(
                "SKU-001", "Product", "Desc",
                -10.0, 10, 5, "A-01", 1
            )
        );

        verify(productDAO, never()).create(any());
    }

    @Test
    @DisplayName("Create with negative stock should throw ValidationException")
    void create_withNegativeStock_shouldThrowException() throws SQLException {
        Category category = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(category);

    assertThrows(
        ValidationException.class,
        () -> productService.create(
            "SKU-001", "Product", "Desc",
            99.99, -5, 5, "A-01", 1
        )
    );

        verify(productDAO, never()).create(any());
    }

    @Test
    @DisplayName("Update should call DAO")
    void update_shouldCallDAO() throws SQLException {
        Product product = new Product(
            1, "SKU-001", "Laptop", "Gaming",
            1299.99, 10, 0, 5, "A-01",
            ProductStatus.AVAILABLE, 1, LocalDateTime.now()
        );
        when(productDAO.update(product)).thenReturn(true);

        boolean result = productService.update(product);

        assertTrue(result);
        verify(productDAO, times(1)).update(product);
    }

    @Test
    @DisplayName("Update with null should throw exception")
    void update_withNull_shouldThrowException() throws SQLException {
        assertThrows(
            IllegalArgumentException.class,
            () -> productService.update(null)
        );

        verify(productDAO, never()).update(any());
    }

    @Test
    @DisplayName("UpdateStock should validate and update")
    void updateStock_shouldValidateAndUpdate() throws SQLException, ValidationException {
        Product product = new Product(
            1, "SKU-001", "Laptop", "Gaming",
            1299.99, 10, 0, 5, "A-01",
            ProductStatus.AVAILABLE, 1, LocalDateTime.now()
        );
        when(productDAO.findById(1)).thenReturn(product);
        when(productDAO.updateStock(1, 20)).thenReturn(true);

        boolean result = productService.updateStock(1, 20);

        assertTrue(result);
        verify(productDAO, times(1)).findById(1);
        verify(productDAO, times(1)).updateStock(1, 20);
    }

    @Test
    @DisplayName("UpdateStock for non-existent product should return false")
    void updateStock_nonExistentProduct_shouldReturnFalse() throws SQLException, ValidationException {
        when(productDAO.findById(999)).thenReturn(null);

        boolean result = productService.updateStock(999, 20);

        assertFalse(result);
        verify(productDAO, times(1)).findById(999);
        verify(productDAO, never()).updateStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Delete should call DAO")
    void delete_shouldCallDAO() throws SQLException {
        when(productDAO.delete(1)).thenReturn(true);

        boolean result = productService.delete(1);

        assertTrue(result);
        verify(productDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("Delete non-existent should return false")
    void delete_nonExistent_shouldReturnFalse() throws SQLException {
        when(productDAO.delete(999)).thenReturn(false);

        boolean result = productService.delete(999);

        assertFalse(result);
        verify(productDAO, times(1)).delete(999);
    }
}