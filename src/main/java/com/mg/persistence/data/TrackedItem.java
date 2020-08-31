package com.mg.persistence.data;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@FieldNameConstants
public abstract class TrackedItem {
    @NotNull
    private String id;
    @NotNull
    private Instant createdOn;
    @NotNull
    private String createdBy;
    @NotNull
    private Instant modifiedOn;
    @NotNull
    private String modifiedBy;
    @NotNull
    private Instant deletedOn;
    @NotNull
    private String deletedBy;
}
