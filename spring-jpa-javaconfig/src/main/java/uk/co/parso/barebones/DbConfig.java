/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author sam
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "testEntityManagerFactory",
        transactionManagerRef = "testTransactionManager",
        basePackages = {"uk.co.parso.barebones.repositories"}
)
public class DbConfig {
       
    @Bean(name = "testEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean testEntityManagerFactory() throws SQLException {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan("uk.co.parso.barebones.entities");
        factory.setDataSource(testDataSource());
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setJpaProperties(hibProperties());
        factory.afterPropertiesSet();
        factory.setJpaDialect(new HibernateJpaDialect());
        
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        factory.setJpaVendorAdapter(adapter);

        return factory;
    }

    private Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.show_sql", "true");
        return properties;
    }

    @Bean(name = "testTransactionManager")
    public PlatformTransactionManager testTransactionManager() throws SQLException {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(testEntityManagerFactory().getObject());
        txManager.setJpaDialect(new HibernateJpaDialect());
        return txManager;
    }

    @Bean
    @Resource(name = "jdbc/test")
    public DataSource testDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?zeroDateTimeBehavior=convertToNull&amp;useUnicode=true&amp;connectTimeout=5000&amp;socketTimeout=60000");
        dataSource.setUsername("sam");
        dataSource.setPassword("sam");

        return dataSource;
    }
}
