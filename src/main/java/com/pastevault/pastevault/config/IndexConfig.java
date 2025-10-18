package com.pastevault.pastevault.config;

import com.pastevault.pastevault.model.VaultNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

@Component
public class IndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndices() {
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("storage.text")
                .onField("name")
                .build();

        mongoTemplate.indexOps(VaultNode.class).ensureIndex(textIndex);
    }
}
