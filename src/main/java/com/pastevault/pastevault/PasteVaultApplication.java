package com.pastevault.pastevault;


import com.mongodb.MongoException;
import com.pastevault.pastevault.model.NodeStatus;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import com.pastevault.pastevault.service.impl.CommonFileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Slf4j
@EnableMongoAuditing // for MongoDB timestamps
@SpringBootApplication
public class PasteVaultApplication {

    private final VaultNodeRepository vaultNodeRepository;

    public PasteVaultApplication(VaultNodeRepository vaultNodeRepository) {
        this.vaultNodeRepository = vaultNodeRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PasteVaultApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            try {
                // TODO: create a seeding function for this
                VaultNode rootNode = VaultNode.builder()
                        .parentPath("")
                        .name("/")
                        .creatorId(CommonFileSystemService.CREATOR_ID)
                        .nodeStatus(NodeStatus.READY)
                        .build();

                vaultNodeRepository.save(rootNode);
            } catch (Exception e) {
                log.error("Root node could not be created. It is very likely that the node already exists");
            }
        };
    }
}
