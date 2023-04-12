# Spring JDBC 자동 구성 개발

- jdbcTemplate 는 jdbcOperations 를 구현한 구현체
- datasource 는 DB와의 연결을 담당 그래서 url 등 설정이 필요함
- 원래는 각각의 클래스로 우선순위를 지정하고 설정을 해야하지만 지금은 간단하게 하나의 Datasource 만 가능하도록 설정

![image](https://user-images.githubusercontent.com/68458092/230820487-87d5891e-d016-4ef1-a3bf-5abfaae1e27a.png)

```Java
@MyAutoConfiguration
@ConditionalMyOnClass("org.springframework.jdbc.core.JdbcOperations")
@EnableMyConfigurationProperties(MyDataSourceProperties.class)
// Transaction 어노테이션에 대한 설정 이게 있어야 @Transaction 사용가능
// AOP 프록시 설정부분도 여기에 있음
@EnableTransactionManagement
public class DataSourceConfig {

    // @Bean 순서대로 호출 히카리가 위에 있어서 위에 먼저 확인을 함
    @Bean
    @ConditionalMyOnClass("com.zaxxer.hikari.HikariDataSource")
    @ConditionalOnMissingBean
    DataSource hikariDataSource(MyDataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());

        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    DataSource dataSource(MyDataSourceProperties properties) throws ClassNotFoundException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass((Class<? extends Driver>) Class.forName(properties.getDriverClassName()));
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());

        return dataSource;
    }

    @Bean
    @ConditionalOnSingleCandidate(DataSource.class) // 빈 구성정보에 이타입이 1개만 등록되어 있다면 가져와서 사용하겠다
    @ConditionalOnMissingBean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnSingleCandidate(DataSource.class)
    @ConditionalOnMissingBean
    JdbcTransactionManager jdbcTransactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }
}
```
