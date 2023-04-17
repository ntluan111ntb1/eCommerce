package com.example.demo.controllers;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import com.auth0.jwt.JWT;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginRepsonse;
import com.example.demo.model.requests.LoginRequest;
import com.example.demo.security.Constants;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
//    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LogManager.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginRepsonse> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            log.error("LOGIN: FAIL, Username or password is incorrect");
            throw new BadRequestException("Username or password is incorrect");
        }
        Authentication authentication  = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JWT.create()
            .withSubject(loginRequest.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))
            .sign(HMAC512(Constants.SECRET));
        log.info("LOGIN: SUCCESS for user {} : ", loginRequest.getUsername());
        return ResponseEntity.ok(new LoginRepsonse(token));
    }

    public UserController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
        if (createUserRequest.getPassword().length() < 7) {
            log.error("CREATE USER: FAIL for user: {}, Password must be at least 7 characters.", createUserRequest.getUsername());
            return ResponseEntity.badRequest().body("Password must be at least 7 characters.");
        } else if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("CREATE USER: FAIL for user: {}, Password field does not match confirm password field", createUserRequest.getUsername());
            return ResponseEntity.badRequest().body("");
        }
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        userRepository.save(user);

        log.info("CREATE USER: SUCCESS for user {}", user.getUsername() );

        return ResponseEntity.ok(user);

    }



}
