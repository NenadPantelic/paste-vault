package com.pastevault.pastevault.dto.request.content;

import com.pastevault.pastevault.model.StorageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TextNode(@NotNull @Size(min = 1, max = 16384) String text) implements PastableItem {

    @Override
    public StorageType getType() {
        return StorageType.TEXT;
    }
}
