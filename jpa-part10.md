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
