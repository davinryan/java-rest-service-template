package com.davinryan.service.dao;

import com.davinryan.service.domain.response.ValidateVendorIdPayload;

/**
 * Data Access Object interface for Customer Account information.
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
     * @param vendorID the vendor ID to be checked
     * @return {@link ValidateVendorIdPayload} indicating results of validation
     */
    ValidateVendorIdPayload validateVendorId(String vendorID) throws DatabaseException;
}