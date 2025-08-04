package com.Pranitha.elasticsearchapp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final ElasticsearchClient elasticsearchClient;

    public SuggestionService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<String> suggestCourseTitles(String prefix) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                .index("courses")  // replace with your actual index name
                .suggest(sg -> sg
                    .suggesters("course-suggest", suggester -> suggester
                        .prefix(prefix)
                        .completion(c -> c
                            .field("suggest")  // replace with the field you want to suggest on
                            .skipDuplicates(true)
                            .size(10)
                        )
                    )
                )
            );

            SearchResponse<Void> response = elasticsearchClient.search(request, Void.class);

            Map<String, List<Suggestion<Void>>> suggestMap = response.suggest();
            return suggestMap.getOrDefault("course-suggest", List.of())
                .stream()
                .flatMap(suggestion -> suggestion.completion().options().stream())
                .map(option -> option.text())
                .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(" Failed to fetch suggestions", e);
        }
    }
}
