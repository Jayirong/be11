package com.yummy.be11.testServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yummy.be11.model.Role;
import com.yummy.be11.model.User;
import com.yummy.be11.repository.UserRepository;
import com.yummy.be11.security.JwtUtil;
import com.yummy.be11.service.UserServiceImpl;

class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldStoreUserCorrectly() {
        // Datos de prueba
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plaintextpassword");
        user.setNombre("John");
        user.setApellido("Doe");

        // Simular encriptación de contraseña
        String encryptedPassword = "encryptedpassword";
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encryptedPassword);

        // Simular almacenamiento del usuario
        when(userRepository.save(user)).thenReturn(user);

        // Llamar al método
        User registeredUser = userService.registerUser(user);

        // Verificar encriptación
        assertEquals(encryptedPassword, registeredUser.getPassword(), "La contraseña debe estar encriptada");

        // Verificar almacenamiento
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void authenticateAndGenerateToken_ShouldGenerateTokenForValidCredentials() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String generatedToken = "mockToken";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn(generatedToken);

        // Act
        String token = userService.authenticateAndGenerateToken(username, password);

        // Assert
        assertEquals(generatedToken, token, "El token generado debe coincidir con el token simulado.");
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, times(1)).generateToken(username);
    }

     @Test
    void authenticateAndGenerateToken_ShouldThrowExceptionForInvalidCredentials() {
        // Arrange
        String username = "testuser";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticateAndGenerateToken(username, password);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtUtil, never()).generateToken(username);
    }

     @Test
    void authenticateAndGenerateToken_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticateAndGenerateToken(username, password);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(username);
    }

    @Test
    void findByUsername_ShouldReturnUserWhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertEquals(user, result, "El usuario retornado debería coincidir con el usuario esperado");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsername_ShouldReturnNullWhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertEquals(null, result, "El resultado debería ser null cuando el usuario no existe");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUserById_ShouldReturnUserWhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId_user(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserById(userId);

        // Assert
        assertEquals(user, result, "El usuario retornado debería coincidir con el usuario esperado");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserById_ShouldThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
    
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
    
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findUserById(userId);
        });
    
        verify(userRepository, times(1)).findById(userId);
    }
 
    @Test
    void updateUser_ShouldUpdateAllowedFields() {
        // Arrange
        String currentUsername = "testuser";
        User existingUser = new User();
        existingUser.setUsername(currentUsername);
        existingUser.setPassword("oldpassword");
        existingUser.setRoles(Set.of(Role.USER));

        User updatedUser = new User();
        updatedUser.setUsername("newusername");
        updatedUser.setPassword("newpassword");
        updatedUser.setRoles(Set.of(Role.ADMIN));

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(currentUsername, updatedUser);

        // Assert
        assertEquals("newusername", result.getUsername(), "El nombre de usuario debería actualizarse");
        assertEquals("encodedpassword", result.getPassword(), "La contraseña debería ser encriptada y actualizada");
        assertEquals(Set.of(Role.ADMIN), result.getRoles(), "Los roles deberían actualizarse");
        verify(userRepository, times(1)).findByUsername(currentUsername);
        verify(userRepository, times(1)).save(existingUser);
        verify(passwordEncoder, times(1)).encode(updatedUser.getPassword());
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String currentUsername = "nonexistentuser";
        User updatedUser = new User();

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUser(currentUsername, updatedUser);
        });

        verify(userRepository, times(1)).findByUsername(currentUsername);
        verify(userRepository, times(0)).save(updatedUser);
    }
}
