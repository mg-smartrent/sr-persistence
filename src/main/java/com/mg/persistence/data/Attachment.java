package com.mg.persistence.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.io.InputStream;
import java.util.HashMap;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class Attachment extends TrackedItem {
    private Type type;
    private String name;
    private String relatedItemId;
    private InputStream dataStream;
    private HashMap<String, Object> metadata = new HashMap<>();


    public enum Type {
        VIDEO,
        IMAGE,
        PDF,
        DOC,
        HTML;
    }
}
