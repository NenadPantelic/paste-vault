package com.pastevault.api.repository;

import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.api.model.NodeStatus;
import com.pastevault.api.model.VaultNode;
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

    @Query("{'parentPath': {$regex: '^?0.*'}, '$text': { '$search': ?1 }}")
    List<VaultNode> searchNodes(String parentPath, String text, Pageable pageable);

    @Query("{'parentPath': {$regex: '^?0.*'}, '$text': { '$search': ?0 }, 'storage.text': {$exists: true}}")
    List<VaultNode> searchFiles(String parentPath, String text, Pageable pageable);

    @Query("{'parentPath': {$regex: '^?0.*'}, '$text': { '$search': ?0 }, 'storage.text': {$exists: false}}")
    List<VaultNode> searchFolders(String parentPath, String text, Pageable pageable);

    @Query("{'parentPath': {$regex: '^?0.*'}}")
    @Update(pipeline = "{$set: {status: ?1}}")
    long setOffspringNodeStatus(String parentPath, NodeStatus nodeStatus);

    @Query("{'parentPath': {$regex: '^?0.*'}}")
    @Update(pipeline = "{$set: {parentPath: ?1}}")
    long renameParentPath(String oldParentPath, String newParentPath);


}
