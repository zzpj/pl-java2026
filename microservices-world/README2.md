# I. Spring Security

---

## A. Prosty przykład konfiguracji Spring Security

- Utwórz projekt w Spring Initializr z parametrami:
    - Project: Maven
    - Language: Java
    - Spring Boot: 4.0.x
    - Group: com.zzpj
    - Artifact: TrainTripsUserService
    - Java: 21
    - Dependencies:
      - Spring Web
      - Spring Security
      - Lombok

- Dodaj prostą konfigurację Spring Security w pliku `application.yaml`:

    ```yaml
    spring:
      application:
        name: TrainTripsUserService   # nazwa aplikacji
      security:
        user:
          name: admin                 # pierwszy użytkownik
          password: admin123
          roles: ADMIN
    
    server:
      port: 8050                      # port aplikacji
    ```

- Otwórz przeglądarkę i przejdź do `http://localhost:8050`. Zobaczysz stronę logowania. Użyj danych:
    - Username: admin
    - Password: admin123

    > Od Spring Boot 3.x i 4.x adnotacja `@EnableWebSecurity` nie jest już potrzebna, ponieważ Spring Security automatycznie aktywuje konfigurację webową, gdy w aplikacji znajduje się bean typu SecurityFilterChain. Oznacza to, że sama obecność klasy konfiguracyjnej z metodą `filterChain()` w pełni uruchamia mechanizmy bezpieczeństwa. Adnotacja była wymagana w starszych wersjach (Spring Boot 2.x), ale w nowoczesnym Spring Security stała się zbędna i jest traktowana jako element legacy. Dzięki temu konfiguracja jest prostsza, czytelniejsza i zgodna z aktualnymi dobrymi praktykami.

---

## B. Dodanie zabezpieczonego endpointu

### 1. Utwórz kontroler `UserController`

```java
@RestController
@RequiredArgsConstructor
class UserController {

    @GetMapping("/internal")
    public String getInternalMessage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return String.format("Hello %s!", user.getUsername());
    }
}
```

### 2. Testowanie

Teraz, po zalogowaniu się, przejdź do `http://localhost:8050/internal`, a zobaczysz spersonalizowaną wiadomość powitalną z nazwą użytkownika.

```http request
GET http://localhost:8050/internal
Authorization: Basic admin admin123
```

---

## C. Zaawansowana konfiguracja: publiczne i chronione endpointy
W tym wariancie rozbudowujemy naszą aplikację o bardziej realistyczny model bezpieczeństwa: nie wszystko jest chronione, ale wybrane zasoby wymagają logowania. To dokładnie ten scenariusz, który spotykasz w prawdziwych systemach:
- `/external` → dostępny dla każdego
- `/internal` → tylko dla zalogowanych użytkowników
Dzięki temu studenci zobaczą, jak Spring Security precyzyjnie steruje dostępem do poszczególnych części API.

### ✨ 1. Dodaj endpoint publiczny

Zaczynamy od dodania nowej metody w kontrolerze, która będzie dostępna bez logowania.

```java
@GetMapping("/external")
public String getExternalMessage() {
    return "Hello all viewers!";
}
```

Co tu się dzieje?
- `/external` to nasz otwarty endpoint — każdy może go wywołać.
- Nie wymaga logowania, tokenów ani sesji.
- Idealny do testów i demonstracji różnic między endpointami.

### ✨ 2. Dodaj konfigurację bezpieczeństwa

Teraz definiujemy, które endpointy są publiczne, a które chronione.

```java
@Configuration
class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/external").permitAll()   // każdy może wejść
                        .anyRequest().authenticated()               // reszta wymaga logowania
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/internal", true)      // po zalogowaniu przekierowanie
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("student")
                .password("{noop}student123")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
```

🔍 Co tu warto zapamiętać?

- `requestMatchers("/external").permitAll() ` endpoint publiczny, bez logowania.
- `anyRequest().authenticated()` wszystkie pozostałe endpointy wymagają logowania.
- `formLogin()` Spring automatycznie generuje formularz logowania.
- `defaultSuccessUrl("/internal", true)` po poprawnym logowaniu użytkownik trafia na endpoint chroniony.
- `InMemoryUserDetailsManager` dwóch użytkowników: admin i student.


### ✨ 3. Testowanie — różnica widoczna od razu

- Endpoint publiczny (bez logowania): `http://localhost:8050/external` Odpowiedź: `Hello all viewers!`
- Endpoint chroniony (wymaga logowania) `http://localhost:8050/internal`. Spring automatycznie przekieruje Cię na formularz logowania. Możesz użyć: `admin / admin123` albo
`student / student123` i po zalogowaniu zobaczyć: `Hello admin!` lub `Hello student!`

### ✨ 4. Podsumowanie co do tej pory się dowiedzieliśmy
- jak precyzyjnie kontrolować dostęp do zasobów,
- jak działa mechanizm autoryzacji w Spring Security,
- jak tworzyć realistyczne API, gdzie część endpointów jest publiczna,
- jak Spring automatycznie obsługuje formularz logowania,
- jak działa SecurityFilterChain, czyli fundament Spring Security 6+.


### ✨ 5. Kodowanie haseł w Spring Security 

Co oznacza `{noop}` w Spring Security?

> **„nie koduj hasła — przechowuj je w czystym tekście”**

Przykład:

```java
UserDetails admin = User.withUsername("admin")
        .password("{noop}admin123")
        .roles("ADMIN")
        .build();
```

To jest **tylko do demo**. W prawdziwych aplikacjach **nigdy nie używamy `{noop}`**. Spring Security udostępnia fabrykę encoderów (wejdź w implementację `createDelegatingPasswordEncoder`):

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

Przykład poprawnego kodowania hasła:

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {

    UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("admin123"))   // hasło zakodowane
            .roles("ADMIN")
            .build();

    UserDetails user = User.withUsername("student")
            .password(passwordEncoder().encode("student123"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(admin, user);
}
```

🔍 Co tu się dzieje?

- `encoder.encode("admin123")` → hasło jest kodowane np. BCryptem
- Spring automatycznie dodaje prefix `{bcrypt}`
- hasła są bezpieczne i zgodne z dobrymi praktykami

Przykład z algorytmem Argon2:

Dodaj dependencję:
```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
    <version>1.78.1</version>
</dependency>
```

oraz popraw implementację "password encoder bean'a":
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
}
```

### ✨ 6. Czas na kontakt z bazą

Upewnij się, że masz dependencję do JPA i H2:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Encja użytkownika (UserEntity). Zakładamy prostą tabelę:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TRAIN_USERS")
public class UserEntity {
  @Id
  private String username;
  private String password;   // zakodowane Argon2
  private String role;       // np. "ADMIN"
}
```
Repozytorium JPA:

```java
@Repository
interface UserRepository extends JpaRepository<UserEntity, String> {
}
```

`UserDetailsService` wczytujący użytkowników z bazy:

```java
@Service
@RequiredArgsConstructor
class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(entity.getUsername())
                .password(entity.getPassword())     // hash Argon2 z bazy
                .roles(entity.getRole())
                .build();
    }
}
```

Konfiguracja Security + Argon2:

```java
@Configuration
@RequiredArgsConstructor
class SecurityConfig {

    private final DatabaseUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/external").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(form -> form.defaultSuccessUrl("/internal", true))
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
```

```java
@Component
@RequiredArgsConstructor
class UserShowRoom implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {

    if (userRepository.existsById("admin")) {
      return; // nie duplikujemy użytkownika
    }

    UserEntity admin = new UserEntity();
    admin.setUsername("admin");
    admin.setPassword(passwordEncoder.encode("admin123")); // Argon2 hash
    admin.setRole("ADMIN");

    userRepository.save(admin);

    System.out.println(">>> Admin user created");
    System.out.println(">>> Username: admin");
    System.out.println(">>> Password: admin123");
    System.out.println(">>> Argon2 hash: " + admin.getPassword());
  }
}
```

Włącz H2 w `application.yaml`. To jest absolutne minimum:

```yaml
spring:
  application:
    name: TrainTripsUserService   # nazwa aplikacji
  datasource:
    url: jdbc:h2:mem:train-trips-db
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
```

---

## D. JWT (JSON Web Token)

> Otwarty standard, definiujący sposób wymiany danych między określonymi stronami za pośrednictwem dokumentów JSON

### 🔧 1. Tworzymy nowy projekt: `TrainTripsUserServiceJWT`

Wejdź na: 👉 https://start.spring.io/

Ustaw:
- **Project:** Maven
- **Language:** Java
- **Spring Boot:**4.0.x***
- **Group:** `com.zzpj`
- **Artifact:** `TrainTripsUserServiceJWT`
- **Name:** `TrainTripsUserServiceJWT`
- **Description:** JWT authentication service
- **Package name:** `com.zzpj.TrainTripsUserServiceJWT`
- **Packaging:** Jar
- **Java:** 25
- **Configuration:** Yaml

Dependencies (wyklikujemy, nic ręcznie!):
- **Spring Web**
- **Spring Security**
- **Lombok**
- **OAuth2 Resource Server**


### ⚙️ 2. Podstawowe właściwości

`application.yaml`:

```yaml
spring:
  application:
    name: TrainTripsUserServiceJWT

server:
  port: 8051
```

Uruchom aplikację i sprawdź, że działa na `http://localhost:8051`.

### 🔐 3. Konfiguracja JWT

Użyj strony jwt.io, aby pokazać przykład struktury JWT. Pokaż, że JWT składa się z trzech części: header, payload i signature: https://www.jwt.io/introduction#what-is-json-web-token-structure

Następnie przejdź do dodania klasy `SecurityConfig` z częściową konfiguracją JWT:

```java
@Configuration
@EnableWebSecurity
class SecurityConfig {

    private static final String SECRET = "pb5uKLB7hWHBBMxqYLjr4-gB4zdgxEkFQJgdwV2TVf7RiANXYcrcH4inE_RjWTlv2Ddppybp7gIDuyIRV";
    //private static final String SECRET = "qwerty"; // min. 256 bits

    @Bean
    public JwtEncoder jwtEncoder() {
        System.out.println("jwtEncoder");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        return NimbusJwtEncoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        System.out.println("jwtDecoder");
        SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
```

Wspomnij, że w praktyce klucz powinien być bezpiecznie przechowywany (np. w Vault) i mieć odpowiednią długość (min. 256 bitów dla HMAC).

Uzupełnij konfigurację bezpieczeństwa, aby endpoint `/validate` był chroniony, a `/external` i `/token` publiczne:

```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/external", "/token").permitAll()
                        .requestMatchers("/validate").authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtAuthenticationConverter()))
                )
                .userDetailsService(userDetailsService())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("admin")
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
```

Wyjaśnienie:
- `csrf(AbstractHttpConfigurer::disable)` → wyłączamy CSRF, bo nasza aplikacja jest stateless i nie używa sesji.
- `authorizeHttpRequests` → definiujemy, które endpointy są publiczne, a które chronione.
- `sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))` → mówimy Spring Security, że nasza aplikacja nie będzie używać sesji, autentykacja będzie oparta na tokenach.
- `oauth2ResourceServer` → konfigurujemy naszą aplikację jako resource server, który będzie weryfikował tokeny JWT.
- `userDetailsService` → definiujemy prostego użytkownika w pamięci, którego będziemy używać do testowania generowania tokenów.


### 🧑‍💻 4. Kontroler z generowaniem i walidacją tokenu

Model AuthRequest:

```java
@Data
class AuthRequest {
    private String username;
    private String password;
}
```

```java
@RestController
@RequiredArgsConstructor
class UserController {

    private final JwtEncoder jwtEncoder;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody AuthRequest request) {

        System.out.println("token endpoint is invoked with username: " + request.getUsername());
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niepoprawny login lub hasło");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niepoprawny login lub hasło");
        }
        System.out.println("user " + user.getUsername() + " has been verified correctly");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuer("TrainTripsUserServiceJWT")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(120))
                .claim("roles", user.getAuthorities())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwsHeader, claims);

        String tokenValue = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        System.out.println("Generated token for user " + user.getUsername() + ": " + tokenValue);

        return ResponseEntity.ok(tokenValue);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("Token poprawny. Użytkownik: " + jwt.getSubject());
    }

    @GetMapping("/external")
    public String external() {
        return "Publiczny endpoint – dostęp bez logowania";
    }
}
```

Wyjaśnienie:
- `/token` → endpoint do generowania tokenu. Sprawdza poprawność loginu i hasła, a następnie tworzy token JWT z odpowiednimi roszczeniami (claims).
- `/validate` → endpoint do weryfikacji tokenu. Jeśli token jest poprawny, zwraca informację o użytkowniku. Jest chroniony, więc wymaga ważnego tokenu w nagłówku Authorization.
- `/external` → publiczny endpoint, który jest dostępny bez logowania, służy do pokazania różnicy między chronionymi a publicznymi zasobami.


### 🧪 5. Testowanie
1. Uruchom aplikację.
2. Wywołaj endpoint `/token` z danymi:
```http request
POST http://localhost:8051/token
Content-Type: application/json  

{
    "username": "admin",
    "password": "admin123"
}
```
3. Otrzymasz token JWT w odpowiedzi.
4. Teraz wywołaj endpoint `/validate` z tokenem w nagłówku:
```http request
GET http://localhost:8051/validate  
Authorization: Bearer <tutaj_wklej_token>
```
5. Otrzymasz odpowiedź z informacją o poprawności tokenu i nazwą użytkownika.
6. Wywołaj endpoint `/external` bez tokenu, aby zobaczyć, że jest publiczny:
```http request
GET http://localhost:8051/external
```
7. Wejdź na stronę jwt.io i wklej swój token, aby zobaczyć jego strukturę i zawartość.

### 🎯 6. Podsumowanie rozdziału
- dodaliśmy pełną konfigurację JWT,
- dodaliśmy kontroler z generowaniem i walidacją tokenu
- sprawdziliśmy działanie endpointów chronionych i publicznych,
- pokazaliśmy, jak wygląda struktura JWT i jakie informacje zawiera,
- zrozumieliśmy, jak Spring Security weryfikuje tokeny i zarządza dostępem do zasobów.

---

# II. Narzędzia do uwierzytelniania i autoryzacji użytkowników oraz usług (serwisów)

---

## A. Teoria
Zanim przejdziemy do praktyki, warto zrozumieć, czym są OAuth 2.0 i OpenID Connect, ponieważ Spring Authorization Server i Keycloak implementują te standardy.

standardy OAuth 2.0 i OpenID Connect (OIDC) to dwa powiązane protokoły używane do uwierzytelniania i autoryzacji w aplikacjach internetowych i mobilnych. Oto krótkie wyjaśnienie każdego z nich:


### OAuth 2.0 – autoryzacja
OAuth 2.0 to protokół autoryzacji, czyli przyznawania dostępu aplikacjom do zasobów użytkownika bez udostępniania jego hasła.

Kluczowe cechy:
- Umożliwia aplikacjom trzecim dostęp do zasobów (np. danych użytkownika) przechowywanych na innym serwerze (np. Google, Facebook).
- Opiera się na tokenach dostępu (access tokens), które pozwalają uzyskać dostęp do API.
- Użytkownik loguje się w zaufanym serwerze (np. Google), a następnie przyznaje dostęp aplikacji.
- OAuth 2.0 nie definiuje, jak użytkownik ma się logować — to zostawia implementacji.

Główne role:
- Resource Owner – właściciel danych (np. użytkownik)
- Client – aplikacja chcąca uzyskać dostęp do danych
- Authorization Server – serwer wydający tokeny (np. Google OAuth)
- Resource Server – serwer przechowujący dane (np. API z danymi użytkownika)

### OpenID Connect (OIDC) – uwierzytelnianie
OpenID Connect to warstwa zbudowana na bazie OAuth 2.0, która dodaje mechanizm uwierzytelniania (czyli potwierdzenia tożsamości użytkownika).

Kluczowe cechy:
- Rozszerza OAuth 2.0 o możliwość logowania się użytkownika (Single Sign-On).
- Oprócz tokena dostępu zwraca też ID Token, który zawiera informacje o użytkowniku (np. e-mail, imię).
- Używany np. przez Google, Microsoft, Facebook do logowania się do innych usług.

Dokumentacja:
- Oauth2.1: https://datatracker.ietf.org/doc/html/rfc6749
- OpenID: https://openid.net/specs/openid-connect-core-1_0.html

---

## B. Spring Authorization Server
Spring Authorization Server (SAS) to lekki, programowalny serwer autoryzacji (framework) do budowania serwerów autoryzacji zgodnych z OAuth 2.0 i OpenID Connect. 
Umożliwia tworzenie własnego serwera autoryzacji, który może wydawać tokeny dostępu i ID Tokeny dla aplikacji klienckich.

### 1. Utwórz projekt w Spring Initializr

Wejdź na: https://start.spring.io/

Uzupełnij pola:

**Metadata**
- Project: *Maven*
- Language: *Java*
- Spring Boot: *4.0.x*
- Group: `com.zzpj`
- Artifact: `AuthorizationServer`
- Name: `AuthorizationServer`
- Description: Demo project for Spring Boot
- Package name: `com.zzpj.AuthorizationServer`
- Packaging: *Jar*
- Java: *25*

**Dependencies**
- Spring Web
- Spring Security
- Spring Authorization Server

Pobierz projekt, rozpakuj i skopiuj folder `AuthorizationServer` do swojego workspace.


### 2. Dodaj konfigurację Authorization Server

```java
@Configuration
@EnableWebSecurity
class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("user-service-client-via-spring-auth-server")
                .clientSecret("{noop}very-secret-code") // tylko do testów!
                .redirectUri("http://localhost:8081/login/oauth2/code/user-service-client-via-spring-auth-server")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .scope("profile")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("{noop}admin123") // tylko do testów!
                        .authorities("ROLE_ADMIN")
                        .passwordEncoder(password -> password) // no-op encoder
                        .build(),
                User.withUsername("user1")
                        .password("{noop}user123") // tylko do testów!
                        .authorities("ROLE_USER")
                        .passwordEncoder(password -> password) // no-op encoder
                        .build());
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://auth.mydomain.com:8080")
                .build();
    }
}
```

Wyjaśnienie:
- `RegisteredClientRepository` – definiujemy klienta OIDC, który będzie się logował do naszego Authorization Server. Ustawiamy `clientId` oraz `clientSecret`, 
- `redirectUri` - to adres w aplikacji-kliencie, na który Authorization Server odsyła użytkownika po zalogowaniu i autoryzacji.
  - użytkownik loguje się na serwerze autoryzacji,
  - serwer po sukcesie robi redirect na ten URL,
  - do tego URL dołącza code (authorization code),
  - aplikacja klienta odbiera code i wymienia go na token.
- Grant types – obsługujemy Authorization Code i Refresh Token, co jest standardem dla OIDC, czyli logowania przez przeglądarkę.
  - `AUTHORIZATION_CODE` Standardowy flow — user jest przekierowywany do logowania, serwer zwraca code, klient wymienia go na token. Wymagany do normalnego logowania.
  - `REFRESH_TOKEN` Pozwala klientowi odświeżyć token bez ponownego logowania użytkownika.
  - `CLIENT_CREDENTIALS` Ten grant type jest używany, gdy klient (aplikacja) chce uzyskać token dostępu do zasobów bez udziału użytkownika. Nie jest typowy dla OIDC, ale jest często używany w scenariuszach serwer-serwer.
- Scopes – definiujemy, że nasz klient będzie prosił o dostęp do OpenID Connect (tożsamości użytkownika) oraz profilu.
- clientAuthenticationMethod – ustawiamy metodę uwierzytelniania klienta (w tym przypadku `client_secret_basic`, czyli podstawowe uwierzytelnianie przez nagłówek Authorization).
- `UserDetailsService` – definiujemy prostego użytkowników do testowania logowania.
- `AuthorizationServerSettings` – ustawiamy adres URL naszego serwera autoryzacji (issuer), który będzie używany w konfiguracji klientów OIDC. issuer to kluczowa informacja, która pozwala klientom OIDC zidentyfikować, z którym serwerem autoryzacji mają do czynienia. W praktyce powinien to być adres, pod którym serwer autoryzacji jest dostępny (np. `http://localhost:8080`), ale dla celów demonstracyjnych używamy fikcyjnego adresu `http://auth.mydomain.com:8080`.


### 3. Sprawdź konfigurację OIDC

Uruchom serwer i otwórz w przeglądarce:

```
http://localhost:8080/.well-known/openid-configuration
```

To jest standardowy endpoint OIDC, który opisuje wszystkie adresy endpointów serwera autoryzacji.



### 4. Utwórz projekt w Spring Initializr

Wejdź na: https://start.spring.io/

**Metadata**
- Project: Maven
- Language: Java
- Spring Boot: 4.0.x
- Group: `com.zzpj`
- Artifact: `UserServiceClient`
- Name: `UserServiceClient`
- Description: Demo project for Spring Boot
- Package name: `com.zzpj.UserServiceClient`
- Packaging: Jar
- Java: 25

**Dependencies**
- Spring Web
- Spring Security
- OAuth2 Client


### 5. Dodaj konfigurację klienta OIDC

Utwórz plik `application.yaml`:

```yaml
spring:
  application:
    name: UserServiceClient
  security:
    oauth2:
      client:
        registration:
          user-service-client-via-spring-auth-server:
            client-id: user-service-client-via-spring-auth-server
            client-secret: very-secret-code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authorization-grant-type: authorization_code
            scope: openid, profile
        provider:
          user-service-client-via-spring-auth-server:
            issuer-uri: http://auth.mydomain.com:8080

server:
  port: 8081
```

To oznacza:

- klient OIDC o nazwie `client`,
- logowanie przez Authorization Server,
- obsługa OpenID Connect (`scope: openid`).


### 6. Dodaj kontroler obsługujący zalogowanego użytkownika

```java
@Controller
class HomeController {

    @GetMapping("/hello")
    public ResponseEntity<String> home(@AuthenticationPrincipal OidcUser user) {
        String hello = "Hi, " + user.getName();
        System.out.println(hello);
        return ResponseEntity.ok(hello);
    }
}
```

Po zalogowaniu przez Authorization Server, Spring automatycznie wstrzyknie obiekt `OidcUser`, który zawiera dane z **ID Token**.


### 7. Jak to działa?

1. Użytkownik wchodzi na `http://localhost:8081/hello`
2. Spring Security przekierowuje go do Authorization Server
3. Authorization Server loguje użytkownika
4. Po poprawnym logowaniu odsyła kod autoryzacyjny
5. UserService wymienia kod na:
    - **ID Token** (tożsamość użytkownika)
    - **Access Token**
6. Spring Security tworzy sesję użytkownika i wstrzykuje `OidcUser`

---

## C. Keycloak

Keycloak to kompletny system **IAM (Identity and Access Management)**, który zapewnia:

- uwierzytelnianie użytkowników (OIDC, OAuth2, SAML),
- autoryzację dostępu,
- zarządzanie użytkownikami, rolami i grupami,
- federację tożsamości (Google, GitHub, LDAP, AD),
- SSO, MFA, reset hasła, rejestrację użytkowników.

Można go uruchomić jako:

- **zip/jar** — [pobierz](https://www.keycloak.org/downloads)
- **obraz Dockerowy** — [quay.io/keycloak](https://quay.io/repository/keycloak/keycloak)
- **dokumentacja** — https://www.keycloak.org/guides#getting-started
- **instrukcja krok po kroku** — https://www.keycloak.org/getting-started/getting-started-zip

### 1. Instalacja i uruchomienie

1. Pobierz Keycloak i rozpakuj paczkę.
2. Otwórz plik `conf/keycloak.conf` i ustaw port:
   ```
   http-port=8999
   ```
3. Uruchom Keycloak (via PowerShell):
    ```
    bin\kc.bat start-dev
    ```
    lub Unix:
    ```
    bin/kc.sh start-dev
    ```
4. Ustaw hasło administratora (root admin).
5. Otwórz panel admina i zaprezentuj podstawowe linki.

### 2. Konfiguracja realm

**Realm** to izolowana przestrzeń bezpieczeństwa, która posiada własnych:

- użytkowników,
- role,
- klientów (aplikacje),
- polityki i konfigurację logowania.

Możesz traktować realm jako **oddzielną domenę uwierzytelniania**.


1. Utwórz **nowy realm** o nazwie `train-trips-realm`.
2. W zakładce **General**:
    - ustaw *Display name*.
3. W zakładce **Login**:
    - User registration: **On**
    - Forgot password: **On**
    - Remember me: **On**
    - Verify email: **Off**
4. W zakładce **Themes**:
    - wybierz motywy prefiksowane `keycloak-`.

### 3. Konfiguracja klienta (aplikacji)

1. Utwórz **nowego klienta** o Client ID `user-service-client-keycloak`.
2. W zakładce **Credentials**:
    - Client Authenticator: *Client ID and Secret*
    - wygeneruj **Client Secret**
3. W zakładce **Settings**:
    - Root URL: `http://localhost:8082`
    - Valid redirect URIs: `/*`
    - Client authentication: **On**
    - Authorization: **On**
    - Login theme: *keycloak*
    - Authentication flow:  
      ✔ Standard Flow  
      ✔ **Direct Access Grants** (opcjonalnie)

### 4. Konfiguracja użytkownika

1. Utwórz nowego użytkownika o loginie `wojtek`.
2. Pole **Required User Actions** pozostaw puste.
3. Opcjonalnie dodaj role.
4. Użyj url: `http://localhost:8999/realms/train-trips-realm/account/` do pierwszego logowania użytkownika i pokaż panel zarządzania kontem.
5. Jest też endpoint OIDC discovery:`http://localhost:8999/realms/train-trips-realm/.well-known/openid-configuration`


### 5. Przykład pobrania tokena:

Połącz się z Keycloakem i pobierz token dla użytkownika `wojtek` 
przy użyciu id klienta: `user-service-client-keycloak` 
i `client_secret` oraz realm'u `train-trips-realm`:

```http
POST http://localhost:8999/realms/master/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=user-service-client-keycloak &
username=wojtek &
password=<wojtek_password> &
grant_type=password &
client_secret=<client_secret>
```


### 6. Utwórz projekt (lub użyj tego samego co dla SAS)

- Spring Boot: 4.0.x
- Zależności: Web, Security, OAuth2 Client
- Group: `com.zzpj`
- Artifact: `UserServiceClientKeycloak`


### 6. Konfiguracja `application.yaml`

```yaml
server:
  port: 8082

spring:
  security:
    oauth2:
      client:
        registration:
          user-service-client-keycloak:
            client-id: user-service-client-keycloak
            client-secret: <secret>
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authorization-grant-type: authorization_code
            scope: openid, profile
        provider:
          user-service-client-keycloak:
            issuer-uri: http://localhost:8999/realms/train-trips-realm
```

Sprawdź w przeglądarce:

```
http://localhost:8082/
```

### 7. Dodaj przekierowanie po zalogowaniu via Keycloak do zabezpieczonego endpointu:

Użyj inżynierii promptu do wygenerowania pozostałej implementacji demo

```text
Po popranym zalogowaniu się przez użytkownika poprzez keycloak przekieruj na zabezpieczony endpoint `internal`
```

Oto ta implementacja (tak na wszelki wypadek):

```java 
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/internal").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/internal", true))
                .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
```

```java
@RestController
public class InternalController {

    @GetMapping("/internal")
    String internal(Principal principal) {
        return "Internal endpoint for " + principal.getName();
    }
}
```

- Użyj adresu `http://localhost:8082/internal` do testowania logowania przez Keycloak.
- Zobacz, jakie informacje zostały zmapowane z użytkownika Keycloak do obiektu `Principal` w Spring Security.

## D. Porównanie `Spring Authorization Server` kontra `Keycloak`
| Obszar | **Keycloak** | **Spring Authorization Server (SAS)** |
| --- | --- | --- |
| **Typ narzędzia** | Kompletny system IAM (Identity & Access Management) | Framework do budowy własnego Authorization Server |
| **Gotowość do użycia** | ✔ Gotowy po instalacji | ✖ Wymaga konfiguracji i kodu |
| **UI / Panel admina** | ✔ Tak (bogaty panel) | ✖ Brak (wszystko w kodzie) |
| **Przechowywanie użytkowników** | ✔ Wbudowana baza + integracje (LDAP, AD) | ✖ Musisz dostarczyć własny UserDetailsService / bazę |
| **Rejestracja użytkowników** | ✔ Wbudowana | ✖ Musisz napisać sam |
| **Reset hasła / e‑maile** | ✔ Wbudowane | ✖ Musisz napisać sam |
| **MFA / OTP** | ✔ Wbudowane | ✖ Brak — musisz zaimplementować |
| **Federacja tożsamości (Google, GitHub, SAML)** | ✔ Wbudowana | ✖ Musisz dodać samodzielnie |
| **Zarządzanie rolami i uprawnieniami** | ✔ Wbudowane (Role, Groups, Policies) | ✖ Brak — musisz dodać w swojej aplikacji |
| **Obsługiwane protokoły** | OIDC, OAuth2, SAML, UMA | OIDC, OAuth2 |
| **Rejestracja klientów (aplikacji)** | UI + REST API | Kod (RegisteredClientRepository) |
| **Skalowanie / klastrowanie** | ✔ Wbudowane | Zależne od Twojej aplikacji |
| **Zastosowanie** | Enterprise IAM, SSO, duże organizacje | Mikroserwisy, customowe systemy, pełna kontrola |
| **Krzywa uczenia** | Niższa (klikamy) | Wyższa (programujemy) |
| **Elastyczność** | Mniejsza (produkt) | Bardzo duża (framework) |
| **Wydawanie tokenów** | Wbudowane, konfigurowalne | W pełni programowalne |

---

# III. Dodatki, jeśli starczy czasu

## A. Integracja Github OAuth via Keycloak

1. W Keycloak → **Identity Providers** → wybierz **GitHub**.
2. W GitHub → **Developer settings** → **OAuth Apps** → utwórz aplikację.
3. Ustaw *Authorization callback URL* na wartość z Keycloak (`Redirect URI`).
4. Ustaw *Homepage URL*:
   ```
   http://localhost:8082/internal
   ```
5. W Keycloak uzupełnij:
    - Client ID
    - Client Secret
6. Advanced settings: **Off**  
   First login flow: **first broker login**
7. Zrestartuj aplikację "kliencką" i spróbuj zalogować się przez GitHub.


---

## B. Keycloak REST API

1. Dodaj zależność:

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-admin-client</artifactId>
    <version>26.0.8</version>
</dependency>
```

Przykład użycia:

```java
@SpringBootApplication
public class UserServiceClientKeycloakApplication {

    //private static final String REALM_NAME = "master";
    private static final String REALM_NAME = "train-trips-realm";

    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientKeycloakApplication.class, args);
    }

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8999")
                .realm(REALM_NAME)
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username("user")
                .password("<password>")
                .build();
    }

    @Bean
    CommandLineRunner commandLineRunner(Keycloak keycloak) {
        return args -> {
            List<UserRepresentation> users = keycloak.realm(REALM_NAME)
                    .users()
                    .search("zzpj", false);

            List<String> usernames = users.stream()
                    .map(AbstractUserRepresentation::getUsername)
                    .toList();

            System.out.println("Users in Keycloak realm 'master': " + usernames);
        };
    }
}
```

Dodaj użytkownikowi rolę `realm-admin` w panelu Keycloak, aby mógł korzystać z admin API.


---


## C. HashiCorp Vault

Vault to narzędzie do **bezpiecznego przechowywania sekretów**, takich jak:
- hasła,
- klucze API,
- dane dostępowe do baz,
- certyfikaty,
- tokeny.


### **Windows / Linux / macOS — najprostsza metoda: pobranie binarki**

Wejdź na stronę pobierania Vault: [HashiCorp Developer](https://developer.hashicorp.com/vault/install)

Pobierz wersję dla swojego systemu:
- Windows → `vault.exe`
- Linux → binarka `vault`
- macOS → Homebrew lub binarka

Uruchomienie Vault w trybie developerskim nie wymaga konfiguracji i przechowuje dane w pamięci.

### Uruchomianie:

```bash
vault server -dev
```

W logach zobaczysz:

```
Root Token: s.xxxxxxxx
```

Zapisz go — to Twój token administracyjny.

### Konfiguracja CLI (jednorazowo)

W nowym terminalu:

```bash
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=s.xxxxxxxx
```


### Włączenie silnika sekretów (KV)

Vault domyślnie ma włączony KV v2 pod ścieżką `secret/`. Przejdź do lokalizacji gdzie `Vault` jest zainstalowany i sprawdź, czy silnik jest aktywny:

```bash
./vault secrets list
```


### 5. Zapis sekretu

Przykład: zapisujemy hasło do bazy:

```bash
./vault kv put secret/db password="SuperTajneHaslo123"
```

### 6. Odczyt sekretu

```bash
./vault kv get secret/db
```

Wynik:

```
====== Data ======
password: SuperTajneHaslo123
```


### 7. Użycie Vault w aplikacji (Spring Boot)

Dodaj zależność:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

Konfiguracja `application.yaml`:

```yaml
spring:
  application:
    name: UserServiceClientKeycloak
  config:
    import: optional:vault://
  cloud:
    vault:
      uri: http://127.0.0.1:8200
      token: ${VAULT_TOKEN:}
      kv:
        enabled: true
        backend: secret
        default-context: db

server:
  port: 8082
```

Aplikacja automatycznie pobierze sekret:

```yaml
my:
  db:
    password: ${password}
```

Jeśli w Vault masz:

```
vault kv put secret/application password="SuperTajneHaslo123"
```

To w Springu możesz użyć:

```java
@Value("${my.db.password}")
String password;
```

W uzupełnieniu: Główny root klucz do Vaulta zamiast w "token:" trzymać np w pliku .env albo środowisku uruchomieniowym dla Intellij



### 8. Podsumowanie

- Vault jako **centralne miejsce na sekrety**
- brak sekretów w `application.yaml`
- dynamiczne pobieranie sekretów
- możliwość rotacji sekretów bez restartu aplikacji
- bezpieczeństwo: tokeny, polityki, role

---
EOF.
