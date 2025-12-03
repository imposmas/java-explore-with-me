package ru.practicum.ewm.service.statistics;

import jakarta.servlet.http.HttpServletRequest;

public interface StatisticsService {

    void saveHit(HttpServletRequest request);

    long getViews(Long eventId);
}
