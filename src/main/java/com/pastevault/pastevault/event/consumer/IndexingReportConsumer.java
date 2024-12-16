package com.pastevault.pastevault.event.consumer;

import com.pastevault.kafka_common.admin.KafkaAdminClient;
import com.pastevault.kafka_common.event.KafkaConsumer;
import com.pastevault.kafka_common.model.AvroIndexingReport;
import com.pastevault.kafka_common.properties.KafkaConfigProperties;
import com.pastevault.kafka_common.properties.KafkaConsumerConfigProperties;
import com.pastevault.pastevault.model.NodeStatus;
import com.pastevault.pastevault.model.VaultNode;
import com.pastevault.pastevault.repository.VaultNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class IndexingReportConsumer implements KafkaConsumer<String, AvroIndexingReport> {

    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigProperties kafkaConfigProperties;
    private final KafkaConsumerConfigProperties kafkaConsumerConfigProperties;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final VaultNodeRepository nodeRepository;

    public IndexingReportConsumer(KafkaAdminClient kafkaAdminClient,
                                  KafkaConfigProperties kafkaConfigProperties,
                                  KafkaConsumerConfigProperties kafkaConsumerConfigProperties,
                                  KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                  VaultNodeRepository nodeRepository) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.kafkaConsumerConfigProperties = kafkaConsumerConfigProperties;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.nodeRepository = nodeRepository;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent applicationStartedEvent) {
        kafkaAdminClient.checkIfTopicsCreated();
        log.info("Topics with names {} are ready for operations", kafkaConfigProperties.topicNamesToCreate());
        Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(
                kafkaConsumerConfigProperties.consumerGroupId())
        ).start();
    }

    @Override
    @KafkaListener(id = "${kafka.consumer.consumer-group-id}", topics = "${kafka.consumer.topic}")
    public void receive(@Payload AvroIndexingReport indexingReport,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset) {
        log.info("[threadId={}] received an indexing report with key {}, partition {} and offset {}.",
                Thread.currentThread().getId(), key, partition, offset);
        String nodeId = indexingReport.getNodeId();

        if (indexingReport.getIndexed()) {
            Optional<VaultNode> vaultNodeOptional = nodeRepository.findById(nodeId);
            if (vaultNodeOptional.isPresent()) {
                log.info("Node {} has been successfully indexed.", nodeId);
                VaultNode vaultNode = vaultNodeOptional.get();
                vaultNode.setNodeStatus(NodeStatus.READY);
                nodeRepository.save(vaultNode);
            }
        } else {
            log.warn("Node {} has not been indexed.", nodeId);
            nodeRepository.deleteById(nodeId);
        }
    }
}