package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HEADER_USER_ID) Long owner,
                                                @Valid @RequestBody BookingRequest booking) {
        return bookingClient.createBooking(owner, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateItem(@RequestParam ("approved") Boolean approved,
                                             @RequestHeader(HEADER_USER_ID) Long owner,
                                             @PathVariable("bookingId") Long bookingId) {
        return bookingClient.updateItem(owner, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                                @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.getAllBookings(userId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getOwnerBooking(@PathVariable("bookingId") Long bookingId,
                                                  @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                       @RequestHeader(HEADER_USER_ID) Long ownerId) {
        return bookingClient.getAllBookingByOwner(ownerId, state);
    }
}
