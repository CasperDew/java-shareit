package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {
    @Test
    void bookingToBookingDtoStartAndEndNull() {
        Item item = new Item();
        item.setId(40L);

        User booker = new User();
        booker.setId(50L);

        Booking booking = new Booking();
        booking.setId(500L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(null);
        booking.setEnd(null);
        booking.setStatus(State.WAITING);

        BookingDto dto = BookingMapper.bookingToBookingDto(booking);

        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void bookingToBookingDtoStatusIsNull() {
        Item item = new Item();
        item.setId(60L);

        User booker = new User();
        booker.setId(70L);

        Booking booking = new Booking();
        booking.setId(600L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(null);

        BookingDto dto = BookingMapper.bookingToBookingDto(booking);

        assertThat(dto.getStatus()).isNull();
    }
}
