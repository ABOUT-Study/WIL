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

## @Conditional
- matches() 메소드 가지고 있는 Been 을 조건부로 등록하게 해주는 어노테이션
- 메소드 레벨에도 적용 가능하지만 클래스 레벨에서 false 일 경우 메소드는 찾지 않음
    
![@Conditional](https://user-images.githubusercontent.com/68458092/224332339-a762bf44-d579-4b72-aaaa-dc31adabfa19.png)
  
- @Conditional 을 직접 사용하는 대신 공통적으로 사용되는 부분을 메타어노테이션을 사용해 공통화
- matches 를 매번 구현하는게 아니라 @ConditionalMyOnClass 에 해당 하는 클래스 경로만 넣으면 되도록 변경
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

```Java
@Configuration(proxyBeanMethods = false)
public class WebServerConfiguration {

    // autoConfig 에 있는 설정들은 전부 내가 만들지 않아도 자동으로 spring에 있는 설정들 이지만
    // 해당 설정들을 사용하면서 원하는 설정만 바꿔서 사용하려면 아래 Been 으로 사용자설정에서 등록해준뒤
    // config 에서 @ConditionalOnMissingBean 로 Conditional 을 메서드에서 확인하게 함
    @Bean
    ServletWebServerFactory customWebServerFactory() {
        TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
        serverFactory.setPort(9090);
        return serverFactory;
    }
}

@MyAutoConfiguration
@ConditionalMyOnClass("org.apache.catalina.startup.Tomcat")
public class TomcatWebServerConfig {
    @Bean("tomcatWebServerFactory")
    @ConditionalOnMissingBean // 메서드레벨에서 해당 빈이 등록이 되어있으면 무시하고 등록되지않으면 아래 빈을 등록시킴
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
```

## Environment Properties
- 설정들을 @Been 등록으로 변경하는게 아니라 설정으로 등록하는 방법  

![스크린샷 2023-03-21 오후 10 05 00](https://user-images.githubusercontent.com/68458092/226622223-6d40ba6d-e773-4ddb-9ba4-fbe0219f369e.png)
- 각 설정 방법들은 우선순위가 있다.
- StandardServeletEnvironment > StandardEnvironment > @PropertiesSource > SpringBoot
- 최신 spring 에서는 StandardServeletEnvironment(web.xml 등) 보다 StandardEnvironment 설정을 권장하고 있다.

![스크린샷 2023-03-21 오후 10 06 00](https://user-images.githubusercontent.com/68458092/226623613-0b27e959-4af5-4667-a62b-4b81f0bf883d.png)

```Java
// Environment 에 property 들이 담겨져 있는데 우선순위가 있다.
@Bean("tomcatWebServerFactory")
@ConditionalOnMissingBean
public ServletWebServerFactory servletWebServerFactory(Environment env) {
    TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
    serverFactory.setContextPath(env.getProperty("contextPath"));
    return serverFactory;
}

apllication.properties 파일
contextPath=/app  3순위
```
![스크린샷 2023-03-21 오후 10 48 05](https://user-images.githubusercontent.com/68458092/226627224-0c96fc96-2584-41be-b109-66ea7010d2f4.png)

### 위에서 Environment 에서 직접 프로퍼티를 가져오는 코드를 리팩토링

```Java

@MyAutoConfiguration
@ConditionalMyOnClass("org.apache.catalina.startup.Tomcat")
public class TomcatWebServerConfig {
    // 원래 @Value 의 placeHolder 의 치환은 스프링컨테이너의 기본 동작 방식이 아님
    // 스프링컨테이너를 확장해서 기능을 추가해줘야함 => PropertySourcesPlaceholderConfigurer
    @Value("${contextPath}")
    String contextPath;
    
    @Bean("tomcatWebServerFactory")
    @ConditionalOnMissingBean
    public ServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
        serverFactory.setContextPath(this.contextPath);
        return serverFactory;
    }
}

@MyAutoConfiguration
public class PropertyPlaceholderConfig {

    // BeanFactoryPostProcessor >> 시간이 지나며 이걸 확장해서 만들어진게 PropertySourcesPlaceholderConfigurer
    // Been 정보를 모은 다음 후처리기(어떤 프로세서가 진행되는 중간과정에 후킹해 들어가 부가작업)
    // 스프링컨테이너의 확장 포인터
    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

>>> 개선 버전

@Bean("tomcatWebServerFactory")
@ConditionalOnMissingBean // 메서드레벨에서 해당 빈이 등록이 되어있으면 무시하고 등록되지않으면 아래 빈을 등록시킴
public ServletWebServerFactory servletWebServerFactory(ServerProperties properties) {
    TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
    serverFactory.setContextPath(properties.getContextPath());
    serverFactory.setPort(properties.getPort());
    return serverFactory;
}
    
public class ServerProperties {
    private String contextPath;
    private int port;
}

@MyAutoConfiguration
public class ServerPropertiesConfig {
    @Bean
    ServerProperties serverProperties(Environment environment) {
        // Binder 해당 클래스 setter 네임에 맞춰서 자동 바인딩 해줌
        return Binder.get(environment).bind("", ServerProperties.class).get();
    }
}

@MyAutoConfiguration
public class ServerPropertiesConfig {
    @Bean
    ServerProperties serverProperties(Environment environment) {
        // Binder 해당 클래스 setter 네임에 맞춰서 자동 바인딩 해줌
        return Binder.get(environment).bind("", ServerProperties.class).get();
    }
}

>>> 위 방법도 프로퍼티 값이 많아지면 검증&재사용이 어려워지기 때문에 추가 수정

@MyAutoConfiguration
@ConditionalMyOnClass("org.apache.catalina.startup.Tomcat")
@EnableMyConfigurationProperties(ServerProperties.class) // import 역활
public class TomcatWebServerConfig {
    ...
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MyConfigurationPropertiesImportSelector.class)
public @interface EnableMyConfigurationProperties {
    Class<?> value();
}

public class MyConfigurationPropertiesImportSelector implements DeferredImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MultiValueMap<String, Object> attr = importingClassMetadata.getAllAnnotationAttributes(EnableMyConfigurationProperties.class.getName());
        Class propertyClass = (Class) attr.getFirst("value");
        return new String[]{propertyClass.getName()};
    }
}

@MyConfigurationProperties(prefix = "server")
public class ServerProperties {
    private String contextPath;
    private int port;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface MyConfigurationProperties {
    String prefix();
}

@MyAutoConfiguration
public class PropertyPostProcessorConfig {
    @Bean
    BeanPostProcessor propertyPostProcessor(Environment env) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                MyConfigurationProperties annotation = AnnotationUtils.findAnnotation(bean.getClass(), MyConfigurationProperties.class);
                if (annotation == null) return bean;

                Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);
                String prefix = (String) annotationAttributes.get("prefix");

                return Binder.get(env).bindOrCreate(prefix, bean.getClass());
            }
        };
    }
}

```
