package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class BookingPeriodValidator implements ConstraintValidator<ValidBookingPeriod, BookingRequest> {
    @Override
    public boolean isValid(BookingRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();

        if (start == null || end == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now) || end.isBefore(now)) {
            return false;
        }

        if (start.isAfter(end)) {
            return false;
        }

        return true;
    }
}
