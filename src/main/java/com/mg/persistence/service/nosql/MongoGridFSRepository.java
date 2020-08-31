package com.mg.persistence.service.nosql;


import com.mg.persistence.data.Attachment;
import com.mg.persistence.data.TrackedItem;
import com.mg.persistence.service.AttachmentRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier(MongoGridFSRepository.QUALIFIER)
public class MongoGridFSRepository<T extends Attachment> implements AttachmentRepository<T> {

    public static final String QUALIFIER = "mongo-grid-fs";
    private final GridFsTemplate gridFsTemplate;

    public MongoGridFSRepository(final MongoTemplate mongoTemplate,
                                 final String collection) {
        this.gridFsTemplate = new GridFsTemplate(
                mongoTemplate.getMongoDbFactory(),
                mongoTemplate.getConverter(),
                collection);
    }


    @Override
    public T findOneBy(final String fieldName, final Object value) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where(fieldName).is(value)));
        return (T) fileToAttachment(file);
    }

    @Override
    public List<T> findAllBy(final String fieldName, final Object value) {
        return findAll(new Query(Criteria.where(fieldName).is(value)));
    }

    @Override
    public List<T> findAll(final Query query) {
        final List<Attachment> attachments = new ArrayList<>();
        final GridFSFindIterable gridFSFiles = gridFsTemplate.find(query);
        gridFSFiles.forEach(file -> attachments.add(fileToAttachment(file)));

        return (List<T>) attachments;
    }

    @Override
    public T save(final T model) {

        DBObject metaData = new BasicDBObject();
        if (model.getCreatedOn() == null) {
            metaData.put(TrackedItem.Fields.createdOn, model.getCreatedOn());
            metaData.put(TrackedItem.Fields.createdBy, model.getCreatedBy());
        }
        metaData.put(TrackedItem.Fields.modifiedOn, model.getModifiedOn());
        metaData.put(TrackedItem.Fields.modifiedBy, model.getModifiedBy());
        metaData.put(Attachment.Fields.type, model.getType());
        metaData.put(Attachment.Fields.name, model.getName());
        model.getMetadata().forEach(metaData::put);

        ObjectId id = gridFsTemplate.store(model.getDataStream(), model.getName(), model.getType().name(), metaData);

        return findOneBy(TrackedItem.Fields.id, id);
    }

    @Override
    public void save(final List<T> models) {
        models.parallelStream().forEach(this::save);
    }

    @Override
    public void delete(final Object id) {
        gridFsTemplate.delete(new Query(Criteria.where(TrackedItem.Fields.id).is(id)));
    }


    private Attachment fileToAttachment(final GridFSFile file) {
        Attachment attachment = new Attachment();
        attachment.setName(file.getFilename());
        attachment.setDataStream(null); //todo:
        if (file.getMetadata() != null) {
            attachment.setType(Attachment.Type.valueOf(file.getMetadata().getString(Attachment.Fields.type)));
        }
        return attachment;
    }
}
