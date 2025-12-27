package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@Import(JacksonAutoConfiguration.class)
public class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto2;
    private UserDto user;

    @BeforeEach
    void setUp() {
        user = new UserDto();
        user.setId(1L);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setUserId(user);
        itemRequestDto.setCreated(LocalDateTime.parse("2025-12-06T16:38:09.4506538"));

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("description2");
        itemRequestDto2.setUserId(user);
        itemRequestDto2.setCreated(LocalDateTime.parse("2025-12-06T17:38:09.4506538"));
    }

    @Test
    void createRequestItem() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is("2025-12-06T16:38:09.4506538")));
    }

    @Test
    void getUserRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(itemRequestDto, itemRequestDto2);

        when(itemRequestService.getUserRequests(anyLong())).thenReturn(requests);

        mvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id", is(1)))
                .andExpect(jsonPath("$.[0]userId.id", is(1)))
                .andExpect(jsonPath("$.[0]description", is("description")))
                .andExpect(jsonPath("$.[0]created", is("2025-12-06T16:38:09.4506538")))
                .andExpect(jsonPath("$.[1]id", is(2)))
                .andExpect(jsonPath("$.[1]userId.id", is(1)))
                .andExpect(jsonPath("$.[1]description", is("description2")))
                .andExpect(jsonPath("$.[1]created", is("2025-12-06T17:38:09.4506538")));
    }

    @Test
    void getRequestId() throws Exception {
        when(itemRequestService.getItemRequestForId(anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is("2025-12-06T16:38:09.4506538")));
    }

    @Test
    void getAllRequest() throws Exception {
        List<ItemRequestDto> requests = List.of(itemRequestDto, itemRequestDto2);

        when(itemRequestService.getAllRequest()).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id", is(1)))
                .andExpect(jsonPath("$.[0]userId.id", is(1)))
                .andExpect(jsonPath("$.[0]description", is("description")))
                .andExpect(jsonPath("$.[0]created", is("2025-12-06T16:38:09.4506538")))
                .andExpect(jsonPath("$.[1]id", is(2)))
                .andExpect(jsonPath("$.[1]userId.id", is(1)))
                .andExpect(jsonPath("$.[1]description", is("description2")))
                .andExpect(jsonPath("$.[1]created", is("2025-12-06T17:38:09.4506538")));
    }
}
