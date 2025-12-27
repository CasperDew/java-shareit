package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(JacksonAutoConfiguration.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto();

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("test");
        userDto.setEmail("test@.com");

        UserDto savedDto = new UserDto();
        savedDto.setId(1L);
        savedDto.setName(userDto.getName());
        savedDto.setEmail(userDto.getEmail());
        when(userService.createUser(any())).thenReturn(savedDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@.com")));
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
        when(userService.updateUser(any())).thenReturn(updatedDto);


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

        when(userService.getUsers()).thenReturn(users);

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


        when(userService.getUserById(1L)).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("test@com")));
    }

    @Test
    void deleteUserForId() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());

        verify(userService, times(1)).deleteUserById(1L);
    }
}
