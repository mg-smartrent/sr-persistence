package com.mg.persistence.service.nosql;


import com.mg.persistence.data.Attachment;
import com.mg.persistence.data.TrackedItem;
import com.mg.persistence.service.AttachmentRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Qualifier(MongoGridFSRepository.QUALIFIER)
public class MongoGridFSRepository<T extends Attachment> implements AttachmentRepository<T> {

    public static final String QUALIFIER = "mongo-grid-fs";
    private final MongoTemplate mongoTemplate;

    public MongoGridFSRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public T findOneBy(final String fieldName, final Object value, final boolean includeData, final String collection) {
        GridFSFile file = getGridFsTemplate(collection).findOne(new Query(Criteria.where(fieldName).is(value)));
        return (T) fileToAttachment(file, includeData, collection);
    }

    @Override
    public T findOneById(final Object id, final boolean includeData, final String collection) {
        return findOneBy("_id", id, includeData, collection);
    }

    @Override
    public T findOneByRelatedItemId(Object relatedItemId, boolean includeData, String collection) {
        return findOneBy(Attachment.Fields.relatedItemId, relatedItemId, includeData, collection);
    }

    @Override
    public List<T> findAllBy(final String fieldName, final Object value, final String collection) {
        return findAll(new Query(Criteria.where(fieldName).is(value)), collection);
    }

    @Override
    public List<T> findAll(final Query query, final String collection) {
        final List<Attachment> attachments = new ArrayList<>();
        final GridFSFindIterable gridFSFiles = getGridFsTemplate(collection).find(query);
        gridFSFiles.forEach(file -> attachments.add(fileToAttachment(file, false, collection)));

        return (List<T>) attachments;
    }

    @Override
    public T save(final T model, final String user, final String collection) {

        DBObject metaData = new BasicDBObject();
        if (model.getCreatedOn() == null) {
            metaData.put(TrackedItem.Fields.createdOn, Instant.now());
            metaData.put(TrackedItem.Fields.createdBy, user);
        }
        metaData.put(TrackedItem.Fields.modifiedOn, Instant.now());
        metaData.put(TrackedItem.Fields.modifiedBy, user);
        metaData.put(Attachment.Fields.type, model.getType());
        metaData.put(Attachment.Fields.name, model.getName());
        metaData.put(Attachment.Fields.relatedItemId, model.getRelatedItemId());
        model.getMetadata().forEach(metaData::put);

        ObjectId id = getGridFsTemplate(collection)
                .store(model.getDataStream(), model.getName(), model.getType().name(), metaData);

        return findOneBy("_id", id.toString(), false, collection);
    }

    @Override
    public void save(final List<T> models, final String user, final String collection) {
        models.parallelStream().forEach(it -> save(it, user, collection));
    }

    @Override
    public void delete(final Object id, final String collection) {
        getGridFsTemplate(collection).delete(new Query(Criteria.where("_id").is(id)));
    }


    private Attachment fileToAttachment(final GridFSFile file, final boolean includeData, final String collection) {
        if (file == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setId(file.getId().toString());
        attachment.setName(file.getFilename());
        final Document meta = file.getMetadata();
        if (meta != null) {

            final Date createdOn = meta.getDate(TrackedItem.Fields.createdOn);
            attachment.setCreatedOn(createdOn != null ? createdOn.toInstant() : null);

            final Date modifiedOn = meta.getDate(TrackedItem.Fields.modifiedOn);
            attachment.setModifiedOn(modifiedOn != null ? modifiedOn.toInstant() : null);

            final Date deleteOn = meta.getDate(TrackedItem.Fields.deletedOn);
            attachment.setDeletedOn(deleteOn != null ? deleteOn.toInstant() : null);

            attachment.setModifiedBy(meta.getString(TrackedItem.Fields.modifiedBy));
            attachment.setCreatedBy(meta.getString(TrackedItem.Fields.createdBy));
            attachment.setDeletedBy(meta.getString(TrackedItem.Fields.deletedBy));
            attachment.setType(Attachment.Type.valueOf(meta.getString(Attachment.Fields.type)));
            attachment.setRelatedItemId(meta.getString(Attachment.Fields.relatedItemId));
        }
        if (includeData) {
            final GridFsResource resource = getGridFsTemplate(collection).getResource(file);
            attachment.setDataStream(resource.getContent());
        }
        return attachment;
    }

    private GridFsTemplate getGridFsTemplate(final String collection) {
        return new GridFsTemplate(
                mongoTemplate.getMongoDbFactory(),
                mongoTemplate.getConverter(),
                collection);
    }
}
