package ru.practicum.ewm.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.BadRequestException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.dto.comment.PublicCommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.storage.CommentRepository;
import ru.practicum.ewm.storage.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for providing public access to event comments.
 * Allows retrieving comments for published events without exposing
 * sensitive information such as author details.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    /**
     * Retrieves all publicly visible comments for a specific event.
     * Includes date range filtering (defaults to last 10 days),
     * pagination and sorting by creation timestamp in descending order.
     */
    @Override
    public List<PublicCommentDto> getCommentsForEvent(
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        log.info("PUBLIC: Fetching comments for eventId={}, from={}, size={}", eventId, from, size);

        eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        LocalDateTime end = (rangeEnd != null)
                ? LocalDateTime.parse(rangeEnd, DateTimeUtils.FORMATTER)
                : LocalDateTime.now();
        LocalDateTime start = (rangeStart != null)
                ? LocalDateTime.parse(rangeStart, DateTimeUtils.FORMATTER)
                : end.minusDays(10);

        if (end.isBefore(start)) {
            throw new BadRequestException("rangeEnd cannot be before rangeStart");
        }

        Pageable pageable = PaginationUtils.toPageable(from, size, Sort.by("createdOn").descending());

        List<PublicCommentDto> result = commentRepository
                .findPublicCommentsByEvent(eventId, start, end, pageable)
                .stream()
                .map(commentMapper::toPublicDto)
                .toList();

        log.info("PUBLIC: Returned {} comments for eventId={}", result.size(), eventId);

        return result;
    }
}