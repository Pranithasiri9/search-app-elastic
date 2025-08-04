package com.Pranitha.elasticsearchapp.controller;

import com.Pranitha.elasticsearchapp.service.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class CourseSuggestionController {

    private final SuggestionService suggestionService;

    public CourseSuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> suggest(@RequestParam("q") String prefix) {
        List<String> suggestions = suggestionService.suggestCourseTitles(prefix);
        return ResponseEntity.ok(suggestions);
    }
}
