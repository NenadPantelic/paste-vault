package com.pastevault.pastevault.dto.request.fs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pastevault.pastevault.dto.request.content.LinkNode;
import com.pastevault.pastevault.dto.request.content.PasteableItem;
import com.pastevault.pastevault.dto.request.content.TextNode;
import jakarta.validation.constraints.NotBlank;

public record NewVaultFileNode(@NotBlank String parentPath,
                               String name,
                               @JsonTypeInfo(
                                       use = JsonTypeInfo.Id.NAME,
                                       include = JsonTypeInfo.As.PROPERTY, // default, just for transparency, do not remove
                                       property = "type")
                               @JsonSubTypes({
                                       @JsonSubTypes.Type(value = LinkNode.class, name = "link"),
                                       @JsonSubTypes.Type(value = TextNode.class, name = "text")
                               })
                               @JsonProperty("storage") PasteableItem storageNode) {
}
