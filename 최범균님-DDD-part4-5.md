# 4장 리포지터리와 모델 구현

## 모듈 위치
리포지터리 인터페이스는 애그리거트와 같이 도메인 영역에 속하고 리포지터리를 구현한 클래스는 인프라스트럭처 영역에 속한다.   
각 타입의 패키지 구성은 다음과 같다.

<img width="820" alt="image" src="https://user-images.githubusercontent.com/20812458/222873345-508ecc01-f99f-4422-8e48-abfd27a6ef8a.png">   
팀 표준에 따라 리포지터리 구현 클래스를 domain.impl과 같은 패키지에 위치시킬 수도 있는데 이것은 리포지터리 인터페이스와 구현체를 분리하기 위한 타협안 같은 것이지 좋은 설계원칙을 따르는 것은 아니다.   
가능하면 리포지터리 구현 클래스를 인프라스트럭처 영역에 위치 시켜서 인프라스트럭처에 대한 의존을 낮춰야 한다.   
** DIP를 적용하는 주된 이유는 저수준 구현이 변경되더라도 고수준이 영향을 받지 않도록 하기 위함이다. (여기서 저수준은 repositoryImpl를 의미하고 고수준은 serviceImpl이라 생각하면 편하다.)   

## 중요하다고 생각된 구문
1. set 메서드 대신 의도가 잘 드러나는 기능을 제공. Ex) 주문 취소 시 setState() 보다 cancel() 로 잘 표현해보자   
2. JPA에서 식별자를 밸류 타입으로 사용할 때 @Id 대신 @EmbeddedId를 사용하되 식별자 타입은 Serializable 이여야 함.   
비교를 위해 equals()와 hashcode() 값을 적절하게 사용   
[출처] EmbeddedId란 : https://galid1.tistory.com/608   
3. 밸류 컬렉션을 별도 테이블로 매핑할 때는 @ElementCollection과 @CollectionTable을 함께 사용한다.   
[출처] ElementCollection, CollectionTable란 : https://prohannah.tistory.com/133   

# 5장 스프링 데이터 JPA를 이용한 조회기능

## CQRS
CQRS는 명령 모델과 조회 모델을 분리하는 패턴이다. 명령 모델은 상태를 변경하는 기능을 구현할 때 사용하고 조회 모델은 데이터를 조회하는 기능을 구현할 때 사용한다.   
예를 들어 회원 가입, 암호 변경, 주문 취소처럼 상ㅌ태를 변경하는 기능을 구현할 때 명령 모델을 사용한다. 주문 목록, 주문 상세처럼 데이터를 보여주는 기능을 구현할 때는 조회 모델을 사용한다.   
** 책의 필자는 조회 모델을 구현할 때 다양한 기술을 사용한다. JPA를 사용할 때도 있고 마이바티스를 사용할 때도 있고 JdbcTemplate을 사용할 때도 있다.   
그러니 모든 DB 연동 코드를 JPA만 사용해서 구현해야 한다고 생각하지는 말자.   

## Speficiation (JPA Specification은 criteria API를 기반으로 만들어졌다.)
<img width="759" alt="image" src="https://user-images.githubusercontent.com/20812458/222874341-66467cd8-e6e3-4afe-9336-32d2c0c584c5.png">   
위의 JpaSpecificationExecutor을 상속 받아 구현을 하면 된다.   
<img width="691" alt="image" src="https://user-images.githubusercontent.com/20812458/222874399-a4dd6e25-5bc1-4d6c-aace-dff892e57e56.png">   
위와 같이 Speficiation을 구현해서 사용하면 된다. 더 간결하게 사용한다면 스펙 빌더를 사용해서 if를 사용할 때의 복잡함을 단순하게 만들고, 연속된 변수 할당을 줄여 코드 가독성을 높인다.   

## 중요하다고 생각된 구문
개인적인 생각으로 사실 5장에서는 중요하다고 생각되는 부분이 CQRS의 정의만 있다고 생각했다.   
DDD이지만 JPA를 사용하는 방법만 적혀있고 Speficiation같은것도 queryDsl이 훨씬 보기 좋고 간결하게 표현되지 않을까 싶기도 했기 때문이다. 

