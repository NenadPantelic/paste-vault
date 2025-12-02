package com.pastevault.api.service.impl;

import com.mongodb.bulk.BulkWriteResult;
import com.pastevault.api.dto.request.NewTag;
import com.pastevault.api.dto.response.TagDTO;
import com.pastevault.api.model.Tag;
import com.pastevault.api.service.TagService;
import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class TagServiceImpl implements TagService {

    private static final ErrorReport DELETING_TAG_WITH_ASSOCIATIONS_ERROR = ErrorReport.BAD_REQUEST.withErrors(
            "Cannot delete a tag which is associated with some vault nodes"
    );

    private final MongoTemplate mongoTemplate;

    public TagServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TagDTO createTag(NewTag newTag) {
        log.info("Creating a tag: {}", newTag.name());

        String name = newTag.name();
        // TODO: tag name must be valid - no blanks, not null etc.
        try {
            Tag tag = Tag.builder()
                    .name(name)
                    .counter(0)
                    .build();
            tag = mongoTemplate.insert(tag);
            log.info("New tag created: {}", tag);
            return new TagDTO(tag.getName(), tag.getCounter());
        } catch (Exception e) { // TODO: too broad; test
            log.error("Tag  {} creation has failed. Reason: {}", name, e.getMessage(), e);
            throw new ApiException(ErrorReport.BAD_REQUEST.withErrors("Tag name must be unique"));
        }
    }

    @Override
    public void updateTagCounters(List<String> incrementedTags, List<String> decrementedTags) {
        log.info("Updating tag counters: incrementedTags = {}, decrementedTags = {}", incrementedTags, decrementedTags);

        // documents violating the unique constraint will be skipped, but other valid documents
        // in the batch will still be upserted
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Tag.class);

        List<Tag> tagsToIncrement = incrementedTags.stream()
                .map(tag -> Tag.builder().name(tag).build())
                .toList();
        Query tagsToIncrementQuery = new Query(Criteria.where("name").in(incrementedTags));
        Update incUpdate = new Update().inc("counter", 1);
        incUpdate.setOnInsert("count", 1);
        tagsToIncrement.forEach(tag -> {
            bulkOperations.upsert(tagsToIncrementQuery, incUpdate);
        });

        List<Tag> tagsToDecrement = decrementedTags.stream()
                .map(tag -> Tag.builder().name(tag).build())
                .toList();
        Query tagsToDecrementQuery = new Query(
                Criteria.where("name").in(decrementedTags)
                        .and("counter").gt(0)
        );
        Update decUpdate = new Update().inc("counter", -1);
        tagsToDecrement.forEach(tag -> {
            bulkOperations.updateMulti(tagsToDecrementQuery, decUpdate);
        });

        BulkWriteResult bulkWriteResult = bulkOperations.execute();
        log.info("Result of updating tag counters - updated {}/{}",
                bulkWriteResult.getModifiedCount(), incrementedTags.size() + decrementedTags.size()
        );
    }


    @Override
    public void deleteTag(String tag) {
        log.info("Deleting a tag {}", tag);
        Query query = new Query(Criteria.where("name").is(tag));

        Tag tagRecord = mongoTemplate.findOne(query, Tag.class);
        if (tagRecord == null) {
            throw new ApiException(ErrorReport.NOT_FOUND);
        }

        if (tagRecord.getCounter() > 0) {
            throw new ApiException(DELETING_TAG_WITH_ASSOCIATIONS_ERROR);
        }

        mongoTemplate.remove(tagRecord);
    }
}
