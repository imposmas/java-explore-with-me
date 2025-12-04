package ru.practicum.ewm.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable toPageable(int from, int size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }

    public static Pageable toPageable(int from, int size, Sort sort) {
        int page = from / size;
        return PageRequest.of(page, size, sort);
    }
}