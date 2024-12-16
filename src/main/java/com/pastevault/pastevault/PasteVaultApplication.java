package com.pastevault.pastevault;

import com.pastevault.kafka_common.admin.KafkaAdminClient;
import com.pastevault.kafka_common.config.KafkaAdminConfig;
import com.pastevault.kafka_common.config.KafkaConsumerConfig;
import com.pastevault.kafka_common.config.KafkaProducerConfig;
import com.pastevault.kafka_common.properties.KafkaConfigProperties;
import com.pastevault.kafka_common.properties.KafkaConsumerConfigProperties;
import com.pastevault.kafka_common.properties.KafkaProducerConfigProperties;
import com.pastevault.pastevault.dto.request.content.LinkNode;
import com.pastevault.pastevault.dto.request.content.TextNode;
import com.pastevault.pastevault.dto.request.fs.NewVaultDirNode;
import com.pastevault.pastevault.dto.request.fs.NewVaultFileNode;
import com.pastevault.pastevault.service.DirectoryService;
import com.pastevault.pastevault.service.FileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;

@ConfigurationPropertiesScan(
        basePackages = {"com.pastevault"},
        basePackageClasses = {
                KafkaConfigProperties.class,
                KafkaProducerConfigProperties.class,
                KafkaConsumerConfigProperties.class
        })
@Import(value = {
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class,
        KafkaAdminConfig.class,
        KafkaAdminClient.class,
        RetryTemplate.class})
@EnableMongoAuditing // for MongoDB timestamps
@SpringBootApplication
public class PasteVaultApplication {

    private final DirectoryService directoryService;
    private final FileService fileService;

    public PasteVaultApplication(DirectoryService directoryService, FileService fileService) {
        this.directoryService = directoryService;
        this.fileService = fileService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PasteVaultApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            NewVaultFileNode node1 = new NewVaultFileNode(
                    "folder-1",
                    "file-1",
                    new TextNode("test-text")
            );

            fileService.createFile(node1);

            NewVaultFileNode node2 = new NewVaultFileNode(
                    "/",
                    "file-1",
                    new LinkNode("https://test.com", false)
            );
            fileService.createFile(node2);

            NewVaultDirNode dir1 = new NewVaultDirNode(
                    "/",
                    "dir-1"
            );
            directoryService.createDirectory(dir1);
        };
    }
}
