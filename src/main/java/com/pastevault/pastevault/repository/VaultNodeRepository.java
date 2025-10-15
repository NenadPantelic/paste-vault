package com.pastevault.pastevault.repository;

import com.pastevault.apicommon.exception.ApiException;
import com.pastevault.apicommon.exception.ErrorReport;
import com.pastevault.pastevault.model.NodeStatus;
import com.pastevault.pastevault.model.VaultNode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface VaultNodeRepository extends MongoRepository<VaultNode, String> {

    default VaultNode findOrNotFound(String nodeId) {
        return this.findById(nodeId).orElseThrow(() -> new ApiException(ErrorReport.NOT_FOUND));
    }

    @Query(value = "{ '_id' : ?0}", delete = true)
    long deleteNodeById(String nodeId);

    @Query(value = "{ '_id' : ?0, creatorId: ?1}", delete = true)
    long deleteNodeByIdAndCreatorId(String nodeId, String creatorId);


    Optional<VaultNode> findByParentPathAndName(String parentPath, String name);

    List<VaultNode> findByParentPath(String parentPath, Pageable pageable);


    @Query("{'parentPath': {$regex: '^?0.*'}}")
    @Update(pipeline = "{$set: {status: ?1}}")
    long setOffspringNodeStatus(String parentPath, NodeStatus nodeStatus);

    @Query("{'parentPath': {$regex: '^?0.*'}}")
    @Update(pipeline = "{$set: {parentPath: ?1}}")
    long renameParentPath(String oldParentPath, String newParentPath);
}
