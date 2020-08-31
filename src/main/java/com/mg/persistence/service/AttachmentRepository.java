package com.mg.persistence.service;


import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface AttachmentRepository<T> {


    /**
     * Find one bizItem.
     *
     * @param fieldName   - name of the field.
     * @param value       - to search for.
     * @param includeData - if true input stream of the file will be attached to the result
     * @param collection  - collection name where the data is stored
     * @return - result with one bizItem or null in case if not found
     */
    T findOneBy(String fieldName, Object value, boolean includeData, String collection);

    /**
     * Find one by ID.
     *
     * @param id          - ID to search for.
     * @param includeData - if true input stream of the file will be attached to the result
     * @param collection  - collection name where the data is stored
     * @return - result with one bizItem or null in case if not found
     */
    T findOneById(Object id, boolean includeData, String collection);

    /**
     * Find all the models by criteria.
     *
     * @param fieldName  - name of the field.
     * @param value      - to search for.
     * @param collection - collection name where the data is stored
     * @return - list of items that matches search criteria
     */
    List<T> findAllBy(String fieldName, Object value, String collection);

    /**
     * Find all the models by criteria.
     *
     * @param query      - search criteria
     * @param collection - collection name where the data is stored
     * @return - list of items that matches search criteria
     */
    List<T> findAll(Query query, String collection);

    /**
     * Persists model in to the database.
     *
     * @param model      - bizItem to persist.
     * @param user       - current user name.
     * @param collection - collection name where the data is stored
     * @return - saved model.
     */
    T save(T model, String user, String collection);


    /**
     * Persists models in to the database.
     *
     * @param models     - bizItems to persist.
     * @param user       - current user name.
     * @param collection - collection name where the data is stored
     */
    void save(List<T> models, String user, String collection);

    /**
     * Remove item from the repository.
     *
     * @param id         - id of the item to delete.
     * @param collection - collection name where the data is stored
     */
    void delete(Object id, String collection);
}
