package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository
        extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

}
