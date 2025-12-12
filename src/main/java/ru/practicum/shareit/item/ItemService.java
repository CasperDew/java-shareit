package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long owner, ItemDto itemDto);

    CommentDto createComment(Long itemId, Long userId, CommentRequest comment);

    ItemDto updateItem(Long id, Long owner, ItemDto itemDto);

    ItemDto getItem(Long id);

    List<ItemDto> getItemsByOwnerId(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);
}
