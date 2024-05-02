package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBookingDates;
import ru.practicum.shareit.item.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%', LOWER(:searchText), '%') OR LOWER(i.description) LIKE CONCAT('%', LOWER(:searchText), '%')) " +
            "AND i.available = true")
    List<Item> searchByText(@Param("searchText") String searchText);

    @Query("SELECT " +
            "i.id AS id, " +
            "i.name AS name, " +
            "i.description AS description, " +
            "i.available AS available, " +
            "MAX(b_Min.start) AS lastBookingDate, " +
            "MIN(b_Max.start) AS nextBookingDate " +
            "   FROM Item i " +
            "       LEFT JOIN Booking b_Min ON b_Min.item = i AND b_Min.start < :now " +
            "       LEFT JOIN Booking b_Max ON b_Max.item = i AND b_Max.start >= :now " +
            "       LEFT JOIN i.owner o" +
            "   WHERE o.id = :userId " +
            "GROUP BY i.id, i.name, i.description, i.available")
    List<ItemDtoResponseWithBookingDates> findAllItemWithDatesByOwnerId(Long userId, LocalDateTime now);

    @Query("SELECT " +
            "i.id AS id, " +
            "i.name AS name, " +
            "i.description AS description, " +
            "i.available AS available, " +
            "MAX(b_Min.start) AS lastBookingDate, " +
            "MIN(b_Max.start) AS nextBookingDate " +
            "   FROM Item i " +
            "       LEFT JOIN Booking b_Min ON b_Min.item = i AND b_Min.start < :now " +
            "       LEFT JOIN Booking b_Max ON b_Max.item = i AND b_Max.start >= :now " +
            "   WHERE i.id = :itemId " +
            "GROUP BY i.id, i.name, i.description, i.available")
    Optional<ItemDtoResponseWithBookingDates> findItemWithDatesByItemId(Long itemId, LocalDateTime now);

}

