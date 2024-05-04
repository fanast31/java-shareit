package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%', LOWER(:searchText), '%') OR LOWER(i.description) LIKE CONCAT('%', LOWER(:searchText), '%')) " +
            "AND i.available = true")
    List<Item> searchByText(@Param("searchText") String searchText);

    List<Item> findAllByOwnerId(Long userId);

}

