package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.MockData;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest extends TestCase {
    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    MockData mockData;

    @Before
    public void setup(){
        User user = mockData.createUser();

        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(mockData.createOrders());
    }

    @Test
    public void submitSuccess(){

        ResponseEntity<UserOrder> response = orderController.submit("user1");
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertEquals(mockData.createItems(), order.getItems());
        assertEquals(mockData.createUser().getId(), order.getUser().getId());
    }

    @Test
    public void submitNotFoundOder(){
        ResponseEntity<UserOrder> response = orderController.submit("invalid username");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getSucsess(){

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user1");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertEquals(mockData.createOrders().size(), orders.size());

    }

    @Test
    public void getNotFoundOder(){
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("not available name");
        assertEquals(404, response.getStatusCodeValue());
    }
}