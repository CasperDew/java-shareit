package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryItemStorage implements ItemRepository {
    private final AtomicLong getNextId = new AtomicLong(1);
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto createItem(Item item) {
        item.setId(getNextId.getAndIncrement());
        items.put(item.getId(), item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            Item updateItem = items.get(item.getId());

            return ItemMapper.mapToItemDto(ItemMapper.updateFieldsItem(item, updateItem));
        } else {
            throw new NotFoundException("Товар не найден");
        }
    }

    @Override
    public ItemDto getItem(Long id) {
        if (items.containsKey(id)) {
            return ItemMapper.mapToItemDto(items.get(id));
        } else {
            throw new NotFoundException("Товар не найден");
        }
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> text.equalsIgnoreCase(item.getName()) || text.equalsIgnoreCase(item.getDescription()))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
