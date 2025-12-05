package ru.practicum.ewm.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.BadRequestException;
import ru.practicum.ewm.common.exceptions.ConflictException;
import ru.practicum.ewm.common.exceptions.NotAuthorizedException;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.DateTimeUtils;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.PrivateCommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.storage.CommentRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing comment operations available to registered users.
 * Provides functionality for creating, retrieving, editing and deleting comments
 * associated with published events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    /**
     * Creates a new comment authored by a user for a published event.
     * Validates that the event exists, is published, and that a user cannot comment
     * on their own event.
     */
    @Override
    @Transactional
    public PrivateCommentDto createComment(Long userId, Long eventId, CommentDto dto) {
        log.info("PRIVATE: Creating comment for eventId={} by userId={}", eventId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new NotFoundException("User with id=" + userId + " was not found");
                });

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be PUBLISHED to comment");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User cannot comment own event");
        }

        Comment comment = commentMapper.toEntity(dto, user, eventId);
        comment.setCreatedOn(LocalDateTime.now());
        comment = commentRepository.save(comment);

        log.info("PRIVATE: Comment created id={} for eventId={} by userId={}",
                comment.getId(), eventId, userId);

        return commentMapper.toFullDto(comment);
    }

    /**
     * Retrieves all comments for an event, visible to a registered user.
     * Applies date range filtering with defaults and paginated sorting by creation timestamp.
     */
    @Override
    public List<PrivateCommentDto> getCommentsForEvent(
            Long userId,
            Long eventId,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        log.info("PRIVATE: Fetching comments for eventId={} by userId={}, from={}, size={}",
                eventId, userId, from, size);

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id={} not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
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

        List<PrivateCommentDto> result = commentRepository
                .findPrivateCommentsByEvent(eventId, start, end, pageable)
                .stream()
                .map(commentMapper::toFullDto)
                .toList();

        log.info("PRIVATE: Returned {} comments for eventId={}", result.size(), eventId);

        return result;
    }

    /**
     * Retrieves a single comment by its identifier.
     */
    @Override
    public PrivateCommentDto getComment(Long userId, Long commentId) {
        log.info("PRIVATE: Fetching comment id={} by userId={}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    return new NotFoundException("Comment id=" + commentId + " not found");
                });

        return commentMapper.toFullDto(comment);
    }

    /**
     * Updates the text of a comment created by the requesting user.
     * Validates ownership before applying changes.
     */
    @Override
    @Transactional
    public PrivateCommentDto updateComment(Long userId, Long commentId, CommentDto dto) {
        log.info("PRIVATE: Updating comment id={} by userId={}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    return new NotFoundException("Comment not found");
                });

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotAuthorizedException("User cannot edit another user's comment");
        }

        comment.setText(dto.getText());

        log.info("PRIVATE: Comment id={} updated by userId={}", commentId, userId);
        return commentMapper.toFullDto(comment);
    }

    /**
     * Deletes a comment created by the requesting user.
     * Ensures the user is the author of the comment before deletion.
     */
    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("PRIVATE: Deleting comment id={} by userId={}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment id={} not found", commentId);
                    return new NotFoundException("Comment not found");
                });

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotAuthorizedException("User cannot delete another user's comment");
        }

        commentRepository.delete(comment);

        log.info("PRIVATE: Comment id={} deleted by userId={}", commentId, userId);
    }
}