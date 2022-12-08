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
- 스프링 데이터 JPA는 CRUD를 처리하기 위한 공통 인터페이스를 제공하고, 사용자는 구현 클래스 없이 인터페이스만 작성해도 개발이 가능하다.
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
- 쿼리에 이름을 부여해서 사용하는 방법으로 어노테이션이나 XML에 쿼리를 정의할 수 있음
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

