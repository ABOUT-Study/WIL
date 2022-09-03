# 엔티티 매핑

## 1. @Entity
- 테이블과 매핑되는 클래스에 사용하는 어노테이션
- jpa가 엔티티 객체를 생성할때 클래스의 **기본생성자**를 사용한다.
- 저장할 필드에 final을 사용할 수 없다.

## 2. 데이터베이스 스키마 자동 생성
- create => 기존 테이블 삭제하고 새로 생성(DROP->CREATE)
- create-drop => create 전략 + drop
- update => 엔티티와 테이블을 비교해서 변경사항만 수정
- validate => 엔티티와 테이블을 비교해서 변경사항이 있으면 경고 후 애플리케이션을 실행하지 않음.

주의 : 개발단계에서는 create, create-drop, 테스트 서버에서는 update, 운영에서는 validate 를 쓰는 것이 일반적이다. 특히나 운영에서 validate 외의 것들을 안쓰도록 조심

## 3. 기본키(primary key) 매핑
- 직접 할당 : 기본키를 애플리케이션에서 직접 할당
- 자동 생성
  - @GeneratedValue 사용하고 원하는 생성전략을 사용해야한다.

---

    1) IDENTYFY 전략 : 기본키 생성을 데이터 베이스에 위임하는 전략. 해당 전략은 데이터를 DB에 insert 한 후에 기본키 값을 조회할 수 있다.
    
    주의 : 엔티티가 영속상태가 되려면 식별자가 반드시 필요하다. 하지만 IDENTYFY는 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있다.
    따라서 해당 전략은 em.persis() 를 호출하는 즉시 INSERT SQL이 DB에 전달된다. (이 전략은 JPA의 쓰기 지연이 동작하지 않는다)
    
    2) SEQUENCE 전략 : 유일한 값을 순서대로 생성하는 데이터베이스 오브젝트에서 식별자를 가져와서 DB에 insert하는 전략
    
    주의 : IDENTIFY와 SEQUENCE 전략의 차이
    IDENTIFY : 엔티티를 DB 저장 -> DB에서 식별자 생성 -> 엔티티에 식별자 할당 -> 영속성 컨텍스트 저장
    SEQUENCE : 시퀀스 오브젝트에서 식별자 생성 -> 엔티티에 식별자 할당 -> 영속성 컨텍스트 저장 -> 엔티티를 DB에 저장
  
    3) Table 전략 : 키 생성 전용 테이블을 직접 만들어서 사용하는 전략(시퀀스 오브젝트를 흉내내는 전략)
    
    4) Auto 전략 : 데이터베이스마다 기본키 만드는 방법이 다양하기때문에, 전략을 자동으로 선택되게 하는 전략
    
    
    
## 4. 필드와 컬럼 매핑
- @Column : 객체 필드를 테이블 컬럼에 매핑
  - @Column을 생략하면 필드의 자바 기본 타입(int,double)일때는 not null, 객체 타입(Integer)일때는 nullable 속성 자동 정의
  - 하지만 @Column을 사용할때는 디폴트 값이 nullable = true이므로, 명시적으로 nullable = false 라고 지정하는 것이 안전.

- @Enumerated : eunm 타입을 매핑할 때 사용
  - EnumType.ORDINAML(enum 순서) 와 EnumType.STRING(enum 이름) 중에 하나를 사용한다.
  - 하지만 EnumType.ORDINAML 는 유지보수 했을때 문제가 생길 수 있기때문에 EnumType.STRING 를 써야함.
