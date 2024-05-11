package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    public static final Sort SORT_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    private User requester;
    private User owner;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    public void addItems() {

        owner = userRepository.save(
                User.builder()
                        .email("owner@mail.ru")
                        .name("owner")
                        .build());

        requester = userRepository.save(
                User.builder()
                        .email("requester@mail.ru")
                        .name("requester")
                        .build());

        itemRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .description("1")
                        .requester(requester)
                        .created(LocalDateTime.now())
                        .build());
        item = itemRepository.save(Item.builder()
                .name("item")
                .description("item")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());
    }

    @Test
    @Transactional
    void shouldFindAllByRequester() {

        List<ItemRequest> results = itemRequestRepository.findAllByRequester(
                requester, SORT_CREATED_DESC);

        assertThat(results).hasSize(1);
        assertEquals(results.get(0), itemRequest);

    }

    @Test
    @Transactional
    void shouldFindAllByRequesterNot() {

        Pageable page = PaginationUtils.createPageable(1, 10, SORT_CREATED_DESC);

        List<ItemRequest> results = itemRequestRepository.findAllByRequesterNot(owner, page);

        assertThat(results).hasSize(1);
        assertEquals(results.get(0), itemRequest);

    }
}