package com.davinryan.service.xslt;

/**
 * XSLT converter interface.
 */
public interface XSLTTransformer {

    /**
     * Utility to convert {@link Object} to MFP eSchedule {@link String}.
     *
     * @param invoice {@link Object } object to convert
     * @return xml representation {@link String}.
     */
    String generateXML(Object invoice);
}
