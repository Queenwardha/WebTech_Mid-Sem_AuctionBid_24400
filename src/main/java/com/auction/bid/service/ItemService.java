package com.auction.bid.service;

import com.auction.bid.model.Item;
import com.auction.bid.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }



    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public void saveItem(Item item, MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                item.setPhoto(file.getBytes());
            }
            itemRepository.save(item);
        } catch (IOException e) {
            System.err.println("Error saving item photo: " + e.getMessage());
        }
    }

    public void updateItem(Long id, Item updatedItem, MultipartFile file) {
        Optional<Item> existingItemOpt = getItemById(id);
        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            existingItem.setName(updatedItem.getName());
            existingItem.setPrice(updatedItem.getPrice());
            try {
                if (!file.isEmpty()) {
                    existingItem.setPhoto(file.getBytes());
                }
            } catch (IOException e) {
                System.err.println("Error updating item photo: " + e.getMessage());
            }
            itemRepository.save(existingItem);
        }
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> searchItems(String search) {
        return itemRepository.findByNameContainingIgnoreCase(search);
    }
}
