package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(HEADER_USER_ID) Long owner, @RequestBody ItemDto itemDto) {
        log.info("Добавлена новая вещь");
        return ResponseEntity.ok().body(itemService.createItem(owner, itemDto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable("itemId") Long itemId,
                                                    @RequestHeader(HEADER_USER_ID) Long userId,
                                                    @RequestBody CommentRequest comment) {
        log.info("Пользователь {} создал комментарий для вещи {}", userId, itemId);
        return ResponseEntity.ok().body(itemService.createComment(itemId, userId, comment));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_USER_ID) Long owner,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable("itemId") Long itemId) {
        log.info("Обновлена ващь с id: {}", itemId);
        return ResponseEntity.ok().body(itemService.updateItem(itemId, owner, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable("itemId") Long itemId) {
        log.info("Получеа вещь с id: {}", itemId);
        return ResponseEntity.ok().body(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsOwner(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Получен список вещей пользователя {}", owner);
        return ResponseEntity.ok().body(itemService.getItemsByOwnerId(owner));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsForText(@RequestParam("text") String text) {
        log.info("Получен списко вещей по тексту {}", text);
        return ResponseEntity.ok().body(itemService.getAllItemsByText(text));
    }
}
