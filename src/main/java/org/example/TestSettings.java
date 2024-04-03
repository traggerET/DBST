package org.example;

/**
 * CLass representing config fields. It will extend over time. Probable some fields will also become a class-object.
 */
public class TestSettings {
    public String testName;
    public String description;
    public String username;
    public String password;
    public String dbUrl;
    public String dbDriverName;
    public QueryInfo queryCfg;
    /**
     * Defines if a query is Update or Select statement
     */
    public String queryType;
    /**
     * Defines how many parallel connections will be created
     */
    public int connNum;

    /**
     * Defines how long will it take to run all connections.
     * Connections are run uniformly.
     */
    public float startUpTime;

    /**
     * Defines how many times one connection will run the query
     */
    public int repeat;
    public String preQueryCfg;
    public String postQueryCfg;
    public int queryTimeout;
}