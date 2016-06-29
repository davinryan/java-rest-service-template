package nz.co.acc.myacc.services.invoicing.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.validation.constraints.Size;

@ApiModel(description = "Technical header form managing this message in ACC")
public class MessageInfo {

    @ApiModelProperty(value = "Indicates the medium this transaction was performed under", required = true, allowableValues = "E (electronic), M (Manual)")
    @NotBlank
    private String documentGateway = "E";

    @ApiModelProperty(value = "An input document reference from the channel. Probably optional. Format VA20")
    private String documentReference;

    @ApiModelProperty(value = "As the file is retrieved it may be necessary to include the source application software version. Max = 30")
    @Size(max = 30)
    private String documentSourceAppVersion = "Provider Invoice Service V1.0";

    @ApiModelProperty(value = "Date this document was created by the submitting service")
    private LocalDate documentDate = new LocalDate();

    @ApiModelProperty(value = "Time this document was created by the submitting service")
    private LocalTime documentTime = new LocalTime();

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public String getDocumentSourceAppVersion() {
        return documentSourceAppVersion;
    }

    public String getDocumentGateway() {
        return documentGateway;
    }

    public void setDocumentGateway(String documentGateway) {
        this.documentGateway = documentGateway;
    }

    public void setDocumentSourceAppVersion(String documentSourceAppVersion) {
        this.documentSourceAppVersion = documentSourceAppVersion;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }

    public LocalTime getDocumentTime() {
        return documentTime;
    }

    public void setDocumentTime(LocalTime documentTime) {
        this.documentTime = documentTime;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "documentGateway='" + documentGateway + '\'' +
                ", documentReference='" + documentReference + '\'' +
                ", documentSourceAppVersion='" + documentSourceAppVersion + '\'' +
                ", documentDate=" + documentDate +
                ", documentTime=" + documentTime +
                '}';
    }
}
