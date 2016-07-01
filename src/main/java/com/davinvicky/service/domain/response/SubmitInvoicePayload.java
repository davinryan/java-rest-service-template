package com.davinvicky.service.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

@ApiModel(description = "Response after submitting a provider invoice. Allows provider to track their submission.")
public class SubmitInvoicePayload implements Serializable {

    @ApiModelProperty(value = "Submission datetime for this invoice. Format = yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private final DateTime submitted;

    @ApiModelProperty(value = "Unique invoice number for this submission. Synonymous with ACC Invoice Number")
    private final String reference;

    public SubmitInvoicePayload(String reference) {
        this.submitted= new DateTime();
        this.reference = reference;
    }

    public DateTime getSubmitted() {
        return submitted;
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "SubmitInvoicePayload{" +
                "submitted=" + submitted +
                ", reference='" + reference + '\'' +
                '}';
    }
}
