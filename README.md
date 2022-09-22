# 스프링 시큐리티

1. WebSecurityConfigurerAdapter 상속받아 스프링 시큐리티 확인하기
   - 사용자 정보 생성
   - 패스워드 인코딩
   - 특정 경로별 권한 체크
2. basic 로그인
   - Default Login 페이지
   - 사용자 지정 Login 페이지
   - 로그인 성공 시 보낼 페이지 지정
   - 로그아웃 성공 시 보낼 페이지 지정
   - 접근 가능한 권한에 따라 제어
   - AuthenticationDetailsSource 정보 커스텀하기
3. Authentication 메커니즘
   - 인증(Authentication)
   - 인증 제공자(AuthenticationProvider)
   - 인증 관리자(AuthenticationManager)
   - UsernamePasswordAuthenticationFilter 커스텀하기
4. login-customer-filter 실습
5. Basic 토큰인증
6. basic 인증 기본 테스트
   - WEB과 API를 분리하여 Security 작성
7. DaoAuthenticationProvider와 UserDetailsService
   - 데이터베이스를 사용하여 사용자를 관리할 수 있도록 처리
   - UserDetails를 구현한 Entity 및 UserDetailsService를 구현한 Service 생성
     - UserDetailsService와 UserDetails 구현체만 구현하면 스프링 시큐리티가 나머지는 쉽게 쓸 수 있도록 도움을 많이 준다.
8. 로그인을 지원하기 위한 필터들
9. RememberMe 토큰 저장
   - PersistenceTokenBasedRememberMeServices