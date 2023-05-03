package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Objects;

public class CartControllerTest {
  UserRepository userRepository;
  CartRepository cartRepository;
  ItemRepository itemRepository;
  CartController cartController;

  @Before
  public void setUp() {
    userRepository = mock(UserRepository.class);
    cartRepository = mock(CartRepository.class);
    itemRepository = mock(ItemRepository.class);
    cartController = new CartController(userRepository, cartRepository, itemRepository);
    User user = new User();
    user.setId(1);
    user.setUsername("LocAdmin");
    user.setPassword("admin");
    user.setCart(new Cart());
    when(userRepository.findByUsername("LocAdmin")).thenReturn(user);
    Item item = new Item();
    item.setId(1L);
    item.setName("Item 1");
    item.setPrice(BigDecimal.valueOf(3.0));
    item.setDescription("This is Item 1");
    when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
  }


  @Test
  public void addCartOk() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(1);
    request.setQuantity(1);
    request.setUsername("LocAdmin");
    ResponseEntity<Cart> response = cartController.addTocart(request);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(BigDecimal.valueOf(3.0), Objects.requireNonNull(response.getBody()).getTotal());
  }

  @Test
  public void addCartNotFoundUsername() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(1);
    request.setQuantity(1);
    request.setUsername("abc");
    ResponseEntity<Cart> response = cartController.addTocart(request);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void addCartNotFoundItemId() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(2);
    request.setQuantity(1);
    request.setUsername("LocAdmin");
    ResponseEntity<Cart> response = cartController.addTocart(request);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void removeFromCart() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(1);
    request.setQuantity(3);
    request.setUsername("LocAdmin");
    cartController.addTocart(request);
    request = new ModifyCartRequest();
    request.setItemId(1);
    request.setQuantity(1);
    request.setUsername("LocAdmin");
    ResponseEntity<Cart> response = cartController.removeFromcart(request);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(BigDecimal.valueOf(6.0), Objects.requireNonNull(response.getBody()).getTotal());

  }

  @Test
  public void removeFromCartNotFoundUser() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(1);
    request.setQuantity(1);
    request.setUsername("abc");
    ResponseEntity<Cart> response = cartController.removeFromcart(request);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void removeFromCartNotFoundItemId() {
    ModifyCartRequest request = new ModifyCartRequest();
    request.setItemId(2);
    request.setQuantity(1);
    request.setUsername("LocAdmin");
    ResponseEntity<Cart> response = cartController.removeFromcart(request);
    assertEquals(404, response.getStatusCodeValue());
  }
}