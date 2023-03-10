## Jetty 서버 구성 추가

- Jetty 서블릿 컨테이너를 추가해서 Tomcat 과 Jetty 중 어떤걸 사용하고 사용하지 않을지 선택하도록 만들어보기

```Java
implementation 'org.springframework.boot:spring-boot-starter-jetty' // 디펜던시 추가
aco.config.autoconfig.JettyWebServerConfig // ImportSelector 추가
  
@MyAutoConfiguration
@Conditional(JettyWebServerConfig.JettyCondition.class)
public class JettyWebServerConfig {
    @Bean("jettyWebServerFactory")
    public ServletWebServerFactory servletWebServerFactory() {
        return new JettyServletWebServerFactory();
    }

    static class JettyCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return true;
        }
    }
}
```

- @Conditional 은 메소드 레벨에도 적용 가능하지만 클래스 레벨에서 false 일 경우 메소드는 찾지 않음
    
![@Conditional](https://user-images.githubusercontent.com/68458092/224332339-a762bf44-d579-4b72-aaaa-dc31adabfa19.png)
