package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.statistics.StatisticsService;
import ru.practicum.ewm.storage.CompilationRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository requestRepository;
    private final StatisticsService statisticsService;

    /**
     * Creates a new compilation of events.
     * Title is required, pinned defaults to false, events may be empty.
     */
    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {

        log.info("ADMIN: Creating compilation with title='{}'", dto.getTitle());

        Set<Event> events = dto.getEvents() == null ?
                Set.of() :
                new HashSet<>(eventRepository.findAllById(dto.getEvents()));

        Compilation comp = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .events(events)
                .build();

        comp = compilationRepository.save(comp);

        return enrichCompilation(comp);
    }

    /**
     * Deletes a compilation by id.
     * Throws NotFoundException if not exists.
     */
    @Override
    public void deleteCompilation(Long compId) {

        log.info("ADMIN: Deleting compilation id={}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }

        compilationRepository.deleteById(compId);
    }

    /**
     * Updates a compilation.
     * Allows updating title, pinned status and event set.
     */
    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {

        log.info("ADMIN: Updating compilation id={}", compId);

        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (dto.getTitle() != null) {
            log.debug("Updating compilation title to '{}'", dto.getTitle());
            comp.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            log.debug("Updating compilation pinned={}", dto.getPinned());
            comp.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            log.debug("Updating compilation events, count={}", dto.getEvents().size());
            comp.setEvents(new HashSet<>(eventRepository.findAllById(dto.getEvents())));
        }

        comp = compilationRepository.save(comp);

        return enrichCompilation(comp);
    }

    /**
     * Converts stored compilation to DTO enriched with:
     * - EventShortDto including confirmed requests count and views.
     */
    private CompilationDto enrichCompilation(Compilation comp) {

        CompilationDto dto = compilationMapper.toDto(comp);

        dto.setEvents(
                comp.getEvents().stream()
                        .map(this::toShortWithStats)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    /**
     * Creates EventShortDto enriched with:
     * - number of confirmed participation requests
     * - number of views from statistics service
     */
    private EventShortDto toShortWithStats(Event event) {

        long confirmed = requestRepository.countByEventIdAndStatus(
                event.getId(),
                RequestStatus.CONFIRMED
        );

        long views = statisticsService.getViews(event.getId());

        return eventMapper.toShortDto(event, confirmed, views);
    }
}