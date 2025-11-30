package com.pastevault.api.service.impl;

import com.mongodb.DuplicateKeyException;
import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.api.constants.SearchConstants;
import com.pastevault.api.dto.request.fs.ListDirectoryContentRequest;
import com.pastevault.api.dto.request.fs.NewVaultDirNode;
import com.pastevault.api.dto.request.fs.UpdateVaultDirNode;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.mapper.VaultNodeMapper;
import com.pastevault.api.model.NodeStatus;
import com.pastevault.api.model.VaultNode;
import com.pastevault.api.model.VaultNodeType;
import com.pastevault.api.repository.VaultNodeRepository;
import com.pastevault.api.service.DirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DirectoryServiceImpl extends CommonFileSystemService implements DirectoryService {

    public DirectoryServiceImpl(VaultNodeRepository vaultNodeRepository) {
        super(vaultNodeRepository);
    }

    @Override
    public VaultNodeDTO createDirectory(NewVaultDirNode newVaultDirNode) {
        log.info("Creating a directory from {}", newVaultDirNode);
        validateName(newVaultDirNode.name());

        String parentPath = newVaultDirNode.parentPath();
        getParentDirNode(parentPath);

        if (!FS_SEPARATOR.equals(parentPath)) {
            parentPath = parentPath + FS_SEPARATOR;
        }

        VaultNode dirNode = VaultNode.builder()
                .parentPath(parentPath)
                .name(newVaultDirNode.name())
                .creatorId(CREATOR_ID)
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

        PageRequest pageRequest = PageRequest.of(page, size, SearchConstants.DEFAULT_DIR_SORT);
        List<VaultNode> nodes = vaultNodeRepository.findByParentPath(parentPath, pageRequest);
        return VaultNodeMapper.mapToDTOList(nodes);
    }

    @Override
    public VaultNodeDTO renameDirectory(String nodeId, UpdateVaultDirNode updateVaultDirNode) {
        log.info("Updating the directory node with id {}", nodeId);
        VaultNode dirNode = getNodeWithTypeCheck(nodeId, VaultNodeType.DIR);

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
        // TODO: delete all offspring nodes
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
