package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) String owner, @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавлена новая вещь");
        return itemService.createItem(Long.parseLong(owner), itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") Long itemId,
                                    @RequestHeader(HEADER_USER_ID) Long userId,
                                    @Valid @RequestBody CommentRequest comment) {
        log.info("Пользователь {} создал комментарий для вещи {}", userId, itemId);
        return itemService.createComment(itemId, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) String owner,
                              @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") Long itemId) {
        log.info("Обновлена ващь с id: {}", itemId);
        return itemService.updateItem(itemId, Long.parseLong(owner), itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") Long itemId) {
        log.info("Получеа вещь с id: {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsOwner(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Получен список вещей пользователя {}", owner);
        return itemService.getItemsByOwnerId(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsForText(@RequestParam("text") String text) {
        log.info("Получен списко вещей по тексту {}", text);
        return itemService.getAllItemsByText(text);
    }
}
