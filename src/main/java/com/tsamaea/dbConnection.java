import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class dbConnection {
    private static HikariDataSource dataSource;
    private static Properties properties;
    
    static {
        try {
            // Load properties from database.properties file
            properties = loadProperties();
            
            // Initialize connection pool
            initializeConnectionPool();
            
            // Test connection on startup
            testConnection();
            
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Load database properties from the properties file
     */
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        
        try (InputStream input = dbConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("database.properties file not found in classpath");
            }
            props.load(input);
        }
        
        return props;
    }
    
    /**
     * Initialize HikariCP connection pool
     */
    private static void initializeConnectionPool() {
        HikariConfig config = new HikariConfig();
        
        // Required connection parameters
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setDriverClassName(properties.getProperty("db.driver"));
        
        // Connection pool settings
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maximumPoolSize")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minimumIdle")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idleTimeout")));
        config.setMaxLifetime(Long.parseLong(properties.getProperty("db.pool.maxLifetime")));
        config.setLeakDetectionThreshold(Long.parseLong(properties.getProperty("db.pool.leakDetectionThreshold")));
        
        // PostgreSQL optimizations
        config.addDataSourceProperty("preparedStatementCacheQueries", 
            properties.getProperty("db.connection.preparedStatementCacheQueries"));
        config.addDataSourceProperty("preparedStatementCacheSizeMiB", 
            properties.getProperty("db.connection.preparedStatementCacheSizeMiB"));
        config.addDataSourceProperty("reWriteBatchedInserts", 
            properties.getProperty("db.connection.reWriteBatchedInserts"));
        config.addDataSourceProperty("tcpKeepAlive", 
            properties.getProperty("db.connection.tcpKeepAlive"));
        
        // SSL settings (required for Aiven)
        config.addDataSourceProperty("ssl", properties.getProperty("db.connection.ssl"));
        config.addDataSourceProperty("sslmode", properties.getProperty("db.connection.sslmode"));
        
        // Timeout settings
        config.addDataSourceProperty("socketTimeout", 
            properties.getProperty("db.connection.socketTimeout"));
        config.addDataSourceProperty("connectTimeout", 
            properties.getProperty("db.connection.connectTimeout"));
        config.addDataSourceProperty("loginTimeout", 
            properties.getProperty("db.connection.loginTimeout"));
        
        // Application name for monitoring
        config.addDataSourceProperty("ApplicationName", 
            properties.getProperty("db.connection.applicationName"));
        
        dataSource = new HikariDataSource(config);
    }
    
    /**
     * Get a connection from the connection pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Test the database connection
     */
    private static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && conn.isValid(5)) {
                System.out.println("✅ Database connection established successfully!");
                System.out.println("Connected to: " + properties.getProperty("db.url"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            throw new RuntimeException("Connection test failed", e);
        }
    }
    
    /**
     * Close the connection pool (call this on application shutdown)
     */
    public static void closeConnectionPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }
    
    /**
     * Get database properties for reference
     */
    public static Properties getProperties() {
        return new Properties(properties); // Return a copy for safety
    }
    
    /**
     * Utility method to create a direct connection (without pool)
     * Useful for initialization scripts or one-time operations
     */
    public static Connection getDirectConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("db.url"),
            properties.getProperty("db.username"),
            properties.getProperty("db.password")
        );
    }
    
    /**
     * Check if connection pool is running
     */
    public static boolean isPoolRunning() {
        return dataSource != null && !dataSource.isClosed();
    }
}