package com.pastevault.pastevault.service.impl;

import com.pastevault.apicommon.exception.ApiException;
import com.pastevault.apicommon.exception.ErrorReport;
import com.pastevault.pastevault.model.NodeStatus;
import com.pastevault.pastevault.model.NodeType;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * @throws ApiException(ErrorReport.NOT_FOUND) if the node does not exist
     */
    public void deleteNode(String nodeId) {
        // deleteById from Spring Boot 3.x does not throw an EmptyResultDataAccessException
        // exception
        if (vaultNodeRepository.deleteNodeById(nodeId) == 0) {
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
        if (dirNode.getType() != NodeType.DIR) {
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

        // TODO: add authz check
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

        return vaultNodeRepository.findByParentPathAndName(parentPath, name);
    }


    public VaultNode getNodeWithTypeCheck(String nodeId, NodeType nodeType) {
        // TODO: add authz check
        VaultNode vaultNode = vaultNodeRepository.findOrNotFound(nodeId);

        if (vaultNode.getType() != nodeType) {
            log.warn("Invalid node type for id {}. Expected a {} node, got {} node.", nodeId, nodeType, vaultNode.getType());
            throw new ApiException(ErrorReport.NOT_FOUND);
        }

        return vaultNode;
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
