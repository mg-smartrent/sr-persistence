package com.mg.persistence.unit.data

import com.mg.persistence.data.Attachment;
import com.mg.persistence.unit.UnitTestSetup
import org.bson.types.ObjectId

import java.time.Instant;

public class AttachmentTest extends UnitTestSetup {

    def 'test: attachment'() {
        setup:
        Instant now = Instant.now()
        String id = ObjectId.get().toString();
        when:
        Attachment attachment = new Attachment()
        attachment.setId(id)
        attachment.setDataStream(null)
        attachment.setName("name")
        attachment.setType(Attachment.Type.IMAGE)
        attachment.setCreatedBy("CreatedBy")
        attachment.setCreatedOn(now)
        attachment.setModifiedBy("ModifiedBy")
        attachment.setModifiedOn(now)
        attachment.setDeletedBy("DeletedBy")
        attachment.setDeletedOn(now)
        HashMap<String, Object> map = new HashMap<>();
        map.put("int", Integer.valueOf(10))
        map.put("string", "string")
        map.put("date", Instant.now())
        attachment.setMetadata(map)

        then:
        attachment.getId() == id
        attachment.getDataStream() == null
        attachment.getName() == "name"
        attachment.getType() == Attachment.Type.IMAGE
        attachment.getCreatedBy() == "CreatedBy"
        attachment.getCreatedOn() == now
        attachment.getModifiedBy() == "ModifiedBy"
        attachment.getModifiedOn() == now
        attachment.getDeletedBy() == "DeletedBy"
        attachment.getDeletedOn() == now
        attachment.getMetadata().size() == 3

    }
}
