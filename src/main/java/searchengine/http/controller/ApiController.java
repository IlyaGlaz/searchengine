package searchengine.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.common.IndexingResponseDto;
import searchengine.dto.common.IndexingResponseErrorDto;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.service.IndexingService;
import searchengine.service.StatisticsService;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponseDto> startIndexing() {
        indexingService.start();
        return ResponseEntity.ok(new IndexingResponseDto("true"));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponseDto> stopIndexing() {
        indexingService.stop();
        return ResponseEntity.ok(new IndexingResponseDto("true"));
    }
}
