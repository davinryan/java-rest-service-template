package com.davinvicky.service.xslt;

import com.davinvicky.service.domain.request.CreateInvoiceFormRequest;

/**
 * XSLT converter interface.
 */
public interface XSLTTransformer {

    /**
     * Utility to convert {@link CreateInvoiceFormRequest} to MFP eSchedule {@link String}.
     *
     * @param invoice {@link CreateInvoiceFormRequest } object to convert
     * @return an MFP xml eSchedule {@link String}.
     */
    String generateMFPSchedule(CreateInvoiceFormRequest invoice);
}
