## 빈 오브젝트와 역할과 구분
### 크게 스프링 컨테이너가 생성하고 관리하는 빈들을 어플리케이션 빈과 컨테이너 인프라스트럭처 빈으로 나눔
- 어플리케이션 빈 : 개발자가 어떤 빈을 사용하겠다고 명시적으로 구성정보를 제공한것
- 컨테이너 인프라스트럭처 빈 : 스프링 컨테이너 자신이거나 스프링컨테이너가 확장하면서 추가한 빈들
  
![Been 구성 정보1](https://user-images.githubusercontent.com/68458092/221560969-01515f7d-50e5-487e-84b0-4f550c8abc53.jpeg)
- 애플리케이션 로직 빈 : 서비스 로직이 담겨져있는 빈
- 애플리케이션 인프라스트럭처 빈 : 만들어져 있는 기능을 사용하기 위해 구성정보를 작성해서 사용하는 빈
  
![Been 구성 정보2](https://user-images.githubusercontent.com/68458092/221561068-6d7d624a-ce6e-48df-8fcb-225619851e77.jpeg)
- 스프링부트에서 기본적으로 있어야 하는 빈이지만 애플리케이션 인프라스트럭처 빈인 이유는 명시적으로 선언을 해줘야지만 빈으로 등록되고 사용됨
  
![Been 구성 정보3](https://user-images.githubusercontent.com/68458092/221561157-12401039-35ed-4369-8389-5376cd150a39.jpeg)


## Auto Configuration (자동 구성정보는 어떤 방식으로 이루어지는가?)

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

- EnableMyAutoConfiguration 에 정적으로 Import를 추가했던 코드를 ImportSelector 인터페이스를 이용해 동적으로 변경
- 아래에선 ImportSelector 를 확장한 DeferredImportSelector 를 사용
  
```Java
public class MyAutoConfigImportSelector implements DeferredImportSelector {
    private final ClassLoader classLoader;

    public MyAutoConfigImportSelector(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        /**
         * ImportCandidates.laod() 가 불러오는 경로
         * META-INF/spring/full-qualified-annotation-name
         * 실제로 스프링부트의 @AutoConfiguration 메타어노테이션을 달고있는 기본 autoconfiguration 들이 아래 경로에 들어가 있음
         * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
         * 위에 경로에 144개의 autoconfiguration 이 있고 그외에도 총 300개 가까이 되는 것들이 있는데
         * 사용하지 않는 설정들은 어떻게 되는지 알아보게됨
         */
        List<String> autoConfigs = new ArrayList<>();

        ImportCandidates.load(MyAutoConfiguration.class, classLoader).forEach(autoConfigs::add);
        return autoConfigs.toArray(new String[0]);
    }
}
```

  
  ![ImportSelector경로 그림](https://user-images.githubusercontent.com/68458092/224325465-ccd388b3-c701-43ef-b352-d532aab49b8f.png)
  

## @Configuration(proxyBeanMethods = false) 옵션
```Java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * spring 에 자동 등록된 Been 뿐만 아니라 직접 원하는 Been 을 등록해야 할 수도 있기 때문에
 *  @Configuration 의 동작 방식을 알고 넘어가야함
 */
@Configuration(proxyBeanMethods = false)
public @interface MyAutoConfiguration {
}
```

```Java
/**
 * 학습 테스트
 * 내가 만든 코드만 테스트 하는게 아닌
 * 만들어져 있는 것을 테스트
 */
public class ConfigurationTest {

    @Test
    void configuration() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(MyConfiguration.class);
        ac.refresh();

        Been1 been1 = ac.getBean(Been1.class);
        Been2 been2 = ac.getBean(Been2.class);

        Assertions.assertThat(been1.common).isSameAs(been2.common); // true
    }

    @Test
    void proxyCommonMethod() {
        MyConfigurationProxy myConfigurationProxy = new MyConfigurationProxy();
        Been1 been1 = myConfigurationProxy.been1();
        Been2 been2 = myConfigurationProxy.been2();

        Assertions.assertThat(been1.common).isSameAs(been2.common); // true
    }

    static class MyConfigurationProxy extends MyConfiguration{
        private Common common;

        @Override
        Common common() {
            if (this.common == null) this.common = super.common();

            return this.common;
        }

    }

    // spring 5.2 버전 부터 추가된 기능
    // proxyBeanMethods = false 사용시 프록시 사용 X
    // 예전에는 true 인 디폴트값 사용을 권장 했으나 최근에는
    // 다른 Been 안에서 다른 Been 을 사용하는게 아니라면 false 를 사용하는 것도 좋다고 함
    // 다른 Been 을 쓰는게 아닌데 true 디폴트값 사용시 굳이 프록시 비용이 발생함
    @Configuration
    static class MyConfiguration {
        @Bean
        Common common() {
            return new Common();
        }

        @Bean
        Been1 been1() {
            return new Been1(common());
        }
        @Bean
        Been2 been2() {
            return new Been2(common());
        }
    }
    static class Been1 {
        private final Common common;

        Been1(Common common) {
            this.common = common;
        }
    }

    static class Been2 {
        private final Common common;

        Been2(Common common) {
            this.common = common;
        }
    }

    static class Common {

    }
}
```


