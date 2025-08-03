package com.Pranitha.elasticsearchapp.controller;

import com.Pranitha.elasticsearchapp.dto.CourseSearchRequest;
import com.Pranitha.elasticsearchapp.dto.CourseSearchResponse;
import com.Pranitha.elasticsearchapp.service.CourseSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")

public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    public CourseSearchController(CourseSearchService courseSearchService) {
        this.courseSearchService = courseSearchService;
    }

    /**
     * API Root Endpoint
     */
    @GetMapping("/")
    public ResponseEntity<String> apiRoot() {
        return ResponseEntity.ok("""
                 Course Search API
                 Use GET /api/search with optional query parameters:
                - q: Search keyword
                - minAge / maxAge
                - category: e.g., Math, Science
                - type: COURSE, CLUB, ONE_TIME
                - minPrice / maxPrice
                - nextSessionDate: ISO-8601 (e.g., 2025-08-03T00:00:00Z)
                - sort: upcoming | priceAsc | priceDesc
                - page / size: pagination

                Example:
                /api/search?q=math&category=Science&minAge=5&sort=priceAsc&page=0&size=5
                """);
    }

    /**
     * Main course search endpoint
     */
    @GetMapping("/search")
    public ResponseEntity<CourseSearchResponse> searchCourses(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "minAge", required = false) Integer minAge,
            @RequestParam(name = "maxAge", required = false) Integer maxAge,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "nextSessionDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime nextSessionDate,
            @RequestParam(name = "sort", defaultValue = "upcoming") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            CourseSearchRequest request = new CourseSearchRequest();
            request.setQ(q);
            request.setMinAge(minAge);
            request.setMaxAge(maxAge);
            request.setCategory(category);
            request.setType(type);
            request.setMinPrice(minPrice);
            request.setMaxPrice(maxPrice);
            request.setNextSessionDate(nextSessionDate); 
            request.setSort(sort);
            request.setPage(page);
            request.setSize(size);

            CourseSearchResponse response = courseSearchService.searchCourses(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CourseSearchResponse(0, List.of()));
        }
    }

    /**
     * Health Check Endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok(" Course Search API is up and healthy");
    }

    
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(" 404: Not Found — Try /api/search or /api/health");
    }
}
