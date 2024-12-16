package com.pastevault.pastevault.service.impl;

import com.pastevault.pastevault.dto.request.fs.ListDirectoryContentRequest;
import com.pastevault.pastevault.dto.request.fs.NewVaultDirNode;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;
import com.pastevault.pastevault.event.producer.NodeIndexingManager;
import com.pastevault.pastevault.mapper.VaultNodeMapper;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import com.pastevault.pastevault.service.DirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final VaultNodeRepository vaultNodeRepository;
    private final CommonFileSystemService fileSystemService;
    private final NodeIndexingManager nodeIndexingManager;

    public DirectoryServiceImpl(VaultNodeRepository vaultNodeRepository,
                                CommonFileSystemService fileSystemService,
                                NodeIndexingManager nodeIndexingManager) {
        this.vaultNodeRepository = vaultNodeRepository;
        this.fileSystemService = fileSystemService;
        this.nodeIndexingManager = nodeIndexingManager;
    }

    @Override
    public VaultNodeDTO createDirectory(NewVaultDirNode newVaultDirNode) {
        log.info("Creating a directory from {}", newVaultDirNode);
        String parentPath = newVaultDirNode.parentPath();
//        fileSystemService.getParentDirNode(parentPath);

        VaultNode dirNode = VaultNode.builder()
                .parentPath(parentPath + CommonFileSystemService.FS_SEPARATOR)
                .name(newVaultDirNode.name())
                .creatorId(CommonFileSystemService.CREATOR_ID)
                .build();
        vaultNodeRepository.save(dirNode);

        nodeIndexingManager.index(dirNode);
        return VaultNodeMapper.mapToDTO(dirNode);
    }

    @Override
    public List<VaultNodeDTO> listDirectoryContent(ListDirectoryContentRequest listDirectoryContentRequest) {
        int page = listDirectoryContentRequest.page();
        int size = listDirectoryContentRequest.size();
        String parentPath = listDirectoryContentRequest.parentPath();
        log.info("List directory content: parentPath = {}, page = {}, size = {}", parentPath, page, size);

        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by(Sort.Order.asc("id")));
        String normalizedParentPath = parentPath.endsWith(CommonFileSystemService.FS_SEPARATOR) ?
                parentPath :
                parentPath + CommonFileSystemService.FS_SEPARATOR;
        List<VaultNode> nodes = vaultNodeRepository.findByParentPath(normalizedParentPath, pageRequest);
        return VaultNodeMapper.mapToDTOList(nodes);
    }

    @Override
    public void deleteDirectory(String nodeId) {
        log.info("Deleting a directory node with id {}", nodeId);
        fileSystemService.deleteNode(nodeId);
    }
}
