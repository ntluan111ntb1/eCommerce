package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Objects;
import java.util.Optional;

public class UserControllerTest {
  UserRepository userRepository;
  CartRepository cartRepository;
  BCryptPasswordEncoder bCryptPasswordEncoder;
  UserController userController;

  @Before
  public void setUp() {
    userRepository = mock(UserRepository.class);
    cartRepository = mock(CartRepository.class);
    bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    User user = new User();
    user.setId(1);
    user.setUsername("LocNB3");
    user.setPassword("LocAdmin");
    user.setCart(new Cart());
    when(userRepository.findByUsername("LocNB3")).thenReturn(user);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bCryptPasswordEncoder.encode("LocAdmin")).thenReturn("LocAdminEncode");
  }

  @Test
  public void findById() {
    ResponseEntity<User> response = userController.findById(1L);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, Objects.requireNonNull(response.getBody()).getId());
  }

  @Test
  public void findByIdNotFound() {
    ResponseEntity<User> response = userController.findById(2L);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void findByUserName() {
    ResponseEntity<User> response = userController.findByUserName("LocNB3");
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("LocNB3", Objects.requireNonNull(response.getBody()).getUsername());
  }

  @Test
  public void findByUserNameNotFound() {
    ResponseEntity<User> response = userController.findByUserName("abc");
    assertEquals(404, response.getStatusCodeValue());
  }


  @Test
  public void createUser() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("LocNB3");
    request.setPassword("LocAdmin");
    request.setConfirmPassword("LocAdmin");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(200, response.getStatusCodeValue());
    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("LocNB3", user.getUsername());
    assertEquals("LocAdminEncode", user.getPassword());
  }

  @Test
  public void createUserBadRequest() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("LocNB3");
    request.setPassword("admin");
    request.setConfirmPassword("admin");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void create_user_password_confirm_mismatch() {
    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("LocNB3");
    request.setPassword("admin");
    request.setConfirmPassword("Admin");
    ResponseEntity<User> response = userController.createUser(request);
    assertEquals(400, response.getStatusCodeValue());
  }
}