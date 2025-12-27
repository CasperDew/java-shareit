package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(JacksonAutoConfiguration.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("test");
        userDto.setEmail("test@gmail.com");

        UserDto savedDto = new UserDto();
        savedDto.setId(1L);
        savedDto.setName(userDto.getName());
        savedDto.setEmail(userDto.getEmail());

        ResponseEntity<Object> response = ResponseEntity.ofNullable(savedDto);

        when(userClient.createUser(any())).thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@gmail.com")));
    }

    @Test
    void createUserWhenNameIsEmpty() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("test@gmail.com");

        UserDto savedDto = new UserDto();
        savedDto.setId(1L);
        savedDto.setName(userDto.getName());
        savedDto.setEmail(userDto.getEmail());

        ResponseEntity<Object> response = ResponseEntity.ofNullable(savedDto);

        when(userClient.createUser(any())).thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWhenEmailNotValid() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("test@.com");

        UserDto savedDto = new UserDto();
        savedDto.setId(1L);
        savedDto.setName(userDto.getName());
        savedDto.setEmail(userDto.getEmail());

        ResponseEntity<Object> response = ResponseEntity.ofNullable(savedDto);

        when(userClient.createUser(any())).thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("updatedName");
        userDto.setEmail("updated@test.com");

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName(userDto.getName());
        updatedDto.setEmail(userDto.getEmail());

        ResponseEntity<Object> response = ResponseEntity.ofNullable(updatedDto);

        when(userClient.updateUser(anyLong(), any())).thenReturn(response);


        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("updatedName")))
                .andExpect(jsonPath("$.email", is("updated@test.com")));
    }

    @Test
    void getUsers() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("User1");
        user1.setEmail("user1@test.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("User2");
        user2.setEmail("user2@test.com");

        List<UserDto> users = List.of(user1, user2);

        ResponseEntity<Object> response = ResponseEntity.ofNullable(users);

        when(userClient.getUsers()).thenReturn(response);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("User1")))
                .andExpect(jsonPath("$[0].email", is("user1@test.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("User2")))
                .andExpect(jsonPath("$[1].email", is("user2@test.com")));
    }


    @Test
    void getUserForId() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@com");

        ResponseEntity<Object> response = ResponseEntity.ofNullable(userDto);

        when(userClient.getUserForId(1L)).thenReturn(response);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@com")));
    }
}
