package com.Pranitha.elasticsearchapp;

import com.Pranitha.elasticsearchapp.dto.CourseSearchRequest;
import com.Pranitha.elasticsearchapp.dto.CourseSearchResponse;
import com.Pranitha.elasticsearchapp.service.CourseSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CourseSearchIntegrationTest {

    @Autowired
    private CourseSearchService courseSearchService;

    //  Test 1: Basic search with no filters
    @Test
    void testSearchWithNoFiltersReturnsResults() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setPage(0);
        request.setSize(5);

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        assertThat(response.getCourses()).isNotEmpty();
        System.out.println("🔍 Total Results: " + response.getTotal());
    }

    //  Test 2: Search by keyword
    @Test
    void testSearchWithKeyword() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQ("Science");
        request.setPage(0);
        request.setSize(5);

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        assertThat(response.getCourses()).isNotEmpty();
        assertThat(response.getCourses().get(0).getTitle()).containsIgnoringCase("science");
    }

    //  Test 3: Filter by category and type
    @Test
    void testFilterByCategoryAndType() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setCategory("Music");
        request.setType("COURSE");
        request.setPage(0);
        request.setSize(5);

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        assertThat(response.getCourses()).allMatch(course ->
            course.getCategory().equalsIgnoreCase("Music")
        );
    }

    //  Test 4: Filter by price range and age
    @Test
    
    void testFilterByPriceAndAgeRange() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setMinPrice(30.0);
        request.setMaxPrice(60.0);
        request.setMinAge(7);
        request.setMaxAge(10);
        request.setPage(0);
        request.setSize(10);

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        assertThat(response.getCourses()).isNotEmpty();
        assertThat(response.getCourses()).allSatisfy(course -> {
            double price = course.getPrice() != null ? course.getPrice() : 0.0;
            assertThat(price).isBetween(30.0, 60.0);
        });
    }


    //  Test 5: Edge case - no results
    @Test
    void testSearchReturnsNoResults() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQ("NonExistentKeywordXYZ");
        request.setPage(0);
        request.setSize(5);

        CourseSearchResponse response = courseSearchService.searchCourses(request);

        assertThat(response.getCourses()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);
    }
}
