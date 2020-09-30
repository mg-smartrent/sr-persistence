package com.mg.persistence.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class Change extends TrackedItem {
    @NotNull
    private String objectId;
    @NotNull
    private String objectType;
    @NotNull
    private String groupId;
    @NotNull
    private String action;
    @NotNull
    private String fieldName;
    private Object oldValue;
    private Object newValue;
}
