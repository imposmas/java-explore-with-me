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
import ru.practicum.ewm.dto.comment.PrivateCommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.storage.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Administrative service for managing comments.
 * Provides functionality to retrieve comments by event or user,
 * and to delete any comment regardless of ownership.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    /**
     * Retrieves comments associated with a specific event.
     * Applies date range filtering, defaulting to the last 10 days
     * when no explicit range is provided.
     */
    @Override
    public List<PrivateCommentDto> getCommentsByEvent(
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        log.info("ADMIN: Fetching comments for eventId={}, from={}, size={}", eventId, from, size);

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

        List<PrivateCommentDto> result = commentRepository
                .findAdminCommentsByEvent(eventId, start, end, pageable)
                .stream()
                .map(commentMapper::toFullDto)
                .toList();

        log.info("ADMIN: Returned {} comments for eventId={}", result.size(), eventId);

        return result;
    }

    /**
     * Retrieves comments created by a specific user.
     * Applies date range filtering and paginated sorting.
     */
    @Override
    public List<PrivateCommentDto> getCommentsByUser(
            Long userId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        log.info("ADMIN: Fetching comments created by userId={}, from={}, size={}", userId, from, size);

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

        List<PrivateCommentDto> result = commentRepository
                .findAdminCommentsByUser(userId, start, end, pageable)
                .stream()
                .map(commentMapper::toFullDto)
                .toList();

        log.info("ADMIN: Returned {} comments created by userId={}", result.size(), userId);

        return result;
    }

    /**
     * Deletes a comment by its identifier.
     * Accessible only to administrators.
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("ADMIN: Deleting comment id={}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    return new NotFoundException("Comment not found");
                });

        commentRepository.delete(comment);

        log.info("ADMIN: Comment id={} deleted", commentId);
    }
}