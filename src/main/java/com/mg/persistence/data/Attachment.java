package com.mg.persistence.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.HashMap;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class Attachment extends TrackedItem {
    private AttachmentType type;
    private String name;
    private String relatedItemId;
    private byte[] data;
    private HashMap<String, Object> metadata = new HashMap<>();


    public void addMetadata(final String key, final Object value) {
        this.metadata.put(key, value);
    }

    public enum AttachmentType {
        VIDEO,
        IMAGE,
        PDF,
        DOC,
        HTML;
    }
}
