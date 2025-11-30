package com.pastevault.api.service.impl;

import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.api.context.Role;
import com.pastevault.api.context.UserContext;
import com.pastevault.api.context.UserContextHolder;
import com.pastevault.api.model.NodeStatus;
import com.pastevault.api.model.VaultNode;
import com.pastevault.api.model.VaultNodeType;
import com.pastevault.api.repository.VaultNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommonFileSystemService {

    public static final String FS_SEPARATOR = "/";
    public static final String CREATOR_ID = "<<CREATOR-ID>>";

    protected final VaultNodeRepository vaultNodeRepository;

    public CommonFileSystemService(VaultNodeRepository vaultNodeRepository) {
        this.vaultNodeRepository = vaultNodeRepository;
    }

    /**
     * Deletes a node with the given id.
     * If the node does not exist, it throws an exception.
     *
     * @param nodeId an id of the node that should be deleted
     * @throws ApiException(ErrorReport.UNAUTHORIZED) if the user is not authenticated or
     *                                                ApiException(ErrorReport.NOT_FOUND) if the node does not exist
     */
    public void deleteNode(String nodeId) {
        UserContext userContext = UserContextHolder.get().orElseThrow(() -> new ApiException(ErrorReport.UNAUTHORIZED));
        long numOfDeletedNodes = userContext.role() == Role.ADMIN ?
                vaultNodeRepository.deleteNodeById(nodeId) :
                vaultNodeRepository.deleteNodeByIdAndCreatorId(nodeId, userContext.userId());

        // deleteById from Spring Boot 3.x does not throw an EmptyResultDataAccessException
        // exception
        if (numOfDeletedNodes == 0) {
            throw new ApiException(ErrorReport.NOT_FOUND);
        }
    }

    /**
     * Retrieves a directory node (if exists) by its full path (parentPath/name).
     *
     * @param path full path to node (parentPath/name)
     * @return vault node (if found)
     * @throws ApiException (BAD_REQUEST - node is not a dir node, NOT_FOUND - node not found)
     */
    public VaultNode getParentDirNode(String path) {
        log.info("Retrieving a parent dir node by its full path: {}", path);
        VaultNode dirNode = getNodeByFullPath(path).orElseThrow(() -> new ApiException(ErrorReport.NOT_FOUND));
        authorizeIfCreatorOrAdmin(dirNode.getCreatorId());

        if (dirNode.getType() != VaultNodeType.DIR) {
            throw new ApiException(ErrorReport.BAD_REQUEST.withErrors("Expected a dir node, got file node"));
        }

        if (dirNode.getNodeStatus() != NodeStatus.READY) {
            log.warn("Dir node {} is not ready for use", dirNode);
            throw new ApiException(ErrorReport.NOT_FOUND);
        }

        return dirNode;
    }

    /**
     * Retrieves a node (if exists) by its full path (parentPath/name).
     *
     * @param path full path to node (parentPath/name)
     * @return optional of vault node (if found)
     */
    public Optional<VaultNode> getNodeByFullPath(String path) {
        log.info("Retrieving a node by full path: {}", path);
        String parentPath, name;

        if (path.length() == 1) {
            if (!FS_SEPARATOR.equals(path)) {
                throw new ApiException(ErrorReport.BAD_REQUEST);
            }
            parentPath = "";
            name = "/";
        } else {
            int parentPathNameSeparationIdx = path.lastIndexOf(FS_SEPARATOR);
            parentPath = path.substring(0, parentPathNameSeparationIdx + 1);
            name = path.substring(parentPathNameSeparationIdx + 1);
        }

        Optional<VaultNode> nodeOptional = vaultNodeRepository.findByParentPathAndName(parentPath, name);
        nodeOptional.ifPresent(node -> authorizeIfCreatorOrAdmin(node.getCreatorId()));
        return nodeOptional;
    }

    public VaultNode getNodeWithTypeCheck(String nodeId, VaultNodeType vaultNodeType) {
        VaultNode vaultNode = vaultNodeRepository.findOrNotFound(nodeId);
        authorizeIfCreatorOrAdmin(vaultNode.getCreatorId());

        if (vaultNode.getType() != vaultNodeType) {
            log.warn("Invalid node type for id {}. Expected a {} node, got {} node.", nodeId, vaultNodeType, vaultNode.getType());
            throw new ApiException(ErrorReport.NOT_FOUND);
        }

        return vaultNode;
    }

    private void authorizeIfCreatorOrAdmin(String creatorId) {
        UserContext userContext = UserContextHolder.get().orElseThrow(() -> new ApiException(ErrorReport.UNAUTHORIZED));
        String userId = userContext.userId();
        Role role = userContext.role();

        if (!creatorId.equals(userId) && role != Role.ADMIN) {
            throw new ApiException(ErrorReport.FORBIDDEN);
        }
    }

    // name must not contain `/`
    protected void validateName(String name) {
        if (name == null || name.isEmpty() || name.contains("/")) {
            throw new IllegalArgumentException("Name is a required field that must not contain '\\'");
        }
    }

    protected String denormalizePath(String path) {
        if (FS_SEPARATOR.equals(path)) {
            return path;
        }

        return path.endsWith(FS_SEPARATOR) ? path : path + FS_SEPARATOR;
    }
}