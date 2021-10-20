package com.fubon.robot.batch.DataConfing;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fubon.robot.batch.config.SpringDatasourceProperties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "evoiceEntityManagerFactory", transactionManagerRef = "evoiceTransactionManager", basePackages = {
		"com.fubon.robot.batch.Data.repository" })
public class EvoiceDataBaseConfig  {

	@Autowired
	SpringDatasourceProperties springDatasourceProperties;

	@Primary
	@Bean(name = "evoiceDbDataSource")
	public DataSource robotDbDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource = new DriverManagerDataSource();
		dataSource.setUrl(springDatasourceProperties.getUrl());
		dataSource.setUsername(springDatasourceProperties.getUsername());
		dataSource.setPassword(springDatasourceProperties.getPassword());
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		return dataSource;
	}

	

	@Primary
	@Bean(name = "evoiceEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		// new 一個entity factory
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();// new一個實作JPA的vendor
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(true); // 設定showSQL
		vendorAdapter.setDatabasePlatform("org.hibernate.dialect.OracleDialect");
		Properties props = new Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
		factory.setDataSource(robotDbDataSource()); // 設定datasource
		factory.setJpaVendorAdapter(vendorAdapter); // 設定Implement JPA vendor
		factory.setPackagesToScan("com.fubon.robot.batch.robot.evdata"); // 設定Entity package位置
		factory.setJpaProperties(props);
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		factory.setPersistenceUnitName("evoice");
		return factory;
	}

	@Primary
	@Bean(name = "evoiceTransactionManager")
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory factory = entityManagerFactory().getObject();
		return new JpaTransactionManager(factory);
	}

}
