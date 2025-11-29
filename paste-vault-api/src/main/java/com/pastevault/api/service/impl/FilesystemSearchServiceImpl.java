package com.pastevault.api.service.impl;

import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.api.constants.SearchConstants;
import com.pastevault.api.dto.request.fs.NodeSearchRequest;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.mapper.VaultNodeMapper;
import com.pastevault.api.model.StorageType;
import com.pastevault.api.model.VaultNode;
import com.pastevault.api.model.VaultNodeType;
import com.pastevault.api.service.FilesystemSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilesystemSearchServiceImpl implements FilesystemSearchService {

    private static final List<String> ALL_VAULT_NODE_TYPES = List.of(
            VaultNodeType.DIR.name(), VaultNodeType.FILE.name()
    );

    @Override
    public List<VaultNodeDTO> searchNodes(NodeSearchRequest nodeSearchRequest) {
        log.info("Search nodes: {}", nodeSearchRequest);

        String text = nodeSearchRequest.text();
        List<String> nodeTypes = getValidCompactNodeTypeList(nodeSearchRequest.nodeTypes());

        boolean shouldSearchByText = text != null && !text.isEmpty();
        boolean shouldSearchByType = !nodeTypes.isEmpty() && nodeTypes != ALL_VAULT_NODE_TYPES;
        if (!shouldSearchByText && !shouldSearchByType) {
            throw new ApiException(ErrorReport.BAD_REQUEST.withErrors("When performing search action at least one " +
                    "of the parameters {name, node type(s)} must be provided")
            );
        }

        PageRequest pageRequest = PageRequest.of(
                nodeSearchRequest.page(), nodeSearchRequest.size(), SearchConstants.DEFAULT_DIR_SORT
        );
        String parentPath = nodeSearchRequest.parentPath();
        List<VaultNode> nodes;

        if (shouldSearchByText && shouldSearchByType) {
            nodes = searchNodesByTextAndTypes(parentPath, text, nodeTypes, pageRequest);
        } else if (shouldSearchByText) {
            nodes = searchNodesByText(parentPath, text, pageRequest);
        } else {
            nodes = searchNodesByTypes(parentPath, nodeTypes, pageRequest);
        }

        return VaultNodeMapper.mapToDTOList(nodes);
    }

    List<VaultNode> searchNodesByText(String parentPath, String text, PageRequest pageRequest) {
        return null;
    }

    List<VaultNode> searchNodesByTextAndTypes(String parentPath, String text, List<String> types, PageRequest pageRequest) {
        return null;
    }

    List<VaultNode> searchNodesByTypes(String parentPath, List<String> types, PageRequest pageRequest) {
        return null;
    }

    private List<String> getValidCompactNodeTypeList(List<String> nodeTypes) {
        boolean dirNodeTypePresent = false;
        boolean fileNodeTypePresent = false;

        Set<String> fileTypes = new HashSet<>();

        for (String nodeType : nodeTypes) {
            VaultNodeType vaultNodeType = VaultNodeType.convertIfPossible(nodeType);
            StorageType storageType = StorageType.convertIfPossible(nodeType);
            if (!dirNodeTypePresent && vaultNodeType == VaultNodeType.DIR) {
                dirNodeTypePresent = true;
            } else if (!fileNodeTypePresent) {
                if (vaultNodeType == VaultNodeType.FILE) {
                    fileNodeTypePresent = true;
                } else if (storageType != null) {
                    fileTypes.add(storageType.name());
                    fileNodeTypePresent = fileTypes.size() == StorageType.values().length;
                }
            }

            if (dirNodeTypePresent && fileNodeTypePresent) {
                return ALL_VAULT_NODE_TYPES;
            }
        }

        if (fileNodeTypePresent) {
            return List.of(VaultNodeType.FILE.name());
        }

        List<String> validNodeTypes = new ArrayList<>(fileTypes);
        if (dirNodeTypePresent) {
            validNodeTypes.add(VaultNodeType.DIR.name());
        }

        return validNodeTypes;
    }
}
