package com.mg.persistence.data;

import com.google.common.io.ByteStreams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;

import java.io.InputStream;
import java.util.HashMap;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class Attachment extends TrackedItem {
    private AttachmentType type;
    private String name;
    private String relatedItemId;
    private InputStream data;
    private HashMap<String, Object> metadata = new HashMap<>();


    public void addMetadata(final String key, final Object value) {
        this.metadata.put(key, value);
    }

    @SneakyThrows
    public byte[] getDataAsByteArray() {
        return data != null ? ByteStreams.toByteArray(data) : null;
    }

    public enum AttachmentType {
        VIDEO,
        IMAGE,
        PDF,
        DOC,
        HTML;
    }
}
