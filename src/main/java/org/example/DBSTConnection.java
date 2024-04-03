package org.example;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Encapsulates connection
 */
public class DBSTConnection {
    BasicDataSource dsc;

    // For each thread create separate BasicDataSource (because we will connect via this object)
    // But keep BasicDataSource instance inside a single thread for sequential queries.
    String dbUrl;
    String driverName;
    String username;
    String password;

    public Connection getConnection() throws SQLException {
        if (dsc == null) {
            dsc = initPool();
        }
        Connection conn = dsc.getConnection();

        return conn;
    }

    private BasicDataSource initPool() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(dbUrl);

        if (username.length() > 0){
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    public String getConnectionInfo() {
        StringBuilder builder = new StringBuilder(100);
        builder
                .append("driver:").append(dsc.getDriverClassName())
                .append(", url:").append(dsc.getUrl())
                .append(", user:").append(dsc.getUsername());
        return builder.toString();
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
