package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
public class UsDataSourceConfig {

    @Value("${jdbc.kunpeng.database.url:}")
    private String url;

    @Value("${jdbc.kunpeng.database.username:}")
    private String userName;

    @Value("${jdbc.kunpeng.database.password:}")
    private String password;

    @Bean("usDataSource")
    @ConfigurationProperties(prefix = "us.datasource")
    public DataSource dataSource()  {
        return new DruidDataSource();
    }

    @Bean("usJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("usDataSource") DataSource  dataSource) {
        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    @Bean("usRiskBaseDataSource")
    @ConfigurationProperties(prefix = "us.riskbase.datasource")
    public DataSource usRiskBaseDataSource()  {
        return new DruidDataSource();
    }

    @Bean("usJdbcTemplate2")
    public JdbcTemplate jdbcTemplate2(@Qualifier("usRiskBaseDataSource") DataSource  dataSource) {
        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    @Bean("kunpengDataSource")
    @ConfigurationProperties(prefix = "jdbc.kunpeng.database")
    public DataSource kunpengDataSource()  {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean("jdbcTemplate")
    public JdbcTemplate kunpengJdbcTemplate(@Qualifier("kunpengDataSource") DataSource  dataSource) {
        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }
}
