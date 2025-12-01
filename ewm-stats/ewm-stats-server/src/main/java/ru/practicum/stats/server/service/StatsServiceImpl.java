package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.exceptions.ValidationException;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    public void saveHit(EndpointHitDto dto) {
        log.debug("Saving hit: {}", dto);
        repository.save(EndpointHitMapper.toEntity(dto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        log.debug("Fetching stats: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        if (start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }

        List<ViewStatsDto> result = (uris == null || uris.isEmpty())
                ? (unique ? repository.getStatsUniqueNoUri(start, end)
                : repository.getStatsNoUri(start, end))
                : (unique ? repository.getStatsUnique(start, end, uris)
                : repository.getStats(start, end, uris));

        return result;
    }
}