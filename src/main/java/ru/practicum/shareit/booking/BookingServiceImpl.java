package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long owner, BookingRequest bookingRequest) {
        User user = userRepository.findById(owner)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Товар нельзя забронировать");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());

        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long owner, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getItem().getOwner().equals(owner)) {
            throw new BadRequestException("Пользователь не является владельцем.");
        }
        if (approved) {
            booking.setStatus(State.APPROVED);
        } else {
            booking.setStatus(State.REJECTED);
        }

        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAll(State state, Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (state == State.ALL) {
            return bookingRepository.findByBookerId(userId)
                    .stream()
                    .map(BookingMapper::bookingToBookingDto)
                    .toList();
        } else {
            return bookingRepository.findByBookerIdAndStatus(userId, state)
                    .stream()
                    .map(BookingMapper::bookingToBookingDto)
                    .toList();
        }
    }

    @Override
    public BookingDto getOwnerOrBookerBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));

        if (!booking.getItem().getOwner().equals(userId)) {
            if (!booking.getBooker().getId().equals(userId)) {
                throw new BadRequestException("Пользователь не является владельцем.");
            }
        }

        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByOwner(State state, Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Такой пользователь не найден.");
        }

        if (state == State.ALL) {
            return bookingRepository.findByItemOwner_id(ownerId)
                    .stream()
                    .map(BookingMapper::bookingToBookingDto)
                    .toList();
        } else {
            return bookingRepository.findByItemOwner_idAndStatus(ownerId, state)
                    .stream()
                    .map(BookingMapper::bookingToBookingDto)
                    .toList();
        }
    }
}
