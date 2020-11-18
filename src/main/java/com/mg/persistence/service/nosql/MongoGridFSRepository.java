package com.mg.persistence.service.nosql;


import com.mg.persistence.data.Attachment;
import com.mg.persistence.data.Attachment.AttachmentType;
import com.mg.persistence.data.TrackedItem;
import com.mg.persistence.service.AttachmentRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.SneakyThrows;
import org.bson.BsonObjectId;
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
    public T findOneBy(final String fieldName,
                       final Object value,
                       final AttachmentType type,
                       final boolean includeData,
                       final String collection) {

        Query query = new Query(Criteria
                .where(fieldName).is(value)
                .and("metadata.type").is(type.name()));
        GridFSFile file = getGridFsTemplate(collection).findOne(query);

        return (T) fileToAttachment(file, includeData, collection);
    }

    @Override
    public T findOneById(final Object id,
                         final AttachmentType type,
                         final boolean includeData,
                         final String collection) {
        return findOneBy("_id", id, type, includeData, collection);
    }

    @Override
    public T findOneByRelatedItemId(final Object relatedItemId,
                                    final AttachmentType type,
                                    final boolean includeData,
                                    final String collection) {
        String fieldName = "metadata." + Attachment.Fields.relatedItemId;
        return findOneBy(fieldName, relatedItemId, type, includeData, collection);
    }

    @Override
    public List<T> findAllByRelatedItemId(final Object relatedItemId,
                                          final AttachmentType type,
                                          final boolean includeData,
                                          final String collection) {
        String fieldName = "metadata." + Attachment.Fields.relatedItemId;
        Query query = new Query(Criteria
                .where(fieldName).is(relatedItemId)
                .and("metadata.type").is(type.name()));

        return findAll(query, includeData, collection);
    }

    @Override
    public List<T> findAllBy(final String fieldName, final Object value, final String collection) {
        return findAll(new Query(Criteria.where(fieldName).is(value)), collection);
    }

    @Override
    public List<T> findAll(final Query query, final String collection) {
        return findAll(query, false, collection);
    }

    @Override
    public List<T> findAll(final Query query, final boolean includeData, final String collection) {
        List<Attachment> attachments = new ArrayList<>();
        GridFSFindIterable gridFSFiles = getGridFsTemplate(collection).find(query);
        gridFSFiles.forEach(file -> attachments.add(fileToAttachment(file, includeData, collection)));

        return (List<T>) attachments;
    }

    @SneakyThrows
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
                .store(model.getData(), model.getName(), model.getType().name(), metaData);

        return findOneBy("_id", id.toString(), model.getType(), false, collection);
    }

    @Override
    public void save(final List<T> models, final String user, final String collection) {
        models.parallelStream().forEach(it -> save(it, user, collection));
    }

    @Override
    public void delete(final Object id, final String collection) {
        getGridFsTemplate(collection).delete(new Query(Criteria.where("_id").is(id)));
    }

    @SneakyThrows
    private Attachment fileToAttachment(final GridFSFile fsFile, final boolean includeData, final String collection) {
        if (fsFile == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setId(((BsonObjectId) fsFile.getId()).getValue().toString());
        attachment.setName(fsFile.getFilename());
        final Document meta = fsFile.getMetadata();
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
            attachment.setType(AttachmentType.valueOf(meta.getString(Attachment.Fields.type)));
            attachment.setRelatedItemId(meta.getString(Attachment.Fields.relatedItemId));

            meta.forEach((key, value) -> attachment.getMetadata().put(key, value));
        }
        if (includeData) {
            final GridFsResource resource = getGridFsTemplate(collection).getResource(fsFile);
            attachment.setData(resource.getInputStream());
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
