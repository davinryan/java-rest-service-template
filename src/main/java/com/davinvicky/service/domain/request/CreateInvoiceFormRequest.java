package nz.co.acc.myacc.services.invoicing.domain.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import nz.co.acc.myacc.common.domain.request.Request;
import nz.co.acc.myacc.common.logging.RedactWhenLogging;
import nz.co.acc.myacc.common.validation.Field;
import nz.co.acc.myacc.services.invoicing.validation.SumOfFeesShouldNotExceedValue;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: the validation here might not be great but its from the MFP schema which is what we have to achieve
 */
@ApiModel(description = "Main Request object for vendors/providers claiming for services provided through ACC to be applied to the MFP system." +
        " This form is based on the Vendor who is the originator of the schedule." +
        " This form can contain up to 200 service lines while a web schedule is limited to 10 service lines." +
        " Each schedule line can contain one or more service lines. The web based schedule is restricted to 5 service lines per schedule lines but the PMS schedule line is in theory unlimited.")
@JsonRootName("CreateInvoiceFormRequest")
@SumOfFeesShouldNotExceedValue(
        max = "99999.99",
        fields = {@Field(name = "serviceDetails", subFieldName = "fee")}
)
public class CreateInvoiceFormRequest extends Request {

    @ApiModelProperty(value ="General information for this submission")
    private transient MessageInfo messageInfo = new MessageInfo();

    @ApiModelProperty(value = "Identifier of provider's organisation submitting this form", required = true)
    @NotBlank
    private String organisationId;

    /**
     * Used for populating b2c and b2b data when storing this record in the database.
     */
    @ApiModelProperty(value = "Email address of provider submitting this form", required = true)
    @NotBlank
    @Email
    private String emailAddress;

    @ApiModelProperty(value = "Date this provider submitted this form. Format = dd/MM/yyyy", required = true)
    @NotNull
    private LocalDate invoiceDate;

    @ApiModelProperty(value = "Unique invoice number for this submission. Max = 10", required = true)
    @Size(max = 10)
    @NotBlank
    private String invoiceNumber;

    @ApiModelProperty(value = "Claimant's date of birth. Format = dd/MM/yyyy", required = true)
    @NotNull
    @RedactWhenLogging
    private LocalDate dob;

    @ApiModelProperty(value = "List of all services provided", required = true)
    @Valid
    private transient List<ServiceDetails> serviceDetails = new ArrayList<ServiceDetails>();

    @ApiModelProperty(value = "Vendor's ACC Name. You can leave this blank really. MFP insists on it even though you " +
            "already have to provide the vendor id. So as a courtesy this service will look up the vendor name if you " +
            "don't provide it. Max = 60")
    @Size(max = 60)
    private String vendorName;

    @ApiModelProperty(value = "Vendor's ACC Name. Max = 12", required = true)
    @Size(min = 1, max = 12)
    @Pattern(regexp = "[^a-z]*")
    @NotBlank
    private String vendorId;

    @ApiModelProperty(value = "ACC Contract identifier. Format = [^a-z]*. Max = 8")
    @Size(max = 8)
    @Pattern(regexp = "[^a-z]*")
    private String contractNumber;

    @ApiModelProperty(value = "Claimant's first name. Max = 20", required = true)
    @Size(min = 1, max = 20)
    @NotBlank
    @RedactWhenLogging
    private String firstName;

    @ApiModelProperty(value = "Claimants last name. Max = 25", required = true)
    @Size(min = 1, max = 25)
    @NotBlank
    @RedactWhenLogging
    private String surname;

    @ApiModelProperty(value = "National Health Index number. Max = 7")
    @Size(max = 7)
    @RedactWhenLogging
    private String nhi;

    @ApiModelProperty(value = "ACC Medical Fees number. Format = [A-Z0-9]*. Max = 12", required = true)
    @Size(min = 1, max = 12)
    @NotBlank
    @Pattern(regexp = "[A-Z0-9]*")
    private String claimNumber;

    @ApiModelProperty(value = "Date of Accident. Format = dd/MM/yyyy")
    private LocalDate doa;

    @ApiModelProperty(value = "A free form area for provider comment relating to the schedule. Max = 255")
    @Size(max = 255)
    @RedactWhenLogging
    private String additionalComments;

    @ApiModelProperty(value ="For use internally only. Helps calculate when the application completes its submission")
    private DateTime endOfRunDateTime;

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public List<ServiceDetails> getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(List<ServiceDetails> serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public LocalDate getDoa() {
        return doa;
    }

    public void setDoa(LocalDate doa) {
        this.doa = doa;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public DateTime getEndOfRunDateTime() {
        return endOfRunDateTime;
    }

    public void setEndOfRunDateTime(DateTime endOfRunDateTime) {
        this.endOfRunDateTime = endOfRunDateTime;
    }

    @Override
    public String toString() {
        return "CreateInvoiceFormRequest{" +
                "messageInfo=" + messageInfo +
                ", organisationId='" + organisationId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", dob=" + dob +
                ", serviceDetails=" + serviceDetails +
                ", vendorName='" + vendorName + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", contractNumber='" + contractNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", nhi='" + nhi + '\'' +
                ", claimNumber='" + claimNumber + '\'' +
                ", doa=" + doa +
                ", additionalComments='" + additionalComments + '\'' +
                ", endOfRunDateTime=" + endOfRunDateTime +
                '}';
    }
}
