package ru.practicum.ewm.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            JOIN Event e ON c.eventId = e.id
            WHERE e.state = ru.practicum.ewm.constants.EventState.PUBLISHED
              AND c.eventId = :eventId
              AND c.createdOn >= COALESCE(:rangeStart, c.createdOn)
              AND c.createdOn <= COALESCE(:rangeEnd, c.createdOn)
            """)
    List<Comment> findPublicCommentsByEvent(
            Long eventId,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);


    @Query("""
            SELECT c FROM Comment c
            JOIN Event e ON c.eventId = e.id
            WHERE e.state = ru.practicum.ewm.constants.EventState.PUBLISHED
              AND c.eventId = :eventId
              AND c.createdOn >= COALESCE(:rangeStart, c.createdOn)
              AND c.createdOn <= COALESCE(:rangeEnd, c.createdOn)
            """)
    List<Comment> findPrivateCommentsByEvent(
            Long eventId,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);


    @Query("""
            SELECT c FROM Comment c
            WHERE c.eventId = :eventId
              AND c.createdOn >= COALESCE(:rangeStart, c.createdOn)
              AND c.createdOn <= COALESCE(:rangeEnd, c.createdOn)
            """)
    List<Comment> findAdminCommentsByEvent(
            Long eventId,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);


    @Query("""
            SELECT c FROM Comment c
            WHERE c.author.id = :userId
              AND c.createdOn >= COALESCE(:rangeStart, c.createdOn)
              AND c.createdOn <= COALESCE(:rangeEnd, c.createdOn)
            """)
    List<Comment> findAdminCommentsByUser(
            Long userId,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);
}