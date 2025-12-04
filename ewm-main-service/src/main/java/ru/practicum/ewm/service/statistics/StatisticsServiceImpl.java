package ru.practicum.ewm.service.statistics;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsServiceClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatsServiceClient statsClient;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveHit(HttpServletRequest request) {

        EndpointHitDto dto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.sendHit(dto);
    }

    @Override
    public long getViews(Long eventId) {

        String start = "2000-01-01 00:00:00";
        String end = LocalDateTime.now().format(FORMATTER);

        List<ViewStatsDto> stats = statsClient
                .getStats(start, end, List.of("/events/" + eventId), true)
                .getBody();

        if (stats == null || stats.isEmpty()) {
            return 0;
        }

        return stats.get(0).getHits();
    }
}