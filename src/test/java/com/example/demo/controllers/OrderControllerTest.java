package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderControllerTest {
  UserRepository userRepository;
  OrderRepository orderRepository;
  OrderController orderController;

  @Before
  public void setUp() {
    userRepository = mock(UserRepository.class);
    orderRepository = mock(OrderRepository.class);
    orderController = new OrderController(userRepository, orderRepository);
    Item item = new Item();
    item.setId(1L);
    item.setName("Item 1");
    item.setPrice(BigDecimal.valueOf(3.0));
    item.setDescription("This is Item 1");
    List<Item> items = new ArrayList<>();
    items.add(item);
    User user = new User();
    Cart cart = new Cart();
    user.setId(1);
    user.setUsername("LocAdmin");
    user.setPassword("admin");
    cart.setId(1L);
    cart.setUser(user);
    cart.setItems(items);
    cart.setTotal(BigDecimal.valueOf(3.0));
    user.setCart(cart);
    when(userRepository.findByUsername("LocAdmin")).thenReturn(user);
  }

  @Test
  public void submit() {
    ResponseEntity<UserOrder> response = orderController.submit("LocAdmin");
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, Objects.requireNonNull(response.getBody()).getItems().size());
  }

  @Test
  public void submitNotFound() {
    ResponseEntity<UserOrder> response = orderController.submit("abc");
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void getOrdersForUser() {
    ResponseEntity<List<UserOrder>> orders = orderController.getOrdersForUser("LocAdmin");
    assertEquals(200, orders.getStatusCodeValue());
  }

  @Test
  public void getOrdersForUserNotFound() {
    ResponseEntity<List<UserOrder>> orders = orderController.getOrdersForUser("abc");
    assertEquals(404, orders.getStatusCodeValue());

  }
}