package org.example.preschool.util;

import org.example.preschool.model.entity.BaseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToLongFunction;

public final class SequencePagination {

    public static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;

    private SequencePagination() {
    }

    public static <T extends BaseEntity> List<T> apply(
            List<T> items,
            Long sequence,
            int limit,
            String direction
    ) {
        return apply(items, sequence, limit, direction, item -> {
            Long itemSequence = item.getSequence();
            return itemSequence == null ? Long.MIN_VALUE : itemSequence;
        });
    }

    public static <T> List<T> apply(
            List<T> items,
            Long sequence,
            int limit,
            String direction,
            ToLongFunction<T> sequenceExtractor
    ) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "limit must be between 1 and " + MAX_LIMIT
            );
        }
        if (!"asc".equals(direction) && !"desc".equals(direction)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
        }

        Comparator<T> comparator = Comparator.comparingLong(sequenceExtractor);
        if ("desc".equals(direction)) comparator = comparator.reversed();

        return items.stream()
                .filter(item -> sequence == null
                        || ("asc".equals(direction)
                        ? sequenceExtractor.applyAsLong(item) > sequence
                        : sequenceExtractor.applyAsLong(item) < sequence))
                .sorted(comparator)
                .limit(limit)
                .toList();
    }
}
