package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    ItemDto createItem(Item item);

    ItemDto updateItem(Item item);

    ItemDto getItem(Long id);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);
}
