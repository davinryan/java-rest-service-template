package com.davinryan.service.test.util.mock;

import org.springframework.jdbc.support.xml.*;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MockOracleXmlHandler implements SqlXmlHandler {
    @Override
    public String getXmlAsString(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public String getXmlAsString(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getXmlAsBinaryStream(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public InputStream getXmlAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getXmlAsCharacterStream(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public Reader getXmlAsCharacterStream(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Source getXmlAsSource(ResultSet rs, String columnName, Class sourceClass) throws SQLException {
        return null;
    }

    @Override
    public Source getXmlAsSource(ResultSet rs, int columnIndex, Class sourceClass) throws SQLException {
        return null;
    }

    @Override
    public SqlXmlValue newSqlXmlValue(String value) {
        return null;
    }

    @Override
    public SqlXmlValue newSqlXmlValue(XmlBinaryStreamProvider provider) {
        return null;
    }

    @Override
    public SqlXmlValue newSqlXmlValue(XmlCharacterStreamProvider provider) {
        return null;
    }

    @Override
    public SqlXmlValue newSqlXmlValue(Class resultClass, XmlResultProvider provider) {
        return null;
    }

    @Override
    public SqlXmlValue newSqlXmlValue(Document doc) {
        return null;
    }
}
