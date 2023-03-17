# 2장

## 스프링 시큐리티 인 액션 구성요소

### 기본 구성에 속한 구성요소와 목적

- 스프링 시큐리티의 종속성은 web, security가 필요하다.
    - spring-boot-stater-web
    - spring-boot-stater-security


### 스프링이 자동으로 구성해주는 빈

- UserDetailsService **(사용자 세부 정보 서비스)**
    - 사용자의 관한 세부 정보를 관리한다.
- PasswordEncoder **(암호 인코더)**
    - 암호를 인코딩한다.
    - 암호가 기존 인코딩과 일치하는지 확인한다.
- AuthenticationProvider **(인증 공급자)**
    - 인증 논리를 정의하고 사용자와 암호의 관리를 위임한다.

- 스프링 시큐리티의 인증 프로세스에 포함 된 주 구성 요소

![part2-1](https://user-images.githubusercontent.com/83939644/225906846-cca12b48-1583-4fa9-bc7c-77bdde00e548.png)

- 인증필터는 인증 요청을 관리자에게 위임하고 응답을 바탕으로 보안 컨텍스트를 구성한다.
- 인증 관리자는 인증 공급자를 이용해 인증을 처리한다.
- 인증 공급자는 인증 논리를 구현한다.
- 인증 공급자는 사용자 관리 책임을 구현하는 사용자 세부 정보 서비스를 인증 논리에 이용한다.
- 인증 공급자는 암호 관리를 구현하는 암호 인코더를 인증 논리에 이용한다.
- 보안 컨텍스트는 인증 프로세스 후 인증 데이터를 유지한다.

### 사용자 관리 구성요소

- UserDetails
    - 사용자를 기술하는 역할
- UserDetailsService
    - 사용자의 이름으로 사용자를 검색하는 역할
    - 사용자의 인증만 필요한 경우
- UserDetailsManager
    - 사용자 추가, 삭제, 수정 작업을 지원
    - 사용자를 관리할 경우
- GrantedAuthority
    - 사용자가 수행할 수 있는 작업을 나타내는 역할

![part2-2](https://user-images.githubusercontent.com/83939644/225906877-15e35f03-0ea6-4c8a-a3cb-62c5611b69a4.png)

```java
UserDetails 인터페이스 예시 

public interface UserDetails extends Serializable{
String getUsername(); //사용자 자격증명 반환메소드
String getPassword(); //사용자 자격증명 반환메소드
Collection<? extends GrantedAuthority> getAuthorities();
boolean isAccountNonExpired(); //계정만료
boolean isAccountNonLocked(); //계정잠금
boolean isCredentialsNonExpried(); //자격증명 만료
boolean isEnabled(); //계정 비활성화
}
```
