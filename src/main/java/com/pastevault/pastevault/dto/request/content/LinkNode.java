package com.pastevault.pastevault.dto.request.content;

import com.pastevault.pastevault.model.StorageType;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;


public record LinkNode(@NotBlank @URL String url, boolean download) implements PastableItem {

    @Override
    public StorageType getType() {
        return StorageType.LINK;
    }
}
