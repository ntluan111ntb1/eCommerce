package com.example.demo.controllers;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginRepsonse;
import com.example.demo.model.requests.LoginRequest;
import com.example.demo.security.Constants;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository = mock(UserRepository.class);

    @Mock
    private CartRepository cartRepository = mock(CartRepository.class);

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Mock
    private AuthenticationManager authenticationManager;

    public void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
                wasPrivate = true;
            }
            declaredField.set(target, toInject);
            if(wasPrivate){
                declaredField.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        injectObjects(userController, "userRepository", userRepository);
        injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        injectObjects(userController, "cartRepository", cartRepository);
    }

    @Test
    public void testLoginSuccess() {
        // Mocking
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        when(userRepository.findByUsername("test")).thenReturn(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken("test", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        // Testing
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test");
        loginRequest.setPassword("password");
        ResponseEntity<LoginRepsonse> responseEntity = userController.login(loginRequest);
        assertEquals(responseEntity.getStatusCode().value(), 200);
    }

    @Test
    public void loginNotValidValue() {
        Mockito.lenient().when(bCryptPasswordEncoder.encode("password")).thenReturn("passwordEncode");
        when(userRepository.findByUsername(any())).thenReturn(null);
        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("password");
        BadRequestException exception = Assertions.catchThrowableOfType(() -> userController.login(request), BadRequestException.class);
        assertEquals("Username or password is incorrect", exception.getMessage());
    }

    @Test
    public void createUserSuccess() {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("passwordEncode");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setConfirmPassword("password");
        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("passwordEncode", user.getPassword());
    }
    @Test
    public void createUserPasswordIsNot7Characters() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("a");
        request.setConfirmPassword("a");
        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    public void createUserConfirmPasswordNotMatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setConfirmPassword("confirmPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void verify_findById() {
        long id = 1L;
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(id);
        assertEquals(200, response.getStatusCodeValue());
        User getUser = response.getBody();
        assertEquals(id, getUser.getId());
        assertEquals("test", getUser.getUsername());
        assertEquals("password", getUser.getPassword());
    }

    @Test
    public void verify_findByUserName() {
        long id = 1L;
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setId(id);
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("test");
        assertEquals(200, response.getStatusCodeValue());
        User getUser = response.getBody();
        assertEquals(id, getUser.getId());
        assertEquals("test", getUser.getUsername());
        assertEquals("password", getUser.getPassword());
    }
}