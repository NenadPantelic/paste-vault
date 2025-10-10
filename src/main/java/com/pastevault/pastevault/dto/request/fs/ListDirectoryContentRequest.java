package com.pastevault.pastevault.dto.request.fs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ListDirectoryContentRequest(String parentPath, Integer page, Integer size) {

    private static final int DEFAULT_PAGE_SIZE = 50;

    @JsonCreator
    public ListDirectoryContentRequest(@JsonProperty(value = "parentPath") @NotBlank String parentPath,
                                       @JsonProperty(value = "page", defaultValue = "0") @Min(0) Integer page,
                                       @JsonProperty(value = "size", defaultValue = "50") @Min(1) @Max(250) Integer size) {
        this.parentPath = parentPath;
        this.page = page == null ? 0 : page;
        this.size = size == null ? DEFAULT_PAGE_SIZE : size;
    }
}
