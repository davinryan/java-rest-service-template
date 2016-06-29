package nz.co.acc.myacc.services.common.logging;

import nz.co.acc.myacc.common.logging.RedactUtil;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by ryanda on 23/05/2016.
 */
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
        Object object = RedactUtil.redactObject(objectToRedact);

        // Verify
        assertThat(object.toString(), containsString("RedactTestObj{redactedField='REDACTED', unsupportedRedactedField=0, normalField='normalField'"));
    }

    @Test
    public void testRedactNestedObject() {
        // Test
        Object object = RedactUtil.redactObject(objectToRedact);

        // Verify
        assertThat(object.toString(), containsString("nestedField=RedactTestObj{redactedField='REDACTED', unsupportedRedactedField=0, normalField='normalField', nestedField=null}}"));
    }
}
