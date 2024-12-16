package com.pastevault.pastevault.event.producer;

import com.pastevault.kafka_common.admin.KafkaAdminClient;
import com.pastevault.kafka_common.event.KafkaProducer;
import com.pastevault.kafka_common.model.AvroVaultNode;
import com.pastevault.kafka_common.properties.KafkaProducerConfigProperties;
import com.pastevault.pastevault.model.VaultNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NodeIndexingManager {

    private final KafkaProducerConfigProperties kafkaProducerConfigProperties;
    private final KafkaProducer<String, AvroVaultNode> kafkaProducer;

    public NodeIndexingManager(KafkaProducerConfigProperties kafkaProducerConfigProperties,
                               KafkaProducer<String, AvroVaultNode> kafkaProducer,
                               KafkaAdminClient kafkaAdminClient) {
        this.kafkaProducerConfigProperties = kafkaProducerConfigProperties;
        this.kafkaProducer = kafkaProducer;
        kafkaAdminClient.createTopics();
    }

    public void index(VaultNode node) {
        log.info("Getting ready to index {}. Sending to Kafka topic {}", node.getId(), kafkaProducerConfigProperties.topic());
        AvroVaultNode vaultAvroNode = AvroNodeMapper.map(node);
        kafkaProducer.send(kafkaProducerConfigProperties.topic(), node.getCreatorId(), vaultAvroNode);
    }
}
