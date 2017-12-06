package com.davinryan.service.dao;

import com.davinryan.service.domain.response.*;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;

import static com.davinryan.common.restservice.jee.JeeUtils.getOracleConnection;

/**
 * Main JDBC dao for provider invoicing.
 */

@Component
public class ExampleJDBCDAO implements JDBCDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleJDBCDAO.class.getName());
    private static final String SQL_VENDOR_INVOICE_STATE = "SUBMIT_ANY_VENDOR_INVOICE";
    private static final String DATABASE_PACKAGE = "owner.DB_PKG";
    private static final String NAMED_QUERY_PREFIX = "{? = call ";
    private static final String NAMED_QUERY_VALIDATE_VENDOR_ID = NAMED_QUERY_PREFIX + DATABASE_PACKAGE + ".f_is_org_authorised_for_vendor(?,?)}";
    private static final String SQL_HEALTH_CHECK = "select * from DUAL;";

    @Autowired
    private DataSource dataSource;

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
            connection = getOracleConnection(dataSource);
            callableStatement = connection.prepareCall(SQL_HEALTH_CHECK);
        } catch (SQLException e) {
            LOGGER.error("Failed to check that the database was alive.", e);
            throw new DatabaseException("Attempting health check against database but failed", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /** {@inheritDoc}. */
    @Cacheable(value = "validVendorIdCache", key = "#vendorId.concat(#orgId).toUpperCase()")
    @Override
    public ValidateVendorIdPayload validateVendorId(String vendorId) throws DatabaseException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String orgName;

        try {
            connection = getOracleConnection(dataSource);
            //get org name from orgid
            LOGGER.info("Validating id without cache: Vendor Identifier=\"" + vendorId + "\""); //NOSONAR
            callableStatement = connection.prepareCall(NAMED_QUERY_VALIDATE_VENDOR_ID);
            callableStatement = createCallableStatement(callableStatement, Types.NUMERIC, vendorId.toUpperCase(), SQL_VENDOR_INVOICE_STATE);
            callableStatement.execute();
            Integer result = callableStatement.getInt(1);
            return new ValidateVendorIdPayload(BooleanUtils.toBoolean(result), true);
        } catch (SQLException e) {
            throw new DatabaseException("Checking vendor=\"" + vendorId + "\"", e);
        } finally {
            handleClosingConnection(connection, callableStatement);
        }
    }

    /**
     * Creates a Callable statement, All parameters must be strings
     *
     * @param callableStatement callable statement that the parameters need to be added to
     * @param returnType        Type that is returned
     * @param parameters        any number of strings
     * @return the created callable statement
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
