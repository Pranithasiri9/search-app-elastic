package com.Pranitha.elasticsearchapp.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;

import com.Pranitha.elasticsearchapp.document.CourseDocument;
import com.Pranitha.elasticsearchapp.dto.CourseSearchRequest;
import com.Pranitha.elasticsearchapp.dto.CourseSearchResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public CourseSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public CourseSearchResponse searchCourses(CourseSearchRequest request) {
        try {
            NativeQuery searchQuery = buildSearchQuery(request);
            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(
                    searchQuery,
                    CourseDocument.class
            );

            List<CourseDocument> documents = searchHits.stream()
            	    .map(hit -> hit.getContent())
            	    .collect(Collectors.toList());

            	return new CourseSearchResponse(searchHits.getTotalHits(), documents); 


        } catch (Exception e) {
            throw new RuntimeException("Error searching courses", e);
        }
    }

    private NativeQuery buildSearchQuery(CourseSearchRequest request) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (request.getQ() != null && !request.getQ().isEmpty()) {
            boolQueryBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(request.getQ())
                    .fields("title", "description")
            ));
        }

        if (request.getCategory() != null) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("category.keyword")
                    .value(request.getCategory())
            ));
        }

        if (request.getType() != null) {
            boolQueryBuilder.filter(f -> f.term(t -> t
                    .field("type.keyword")
                    .value(request.getType())
            ));
        }

        if (request.getMinAge() != null || request.getMaxAge() != null) {
            boolQueryBuilder.filter(buildRangeQuery("minAge", request.getMinAge(), request.getMaxAge()));
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            boolQueryBuilder.filter(buildRangeQuery("price", request.getMinPrice(), request.getMaxPrice()));
        }

        if (request.getNextSessionDate() != null) {
            String dateStr = request.getNextSessionDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            boolQueryBuilder.filter(f -> f.range(r -> r
                    .field("nextSessionDate")
                    .gte(JsonData.of(dateStr))
            ));
        }

        SortOptions sortOptions = buildSortOptions(request);

        PageRequest pageRequest = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 10
        );

        return NativeQuery.builder()
                .withQuery(q -> q.bool(boolQueryBuilder.build()))
                .withSort(sortOptions)
                .withPageable(pageRequest)
                .build();
    }

    private SortOptions buildSortOptions(CourseSearchRequest request) {
        if ("priceAsc".equalsIgnoreCase(request.getSort())) {
            return SortOptions.of(s -> s
                    .field(f -> f
                            .field("price")
                            .order(SortOrder.Asc)
                    ));
        } else if ("priceDesc".equalsIgnoreCase(request.getSort())) {
            return SortOptions.of(s -> s
                    .field(f -> f
                            .field("price")
                            .order(SortOrder.Desc)
                    ));
        }

        return SortOptions.of(s -> s
                .field(f -> f
                        .field("nextSessionDate")
                        .order(SortOrder.Asc)
                ));
    }

    private Query buildRangeQuery(String field, Object min, Object max) {
        RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field(field);
        if (min != null) rangeBuilder.gte(JsonData.of(min));
        if (max != null) rangeBuilder.lte(JsonData.of(max));
        return Query.of(q -> q.range(rangeBuilder.build()));
    }
}
