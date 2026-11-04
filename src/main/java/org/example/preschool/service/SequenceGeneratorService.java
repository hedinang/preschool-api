package org.example.preschool.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private static final String COUNTER_COLLECTION = "sequence_counters";

    private final MongoTemplate mongoTemplate;

    public long next(String sequenceName) {
        Document counter = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("sequence", 1),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Document.class,
                COUNTER_COLLECTION
        );
        if (counter == null || !(counter.get("sequence") instanceof Number sequence)) {
            throw new IllegalStateException("Could not increment sequence: " + sequenceName);
        }
        return sequence.longValue();
    }
}
