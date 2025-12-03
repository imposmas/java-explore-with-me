package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exceptions.NotFoundException;
import ru.practicum.ewm.common.util.PaginationUtils;
import ru.practicum.ewm.constants.RequestStatus;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.statistics.StatisticsService;
import ru.practicum.ewm.storage.CompilationRepository;
import ru.practicum.ewm.storage.ParticipationRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository requestRepository;
    private final StatisticsService statisticsService;

    /**
     * Fetch list of public compilations with optional pinned filter.
     * Filtering is applied on database level to ensure correct pagination.
     */
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {

        Pageable pageable = PaginationUtils.toPageable(from, size);
        log.info("PUBLIC: getCompilations(pinned={}, from={}, size={})", pinned, from, size);

        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        log.debug("Found {} compilations", compilations.size());

        return compilations.stream()
                .map(this::enrichCompilation)
                .collect(Collectors.toList());
    }

    /**
     * Fetch a public compilation by id.
     */
    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info("PUBLIC: getCompilation(id={})", compId);

        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error("Compilation {} not found", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });

        return enrichCompilation(comp);
    }

    /**
     * Builds a CompilationDto enriched with event statistics.
     */
    private CompilationDto enrichCompilation(Compilation comp) {

        if (comp.getEvents() == null) {
            log.warn("Compilation {} has null events list", comp.getId());
        }

        CompilationDto dto = compilationMapper.toDto(comp);

        dto.setEvents(
                comp.getEvents() == null ? List.of() :
                        comp.getEvents().stream()
                                .map(this::toShortWithStats)
                                .collect(Collectors.toList())
        );

        return dto;
    }

    /**
     * Converts an Event to EventShortDto including confirmed requests and view stats.
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