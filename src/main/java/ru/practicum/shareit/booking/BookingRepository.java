package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long id);

    List<Booking> findByItemOwner_id(Long ownerId);

    List<Booking> findByBookerIdAndStatus(Long bookerId, State state);

    List<Booking> findByItemOwner_idAndStatus(Long bookerId, State state);
}
