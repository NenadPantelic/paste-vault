package com.pastevault.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "nodes")
@CompoundIndexes({
        @CompoundIndex(name = "parent_path__name", def = "{'parentPath' : 1, 'name': 1}", unique = true)
})
public class VaultNode {

    @Id
    private String id;
    @Indexed
    private String parentPath;
    private String name;
    private String creatorId;
    private StorageNode storage;
    @Builder.Default
    private NodeStatus nodeStatus = NodeStatus.READY;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public VaultNodeType getType() {
        return storage != null ? VaultNodeType.FILE : VaultNodeType.DIR;
    }

    public String getFullPath() {
        return String.format("%s%s", parentPath, name);
    }
}
