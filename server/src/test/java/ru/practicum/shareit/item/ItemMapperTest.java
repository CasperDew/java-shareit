package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {
    @Test
    void mapToItemAllFieldsCopied() {
        ItemDto dto = new ItemDto();
        dto.setName("Laptop");
        dto.setDescription("Powerful laptop");
        dto.setAvailable(true);
        dto.setRequestId(10L);

        Item item = ItemMapper.mapToItem(5L, dto);

        assertThat(item).isNotNull();
        assertThat(item.getOwner()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Laptop");
        assertThat(item.getDescription()).isEqualTo("Powerful laptop");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(10L);
    }

    @Test
    void mapToItemNullFieldsHandled() {
        ItemDto dto = new ItemDto();
        dto.setName(null);
        dto.setDescription(null);
        dto.setAvailable(null);
        dto.setRequestId(null);

        Item item = ItemMapper.mapToItem(7L, dto);

        assertThat(item.getOwner()).isEqualTo(7L);
        assertThat(item.getName()).isNull();
        assertThat(item.getDescription()).isNull();
        assertThat(item.getAvailable()).isNull();
        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void mapToItemDtoAllFieldsCopied() {
        Item item = new Item();
        item.setId(100L);
        item.setName("Mouse");
        item.setDescription("Wireless mouse");
        item.setAvailable(false);
        item.setRequestId(20L);

        ItemDto dto = ItemMapper.mapToItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getName()).isEqualTo("Mouse");
        assertThat(dto.getDescription()).isEqualTo("Wireless mouse");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getRequestId()).isEqualTo(20L);
        assertThat(dto.getComments()).isNull();
    }

    @Test
    void mapToItemDtoNullFieldsHandled() {
        Item item = new Item();
        item.setId(null);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        item.setRequestId(null);

        ItemDto dto = ItemMapper.mapToItemDto(item);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getAvailable()).isNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void mapToItemDtoWithCommentsAllFieldsAndComments() {
        Item item = new Item();
        item.setId(200L);
        item.setName("Keyboard");
        item.setDescription("Mechanical keyboard");
        item.setAvailable(true);

        CommentDto comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("Great keyboard!");
        comment1.setAuthorName("Alice");
        comment1.setCreated(LocalDateTime.now());

        List<CommentDto> comments = List.of(comment1);

        ItemDto dto = ItemMapper.mapToItemDtoWithComments(item, comments);

        assertThat(dto.getId()).isEqualTo(200L);
        assertThat(dto.getName()).isEqualTo("Keyboard");
        assertThat(dto.getDescription()).isEqualTo("Mechanical keyboard");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getComments()).containsExactly(comment1);
    }

    @Test
    void mapToItemDtoWithCommentsEmptyCommentsList() {
        Item item = new Item();
        item.setId(300L);

        ItemDto dto = ItemMapper.mapToItemDtoWithComments(item, List.of());

        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void mapToCommentDtoAllFields() {
        User user = new User();
        user.setName("Bob");

        Comment comment = new Comment();
        comment.setId(50L);
        comment.setText("Good item!");
        comment.setUser(user);
        comment.setCreated(LocalDateTime.of(2025, 12, 7, 10, 0));

        CommentDto dto = ItemMapper.mapToCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(50L);
        assertThat(dto.getText()).isEqualTo("Good item!");
        assertThat(dto.getAuthorName()).isEqualTo("Bob");
        assertThat(dto.getCreated())
                .isEqualTo(LocalDateTime.of(2025, 12, 7, 10, 0));
    }

    @Test
    void mapToCommentDtoNullTextAndCreated() {
        User user = new User();
        user.setName("Charlie");

        Comment comment = new Comment();
        comment.setText(null);
        comment.setCreated(null);
        comment.setUser(user);

        CommentDto dto = ItemMapper.mapToCommentDto(comment);

        assertThat(dto.getText()).isNull();
        assertThat(dto.getCreated()).isNull();
        assertThat(dto.getAuthorName()).isEqualTo("Charlie");
    }

    @Test
    void mapToCommentTextCopied() {
        CommentRequest request = new CommentRequest();
        request.setText("This is a comment");

        Comment comment = ItemMapper.mapToComment(request);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("This is a comment");
        assertThat(comment.getId()).isNull();
        assertThat(comment.getUser()).isNull();
        assertThat(comment.getCreated()).isNull();
    }

    @Test
    void mapToCommentNullText() {
        CommentRequest request = new CommentRequest();
        request.setText(null);

        Comment comment = ItemMapper.mapToComment(request);

        assertThat(comment.getText()).isNull();
    }
}
