package com.pastevault.pastevault.repository;

import com.pastevault.apicommon.exception.ApiException;
import com.pastevault.apicommon.exception.ErrorReport;
import com.pastevault.pastevault.model.VaultNode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VaultNodeRepository extends MongoRepository<VaultNode, String> {

    default VaultNode findOrNotFound(String nodeId) {
        return this.findById(nodeId).orElseThrow(() -> new ApiException(ErrorReport.NOT_FOUND));
    }

    Optional<VaultNode> findByParentPathAndName(String parentPath, String name);

    List<VaultNode> findByParentPath(String parentPath, Pageable pageable);
}
