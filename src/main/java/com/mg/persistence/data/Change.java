package com.mg.persistence.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class Change extends TrackedItem {
    private String objectType;
    private String action;
    private String fieldName;
    private Object oldValue;
    private Object newValue;
    private String groupId;
}
