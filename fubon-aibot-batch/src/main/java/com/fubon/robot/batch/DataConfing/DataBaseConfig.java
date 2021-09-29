package com.fubon.robot.batch.DataConfing;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "robotEntityManagerFactory",
        transactionManagerRef= "robotTransactionManager",
        basePackages = { "com.fubon.robot.batch.robotData.repository" }
)
public class DataBaseConfig {

	@Autowired
	SystemFileSettinService systemFileSettinService;

	@Bean(name = "robotDbDataSource")
	public DataSource robotDbDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		SysSeting setting = systemFileSettinService.getSysFileSetting();
		dataSource = new DriverManagerDataSource();
		dataSource.setUrl(setting.getAibotDBServerAddress());
		dataSource.setUsername(setting.getAibotDBServerAccount());
		dataSource.setPassword(setting.getAibotDBServerPwd());
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		return dataSource;
	}

	
	@Bean(name = "robotEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		// new 一個entity factory
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();// new一個實作JPA的vendor
		vendorAdapter.setGenerateDdl(false);
		vendorAdapter.setShowSql(true); // 設定showSQL
	    Properties props = new Properties();
	    props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
	    factory.setJpaProperties(props);
		factory.setDataSource(robotDbDataSource()); // 設定datasource
		factory.setJpaVendorAdapter(vendorAdapter); // 設定Implement JPA vendor
		factory.setPackagesToScan("com.fubon.robot.batch.robot.data"); // 設定Entity package位置
		factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		factory.setPersistenceUnitName("robot");
		return factory;
	}

	@Bean(name = "robotTransactionManager")
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory factory = entityManagerFactory().getObject();
		
		return new JpaTransactionManager(factory);
	}


}
