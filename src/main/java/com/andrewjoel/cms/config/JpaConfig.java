package com.andrewjoel.cms.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class JpaConfig {
    @Autowired
    Environment environment;

    @Bean
    @ConfigurationProperties("app.jdbc")
    DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JpaTransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean
    HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            DataSource dataSource,
            HibernateJpaVendorAdapter jpaVendorAdapter,
            Properties jpaProperties
    ) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("com.andrewjoel.cms");
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean;
    }

    @Primary
    @Bean
    Properties jpaPropertiesProvider() {
        Properties properties = new Properties();

        properties.put("hibernate.max_fetch_depth", environment.getProperty("app.jdbc.max-fetch-depth"));
        properties.put("hibernate.show_sql", environment.getProperty("app.jdbc.show-sql"));
        properties.put("hibernate.generate_ddl", environment.getProperty("app.jdbc.generate-ddl"));
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("app.jdbc.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("app.jdbc.dialect"));

        return properties;
    }
}
