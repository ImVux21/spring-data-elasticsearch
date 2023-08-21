package com.example.searchengine.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.example.searchengine.entity.Product;
import com.example.searchengine.repository.ProductRepository;
import com.example.searchengine.response.SearchHitsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermSearchService {
    private final ProductRepository productRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    public void createProductIndexBulk(List<Product> products) {
        productRepository.saveAll(products);
    }

    public void createProductIndex(Product product) {
        productRepository.save(product);
    }

    public List<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Searching for terms
     */
    public SearchHitsResponse searchForTerm(String query) {
        TermQuery termQuery = QueryBuilders
                .term()
//                .field("tags").value(query)
                // Searching for multiple terms
                .field("tags").value("Soup").value("Meat")
                .caseInsensitive(true)
                .build();
        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .term(termQuery)
                )
//                .withFilter(q -> q
//                        .bool(b -> b
//                                .mustNot(m -> m
//                                        .term(t -> t
//                                                .field("tags").value("Soup")
//                                        )
//                                )
//                        )
//                )
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    /**
     * Retrieve product by ids
     */
    public SearchHitsResponse retrieveByIds(List<String> ids) {
        IdsQuery idsQuery = QueryBuilders
                .ids()
                .values(ids)
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.ids(idsQuery))
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    /**
     * Range Searches
     */
    public SearchHitsResponse searchRanges(String from, String to) {
        RangeQuery rangeQuery = QueryBuilders
                .range()
                .field("in_stock")
                .gte(JsonData.of(1))
//                .gt(JsonData.of(1))
                .lte(JsonData.of(5))
//                .lt(JsonData.of(5))
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.range(rangeQuery))
                // page 2 with the page size 10
                .withPageable(PageRequest.of(2, 10))
                // size of result, default: 10
//                .withPageable(Pageable.ofSize(20))
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    /**
     *Prefixes, wildcards & regular expressions
     */
    public SearchHitsResponse searchByPrefixes(String query) {
        PrefixQuery prefixQuery = QueryBuilders
                .prefix()
                .field("tags").value(query)
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.prefix(prefixQuery))
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    public SearchHitsResponse searchByWildcards(String query) {
        WildcardQuery wildcardQuery = QueryBuilders
                .wildcard()
                .field("tags").value(query)
                .wildcard("Past?")
                .wildcard("Bee*")
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.wildcard(wildcardQuery))
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    public SearchHitsResponse searchByRegexp(String query) {
        RegexpQuery regexpQuery = QueryBuilders
                .regexp()
                .field("tags").value(query)
                .build();

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q.regexp(regexpQuery))
                .build();

        SearchHits<Product> productHits = produceResult(searchQuery);
        return serializerSearchHits(productHits);
    }

    /**
     * Match Query
     */
//    public SearchHitsResponse matchQuery(String query) {
//    }

    private SearchHits<Product> produceResult(Query query) {
        SearchHits<Product> productHits = elasticsearchOperations.search(query, Product.class);
        log.info(productHits.toString());

        return productHits;
    }

    private SearchHitsResponse serializerSearchHits(SearchHits<Product> productSearchHits) {
        return SearchHitsResponse.builder()
                .searchHits(productSearchHits.getSearchHits())
                .totalHits(productSearchHits.getTotalHits())
                .maxScore(productSearchHits.getMaxScore())
                .build();
    }
}
