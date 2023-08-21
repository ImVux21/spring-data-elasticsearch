package com.example.searchengine.response;

import com.example.searchengine.entity.Product;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

@Data
@Builder
public class SearchHitsResponse {
    private long totalHits;
    private float maxScore;
    private List<SearchHit<Product>> searchHits;
}
