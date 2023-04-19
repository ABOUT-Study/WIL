# 4장

## 암호 처리

- PasswordEncoder의 구현 및 이용
- 스프링 시큐리티 암호화 모듈 툴 이용

4-1 PasswordEncoder 계약의 이해

<img width="538" alt="스크린샷 2023-04-15 오후 8 24 19" src="https://user-images.githubusercontent.com/83939644/233043272-75e49025-2002-4f11-b4cb-aad0b82a522a.png">

- 기본적으로 애플리케이션은 인코딩된 암호를 저장하며 암호를 쉽게 읽을수 없게 해시로 저장하는것이 좋다.
- PasswordEncoder에서는 encode() 및 machers() 메서드가 책임을 정의한다.

### * PasswordEncoder의 인터페이스

```coffeescript
public interface PasswordEncoder{
	String encode(CharSequence rawPassword); // 주어진 암호의 해시를 제공하거나 암호화를 수행
	boolean matchers(CharSequence rawPassword, String encodedPassword);// 인코딩된 문자열이 원시 암호와 일치하는지 확인.
		
	//인코딩된 암호를 보안향상을 위해 다시 인코딩하는 역할 (기본이 false, true일경우 재 인코딩)
	default boolean upgradeEncoding(String encodedPassword){
		return flase;
	}
}

```

SHA-512를 이용하는 PasswordEncoder구현

```coffeescript
public class Sha512PasswordEncoder implements PasswordEncoder{
	@Override
	public String encode(CharSequence rawPassword){
		return hasWithSHA512(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodePassword){
		String hashedPassword = encode(rawPassword);
		return encodedPassword.equals(hashedPassword);
	}
}

```

- PasswordEncoder의 구현 선택
    - NoOpPasswordEncoder - 암호를 인코딩하지않고 일반 텍스트로 유지 (최대한 사용 X)
    - StandardPasswordEncoder - SHA-256를 이용한 암호 해시. 구식의 버전 으로 현재는 사용을 지양하는 편.
    - Pbkdf2PasswordEncoder - PBKDF2를 이용한다.
    - BCryptPasswordEncoder - bcrypt 강력 해싱 함수로 암호를 인코딩 (많이 사용)
    - SCryptPasswordEncoder - scrypt 해싱함수로 암호를 인코딩

4-2 스프링 시큐리티 암호화 모듈

- 스프링 시큐리티 암호화 모듈(SSCM)의 두 가지 필수 기능
    - 키 생성기 - 해싱 및 암호화 알고리즘을 위한 키를 생성하는 객체
        - 키 생성기
            - 키 생성기는 특정한 종류의 키를 생성하는 객체로서 암호화나 해싱 알고리즘에 필요
            - 주요 2가지 유형 인터페이스.
                - ByteKeyGenerator
                    
                    ```coffeescript
                    public interface BytesKeyGenerator{
                    	int getKeyLength();
                    	byte[] generateKey();
                    }
                    
                    BytesKeyGenerator keyGenerator = KeyGenerators.secureRandom();
                    byte[] key = keyGenerator.generateKey();
                    int keyLength = keyGenerator.getkeyLength();
                    
                    //기본적으로 8바이트 길이의 키를 생성한다. 키의 길이를 다르게 지정하고 싶을때는 
                    //KeyGenerators.secureRandom()의 원하는 값을 전달하면 된다.
                    ```
                    
                - StringKeyGenerator
                    - 일반적으로 해싱 및 암호화 알고리즘의 솔트값 생성으로 이용.
                    
                    ```coffeescript
                    public interface StringKeyGenerator{
                    	String generaterKey();
                    }
                    
                    StringKeyGenerator keyGenerator = KeyGenerators.string();
                    String salt = keyGenerator.generateKey();
                    
                    //8 바이트 키를 생성하고 16진수 문자열로 인코딩하며 이를 문자열로 반환.
                    ```
                    
    - 암호기 - 데이터를 암호화 및 복호화하는 객체
        - 두가지 유형의 암호기가 정의, 이들의 역할을 비슷 하지만 다른 데이터 형식을 처리한다.
            - ByteEncryptor - 범용적이며, 바이트배열로 입력데이터를 받는다.
                
                ```coffeescript
                public interface BytesEnryptor{
                	byte[] encrypt(byte[] byteArray);
                	byte[] decrypt(byte[] encryptedByteArray);
                }
                
                ```
                
            - TextEncryptor - 데이터를 문자열로 관리.
                
                ```coffeescript
                public interface TextEncryptor{
                	String encrypt(String text);
                	String decrypr(String encryptedText);
                }
                ```
                

### 요약

- PasswordEncoder는 인증 논리에서 암호를 처리하는 가장 중요한 책임을 담당.
- 해싱 알고리즘에 대해 여러가지 대안을 제공하므로 필요한 구현을 선택하여 사용할수 있다.
- SSCM에는 키생성기와 암호기를 구현하는 여러 대안이 있다.
- 키 생성기는 암호화 알고리즘에 이용되는 키생성을 도와주는 유틸리티 객체
- 암호기는 데이터 암호화와 복호화를 수행하도록 도와주는 유틸리티 객체.
