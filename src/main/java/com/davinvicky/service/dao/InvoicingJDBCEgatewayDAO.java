package nz.co.acc.myacc.services.invoicing.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import nz.co.acc.myacc.common.jee.JeeUtils;
import nz.co.acc.myacc.services.invoicing.domain.request.CreateInvoiceFormRequest;
import nz.co.acc.myacc.services.invoicing.domain.response.*;
import nz.co.acc.myacc.services.invoicing.util.XmlObjectMapper;
import oracle.xdb.XMLType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Main JDBC dao for provider invoicing.
 */

@Component
public class InvoicingJDBCEgatewayDAO implements InvoicingJDBCDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoicingJDBCEgatewayDAO.class.getName());
    private static final String SQL_VENDOR_INVOICE_STATE = "SUBMIT_ANY_VENDOR_INVOICE";
    private static final String ACC40_47E_PACKAGE = "egateway_owner.ACC40_47E_REF_EGATEWAY_PKG";
    private static final String ACC_FORMS_PROVIDER_PACKAGE = "FORMS_OWNER.PROVIDER_PKG";
    private static final String EGATEWAY_PACKAGE = "egateway_owner.EGATEWAY_PKG";
    private static final String NAMED_QUERY_PREFIX = "{? = call ";
    private static final String ACC47_PACKAGE = "egateway_owner.acc47e_egateway_pkg";
    private static final String NAMED_QUERY_VALIDATE_VENDOR_ID = NAMED_QUERY_PREFIX + ACC40_47E_PACKAGE + ".f_is_org_authorised_for_vendor(?,?,?)}";
    private static final String SQL_PROCEDURE_FORM = NAMED_QUERY_PREFIX + EGATEWAY_PACKAGE + ".f_insert_form_submission(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static final String NAMED_QUERY_VALIDATE_SERVICE_CODE = NAMED_QUERY_PREFIX + ACC40_47E_PACKAGE + ".f_service_contract_items_valid(?,?,?)";
    private static final String INVOICE_PROCEDURE = "{call " + ACC_FORMS_PROVIDER_PACKAGE  + ".GET_NEXT_ID(?,?,?,?)}";
    private static final String NAMED_QUERY_VALIDATE_CLAIM_NUMBER = NAMED_QUERY_PREFIX +ACC47_PACKAGE+".f_is_claim_number_valid(?)}";
    private static final String SPLUNK_VALID ="\" is valid";
    private static final String SQL_HEALTH_CHECK = "select * from DUAL;";

    @Autowired
    private DataSource egatewayDatasource;

    @Autowired
    private XmlObjectMapper xmlMapper;

    private JeeUtils jeeUtils;

    public InvoicingJDBCEgatewayDAO() {
        this.jeeUtils = new JeeUtils();
    }

    /** {@inheritDoc}. */
    @Override
    @Transactional
    public void healthCheck() throws DatabaseException {
        LOGGER.info("#####################################");
        LOGGER.info("Performing Database HealthCheck");
        LOGGER.info("#####################################");
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            callableStatement = connection.prepareCall(SQL_HEALTH_CHECK);
        } catch (SQLException e) {
            LOGGER.error("Failed to check that the database was alive.", e);
            throw new DatabaseException("Attempting health check against database but failed", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Override
    @Transactional
    public void saveInvoiceRequest(CreateInvoiceFormRequest invoice) throws DatabaseException {
        LOGGER.info("#####################################");
        LOGGER.info("Saving Schedule to Database");
        LOGGER.info("#####################################");
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            String orgName = getOrganisationName(connection, invoice.getOrganisationId());
            callableStatement = connection.prepareCall(SQL_PROCEDURE_FORM);
            saveInvoiceRequest(invoice, callableStatement, orgName, connection);
        } catch (SQLException e) {
            LOGGER.error("Failed to save CreateInvoiceFormRequest to database.", e);
            throw new DatabaseException("Attempting to save to the database but failed", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    private void saveInvoiceRequest(CreateInvoiceFormRequest invoice, CallableStatement callableStatement, String orgName, Connection connection) throws SQLException {

            //convert json into xml string
            String invoiceXML = convertCreateInvoiceFormRequestToXml(invoice);

            callableStatement.registerOutParameter(1, Types.NUMERIC);
            XMLType xml = XMLType.createXML(connection, invoiceXML);
            callableStatement.setObject(2, xml);

            // add correlation id as external track trace
            callableStatement.setString(3, invoice.getCorrelationId());
            callableStatement.setObject(4, xml);
            //form paper version
            callableStatement.setString(5, "1.0");
            // egateway id
            callableStatement.setString(6, invoice.getCorrelationId());
            //status code, if it has got here it is an R
            callableStatement.setString(7, "R");
            String emailAddress = invoice.getEmailAddress();
            //SUBMITTING_SYSTEMS.EMAIL
            callableStatement.setString(8, emailAddress);
            //SUBMITTING_SYSTEMS.USERS setting to null


            callableStatement.setString(9, orgName);
            callableStatement.setString(10, emailAddress);
            //SUBMITTING_SYSTEMS.USERS setting to null
            callableStatement.setString(11, orgName);

            //software name for EGATEWAY_OWNER.SUBMITTING_SOFTWARE
            callableStatement.setString(12, invoice.getType());
            //software name for EGATEWAY_OWNER.SUBMITTING_VERSION
            callableStatement.setString(13, "2016.05.17");
            //form name for EGATEWAY_OWNER.FORM_SCHEMAS
            callableStatement.setString(14, invoice.getType());
            //form schema version for EGATEWAY_OWNER.FORM_SCHEMAS
            callableStatement.setString(15, "20160517");
            //nummy schema  for EGATEWAY_OWNER.FORM_SCHEMAS we use JSON now
            callableStatement.setString(16, "N/A");
            //dummy schema for EGATEWAY_OWNER.FORM_SCHEMAS we use JSON now
            callableStatement.setString(17, "N/A");

            callableStatement.execute();

        Integer formSubmissionId = callableStatement.getInt(1);
            LOGGER.debug("Form submission ID", formSubmissionId);

    }

    private String convertCreateInvoiceFormRequestToXml(CreateInvoiceFormRequest invoice) {
        String invoiceXML = "";
        try {
            invoiceXML = xmlMapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            LOGGER.error("Something when wrong while trying to convert invoice from json to xml", e);
        }
        return invoiceXML;
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validVendorIdCache", key = "#vendorId.concat(#orgId).toUpperCase()")
    @Override
    public ValidateVendorIdPayload validateVendorId(String vendorId, String orgId) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String orgName;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            //check if vendor exists
            if (getVendorName(connection, vendorId) == null) {
                return new ValidateVendorIdPayload(false, false);
            }
            //get org name from orgid
            orgName = getOrganisationName(connection, orgId);
            LOGGER.info("Validating VendorId without cache: OrganisationID=\"" + orgId + "\", Organisation Name=\""
                    + orgName + "\",Vendor Identifier=\"" + vendorId + "\""); //NOSONAR
            callableStatement = connection.prepareCall(NAMED_QUERY_VALIDATE_VENDOR_ID);
            callableStatement = createCallableStatement(callableStatement, Types.NUMERIC, orgName, vendorId.toUpperCase(), SQL_VENDOR_INVOICE_STATE);
            callableStatement.execute();
            Integer result = callableStatement.getInt(1);
            return new ValidateVendorIdPayload(BooleanUtils.toBoolean(result), true);
        } catch (SQLException e) {
            throw new DatabaseException("Checking if organisation=\"" + orgId + "\" is authorised to submit invoices for vendor=\"" + vendorId + "\"", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validVendorIdCache", key = "#vendorId.concat(#orgName).toUpperCase()")
    @Override
    public ValidateVendorIdPayload validateVendorIdWithOrgName(String vendorId, String orgName) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            //check if vendor exists
            if (getVendorName(connection, vendorId) == null) {
                return new ValidateVendorIdPayload(false, false);
            }
            //get org name from orgid
            LOGGER.info("Validating VendorId without cache: Organisation Name=\""
                    + orgName + "\",Vendor Identifier=\"" + vendorId + "\""); //NOSONAR
            callableStatement = connection.prepareCall(NAMED_QUERY_VALIDATE_VENDOR_ID);
            callableStatement = createCallableStatement(callableStatement, Types.NUMERIC, orgName, vendorId.toUpperCase(), SQL_VENDOR_INVOICE_STATE);
            callableStatement.execute();
            Integer result = callableStatement.getInt(1);
            return new ValidateVendorIdPayload(BooleanUtils.toBoolean(result), true);
        } catch (SQLException e) {
            throw new DatabaseException("Checking if organisation=\"" + orgName + "\" is authorised to submit invoices for vendor=\"" + vendorId + "\"", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Override
    @Cacheable(value = "vendorNameCache", key = "#vendorId.toUpperCase()")
    public String getVendorName(String vendorId) throws DatabaseException {
        Connection connection = null;
        LOGGER.info("Getting vendor name without cache: ,Vendor Identifier=\"" + vendorId + "\""); //NOSONAR

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            return getVendorName(connection, vendorId);
        } catch (SQLException e) {
            throw new DatabaseException("Retrieving vendorname where vendorId=\"" + vendorId+ "\"", e);
        } finally {
            handleClosingConnection(connection, null);
        }
    }

    @Cacheable(value = "vendorNameCache", key = "#vendorId.toUpperCase()")
    private String getVendorName(Connection connection, String vendorId) throws SQLException {
        String vendorName = null;
        String orgNameSQL = "SELECT VENDOR_NAME FROM egateway_owner.VENDOR WHERE VENDOR_ACC_IDENTIFIER=? AND VENDOR_STATUS_TYPE_CODE != 2";
        PreparedStatement preparedStatement = connection.prepareStatement(orgNameSQL); // NOSONAR
        preparedStatement.setString(1, vendorId.toUpperCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            vendorName = resultSet.getString("VENDOR_NAME");
        }
        return vendorName;
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validProviderIdCache", key = "#providerId.toUpperCase()")
    @Override
    public ValidateProviderIdPayload validateProviderId(String providerId) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            LOGGER.info("Validating ProviderId without cache: Provider Id =\"" + providerId + "\"");//NOSONAR
            //check if contract ID exists
            int records = -1;
            String sql = "SELECT COUNT(*) FROM egateway_mfprep_user.PROVIDER WHERE PROVIDER_ACC_IDENTIFIER=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql); // NOSONAR
            preparedStatement.setString(1, providerId.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                records = resultSet.getInt(1);
            }
            return new ValidateProviderIdPayload(records > 0);
        } catch (SQLException e) {
            throw new DatabaseException("Checking Provider Id=\" " + providerId + "\" existence and active state", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validContractNoCache", key = "#contractNo.toUpperCase()")
    @Override
    public ValidateContractNumberPayload validateContractNumber(String contractNo) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            LOGGER.info("Validating ContractNo without cache: Contract No=\"" + contractNo + "\"");//NOSONAR
            //check if contract ID exists
            int records = -1;
            String sql = "SELECT COUNT(*) FROM egateway_mfprep_user.CONTRACT WHERE CONTRACT_ACC_IDENTIFIER=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql); // NOSONAR
            preparedStatement.setString(1, contractNo.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                records = resultSet.getInt(1);
            }
            return new ValidateContractNumberPayload(records > 0);
        } catch (SQLException e) {
            throw new DatabaseException("Checking Contract No=\"" + contractNo + SPLUNK_VALID, e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validServiceCodeCache", key = "#contractNo != null ? " +
            "#serviceCode.concat(#serviceDate.toLocalDate().toString()).concat(#contractNo).toUpperCase() : " +
            "#serviceCode.concat(#serviceDate.toLocalDate().toString()).toUpperCase()")
    @Override
    public ValidateServiceCodePayload validateServiceCode(String serviceCode, DateTime serviceDate, String contractNo) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;

        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            LOGGER.info("Validating Service Code without cache: Service Code=\"" + serviceCode + "\",serviceDate=\""
                    + serviceCode + "\",contractNo=\"" + contractNo + "\"");//NOSONAR
            callableStatement = connection.prepareCall(NAMED_QUERY_VALIDATE_SERVICE_CODE);
            callableStatement.registerOutParameter(1, Types.NUMERIC);
            callableStatement.setString(2, serviceCode.toUpperCase());

            // Yes we can handle not sending a contract number. It is optional.
            if (StringUtils.isNotBlank(contractNo)) {
                callableStatement.setString(3, contractNo.toUpperCase());
            } else {
                callableStatement.setString(3, null);
            }
            callableStatement.setDate(4, new Date(serviceDate.getMillis()));
            callableStatement.execute();
            Integer result = callableStatement.getInt(1);
            return new ValidateServiceCodePayload(BooleanUtils.toBoolean(result));
        } catch (SQLException e) {
            throw new DatabaseException("Checking Service Code=\"" + serviceCode + SPLUNK_VALID, e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validClaimNumberCache", key = "#claimNumber.toUpperCase()")
    @Override
    public ValidateClaimNumberPayload validateClaimNumber(String claimNumber) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            LOGGER.info("Validating claim number without cache: Claim number=\"" + claimNumber + "\"");//NOSONAR
            callableStatement = connection.prepareCall(NAMED_QUERY_VALIDATE_CLAIM_NUMBER);
            callableStatement.registerOutParameter(1, Types.NUMERIC);
            callableStatement.setString(2, claimNumber.toUpperCase()); // p_medical_fees_number
            callableStatement.execute();
            Integer result = callableStatement.getInt(1);
            return new ValidateClaimNumberPayload(BooleanUtils.toBoolean(result));
        } catch (SQLException e) {
            throw new DatabaseException("Error checking claim number=\" " + claimNumber + SPLUNK_VALID, e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Override
    public String getInvoiceNumber() throws DatabaseException {
        String invoiceNumber ="";
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = jeeUtils.getOracleConnection(egatewayDatasource);
            callableStatement = connection.prepareCall(INVOICE_PROCEDURE);
            callableStatement.setString(1,"EINVOICE");
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.execute();
             invoiceNumber = callableStatement.getString(2);
            String resultCode =  callableStatement.getString(3);
            String resultData = callableStatement.getString(4);
            LOGGER.info("Retrieved new Invoice number=\"" + invoiceNumber + "\", Code=\"" + resultCode + "\" Result data=\"" + resultData + "\"");
        } catch (SQLException e) {
            throw new DatabaseException("Could not get invoice number with result code ", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }

        return invoiceNumber;
    }

    @Cacheable(value = "organisationNameCache", key = "#orgId.toLowerCase()")
    private String getOrganisationName(Connection connection, String orgId) throws SQLException {
        String orgName = "";
        String orgNameSQL = "SELECT ORGANISATION_NAME FROM egateway_owner.ORGANISATIONS WHERE ORGANISATION_ID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(orgNameSQL); // NOSONAR
        preparedStatement.setString(1, orgId.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            orgName = resultSet.getString("ORGANISATION_NAME");
        }
        return orgName;
    }

    /**
     * Creates a Callable statement, All parameters must be strings
     *
     * @param callableStatement callable statment that the parameters need to be added to
     * @param returnType        Type that is returned
     * @param parameters        any number of strings
     * @return the created callable statment
     * @throws SQLException
     */
    private CallableStatement createCallableStatement(CallableStatement callableStatement, int returnType, String... parameters) throws SQLException {

        callableStatement.registerOutParameter(1, returnType);
        for (int i = 0; i < parameters.length; i++) {
            callableStatement.setString(i + 2, parameters[i]);
        }
        return callableStatement;
    }

    private void handleClosingConnection(Connection conn, CallableStatement cs) {
        try {
            if (cs != null)
                cs.close();
        } catch (Exception e) {
            LOGGER.error("Error closing CallableStatement", e);
        }

        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            LOGGER.error("Error closing connection", e);
        }
    }
}
