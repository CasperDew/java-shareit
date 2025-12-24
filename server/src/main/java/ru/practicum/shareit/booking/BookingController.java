package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(HEADER_USER_ID) Long owner,
                                                    @RequestBody BookingRequest booking) {
        log.info("{} создал бронь", owner);
        return ResponseEntity.ok().body(bookingService.create(owner, booking));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateItem(@RequestParam("approved") Boolean approved,
                                 @RequestHeader(HEADER_USER_ID) Long owner,
                                 @PathVariable("bookingId") Long bookingId) {
        log.info("{} обновил бронь с ID {}", owner, bookingId);
        return ResponseEntity.ok().body(bookingService.update(owner, bookingId, approved));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                          @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен список брони пользователя с id: {}", userId);
        return ResponseEntity.ok().body(bookingService.getAll(state, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getOwnerBooking(@PathVariable("bookingId") Long bookingId,
                                      @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Получен владелец забронированного лота");
        return ResponseEntity.ok().body(bookingService.getOwnerOrBookerBooking(bookingId, userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                 @RequestHeader(HEADER_USER_ID) Long ownerId) {
        log.info("Получен список брони по владельцу с id {}", ownerId);
        return ResponseEntity.ok().body(bookingService.getAllByOwner(state, ownerId));
    }
}
