## Been 구성 정보

![Been 구성 정보1](https://user-images.githubusercontent.com/68458092/221560969-01515f7d-50e5-487e-84b0-4f550c8abc53.jpeg)

![Been 구성 정보2](https://user-images.githubusercontent.com/68458092/221561068-6d7d624a-ce6e-48df-8fcb-225619851e77.jpeg)

![Been 구성 정보3](https://user-images.githubusercontent.com/68458092/221561157-12401039-35ed-4369-8389-5376cd150a39.jpeg)


## Auto Configuration

Import 를 사용해서 WebServer 와 DispatcherServlet Been 등록을 분리하고  
EnableMyAutoConfiguration 을 추가해 Auto Configuration 을 구성
```Java
@Configuration
public class DispatcherServletConfig {
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }
}

@Configuration
public class TomcatWebServerConfig {
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({TomcatWebServerConfig.class, DispatcherServletConfig.class})
public @interface EnableMyAutoConfiguration {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
@ComponentScan
@EnableMyAutoConfiguration
public @interface MySpringBootApplication {
}
```

![AutoConfiguration 구성](https://user-images.githubusercontent.com/68458092/221560795-ee88f1a2-f393-4352-ac70-eaf506a42cca.jpeg)

