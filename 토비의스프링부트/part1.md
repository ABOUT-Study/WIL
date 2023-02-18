## 스프링 부트 소개
스프링 부트(Spring Boot) 는 스프링 기반으로 실무에서 사용 가능한 수준의 독립실행형 어플리케이션을  
복잡한 고민없이 빠르게 작성할 수 있게 도와주는 여러가지 도구의 모음이다.
#### 💡 스프링 ≠ 스프링 부트

## 스프링 부트의 핵심 목표
- 매우 빠르고 광범위한 영역의 스프링 개발 경험을 제공
- 강한 주장을 가지고 즉시 적용 가능한 기술 조합을 제공하면서, 필요에 따라 원하는 방식으로 손쉽게 변형 가
능
- 프로젝트에서 필요로 하는 다양한 비기능적인 기술(내장형 서버, 보안, 메트릭, 상태 체크, 외부 설정 방식 등)
제공
- 코드 생성이나 XML 설정을 필요로 하지 않음

## 스프링 부트의 역사
2012년 스프링 프레임워크 프로젝트에 이슈로 등록된 "Containerless 웹 개발 아키텍처의 지원" 요청에서 논의와 개발 시작

## 스프링 부트 시작하기

### 1.단일 서블릿 컨테이너  
설정 및 매핑정보들을 xml에 다 등록했었음  
<img width="906" height="400" alt="단일서블릿컨테이너" src="https://user-images.githubusercontent.com/68458092/219827423-53d2e71a-131d-45f5-a977-9d6932f636cb.png">

<img width="906" height="400" alt="서블릿컨테이너코드" src="https://user-images.githubusercontent.com/68458092/219827536-bd7495a4-acfa-4fd8-afc8-b216b621e100.png">

### 2.프론트 컨트롤러  
서블릿 컨테이너만 사용시 공통코드의 중복이 늘어나게 돼서 이걸 프론트컨트롤러에서 선/후 처리등을 맡아서 작업(인증, 보안, 다국어처리 등)
<img width="906" height="400" alt="프론트컨트롤러컨테이너" src="https://user-images.githubusercontent.com/68458092/219827877-33929863-85f4-44d8-8b92-0130f3a7c15b.png">

<img width="906" height="400" alt="프론트컨트롤러코드" src="https://user-images.githubusercontent.com/68458092/219827961-8583724b-28f8-41fe-a458-a602e6d80f0a.png">

### 3.스프링 컨테이너 DI

<img width="906" height="400" alt="빈으로 등록한 스프링컨테이너" src="https://user-images.githubusercontent.com/68458092/219828168-e0fe111a-0420-4e33-9f98-211d26819cb7.png">

<img width="906" height="400" alt="스프링컨테이너(어셈블러)" src="https://user-images.githubusercontent.com/68458092/219828448-e8c57f20-3879-4b47-a7df-302fe630fc0c.png">

<img width="906" height="400" alt="헬로컨트롤러" src="https://user-images.githubusercontent.com/68458092/219828266-70eff600-2565-4322-bb72-04789266f21e.png">

<img width="906" height="400" alt="DI사용한서블릿코드" src="https://user-images.githubusercontent.com/68458092/219828352-8ddce02c-8568-436f-8463-4a251a19fe14.png">


