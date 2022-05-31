# Spring Security Study
스프링 시큐리티의 작동방식과 모듈에 대한 공부를 저장하는 저장소

---
## Spring Security ?
애플리케이션은 보안에 관한 인증(Authentication)과 인가(Authorization) 에 대한 처리를 해주어야 한다. Spring 에서는 Spring Security 라는 별도의 프레임워크에서 관련된 기능을 제공하고 있다.
**Spring 기반의 애플리케이션의 보안(인증과 권한, 인가 등)을 담당하는 스프링 하위 프레임 워크**이다.  `Spring Security` 는 `‘인증’`과 `‘권한’`에 대한 부분을 Filter 흐름에 따라 처리하고 있다. `Filter` 는 `Dispatcher Servlet` 으로 가기 전에 적용되므로 가장 먼저 URL 요청을 받지만, `Interceptor`는 `Dispatcher` 와 `Controller` 사이에 위치한다는 점에서 적용 시기의 차이가 있다. `Spring Security` 는 보안과 관련해서 체계적으로 많은 옵션을 제공해주기 때문에 개발자 입장에서는 일일이 보안관련 로직을 작성하지 않아도 된다는 장점이 있다.

### Spring Security Authentication Architecture

![Spring Security 인증 구조](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FSvk8p%2FbtqEIKlEbTZ%2FvXKzokudAYZT9kRGXNHJe1%2Fimg.png)

Spring Security 인증 구조

### 인증(Authentication)과 인가(Authorization)

- 인증(Authentication) : 해당 사용자가 본인이 맞는지를 확인하는 절차
- 인가(Authorization) : 인증된 사용자가 요청한 자원에 접근 가능한지를 결정하는 절차

Spring Security 는 기본적으로 인증 절차를 거친 이후 인가 절차를 진행하게 되며, 인가 과정에서 해당 리소스에 대한 접근 권한이 있는지 확인을 하게 된다. Spring Security 에서는 이러한 인증과 인가를 위해 Principal 을 ID(아이디)로, Credential 을 PW(비밀번호) 로 사용하는 **Credential 기반의 인증 방식**을 사용한다.

- Principal(접근 주체) : 보호받는 Resource 에 접근하는 대상
- Credential(비밀번호) : Resource 에 접근하는 대상의 비밀번호

---
## Spring Security Module
### 주요 모듈 구성
### SecurityContextHolder

보안 주체의 세부 정보를 포함한 응용프로그램의 **현재 보안 컨테스트에 대한 세부 정보가 저장**된다. 기본적으로 `SecurityContextHolder.MODE_INHERITABLETHREADLOCAL` 방법과 `SecurityContextHolder.MODE_THREADLOCAL` 방법을 제공하고 있다.

### SecurityContext

`Authentication` 을 보관하는 역할

`SecurityContext` 를 통해서 `Authentication` 객체를 꺼내올 수 있다.

### Authentication

현재 접근하는 주체의 정보와 권한을 담는 인터페이스이다. `Authentication`  객체는 `SecurityContext` 에 저장된다. `SecurityContextHolder` 를 통해 `SecurityContext` 에 접근하고 `SecurityContext` 를 통해 `Authentication` 에 접근 할 수 있다.

`SecurityContextHolder → SecurityContext → Authentication`

```java
// Authentication 
public interface Authentication extends Principal, Serializable {
    // 현재 사용자의 권한 목록을 가져옴
    Collection<? extends GrantedAuthority> getAuthorities();
    
    // credentials(주로 비밀번호)을 가져옴
    Object getCredentials();
    Object getDetails();

    // Principal 객체를 가져옴
    Object getPrincipal();

    // 인증 여부를 가져옴
    boolean isAuthenticated();

    // 인증 여부 설정
    void setAuthenticated(boolean isAuthenticated) throw IllegalArgumentException;
```

### UsernamePasswordAuthenticationToken

`Authentication` 을 `implements` 한 `AbstractAuthenticationToken` 의 하위 클래스이다. `User` 의 ID 가 `Principal` 역할을 하고, Password 가 `Credential` 의 역할을 한다. `UsernamePasswordAuthenticationToken` 의 **첫 번째 생성자는 인증 전의 객체를 생성**하고, **두번째 생성자는 인증이 완료된 객체를 생성**한다.

```java
// UsernamePasswordAuthenticationToken 
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {
    // 주로 사용자의 ID 에 해당
    private final Object principal;
    // 주로 사용자의 PW 에 해당
    private Object credentials;

    // 인증 완료 전의 객체 생성
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // 인증 완료 후의 객체 생성
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
                                            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }
}

// AbstractAuthenticationToken 
public abstract class AbstractAuthenticationToken implements Authentication, CredentialsContainer {
}
```

### AuthenticationProvider

실제 인증에 대한 처리하는 부분이다. 인증 전의 Authentication 객체를 받아 인증이 완료된 객체를 반환하는 역할을 한다. AuthenticationProvider 인터페이스를 구현해서 Custom 한 AuthenticationProvider 을 작성해서 AuthenticationManager 에 등록하면 된다.

```java
// AuthenticationProvider
public interface AuthenticationProvider {
    // 인증 전의 Authentication 객체를 받아서 인증된 Authentication 객체 반환
    Authentication authenticate(Authentication var1) throws AuthenticationException;
    boolean supports(Class<?> var1);
}
```

### AuthenticationManager

인증에 대한 처리를 담당하고 있는 `Interface` 이다. 실질적으로는 `AuthenticationManager` 에 등록된 `AuthenticationProvider` 를 통해 처리하고 있다. 말 그대로 Manager 이다. 인증이 성공하면 `Authentication` 객체의 **2번째 생성자**를 이용해 인증이 성공한 (`isAuthenticated=true`) 객체를 생성하여 `SecurityContext` 에 저장한다. **인증 상태를 유지하기 위해 세션에 보관**하며, 인증이 실패한 경우에는 `AuthenticationException` 을 발생시킨다

```java
// AuthenticationManager
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication) 
                                            throws AuthenticationException;
}

/**  ProviderManager : AuthenticationManager 구현체
*
* AuthenticationManager 를 import 한 ProviderManager
* AuthenticationProvider : 실제 인증 과정에 대한 로직을 가진다
* 모든 provider 를 조회하면서 authenticate 처리를 한다.
*/ 
public class ProviderManager 
        implements AuthenticationManager, MessageSourceAware, InitializingBean {
    
    public List<AuthenticationProvider> getProviders() {
            return providers;
    }
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        boolean debug = logger.isDebugEnabled();
        // for 문 으로 모든 provider 를 순회하면서 처리하고 result 가 나올 때까지 반복한다.
        for ( AuthenticationProvider provider : getProviders() ) {
            ...
                try {
                    result = provider.authenticate(authentication);
                    if ( result != null ) {
                            copyDetails(authentication, result);
                            break;
                    }
                } catch ( AccountStatusException e) {
                    prepareException(e, authentication);
                    // SEC-546: Avoid polling additional providers if auth failure is due to
                    // invalid account status
                    throw e;
                }
            ...
        }
        throw lastException;
    } 
}
```

앞서 `ProviderManager` 에 직접 구현한 `CustomAuthenticationProvider` 를 등록하는 방법은 `WebSecurityConfigurerAdapter` 를 상속해 만든 `SecurityConfig` 에서 할 수 있다. `WebSecurityConfigurerAdapter` 의 상위 클래스에서는 `AuthenticationManager` 를 가지고 있기 때문에 직접 만든 `CustomAuthenticationProvider` 를 등록할 수 있게 된다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() throws Exception {
        return new CustomAuthenticationProvider();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider());
    }
}
```

### UserDetails

인증에 성공하면 생성되는 `UserDetails` 객체는 `Authentication` 객체를 구현한 `UsernamePasswordAuthenticationToken` 을 생성하기 위해 사용된다. `UserDetails` 인터페이스를 살펴보면 정보를 반환하는 메소드를 가지고 있다. 직접 개발한 `UserVO` 모델에 `UserDetails` 를 implements 하여 이를 처리하거나 `UserDetailsVO` 에 `UserDetails` 를 implements 하여 처리 가능 하다.

```java
// UserDetails 인터페이스
public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

### UserDetailsService

### PasswordEncoding

### GrantedAuthority