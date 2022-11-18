# 객체지향 쿼리언어

---

## JPA 는 다양한 쿼리 방법을 지원

- ***JPQL***
- Criteria
- ***QueryDSL***
- 네이티브 SQL
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

## 기존 find의 한계

- EntityManager.find()는 가장 단순한 조회 방법
- ***근데 만약 나이가 18살 이상인 회원을 다 검색하고 싶다면?***
- 모든 회원을 find로 가져와서 메모리에 올려놓고 검색하는 방법은 현실성이 없음. 데이터베이스에서 조건을 걸어서 조회해야함. 

## JPQL

- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리를 제공. 일반 SQL과 비슷하지만 ***엔티티를 대상***으로 한다는 점이 다르다.

```
  String sql = "select m from Member.class as m where m.username like '%kil%'";
```

- SQL을 추상화하기때문에 특정 데이터베이스 SQL에 의존하지 않는다.
- JPQL은 결국 SQL로 변환된다.
- 동적 쿼리 생성이 쉽지 않다.

## Criteria 

- 문자가 아닌 자바 코드로 JPQL을 작성할 수 있음.

```
//Criteria 사용 준비
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);

//루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);

//쿼리 생성 
CriteriaQuery<Member> cq =  query.select(m).where(cb.equal(m.get("username"), “kim”));
List<Member> resultList = em.createQuery(cq).getResultList()
```

- JPA 공식기능이지만 실무에서는 구현이 너무 복잡해서 사용 X

## queryDSL 

- 문자가 아닌 자바코드로 JPQL을 작성할 수 있다.

```
//JPQL
//select m from Member m where m.age > 18
JPAFactoryQuery queryFactory= new JPAQueryFactory(em); 
QMember m = QMember.member;
List<Member> list = queryFactory.selectFrom(m) 
			.where(m.age.gt(18))
			.orderBy(m.name.desc())
			.fetch()
```

- JPQL의 빌더 역할을 한다.
- 컴파일 시점에서 문법 오류를 찾을 수 있다.
- 동적쿼리 작성이 편리하고 단순하다.

## 네이티브 SQL 

- JPA가 제공하는 SQL을 직접 사용하는 기능
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능을 사용할 때 사용(ex, 오라클 CONNECT BY)

```
String sql ="SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’";
List<Member> resultList = em.createNativeQuery(sql, Member.class)
			.getResultList();
```

## JPQL 문법

- select m from Member as m where m.age  > 18   
  → 대소문자를 구분한다.
- 엔티티와 속성은 대소문자 구분을 한다.
- JPQL 키워드는 대소문자 구분을 하지 않는다(SELECT, FROM, where)
- 엔티티 이름 사용, 테이블 이름이 아님(Member)
- 별칭은 필수(alias) (as는 생략 가능)

## TypedQuery, Query

- TypedQuery : 반환 타입이 명확할 때 사용

``` 
TypedQuery<Member> query = em.createQuery("SELECT m FROM Meber m", Member.class);
```

- Query : 반환 타입이 명확하지 않을 때 사용

```
Query query = em.createQuery("SELECT m.username, m.age FROM Meber m");
```

## 결과 조회 API

- query.getResultList(): 결과가 하나 이상일 때 리스트 반환
  - 결과가 없으면 빈 리스트 반환
- query.getSingleResult(): 결과가 정확히 하나
  - 결과가 하나만 나오는 것 외의 모든 상황에서 에러가 나오기 때문에 사용에 주의가 필요.
  - 결과가 없으면: javax.persistence.NoResultException
  - 둘 이상이면: javax.persistence.NonUniqueResultException

## 프로젝션

- SELECT 절에 조회할 대상을 지정하는 것을 프로젝션이라 한다. 프로젝션 대상은 엔티티, 임베디드 타입, 스칼라 타입이 있다.

1. 엔티티 프로젝션
   - 이렇게 조회한 엔티티는 영속성 컨텍스트에서 관리된다.

```
SELECT m FROM Member m
SELECT m. team  FROM Member m
```

2. 임베디드 타입 프로젝션
   - 임베디드 타입은 조회의 시작점이 될 수 없다.
   - 임베디드타입은 엔티티 타입이 아닌 값 타입이다. 따라서 조회한 결과는 영속성 컨텍스트에서 관리 X

```
// 다음과 같이 조회의 시작점으로 사용 못함
String query = "SELECT a FROM Address a";

// 이렇게 써야함.
"SELECT o.address FROM Order o"
```

3. 여러 값 조회 프로젝션

- Query 타입으로 조회

```
Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
List result = query.getResultList();

Iterator iterator = resultList.iterator();
while(iterator.hasNext()) {
	Object[] row = (Object[]) iterator.next();
	...
}
```

- Object[] 타입으로 조회

```
List<Object[]> resultList = em.createQuery("SELECT m.username, m.age FROM Member m").getResultList();

for (Object[] row : resultList) {
	String userName = (String) row[0];
	Integer age = (Integer) row[1];
}
```

- new 명령어로 조회
  - 단순 값을 DTO로 바로 조회 ( SELECT new jpabook.jpql.UserDTO(m.username, m.age)from Memer m; )
  - 패키지 명을 포함한 전체 클래스 명을 적어줘야 한다.
  - 순서와 타입이 일치하는 생성자 필요

```
TypedQuery<UserDTO> query = em.createQUery("SELECT new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m", UserDTO.class);

List<UserDTO> resultList = query.getResultList();
```

## 페이징 처리

- 데이터베이스마다 페이징을 처리하는 SQL 문법이 다르다.
- 그래서 JPA는 페이징을 다음 두 API로 추상화했다.
  - setFirstResult(int startPosition): 조회 시작 위치(0부터 시작)
  - setMaxResults(int maxResult): 조회할 데이터 수

## 집합과 정렬

### 집합 함수

- COUNT 반환타입: LONG

- MAX, MIN
- AVG 반환타입: Double
- SUM 반환타입: 정수합: LONG 소수합: Double BigInteger합:BigInteger BigDecimal합: BigDecimal

- 참고사항 
  - NULL 값은 무시하므로 통계에 잡히지 않는다
  - 값이 없을 경우 NULL, COUNT 는 0
  - DISTINCT 를 함수 안에 사용해서 중복된 값을 제거 후 사용가능 ex) COUNT(DISTINCT m.age)
  - DISTINCT를 COUNT 에서 사용할 때 임베디드 타입은 지원하지 않는다

## JPQL 조인

JPQL 은 JOIN 명령어 다음에 조인할 객체의 연관 필드를 사용한다.

### 내부조인

```
SELECT m
from Member m [INNER] JOIN m.team t WHERE t.name = :teamName

SELECT m
from Member m [INNER] JOIN m.team on m.team_id = t.id t WHERE t.name = :teamName
```

### 외부조인

```
SELECT m FROM Member m. LEFT [OUTER] JOIN m.team t
```

### 세타조인 (관계없는 엔티티도 조인 가능하며 내부조인만 지원)

```
SELECT COUNT(m) FROM Member m, Team t WHERE m.username = t.name
```

### JOIN ON 절 (JPA 2.1 부터 지원)

내부조인의 ON 절은 WHERE 절을 사용할 때와 결과가 같으므로 ON 절은 보통 외부조인에서만 사용한다.

```
SELECT m,t FROM Member m LEFT JOIN m.team t on t.name = 'A'
```

### 페치 조인

JPQL 에서 성능 최적화를 위해 제공하는 기능으로 연관된 엔티티나 컬렉션을 한번에 같이 조회하는 기능이다.

- 일반 페치 조인

```
// JPQL
SELECT m FROM Member m JOIN FETCH m.team
// 실행된 SQL
SELCT m.*, t.* FROM Member m INNER JOIN team t ON m.team_id = t.id
```

- 컬렉션(일대다) 페치 조인

```
// JPQL
SELECT t FROM Team t JOIN FETCH t.members WHERE t.name = '팀A'
//실행된 SQL
SELECT t.*, m.* FROM Team t INNER JOIN Member m ON t.id = m.team_id WHERE t.name = '팀A'
```

<img src="https://user-images.githubusercontent.com/68458092/198832400-a7640dbf-97fb-449e-83c8-c02b57d5d739.jpeg" alt="DB2CB598-053B-44B1-82B4-B59A2B4314D9_1_201_a" style="width:900px;height:400px" />

Member 테이블과 조인하면서 결과가 증가해 팀A가 2건 조회되었다.

실제 출력 결과를 보면 Team 2개에 각 Team 객체에 Members 2개씩 들어있음

``` 
teamname = 팀A, team = Team@0x100
username = 회원1, member = Member@0x200
username = 회원2, member = Member@0x300
teamname = 팀A, team = Team@0x100
username = 회원1, member = Member@0x200
username = 회원2, member = Member@0x300
```

* 참고 일대다 조인은 결과가 증가할 수 있지만 일대일,다대일 조인은 결과가 증가하지 않는다.

### 페치 조인과 DISTINCT

JPQL 의 DISTINCT 명령어는 SQL 에 추가하는 것은 물론이고 애플리케이션에서 한번 더 중복을 제거한다.

```
SELECT DISTINCT t FROM Team t JOIN FETCH t.members WHERE t.name = '팀A'
```

이렇게 해도 각 로우데이터(member) 데이터가 다르기때문에 SQL 에서는 효과가 없지만 애플리케이션에서 중복을 한번 더 제거하게 되면서 

Team 객체는 1개만 나오게 된다

```
teamname = 팀A, team = Team@0x100
username = 회원1, member = Member@0x200
username = 회원2, member = Member@0x300
```

### 페치 조인과 일반 조인의 차이

JPQL 은 결과를 반환할 때 연관관계를 고려하지 않는다. SELECT 절에 지정한 엔티티만 조회할 뿐

- 일반 조인시
  - 컬렉션을 지연로딩시 프록시나 초기화하지 않은 컬렉션 래퍼를 반환
  - 즉시로딩시 컬렉션을 즉시로딩하기 위해 쿼리를 한번 더 실행한다
- 페치 조인시
  - 연관된 엔티티도 함께 조회한다

### 페치 조인의 특징과 한계

1. 페치조인을 사용하면 SQL 한번으로 연관된 엔티티들을 함께 조회할 수 있어서 SQL 호출횟수를 줄여 성능을 최적화할 수 있다.
2. 글로벌 로딩 전략보다 우선하기 때문에 지연로딩을 설정해도 페치조인을 적용해 함께 조회한다.
3. JPA 표준에서는 페치 조인 대상에는 별칭을 줄 수 없다.(하이버네이트는 지원하지만 잘못사용시 데이터 무결성이 깨질 수 있음)
4. 둘 이상의 컬렉션을 페치할 수 없다.(2개 이상의 일대다 관계 조인 불가능)
5. 컬렉션(일대다)을 페치 조인하면 페이징 API 사용할 수 없다.

### 경로 표현식
jpql에서 .을 사용해서 객채의  값에 접근할 수 있다. 이를 경로 표현식이라 한다.

- 상태 필드 : 단순히 값을 저장하기 위한 필드
ex) "select m.username from Member m"
- 연관 필드 : 연관관계를 위한 필드
     - 단일 값 연관 필드 : @ManyToOne, @OneToOne, 대상이 엔티티   
      ```"select m.team from Member m" ```
     - 컬렉션 값 연관 필드 : @OneToMany, @ManyToMany, 대상이 컬렉션   
      ```"select m.orders from Member m"```   
      (컬렉션 값은 더 이상 탐색이 불가능하지만 명시적 조인을 통해 별칭을 얻어 탐색 할 수 있다)   
      ```"select t.members,m.username from Team t join t.members m"```
      
- 주의사항
***연관 필드에서 묵시적 내부 조인이 발생***한다. 즉, select m.team from Member m은 명시적으로 join을 쓰지 않았지만 
```select from Member m inner join Team t on m.team_id = t.id```같이 내부적으로 inner join이 추가되어 db에서 조회된다.

***묵시적 조인은 조인이 일어나는 상황을 개발자가 파악하기 어렵기 때문에 지양하고 명시적 조인을 사용하자!***

### 서브 쿼리

```
나이가 평균보다 많은 회원

select m from Member m
where m.age > (select avg(m2.age) from Member m2)
```

일반 sql 문과 같다.
기억해야할 것은 where, having 절에서는 서브쿼리가 사용가능하지만 ***from 절의 서브쿼리는 현재 jpql에서 사용 불가능***하다.
이를 구현하려면 다른 방법으로 모색해야한다.

### 조건식
기본 CASE 식
```
select 
	case when m.age <= 10 then '학생요금'
	     when m.age >= 60 then '경로 요금'
	     else '일반요금'
	end
from Member m
```
단순 CASE 식
```
select 
	case t.name
	     when '팀A' then '인센티브110%'
	     when '팀B' then '인센티브120%'
	     else '인센티브105%'
	end
from Team t
```
- COALESCE : 하나씩 조회해서 null 이 아니면 반환
```
select coalesce(m.username, '이름 없는 회원') from Member m
```
- NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
```
select NULLIF(m.username, '관리자') from Member m
```
