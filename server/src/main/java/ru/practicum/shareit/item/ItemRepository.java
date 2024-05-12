package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%', LOWER(:searchText), '%') OR LOWER(i.description) LIKE CONCAT('%', LOWER(:searchText), '%')) " +
            "AND i.available = true")
    Page<Item> searchByText(@Param("searchText") String searchText, Pageable page);

    Page<Item> findAllByOwnerId(Long userId, Pageable page);

    List<Item> findAllByRequestIn(List<ItemRequest> itemRequests, Sort sort);

}

