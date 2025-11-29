package com.pastevault.api.service.impl;

import com.pastevault.api.dto.request.content.NewLinkNode;
import com.pastevault.api.dto.request.content.PasteableItem;
import com.pastevault.api.dto.request.content.NewTextNode;
import com.pastevault.api.dto.request.fs.NewVaultFileNode;
import com.pastevault.api.dto.request.fs.UpdateVaultFileNode;
import com.pastevault.api.dto.response.VaultFileNodeDTO;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.mapper.VaultNodeMapper;
import com.pastevault.api.model.*;
import com.pastevault.api.repository.VaultNodeRepository;
import com.pastevault.api.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileServiceImpl extends CommonFileSystemService implements FileService {


    private final VaultNodeRepository vaultNodeRepository;

    public FileServiceImpl(VaultNodeRepository vaultNodeRepository) {
        super(vaultNodeRepository);
        this.vaultNodeRepository = vaultNodeRepository;
    }

    @Override
    public VaultNodeDTO createFile(NewVaultFileNode newVaultFileNode) {
        log.info("Creating a file from {}", newVaultFileNode);
        validateName(newVaultFileNode.name());

        String parentPath = newVaultFileNode.parentPath();
        getParentDirNode(parentPath);

        VaultNode fileNode = VaultNode.builder()
                .parentPath(parentPath + FS_SEPARATOR)
                .name(newVaultFileNode.name())
                .storage(createStorageNode(newVaultFileNode.storageNode()))
                .creatorId(CREATOR_ID)
                .build();

        log.info("Creating a file node: {}", fileNode);
        fileNode = vaultNodeRepository.save(fileNode);
        return VaultNodeMapper.mapToDTO(fileNode);
    }

    @Override
    public VaultFileNodeDTO getFile(String nodeId) {
        log.info("Fetching a file node with id {}", nodeId);
        VaultNode fileNode = getNodeWithTypeCheck(nodeId, VaultNodeType.FILE);
        return VaultNodeMapper.mapToFileDTO(fileNode);
    }

    @Override
    public VaultNodeDTO updateFile(String nodeId, UpdateVaultFileNode updateVaultFileNode) {
        log.info("Updating a node[id = {}] with {}", nodeId, updateVaultFileNode);
        VaultNode fileNode = getNodeWithTypeCheck(nodeId, VaultNodeType.FILE);

        fileNode.setName(updateVaultFileNode.name());
        fileNode.setStorage(createStorageNode(updateVaultFileNode.storageNode()));
        fileNode = vaultNodeRepository.save(fileNode);

        return VaultNodeMapper.mapToDTO(fileNode);
    }

    @Override
    public void deleteFile(String nodeId) {
        log.info("Deleting a file node with id {}", nodeId);
        deleteNode(nodeId);
    }

    private StorageNode createStorageNode(PasteableItem pasteableItem) {
        if (pasteableItem == null) {
            return null;
        }

        StorageType storageType = pasteableItem.getType();
        String value;

        if (storageType == StorageType.LINK) {
            value = ((NewLinkNode) pasteableItem).url();
            return new LinkNode(StorageType.LINK, value);
        } else if (storageType == StorageType.TEXT) {
            value = ((NewTextNode) pasteableItem).text();
            return new TextNode(StorageType.TEXT, value);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported storage type %s", storageType));
        }
    }
}
