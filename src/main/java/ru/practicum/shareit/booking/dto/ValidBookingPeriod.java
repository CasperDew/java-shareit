package ru.practicum.shareit.booking.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingPeriodValidator.class)
@Documented
public @interface ValidBookingPeriod {
    String message() default "Бронирование некорректно: даты должны быть в будущем, начало ≤ конец";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
