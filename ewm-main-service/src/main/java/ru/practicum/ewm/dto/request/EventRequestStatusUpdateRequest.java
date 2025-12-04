package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.ewm.constants.RequestStatus;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    @NotEmpty
    private Set<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
