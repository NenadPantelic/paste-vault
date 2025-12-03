package com.pastevault.api;


import com.pastevault.api.model.NodeStatus;
import com.pastevault.api.model.VaultNode;
import com.pastevault.api.repository.VaultNodeRepository;
import com.pastevault.api.service.impl.CommonFileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableMongoAuditing // for MongoDB timestamps
//@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // TODO: check this
public class PasteVaultApiApplication {

    private final VaultNodeRepository vaultNodeRepository;

    public PasteVaultApiApplication(VaultNodeRepository vaultNodeRepository) {
        this.vaultNodeRepository = vaultNodeRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PasteVaultApiApplication.class, args);
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
