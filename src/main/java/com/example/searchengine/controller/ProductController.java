package com.example.searchengine.controller;

import com.example.searchengine.entity.Product;
import com.example.searchengine.response.SearchHitsResponse;
import com.example.searchengine.service.FulltextSearchService;
import com.example.searchengine.service.TermSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final TermSearchService termSearchService;
    private final FulltextSearchService fulltextSearchService;

    @GetMapping("/{name}")
    public List<Product> getProductsByName(@PathVariable String name) {
        return termSearchService.getProductByName(name);
    }

    @GetMapping("/search/term/{query}")
    public SearchHitsResponse searchByTags(@PathVariable String query) {
        return termSearchService.searchForTerm(query);
    }

    @GetMapping("/search/ids")
    public SearchHitsResponse retrieveByIds() {
        return termSearchService.retrieveByIds(List.of("07xq84kBNoOlA9uXW6zS", "27xq84kBNoOlA9uXW6zT", "300"));
    }

    @GetMapping("/search/range")
    public SearchHitsResponse searchRanges(String from, String to) {
        return termSearchService.searchRanges(from, to);
    }

    @GetMapping("/search/regexp/{query}")
    public SearchHitsResponse searchRegexp(@PathVariable String query) {
        return termSearchService.searchByRegexp(query);
    }

    @GetMapping("/search/match/{query}")
    public SearchHitsResponse searchMatch(@PathVariable String query) {
        return fulltextSearchService.searchMatch(query);
    }

    @GetMapping("/search/multi-match/{query}")
    public SearchHitsResponse searchMultiMatch(@PathVariable String query) {
        return fulltextSearchService.searchMultiMatch(query);
    }
}
