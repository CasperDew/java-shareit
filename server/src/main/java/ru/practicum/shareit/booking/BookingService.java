package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(Long owner, BookingRequest booking);

    BookingDto update(Long owner, Long bookingId, Boolean approved);

    List<BookingDto> getAll(State state, Long userId);

    BookingDto getOwnerOrBookerBooking(Long bookingId, Long userId);

    List<BookingDto> getAllByOwner(State state, Long ownerId);
}
