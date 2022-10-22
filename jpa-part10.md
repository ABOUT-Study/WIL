# 객체지향 쿼리언어

### JPA 는 다양한 쿼리 방법을 지원
- ***JPQL***
- Criteria
- ***QueryDSL***
- 네이티브 SQL
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

### 기존 find의 한계
- EntityManager.find()는 가장 단순한 조회 방법
- ***근데 만약 나이가 18살 이상인 회원을 다 검색하고 싶다면?***
- 모든 회원을 find로 가져와서 메모리에 올려놓고 검색하는 방법은 현실성이 없음. 데이터베이스에서 조건을 걸어서 조회해야함. 

### JPQL
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리를 제공. 일반 SQL과 비슷하지만 ***엔티티를 대상***으로 한다는 점이 다르다.
```
  String sql = "select m from Member.class as m where m.username like '%kil%'";
```
- SQL을 추상화하기때문에 특정 데이터베이스 SQL에 의존하지 않는다.
- 동적 쿼리 생성이 쉽지 않다.

### Criteria 
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

### queryDSL 
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

