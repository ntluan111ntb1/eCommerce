package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  //  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private static final Logger log = LogManager.getLogger(UserController.class);

  public UserController(UserRepository userRepository,
                        CartRepository cartRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.cartRepository = cartRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<User> findById(@PathVariable Long id) {
    log.info("FindById called with id {}", id);
    return ResponseEntity.of(userRepository.findById(id));
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> findByUserName(@PathVariable String username) {
    log.info("FindByUserName called with username {}", username);
    User user = userRepository.findByUsername(username);
    return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
  }

  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
    String username = createUserRequest.getUsername();
    String password = createUserRequest.getPassword();
    log.info("Creating user {}", username);
    User user = new User();
    user.setUsername(username);
    Cart cart = new Cart();
    cartRepository.save(cart);
    user.setCart(cart);
    if (password.length() < 7 ||
        !password.equals(createUserRequest.getConfirmPassword())) {
      log.error("Error with user password. Cannot create user {}", username);
      return ResponseEntity.badRequest().build();
    }
    user.setPassword(bCryptPasswordEncoder.encode(password));
    userRepository.save(user);
    log.info("New user {} created", username);
    return ResponseEntity.ok(user);
  }

}
