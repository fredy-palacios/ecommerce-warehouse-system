package com.fredypalacios.service;

import java.sql.SQLException;
import java.util.Arrays;
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
import com.fredypalacios.model.Category;
import com.fredypalacios.utils.ValidationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
public class CategoryServiceTest {

    @Mock
    private CategoryDAO categoryDAO;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("FindAll should return list of categories")
    void findAll_shouldReturnCategories() throws SQLException {
        List<Category> mockCategories = Arrays.asList(
                new Category(1, "Electronics", "Laptops and tech devices", true),
                new Category(2, "Books", "Software development books", true)
        );

        when(categoryDAO.findAll()).thenReturn(mockCategories);

        List<Category> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).name());
        verify(categoryDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("FindAllActive should return only active categories")
    void findAllActive_shouldReturnOnlyActive() throws SQLException {
        List<Category> activeCategories = Arrays.asList(
                new Category(1, "Electronics", "Devices", true),
                new Category(2, "Books", "Books", true)
        );
        when(categoryDAO.findAllActive()).thenReturn(activeCategories);

        List<Category> result = categoryService.findAllActive();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Category::active));
        verify(categoryDAO, times(1)).findAllActive();
    }

    @Test
    @DisplayName("FindById should return category when exists")
    void findById_whenExists_shouldReturnCategory() throws SQLException {
        Category mockCategory = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(mockCategory);

        Category result = categoryService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Electronics", result.name());
        verify(categoryDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("FindById should return null when not exists")
    void findById_whenNotExists_shouldReturnNull() throws SQLException {
        when(categoryDAO.findById(999)).thenReturn(null);

        Category result = categoryService.findById(999);

        assertNull(result);
        verify(categoryDAO, times(1)).findById(999);
    }

    @Test
    @DisplayName("Create with valid data should succeed")
    void create_withValidData_shouldSucceed() throws SQLException, ValidationException {
        when(categoryDAO.create(any(Category.class))).thenReturn(true);

        boolean result = categoryService.create("Electronics", "Electronic devices");

        assertTrue(result);
        verify(categoryDAO, times(1)).create(any(Category.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"A"})
    @DisplayName("Create with invalid name should throw ValidationException")
    void create_withInvalidName_shouldThrowValidationException(String invalidName) throws SQLException {
        assertThrows(ValidationException.class, () -> {
            categoryService.create(invalidName, "Description");
        });

        verify(categoryDAO, never()).create(any(Category.class));
    }

    @Test
    @DisplayName("Update should call DAO with category")
    void update_shouldCallDAO() throws SQLException {
        Category category = new Category(1, "Updated Name", "Updated desc", true);
        when(categoryDAO.update(category)).thenReturn(true);

        boolean result = categoryService.update(category);

        assertTrue(result);
        verify(categoryDAO, times(1)).update(category);
    }

    @Test
    @DisplayName("Update with null category should throw exception")
    void update_withNull_shouldThrowException() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.update(null);
        });

        verify(categoryDAO, never()).update(any(Category.class));
    }

    @Test
    @DisplayName("Delete should call DAO")
    void delete_shouldCallDAO() throws SQLException {
        when(categoryDAO.delete(1)).thenReturn(true);

        boolean result = categoryService.delete(1);

        assertTrue(result);
        verify(categoryDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("ToggleActive should invert active status")
    void toggleActive_shouldInvertStatus() throws SQLException {
        Category activeCategory = new Category(1, "Electronics", "Devices", true);
        when(categoryDAO.findById(1)).thenReturn(activeCategory);
        when(categoryDAO.update(any(Category.class))).thenReturn(true);


        boolean result = categoryService.toggleActive(1);

        assertTrue(result);
        verify(categoryDAO, times(1)).findById(1);

        // Checks that update was called with an INACTIVE category
        verify(categoryDAO, times(1)).update(argThat(cat ->
                cat.id() == 1 && !cat.active()
        ));
    }

    @Test
    @DisplayName("ToggleActive with non-existent ID should return false")
    void toggleActive_withNonExistentId_shouldReturnFalse() throws SQLException {
        when(categoryDAO.findById(999)).thenReturn(null);

        boolean result = categoryService.toggleActive(999);

        assertFalse(result);
        verify(categoryDAO, times(1)).findById(999);
        verify(categoryDAO, never()).update(any(Category.class));
    }
}