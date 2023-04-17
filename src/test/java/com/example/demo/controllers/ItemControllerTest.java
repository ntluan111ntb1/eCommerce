package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.util.MockData;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    MockData mockData;

    @Test
    public void testGetItems(){
        when(itemRepository.findAll()).thenReturn(mockData.createItems());
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertEquals(mockData.createItems(), items);
    }

    @Test
    public void getItemById(){
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockData.createItem(1)));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        assertEquals(mockData.createItem(1L), item);
    }

    @Test
    public void verify_getItemByName(){
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(mockData.createItem(1), mockData.createItem(2)));
        ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = Arrays.asList(mockData.createItem(1), mockData.createItem(2));
        assertEquals(mockData.createItems(), items);

    }
}