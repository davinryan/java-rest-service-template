package nz.co.acc.myacc.services.common.logging;

import nz.co.acc.myacc.common.logging.RedactWhenLogging;

/**
 * Created by ryanda on 24/05/2016.
 */
public class RedactTestObj {

    @RedactWhenLogging
    private String redactedField = "redactedField";

    @RedactWhenLogging
    private Integer unsupportedRedactedField = 0;

    private String normalField = "normalField";

    private Object nestedField;

    public String getRedactedField() {
        return redactedField;
    }

    public void setRedactedField(String redactedField) {
        this.redactedField = redactedField;
    }

    public String getNormalField() {
        return normalField;
    }

    public void setNormalField(String normalField) {
        this.normalField = normalField;
    }

    public Integer getUnsupportedRedactedField() {
        return unsupportedRedactedField;
    }

    public void setUnsupportedRedactedField(Integer unsupportedRedactedField) {
        this.unsupportedRedactedField = unsupportedRedactedField;
    }

    public Object getNestedField() {
        return nestedField;
    }

    public void setNestedField(Object nestedField) {
        this.nestedField = nestedField;
    }

    @Override
    public String toString() {
        return "RedactTestObj{" +
                "redactedField='" + redactedField + '\'' +
                ", unsupportedRedactedField=" + unsupportedRedactedField +
                ", normalField='" + normalField + '\'' +
                ", nestedField=" + nestedField +
                '}';
    }
}
