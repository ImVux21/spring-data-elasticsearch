package com.example.searchengine.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.searchengine.entity.Product;
import com.example.searchengine.response.SearchHitsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Response;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FulltextSearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHitsResponse searchMatch(String query) {
        MatchQuery matchQuery = QueryBuilders
                .match()
                .field("name").query(query)
//                .operator(Operator.And)
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .match(matchQuery)
                )
                .build();

        SearchHits<Product> productHits = elasticsearchOperations.search(searchQuery, Product.class);
        log.info(productHits.toString());

        return serializerSearchHits(productHits);
    }

    public SearchHitsResponse searchMultiMatch(String query) {;
        MultiMatchQuery multiMatchQuery = QueryBuilders
                .multiMatch()
                .fields("name", "tags")
                .query(query)
                .build();

        String[] includeFields = new String[] {"name", "tags"};
        String[] excludeFields = new String[] {};

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(multiMatchQuery)
                )
                .withSourceFilter(new FetchSourceFilter(includeFields, excludeFields))
                .build();

        SearchHits<Product> productHits = elasticsearchOperations.search(searchQuery, Product.class);
        log.info(productHits.toString());

        return serializerSearchHits(productHits);
    }

    private SearchHitsResponse serializerSearchHits(SearchHits<Product> productSearchHits) {
        return SearchHitsResponse.builder()
                .searchHits(productSearchHits.getSearchHits())
                .totalHits(productSearchHits.getTotalHits())
                .maxScore(productSearchHits.getMaxScore())
                .build();
    }
}
