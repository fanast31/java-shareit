package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
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

    List<Booking> findByBookerId(Long bookerId, Pageable page);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable page);

    List<Booking> findByItem_Owner_Id(Long itemOwnerId, Pageable page);

    List<Booking> findByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus status, Pageable page);

    Optional<Booking> findFirstByItemAndStatusIsNotAndStartBefore(
            Item item, BookingStatus status, LocalDateTime start, Sort sort);

    Optional<Booking> findFirstByItemAndStatusIsNotAndStartAfter(
            Item item, BookingStatus status, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemAndBooker(Item item, User user);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookingId, LocalDateTime now, Pageable page);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long bookingId, LocalDateTime now, Pageable page);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookingId, LocalDateTime now, Pageable page);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long bookingId, LocalDateTime now, Pageable page);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Pageable page);

}
