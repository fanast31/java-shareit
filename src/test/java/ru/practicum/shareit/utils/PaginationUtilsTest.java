package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class PaginationUtilsTest {

    @Test
    public void testCreatePageableWithValidParams() {
        Integer from = 10;
        Integer size = 5;
        Pageable pageable = PaginationUtils.createPageable(from, size);

        assertNotNull(pageable);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
    }

    @Test
    public void testCreatePageableWithSortWithValidParams() {
        Integer from = 10;
        Integer size = 5;
        Sort sort = Sort.by("date").descending();
        Pageable pageable = PaginationUtils.createPageable(from, size, sort);

        assertNotNull(pageable);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(sort, pageable.getSort());
    }

    @Test
    public void testCreatePageableWithNullParams() {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            PaginationUtils.createPageable(null, null);
        });

        assertEquals("Bad pagination data", exception.getMessage());
    }

    @Test
    public void testCreatePageableWithSortWithNullParams() {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            PaginationUtils.createPageable(null, null, Sort.by("date").descending());
        });

        assertEquals("Bad pagination data", exception.getMessage());
    }

    @Test
    public void testCreatePageableWithInvalidParams() {
        Integer from = -1;
        Integer size = 5;

        Exception exception = assertThrows(BadRequestException.class, () -> {
            PaginationUtils.createPageable(from, size);
        });

        assertEquals("Bad pagination data", exception.getMessage());
    }

    @Test
    public void testCreatePageableWithSortWithInvalidParams() {
        Integer from = 10;
        Integer size = 0;
        Sort sort = Sort.by("date").descending();

        Exception exception = assertThrows(BadRequestException.class, () -> {
            PaginationUtils.createPageable(from, size, sort);
        });

        assertEquals("Bad pagination data", exception.getMessage());
    }

}