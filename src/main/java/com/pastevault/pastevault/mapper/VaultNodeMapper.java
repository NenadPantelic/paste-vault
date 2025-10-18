package com.pastevault.pastevault.mapper;

import com.pastevault.pastevault.dto.response.VaultFileNodeDTO;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;
import com.pastevault.pastevault.dto.response.VaultStorageNodeDTO;
import com.pastevault.pastevault.model.VaultNodeType;
import com.pastevault.pastevault.model.StorageNode;
import com.pastevault.pastevault.model.VaultNode;

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
                .storage(mapToStorageNodeDTO(vaultNode.getStorage()))
                .build();
    }

    private static VaultStorageNodeDTO mapToStorageNodeDTO(StorageNode storageNode) {
        if (storageNode == null) {
            return null;
        }

        return VaultStorageNodeDTO.builder()
                .storageType(storageNode.getStorageType())
                .value(storageNode.getValue())
                .build();
    }
}
