package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN FETCH booking.item item " +
            "LEFT JOIN FETCH item.owner itemOwner " +
            "LEFT JOIN FETCH booking.booker booker " +
            "WHERE booking.id = :bookingId AND (itemOwner.id = :userId OR booker.id = :userId)")
    Optional<Booking> bookingForView(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN FETCH booking.item item " +
            "LEFT JOIN FETCH item.owner itemOwner " +
            "WHERE booking.id = :bookingId AND itemOwner.id = :userId")
    Optional<Booking> bookingForUpdate(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

}
