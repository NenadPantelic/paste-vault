package com.pastevault.pastevault.service;

import com.pastevault.pastevault.dto.request.fs.NodeSearchRequest;
import com.pastevault.pastevault.dto.response.VaultNodeDTO;

import java.util.List;

public interface FilesystemSearchService {

    List<VaultNodeDTO> searchNodes(NodeSearchRequest nodeSearchRequest);
}
