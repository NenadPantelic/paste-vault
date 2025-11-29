package com.pastevault.api.controller;

import com.pastevault.api.dto.request.fs.NewVaultFileNode;
import com.pastevault.api.dto.request.fs.UpdateVaultFileNode;
import com.pastevault.api.dto.response.VaultFileNodeDTO;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.service.FileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VaultNodeDTO createFile(@Valid @RequestBody NewVaultFileNode newVaultFileNode) {
        log.info("Received a request to create a new file.");
        return fileService.createFile(newVaultFileNode);
    }

    @GetMapping("/{nodeId}")
    public VaultFileNodeDTO getFile(@PathVariable("nodeId") String nodeId) {
        log.info("Received a request to get a file.");
        return fileService.getFile(nodeId);
    }

    @PutMapping("/{nodeId}")
    public VaultNodeDTO updateFile(@PathVariable("nodeId") String nodeId,
                                   @Valid @RequestBody UpdateVaultFileNode updateVaultFileNode) {
        log.info("Received a request to update a file.");
        return fileService.updateFile(nodeId, updateVaultFileNode);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{nodeId}")
    public void deleteFile(@PathVariable("nodeId") String nodeId) {
        log.info("Received a request to delete a file.");
        fileService.deleteFile(nodeId);
    }
}
