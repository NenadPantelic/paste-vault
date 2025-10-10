package com.pastevault.pastevault.controller;

import com.pastevault.pastevault.dto.request.fs.ListDirectoryContentRequest;
import com.pastevault.pastevault.dto.request.fs.NewVaultDirNode;
import com.pastevault.pastevault.dto.request.fs.UpdateVaultDirNode;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;
import com.pastevault.pastevault.service.DirectoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
// AntPathMatcher's combine method will attempt to put a path separator (/) between segments.
// Also, the RequestMappingInfo's path parsing code does this. The former can be replaced easily.
// To allow action-based API paths, define an empty RequestMapping
@RequestMapping
public class DirectoryController {

    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/dirs")
    public VaultNodeDTO createDirectory(@Valid @RequestBody NewVaultDirNode newVaultDirNode) {
        log.info("Received a request to create a new directory.");
        return directoryService.createDirectory(newVaultDirNode);
    }

    @PostMapping("/api/v1/dirs:list")
    public List<VaultNodeDTO> listDirectoryContent(@Valid @RequestBody ListDirectoryContentRequest listDirectoryContentRequest) {
        log.info("Received a request to list the content of a directory.");
        return directoryService.listDirectoryContent(listDirectoryContentRequest);
    }

    @PatchMapping("/api/v1/dirs/{nodeId}:rename")
    public VaultNodeDTO renameDirectory(@PathVariable("nodeId") String nodeId,
                                        @Valid @RequestBody UpdateVaultDirNode updateVaultDirNode) {
        log.info("Received a request to update the directory");
        return directoryService.renameDirectory(nodeId, updateVaultDirNode);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/v1/dirs/{nodeId}")
    public void deleteDirectory(@PathVariable("nodeId") String nodeId) {
        log.info("Received a request to delete a directory.");
        directoryService.deleteDirectory(nodeId);
    }
}
