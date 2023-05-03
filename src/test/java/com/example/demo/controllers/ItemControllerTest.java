package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemControllerTest {
  ItemRepository itemRepository;
  ItemController itemController;

  @Before
  public void setUp() {
    itemRepository = mock(ItemRepository.class);
    itemController = new ItemController(itemRepository);
    Item item = new Item();
    item.setId(1L);
    item.setName("Item 1");
    item.setPrice(BigDecimal.valueOf(3.0));
    item.setDescription("This is Item 1");
    when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(itemRepository.findByName("Item 1")).thenReturn(Collections.singletonList(item));
  }

  @Test
  public void getItems() {
    ResponseEntity<List<Item>> response = itemController.getItems();
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  public void getItemById() {
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Item 1", Objects.requireNonNull(response.getBody()).getName());
  }

  @Test
  public void getItemsByName() {
    ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, Objects.requireNonNull(response.getBody()).size());
  }

  @Test
  public void getItemsByNameNotFound() {
    ResponseEntity<List<Item>> response = itemController.getItemsByName("abc");
    assertEquals(404, response.getStatusCodeValue());
  }
}