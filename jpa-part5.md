# 연관관계 매핑 기초

## 연관관계 매핑을 이해하기 위한 핵심 키워드

- 방향 : 단방향, 양방향 (객체관계에서만 존재)
- 다중성 : 다대일(N:1),일대다(1:N), 일대일(1:1), 다대다(N:N)
- 연관관계의 주인 : 양방향 연관관계시 주인을 정해야함
- 객체 그래프 탐색 : 객체 연관관계를 사용한 조회
- 객체지향 쿼리 사용 : JPQL, QueryDsl

## 다대일(N:1) 연관관계

@ManyToOne 다중성을 나타내는 어노테이션 (필수)

속성 optional, fetch(글로벌 페치전략), cascade(영속성 전이기능), targetEntity(연관엔티티 타입 정보설정 잘안씀)

@JoinColumn 외래기 매핑시 사용 (생략가능)

속성 name(외래키이름 필드명_키컬럼명) 등등 @Column 속성과 비슷

## 단방향 연관관계

<img src="https://user-images.githubusercontent.com/68458092/196199211-feed4b76-b6f4-44b2-ba68-2553f531b713.jpeg" alt="F55DE72C-68F7-443B-9AFD-24B3674D0F48_1_201_a" style="width:900px;height:400px" />

객체 연관관계 (단방향)

Member 에서 team 을 접근하는 필드는 있지만 team 에서 member 를 접근하는 필드는 없다

테이블 연관관계 (양방향)

team_id 를 통해 양방향 Join 이 가능하다



## 양방향 연관관계

<img src="https://user-images.githubusercontent.com/68458092/196199884-e82f73c7-8871-44f8-be68-2ddf92e15ec0.jpeg" alt="AA7A2D61-1379-4D0A-A92C-C16AA7048EE0_1_201_a" style="width:900px;height:400px" />

@OneToMany(mappedBy = "team")

private List<Member> memberList;

team.getMemberList() 로 객체 그래프 탐색을 이용해 회원들 조회가 가능

mappedBy 는 양방향 연관관계에서 주인이 아닌곳에서 주인을 지정하기 위해 사용



### 객체에는 양방향 연관관계라는 것이 없다

[객체] 회원 -> 팀 (단방향), 팀 -> 회원(단방향)

[테이블] 회원 < - > 팀 (양방향) 외래키 하나로 Join 가능

그래서 두 연관관계중 주인을 정하고 주인만 외래키를 관리하고 등록,수정,삭제 할 수 있다 (주인이 아닌곳은 조회만 가능->CASCADE(영속성전이시 가능))
  
## 요약
- 단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색기능이 추가된다.
- 양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야한다.
- 따라서 우선 단방향 매핑을 사용하고 양방향이 필요시 추가하는게 좋다.
