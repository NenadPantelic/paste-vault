package com.pastevault.pastevault.service.impl;

import com.mongodb.DuplicateKeyException;
import com.pastevault.apicommon.exception.ApiException;
import com.pastevault.apicommon.exception.ErrorReport;
import com.pastevault.pastevault.dto.request.fs.ListDirectoryContentRequest;
import com.pastevault.pastevault.dto.request.fs.NewVaultDirNode;
import com.pastevault.pastevault.dto.request.fs.UpdateVaultDirNode;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;
import com.pastevault.pastevault.mapper.VaultNodeMapper;
import com.pastevault.pastevault.model.NodeStatus;
import com.pastevault.pastevault.model.NodeType;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import com.pastevault.pastevault.service.DirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DirectoryServiceImpl extends CommonFileSystemService implements DirectoryService {

    // almost identical to timestamp sort
    private static final Sort DEFAULT_DIR_SORT = Sort.by(Sort.Order.asc("id"));

    public DirectoryServiceImpl(VaultNodeRepository vaultNodeRepository) {
        super(vaultNodeRepository);
    }

    @Override
    public VaultNodeDTO createDirectory(NewVaultDirNode newVaultDirNode) {
        log.info("Creating a directory from {}", newVaultDirNode);
        validateName(newVaultDirNode.name());

        String parentPath = newVaultDirNode.parentPath();
        getParentDirNode(parentPath);

        if (!CommonFileSystemService.FS_SEPARATOR.equals(parentPath)) {
            parentPath = parentPath + CommonFileSystemService.FS_SEPARATOR;
        }

        VaultNode dirNode = VaultNode.builder()
                .parentPath(parentPath)
                .name(newVaultDirNode.name())
                .creatorId(CommonFileSystemService.CREATOR_ID)
                .build();

        dirNode = trySaveVaultNode(dirNode);
        return VaultNodeMapper.mapToDTO(dirNode);
    }

    @Override
    public List<VaultNodeDTO> listDirectoryContent(ListDirectoryContentRequest listDirectoryContentRequest) {
        int page = listDirectoryContentRequest.page();
        int size = listDirectoryContentRequest.size();
        String parentPath = denormalizePath(listDirectoryContentRequest.parentPath());
        log.info("List directory content: parentPath = {}, page = {}, size = {}", parentPath, page, size);

        PageRequest pageRequest = PageRequest.of(page, size, DEFAULT_DIR_SORT);
        List<VaultNode> nodes = vaultNodeRepository.findByParentPath(parentPath, pageRequest);
        return VaultNodeMapper.mapToDTOList(nodes);
    }

    @Override
    public VaultNodeDTO renameDirectory(String nodeId, UpdateVaultDirNode updateVaultDirNode) {
        log.info("Updating the directory node with id {}", nodeId);
        VaultNode dirNode = getNodeWithTypeCheck(nodeId, NodeType.DIR);

        String currentName = dirNode.getName();
        String newName = updateVaultDirNode.name();

        if (!currentName.equals(newName)) {
            // update node and offspring - set to unavailable because the renaming has to be propagated across dir
            // node and its descendants; to prevent new nodes, deletes and invalid lookups
            updateNodesStatusInSubtree(dirNode, NodeStatus.UNAVAILABLE);
            renameDirWithDescendants(dirNode, newName);
            updateNodesStatusInSubtree(dirNode, NodeStatus.READY);
        }

        return VaultNodeMapper.mapToDTO(dirNode);
    }

    @Override
    public void deleteDirectory(String nodeId) {
        log.info("Deleting a directory node with id {}", nodeId);
        deleteNode(nodeId);
    }

    private VaultNode trySaveVaultNode(VaultNode vaultNode) {
        try {
            return vaultNodeRepository.save(vaultNode);
        } catch (DuplicateKeyException e) {
            String errMessage = String.format("There is already a node with the same name (%s) in the " +
                    "destination directory", vaultNode.getName()
            );
            throw new ApiException(ErrorReport.CONFLICT.withErrors(errMessage));
        } catch (Exception e) {
            log.warn("Could not create a vault node due to {}. Cause: {}", e.getMessage(), e.getCause(), e);
            throw new ApiException(ErrorReport.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateNodesStatusInSubtree(VaultNode rootNode, NodeStatus status) {
        rootNode.setNodeStatus(status);
        String fullPath = denormalizePath(rootNode.getFullPath());
        long updatedNodesCount = vaultNodeRepository.setOffspringNodeStatus(fullPath, status);
        log.info("{} offspring nodes of a node[id = {}, parent path = {}] updated. New status: {}",
                updatedNodesCount, rootNode.getId(), fullPath, status
        );
        // TODO: 2 update commands - switch to MongoTemplate
        vaultNodeRepository.save(rootNode);
    }

    private void renameDirWithDescendants(VaultNode rootNode, String newName) {
        // rename the node
        String oldParentPath = denormalizePath(rootNode.getFullPath());
        rootNode.setName(newName);
        String newParentPath = denormalizePath(rootNode.getFullPath());

        long updatedNodesCount = vaultNodeRepository.renameParentPath(oldParentPath, newParentPath);
        log.info("{} offspring nodes of a node[id = {}, old parent path = {}] updated. New parent path: {}",
                updatedNodesCount, rootNode.getId(), oldParentPath, newParentPath
        );
        // TODO: 2 update commands - switch to MongoTemplate
        vaultNodeRepository.save(rootNode);
    }
}
