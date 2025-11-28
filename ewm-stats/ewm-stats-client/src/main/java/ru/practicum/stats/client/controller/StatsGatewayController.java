package ru.practicum.stats.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.client.client.StatsServiceClient;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsGatewayController {

    private final StatsServiceClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@Valid @RequestBody EndpointHitDto dto) {
        log.info("CLIENT /hit — forwarding {}", dto);
        return statsClient.sendHit(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam String start,
                                                       @RequestParam String end,
                                                       @RequestParam(required = false) List<String> uris,
                                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("CLIENT /stats — forwarding request");
        return statsClient.getStats(start, end, uris, unique);
    }
}