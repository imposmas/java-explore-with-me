package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StatsServiceClient extends BaseClient {

    public StatsServiceClient(@Value("${stats-server.url}") String serverUrl) {
        super(new RestTemplate(), serverUrl);
    }

    public ResponseEntity<Object> sendHit(EndpointHitDto dto) {
        log.debug("Forwarding hit: {}", dto);
        return post("/hit", dto, Object.class);
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start,
                                                       String end,
                                                       List<String> uris,
                                                       boolean unique) {
        StringBuilder url = new StringBuilder("/stats?start={start}&end={end}&unique={unique}");

        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (int i = 0; i < uris.size(); i++) {
                url.append("&uris={uri").append(i).append("}");
                params.put("uri" + i, uris.get(i));
            }
        }

        ResponseEntity<ViewStatsDto[]> response =
                get(url.toString(), params, ViewStatsDto[].class);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody() != null ? List.of(response.getBody()) : List.of());
    }
}