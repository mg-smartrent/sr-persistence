package com.mg.persistence.service.nosql;

import com.mg.persistence.data.Change;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

@Log4j2
@Service
@Qualifier(MongoTrackingChangeService.QUALIFIER)
public class MongoTrackingChangeService {

    public static final String QUALIFIER = "mongo-tracking-change";
    private final MongoTemplate mongoTemplate;

    public MongoTrackingChangeService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Async
    public void saveChanges(final Object working,
                            final Object base,
                            final String baseId,
                            final String changesCollection,
                            final String username) {
        try {
            final List<Change> changes = getChanges(working, base, baseId, username);
            if (!CollectionUtils.isEmpty(changes)) {
                log.debug("Saving {} changes into {}", changes.size(), changesCollection);
                mongoTemplate.save(changes, changesCollection);
            }
        } catch (Exception e) {
            log.error("Failed to save changes: ", e);
        }
    }

    private List<Change> getChanges(final Object workingModel,
                                    final Object baseModel,
                                    final String baseModelId,
                                    final String username) {
        final List<Change> changes = new ArrayList<>();
        final String groupId = UUID.randomUUID().toString();
        final DiffNode diff = ObjectDifferBuilder.startBuilding()
                .comparison().ofType(Instant.class).toUseEqualsMethod()
                .and().comparison().ofType(Date.class).toUseEqualsMethod()
                .and().build()
                .compare(workingModel, baseModel);

        ObjectDifferBuilder.buildDefault().compare(workingModel, baseModel);

        if (diff.hasChanges()) {
            diff.visit((node, visit) -> {
                if (!node.hasChildren()) { // Only get diff if the property has no child
                    final Object newValue = node.canonicalGet(workingModel);
                    final Object oldValue = node.canonicalGet(baseModel);

                    Change change = new Change();
                    change.setObjectId(baseModelId);
                    change.setFieldName(node.getPath().toString());
                    change.setGroupId(groupId);
                    change.setNewValue(newValue);
                    change.setOldValue(oldValue);
                    change.setObjectType(workingModel.getClass().getSimpleName());
                    change.setAction(node.getState().name());
                    change.setCreatedOn(Instant.now());
                    change.setModifiedOn(Instant.now());
                    change.setCreatedBy(Optional.ofNullable(username).orElse("Unknown"));
                    change.setModifiedBy(Optional.ofNullable(username).orElse("Unknown"));
                    changes.add(change);
                }
            });
        }
        return changes;
    }

}
