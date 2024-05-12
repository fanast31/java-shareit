package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User owner1;

    private User owner2;

    @BeforeEach
    public void addItems() {

        owner1 = User.builder()
                .email("mail1@mail.ru")
                .name("name1")
                .build();
        userRepository.save(owner1);
        itemRepository.save(Item.builder()
                .id(1L)
                .name("item1")
                .description("1")
                .owner(owner1)
                .available(true)
                .build());

        owner2 = User.builder()
                .email("mail2@mail.ru")
                .name("name2")
                .build();
        userRepository.save(owner2);
        itemRepository.save(Item.builder()
                .id(2L)
                .name("item2")
                .description("23")
                .owner(owner2)
                .available(true)
                .build());

    }

    @Test
    void searchByText() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.searchByText("m1", page).getContent();

        assertEquals(items.size(), 1);
    }

    @Test
    void findAllByOwnerId() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByOwnerId(owner1.getId(), page).getContent();

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "item1");
    }

    @Test
    void findAllByRequestIn() {

        ItemRequest request1 = ItemRequest.builder()
                .requester(owner1)
                .description("Need an item1")
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .requester(owner2)
                .description("Looking for item2")
                .created(LocalDateTime.now())
                .build();

        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        Item item1 = itemRepository.findById(1L).orElse(null);
        assertNotNull(item1);
        item1.setRequest(request1);
        itemRepository.save(item1);

        Item item2 = itemRepository.findById(2L).orElse(null);
        assertNotNull(item2);
        item2.setRequest(request2);
        itemRepository.save(item2);

        List<ItemRequest> itemRequests = List.of(request1, request2);

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        List<Item> items = itemRepository.findAllByRequestIn(itemRequests, sort);

        assertEquals(2, items.size());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item2.getName(), items.get(1).getName());

    }

    @AfterEach
    public void deleteItems() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

}