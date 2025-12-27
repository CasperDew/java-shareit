package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@Import(JacksonAutoConfiguration.class)
public class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto itemDto2;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item");
        itemDto.setDescription("test");
        itemDto.setAvailable(true);

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("item2");
        itemDto2.setDescription("test2");
    }

    @Test
    void createItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ofNullable(itemDto);

        when(itemClient.createItem(anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("test")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void createItemWhenNameIsEmpty() throws Exception {
        itemDto.setName("");
        ResponseEntity<Object> response = ResponseEntity.ofNullable(itemDto);

        when(itemClient.createItem(anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemWhenDescriptionIsEmpty() throws Exception {
        itemDto.setDescription("");
        ResponseEntity<Object> response = ResponseEntity.ofNullable(itemDto);

        when(itemClient.createItem(anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItemWhenAvailableIsEmpty() throws Exception {
        itemDto.setAvailable(null);
        ResponseEntity<Object> response = ResponseEntity.ofNullable(itemDto);

        when(itemClient.createItem(anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment() throws Exception {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text");
        commentDto.setAuthorName("name");
        commentDto.setCreated(LocalDateTime.parse("2025-12-06T16:38:09.4506538"));

        ResponseEntity<Object> response = ResponseEntity.ofNullable(commentDto);

        when(itemClient.createComment(anyLong(),anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("text")))
                .andExpect(jsonPath("$.authorName", is("name")))
                .andExpect(jsonPath("$.created", is("2025-12-06T16:38:09.4506538")));
    }

    @Test
    void createCommentWhenTextIsEmpty() throws Exception {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorName("name");
        commentDto.setCreated(LocalDateTime.parse("2025-12-06T16:38:09.4506538"));

        ResponseEntity<Object> response = ResponseEntity.ofNullable(commentDto);

        when(itemClient.createComment(anyLong(),anyLong(),any())).thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updateItem = new ItemDto();
        updateItem.setId(1L);
        updateItem.setName("updateItem");
        updateItem.setDescription("testUpdate");

        ResponseEntity<Object> response = ResponseEntity.ofNullable(updateItem);

        when(itemClient.updateItem(anyLong(),anyLong(),any())).thenReturn(response);

        mvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("updateItem")))
                .andExpect(jsonPath("$.description", is("testUpdate")));
    }

    @Test
    void getItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ofNullable(itemDto);

        when(itemClient.getItemForId(anyLong())).thenReturn(response);

        mvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("item")))
                .andExpect(jsonPath("$.description", is("test")));
    }

    @Test
    void getItemsOwner() throws Exception {
        List<ItemDto> items = List.of(itemDto,itemDto2);

        ResponseEntity<Object> response = ResponseEntity.ofNullable(items);

        when(itemClient.getItemsOwner(anyLong())).thenReturn(response);

        mvc.perform(get("/items")
                        .header(HEADER_USER_ID, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].description", is("test")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2")))
                .andExpect(jsonPath("$[1].description", is("test2")));
    }

    @Test
    void getItemsForText() throws Exception {
        List<ItemDto> items = List.of(itemDto,itemDto2);

        ResponseEntity<Object> response = ResponseEntity.ofNullable(items);

        when(itemClient.getItemForText(anyLong(), anyString())).thenReturn(response);

        mvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, "1")
                        .param("text","test")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item")))
                .andExpect(jsonPath("$[0].description", is("test")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2")))
                .andExpect(jsonPath("$[1].description", is("test2")));
    }
}
