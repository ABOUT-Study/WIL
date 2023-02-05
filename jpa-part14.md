# 컬렉션과 부가 기능

## JPA 가 지원하는 컬렉션의 종류와 중요한 부가 기능들을 알아보자.

- 컬렉션 : 다양한 컬렉션과 특징을 설명
- 컨버터 : 엔티티의 데이터를 변환해서 데이터베이스에 저장
- 리스너 : 엔티티에서 발생한 이벤트를 처리
- 엔티티 그래프 : 엔티티를 조회할 때 연관된 엔티티들을 선택해서 함께 조회

## 컬렉션
Collection, List, Set, Map
*구현체에 따라 제공하는 기능이 조금씩 다를 수 있다.(하이버네이트 기준으로 설명됨)

```Java
//org.hibernate.collection.internal.PersistentBag 중복O, 순서X
@OneToMany
Collection<Member> collection = new ArrayList<Member>();

//org.hibernate.collection.internal.PersistentBag 중복O, 순서X
@OneToMany
List<Member> collection = new ArrayList<Member>();

//org.hibernate.collection.internal.PersistentSet 중복X, 순서X
@OneToMany
Set<Member> collection = new HashSet<Member>();

//org.hibernate.collection.internal.PersistentList 중복O, 순서O
@OneToMany @OrderColumn(name = "POSITION")
List<Member> collection = new ArrayList<Member>();
```

### List + @OrderColumn (실무에서 잘 사용하지 않는다)
순서가 있는 특수한 컬렉션으로 인식 (데이터베이스에 순서값을 저장해서 조회할 때 사용한다는 의미)
![jpa14-1](https://user-images.githubusercontent.com/68458092/210575594-c67c3455-3da4-47be-b44c-abc7871f9eda.png)

#### @OrderColumn 의 단점
- Board 엔티티에서 매핑하므로 Comment 에서는 POSITION 값을 알 수가 없어서 Comment insert 시 값이 저장되지 않고 Board.comments 의 값을 사용해 UPDATE 가 추가된다.
- List 변경시 많은 UPDATE 발생 (댓글2 삭제시 댓글3,4 POSITION을 1씩 줄이는 UDPATE 발생)
- 중간에 POSITION 값이 없으면 NullPointException 발생 (댓글2를 강제로 삭제할경우 POSITION 이 [0,2,3] 이 되어서 중간 1값이 null)

### @OrderBy (모든 컬렉션에 사용가능)
@OrderColumn 이 데이터베이스에 순서용 컬럼을 매핑해서 관리했다면 @OrderBy는 데이터베이의 ORDER BY 절을 사용해서 정렬한다.

```Java
@OneToMany(mappedBy = "team")
@OrderBy("username desc, id asc")
private Set<Member> members = new HashSet<Member>();
```
- @OrderBy 는 JPQL의 order by 절 처럼 엔티티의 필드를 대상으로 한다.
*하이버네이트는 Set 에 @OrderBy 를 적용해서 결과를 조회하면 순서를 유지하기 위해 HashSet 대신 LinkedHashSet을 내부에서 사용한다.

## 컨버터
@Converter 를 사용하면 엔티티의 데이터를 변환해서 데이터베이스에 저장할 수 있다.

```Java
// Entity Class
@Id
private Long id;
@Convert(converter = BooleanToYNConverter.class)
private boolean vip;

// Converter Class
@Converter
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

  @Override
  public String convertToDatabaseColumn(Boolean attribute) {
    return (attribute != null && attribute) ? "Y" : "N";
  }
  
  @Override
  public Boolean convertToEntityColumn(String dbData) {
    return "Y".equals(dbData);
  }

}

public interface AttributeConverterK<X, Y> {
  public Y convertToDatabaseColumn(X attribute); // 엔티티의 데이터를 데이터베이스 컬럼에 저장할 데이터로 변환
  public X convertToEntityColumn(Y dbData); // 조회한 컬럼 데이터를 엔티티의 데이터로 변환
}
```

### 글로벌 설정
컨버터 클래스에 @Converter(autoApply = true) 옵션 적용시 @Convert를 지정하지 않아도 모든 boolean 타입에 대해 자동으로 컨버터가 적용

## 리스너
모든 엔티티를 대상으로 생명주기에 따른 이벤트를 처리할 수 있다.

![jpa14-2](https://user-images.githubusercontent.com/68458092/210575908-6fc49591-718f-44e3-a78e-7321e6e1a7da.png)
- PostLoad : 엔티티가 영속성 컨텍스트에 조회된 직후, 또는 refresh를 호출한 후(2차 캐시에 저장되어 있어도 호출된다.)
- PrePersist : persist() 메소드를 호출해서 엔티티를 영속성 컨텍스트에 관리하기 직전에 호출된다. 식별자 생성 전략을 사용한 경우에는 엔티티의 식별자는 존재하지 않는 상태이다. 새로운 인스턴스를 merge 할 때도 수행된다.
- PreUpdate : flush나 commit을 호출해서 엔티티를 데이터베이스에 수정하기 직전에 호출된다.
- PreRemove : remove 메소드를 호출해서 엔티티를 영속성 컨텍스트에서 삭제하기 직전에 호출된다. 또한 삭제 명령어로 영속성 전이가 일어날 때도 호출된다. orphanRemoval 에 대해서는 flush나 commit 시에 호출된다.
- PostPrsist : flush나 commit을 호출해서 엔티티를 데이터베이스에 저장한 직후에 호출된다. 식별자가 항상 존재한다. 참고로 식별자 생성 전략이 IDENTITY인 경우 식별자를 생성하기 위해 persist()를 호출하면서 데이터베이스에 해당 엔티티를 저장하므로, 이때는 persist()를 호출한 직후에 바로 PostPersist가 호출된다.
- PostUpdate : flush나 commit을 호출해서 엔티티를 데이터베이스에 수정한 직후에 호출된다.(persist 시에는 호출되지 않는다)
- PostRemove : flush나 commit을 호출해서 엔티티를 데이터베이스에 삭제한 직후에 호출된다.

### 이벤트 적용 위치
- 엔티티에 직접 적용
- 별도의 리스너 등록
- 기본 리스너 사용

### 엔티티에 직접 적용
```Java
//엔티티 클래스
@Id
private Long id;
@PrePersist
public void PrePersist(){
    System.out.println("PrePersist id = " + id);
}
```

### 별도의 리스너 등록
```Java
// 엔티티 클래스에 @EntityListeners 추가
@Entity
@EntityListeners(DuckListener.class)
...

public class DuckListener {
  
  @PrePersist
  //특정 타입이 확실하면 특정 타입을 받을 수 있다.
  //파라미터로 대상 엔티티를 받을 수 있고 리턴타입은 void로 설정해야한다.
  private void prePersist(Object obj) {
    System.out.println("DuckListener.PrePersist obj = [" + obj + "]");
  }
}
```

### 기본 리스너 사용
아래 xml 형태로 META-INF/orm.xml에 등록이 가능하다. [기본리스너설정링크](https://sterl.org/2017/08/jpa-default-entity-listener)

### 여러 리스너 등록시 호출 순서
1. 기본 리스너
2. 부모 클래스 리스너
3. 리스너
4. 엔티티

### 더 세밀한 설정
```Java
@Entity
@EntityListeners(ProductListener.class)
@ExcludeDefaultListeners
@ExcludeSuperclassListeners
public class Duck extends BaseEntity {
  ...
}
```
- @ExcludeDefaultListeners : 기본 리스너 무시
- @ExcludeSuperclassListeners : 상위 클래스 이벤트 리스너 무시

## 엔티티 그래프
#### 엔티티를 조회할 때 연관된 엔티티를 함께 조회하려면 FetchType.EAGER 로 설정하거나 페치 조인을 사용하면 되지만  
항상 함께 조회하기 때문에 필요없을 때에도 조회하는 단점이 있다. 그래서 FetchType.LAZY 로 사용하고 연관된 엔티티를 함께  
조회할 필요가 있으면 JPQL 페치 조인을 사용한다. 하지만 이럴 경우 중복되는 JPQL 작성이 많아진다.
- 주문 조회
- 주문과 주문한 유저 조회
- 주문과 주문 아이템 조회
모두 주문을 조회하지만 함께조회할 엔티티가 바뀌면서 주문조회 JPQL 이 반복된다.
이럴때 JPA2.1 에 추가된 엔티티 그래프 기능을 사용하면 엔티티를 조회하는 시점에 함께 조회할 연관된 엔티티를 선택할 수 있다.

### Named 엔티티 그래프
```Java
@NamedEntityGraph(name = "Order.withMember", attributeNodes = {
    @NamedAttributeNode("member")
})
@Entity
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
```
- name : 엔티티 그래프의 이름을 정의한다.
- attributeNodes : 함께 조회할 속성을 선택한다. 이때 @NamedAttributeNode 를 사용하고 함께 조회할 속성을 선택한다.
*둘 이상 정의시 @NamedEntityGraphs 사용

### em.find()에서 엔티티 그래프 사용
```Java
EntityGraph graph = em.getEntityGraph("Order.withMember");

Map hints = new HashMap();
hints.put("javax.persistence.fetchgraph", graph);

Order order = em.find(Order.class, orderId, hints);
```
- 엔티티 그래프는 JPA의 힌트 기능을 사용하므로 힌트의 키와 값을 위와 같이 넣어 주어야 한다.

### subgraph
```Java
@NamedEntityGraph(name = "Order.withAll", attributeNodes = {
    @NamedAttributeNode("member"),
    @NamedAttributeNode(value = "orderItems", subgraph = "orderItems")
    },
    subgraphs = @NamedSubgraph(name = "orderItems", attributeNodes = {
        @NamedAttributeNode("item")
    })
)
@Entity
public class Order {
    ...
}
```
- Order → OrderItem → Item과 같이 연달아서 엔티티 그래프를 조회할 경우 사용한다.
### JPQL 에서 엔티티 그래프 사용
```Java
List<Order> resultList =
    em.createQuery("select o from Order o where o.id = :orderId",
        Order.class)
        .setParameter("orderId", orderId)
        .setHint("javax.persistence.fetchgraph", em.getEntityGraph("Order.withAll"))
        .getResultList();
```
- em.find() 와 동일하게 힌트만 추가하면 된다.

### 동적 엔티티 그래프
```Java
EntityGraph<Order> graph = em.createEntityGraph(Order.class);
graph.addAttributeNodes("member");
Subgraph<OrderItem> orderItems = graph.addSubgraph("orderItems");
orderItems.addAttributeNodes("item");

Map hints = new HashMap();
hints.put("javax.persistence.fetchgraph", graph);

Order order = em.find(Order.class, orderId, hints);
```
- 엔티티 그래프를 동적으로 구성하려면 createEntityGraph() 메소드를 사용하면 된다.

### 엔티티 그래프 정리
#### 이미 로딩된 엔티티
- 영속성 컨텍스트에 해당 엔티티가 이미 로딩되어 있으면 엔티티 그래프가 적용되지 않는다.(아직 초기화되지 않은 프록시에는 엔티티 그래프가 적용된다.)
```Java
Order order = em.find(Order.class, orderId); // 이미 조회
hints.put("javax.persistence.fetchgraph", em.getEntityGraph("Order.withMember"));

Order order2 = em.find(Order.class, orderId, hints);
```
#### fetchgraph, loadgraph의 차이
- fetchgraph는 엔티티 그래프에 선택한 속성만 함께 조회한다. 반면에 loadgraph 속성은 엔티티 그래프에  
선택한 속성뿐만 아니라 글로벌 fetch 모드가 FetchType.EAGER로 설정된 연관관계도 포함해서 함께 조회한다.
