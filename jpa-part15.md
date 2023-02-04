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
- ***프록시는 원본 엔티티를 상속받아서 만들어지므로*** 엔티티를 사용하는 클라이언트는 엔티티가 프록시인지 아니면 원본 엔티티인지 구분하지 않고 사용할 수 있다. 이로 인해 문제가 발생할 수도 있다.
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
		Member findMember = em.find(Member.class, "member1");
		
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

		Member findMember = em.find(Member.class, "member1");
		Member refMember = em.getReference(Member.class, "member1");
		
 		System.out.println("refMember = " + refMember.getClass());
    		System.out.println("findMember = " + findMember.getClass());
    
		Assert.assertTrue(refMember == findMember); // true
}
```
- 이것도 당연히 동일성을 보장해야한다. 그래서 원본엔티티(findMember)가 먼저 조회되고 영속성 컨텍스트가 관리하고있고, 다음에 프록시를 조회하더라도 원본 엔티티를 반환한다. 그래서 이것도 동일성(==)을 보장한다.

### 프록시 타입 비교
- 프록시는 원본 엔티티를 상속 받아서 생성된다. 그러므로 프록시로 조회된 엔티티의 타입을 비교할 때는 ==가 아니라 instanceof를 사용해야한다.

### 상속관계와 프록시

- 상속 관계를 프록시로 조회할 때 발생할 수 있는 문제점과 해결방안을 알아보자
![jpa 15-4](https://user-images.githubusercontent.com/22884224/216765251-8df43a3b-916b-467b-ac62-004b9ef130a9.png)

- 위와 같은 구조의 클래스 모델을 생성할 경우 프록시를 부모 타입으로 조회하면 문제가 발생한다.
```
@Test
public void 부모타입으로_프록시조회() {
	//테스트 데이터 준비
	Book saveBook = new Book();
	saveBook.setName("jpaBook");
	saveBook.setAuthor("kim");
	em.persist(saveBook);

	em.flush();
	em.clear();

	//테스트 시작
	Item proxyItem = em.getReference(Item.class, saveBook.getId());
	System.out.println("proxyItem = " + proxyItem.getClass());

	if (proxyItem instanceof Book) {			// false
		System.out.println("proxyItem instanceof Book");
		Book book = (Book) proxyItem;			// false
		System.out.println("책 저자 = " + book.getAuthor());
	}

	//결과 검증
	Assert.assertFalse(proxyItem.getClass() == Book.class);
	Assert.assertFalse(proxyItem instanceof Book);
	Assert.assertTrue(proxyItem instanceof Item);
}
```

- 그런데 출력 결과를 보면 기대와는 다르게 저자가 출력되지 않은 것을 알 수 있다.

### 왜 원하는 출력값 다를까?
- 실제 조회된 엔티티는 Book이므로 Book 타입을 기반으로 원본 엔티티 인스턴스가 생성된다. 그런데 em.getReference() 메소드에서 Item 엔티티를 대상으로 조회 했으므로 프록시인 proxyItem은 Item 타입을 기반으로 만들어진다. 이런 이유로 다음 연산이 기대와 다르게 false를 반환한다. 왜냐하면 proxyItem은 Item&Proxy 타입이고 이 타입은 Book 타입과 관계가 없기 때문이다.
- 정리하자면 다음과 같은 문제가 있다.
```
proxyItem instanceof Book // 1. instanceof 연산을 사용할 수 없다.
```
```
Book book = (Book) proxyItem; // 2. 하위타입으로 다운캐스팅을 할 수 없다.
```

### 문제 발생 경우를 좀 더 살펴보자
```
@Entity
public class OrderItem {
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	private Item item;
	
	...
}
```

```
@Test
public void 상속관계와_프록시_도메인모델() {
	//테스트 데이터 준비
	Book saveBook = new Book();
	saveBook.setName("jpaBook");
	saveBook.setAuthor("kim");
	em.persist(saveBook);
	
	OrderItem saveOrderItem = new OrderItem();
	saveOrderItem.setItem(book);
	em.persist(saveOrderItem);
	
	em.flush();
	em.clear();
	
	// 테스트 시작
	OrderItem orderItem = em.find(OrderItem.class, saveOrderItem.getId());
	Item item = orderItem.getItem();
	
	System.out.println("item = " + item.getClass());	// 프록시 조회
	
	Assert.assertFalse(item.getClass() == Book.class);
	Assert.assertFalse(item instanceof Book);		// false
	Assert.assertTrue(item instanceof Item);
	
```

### 그렇다면 상속관계에서 발생하는 프록시 문제를 어떻게 해결해야 할까?
1. JPQL로 대상 직접 조회
	- 하지만 이방법은 다형성을 활용할 수 없다.
```
Book jpqlBook = em.createQuery
	("select b from Book b where b.id=:bookId", Book.class)
	.setParameter("bookId", item.getId())
	.getSingleResult();
	
```

2. 프록시 벗기기
```
	...
	Item item = orderItem.getItem();
	Item unProxyItem = unProxy(item);
	
	if (unProxyItem instanceof Book) {
		System.out.println("proxyItem instanceod Book");
		Book book = (Book) unproxyItem;
		System.out.println("책 저자 = " + book.getAuthor());
	}
	
	Assert.assertTrue(item != unProxyItem);
}

//하이버네이트가 제공하는 프록시에서 원본 엔티티를 찾는 기능을 사용하는 메소드
public static <T> T unProxy(Object entity){
	if(entity instanceof HibernateProxy) {
		entity = ((HibernateProxy) entity)
							.getHibernateLazyInitializer()
							.getImplementation();
	}
	return (T) entity;
}
```

- 그런데 이 방법은 프록시에서 원본 엔티티를 직접 꺼내기 때문에 프록시와 원본 엔티티의 동일성 비교가 실패한다는 문제점이 있다.
```
item == unProxyItem // false
```

3. 기능을 위한 별도의 인터페이스 제공
```
public interface TitleView {
	String getTitle();
}

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item implements TitleView {
	@Id @GeneratedValue
	@Column(name = "ITEM_ID")
	private Long id;
	
	...
}

@Entity
@DiscriminatorValue("B")
public class Book extends Item() {
	private String author;
	private String isbn;
	
	@Ovverride
	public String getTitme() {
		return "[제목:" + getName() + "저자:" + 며쇅 + "]";
	}
}
```

```
@Entity
public class OrderItem {
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	private Item item;
	
	...
	
	public void printItem() {
		System.out.println("TITLE="+item.getTitle());
	}
}
```
```
OrderItem orderItem = em.find(OrderItem.class, saveOrderItem.getId());
orderItem.printItem();
```

### N+1
- N + 1문제란?
```
@Entity
public class Member {

	@Id @GeneratedValue
	private Long id;

	@OneToMany(mappedBy = "member", fetch = 즉시 or )
	private List<Order> orders = new ArrayList<Order>();

	...
}
```
```
@Entity
public class Order {
	@Id @GeneratedValue
	private Long id;

	@ManyToOne
	private Member member;
	...
}
```

```
SELECT * FROM MEMNER
SELECT * ORDERS WHERE MEMBER_ID =1 // 회원과 연관된 주문
SELECT * ORDERS WHERE MEMBER_ID =2 // 회원과 연관된 주문
SELECT * ORDERS WHERE MEMBER_ID =3 // 회원과 연관된 주문
SELECT * ORDERS WHERE MEMBER_ID =4 // 회원과 연관된 주문
SELECT * ORDERS WHERE MEMBER_ID =5 // 회원과 연관된 주문
SELECT * ORDERS WHERE MEMBER_ID =6 // 회원과 연관된 주문
```

### N + 1 해결책
1. 패치 조인을 사용하자
	- 페치 조인은 SQL 조인을 사용해서 연관된 엔티티를 함께 조회하므로 N+1 문제가 발생하지 않는다.
	```
	select m from Member m join fetch m.orders

	// 결과 SQL 로그
	SELECT M.*, O.* FROM MEMBER M
	INNER JOIN ORDERS O ON M.ID=O.MEMBER_ID
	```
2. 하이버네이트 @BatchSize
	- BatchSize 어노테이션을 사용하면 연관된 엔티티를 조회할 때 지정한 size만큼 SQL의 IN절을 사용해서 조회한다. 만약 조회한 회원이 10명인데 size=5로 지정하면 2번의 SQL만 추가로 실행한다.
	```
	@Entity
	public class Member {

	@Id @GeneratedValue
	private Long id;

	@BatchSize(size = 5)
	@OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
	private List<Order> orders = new ArrayList<Order>();

	...
	}
	```
	
	- hibernate.default+batch_fetch_size 속성을 사용하면 애플리케이션 전체에 기본으로 @BatchSize를 적용할 수 있다.


### 성능 최적화
- 엔티티가 영속성 컨텍스트에 관리되면 1차 캐시부터 변경 감지까지 얻을 수 있는 혜택이 많다. 하지만 영속성 컨텍스트는 변경 감지를 위해 스냅샷 인스턴스를 보관하므로 더 많은 메모리를 사용하는 단점이 있다. 이때는 읽기 전용으로 엔티티를 조회하면 메모리 사용량을 최적화 할 수 있다.
1. 스칼라 타입으로 조회
	- 스칼라 타입은 영속성 컨텍스트가 결과를 관리 하지 않는다.
	```
	select o.id, o.name, o.price from Order p(변경 후)
	```
2. 읽기 전용 쿼리 힌트 사용
	- 하이버네이트 전용 힌트인 org.hibernate.readOnly를 사용하면 엔티티를 읽기 전용(스냅샷 보관 안함)으로 조회 할 수 있다. 메모리 절약 측면에서 이득.
	```
	TypedQuery<Order> query = em.createQuery(“select o from Order o”, Order.class);
	query.setHint(“org.hibernate.readOnly”, true);
3. 읽기 전용 트랜잭션 사용
	- 트랜잭션에 readOnly=true 옵션을 주면 강제로 플러시를 호출하지 않는 한 플러시가 일어나지 않는다. 영속성 컨텍스트를 플러시 하지 않으니 엔티티의 등록, 수정, 삭제 할 때 일어나는 스냅샵 비교와 같은 무거운 로직들을 수행 하지 않으므로 성능이 향상 된다.
	```
	@Transactional(readOnly=true)
	```
4. 트랜잭션 밖에서 읽기
	- 트랜잭션 밖에서 읽는다는 것은 트랜잭션 없이 엔티티를 조회 한다는 뜻이다. 트랜잭션을 사용하지 않으면 플러시가 일어나지 않으므로 조회 성능이 향상된다.
	```
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	```

### 배치
- 수천에서 수만 건 이상의 엔티티를 한번에 등록 할 때 주의 할 점은 영속성 컨텍스트에 엔티티가 계속 쌓이지 않도록 일정 단위마다 영속성 컨텍스트의 엔티티를 데이터 베이스에 플러시 하고 영속성 컨텍스트를 초기화 해야 한다.

- 등록배치
```
Entitymanager em = entityManagerFactory.createEntityManager();
EntityTransaction tx = em.getTransaction();
tx.begin();

for(int i=0; i< 10000; i++){
    Product product = new Product(“item” + i, 10000);
    em.persist(product);

    if(i % 100 == 0){
      em.flush();
    em.clear();
}}
tx.commit();
em.close();
```

- 수정배치
1. 페이징 처리
```
Entitymanager em = entityManagerFactory.createEntityManager();
EntityTransaction tx = em.getTransaction();
tx.begin();
int pageSize = 100;
for(int i=0; i< 10; i++){
    List<Product> resultList = em.createQuery(“select p from Product p”, Product.class)
        .setFirstResult(i * pageSize)
        .setMaxResults(pageSize)
        .getResultList();

    for(Product product : resultList){
        product.setProduct(product.getPrice() + 100);
    }
    em.flush();
    em.clear();
}}
tx.commit();
em.close();
```

2. 하이버네이트 scroll 사용
- 하이버네이트는 scroll이라는 이름으로 JDBC 커서를 지원한다.
```
EntityTransaction tx = em.getTransaction();
Session session = em.unwrap(Session.class);

tx.begin();
ScrollableResults scroll = session.createQuery("select p from Product p")
																		.setCacheMode.IGNORE) // 2차 캐시 기능을 끈다
																		.scroll(ScrollMode.FORWARD_ONLY);

int count = 0;

while (scroll.next()) {
	Product p = (Product) scroll.get(0);
	p.setPrice(p.getPrice() + 100);

	count++;
	if(count % 100 ==0) {
		session.flush(); // 플러시
		session.clear(); // 영속성 컨텍스트 초기화
	}
}

tx.commit();
session.close();
```
- scroll은 하이버네이트 전용 기능이므로 먼저 em.unwrap() 메소드를 사용해서 하이버네이트 세션을 구한다. 다음으로 쿼리를 조회하면서 scroll() 메소드로 ScrollableResults 객체를 반환받는다. 이 객체의 next() 메소드를 호출하면 엔티티를 하나씩 조회할 수 있다.
