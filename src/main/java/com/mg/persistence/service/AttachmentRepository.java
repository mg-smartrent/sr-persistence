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
     * @return - result with one bizItem or null in case if not found
     */
    T findOneBy(String fieldName, Object value, boolean includeData);

    /**
     * Find all the models by criteria.
     *
     * @param fieldName - name of the field.
     * @param value     - to search for.
     * @return - list of items that matches search criteria
     */
    List<T> findAllBy(String fieldName, Object value);

    /**
     * Find all the models by criteria.
     *
     * @param query - search criteria
     * @return - list of items that matches search criteria
     */
    List<T> findAll(Query query);

    /**
     * Persists model in to the database.
     *
     * @param model - bizItem to persist.
     * @param user  - current user name.
     * @return - saved model.
     */
    T save(T model, String user);


    /**
     * Persists models in to the database.
     *
     * @param models - bizItems to persist.
     * @param user   - current user name.
     */
    void save(List<T> models, String user);

    /**
     * Remove item from the repository.
     *
     * @param id - id of the item to delete.
     */
    void delete(Object id);
}
