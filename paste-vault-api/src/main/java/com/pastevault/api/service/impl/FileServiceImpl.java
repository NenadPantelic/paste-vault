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
import com.pastevault.api.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl extends CommonFileSystemService implements FileService {


    private final VaultNodeRepository vaultNodeRepository;
    private final TagService tagService;

    public FileServiceImpl(VaultNodeRepository vaultNodeRepository, TagService tagService) {
        super(vaultNodeRepository);
        this.vaultNodeRepository = vaultNodeRepository;
        this.tagService = tagService;
    }

    @Transactional
    @Override
    public VaultNodeDTO createFile(NewVaultFileNode newVaultFileNode) {
        log.info("Creating a file from {}", newVaultFileNode);
        validateName(newVaultFileNode.name());

        String parentPath = newVaultFileNode.parentPath();
        getParentDirNode(parentPath);

        List<String> uniqueTags = getUniqueTags(newVaultFileNode.tags());
        VaultNode fileNode = VaultNode.builder()
                .parentPath(parentPath + FS_SEPARATOR)
                .name(newVaultFileNode.name())
                .tags(uniqueTags)
                .storage(createStorageNode(newVaultFileNode.storageNode()))
                .creatorId(CREATOR_ID)
                .build();

        log.info("Creating a file node: {}", fileNode);
        fileNode = vaultNodeRepository.save(fileNode);

        updateNodeTags(List.of(), uniqueTags);

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

        List<String> oldTags = fileNode.getTags();
        List<String> newTags = getUniqueTags(updateVaultFileNode.tags());

        fileNode.setName(updateVaultFileNode.name());
        fileNode.setStorage(createStorageNode(updateVaultFileNode.storageNode()));
        fileNode.setTags(newTags);
        fileNode = vaultNodeRepository.save(fileNode);

        updateNodeTags(oldTags, newTags);

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

    private List<String> getUniqueTags(List<String> tags) {
        return tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .distinct()
                .toList();
    }

    private void updateNodeTags(List<String> oldTags, List<String> newTags) {
        // both oldTags and newTags must contain unique values only
        Set<String> oldTagsSet = new HashSet<>(oldTags);
        Set<String> newTagsSet = new HashSet<>(newTags);

        // present in oldTags, not present in newTagsSet
        List<String> removedTags = oldTags.stream()
                .filter(tag -> !newTagsSet.contains(tag))
                .collect(Collectors.toList());

        // present in newTags, not present in oldTagsSet
        List<String> addedTags = newTags.stream()
                .filter(tag -> !oldTagsSet.contains(tag))
                .collect(Collectors.toList());

        tagService.updateTagCounters(addedTags, removedTags);
    }
}
