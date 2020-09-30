package com.mg.persistence.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;

@Data
@NoArgsConstructor
@FieldNameConstants
public abstract class TrackedItem implements Cloneable {
    private String id;
    @NotNull
    @PastOrPresent
    private Instant createdOn;
    @NotNull
    private String createdBy;
    @NotNull
    @PastOrPresent
    private Instant modifiedOn;
    @NotNull
    private String modifiedBy;
    @PastOrPresent
    private Instant deletedOn;
    private String deletedBy;
}
