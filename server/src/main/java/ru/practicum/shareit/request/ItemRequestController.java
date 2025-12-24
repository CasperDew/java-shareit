package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequestItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пользователь {} создал запрос", userId);
        return ResponseEntity.ok().body(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Поиск список запросов пользователя");
        return ResponseEntity.ok().body(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestId(@PathVariable("requestId") Long requestId) {
        log.info("Получена информация о запросе с ID {}", requestId);
        return ResponseEntity.ok().body(itemRequestService.getItemRequestForId(requestId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequest() {
        log.info("Получен полный список запросов");
        return ResponseEntity.ok().body(itemRequestService.getAllRequest());
    }
}
