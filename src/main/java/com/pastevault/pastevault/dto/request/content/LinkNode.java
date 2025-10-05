package com.pastevault.pastevault.dto.request.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.pastevault.pastevault.model.StorageType;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;


@JsonTypeName("link")
public record LinkNode(@JsonProperty("url") @NotBlank @URL String url,
                       @JsonProperty("download") boolean download) implements PasteableItem {

    @Override
    public StorageType getType() {
        return StorageType.LINK;
    }
}
