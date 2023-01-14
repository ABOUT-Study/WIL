# 예외처리
### JPA 표준 예외 처리
- JPA 표준 예외들은 javax.persistence.PersistenceException의 자식 클래스다. 그리고 이예외 클래스는 RuntimeException의 자식이다. ***따라서 JPA 예외는 모두 언체크 예외다.***
- JPA 표준 예외는 크게 2가지로 나눌 수 있다.
  1. 트랜잭션 롤백을 표시하는 예외 : 심각한 예외이므로 복구해선 안된다. 
  2. 트랜잭션 롤백을 표시하지 않는 예외 : 심각한 에외는 아니다. 따라서 개발자가 트랜잭션을 커밋할지 롤백할지 판단하면 된다.

### 스프링 프레임워크의 JPA 에외 변환
- 서비스 계층에서 데이터 접근 계층의 구현 기술(=JPA)에 직접 의존하는 것은 좋은 설계는 아니다. 이것은 예외도 마친가지다. ***즉, 서비스 계층에서 JPA의 예외를 직접 사용하면 JPA에 의존하게 된다.***
- 이런 문제를 해결하기위해 스프링 프레임워크는 데이터 접근 계층에 대한 예외를 추상화 할 수 있다.
- ex) javax.persistence.PersistenceException -> org.springgramework.orm.jpa.JpaSystemException, javax.persistence.NonUniqueResultException -> org.springframework.dao.IncorrectrResultSizeDataAccessException
javax.persistence.LockTimeoutException -> org.springframework.dao.CannotAcquireLockException

### 스프링 프레임워크에 JPA 예외 변환기 적용
- JPA 예외를 스프링 프레임워크가 제공하는 추상화된 예외로 변경하려면 PersistenceExceptionTransactionPostProcessor를 스프링 빈으로 등록하면 된다. 이것은 @Repository 어노테이션을 사용한 곳에 예외 변환 AOP를 적용해서 JPA 예외를 스프링 프레임워크가 추상화한 예외로 변환해준다.

```
@Bean
public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	return new PersistenceExceptionTranslationPostProcessor();
}
```

```
@Repository
public class NoResultExceptionTestService {
  @PersistenceContext EntityManager em;
  
  public Member findMember() throws javax.persistence.NoResultException
  {
      return em.createQuery("select m from Member m", Member.class).getSingleResult();
  }
}
```

- 위의 코드에서 조회된 결과가 없으면 NoResultException 예외가 발생한다. 이 예외가 findMember() 메소드를 나갈때  PersistenceExceptionTransactionPostProcessor에서 등록한 AOP 인터셉터가 동작해서 해당 예외를 org.springframework.dao.EmptyResultDataAccessException 예외로 변환해서 반환한다.

### 트랜잭션 롤백시 주의사항
- 트랜잭션을 롤백하는 것은 데이터베이스의 반영 사항만 롤백 하는 것이지 수정한 자바 객체까지 원 상태로 복구해주지는 않는다. ***예를 들어 엔티티 수정중에 문제가 있어서 트랜잭션을 롤백하면 데이터베이스의 데이터는 원래대로 복구되지만 객체는 수정된 상태로 영속성 컨텍스트에 남아 있다. 따라서 새로운 영속성 컨텍스트를 생성해서 사용하거나 EntityManager.clear()를 호출해서 영속성 컨텍스트를 초기화한 다음에 사용해야 한다.***
- 스프링 프레임워크는 이런 문제를 예방하기 위해 ***영속성 컨텍스트의 범위에 따라 다른 방법을 사용한다.***
	1. 기본 전략인 트랜잭션당 영속성 컨텍스트 전략은 문제가 발생하면 트랜잭션 AOP 종료 시점에 트랜잭션을 롤백하면서 영속성 컨텍스트도 함께 종료하므로 문제가 발생하지 않는다.
	2. 문제는 OSIV처럼 영속성 컨텍스트의 범위를 트랜잭션 범위보다 넓게 사용해서 여러 트랜잭션이 하나의 영속성 컨텍스트를 사용할 때 발생한다. ***스프링 프레임워크는 영속성 컨텍스트의 범위를 트랜잭션의 범위보다 넓게 설정하면 트래잭션 롤백시 영속성 컨텍스트를 초기화해서 잘못된 영속성 컨텍스트를 사용하는 문제를 예방한다.***
