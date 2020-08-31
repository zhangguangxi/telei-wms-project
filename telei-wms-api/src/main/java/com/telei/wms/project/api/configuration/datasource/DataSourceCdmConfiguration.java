package com.telei.wms.project.api.configuration.datasource;


import com.nuochen.framework.app.commons.DataSourceHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Description: 数据源配置
 * @Auther: leo
 * @Date: 2020/6/8 15:35
 */
@Configuration
@MapperScan(basePackages = {DataSourceCdmConfiguration.REPOSITORY_PACKAGE}, sqlSessionFactoryRef = "wmsSqlSessionFactory")
public class DataSourceCdmConfiguration {
    static final String REPOSITORY_PACKAGE = "com.telei.wms.datasource.wms.repository";
    private static final String CONFIGURATION_PREFIX = "datasource.wms";
    private static final String MAPPER_LOCATION_PATTERN = "classpath:" + REPOSITORY_PACKAGE.replace(".", "/") + "/**/*.xml";

    /**
     * 数据源
     * @return
     */
    @Primary
    @Bean
    @ConfigurationProperties(prefix = CONFIGURATION_PREFIX)
    public DataSource wmsDataSource() {
        return DataSourceHelper.createDataSource();
    }

    /**
     * 事务管理器
     * @return
     */
    @Primary
    @Bean
    public DataSourceTransactionManager wmsTransactionManager() {
        return DataSourceHelper.createDataSourceTransactionManager((wmsDataSource()));
    }

    /**
     * session工厂
     * @return
     */
    @Primary
    @Bean
    public SqlSessionFactory wmsSqlSessionFactory() {
        return DataSourceHelper.createSqlSessionFactory(wmsDataSource(), MAPPER_LOCATION_PATTERN);
    }
}
