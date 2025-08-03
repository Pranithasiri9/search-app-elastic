package com.Pranitha.elasticsearchapp.repository;

import com.Pranitha.elasticsearchapp.document.CourseDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {

    // Derived Query Methods
    Page<CourseDocument> findByCategory(String category, Pageable pageable);
    
    List<CourseDocument> findByType(String type);
    
    List<CourseDocument> findByPriceBetween(Double minPrice, Double maxPrice);
    
    List<CourseDocument> findByMinAgeGreaterThanEqual(Integer minAge);
    
    List<CourseDocument> findByNextSessionDateAfter(LocalDate date);

    // Custom Query Methods
    @Query("""
        {
            "bool": {
                "must": [
                    {"multi_match": {
                        "query": "?0",
                        "fields": ["title", "description"]
                    }},
                    {"term": {
                        "category.keyword": "?1"
                    }}
                ]
            }
        }
        """)
    List<CourseDocument> searchByKeywordAndCategory(String keyword, String category);

    @Query("""
        {
            "range": {
                "nextSessionDate": {
                    "gte": "?0"
                }
            }
        }
        """)
    Page<CourseDocument> findUpcomingCourses(String date, Pageable pageable);

    @Query("""
        {
            "bool": {
                "filter": [
                    {"range": {
                        "price": {
                            "gte": ?0,
                            "lte": ?1
                        }
                    }},
                    {"range": {
                        "nextSessionDate": {
                            "gte": "?2"
                        }
                    }}
                ]
            }
        }
        """)
    List<CourseDocument> findCoursesInPriceRangeWithDate(Double minPrice, Double maxPrice, String startDate);
}