package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> bookingForView(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    Optional<Booking> findByIdAndItem_Owner_Id(Long bookingId, Long userId);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findByItem_Owner_Id(Long itemOwnerId);

    List<Booking> findByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus status);

    Optional<Booking> findFirstByItemAndStatusIsNotAndStartBefore(Item item, BookingStatus status, LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItemAndStatusIsNotAndStartAfter(Item item, BookingStatus status, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemAndBooker(Item item, User user);
}
