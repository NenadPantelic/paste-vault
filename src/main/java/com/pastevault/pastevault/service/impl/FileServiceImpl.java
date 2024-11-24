package com.pastevault.pastevault.service.impl;

import com.pastevault.pastevault.dto.request.content.LinkNode;
import com.pastevault.pastevault.dto.request.content.PastableItem;
import com.pastevault.pastevault.dto.request.content.TextNode;
import com.pastevault.pastevault.dto.request.fs.NewVaultFileNode;
import com.pastevault.pastevault.dto.request.fs.UpdateVaultFileNode;
import com.pastevault.pastevault.dto.response.VaultFileNodeDTO;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;
import com.pastevault.pastevault.mapper.VaultNodeMapper;
import com.pastevault.pastevault.model.NodeType;
import com.pastevault.pastevault.model.StorageNode;
import com.pastevault.pastevault.model.StorageType;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import com.pastevault.pastevault.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileServiceImpl implements FileService {


    private final VaultNodeRepository vaultNodeRepository;
    private final CommonFileSystemService fileSystemService;

    public FileServiceImpl(VaultNodeRepository vaultNodeRepository,
                           CommonFileSystemService fileSystemService) {
        this.vaultNodeRepository = vaultNodeRepository;
        this.fileSystemService = fileSystemService;
    }

    @Override
    public VaultNodeDTO createFile(NewVaultFileNode newVaultFileNode) {
        log.info("Creating a file from {}", newVaultFileNode);

        String parentPath = newVaultFileNode.parentPath();
        fileSystemService.getParentDirNode(parentPath);

        VaultNode fileNode = VaultNode.builder()
                .parentPath(parentPath + CommonFileSystemService.FS_SEPARATOR)
                .name(newVaultFileNode.name())
                .storageNode(createStorageNode(newVaultFileNode.storageNode()))
                .creatorId(CommonFileSystemService.CREATOR_ID)
                .build();

        return VaultNodeMapper.mapToDTO(fileNode);
    }

    @Override
    public VaultFileNodeDTO getFile(String nodeId) {
        log.info("Fetching a file node with id {}", nodeId);
        VaultNode fileNode = fileSystemService.getNodeWithTypeCheck(nodeId, NodeType.FILE);
        return VaultNodeMapper.mapToFileDTO(fileNode);
    }

    @Override
    public VaultNodeDTO updateFile(String nodeId, UpdateVaultFileNode updateVaultFileNode) {
        log.info("Updating a node[id = {}] with {}", nodeId, updateVaultFileNode);
        VaultNode fileNode = fileSystemService.getNodeWithTypeCheck(nodeId, NodeType.FILE);

        fileNode.setName(updateVaultFileNode.name());
        fileNode.setStorageNode(createStorageNode(updateVaultFileNode.storageNode()));
        fileNode = vaultNodeRepository.save(fileNode);

        return VaultNodeMapper.mapToDTO(fileNode);
    }

    @Override
    public void deleteFile(String nodeId) {
        log.info("Deleting a file node with id {}", nodeId);
        fileSystemService.deleteNode(nodeId);
    }

    private StorageNode createStorageNode(PastableItem pastableItem) {
        if (pastableItem == null) {
            return null;
        }

        StorageType storageType = pastableItem.getType();
        String value;

        if (storageType == StorageType.LINK) {
            value = ((LinkNode) pastableItem).url();
        } else if (storageType == StorageType.TEXT) {
            value = ((TextNode) pastableItem).text();
        } else {
            throw new IllegalArgumentException(String.format("Unsupported storage type %s", storageType));
        }

        return new StorageNode(storageType, value);
    }
}
