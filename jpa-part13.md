# 트랜잭션 범위의 영속성 컨텍스트

### 스프링 컨테이너의 기본 전략
- 트랜잭션의 범위와 영속성 컨텍스트의 생존 범위가 같다. ***즉, 트랜잭션을 시작할 때 영속성 컨텍스트를 생성하고 트랜잭션이 끝날 때 영속성 컨텍스트를 종료한다.***
![jpa13-1](https://user-images.githubusercontent.com/22884224/210138495-a1d430d9-5f62-431f-9001-485bb213b49e.png)
- @Transactional을 사용하여 트랜잭션을 시작하게 되는데, 단순 호출처럼 보이는 부분도 사실 스프링의 트랜잭션 AOP가 먼저 작동하게 된다.
![jpa13-2](https://user-images.githubusercontent.com/22884224/210138500-054a0fbc-a785-4c60-9bdb-54a163fa9aa3.png)

### 트랜잭션이 같으면 같은 영속성 컨텍스트를 사용한다.
- 엔티티 매니저를 사용하는 A,B 코드는 모두 같은 트랜잭션 범위에 있다. 따라서 엔티티 매니저는 달라도 같은 영속성 컨텍스트를 사용한다.
![jpa13-3](https://user-images.githubusercontent.com/22884224/210138611-a50316af-1bc8-416c-9b6f-09ca5dfd0ca8.png)

### 트랜잭션이 다르면 다른 영속성 컨텍스트를 사용한다.
- 여러 스레드에서 동시에 요청이 와서 같은 엔티티 매니저를 사용해도 트랜잭션에 따라 접근하는 영속성 컨텍스트가 다르다. 스프링 컨테이너는 스레드 마다 각각 다른 트랜잭션을 할당한다. 그러므로 멀티스레드 상황에서 안전하다.
![jpa13-4](https://user-images.githubusercontent.com/22884224/210138629-0a280c2b-d815-41ac-8228-2b9809ac110d.png)

# 준영속 상태와 지연로딩
- 조회한 엔티티가 서비스와 레포지토리 계층에서는 영속성 컨텍스트에 의해 관리되지만 컨트롤러나 뷰같은 프리젠테이션 계층에서는 준영속 상태가 된다. ***따라서 변경 감지와 지연 로딩이 동작하지 않는다.***

### 문제 발생(변경감지, 지연로딩)
- 변경 감지기능은 영속성 컨텍스트가 살아 있는 서비스 계층(트랜잭션 범위)까지만 동작하고 영속성 컨텍스트가 종료된 프리젠테이션 계층에서는 동작하지 않는다.
- 엔티티를 지연로딩으로 설정하고 프록시 객체로 조회한다고 가정하자. 아직 초기화하지 않은 프록시 객체를 사용하면 실제 데이터를 가지고 오기 위해 초기화를 시도한다. 하지만 준영속 상태에서는 영속성컨테스트가 없으므로 지연로딩을 할 수가 없다. 시도하면 예외가 발생한다.

### 해결방법
- 변경감지 -> 서비스 계층 안에서 비즈니스 로직을 수행한다.
- 지연로딩 -> 크게 2가지 방법으로 해결 가능
  1. 필요한 엔티티를 미리 로딩
  2. OSIV를 사용하여 엔티티를 항상 영속 상태로 유지

### 지연로딩 문제해결 방법(필요한 엔티티를 미리 로딩)
1. 글로벌 페치 전략 수정 (=즉시로딩)
```
@Entity
public class Order {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER)	// 즉시 로딩
    private Member member;
}
```
- 단점
  1. 사용하지 않는 엔티티를 로딩한다.
  2. N+1문제가 발생한다.

2. JPQL 페치 조인
    - JPQL 페치 조인은 호출하는 시점에 연관된 엔티티들을 모두 로딩해온다.***(가장 현실적인 방법)***

3. 강제로 초기화
    - 영속성 컨텍스트가 살아있을 때 프리젠테이션 계층이 필요한 엔티티를 강제로 초기화해서 반환하는 방법이다. (미리 초기화하면 준영속 상태에서도 엔티티를 사용가능하다.)
    - 하지만 서비스 계층에서 프록시를 초기화하는것은 서비스 계층의 역할에 맞지 않다. 그래서 FACADE라는 계층을 만들어서 초기화 역할을 대신 해줄 수 있다.
```
class OrderService {
    @Transactional
    public Order findOrder(id) {
        Order order = orderRepository.findOrder(id);
        order.getMember().getName(); // 프록시 객체를 강제로 초기화한다.
        return orer;
    }
}

```

4. FACADE 계층 추가
    - 프리젠테이션 계층과 서비스 계층 사이에 FACADE 계층을 하나 더 두는 방법이다. 이제부터 뷰를 위한 프록시 초기화는 이곳에서 담당한다. 덕분에 서비스 계층은 프리젠테이션 계층을 위해 프록시를 초기화 하지 않아도 된다. 결과적으로 FACADE 계층을 도입해서 서비스 계층과 프리젠테이션 계층 사이에 논리적인 의존성을 분리할 수 있다.
![jpa13-5](https://user-images.githubusercontent.com/22884224/210140237-0f3d2104-88c3-4252-974a-a45a0fe0f844.png)

### 지연로딩 문제해결 방법(OSIV를 사용하여 엔티티를 항상 영속 상태로 유지)
- 뷰를 개발할 때 필요한 엔티티를 미리 초기화하는 방법은 생각보다 오류가 발생할 가능성이 높다.
- 모든 문제는 엔티티가 프리젠테이션 계층에서 준영속 상태이기 때문에 발생한다. ***이를 해결하는 방법은 OSIV 이다.***
- OSIV는 영속성 컨텍스트를 뷰까지 열어든다는 뜻이다.

# OSIV
### 과거 OSIV: 요청당 트랜잭션
- osiv의 핵심은 뷰에서도 지연로딩이 가능하다.
- 가장 단순한 구현 방법은 클라이언트의 요청이 들어오자마자 서블릿 필터나 스프링 인터셉터에서 트랜잭션을 시작하고 요청이 끝날떄 트랜잭션도 종료하는 방법
  - ***요청 당 트랜잭션(Transaction per Request) 방식의 OSIV***
![jpa13-6](https://user-images.githubusercontent.com/22884224/211160005-708fcefb-e585-4b31-8dca-4a2b7b93c7db.png)
- 트랜잭션을 요청에 시작과 종료에 동작하므로 영속성 컨텍스트도 동일한 범위에서 유지
- 뷰에서도 지연 로딩을 할 수 있으므로 미리 초기화할 필요가 없어짐

### 요청 당 트랜잭션 방식의 OSIV 문제점
- 컨트롤러나 뷰 같은 프리젠테이션 계층이 엔티티를 변경할 수 있다는 점
- 예를 들어 뷰에 노출할 떄만 이름을 변경하고, 실제 DB에는 반영하고 싶지 않은 경우 문제 발생
```
class MemberController {
		
		public String viewmember(Long id) {
				Member member = memberService.getMember(id);
				member.setName("XXX"); //보안상 XXX로 변경
				model.addAttribute("member", member);
				...				
		}
}
```

### 요청 당 트랜잭션 방식의 문제점 해결 3가지
1. 읽기 전용 메소드만 제공하는 인터페이스를 프리젠테이션 계층에 제공
```
interface MemberView {
		public String getName();
}


@Entity
class Member implements MemberView {
		...
}


class MemberService {

		public MemberView getMember(id) {
				return memberRepository.findById(id);
		}
}
```

2. 엔티티래핑
- 엔티티의 읽기 전용 메소드만 가지고 있는 엔티티를 감싼 객체를 만들고 반환하는 방법
```
class MemberWrapper {
		private Member member;

		public MemberWrapper(Member member) {
				this.member = member;
		}

		//읽기 전용 메소드만 제공
		public String getName() {
				member.getName();
		}
}
```

3. DTO만 반환
- 가장 전통적인 방법이며 엔티티 대신에 단순히 데이터만 전달하는 객체인 DTO를 반환
```
class MemberDTO {
		private String name;

		//Getter, Setter
}

...
MemberDTO memberDTO = new MemberDTO();
memberDTO.setName(member.getName());
return memberDTO;
```

- 하지만 위 방법모두 코드량이 증가하는 단점이 있다. 이런 문제들 때문에 ***요청당 트랜잭션 방식의 OSIV는 최근에 거의 사용하지 않음.***
- 문제점을 보완해서 비즈니스계층에서만 트랜잭션을 유지하는 방식의 OSIV를 사용

# 스프링 OSIV
- 서블릿 필터에서 적용할지 스프링 인터셉터에서 적용할지에 따라 원하는 클래스를 선택 사용
  - 하이버네이트 OSIV 서블릿 필터:
    - org.springframework.orm.hibernate4.support.OpenSessionInViewFilter
  - 하이버네이트 OSIV 스프링 인터셉터:
    - org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor
  - JPA OEIV 서블릿 필터:
    - org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter
  - JPA OEIV 스프링 인터셉터
    - org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor

### 스프링 OSIV 분석
- 요청 당 트랜잭션 방식의 OSIV의 문제를 어느 정도 해결
- 스프링 프레임워크가 제공하는 OSIV는 ***"비즈니스 계층에서 트랜잭션을 사용하는 OSIV"***
![jpa13-7](https://user-images.githubusercontent.com/22884224/211160019-0b787aff-0582-4dbb-b307-3f82ba10bc08.png)
- 스프링 프레임워크가 제공하는 OSIV의 동작 원리
  - 클라이언트의 요청이 들어오면 서블릿 필터나 스프링 인터셉터에 영속성 컨텍스트를 생성 이때 트랜잭션은 시작하지 않음
  - 서비스 계층에서 @Transactional로 트랜잭션을 시작할 때 1번에 미리 생성해둔 영속성 컨텍스트를 찾아와 트랜잭션을 시작
  - 서비스 계층이 끝나면 트랜잭션을 커밋하고 영속성 컨텍스트를 플러시 이때 트랜잭션은 끝내지만 영속성 컨텍스트는 종료하지 않음
  - 컨트롤러와 뷰까지 영속성 컨텍스트가 유지되므로 조회한 엔티티 영속상태를 유지
  - 서블릿 필터나, 스프링 인터셉터로 요청이 돌아오면 영속성 컨텍스트를 종료 이때 ***플러시를 호출***하지 않고 바로 종료

### 트랜잭션 없이 읽기
- 영속성 컨텍스트를 통한 모든 변경은 트랜잭션 안에서 처리
  - ***트랜잭션 없이 엔티티변경 후 플러시하면 TransactionRequiredException 예외 발생***
- 엔티티를 변경하지 않고 단순 조회만 하는 경우 트랜잭션 없이 읽기가 가능 (지연 로딩도 조회)

### 스프링이 제공하는 OSIV 적용 예제
```
class MemberController {
		
		public String viewMember(Long id) {
				Member member = memberService.getMember(id);
				member.setName("XXX"); //보안상 XXX로 변경
				model.addAtrribute("member", member);
		}
}
```
- 스프링의 OSIV의 경우 2가지 이유로 영속성 컨텍스트를 플러시 처리 안함
  - 트랜잭션을 사용하는 서비스 계층이 끝날 떄 ***트랜잭션이 커밋되면서 이미 플러시 처리***, 스프링이 제공하는 ***OSIV 서블릿 필터나 인터셉터는 요청이 끝나면 플러시 호출하지 않고 em.close()로 영속성 컨텍스트를 종료***
  - 프리젠테이션 계층에서 em.flush()를 호출해서 강제 플러시해도 트랜잭션 범위 밖이므로 예외(TransactionRequiredException)가 발생

### 스프링 OSIV 주의사항
- 프리젠테이션 계층에서 엔티티를 수정한 직후 트랜잭션을 시작하는 서비스 호출시 문제 발생
```
class MemberController {
		public String viewMember(Long id) {
				Member member = memberService.getMember(id);
				member.setName("XXX");
				
				memberService.biz();
				return "view"
		}
}


class MemberService {
		@Transactional
		public void biz() {
				//... 비즈니스 로직 실행
		}
}
```
- 문제를 해결하기 위한 단순한 방법은 트랜잭션이 있는 비즈니스 로직 호출 후 엔티티 변경
```
memberService.biz(); //비즈니스 로직 먼저 실행

Member member = memberService.getMember(id);
member.setName("XXX"); //마지막에 엔티티를 수정

```
  - 스프링 OSIV는 같은 영속성 컨텍스트를 여러 트랜잭션이 공유할 수 있으므로 이런 문제 발생
  - OSIV를 사용하지 않는 영속성 컨텍스트 전략은 트랜잭션 생명주기와 같으므로 문제 발생 X
    
