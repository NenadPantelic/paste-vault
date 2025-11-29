package com.pastevault.api.controller;

import com.pastevault.api.dto.request.fs.NodeSearchRequest;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.service.FilesystemSearchService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final FilesystemSearchService fsSearchService;

    public SearchController(FilesystemSearchService fsSearchService) {
        this.fsSearchService = fsSearchService;
    }

    @PostMapping
    public List<VaultNodeDTO> search(@Valid @RequestBody NodeSearchRequest searchRequest) {
        log.info("Received a request to search nodes...");
        return fsSearchService.searchNodes(searchRequest);
    }
}
