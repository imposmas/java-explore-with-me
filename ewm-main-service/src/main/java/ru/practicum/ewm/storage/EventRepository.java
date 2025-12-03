package ru.practicum.ewm.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.constants.EventState;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // события пользователя
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("""
    SELECT e FROM Event e
    WHERE e.state = ru.practicum.ewm.constants.EventState.PUBLISHED
      AND (:categories IS NULL OR e.category.id IN :categories)
      AND (:paid IS NULL OR e.paid = :paid)
      AND (e.eventDate >= COALESCE(:rangeStart, e.eventDate))
      AND (e.eventDate <= COALESCE(:rangeEnd, e.eventDate))
    """)
    List<Event> searchPublicEvents(List<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Pageable pageable);

    // --- админский поиск (/admin/events) ---
    @Query("""
    SELECT e FROM Event e
    WHERE (:users IS NULL OR e.initiator.id IN :users)
      AND (:states IS NULL OR e.state IN :states)
      AND (:categories IS NULL OR e.category.id IN :categories)
      AND e.eventDate >= COALESCE(:rangeStart, e.eventDate)
      AND e.eventDate <= COALESCE(:rangeEnd, e.eventDate)
""")
    List<Event> searchAdminEvents(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Pageable pageable);

    boolean existsByCategoryId(Long categoryId);
}