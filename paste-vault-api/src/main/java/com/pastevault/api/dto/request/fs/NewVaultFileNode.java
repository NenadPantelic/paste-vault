package com.pastevault.api.dto.request.fs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pastevault.api.dto.request.content.NewLinkNode;
import com.pastevault.api.dto.request.content.PasteableItem;
import com.pastevault.api.dto.request.content.NewTextNode;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record NewVaultFileNode(@NotBlank String parentPath,
                               String name,
                               @JsonTypeInfo(
                                       use = JsonTypeInfo.Id.NAME,
                                       include = JsonTypeInfo.As.PROPERTY, // default, just for transparency, do not remove
                                       property = "type")
                               @JsonSubTypes({
                                       @JsonSubTypes.Type(value = NewLinkNode.class, name = "link"),
                                       @JsonSubTypes.Type(value = NewTextNode.class, name = "text")
                               })
                               @JsonProperty("storage") PasteableItem storageNode,
                               @JsonProperty("tags") List<String> tags) {
}
