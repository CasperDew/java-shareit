package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(HEADER_USER_ID) String owner,
                                    @Valid @RequestBody BookingRequest booking) {
        log.info("{} создал бронь", owner);
        return bookingService.create(Long.parseLong(owner), booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateItem(@RequestParam ("approved") Boolean approved,
                                 @RequestHeader(HEADER_USER_ID) String owner,
                                 @PathVariable("bookingId") Long bookingId) {
        log.info("{} обновил бронь с ID {}", owner, bookingId);
        return bookingService.update(Long.parseLong(owner), bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                          @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен список брони пользователя с id: {}", userId);
        return bookingService.getAll(state, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOwnerBooking(@PathVariable("bookingId") Long bookingId,
                                      @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен владелец забронированного лота");
        return bookingService.getOwnerOrBookerBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                 @RequestHeader(HEADER_USER_ID) Long ownerId) {
        log.info("Получен список брони по владельцу с id {}", ownerId);
        return bookingService.getAllByOwner(state, ownerId);
    }
}
