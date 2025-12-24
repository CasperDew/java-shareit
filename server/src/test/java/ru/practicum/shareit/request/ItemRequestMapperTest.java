package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {
    @Test
    void testItemRequestToDtoAllFieldsCopied() {
        User user = new User();
        user.setId(200L);
        user.setName("User1");
        user.setEmail("user1@test.com");

        UserDto userDto = UserMapper.mapToUSerDto(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(100L);
        itemRequest.setDescription("Нужно купить лампу");
        itemRequest.setUserId(user);
        itemRequest.setCreated(LocalDateTime.of(2025, 12, 7, 10, 0));

        ItemRequestDto dto = ItemRequestMapper.itemRequestToDto(itemRequest);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getDescription()).isEqualTo("Нужно купить лампу");
        assertThat(dto.getUserId()).isEqualTo(userDto);
        assertThat(dto.getCreated())
                .isEqualTo(LocalDateTime.of(2025, 12, 7, 10, 0));
    }

    @Test
    void testItemRequestToDtoNullFieldsHandled() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(null);
        itemRequest.setDescription(null);
        itemRequest.setUserId(null);
        itemRequest.setCreated(null);

        ItemRequestDto dto = ItemRequestMapper.itemRequestToDto(itemRequest);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCreated()).isNull();
    }

    @Test
    void testItemRequestToDtoOriginalObjectNotModified() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Тест");

        Long originalId = itemRequest.getId();
        String originalDesc = itemRequest.getDescription();

        ItemRequestDto dto = ItemRequestMapper.itemRequestToDto(itemRequest);

        assertThat(itemRequest.getId()).isEqualTo(originalId);
        assertThat(itemRequest.getDescription()).isEqualTo(originalDesc);
    }
}
