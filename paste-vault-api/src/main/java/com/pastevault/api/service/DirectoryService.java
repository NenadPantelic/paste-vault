package com.pastevault.api.service;

import com.pastevault.api.dto.request.fs.ListDirectoryContentRequest;
import com.pastevault.api.dto.request.fs.NewVaultDirNode;
import com.pastevault.api.dto.request.fs.UpdateVaultDirNode;
import com.pastevault.api.dto.response.VaultNodeDTO;

import java.util.List;

public interface DirectoryService {

    /**
     * Creates a dir node.
     *
     * @param newVaultDirNode dir node details
     * @return VaultNodeDTO mapped DTO
     */
    VaultNodeDTO createDirectory(NewVaultDirNode newVaultDirNode);


    /**
     * Lists the content of a directory.
     *
     * @param listDirectoryContentRequest containing the necessary filtering data
     */
    List<VaultNodeDTO> listDirectoryContent(ListDirectoryContentRequest listDirectoryContentRequest);

    /**
     * Updates a directory node (if exists)
     *
     * @param nodeId directory node id
     * @param updateVaultDirNode node data for update
     */
    VaultNodeDTO renameDirectory(String nodeId, UpdateVaultDirNode updateVaultDirNode);

    /**
     * Deletes a directory node (if exists).
     *
     * @param nodeId an identifier of a node
     */
    void deleteDirectory(String nodeId);
}
