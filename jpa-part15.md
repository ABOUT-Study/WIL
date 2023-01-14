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

# 트랜잭션 롤백시 주의사항
- 트랜잭션을 롤백하는 것은 데이터베이스의 반영 사항만 롤백 하는 것이지 수정한 자바 객체까지 원 상태로 복구해주지는 않는다. ***예를 들어 엔티티 수정중에 문제가 있어서 트랜잭션을 롤백하면 데이터베이스의 데이터는 원래대로 복구되지만 객체는 수정된 상태로 영속성 컨텍스트에 남아 있다. 따라서 새로운 영속성 컨텍스트를 생성해서 사용하거나 EntityManager.clear()를 호출해서 영속성 컨텍스트를 초기화한 다음에 사용해야 한다.***
- 스프링 프레임워크는 이런 문제를 예방하기 위해 ***영속성 컨텍스트의 범위에 따라 다른 방법을 사용한다.***
	1. 기본 전략인 트랜잭션당 영속성 컨텍스트 전략은 문제가 발생하면 트랜잭션 AOP 종료 시점에 트랜잭션을 롤백하면서 영속성 컨텍스트도 함께 종료하므로 문제가 발생하지 않는다.
	2. 문제는 OSIV처럼 영속성 컨텍스트의 범위를 트랜잭션 범위보다 넓게 사용해서 여러 트랜잭션이 하나의 영속성 컨텍스트를 사용할 때 발생한다. ***스프링 프레임워크는 영속성 컨텍스트의 범위를 트랜잭션의 범위보다 넓게 설정하면 트래잭션 롤백시 영속성 컨텍스트를 초기화해서 잘못된 영속성 컨텍스트를 사용하는 문제를 예방한다.***

# 엔티티 비교
- 영속성 컨텍스트 내부에는 엔티티 인스턴스를 보관하기 위한 1차 캐시가 있다.  이 1차 캐시는 영속성 컨텍스트와 생명주기를 같이 한다.

### 영속성 컨텍스트가 같을 때 엔티티 비교
- 다음은 같은 트랜잭션 범위에 있으므로 같은 영속성 컨텍스트를 사용한다. 따라서 영속성 컨텍스트가 같으면 엔티티를 비교할 때 다음 3가지 조건을 모두 만족한다.
	- 동일성: == 비교가 같다
	- 동등성 : equals() 비교가 같다
	- 데이터베이스 동등성 : @Id인 데이터베이스 식별자가 같다
```
@Test
@Transactional
public void 회원가입() throws Exception {
	// Given
	Member member = new Member("kim");

	// When
	Long saveId = memberService.join(member);

	// Then
	Member findMember = memberRepository.findOne(saveId);
	assertTrue(member == findMember); // true 참조값 비교
}
```
![jpa15-1](https://user-images.githubusercontent.com/22884224/212464758-f1518c4f-ed81-4076-a15c-e3d8b85c16dc.png)
 
### 영속성 컨텍스트가 다를 때 엔티티 비교
```
@Test
//    @Transactional
public void 회원가입() throws Exception {
    	// Given
	Member member = new Member("kim");

	// When
	Long saveId = memberService.join(member);

	// Then
	Member findMember = memberRepository.findOne(saveId);
	assertTrue(member == findMember); // true 참조값 비교
}

@Transactional
public class MemberService {
	@Autowired MemberRepository memberRepository;
	
	public Long join(Member member) {
		...
		memberRepository.save(member);
		return member.getId();
	}
}

@Transactional
public class MemberRepository {
	@PersistenceContext
	EntityManager em;
	
	public void save(Member member) {
		em.persist(member);
	}
	
	public Member findOne(Long id) {
		return em.find(Member.class, id);
	}
}
```

![jpa15-2](https://user-images.githubusercontent.com/22884224/212464767-46110286-289e-47b1-bde6-eb8ea1e202ce.png)
![jpa15-3](https://user-images.githubusercontent.com/22884224/212464772-075d44a8-4a4a-469d-a4b4-c06ad21a7c31.png)

- 위의 코드는 동일성(==)비교가 실패한다. 왜냐하면 member와 findMember는 다른 영속성 컨텍스트에서 관리 되었기 때문이다. 하지만 동등성(equals), 데이터베이스 동등성 비교는 성공한다.
- 즉, 동일성 비교는 같은 영속성 컨텍스트의 관리를 받는 영속상태의 엔티티에만 적용할 수 있다.

# 프록시 심화 주제
- 프록시는 원본 엔티티를 상속받아서 만들어지므로 엔티티를 사용하는 클라이언트는 엔티티가 프록시인지 아니면 원본 엔티티인지 구분하지 않고 사용할 수 있다. 이로 인해 문제가 발생할 수도 있다.
### 영속성 컨텍스트와 프록시
- 영속성 컨텍스트는 자신이 관리하는 영속 엔티티의 동일성을 보장한다. 그럼 프록시로 조회한 엔티티의 동일성도 보장할까?
```
@Test
public void 프록시와_동등성비교() {
		Member saveMember = new Member("member1", "회원1");
		em.persist(saveMember);
		em.flush();
		em.clear();

		Member refMember = em.getReference(Member.class, "member1");
		Member findMember = new Member(Member.class, "member1");
		
 		System.out.println("refMember = " + refMember.getClass());
    		System.out.println("findMember = " + findMember.getClass());
    
		Assert.assertTrue(refMember == findMember); // true
}
```

- refMember는 프록시로 조회했고, findMember는 원본 엔티티다. 그러므로 둘은 서로 다른 인스턴스로 생각할 수 있다.
- 하지만 이러면 같은 영속성 컨텍스트의 동일성을 보장한다는 원칙을 지킬 수가 없다. 그래서 ***결과는 둘다 프록시로 조회된다.***
- ***이렇게 된 원리는 프록시로 조회된 엔티티(refMember)에 대해서 같은 엔티티를 찾는 요청(findMember)이 오면 원본 엔티티가 아닌 처음 조회된 프록시를 조회한다. 그래서 하나의 영속성 컨텍스트에서 동일성(==)이 보장된다.***

- 그럼 반대의 경우는 어떻게 될까?
```
@Test
public void 프록시와_동등성비교() {
		Member saveMember = new Member("member1", "회원1");
		em.persist(saveMember);
		em.flush();
		em.clear();

		Member findMember = new Member(Member.class, "member1");
		Member refMember = em.getReference(Member.class, "member1");
		
 		System.out.println("refMember = " + refMember.getClass());
    		System.out.println("findMember = " + findMember.getClass());
    
		Assert.assertTrue(refMember == findMember); // true
}
```
- 이것도 당연히 동일성을 보장해야한다. 그래서 원본엔티티(findMember)가 먼저 조회되고 영속성 컨텍스트가 관리하고있고, 다음에 프록시를 조회하더라도 원본 엔티티를 반환한다. 그래서 이것도 동일성(==)을 보장한다.

### 프록시 타입 비교
- 프록시는 원본 엔티티를 상속 받아서 생성된다. 그러므로 프록시로 조회된 엔티티의 타입을 비교할 때는 ==가 아니라 instanceof를 사용해야한다.
