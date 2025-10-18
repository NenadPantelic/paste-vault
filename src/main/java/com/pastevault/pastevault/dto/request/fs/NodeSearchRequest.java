package com.pastevault.pastevault.dto.request.fs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record NodeSearchRequest(@NotBlank String parentPath,
                                String text,
                                // valid values - DIR, FILE, TEXT, LINK
                                List<String> nodeTypes,
                                Integer page,
                                Integer size) {


    private static final int DEFAULT_PAGE_SIZE = 50;

    @JsonCreator
    public NodeSearchRequest(@JsonProperty(value = "parentPath") @NotBlank String parentPath,
                             @JsonProperty(value = "text") String text,
                             @JsonProperty(value = "types") List<String> nodeTypes,
                             @JsonProperty(value = "page", defaultValue = "0") @Min(0) Integer page,
                             @JsonProperty(value = "size", defaultValue = "50") @Min(1) @Max(250) Integer size) {
        this.parentPath = parentPath;
        this.text = text;
        this.nodeTypes = nodeTypes;
        this.page = page == null ? 0 : page;
        this.size = size == null ? DEFAULT_PAGE_SIZE : size;
    }
}
