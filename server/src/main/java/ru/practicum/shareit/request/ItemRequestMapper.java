package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.Optional;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setUserId(
                Optional.ofNullable(itemRequest.getUserId())
                        .map(UserMapper::mapToUSerDto)
                        .orElse(null)
        );
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }
}
