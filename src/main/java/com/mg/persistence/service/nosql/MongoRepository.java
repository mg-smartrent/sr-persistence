package com.mg.persistence.service.nosql;

import com.mg.persistence.data.TrackedItem;
import com.mg.persistence.service.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier(MongoRepository.QUALIFIER)
public class MongoRepository<T extends TrackedItem> implements Repository<T> {

    public static final String QUALIFIER = "mongo-repo";
    private final MongoTemplate mongoTemplate;
    private final MongoTrackingChangeService changeService;

    public MongoRepository(final MongoTemplate mongoTemplate,
                           final MongoTrackingChangeService changeService) {
        this.mongoTemplate = mongoTemplate;
        this.changeService = changeService;
    }


    public T findOneBy(final String fieldName,
                       final Object value,
                       final Class<T> entityClass,
                       final String collection) {
        List<T> results = findAllBy(fieldName, value, entityClass, collection);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> findAllBy(final String fieldName,
                             final Object value,
                             final Class<T> entityClass,
                             final String collection) {
        Query query = new Query().addCriteria(Criteria.where(fieldName).is(value));

        return mongoTemplate.find(query, entityClass, collection);
    }

    public List<T> findAll(final Query query, final Class<T> entityClass, final String collection) {
        return mongoTemplate.find(query, entityClass, collection);
    }

    public long count(final Query query, final Class<T> entityClass, final String collection) {
        return mongoTemplate.count(query, entityClass, collection);
    }

    public T save(final T model, final Class<T> entityClass, final String collection) {
        final T baseModel = findOneBy(TrackedItem.Fields.id, model.getId(), entityClass, collection);
        final T workingModel = mongoTemplate.save(model, collection);
        if (baseModel != null) {
            String changesCollection = collection + "_changes";
            changeService.saveChanges(
                    workingModel, baseModel, changesCollection, workingModel.getModifiedBy());
        }

        return workingModel;
    }

    public void save(final List<T> models, final Class<T> entityClass, final String collection) {
        models.forEach(it -> save(it, entityClass, collection));
    }


}
