package com.pastevault.pastevault.service;

import com.pastevault.pastevault.dto.request.fs.NewVaultFileNode;
import com.pastevault.pastevault.dto.request.fs.UpdateVaultFileNode;
import com.pastevault.pastevault.dto.response.VaultFileNodeDTO;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;

public interface FileService {

    /**
     * Creates a file node.
     *
     * @param newVaultFileNode file node details including optional storage data
     * @return VaultNodeDTO mapped DTO
     */
    VaultNodeDTO createFile(NewVaultFileNode newVaultFileNode);

    /**
     * Gets a file node based on nodeId.
     *
     * @param nodeId an identifier of a node
     * @return VaultNodeDTO (minimal) representation of a node. If the node is not found, raises 404.
     */

    VaultFileNodeDTO getFile(String nodeId);

    /**
     * Creates a file node.
     *
     * @param nodeId              an identifier of a node
     * @param updateVaultFileNode file node details including optional storage data
     * @return VaultNodeDTO mapped DTO
     */
    VaultNodeDTO updateFile(String nodeId, UpdateVaultFileNode updateVaultFileNode);

    /**
     * Deletes a file node.
     * @param nodeId an identifier of a node
     * */
    void deleteFile(String nodeId);
}
