package com.davinvicky.service.test.util.builders;

import com.davinvicky.service.domain.request.CreateInvoiceFormRequest;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Builder utility for building {@link CreateInvoiceFormRequest} objects.
 */
public class CreateInvoiceFormBuilder {

    private static DateTimeFormatter DATE_FORMAT_STRING = DateTimeFormat.forPattern("dd/MM/yyyy");

    private CreateInvoiceFormRequest form = new CreateInvoiceFormRequest();

    public CreateInvoiceFormBuilder createValid() {
        form.setCorrelationId("12345");
        form.setType("ACC47e");
        form.setInvoiceDate(LocalDate.parse("11/04/2016", DATE_FORMAT_STRING));
        form.setInvoiceNumber("ACC123456");
        form.setDob(LocalDate.parse("08/11/1983", DATE_FORMAT_STRING));
        addValidServiceDetails();
        form.setVendorId("VENDOR_ID");
        form.setContractNumber("09234287");
        form.setFirstName("James");
        form.setSurname("Conner");
        form.setNhi("abc1234");
        form.setClaimNumber("AB12345");
        form.setDoa(LocalDate.parse("22/01/2016", DATE_FORMAT_STRING));
        form.setAdditionalComments("Some additional comments");
        form.setEmailAddress("acc@acc.co.nz");
        return this;
    }

    public CreateInvoiceFormBuilder addValidServiceDetails() {
        form.getServiceDetails().add(new ServiceDetailsBuilder().createValid().build());
        return this;
    }

    public CreateInvoiceFormRequest build() {
        return form;
    }
}
