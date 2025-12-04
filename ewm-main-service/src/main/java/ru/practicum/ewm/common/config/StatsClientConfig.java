package ru.practicum.ewm.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.stats.client.StatsServiceClient;

@Configuration
public class StatsClientConfig {

    @Bean
    public StatsServiceClient statsServiceClient(
            @Value("${stats-server.url}") String serverUrl
    ) {
        return new StatsServiceClient(serverUrl);
    }
}