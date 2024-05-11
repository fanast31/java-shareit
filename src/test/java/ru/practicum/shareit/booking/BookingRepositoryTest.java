package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testBookingForViewWhenOwner() {

        LocalDateTime now = LocalDateTime.now();

        User user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build());

        User user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .name("user1")
                .description("user1")
                .available(true)
                .owner(user1)
                .build());

        Booking booking1 = bookingRepository.save(Booking.builder()
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .item(item1)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build());

        Optional<Booking> result =
                bookingRepository.bookingForView(booking1.getId(), user1.getId());

        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), booking1.getId());

    }

}