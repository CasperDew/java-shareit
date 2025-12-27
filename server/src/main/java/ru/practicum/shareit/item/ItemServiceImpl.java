package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        validateUser(ownerId);
        return ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItem(ownerId, itemDto)));
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentRequest commentRequest) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такого товара не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        List<Booking> bookingsUser = bookingRepository.findByBookerId(userId);
        Optional<Booking> bookingItemId = bookingsUser.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .findFirst();
        if (bookingItemId.isEmpty() || bookingItemId.get().getEnd().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Такого бронирования у пользователя не найдено.");
        }
        Comment comment = ItemMapper.mapToComment(commentRequest);
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());
        return ItemMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto updateItem(Long id, Long ownerId, ItemDto itemDto) {
        validateUser(ownerId);
        Item item = ItemMapper.mapToItem(ownerId, itemDto);
        Item updateItem = ItemMapper.updateFieldsItem(item, itemRepository.findById(id).get());
        updateItem.setId(id);
        return ItemMapper.mapToItemDto(itemRepository.save(updateItem));
    }

    @Override
    public ItemDto getItem(Long id) {
        if (itemRepository.findById(id).isPresent()) {
            List<CommentDto> comments = commentRepository.findByItemId(id)
                    .stream()
                    .map(ItemMapper::mapToCommentDto)
                    .toList();
            return ItemMapper.mapToItemDtoWithComments(itemRepository.findById(id).get(), comments);
        } else {
            throw new NotFoundException("Такого товара не найдено");
        }
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.getAllItemByOwner(ownerId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchItems(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public void validateUser(Long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Пользователь не существует");
        }
    }
}
