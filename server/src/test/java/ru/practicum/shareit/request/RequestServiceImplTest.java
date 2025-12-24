package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceImplTest {
    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Requester");
        user.setEmail("requester@test.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item = itemRepository.save(item);
    }

    @Test
    void testCreateItemRequestSuccess() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need this item");

        ItemRequestDto result = requestService.createItemRequest(user.getId(), requestDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need this item");
        assertThat(result.getCreated()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        assertThat(itemRequestRepository.count()).isEqualTo(1);
    }

    @Test
    void testCreateItemRequestUserNotFound() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test");

        assertThatThrownBy(() -> requestService.createItemRequest(999L, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найде");
    }

    @Test
    void testGetUserRequestsSuccess() {
        ItemRequestDto req1 = new ItemRequestDto();
        req1.setDescription("Req 1");
        ItemRequestDto req2 = new ItemRequestDto();
        req2.setDescription("Req 2");
        requestService.createItemRequest(user.getId(), req1);
        requestService.createItemRequest(user.getId(), req2);

        List<ItemRequestDto> requests = requestService.getUserRequests(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Req 1");
        assertThat(requests.get(1).getDescription()).isEqualTo("Req 2");
    }

    @Test
    void testGetItemRequestForIdSuccess() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Urgent request");
        ItemRequestDto createdRequest = requestService.createItemRequest(user.getId(), requestDto);

        item.setRequestId(itemRequestRepository.findById(createdRequest.getId()).get().getId());
        itemRepository.save(item);


        ItemRequestDto result = requestService.getItemRequestForId(createdRequest.getId());

        assertThat(result.getId()).isEqualTo(createdRequest.getId());
        assertThat(result.getDescription()).isEqualTo("Urgent request");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Test Item");
    }

    @Test
    void testGetItemRequestForIdRequestNotFound() {
        assertThatThrownBy(() -> requestService.getItemRequestForId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос не существует");
    }

    @Test
    void testGetAllRequestSuccess() {
        ItemRequestDto req1 = new ItemRequestDto();
        req1.setDescription("All 1");
        ItemRequestDto req2 = new ItemRequestDto();
        req2.setDescription("All 2");
        requestService.createItemRequest(user.getId(), req1);
        requestService.createItemRequest(user.getId(), req2);

        List<ItemRequestDto> allRequests = requestService.getAllRequest();

        assertThat(allRequests).hasSize(2);
        assertThat(allRequests.get(0).getDescription()).isEqualTo("All 1");
        assertThat(allRequests.get(1).getDescription()).isEqualTo("All 2");
    }
}
