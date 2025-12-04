package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.compilation.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationsController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                publicCompilationService.getCompilations(pinned, from, size)
        );
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        return ResponseEntity.ok(
                publicCompilationService.getCompilation(compId)
        );
    }
}