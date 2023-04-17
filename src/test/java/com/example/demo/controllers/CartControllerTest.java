package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.util.MockData;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    MockData mockData;

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup() {

        when(userRepository.findByUsername("user1")).thenReturn(mockData.createUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(mockData.createItem(1)));

    }

    @Test
    public void addToCart_success() {
        when(userRepository.findByUsername("user1")).thenReturn(mockData.createUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(mockData.createItem(1)));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("user1");

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        Cart generatedCart = mockData.createCart(mockData.createUser());

        assertNotNull(actualCart);

        Item item = mockData.createItem(request.getItemId());
        BigDecimal itemPrice = item.getPrice();

        BigDecimal expectedTotal = itemPrice.multiply(BigDecimal.valueOf(request.getQuantity())).add(generatedCart.getTotal());

        assertEquals("user1", actualCart.getUser().getUsername());
        assertEquals(generatedCart.getItems().size() + request.getQuantity(), actualCart.getItems().size());
        assertEquals(mockData.createItem(1), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());
    }

    @Test
    public void addToCart_notFoundUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("user1");
        User user = new User();
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCart_notFoundItems() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("user1");
        User user = new User();
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verify_removeFromCart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("user1");

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        Cart cart = response.getBody();
        Cart mockDataCart = mockData.createCart(mockData.createUser());

        Item item = mockData.createItem(modifyCartRequest.getItemId());
        BigDecimal itemPrice = item.getPrice();
        BigDecimal total = mockDataCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(modifyCartRequest.getQuantity())));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user1", cart.getUser().getUsername());
        assertEquals(mockDataCart.getItems().size() - modifyCartRequest.getQuantity(), cart.getItems().size());
        assertEquals(mockData.createItem(2), cart.getItems().get(0));
        assertEquals(total, cart.getTotal());
    }

    @Test
    public void removeFromCart_notFoundUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("user1");
        User user = new User();
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_notFoundItems() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("user1");
        User user = new User();
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
    }
}