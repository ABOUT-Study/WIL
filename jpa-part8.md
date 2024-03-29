
- 이제 까지는 다음과 같이 find() 메소드로 db를 통한 실제 엔티티 객체를 조회했다.
```
em.find() : 데이터베이스를 통해서 실제 엔티티 객체 조회
```
- 근데 객체를 가지고 올때 연관된 객체들까지도 모두 가져올 수도 있다. 만약 연관된 객체를 사용하지 않는데도 모두 조회하면 낭비일 수 있다. 
- ***테이블을 조회해서 객체를 가져올 때 연관관계 객체를 안가져오고싶을때 어떻게 해야할까?***
 ---

# 프록시
```
em.getReference() : 데이터 베이스 조회를 미루는 프록시 엔티티 객체 조회
```

- getReference() 메서드를 사용하면 진짜 객체가 아닌 하이버 네이트 내부 로직으로 프록시 엔티티 객체를 반환한다. 
- 프록시 객체는 간단히 설명하면 객체 틀은 같지만 내용이 비어있는 개체다.

<img width="716" alt="1" src="https://user-images.githubusercontent.com/22884224/194730823-24ec320b-f7ec-4939-9ee8-b30c9fca29ef.png">

### 특징
- 실제 클래스를 상속받아서 만들어짐. 그래서 타입체크시 주의해야함.
```
m1.getClass() == m2.getClass() //false
m1 instanceof Member // true
m2 instanceof Member // true
```

<img width="767" alt="2" src="https://user-images.githubusercontent.com/22884224/194730828-89d54b63-38e9-4255-b7f6-10e860844d99.png">

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

![3](https://user-images.githubusercontent.com/22884224/194730830-89b3294c-6db1-406a-bfea-a54224fc1d70.png)

---

# 즉시로딩과 지연로딩
- 다시 돌아와서 Member 엔티티를 조회할때 연관된 Team은 조회하지않고, member 정보만 조회하는 방법은 프록시를 활용하면 된다.
### 지연로딩
- 지연로딩 fetch = FetchType.LAZY를 사용해서 프록시로 조회
```
/*Member*/
@Entity
public class Member{
	
	@ManyToOne(fetch = FetchType.LAZY) //지연로딩 사용
	@JoinColumn(name="TEAM_ID")
	private Team team;
	
}
```

```
Member m = em.find(Member.class, member1.getId()); //Member 객체 반환
System.out.println("m = "+ m.getTeam().getClass()); //Team$HibernateProxy객체 반환
m.getTeam().getName() // team을 실제로 사용하는 시점에서 db조회 엔티티 반환
```
- ***연관관계에 있는 다른 엔티티를 사용하는 빈도수가 낮을 경우 지연로딩을 사용해 불필요한 엔티티 조회를 막을 수 있다.***

### 즉시로딩
- ***Member와 Team을 같이 쓰는 빈도가 높을 경우에는 어떻게 해야 할까?***
- 즉시로딩 fetch = FetchType.EAGER를 사용해서 함께 조회
```
/*Member*/
@Entity
public class Member{
	
	@ManyToOne(fetch = FetchType.EAGER) //즉시로딩 사용
	@JoinColumn(name="TEAM_ID")
	private Team team;
	
}
```
```
Member m = em.find(Member.class, member1.getId()); //Member 객체 반환
System.out.println("m = "+ m.getTeam().getClass()); //Team 객체 반환

```
- ***Member를 가져오는 시점에서 연관관계에 있는 Team까지 바로 가져오는 것을 즉시 로딩이라 한다.***

### 즉시로딩 주의사항
- 즉시로딩을 적용하면 예상하지 못한 SQL이 발생한다.(***ex 하나의 엔티티에 연관된 엔티티가 다수라면 find() 한 번 수행시 수십 수백개의 테이블을 조인해서 한번에 가져온다***)
- 즉시로딩은 JPQL에서 N+1 문제를 일으킨다.
```
/*Member*/
@Entity
public class Member{
	...
	@ManyToOne(fetch = FetchType.EAGER) //즉시로딩 사용
	@JoinColumn(name="TEAM_ID")
	private Team team;
	...
}
...
List<Member> members = em.createQuery("select m from Member m", Member.class)
				.getResultList();
//SQL: select * from Member
//SQL: select * from Team where TEAM_ID = xxx
...
```
- 위 JPQL을 그대로 쿼리로 번역하게 되면 Member를 가져오기 위한 쿼리 수행 이후 바로 Member 내부의 Team을 가져오기 위한 쿼리를 다시 수행하게 된다 → N+1(1개의 쿼리를 날리면 +N개의 쿼리가 추가수행된다)

### N+1의 해결책
1. 전부 지연로딩으로 설정한다.
2. 가져와야하는 엔티티에 한해서 fetch join을 사용해서 가져온다.
```
List<Member> members = em.createQuery("select m from Member m fetch join m.team", Member.class)
				.getResultList();
```
이렇게 JPQL을 실행하면 fetch join을 통해 Team 도 가져왔기 때문에 문제가 없다.

### 설정
- @ManyToOne, @OneToOne은 기본이 즉시 로딩으로 되어 있다.→ 직접 전부 LAZY로 설정
- @OneToMany, @ManyToMany는 기본이 지연 로딩

# 영속성 전이(CASCAD)와 고아객체

### 영속성 전이
- 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때 사용.
- ex: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장.

```
Child child1 = new Child();
Child child2 = new Child();
Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);
em.persist(child1);
em.persist(child2);// persist를 3번이나 해야한다.
```

- 영속성 전이(CASCADE)를 이용한 엔티티 저장 방법
```
@Entity
public class Parent{
	...
	@OneToMany(mappedBy = "parent", cascade=CascadeType.ALL)//영속성 전이 속성(CASCADE)사용
	private List<Child> childList = new ArrayList<>();

	public void addChild(Child child){
		childList.add(child);
		child.setParent(this);
	}
	...
}
```

```
Child child1 = new Child();
Child child2 = new Child();
Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);// parent만 persist 해주니 child도 같이 persist된다.
```
- parent만 persist해주니 그에 관련된 childList들도 같이 persist가 된다.

### cascade 주의사항 2가지
- 만약 또다른 엔티티가 child를 소유하고 있다면 cascade 사용을 지양해야한다. 너무 복잡해질수 있다. ***즉, 어떤 엔티티(child)의 소유자(parent)가 1개 일때만 사용하는 것이 좋다.***
- 라이프 싸이클이 똑같을때. ***parent와 child가 생성되고 삭제되는 싸이클이 비슷한경우에 사용하는 것이 좋다***

### 고아 객체
- 부모 엔티티와 연결관계가 끊어진 자식 엔티티를 자동 삭제하는 기능을 제공
- 삭제한 엔티티가 다른곳에서 참조하면 문제가 발생할 수 있어서 @OneToMany, @OneToOne 에서만 사용가능
- orphanRemoval = true 옵션 사용

```
Parent parent = em.find(Parent.class, id);
parent.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
parent.getChildren().clear(); // 모든 자식 엔티티 제거
```

### 참고사항
- CascadeType.REMOVE 설정과 같음

## 영속성 전이 + 고아객체 (생명주기 관리)
CascadeType.ALL + orphanRemoval = true
두 옵션을 모두 활성화시 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있다
- 자식을 저장하려면 부모에 등록만 하면 된다 (Cascade)
- 자식을 삭제하려면 부모에서 제거하면 된다 (orphanRemoval)
