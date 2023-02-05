# 데이터 접근 계층 개발의 문제
- 데이터 접근 계층(DAO)은 CRUD라고 부르는 유사항 ***등록, 수정, 삭제 조회 코드가 반복해서 개발***된다.

```
public class MemberRepository {
    public void save(Member member) {...}
    public Member findOne(Long id) {...}
    public List<Member> findAll() {...}
    public Member findByUsername(String username) {...}

}

public class TeamRepository {
    public void save(Team team) {...}
    public Team findOne(Long id) {...}
    public List<Team> findAll() {...}
    public Team findByTeam(String teamname) {...}
}
```

- 이러한 반복 문제를 해결하려면 제네릭과 상속을 적절히 사용해서 공통 부분을 처리하는 부모클래스인 GenericDAO를 만들어야하지만 이 방법은 공통 기능을 구현한 부모 클래스에 너무 종속되고 구현 클래스 상속이 가지는 단점이 노출된다.

# 스프링 데이터 JPA
- 스프링 프레임워크에서 JPA를 편리하게 사용할 수 있도록 ***스프링 데이터 JPA***를 지원한다.
- 스프링 데이터 JPA는 CRUD를 처리하기 위한 ***공통 인터페이스를 제공***하고, 사용자는 ***구현 클래스 없이 인터페이스만 작성해도 개발이 가능***하다.
(인터페이스를 작성하면 실행시점에 스프링 데이터 JPA가 구현객체를 동적으로 생성 및 주입해주기 떄문이다)

### JpaRepository
- 스프링 데이터 JPA는 CRUD 기능을 공통으로 처리하는 JpaRepository 인터페이스를 제공한다.
- JpaRepositry인터페이스는 다음과 같은 메소드들이 존재한다.
  - findAll()
  - findOne()
  - save()
  - delete()
- JpaRepository 인터페이스를 상속받으면 공통 인터페이스를 사용할 수 있다. 그리고 제네릭에 엔티티 클래스와 엔티티 클래스가 사용하는 식별자 타입을 지정하면 된다.
```
public interface MemberRepository extends JpaRepository<Member, Long> {

}
```

### 쿼리 메소드 기능
- 메소드 이름만으로 쿼리를 생성해주는 기능이 있어서 메소드만 선언하면 해당 메소드의 이름으로 적절한 JPQL 쿼리를 생성해서 실행한다.
- 쿼리 메소드 기능은 총 3가지다
  - 메소드 이름으로 쿼리생성
  - 메소드 이름으로 JPA NamedQuery 호출
  - @Query 어노테이션을 사용해서 리포지토리 인터페이스에 쿼리 직접 정의

1. 메소드 이름으로 쿼리생성
  - 만약 이메일과 이름으로 회원을 조회하려면 다음과 같은 메소드를 정의해준다. 그러면 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행한다.
  - 정해진 규칙에 따라 메소드 이름을 지어야한다. 
```
public interface MemberRepository extends Repository<Member, Long> {
    List<Member> findByEmailAndName(String email, String name);
    // select m from Member m where m.email =?1 and m.name = ?2
}
```

2. JPA NamedQuery
- 정의 : 쿼리에 이름을 부여해서 사용하는 방법으로 어노테이션이나 XML에 쿼리를 정의할 수 있음
```
// @NamedQuery 어노테이션으로 Named 쿼리 정의
@Entity
@NamedQuery(
    name="Member.findByUsername",
    query="select m from Member m where m.username = :username")
public class Member {
    ...
}
```

- JPA를 직접 사용해서 Named 쿼리 호출
```
public class MemberRepository {
    
    public List<Member> findByUsername(String username) {
        ...
        List<Member> resultList = 
            em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "회원1")
                .getResultList();
    }
}
```

- 스프링 데이터 JPA로 Named 쿼리 호출
```
public interface MemberRepository extedns JpaRepository<Member, Long> { // 여기 선언한 Member 도메인 클래스
    // 스프링 데이터 JPA는 선언한 '도메인 클래스 + . + 도메인 이름'으로 Named 쿼리를 찾아서 실행
    // 여기서는 Member.findByUsername이라는 Named 쿼리를 실행
    // 만약 실행할 Named 쿼리가 없으면 메소드 이름으로 쿼리 생성 전략을 사용
    List<Member> findByUsername(@Parma("username") String username); // 이름 기반 파라미터를 바인딩
}
```

3. @Query, 레포지토리 메소드에 쿼리 정의
- @Query 어노테이션을 사용해 실행할 메소드에 정적 쿼리를 직접 작성하므로 이름없는 Named 쿼리라고 할 수 있으며, JPA Named 쿼리처럼 실행 시점에 문법 오류를 발견할 수 있음
- 만약 네이티브 SQL을 사용하려면 @Query 어노테이션에 nativeQuery = true를 설정
```
// 메소드에 JPQL 쿼리 작성
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.username = ?1")
    Member findByUsername(String username);
{
```

```
// JPA 네이티브 SQL 지원
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "SELECT * FROM MEMBER WHERE USERNAME = ?0", nativeQuery = true)
    Member findByUsername(String username);
}
```
### 벌크성 수정쿼리
```
// 스프링 데이터 JPA를 사용한 벌크성 수정 쿼리
@Modifying
@Query("update Product p set p.price = p.price * 1.1 where p.stockAmount < :stockAmount")
int bulkPriceUp(@Param("stockAmount") String stockAmount);
```
- 벌크성 수정, 삭제 쿼리는 ***@Modifying 어노테이션***을 사용하면 된다.
    - 사용하지 않으면 queryExecutionRequestException 예외 발생
- 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트를 초기화하고 싶으면 clearAutomatically 옵션을 true로 설정하면 된다.
    - 이 옵션 없이 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다. 만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자.

- 참고 : ***벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.*** 권장하는 방법은 다음과 같다.
    1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
    2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.

### 반환타입
- 결과가 한건 이상이면 컬렉션 인터페이스를 사용하고, 단건이면 반환 타입을 지정한다.
- 조회 결과가 없으면 컬렉션은 빈 컬렉션을 반환하고, 단건은 null을 반환한다.
- 단건을 기대하고 반환 타입을 지정했는데 결과가 2건 이상이면 NonUniqueResultException 예외가 발생한다.

### 페이징과 정렬
- 파라미터에 Pageable를 사용하면 반환 타입으로 List나 Page를 사용할 수 있다.
- 반환타입으로 Page를 사용하면 전체 데이터 건수를 조회하는 count 쿼리를 추가로 호출한다.
```
// count 쿼리 사용
Page<Member> findByName(String name, Pageable pageable);
// count 쿼리 사용 안함
List<Member> findByName(String name, Pageable pageable);

List<Member> findByName(String name, Sort sort);
```
```
// Page 사용 예제 실행 코드
// 페이징 조건과 정렬 조건 설정
PageRequery pageRequest = new PageRequest(0, 10, new Sort(Direction.DESC, "name"));
Page<Member> result = memberRepositry.findByStartingWith("김", pageRequery);

List<Member> members = result.getContent();	// 조회된 데이터
int totalPages = result.getTotalPages();	// 전체 페이지 수
boolean hasNextPage = result.hasNextPage();	// 다음 페이지 존재 여부
```
- Pageable은 인터페이스여서 실제 사용할 때는 인터페이스를 구현한 PageRequest 객체를 사용한다.
- PageRequest 생성자는 현재페이지, 조회할 데이터 수, 정렬 추가정보를 파라미터로 넣을 수 있다.
- 페이지는 0부터 시작한다.

### 힌트
- JPA 쿼리 힌트는 SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트다. 기본적으로 JPA가 변경감지에 의해 update 쿼리를 날리려면 원본을 캐시에 저장하고 관리하는 entity 객체 또한 따로 갖고 있는다. 이를 위해 영속성 컨텍스트는 항상 원본과 관리하는 사본 객체 두 객체를 메모리에 저장하게 된다. 하지만 이게 성능 이슈가 될 수도 있다. 읽기만 할때는 엔티티 객체 두개를 갖는것은 메모리 비용의 낭비가 될 수 있기 때문이다. 그래서 JPA Hint를 통해 읽기 전용이라고 알려주고 캐시에 사본 객체(스냅샷)를 저장하지 않도록 공간 비용을 최적화 할 수 있다.
```
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
Member findReadOnlyByUsername(String username);
```
```
@QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly", value = "true")},
            forCounting = true)
Page<Member> findByUsername(String name, Pagable pageable);
```
- forCounting 속성은 반환타입으로 Page 인터페이스를 적용하면 추가로 호출하는 페이징을 위한 count 쿼리도 쿼리 힌트 적용(기본값 true)
- 하지만 실제로 readOnly로 설정하고 성능 최적화를 해도 개선치는 미비하다. 성능 테스트를 해보고 확실하게 성능 개선이 된다면 사용하자!
- 대부분은 Redis를 이용한 캐싱을 이용해서 성능 최적화를 함. Redis를 쓰지 않는 선에서 성능 최적화를 위해선 쓸 수 있지만, 필수라고 보기엔 적절하지 않다.

## 명세
- 도메인 주도 설계라는 책에서 명세라는 개념을 소개하는데, 스프링 데이터 JPA 는 JPA Criteria 로 이 개념을 사용할 수 있도록 지원한다.
- [DDD] Specification 객체가 특정 조건을 만족하는지 확인하는 역할을 한다.
- 명세(Specification)를 이해하기 위한 핵심 단어는 술어(predicate) 인데 이것은 단순히 참or거짓으로 평가된다.
- 그리고 이것은 AND,OR 같은 연산자로 조합할 수 있다. 예를 들어 데이터를 검색하기 위한 제약 조건 하나하나를 술어라 할 수있다.
- 이 술어를 스프링 데이터 JPA 는 Specification 클래스로 정의했다.

사용하려면 JpaSpecificationExecutor 인터페이스를 상속받으면 된다.

```Java
public interface OrderRepository extends JpaRepository<Order, Long>, 
JpaSpecificationExecutor<Order> {

}
```
```Java
public interface JpaSpecificationExecutor<T> {
    T findOne (Specification<T> spec);
    List<T> findAll(Specification<T> spec);
    Page<T> findAll(Specification<T> spec, Pageable pageable);
    List<T> findAll(Specification<T> spec, Sort sort);
    long count(Specification<T> spec);
}
```
```Java
public List<Order> findOrders(String name) {
    List<Order> result = orderRepository.findAll(
        where(memberName(name)).and(isOrderStatus())
    );

    return result;
}
```
```Java
public class OrderSpec {
    public static Specification<Order> memberName(final String memberName) {
        return new Specification<Order>() {
            public Predicate toPredicate(Root<Order> root,
                CriteriaQuery<?> query, CriteriaBuilder builder) {

                if(StringUtils.isEmpty(memberName)) return null;

                Join<Order, Member> m = root.join("member",
                     JoinType.INNER); //회원과 조인
                return builder.equal(m.get("name"), memberName)
            }
        }
    }

    public static Specification<Order> isOrderStatus() {
        return new Specification<Order>() {
            public Predicate toPredicate(Root<Order> root,
                CriteriaQuery<?> query, CriteriaBuilder builder) {

                return builder.equal(root.get("status"), OrderStatus.ORDER);
            }
        }
    }
}
```
## 사용자 정의 리포지토리 구현
- 여러가지 이유로 메소드를 직접 구현해야 할 때가 있는데 레포지토리를 직접 구현하면 공통 인터페이스까지 모두 구현해야 하기 때문에 필요한 메서드만 구현할 수 있도록 하는 방법을 제공한다.
```Java
// 인터페이스 이름은 자유
public interface MemberRepositoryCustom {
    public List<Member> findMemberCustom();
}
// 클래스명은 리포지토리 인터페이스 이름 + Impl 로 지어야함 (다르게 하고 싶을 경우 repositoryImplementationPostfix 설정을 변경해줘야함)
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    @Override
    public List<Member> findMemberCustom() {
        ...//구현
    }
}
// 마지막으로 리포지토리 인터페이스에서 커스텀 인터페이스 상속
public interface MemberRepository entends JpaRepository<Member, Long>, MemberRepositoryCustom {
    ...
}
```

## Web 확장
- 식별자로 도메인클래스를 바로 바인딩해주는 컨버터 기능, 페이징과 정렬 기능
설정 JavaConfig 에 @EnableSpringDataWebSupport 추가시 HandlerMethodArgumentResolver 가 스프링 빈으로 등록된다.

### 도메인 클래스 컨버터 기능
```Java
@GetMapping()
public String memberUpdateForm(@RequestParam("id") Member member) {
    return member.getName();
}
```

### 페이징과 정렬 기능
- 기본값은 page = 0, size = 20 변경시 @PageableDefault 사용
- 페이징 정보가 둘 이상일 경우 @Qualifier("member"), @Qualifier("order") 어노테이션 사용
```Java
@GetMapping()
public Page<Member> getMembers(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<Member> members = memberRepo.findAll(pageable);
    return members;
}
```

## 스프링 데이터 JPA 가 사용하는 구현체
- 스프링 데이터 JPA 가 제공하는 공통인터페이스는 SimpleJpaRepository 클래스가 구현한다.

```Java
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    @Transactional
    public <S extends T> S save(S entity) {
        if (entityInfomation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }
}
```
- @Transactional JPA의 모든 변경은 트랜잭션 안에서 이루어져야 하기때문에 (등록,수정,삭제)하는 메소드에는 트랜잭션이 적용되어 있고 서비스 계층에서 적용시 해당트랜잭션 전파
- @Transactional(readOnly = true) 조회하는 메소드에는 readOnly 옵션이 적용되 플러시를 생략해 약간의 성능향상을 얻을 수 있다.
- save() 새로운 엔티티면 저장하고 이미 있는 엔티티면 병합한다.
