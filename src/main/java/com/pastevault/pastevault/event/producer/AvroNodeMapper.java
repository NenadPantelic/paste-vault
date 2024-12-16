package com.pastevault.pastevault.event.producer;

import com.pastevault.kafka_common.model.AvroVaultNode;
import com.pastevault.pastevault.model.VaultNode;

public class AvroNodeMapper {

    public static AvroVaultNode map(VaultNode node) {
        if (node == null) {
            return null;
        }

        return AvroVaultNode.newBuilder()
                .setNodeId(node.getId())
                .setParentPath(node.getParentPath())
                .setName(node.getName())
                .setCreatorId(node.getCreatorId())
                .setType(node.getType().name())
                .setCreatedAt(node.getCreatedAt().toEpochMilli())
                .setUpdatedAt(node.getUpdatedAt().toEpochMilli())
                .build();
    }
}
