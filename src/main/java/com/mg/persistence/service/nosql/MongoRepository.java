package com.mg.persistence.service.nosql;

import com.mg.persistence.service.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier(MongoRepository.QUALIFIER)
public class MongoRepository<T> implements Repository<T> {

    public static final String QUALIFIER = "mongo";
    private final MongoTemplate mongoTemplate;

    public MongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public T findOneBy(final String fieldName, final Object value, final Class<T> entityClass) {
        List<T> results = findAllBy(fieldName, value, entityClass);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> findAllBy(final String fieldName, final Object value, final Class<T> entityClass) {
        Query query = new Query().addCriteria(Criteria.where(fieldName).is(value));

        return mongoTemplate.find(query, entityClass, entityClass.getSimpleName());
    }

    public List<T> findAll(final Query query, final Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass, entityClass.getSimpleName());
    }

    public long count(final Query query, final Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass, entityClass.getSimpleName());
    }

    public T save(final T model) {
        return mongoTemplate.save(model, model.getClass().getSimpleName());
    }

    public void save(final List<T> models) {
        models.forEach(this::save);
    }

}
