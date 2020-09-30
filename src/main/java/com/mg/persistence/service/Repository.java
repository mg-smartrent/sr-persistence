package com.mg.persistence.service;

import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface Repository<T> {


    /**
     * Find one bizItem.
     *
     * @param fieldName   - name of the field.
     * @param value       - to search for.
     * @param entityClass - target itemType
     * @param collection  - target collection name
     * @return - result with one bizItem or null in case if not found
     */
    T findOneBy(String fieldName, Object value, Class<T> entityClass, String collection);

    /**
     * Find all the models by criteria.
     *
     * @param fieldName   - name of the field.
     * @param value       - to search for.
     * @param entityClass - target itemType
     * @param collection  - target collection name
     * @return - list of items that matches search criteria
     */
    List<T> findAllBy(String fieldName, Object value, Class<T> entityClass, String collection);

    /**
     * Find all the models by criteria.
     *
     * @param query       - search criteria
     * @param entityClass - item type to search for
     * @param collection  - target collection name
     * @return - list of items that matches search criteria
     */
    List<T> findAll(Query query, Class<T> entityClass, String collection);

    /**
     * Persists model in to the database.
     *
     * @param model       - bizItem to persist.
     * @param entityClass - item type to search for
     * @param collection  - target collection name
     * @return - saved model.
     */
    T save(T model, Class<T> entityClass, String collection);


    /**
     * Persists models in to the database.
     *
     * @param models      - bizItems to persist.
     * @param entityClass - item type to search for
     * @param collection  - target collection name
     */
    void save(List<T> models, Class<T> entityClass, String collection);

    /**
     * Count number of the records.
     *
     * @param query       -count criteria.
     * @param entityClass - target entity class.
     * @param collection  - target collection name
     * @return - count of records that matches the search criteria.
     */
    long count(Query query, Class<T> entityClass, String collection);

}
