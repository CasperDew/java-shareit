package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item = itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, State state) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(state);
        return bookingRepository.save(booking);
    }

    @Test
    void createSuccess() {
        BookingRequest request = new BookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.create(booker.getId(), request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getStart()).isEqualTo(request.getStart());
        assertThat(result.getEnd()).isEqualTo(request.getEnd());
        assertThat(result.getStatus()).isEqualTo(State.WAITING);
        assertThat(bookingRepository.count()).isEqualTo(1);
    }

    @Test
    void createUserNotFound() {
        BookingRequest request = new BookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(999L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void createItemNotFound() {
        BookingRequest request = new BookingRequest();
        request.setItemId(999L);  // несуществующий item
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Предмет не найден");
    }

    @Test
    void createItemNotAvailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingRequest request = new BookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(booker.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Товар нельзя забронировать");
    }

    @Test
    void updateApproveSuccess() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking = bookingRepository.save(booking);

        BookingDto result = bookingService.update(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(State.APPROVED);
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void updateRejectSuccess() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking = bookingRepository.save(booking);

        BookingDto result = bookingService.update(owner.getId(), booking.getId(), false);

        assertThat(result.getStatus()).isEqualTo(State.REJECTED);
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void updateBookingNotFound() {
        assertThatThrownBy(() -> bookingService.update(owner.getId(), 999L, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронь не найдена");
    }

    @Test
    void getAllFilteredByStateSuccess() {
        Booking b1 = createBooking(booker, item, State.APPROVED);
        createBooking(booker, item, State.REJECTED); // не должен попасть

        List<BookingDto> result = bookingService.getAll(State.APPROVED, booker.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(b1.getId());
        assertThat(result.get(0).getStatus()).isEqualTo(State.APPROVED);
    }

    @Test
    void getAllAllStateSuccess() {
        Booking b1 = createBooking(booker, item, State.WAITING);
        Booking b2 = createBooking(booker, item, State.APPROVED);
        Booking b3 = createBooking(booker, item, State.REJECTED);

        List<BookingDto> result = bookingService.getAll(State.ALL, booker.getId());

        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("id")
                .containsExactlyInAnyOrder(b1.getId(), b2.getId(), b3.getId());
    }

    @Test
    void getAllUserNotFound() {
        assertThatThrownBy(() -> bookingService.getAll(State.ALL, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getOwnerOrBookerBookingOwnerAccess_success() {
        Booking booking = createBooking(booker, item, State.WAITING);

        BookingDto result = bookingService.getOwnerOrBookerBooking(booking.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getOwnerOrBookerBookingBookerAccess_success() {
        Booking booking = createBooking(booker, item, State.WAITING);

        BookingDto result = bookingService.getOwnerOrBookerBooking(booking.getId(), booker.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getOwnerOrBookerBookingUnauthorizedUser() {
        User unauthorized = new User();
        unauthorized.setName("Unauthorized");
        unauthorized.setEmail("unauth@test.com");
        unauthorized = userRepository.save(unauthorized);

        Booking booking = createBooking(booker, item, State.WAITING);

        User finalUnauthorized = unauthorized;
        assertThatThrownBy(() ->
                bookingService.getOwnerOrBookerBooking(booking.getId(), finalUnauthorized.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Пользователь не является владельцем.");
    }

    @Test
    void getAllByOwnerAllState_success() {
        Booking b1 = createBooking(booker, item, State.WAITING);
        Booking b2 = createBooking(booker, item, State.APPROVED);

        List<BookingDto> result = bookingService.getAllByOwner(State.ALL, owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("id")
                .containsExactlyInAnyOrder(b1.getId(), b2.getId());
    }
}
