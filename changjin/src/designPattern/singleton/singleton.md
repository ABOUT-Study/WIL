## 싱글톤(Singleton) 패턴

### 싱글톤 패턴이란
 - 인스턴스를 한 개만 생성하는 패턴

### 구현 방법
``` JAVA
public class Singleton {
    private static Singleton uniqueInstance;
    
    // 기타 인스턴스 변수
    
    private Singleton() {}

    public static synchronized Singleton getInstance() { // threadsafe
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}
```

### synchronized 문제점

1. 멀티스레드 환경에서 동기화(한 스레드가 getInstance()를 끝낼 때까지 다른 스레드는 기다림)를 강제하면 속도 문제가 발생한다.
2. getInstance 에서 동기화가 필요한 시점은 싱글톤이 인스턴스 생성을 체크하는 부분 뿐이므로 불필요한 오버헤드가 발생한다.


## synchronized 해결법

### 1. 인스턴스를 시작하자마자 만들기
``` JAVA
public class Singleton {
    private static Singleton uniqueInstance = new Singleton;
    
    // 기타 인스턴스 변수
    
    private Singleton() {}

    public static Singleton getInstance() { // threadsafe
        return uniqueInstance;
    }
}
```

1. 동적 바인딩 대신 클래스 로딩시 인스턴스를 생성한다.
2. JVM이 유일한 인스턴스를 생성하기 전에 어떤 스레드도 uniqueInstance 정적 변수에 접근할 수 없다.
3. 싱글톤 생성 비용이 비싸다면 싱글톤을 사용하지 않는 스레드에서도 생성 비용을 치뤄야한다는 단점이 있다.

### 2. Double Checking Locking(DCL) 사용 (Lazy Initialization)

``` JAVA
public class Singleton {
    private volatile static Singleton uniqueInstance;
    
    // 기타 인스턴스 변수
    
    private Singleton() {}
    synchronnized (Singleton.class)
    public static synchronized Singleton getInstance() {
        if(uniqueInstance == null){
            uniqueInstance = new Singleton();
        }         
    }
}
```

1. 변수가 초기화되었는지 확인한다 (Lock을 얻지 않음). 초기화되면 즉시 반환한다. (synchronized 블럭을 거치지 않음)
2. 변수가 초기화되지 않았다면 Lock을 얻는다.
3. 변수가 이미 초기화되었는지 다시 확인한다. 다른 스레드가 먼저 잠금을 획득했다면 이미 초기화를 수행했을 수 있다. 그렇다면 초기화 된 변수를 반환한다. 그렇지 않으면 초기화하고 변수를 반환한다. 
