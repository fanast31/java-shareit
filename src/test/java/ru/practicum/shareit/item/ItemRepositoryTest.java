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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private User owner1;

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

        User owner2 = User.builder()
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

        User owner3 = User.builder()
                .email("mail3@mail.ru")
                .name("name3")
                .build();
        userRepository.save(owner3);
        itemRepository.save(Item.builder()
                .id(3L)
                .name("item3")
                .description("31")
                .owner(owner3)
                .available(true)
                .build());

    }

    @Test
    void searchByText() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.searchByText("m1", page);

        assertEquals(items.size(), 1);
    }

    @Test
    void findAllByOwnerId() {
        Pageable page = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByOwnerId(owner1.getId(), page);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "item1");
    }

    @Test
    void findAllByRequestIn() {
        ItemRequest request1 = ItemRequest.builder()
                .requester(owner1)
                .description("Need an item1")
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .requester(owner1)
                .description("Looking for item2")
                .build();
    }

    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
    }

}