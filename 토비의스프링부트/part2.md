## DispatcherServlet 사용과 스프링컨테이너로 통합
```Java
public class MySpringApplication {
    public static void run(Class<?> applicationClass, String... args) {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext(){
            @Override
            protected void onRefresh() {
                super.onRefresh();
                
                ServletWebServerFactory servletWebServerFactory = this.getBean(ServletWebServerFactory.class);
                DispatcherServlet dispatcherServlet = this.getBean(DispatcherServlet.class);
                
                WebServer webServer = servletWebServerFactory.getWebServer(servletContext -> {
                    servletContext.addServlet("dispatcherServlet", dispatcherServlet)
                            .addMapping("/*");
                });
                webServer.start();
            }
        };
        applicationContext.register(applicationClass);
        applicationContext.refresh();
    }
}

@Configuration
@ComponentScan
public class HelloSpringBootApplication {

	// 팩토리 메서드를 사용해서 빈으로 등록
	@Bean
	public ServletWebServerFactory servletWebServerFactory() {
		return new TomcatServletWebServerFactory();
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		return new DispatcherServlet();
	}

	public static void main(String[] args) {
		MySpringApplication.run(HelloSpringBootApplication.class, args);
	}
}
```

![디스페쳐서블릿 그림](https://user-images.githubusercontent.com/68458092/220891681-7ec283a4-c209-48fa-af17-683ee5d3bcef.png)

#### 자세한 디스패처 서블릿의 동작 과정은 [여기](https://mangkyu.tistory.com/216) 를 참고 해주세요.

## DI & Decorator, Proxy 패턴

### * 의존 역전
![의존역전 그림](https://user-images.githubusercontent.com/68458092/221351249-0d3d386f-c387-4104-a13f-454157ce8989.png)

### * 데코레이터 패턴
![데코레이터 그림](https://user-images.githubusercontent.com/68458092/221351267-d4bacaf6-d262-4993-9ab8-4b9dfa66fecc.png)

![데코레이터 그림2](https://user-images.githubusercontent.com/68458092/221351284-7eff04bd-a558-4821-88ea-8770345e5520.png)
```Java
@Service
@Primary // 빈이 늘어늘 수 있기 때문에 java 코드로 명시적으로 해주는게 더 좋을 수 있음
public class HelloDecorator implements HelloService {

    private final HelloService helloService;

    public HelloDecorator(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public String sayHello(String name) {
        return "*" +  helloService.sayHello(name) + "*";
    }
}
```
### * 프록시 패턴
![프록시 그림](https://user-images.githubusercontent.com/68458092/221351295-8187814e-c64f-46ef-97f3-9f782f221f8e.png)


## 메타 에노테이션과 합성 애노테이션

### * 메타 애노테이션

![메타애노테이션 그림](https://user-images.githubusercontent.com/68458092/221351000-952a2a33-dea1-40e1-b9e6-005b927b4cc0.png)

```Java
// Target 이 중요
// @Test 의 Target 이 애노테이션, 매소드 타입이기 때문에 메타 애노테이션이 가능함
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@UnitTest
@interface FestUnitTest {
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Test
@interface UnitTest {
}

@FestUnitTest
void helloApi() {
    ...
}
```

### * 합성 애노테이션 (1개이상의 애노테이션 합성)

```Java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
@ComponentScan
public @interface MySpringBootApplication {
}

@MySpringBootApplication
public class HelloSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringBootApplication.class, args);
	}
}
```
