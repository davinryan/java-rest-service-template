package com.davinvicky.service.dao;

import com.davinvicky.service.domain.request.CreateInvoiceFormRequest;
import com.davinvicky.service.domain.request.ValidateVendorWithOrgNameRequest;
import com.davinvicky.service.domain.response.*;
import org.joda.time.DateTime;

import java.sql.SQLException;

/**
 * Data Access Object interface for Customer Account information.
 *
 * @author BinnsA, MckeownS
 */
public interface JDBCDAO {

    /**
     * This method will check that the database is still alive.
     */
    void healthCheck() throws DatabaseException;

    /**
     * Takes a vendorID and organisation id, returns true if that
     * organisation is authorised for that vendorID
     *
     * @param vendorID the acc vendor ID to be checked
     * @param orgName  the Organisation name that is associated with the users certificate
     * @return {@link ValidateVendorIdPayload} indicating results of validation
     */
    ValidateVendorIdPayload validateVendorId(String vendorID, String orgName) throws DatabaseException;

    /**
     * Takes a vendorID and organisation name, returns true if that
     * organisation is authorised for that vendorID
     *
     * @deprecated Please use validateVendorId as organisation names may not be unique.
     *
     * @param vendorID the acc vendor ID to be checked
     * @param orgName  the Organisation name that is associated with the users certificate
     * @return {@link ValidateVendorIdPayload} indicating results of validation
     */
    ValidateVendorIdPayload validateVendorIdWithOrgName(String vendorID, String orgName) throws DatabaseException;

    /**
     * This method will send the invoice to MFP after saving it to the egateway database.
     *
     * @param invoice Submitted Invoice
     */
    void saveInvoiceRequest(CreateInvoiceFormRequest invoice) throws DatabaseException;

    /**
     * Takes a provider id and returns a payload indicated whether that provider exists and is active
     * @param providerId the acc provider id to be checed
     * @param correlationId simple generated value for tracking this transaction
     * @return {@link ValidateProviderIdPayload} indicating results of validation
     * @throws DatabaseException
     */


    /**
     * Calls stored procedure to retrieve the next invoice number from the sequence
     *
     * @return the next invoice number supplied by the forms owner sequence
     * @throws DatabaseException
     */
    String getInvoiceNumber() throws DatabaseException;

    /**
     * Returns a vendor name only if it exists and is active. Otherwise null is returned.
     *
     * @param vendorId vendor id to use to get vendor name
     * @return Vendor Name as {@link String}
     * @throws SQLException
     */
    String getVendorName(String vendorId) throws DatabaseException;

    /**
     * Validates whether a provider id exists and is valid.
     *
     * @param providerId provider id
     * @return {@link ValidateProviderIdPayload} indicating results of validation
     * @throws DatabaseException
     */
    ValidateProviderIdPayload validateProviderId(String providerId) throws DatabaseException;

    /**
     * Validate whether a service code and contract number are valid.
     *
     * @param contractNo contract number
     * @return {@link ValidateContractNumberPayload} indicating results of validation
     * @throws DatabaseException
     */
    ValidateContractNumberPayload validateContractNumber(String contractNo) throws DatabaseException;

    /**
     * Validates whether a service code exists and is valid for given service date and contract number.
     *
     * @param serviceCode service code
     * @param serviceDate date the service was performed
     * @param contractNo  contract number
     * @return {@link ValidateServiceCodePayload} indicating results of validation
     * @throws DatabaseException
     */
    ValidateServiceCodePayload validateServiceCode(String serviceCode, DateTime serviceDate, String contractNo) throws DatabaseException;

    /**
     * Validates whether a service code exists and is valid for given service date and contract number.
     *
     * @param claimNumber
     * @return {@link ValidateClaimNumberPayload} indicating results of validation
     * @throws DatabaseException
     */
    ValidateClaimNumberPayload validateClaimNumber(String claimNumber) throws DatabaseException;
}