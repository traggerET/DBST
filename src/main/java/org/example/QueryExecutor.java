package org.example;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * Class responsible for executing query and collecting infos
 */
public class QueryExecutor {
    static final String SELECT   = "SELECT"; // $NON-NLS-1$
    static final String UPDATE   = "UPDATE"; // $NON-NLS-1$
    private final String queryType;
    private final String query;

    private int queryTimeout;

    private DBSTConnection dsce;

    protected static final String ENCODING = StandardCharsets.UTF_8.name();

    public QueryExecutor(DBSTConnection dsce, String query, String queryType, int queryTimeout) {
        this.dsce = dsce;
        this.query = query;
        this.queryType = queryType;
        this.queryTimeout = queryTimeout;
    }

    public TestResult exec() {
        TestResult res = new TestResult();
        res.setDataType(TestResult.TEXT);
        res.setContentType("text/plain"); // $NON-NLS-1$
        res.setDataEncoding(ENCODING);

        res.setSuccessful(true);
        res.setMsgOK();
        res.setCodeOK();

        res.markStart();
        Connection conn = null;
        try {
            try {
                conn = dsce.getConnection();
            } finally {
                res.connectionEstablished();
            }
            res.setRespHeaders(dsce.getConnectionInfo());
            res.setRespData(execute(conn, res));
        } catch (SQLException ex) {

        } catch (Exception ex) {

        } finally {
            close(conn);
        }

        res.markTerminated();
        return res;
    }

    private byte[] execute(Connection conn, TestResult res) throws SQLException, UnsupportedEncodingException {
        if (SELECT.equals(queryType)) {
            try (Statement stmt = conn.createStatement()) {
                setTimeout(stmt, queryTimeout);
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(query);
                    res.execTerm();
                    return getString(rs).getBytes(ENCODING);
                } finally {
                    close(rs);
                }
            }
        } else if (UPDATE.equals(queryType)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(query);
                res.execTerm();
                int updateCount = stmt.getUpdateCount();
                String results = updateCount + " updates";
                return results.getBytes(ENCODING);
            }
        }
        return null;
    }

    private String getString(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        StringBuilder sb = new StringBuilder();
        int num = meta.getColumnCount();
        for (int i = 1; i <= num; i++) {
            sb.append(meta.getColumnLabel(i));
            if (i == num) {
                sb.append('\n');
            } else {
                sb.append('\t');
            }
        }
        return sb.toString();
    }

    private static void setTimeout(Statement stmt, int timeout) throws SQLException {
        if (timeout >= 0) {
            stmt.setQueryTimeout(timeout);
        }
    }

    public static void close(ResultSet c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
        }
    }

    public static void close(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
        }
    }
}
