package com.team73.studyshare.controller;

import com.team73.studyshare.model.ItemType;
import com.team73.studyshare.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/api/search")
    public List<?> searchItems(
            @RequestParam String query,
            @RequestParam ItemType type
    ) {
        return searchService.searchItems(query, type);
    }
}

