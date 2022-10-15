# 9장 값 타입
1. 기본 값타입
- 자바 기본타입(int,double...)
- 래퍼클래스(Integer...)
- String
2. 임베디드 타입
3. 컬렉션 타입

## 임베디드 타입
새로운 값 타입을 직접 정의해서 사용가능
Member 엔티티에서 다음과 같이 사용 가능하다
```
class Member{
  @Id
  private Long id;
  @Embedded
  Periord workPeriod;
  @Embedded
  Adrress homeAdrress;
}
@Embeddable
class Period{
  private LocalDateTile startDate;
  private LocalDateTile endDate;
}
```
회원 엔티티가 더욱 의미있고 응집력 있게 변함
어노테이셔는 둘중 하나는 생략이 가능하고 기본생성자는 필수
* 하이버네이트는 임베디드타입을 컴포넌트라 한다.

## @AttributeOrverride 속성 재정의 (* 무조건 엔티티에 설정해야한다)
회원에게 회사 주소가 추가 된다면?
한 엔티티에 같은 임베디드 타입이 중복 될 경우 코드가 지저분해진다(중복해서 사용할 일은 많지 않다.)
```
@Embedded
Address homeAddress;
@AttributeOverrides({
  @AttributeOverride(name="city", column=@Column(name="~")),
  @AttributeOverride(name="city", column=@Column(name="~")),
})
```
* 임베디드 타입과 null
```
member.setAddress(null); // address 에 있는 컬럼 모두 null
```

## 값 타입과ㅏ 불변객체
값 타입 공유 참조 문제
```
member1.setAddress(new Address("old City"));
Address address = member1.getAddress();
address.setCity("newCity");
member2.setAddress(address);
```
회원 1,2 의 주소가 모두 변경됨
자바는 기본타입은 복사해서 전달하지만 객체에 값을 대입하면 참조값을 전달한다  
그래서 객체의 공유참조를 피할 수 없다.
따라서 불변객체를 사용해서 부작용을 원천 차단한다.
