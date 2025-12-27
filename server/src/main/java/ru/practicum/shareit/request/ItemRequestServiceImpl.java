package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setUserId(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return itemRequestRepository.findByUserId_Id(userId)
                .stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestForId(Long requestId) {
        List<Item> items = itemRepository.getItemByRequestId(requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не существует"));
        ItemRequestDto dto = ItemRequestMapper.itemRequestToDto(request);
        dto.setItems(items.stream().map(ItemMapper::mapToItemDto).toList());
        return dto;
    }

    @Override
    public List<ItemRequestDto> getAllRequest() {
        return itemRequestRepository.findAll()
                .stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .toList();
    }
}
