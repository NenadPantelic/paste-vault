package com.pastevault.api.dto.request.content;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.pastevault.api.model.StorageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonTypeName("text")
public record NewTextNode(@NotNull @Size(min = 1, max = 16384) String text) implements PasteableItem {

    @Override
    public StorageType getType() {
        return StorageType.TEXT;
    }
}
