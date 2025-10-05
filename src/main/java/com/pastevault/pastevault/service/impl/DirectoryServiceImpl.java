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

import java.util.ArrayList;
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
        String parentPath = listDirectoryContentRequest.parentPath();
        log.info("List directory content: parentPath = {}, page = {}, size = {}", parentPath, page, size);

        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by(Sort.Order.asc("id")));
        String normalizedParentPath = parentPath.endsWith(CommonFileSystemService.FS_SEPARATOR) ?
                parentPath :
                parentPath + CommonFileSystemService.FS_SEPARATOR;
        List<VaultNode> nodes = vaultNodeRepository.findByParentPath(normalizedParentPath, pageRequest);
        return VaultNodeMapper.mapToDTOList(nodes);
    }

    // TODO: not used at the moment
    @Override
    public VaultNodeDTO updateDirectory(String nodeId, UpdateVaultDirNode updateVaultDirNode) {
        log.info("Updating the directory node with id {}", nodeId);
        VaultNode dirNode = getNodeWithTypeCheck(nodeId, NodeType.DIR);

        String currentName = dirNode.getName();
        String newName = updateVaultDirNode.name();

        if (!currentName.equals(newName)) {
            // update node and offspring - set to unavailable because the renaming has to be propagated across the dir
            // node and its descendants
            dirNode.setNodeStatus(NodeStatus.UNAVAILABLE);
            renameDirWithDescendants(dirNode, newName);
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

    private void renameDirWithDescendants(VaultNode dirNode, String newName) {
        // rename the node
        dirNode.setName(newName);
        List<VaultNode> updatedNodes = setOffspringNodesStatus(dirNode.getFullPath(), NodeStatus.UNAVAILABLE);
        updatedNodes.add(dirNode);
        vaultNodeRepository.saveAll(updatedNodes);
    }

    private List<VaultNode> setOffspringNodesStatus(String path, NodeStatus status) {
        // set all nodes which parent path starts with `path` to `status`
        return new ArrayList<>();
    }
}
