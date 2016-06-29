package nz.co.acc.myacc.services.invoicing.dao;

/**
 * General exception for all database errors.
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message, Exception throwable) {
        super(message, throwable);
    }
}
