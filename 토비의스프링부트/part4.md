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
            return ClassUtils.isPresent("org.eclipse.jetty.server.Server", context.getClassLoader());
        }
    }
}
```

- @Conditional 은 메소드 레벨에도 적용 가능하지만 클래스 레벨에서 false 일 경우 메소드는 찾지 않음
    
![@Conditional](https://user-images.githubusercontent.com/68458092/224332339-a762bf44-d579-4b72-aaaa-dc31adabfa19.png)
  
- @Conditional 을 직접 사용하는 대신 공통적으로 사용되는 부분을 메타어노테이션을 사용해 공통화
```Java
public class MyOnClassConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalMyOnClass.class.getName());
        String value = (String) annotationAttributes.get("value");
        return ClassUtils.isPresent(value, context.getClassLoader());
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(MyOnClassConditional.class)
public @interface ConditionalMyOnClass {
    String value();
}

@MyAutoConfiguration
@ConditionalMyOnClass("org.eclipse.jetty.server.Server")
public class JettyWebServerConfig {
    @Bean("jettyWebServerFactory")
    public ServletWebServerFactory servletWebServerFactory() {
        return new JettyServletWebServerFactory();
    }
}
```
  
- 상단 Conditional 공통화 코드의 도식화
  
<img width="1227" alt="Custom Conditional" src="https://user-images.githubusercontent.com/68458092/224537671-5d96515b-ab87-48ae-bd2e-ab7e2417433b.png">
  
- 자동구성정보를 사용하지 않고 내가 만든 구성정보를 사용하려면 내가 만든 구성정보를 스프링의 자동구성 정보에 등록하는 방법도 있지만  
사용자 구성정보에 직접 정의를 하는 방법이 있음.
<img width="1290" alt="자동구성정보에 사용자구성정보 삽입" src="https://user-images.githubusercontent.com/68458092/224537729-b03159a7-bd70-43d6-9eab-277aa4e8446e.png">


