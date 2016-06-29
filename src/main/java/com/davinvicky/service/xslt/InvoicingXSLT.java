package nz.co.acc.myacc.services.invoicing.xslt;

import nz.co.acc.myacc.services.invoicing.domain.request.CreateInvoiceFormRequest;

/**
 * XSLT converter interface.
 */
public interface InvoicingXSLT {

    /**
     * Utility to convert {@link CreateInvoiceFormRequest} to MFP eSchedule {@link String}.
     *
     * @param invoice {@link CreateInvoiceFormRequest } object to convert
     * @return an MFP xml eSchedule {@link String}.
     */
    String generateMFPSchedule(CreateInvoiceFormRequest invoice);
}
