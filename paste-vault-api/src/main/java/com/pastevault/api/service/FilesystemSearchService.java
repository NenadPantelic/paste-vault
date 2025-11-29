package com.pastevault.api.service;

import com.pastevault.api.dto.request.fs.NodeSearchRequest;
import com.pastevault.api.dto.response.VaultNodeDTO;

import java.util.List;

public interface FilesystemSearchService {

    List<VaultNodeDTO> searchNodes(NodeSearchRequest nodeSearchRequest);
}
