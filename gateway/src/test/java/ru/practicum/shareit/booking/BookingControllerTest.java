package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@Import(JacksonAutoConfiguration.class)
public class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private BookingRequest bookingRequest;
    private BookingDto bookingDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(itemDto);
        bookingDto.setStart(LocalDateTime.parse("2035-12-04T16:38:09.4506538"));
        bookingDto.setEnd(LocalDateTime.parse("2035-12-05T16:38:09.4506538"));

        bookingRequest = new BookingRequest();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.parse("2035-12-04T16:38:09.4506538"));
        bookingRequest.setEnd(LocalDateTime.parse("2035-12-05T16:38:09.4506538"));
    }

    @Test
    void createBooking() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ofNullable(bookingDto);

        when(bookingClient.createBooking(anyLong(),any())).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2035-12-04T16:38:09.4506538")))
                .andExpect(jsonPath("$.end", is("2035-12-05T16:38:09.4506538")))
                .andExpect(jsonPath("$.item.id", is(1)));
    }

    @Test
    void createBookingWhenDateStartAfterEnd() throws Exception {
        bookingRequest.setStart(LocalDateTime.parse("2045-12-04T16:38:09.4506538"));

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingWhenDateEndBeforeStart() throws Exception {
        bookingRequest.setEnd(LocalDateTime.parse("2030-12-04T16:38:09.4506538"));

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingWhenDateInThePast() throws Exception {
        bookingRequest.setEnd(LocalDateTime.parse("2020-12-04T16:38:09.4506538"));
        bookingRequest.setEnd(LocalDateTime.parse("2020-12-05T16:38:09.4506538"));

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ofNullable(bookingDto);

        when(bookingClient.updateItem(anyLong(), anyLong(), any())).thenReturn(response);

        mvc.perform(patch("/bookings/1")
                        .header(HEADER_USER_ID, "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2035-12-04T16:38:09.4506538")))
                .andExpect(jsonPath("$.end", is("2035-12-05T16:38:09.4506538")))
                .andExpect(jsonPath("$.item.id", is(1)));
    }

    @Test
    void getAllBooking() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(2L);
        bookingDto1.setItem(itemDto);
        bookingDto1.setStart(LocalDateTime.parse("2035-12-04T11:38:09.4506538"));
        bookingDto1.setEnd(LocalDateTime.parse("2035-12-04T12:38:09.4506538"));

        List<BookingDto> bookings = List.of(bookingDto, bookingDto1);

        ResponseEntity<Object> response = ResponseEntity.ofNullable(bookings);

        when(bookingClient.getAllBookings(anyLong(), any())).thenReturn(response);

        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, "1")
                        .param("approved", "true")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id", is(1)))
                .andExpect(jsonPath("$.[0]start", is("2035-12-04T16:38:09.4506538")))
                .andExpect(jsonPath("$.[0]end", is("2035-12-05T16:38:09.4506538")))
                .andExpect(jsonPath("$.[0]item.id", is(1)))
                .andExpect(jsonPath("$.[1]id", is(2)))
                .andExpect(jsonPath("$.[1]start", is("2035-12-04T11:38:09.4506538")))
                .andExpect(jsonPath("$.[1]end", is("2035-12-04T12:38:09.4506538")))
                .andExpect(jsonPath("$.[1]item.id", is(1)));
    }

    @Test
    void getOwnerBooking() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ofNullable(bookingDto);

        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(response);

        mvc.perform(get("/bookings/1")
                        .header(HEADER_USER_ID, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2035-12-04T16:38:09.4506538")))
                .andExpect(jsonPath("$.end", is("2035-12-05T16:38:09.4506538")))
                .andExpect(jsonPath("$.item.id", is(1)));
    }

    @Test
    void getAllBookingByOwner() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(2L);
        bookingDto1.setItem(itemDto);
        bookingDto1.setStart(LocalDateTime.parse("2035-12-04T11:38:09.4506538"));
        bookingDto1.setEnd(LocalDateTime.parse("2035-12-04T12:38:09.4506538"));

        List<BookingDto> bookings = List.of(bookingDto, bookingDto1);

        ResponseEntity<Object> response = ResponseEntity.ofNullable(bookings);

        when(bookingClient.getAllBookingByOwner(anyLong(), any())).thenReturn(response);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, "1")
                        .param("approved", "true")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id", is(1)))
                .andExpect(jsonPath("$.[0]start", is("2035-12-04T16:38:09.4506538")))
                .andExpect(jsonPath("$.[0]end", is("2035-12-05T16:38:09.4506538")))
                .andExpect(jsonPath("$.[0]item.id", is(1)))
                .andExpect(jsonPath("$.[1]id", is(2)))
                .andExpect(jsonPath("$.[1]start", is("2035-12-04T11:38:09.4506538")))
                .andExpect(jsonPath("$.[1]end", is("2035-12-04T12:38:09.4506538")))
                .andExpect(jsonPath("$.[1]item.id", is(1)));
    }
}
