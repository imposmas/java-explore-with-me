package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.Compilation;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDto toDto(Compilation c) {
        CompilationDto dto = new CompilationDto();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setPinned(c.getPinned());
        return dto;
    }
}