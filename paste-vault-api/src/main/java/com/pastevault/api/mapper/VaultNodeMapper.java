package com.pastevault.api.mapper;

import com.pastevault.api.dto.response.VaultFileNodeDTO;
import com.pastevault.api.dto.response.VaultNodeDTO;
import com.pastevault.api.dto.response.VaultStorageNodeDTO;
import com.pastevault.api.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class VaultNodeMapper {

    public static VaultNodeDTO mapToDTO(VaultNode vaultNode) {
        if (vaultNode == null) {
            return null;
        }

        return VaultNodeDTO.builder()
                .id(vaultNode.getId())
                .parentPath(vaultNode.getParentPath())
                .name(vaultNode.getName())
                .type(vaultNode.getType().name())
                .tags(vaultNode.getTags())
                .build();
    }

    public static List<VaultNodeDTO> mapToDTOList(List<VaultNode> vaultNodes) {
        if (vaultNodes == null) {
            return null;
        }

        return vaultNodes.stream()
                .map(VaultNodeMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public static VaultFileNodeDTO mapToFileDTO(VaultNode vaultNode) {
        if (vaultNode == null) {
            return null;
        }

        if (vaultNode.getType() == VaultNodeType.DIR) {
            throw new IllegalArgumentException("Invalid node type provided. Expected a file node, got dir node.");
        }

        return VaultFileNodeDTO.builder()
                .id(vaultNode.getId())
                .parentPath(vaultNode.getParentPath())
                .name(vaultNode.getName())
                .tags(vaultNode.getTags())
                .storage(mapToStorageNodeDTO(vaultNode.getStorage()))
                .build();
    }

    private static VaultStorageNodeDTO mapToStorageNodeDTO(StorageNode storageNode) {
        if (storageNode == null) {
            return null;
        }

        String value = storageNode.getStorageType() == StorageType.LINK ?
                ((LinkNode) storageNode).getUrl() :
                ((TextNode) storageNode).getText();

        return VaultStorageNodeDTO.builder()
                .storageType(storageNode.getStorageType())
                .value(value)
                .build();
    }
}
