package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_USER_ID) Long owner,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(owner, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable("itemId") Long itemId,
                                                @RequestHeader(HEADER_USER_ID) Long userId,
                                                @Valid @RequestBody CommentRequest comment) {
        return itemClient.createComment(itemId, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) Long owner,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable("itemId") Long id) {
        return itemClient.updateItem(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") Long id) {
        return itemClient.getItemForId(id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOwner(@RequestHeader(HEADER_USER_ID) Long owner) {
        return itemClient.getItemsOwner(owner);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsForText(@RequestHeader(HEADER_USER_ID) Long userId,
                                                  @RequestParam("text") String text) {
        return itemClient.getItemForText(userId, text);
    }
}
