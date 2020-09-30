package com.mg.persistence.service.nosql;

import com.mg.persistence.data.Change;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@Qualifier(MongoTrackingChangeService.QUALIFIER)
public class MongoTrackingChangeService {

    public static final String QUALIFIER = "mongo-tracking-change";
    private final MongoTemplate mongoTemplate;

    public MongoTrackingChangeService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveChanges(final Object workingModel,
                            final Object baseModel,
                            final String changesCollection,
                            final String username) {
        final List<Change> changes = getChanges(workingModel, baseModel, username);
        if (!CollectionUtils.isEmpty(changes)) {
            log.debug("Saving {} changes into {}", changes.size(), changesCollection);
            mongoTemplate.save(changes, changesCollection);
        }
    }

    private List<Change> getChanges(final Object workingModel, final Object baseModel, final String username) {
        final List<Change> changes = new ArrayList<>();
        final String groupId = UUID.randomUUID().toString();
        final DiffNode diff = ObjectDifferBuilder.buildDefault().compare(workingModel, baseModel);

        if (diff.hasChanges()) {
            diff.visit((node, visit) -> {
                if (!node.hasChildren()) { // Only get diff if the property has no child
                    final Object newValue = node.canonicalGet(workingModel);
                    final Object oldValue = node.canonicalGet(baseModel);

                    Change change = new Change();
                    change.setFieldName(node.getPropertyName());
                    change.setGroupId(groupId);
                    change.setNewValue(newValue);
                    change.setOldValue(oldValue);
                    change.setObjectType(workingModel.getClass().getSimpleName());
                    change.setAction(getChangeAction(node));
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

    private String getChangeAction(final DiffNode node) {
        if (node.isAdded()) {
            return "added";
        } else if (node.isChanged()) {
            return "changed";
        } else if (node.isRemoved()) {
            return "deleted";
        } else {
            return "unknown";
        }
    }
}
