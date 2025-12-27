package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequestItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequestItem(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestId(@RequestHeader(HEADER_USER_ID) Long userId,
                                               @PathVariable("requestId") Long requestId) {
        return itemRequestClient.getRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestClient.getAllRequest(userId);
    }
}
