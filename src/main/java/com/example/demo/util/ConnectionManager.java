package com.example.demo.util;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

   // @Value("${pg.connection.username}")
    public static final String USER_NAME = "postgres";
  //  @Value("${pg.connection.password}")
    public static final String PASSWORD = "postgres";
  //  @Value("${pg.connection.url}")
    public static final String CONNECTION_URL = "jdbc:postgresql://localhost:5434/postgres";

        private static HikariDataSource hikariDataSource;
    
    static {
        try {
            hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl(CONNECTION_URL);
            hikariDataSource.setUsername(USER_NAME);
            hikariDataSource.setPassword(PASSWORD);

            hikariDataSource.setMinimumIdle(2);
            hikariDataSource.setMaximumPoolSize(5);
            hikariDataSource.setAutoCommit(true);
            hikariDataSource.setLoginTimeout(5);
        } catch (SQLException exception) {
            logger.error("Can't connection to postgres");
        }
    }

    public static DataSource getDataSource(){
        return hikariDataSource;
    }
}
