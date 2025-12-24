package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        userRepository.save(booker);

        item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        itemRepository.save(item);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(State.APPROVED);
        bookingRepository.save(booking);
    }

    @Test
    void createItemSuccess() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);


        ItemDto result = itemService.createItem(owner.getId(), itemDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(itemRepository.count()).isEqualTo(2);
    }

    @Test
    void createItemUserNotFound() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("test");
        itemDto.setAvailable(true);

        assertThatThrownBy(() -> itemService.createItem(999L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не существует");
    }

    @Test
    void createCommentItemNotFound() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("test");

        assertThatThrownBy(() -> itemService.createComment(999L, booker.getId(), commentRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Такого товара не найдено");
    }

    @Test
    void createCommentUserNotFound() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("Test");

        assertThatThrownBy(() -> itemService.createComment(item.getId(), 999L, commentRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Такого пользователя не существует");
    }

    @Test
    void createCommentNoBooking() {
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@test.com");

        userRepository.save(otherUser);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("No booking!");

        assertThatThrownBy(() -> itemService.createComment(item.getId(), otherUser.getId(), commentRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Такого бронирования у пользователя не найдено.");
    }

    @Test
    void updateItemSuccess() {
        ItemDto updatedDto = new ItemDto();
        updatedDto.setName("Updated Name");
        updatedDto.setDescription("Updated Desc");
        updatedDto.setAvailable(false);

        ItemDto result = itemService.updateItem(item.getId(), owner.getId(), updatedDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItemUserNotFound() {
        ItemDto updatedDto = new ItemDto();
        updatedDto.setName("Test");

        assertThatThrownBy(() -> itemService.updateItem(item.getId(), 999L, updatedDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не существует");
    }

    @Test
    void getItemSuccess() {
        ItemDto result = itemService.getItem(item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Item1");
        assertThat(result.getComments()).isEmpty(); // Пока нет комментариев
    }

    @Test
    void getItemNotFound() {
        assertThatThrownBy(() -> itemService.getItem(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Такого товара не найдено");
    }

    @Test
    void getItemsForOwnerSuccess() {
        List<ItemDto> items = itemService.getItemsByOwnerId(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Item1");
    }

    @Test
    void getAllItemsForTextSuccess() {
        List<ItemDto> items = itemService.getAllItemsByText("Item1");

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Item1");
    }

    @Test
    void getAllItemsForTextEmptyText() {
        List<ItemDto> items = itemService.getAllItemsByText("");
        assertThat(items).isEmpty();
    }
}
