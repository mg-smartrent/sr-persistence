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
     * @return - result with one bizItem or null in case if not found
     */
    T findOneBy(String fieldName, Object value, Class<T> entityClass);

    /**
     * Find all the models by criteria.
     *
     * @param fieldName   - name of the field.
     * @param value       - to search for.
     * @param entityClass - target itemType
     * @return - list of items that matches search criteria
     */
    List<T> findAllBy(String fieldName, Object value, Class<T> entityClass);

    /**
     * Find all the models by criteria.
     *
     * @param query       - search criteria
     * @param entityClass - item type to search for
     * @return - list of items that matches search criteria
     */
    List<T> findAll(Query query, Class<T> entityClass);

    /**
     * Persists model in to the database.
     *
     * @param model - bizItem to persist.
     * @return - saved model.
     */
    T save(T model);


    /**
     * Persists models in to the database.
     *
     * @param models - bizItems to persist.
     */
    void save(List<T> models);

    /**
     * Count number of the records.
     *
     * @param query -count criteria.
     * @param entityClass - target entity class.
     * @return - count of records that matches the search criteria.
     */
    long count(Query query, Class<T> entityClass);

}
