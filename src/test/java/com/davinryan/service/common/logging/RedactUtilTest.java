package com.davinryan.service.common.logging;

import org.junit.Before;
import org.junit.Test;

import static com.davinryan.common.restservice.logging.RedactUtil.redactObject;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class RedactUtilTest {

    private RedactTestObj objectToRedact;

    @Before
    public void before() {
        objectToRedact = new RedactTestObj();
        objectToRedact.setNestedField(new RedactTestObj());
    }

    @Test
    public void testRedactObject() {
        // Test
        Object object = redactObject(objectToRedact);

        // Verify
        assertThat(object.toString(), containsString("RedactTestObj{redactedField='REDACTED', unsupportedRedactedField=0, normalField='normalField'"));
    }

    @Test
    public void testRedactNestedObject() {
        // Test
        Object object = redactObject(objectToRedact);

        // Verify
        assertThat(object.toString(), containsString("nestedField=RedactTestObj{redactedField='REDACTED', unsupportedRedactedField=0, normalField='normalField', nestedField=null}}"));
    }
}
