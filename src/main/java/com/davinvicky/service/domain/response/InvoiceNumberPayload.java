package com.davinvicky.service.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.LocalDate;

import java.io.Serializable;

@ApiModel(description = "Response to retrieving next ACC invoice number")
public class InvoiceNumberPayload implements Serializable {

    @ApiModelProperty(value = "Unique invoice number for this submission")
    private String invoiceNumber;
    private LocalDate currentDate;

    public InvoiceNumberPayload(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    @Override
    public String toString() {
        return "InvoiceNumberPayload{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", currentDate=" + currentDate +
                '}';
    }
}
