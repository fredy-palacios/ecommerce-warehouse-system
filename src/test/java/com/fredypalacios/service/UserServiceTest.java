package com.fredypalacios.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fredypalacios.dao.UserDAO;
import com.fredypalacios.enums.UserRole;
import com.fredypalacios.model.User;
import com.fredypalacios.utils.ValidationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("FindAll should return list of users")
    void findAll_shouldReturnUsers() throws SQLException {
        List<User> mockUsers = Arrays.asList(
            new User(
                1, "admin", "hashedPass",
                "admin@test.com", "Admin User",
                UserRole.MANAGER, LocalDateTime.now()
            ),
            new User(
                2, "picker1", "hashedPass",
                "picker@test.com", "Picker User",
                UserRole.PICKER, LocalDateTime.now()
            )
        );

        when(userDAO.findAll()).thenReturn(mockUsers);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).username());
        assertEquals(UserRole.MANAGER, result.get(0).role());
        verify(userDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("FindAll should return empty list when no users")
    void findAll_whenEmpty_shouldReturnEmptyList() throws SQLException {
        when(userDAO.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("FindById should return user when exists")
    void findById_whenExists_shouldReturnUser() throws SQLException {
        User mockUser = new User(
            1, "admin", "hashedPass",
            "admin@test.com", "Admin",
            UserRole.MANAGER, LocalDateTime.now()
        );
        when(userDAO.findById(1)).thenReturn(mockUser);

        User result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("admin", result.username());
        verify(userDAO, times(1)).findById(1);
    }

    @Test
    @DisplayName("FindById should return null when not exists")
    void findById_whenNotExists_shouldReturnNull() throws SQLException {
        when(userDAO.findById(999)).thenReturn(null);

        User result = userService.findById(999);

        assertNull(result);
        verify(userDAO, times(1)).findById(999);
    }

    @Test
    @DisplayName("FindByUsername should return user when exists")
    void findByUsername_whenExists_shouldReturnUser() throws SQLException {
        User mockUser = new User(
            1, "admin", "hashedPass",
            "admin@test.com", "Admin",
            UserRole.MANAGER, LocalDateTime.now()
        );
        when(userDAO.findByUserName("admin")).thenReturn(mockUser);

        User result = userService.findByUsername("admin");

        assertNotNull(result);
        assertEquals("admin", result.username());
        verify(userDAO, times(1)).findByUserName("admin");
    }

    @ParameterizedTest(name = "[{index}] username=\"{0}\"")
    @MethodSource("invalidUsernames")
    @DisplayName("FindByUsername with invalid inputs should return null")
    void findByUsername_invalidInputs_shouldReturnNull(String username) throws SQLException {
        User result = userService.findByUsername(username);

        assertNull(result);
        verify(userDAO, never()).findByUserName(any());
    }

    private static Stream<String> invalidUsernames() {
        return Stream.of(
            null,
                "",
                "   ");
    }

    @Test
    @DisplayName("FindByRole with null should throw exception")
    void findByRole_withNull_shouldThrowException() throws SQLException {
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.findByRole(null)
        );

        verify(userDAO, never()).findByRole(any());
    }

    @Test
    @DisplayName("Create with valid data should succeed")
    void create_withValidData_shouldSucceed() throws SQLException, ValidationException {
        when(userDAO.create(any(User.class))).thenReturn(true);

        boolean result = userService.create(
            "admin123",
            "SecurePass123!",
            "admin@test.com",
            "John Admin",
            UserRole.MANAGER
        );

        assertTrue(result);
        verify(userDAO, times(1)).create(argThat(user ->
            user.username().equals("admin123") &&
                !user.password().equals("SecurePass123!") &&
                user.email().equals("admin@test.com") &&
                user.fullName().equals("John Admin") &&
                user.role() == UserRole.MANAGER
        ));
    }

    @Test
    @DisplayName("Create should hash password before saving")
    void create_shouldHashPassword() throws SQLException, ValidationException {
        when(userDAO.create(any(User.class))).thenReturn(true);
        String plainPassword = "MyPassword123";

        userService.create("user", plainPassword, "user@test.com", "User Name", UserRole.PICKER);

        verify(userDAO, times(1)).create(argThat(user ->
            !user.password().equals(plainPassword) &&
                user.password().startsWith("$2a$")
        ));
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("invalidCreateInputs")
    @DisplayName("Create should throw ValidationException when input is invalid")
    void create_withInvalidInput_shouldThrowException(
            String username,
            String password,
            String email,
            String testCase
    ) throws SQLException {
        assertThrows(
            ValidationException.class,
            () -> userService.create(username, password, email, "Full Name", UserRole.MANAGER)
        );
        verify(userDAO, never()).create(any());
    }

    private static Stream<Arguments> invalidCreateInputs() {
        return Stream.of(
            Arguments.of("ab", "Pass123!", "email@test.com", "Username too short"),
            Arguments.of("asdfdsfsdfdsfddcscdcsdcdccdscdsccsc", "Pass123!", "email@test.com", "Username too long"),
            Arguments.of("username", "short", "email@test.com", "Password too short"),
            Arguments.of("username", "nouppercasenum123", "email@test.com", "Password missing uppercase"),
            Arguments.of("username", "NOLOWERCASE123", "email@test.com", "Password missing lowercase"),
            Arguments.of("username", "NoNumbers", "email@test.com", "Password missing numbers"),
            Arguments.of("username", "Pass123!", "invalid", "Email invalid format"),
            Arguments.of("username", "Pass123!", "invalid@", "Email incomplete"),
            Arguments.of(null, "Pass123!", "email@test.com", "Username null"),
            Arguments.of("", "Pass123!", "email@test.com", "Username empty")
        );
    }

    @Test
    @DisplayName("Create with null role should throw ValidationException")
    void create_withNullRole_shouldThrowException() throws SQLException {
        assertThrows(
            ValidationException.class,
            () -> userService.create(
                "username", "Pass123!",
                "email@test.com", "Full Name", null
            )
        );

        verify(userDAO, never()).create(any());
    }

    @Test
    @DisplayName("Update should call DAO")
    void update_shouldCallDAO() throws SQLException {
        User user = new User(
                1, "admin", "hashedPass",
                "admin@test.com", "Admin",
                UserRole.MANAGER, LocalDateTime.now()
        );
        when(userDAO.update(user)).thenReturn(true);

        boolean result = userService.update(user);

        assertTrue(result);
        verify(userDAO, times(1)).update(user);
    }

    @Test
    @DisplayName("Update with null should throw exception")
    void update_withNull_shouldThrowException() throws SQLException {
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.update(null)
        );

        verify(userDAO, never()).update(any());
    }

    @Test
    @DisplayName("UpdatePassword should hash new password")
    void updatePassword_shouldHashNewPassword() throws SQLException, ValidationException {
        User existingUser = new User(
                1, "admin", "oldHashedPass",
                "admin@test.com", "Admin",
                UserRole.MANAGER, LocalDateTime.now()
        );
        when(userDAO.findById(1)).thenReturn(existingUser);
        when(userDAO.update(any(User.class))).thenReturn(true);
        String newPlainPassword = "NewPassword123!";

        boolean result = userService.updatePassword(1, newPlainPassword);

        assertTrue(result);
        verify(userDAO, times(1)).update(argThat(user ->
            user.id() == 1 &&
                !user.password().equals(newPlainPassword) &&
                user.password().startsWith("$2a$") &&
                !user.password().equals("oldHashedPass")
        ));
    }

    @Test
    @DisplayName("UpdatePassword for non-existent user should return false")
    void updatePassword_nonExistentUser_shouldReturnFalse() throws SQLException, ValidationException {
        when(userDAO.findById(999)).thenReturn(null);

        boolean result = userService.updatePassword(999, "NewPass123!");

        assertFalse(result);
        verify(userDAO, times(1)).findById(999);
        verify(userDAO, never()).update(any());
    }

    @Test
    @DisplayName("Delete should call DAO")
    void delete_shouldCallDAO() throws SQLException {
        when(userDAO.delete(1)).thenReturn(true);

        boolean result = userService.delete(1);

        assertTrue(result);
        verify(userDAO, times(1)).delete(1);
    }

    @Test
    @DisplayName("Delete non-existent user should return false")
    void delete_nonExistent_shouldReturnFalse() throws SQLException {
        when(userDAO.delete(999)).thenReturn(false);

        boolean result = userService.delete(999);

        assertFalse(result);
        verify(userDAO, times(1)).delete(999);
    }
}