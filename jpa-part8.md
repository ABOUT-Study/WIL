
- 이제 까지는 다음과 같이 find() 메소드로 db를 통한 실제 엔티티 객체를 조회했다.
```
em.find() : 데이터베이스를 통해서 실제 엔티티 객체 조회
```
- 근데 객체를 가지고 올때 연관된 객체들까지도 모두 가져올 수도 있다. 만약 연관된 객체를 사용하지 않는데도 모두 조회하면 낭비일 수 있다. 
- ***테이블을 조회해서 객체를 가져올 때 연관관계 객체를 안가져오고싶을때 어떻게 해야할까?***
 ---

## 프록시
```
em.getReference() : 데이터 베이스 조회를 미루는 프록시 엔티티 객체 조회
```

- getReference() 메서드를 사용하면 진짜 객체가 아닌 하이버 네이트 내부 로직으로 프록시 엔티티 객체를 반환한다. 
- 프록시 객체는 간단히 설명하면 객체 틀은 같지만 내용이 비어있는 개체다.


### 특징
- 실제 클래스를 상속받아서 만들어짐. 그래서 타입체크시 주의해야함.
```
m1.getClass() == m2.getClass() //false
m1 instanceof Member // true
m2 instanceof Member // true
```
- 실제 클래스와 겉 모양이 같다.
- 프록시 객체는 실제 객체의 참조를 보관한다.
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출(프록시 객체가 실제 엔티티로 바뀌는것이 아니다)
- 영속성 컨테스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
```
Member m1 = em.find(Member.class, member1.getId());
System.out.println("m1 = "+ m1.getClass());//Member

Member reference = em.getReference(Member.class, member1.getId());
System.out.println("reference = " reference.getClass()); //Member

m1 == reference //true
```
왜냐하면 이미 1차캐시에 올라와있는데, 프록시를 반환할 필요가 없다.

- 반대로 getReference()로 프록시 객체를 가지고 있으면 실제로 find()를 했을때도 프록시 객체를 반환한다.

### 프록시 객체의 초기화 과정
```
Member member = em.getRefernce(Member.class, "id1");//(1)
member.getName();//(2)- 
```
- (1) em.getReference()를 하면 프록시객체를 가져온다.
- (2) getName()을 하면 JPA가 영속성 컨텐스트에 초기화 요청을 한다.
- (3) 영속성 컨텍스트에서는 실제 db를 조회해서 가져온 다음 실제 Entity에 값을 넣어 생성한 다음, 프록시 객체와 실제 엔티티를 연결해서 실제 엔티티를 반환한다.
- 그 이후에는 이미 초기화 되어있는 프록시 객체이기에 해당 엔티티를 반환한다.
