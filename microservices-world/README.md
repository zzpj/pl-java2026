# Microservices 2026

## OpenAPI
> OpenAPI to standard pozwalający na definiowanie interfejsów API w sposób czytelny zarówno dla ludzi, jak i maszyn. 
Umożliwia automatyczne generowanie dokumentacji, kodu klienta i serwera oraz testowanie API bez potrzeby ręcznego pisania specyfikacji. 
Dzięki niemu programiści mogą łatwiej projektować, udostępniać i integrować usługi między różnymi systemami.

* [OpenAPI strona główna](https://www.openapis.org/)
* [OpenAPI ready examples](https://nordicapis.com/3-example-openapi-definitions/)
* Prompt dla AI: `wygeneruj mi proszę w standardzie openapi prosty opis serwisu z jednym endpointem który zwroci nam status aplikacji UP/DOWN`
* [OpenAPI file (train-api)](train-api-example.yml)

### Dygresja 2026 nt Spring Boot 3 vs Spring Boot 4 i kompatybilności z Spring Cloud
```text
Spring Boot 3.5.11 + Spring Cloud 2025.1.1 to stabilny wybór.  
Spring Boot 4.0.3 nie ma stabilnego Spring Cloud, więc nie nadaje się do Eureki, Vaulta ani Config Servera.
```

### Open API demo
* Otwórz `start.spring.io`:
    * Project: Maven
    * Language: Java
    * Spring Boot: 3.5.11
    * Project Metadata
        * Group: com.zzpj
        * Artifact: OpenApiDemo
        * Name: OpenApiDemo
        * Description: Demo project for Spring Boot
        * Package name: com.zzpj.TrainTripsManager
        * Packaging: Jar
        * Java: 21
    * Dependencies: Web, SpringDoc OpenAPI

* Dodaj `openapi.yml` do `src/main/resources/static`, kontent z wyniku od AI
* Uzupełnij `application.yaml` o konfigurację dla Swagger UI:
```yaml
spring:
  application:
    name: OpenApiDemo

server:
  port: 8079

springdoc:
  swagger-ui:
    url=: /openapi.yaml
```
* Uruchom i sprawdź: http://localhost:8079/swagger-ui/index.html
* O Szwagrze słów kilka: `Swagger to narzędzie do dokumentowania i testowania API, które współpracuje z OpenAPI. Umożliwia interaktywną eksplorację API, generowanie dokumentacji oraz testowanie endpointów bez potrzeby pisania dodatkowego kodu. Dzięki Swagger UI, programiści mogą łatwo zrozumieć, jak korzystać z API i szybko sprawdzić jego działanie.`
* Uruchom endpoint i wyjaśnij, dlaczego nie działa ;)
* Dodaj w `pom.xml` zależność do obsługi nullable w OpenAPI:
```xml
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.6</version>
</dependency>
```
* Dodaj maven plugin:
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.4.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/static/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.zzpj.OpenApiDemo.api</apiPackage>
                <modelPackage>com.zzpj.OpenApiDemo.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useJakartaEe>true</useJakartaEe>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                    <library>spring-cloud</library>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
* Kluczowe opcje konfiguracji pluginu:
  * `geneneratorName` - wybór języka i frameworka, dla którego chcemy wygenerować kod (https://openapi-generator.tech/docs/generators/)
  * `<interfaceOnly>true</interfaceOnly>`: Generator tworzy wyłącznie interfejsy Javy z adnotacjami mapującymi, pozostawiając implementację logiki biznesowej w rękach programisty w osobnych klasach.
  * `<useJakartaEe>true</useJakartaEe>`: Wymusza użycie nowoczesnych pakietów jakarta.* zamiast starych javax.*, co jest niezbędne dla poprawnego działania w Spring Boot 3.x.
  * `<sourceFolder>src/gen/java/main</sourceFolder>`: Definiuje niestandardową ścieżkę wewnątrz katalogu wyjściowego, w której zostaną umieszczone wygenerowane pliki źródłowe .java.
  * `<library>spring-cloud</library>`: Instruuje generator, aby przygotował kod zoptymalizowany pod ekosystem Spring Cloud, na przykład generując klienty Feign do komunikacji między mikroserwisami.
* Uruchom: `mvn clean compile`
* Sprawdź wygenerowane klasy w `src/gen/java/main/com/zzpj/OpenApiDemo/api` i `src/gen/java/main/com/zzpj/OpenApiDemo/model`
* Implementacja endpointu:
```java
@RestController
class HealthCheckController implements HealthApi {
    @Override
    public ResponseEntity<HealthGet200Response> healthGet() {
        return ResponseEntity.of(java.util.Optional.of(new HealthGet200Response().status(HealthGet200Response.StatusEnum.UP)));
    }
}
```

### OpenAPI w praktyce (serwer/producer)
* Aplikacja udostępniająca api do organizacji podróży pociągami, z wykorzystaniem OpenAPI do zdefiniowania i wygenerowania API, a następnie implementacji logiki biznesowej w Spring Boot.
* From `start.spring.io`:
    * Project: Maven
    * Language: Java
    * Spring Boot: 3.5.11
    * Project Metadata 
      * Group: com.zzpj
      * Artifact: TrainTripsManager
      * Name: TrainTripsManager
      * Description: Demo project for Spring Boot
      * Package name: com.zzpj.TrainTripsManager
      * Packaging: Jar
      * Java: 21
    * Dependencies: Web, SpringDoc OpenAPI, Actuators

* Dependencies-config:
    * dependencies:
  ```xml
    <dependency>
        <groupId>org.openapitools</groupId>
        <artifactId>jackson-databind-nullable</artifactId>
        <version>0.2.6</version>
    </dependency>
  ```
    * plugin:
  ```xml
    <plugin>
        <groupId>org.openapitools</groupId>
        <artifactId>openapi-generator-maven-plugin</artifactId>
        <version>7.13.0</version>
        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
                <configuration>
                    <inputSpec>${project.basedir}/src/main/resources/static/train-api.yaml</inputSpec>
                    <generatorName>spring</generatorName>
                    <apiPackage>com.zzpj.openapi.api</apiPackage>
                    <modelPackage>com.zzpj.openapi.model</modelPackage>
                    <configOptions>
                        <interfaceOnly>true</interfaceOnly>
                        <useJakartaEe>true</useJakartaEe>
                        <sourceFolder>src/gen/java/main</sourceFolder>
                        <library>spring-cloud</library>
                    </configOptions>
                </configuration>
            </execution>
        </executions>
    </plugin>
  ```
  * Uzupełnij `application.yaml` o konfigurację dla Swagger UI:
    ```yaml
    spring:
      application:
        name: TrainTripsManager
    
    server:
      port: 8081
    
    springdoc:
      swagger-ui:
        url=: /train-api.yaml
    ```
    * Skopiuj `train-api-example.yaml` do `src/main/resources/static/train-api.yaml` (Zmien nazwe pliku, zwroc uwagę na port)
    
* run `mvn clean install`
* uzupełnij plik yaml aby odpalać poprzez swaggera endpointy:
```yaml
servers:
  - url: https://api.example.com
    description: Production
  - url: http://localhost:8081
    description: Local development
```
* implement generated api
```java

@RestController
@RequiredArgsConstructor
class StationController implements StationsApi {

    private final StationService stationService;

    @Override
    public ResponseEntity<List<Station>> getStations() {
        return ResponseEntity.ok(stationService.getStations());
    }
}
```

```java

@Service
class StationService {
    public List<Station> getStations() {
        Station plStation = new Station().id(UUIDConstant.LODZ_STATION).name("Łódź Fabryczna").countryCode("PL").timezone("Europe/Warsaw");
        Station deStation = new Station().id(UUIDConstant.BERLIN_STATION).name("Berlin Hauptbahnhof").countryCode("DE").timezone("Europe/Berlin");
        Station frStation = new Station().id(UUIDConstant.PARIS_STATION).name("Paris Gare du Nord").countryCode("FR").timezone("Europe/Paris");
        Station itStation = new Station().id(UUIDConstant.ROME_STATION).name("Roma Termini").countryCode("IT").timezone("Europe/Rome");
        return List.of(plStation, deStation, frStation, itStation);
    }
}

@UtilityClass
class UUIDConstant {
    public static final UUID LODZ_STATION = UUID.fromString("b2cc2fe2-be4b-4733-9e21-9419711d0e04");
    public static final UUID PARIS_STATION = UUID.fromString("083d3f87-a738-4567-9472-2cf0c325c115");
    public static final UUID BERLIN_STATION = UUID.fromString("23c20c5f-d257-46f6-ace3-9074dad470a2");
    public static final UUID ROME_STATION = UUID.fromString("139d47ee-4724-4028-b261-e003fe5fcc40");
}
```

* use url: http://localhost:8081/swagger-ui/index.html
* Reminder: generated code is not commited to repo

### OpenAPI i obsługa wyjątków
> W architekturze Spring Boot (szczególnie przy podejściu API-First) najlepszą praktyką jest oddzielenie logiki biznesowej od obsługi błędów. Robi się to za pomocą Global Exception Handler (adnotacja @RestControllerAdvice).

```java
@RestControllerAdvice
class GlobalExceptionHandler {

	// Obsługa konkretnego błędu
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> handleRuntimeError(IllegalStateException ex) {
		return null;
	}

	// Obsługa wszystkich pozostałych błędów
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralError(Exception ex) {
		return null;
	}
}
```
Szczegółowo:
```java
@RestControllerAdvice
class GlobalExceptionHandler {

	// Obsługa konkretnego błędu
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> handleRuntimeError(IllegalStateException ex) {
		
		Problem problem = new Problem()
				.status(HttpStatus.BAD_REQUEST.value())
				.title("Bad Request")
				.detail(ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
	}

	// Obsługa wszystkich pozostałych błędów
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralError(Exception ex) {

		Problem problem = new Problem()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.title("Internal Server Error")
				.detail(ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
	}
}
```
Przykładowa implementacja endpointu który rzuca wyjątek:
```java
@RestController
@RequiredArgsConstructor
class BookingController implements BookingsApi {

    @Override
    public ResponseEntity<Booking> createBooking(Booking booking) {

        if (booking == null || booking.getTripId() == null) {
            throw new IllegalStateException("Booking cannot be null");
        }

        return null;
    }
}
```
Wywołanie:
```http request
POST http://localhost:8081/bookings
Content-Type: application/json

{
  "id": "",
  "trip_id": "",
  "passenger_name": "",
  "has_bicycle": false,
  "has_dog": false
}
```

### OpenAPI w praktyce (client/consumer)

* Tworzymy drugi serwis `start.spring.io`
    * Project: Maven
    * Language: Java
    * Spring Boot: 3.5.11
    * Project Metadata Group: com.zzpj
    * Artifact: TrainTripsOrganizerService
    * Name: TrainTripsOrganizerService
    * Description: Demo project for Spring Boot
    * Package name: com.zzpj.TrainTripsOrganizerService
    * Packaging: Jar
    * Java: 21
    * Dependencies: Web, Actuator, Lombok, SpringDoc OpenAPI
* complete `pom.xml` with openAPI dependencies:

```xml

<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.6</version>
</dependency>
```

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.13.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/api/train-api.yaml</inputSpec>
                <generatorName>java</generatorName>
                <apiPackage>com.zzpj.openapi.api</apiPackage>
                <modelPackage>com.zzpj.openapi.model</modelPackage>
                <generateApiTests>false</generateApiTests>
                <generateModelTests>false</generateModelTests>
                <configOptions>
                    <useJakartaEe>true</useJakartaEe>
                    <library>resttemplate</library>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

* kopiuj do odpowiedniego katalogu plik yaml z poprzedniego projektu `train-api.yml` 
* sprawdź, czy uzupełniona jest sekcja servers o nasza lokalną instancję - przykład jak to powinno wyglądać:
```yaml
servers:
  - url: https://api.example.com
    description: Production
  - url: http://localhost:8081
    description: Local development
```
* remove spring test units if needed
* run `mvn clean compile` and check generated code in `src/gen/java/main/com/zzpj/openapi/api` and `src/gen/java/main/com/zzpj/openapi/model`
* pokaż różnicę między wygenerowaną strukturą w obu serwisach (producer vs consumer)
* complete code:

```yaml
spring:
  application:
    name: TrainTripsOrganizerService

server:
  port: 8090
```

Troubleshooting: Gdy IntelliJ nie widzi wygenerowanych klas, wykonaj następujące kroki:
- Maven tool window -> Reload All Maven Projects
- (jeśli trzeba) Build -> Rebuild Project

```java

@SpringBootApplication
public class TrainTripsOrganizerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainTripsOrganizerServiceApplication.class, args);
    }

    @Bean
    public StationsApi stationsApi() {
        return new StationsApi();
    }

    @Bean
    public CommandLineRunner commandLineRunner(StationsApi stationsApi) {
        return args -> {
            stationsApi.getStations().forEach(System.out::println);
        };
    }
}
```
* Uruchom, aby przetestować komunikację między serwisami, sprawdź czy dane są poprawnie zwracane i przetwarzane
* Przy pierwszym uruchomieniu powinniśmy dostać błąd `Caused by: java.net.UnknownHostException: api.example.com` popraw i wskaż błąd w kontekście wielu instancji tego samego api
```java
@Bean
public StationsApi stationsApi(@Value("${TRAIN_TRIP_MANAGER_SERVICE}") String baseUrl) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return new StationsApi(apiClient);
}
```
```yaml
TRAIN_TRIP_MANAGER_SERVICE: http://localhost:8081
```

#### Obsługa "wyjątku" rzuconego przez API
```java
@Bean
public CommandLineRunner commandLineRunner(StationsApi stationsApi, BookingsApi bookingsApi) {
    return args -> {
        stationsApi.getStations().forEach(System.out::println);

        System.out.println("------");

        try {
            Booking booking = bookingsApi.createBooking(new Booking());
        } catch (RestClientResponseException e) {
            Problem responseBodyAs = e.getResponseBodyAs(Problem.class);
            System.out.println(responseBodyAs);
        }

    };
}

@Bean
public BookingsApi bookingsApi(@Value("${TRAIN_TRIP_MANAGER_SERVICE}") String baseUrl) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return new BookingsApi(apiClient);
}
```


## Część teoretyczna o mikroserwicach
Mikroserwisy pojawiły się jako odpowiedź na rosnącą złożoność dużych aplikacji monolitycznych, które trudno było rozwijać, skalować i utrzymywać. 
Pozwalają dzielić system na małe, niezależne usługi, które można wdrażać i skalować osobno. Dzięki temu zespoły mogą pracować szybciej, 
a system staje się bardziej elastyczny i odporny na awarie. Każdy mikroserwis ma własną logikę biznesową i własną bazę danych.

1. [Architektura mikroserwisowa](img/micro-arch.png)
2. [Wzorce architektoniczne](img/arch-patterns.png)
3. [Róźne propozycje architektur](img/arch-styles.png)
4. [Kluczowe zasady mikroserwisów](https://12factor.net/)

### Spring Cloud
Spring Cloud to zestaw narzędzi i bibliotek, które rozwiązują typowe problemy pojawiające się w architekturze mikroserwisowej. 
Możesz myśleć o nim jak o „platformie wsparcia” dla Spring Boot, która dodaje funkcje potrzebne, gdy aplikacja składa się z wielu 
niezależnych usług.

#### 🌩️ Co dokładnie daje Spring Cloud w mikroserwisach?
- Rejestracja i odkrywanie usług – np. Eureka, dzięki której mikroserwisy mogą się odnajdywać bez twardych adresów.
- Zarządzanie konfiguracją – Spring Cloud Config pozwala trzymać konfigurację w jednym miejscu (np. Git) i aktualizować ją bez restartu.
- Komunikacja między usługami – Feign, LoadBalancer, Gateway ułatwiają wywoływanie innych mikroserwisów i równoważenie ruchu.
- Bezpieczeństwo i sekrety – Spring Cloud Vault pozwala bezpiecznie przechowywać hasła, tokeny i klucze.
- Odporność i stabilność – mechanizmy retry, circuit breaker, timeouts, rate limiting.
- Integracja zdarzeniowa – Spring Cloud Stream do komunikacji przez brokery (Kafka, RabbitMQ).

#### 🧩 W skrócie
Spring Cloud to „system operacyjny” dla mikroserwisów w Springu — dostarcza wszystkie brakujące elementy, które sprawiają, 
że wiele małych usług może działać jak jeden spójny system.
- https://spring.io/projects/spring-cloud

## Eureka Server
Eureka to serwer rejestracji usług, który pozwala mikroserwisom automatycznie odnajdywać się w systemie bez ręcznego wpisywania adresów. 
Każdy mikroserwis rejestruje się w Eurece i regularnie wysyła „heartbeat”, dzięki czemu Eureka wie, które instancje są dostępne. 
Dzięki temu komunikacja między usługami jest dynamiczna, odporna na zmiany i nie wymaga twardych adresów ani konfiguracji sieciowej.

* Czwarty serwis z użyciem: `start.spring.io`
    * Project: Maven
    * Language: Java
    * Spring Boot: 3.5.11
    * Project Metadata Group: com.zzpj
    * Artifact: TrainTripsMothership
    * Name: TrainTripsMothership
    * Description: Demo project for Spring Boot
    * Package name: com.zzpj.TrainTripsMothership
    * Packaging: Jar
    * Java: 21
    * Dependencies: Web, Eureka Server
* Open main class with `@SpringBootApplication` annotation
* Use Spring Cloud’s `@EnableEurekaServer` to stand up a registry with which other applications can communicate. This is
  a regular Spring Boot application with one annotation added to enable the service registry.
* By default, the registry also tries to register itself, so you need to disable that behavior as well
  in  `application.properties` file.
```yaml
spring:
  application:
    name: TrainTripsMothership
  
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
server:
  port: 8761
```
* Enter URL: `http://localhost:8761/`


### Register both, newly created services

* Complete `pom.xml`:
  ```xml
  <spring-cloud.version>2025.0.1</spring-cloud.version>
  ```
  ```xml
  <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  </dependency>
  ```
  ```xml
  <dependencyManagement>
     <dependencies>
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-dependencies</artifactId>
             <version>${spring-cloud.version}</version>
             <type>pom</type>
             <scope>import</scope>
         </dependency>
     </dependencies>
  </dependencyManagement>
  ```
* Add annotation `@EnableDiscoveryClient` to main class
* Add some properties into `application.yaml`
```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

* Run all services and determine if service has been registered in Eureka Discovery Server, either by
  entering `http://localhost:8761/` or using logs.


### Spring Cloud Client Load balancer
1. [Load balancer - grafika](img/load-balancer.jpg)
1. Stop running `TrainTripsManager` and comment `server.port` properties
1. Add `TestController`:
   ```java
    @RestController
    class TestController {
    
        @Value("${spring.application.name}")
        private String applicationName;
    
        @Value("${server.port}")
        private String appPort;
    
        @GetMapping("/hello/{name}")
        public String getServiceName(@PathVariable("name") String name) {
            return "Hello " + name + ", you are using " + applicationName + " on port: " + appPort;
        }
    }
   ```
1. Run two (or more) instances using Spring Boot Run Configuration, use Environment > VM Options for setting ports:
  * `-Dserver.port=8021`
  * `-Dserver.port=8022`
  * `-Dserver.port=8023`
1. Refresh Eureka Discovery page and determine if both instances of the same service are available
1. Add load balancer dependency in `TrainTripsOrganizerService` project
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-loadbalancer</artifactId>
   </dependency>
   ```
1. `TrainTripsManagerSupplier` implementation:
> `ServiceInstanceListSupplier` to interfejs, który dostarcza listę instancji usług dla danego identyfikatora usługi. W tym przypadku `TrainTripsManagerSupplier` implementuje ten interfejs, zwracając listę trzech instancji usługi `train-trips-service`, każda z nich działa na innym porcie (8021, 8022, 8023). Dzięki temu, gdy `TrainTripsOrganizerService` będzie chciał wywołać `train-trips-service`, będzie mógł skorzystać z tej listy instancji i równoważyć obciążenie między nimi.

```java
    @Bean
public ServiceInstanceListSupplier serviceInstanceListSupplier() {
    return new ServiceInstanceListSupplier() {

        private final String serviceId = "train-trips-manager-service";

        // Lista znanych instancji (statyczna)
        private final List<ServiceInstance> instances = List.of(
                new DefaultServiceInstance("1", serviceId, "localhost", 8021, false),
                new DefaultServiceInstance("2", serviceId, "localhost", 8022, false),
                new DefaultServiceInstance("3", serviceId, "localhost", 8023, false)
        );

        @Override
        public String getServiceId() {
            return serviceId;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
            return Flux.just(instances);
        }


    };
}
```   
1. Create `restTemplate` bean with `@LoadBalanced` annotation, which will allow us to use logical service names instead of hardcoded URLs when making requests to other services.:
   ```java
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
   ```
1. Oraz "bean" z command line runnerem i ADRESEM DO KTÓREGO UDERZAMY, KTÓRY JEST LOGICZNYM NAZWĄ USŁUGI, NIE KONKRETNYM URL-em (dzięki load balancerowi):
```java
@Bean
public CommandLineRunner commandLineRunner(RestTemplate restTemplate) {
    return args -> {

        String applicationName = "FromOrganizationService";
        int i = 0;
        while (true) {
            try {
                ResponseEntity<String> forEntity = restTemplate.getForEntity("http://train-trips-manager-service/hello/" + applicationName, String.class);
                System.out.println("LB → Request id: " + i + " Response: " + forEntity.getBody());
                Thread.sleep(1000);
                i++;
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

    };
}
```
1. Wykonaj symulację awarii jednej z instancji `TrainTripsManager` (np. zatrzymaj jedną z nich) i pokaż, że komunikacja nadal działa, ale dostajemy błąd gdy uderza do instancji serwisu która padła, uzupełnij impplementację
```java
            @Override
            public Flux<List<ServiceInstance>> get() {

                // Filtrowanie tylko żywych instancji
                List<ServiceInstance> alive = instances.stream()
                        .filter(this::isAlive)
                        .toList();

                System.out.println("LB → dostępne instancje: " + alive.stream().map(ServiceInstance::getPort).toList());

                return Flux.just(alive);
            }

            // Health-check TCP
            private boolean isAlive(ServiceInstance instance) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(instance.getHost(), instance.getPort()), 150);
                    return true;
                } catch (Exception e) {
                    System.out.println("LB → instancja DOWN: port " + instance.getPort());
                    return false;
                }
            }
```

## Config Server

> Config Server w Spring Cloud to centralne miejsce przechowywania konfiguracji dla wszystkich mikroserwisów, dzięki czemu każdy z nich pobiera ustawienia z jednego, spójnego źródła. Najczęściej trzyma konfigurację w repozytorium Git, co pozwala na wersjonowanie i łatwe zarządzanie zmianami. Serwisy klienckie pobierają konfigurację przy starcie lub dynamicznie, co eliminuje konieczność trzymania plików YAML w każdym mikroserwisie. W efekcie masz jedno źródło prawdy i pełną kontrolę nad konfiguracją całego systemu.

1. Open [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `TrainTripsConfigServer`
    * Project: Maven
    * Language: Java
    * Spring Boot: 3.5.11
    * Project Metadata Group: com.zzpj
    * Artifact: TrainTripsConfigServer
    * Name: TrainTripsConfigServer
    * Description: Demo project for Spring Boot
    * Package name: com.zzpj.TrainTripsConfigServer
    * Packaging: Jar
    * Java: 21
    * Dependencies: Spring Web, Eureka Discovery Client, Config Server
1. Click Generate button, download and unzip package
1. Copy unzipped `TrainTripsConfigServer` folder into your project folder
1. DODAJ ADNOTACJE: `@EnableDiscoveryClient` & `@EnableConfigServer` into main class
1. Add some properties into `application.yml`
```yaml
spring:
  application:
    name: TrainTripsConfigServer
  cloud:
    config:
      server:
        git:
          uri: https://github.com/zzpj/demo-config-server.git
          default-label: main
          clone-on-start: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8040
 ```
1. Show Github repo in IntelliJ or browser: https://github.com/zzpj/demo-config-server.git
1. Complete `pom.xml` of `TrainTripsOrganizerService`
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-config</artifactId>
   </dependency>
   ```
1. Go to `application.yml` files and add:
    ```yml
    spring:
      application:
        name: TrainTripsOrganizerService
      config:
        import: "optional:configserver:"
      cloud:
        config:
          discovery:
            enabled: true
            service-id: TrainTripsConfigServer
          name: train-trips-organizer-service
          profile: dev
          label: main
          fail-fast: true
    ```
1. Add properties check with use of `@Value` annotation
   ```java
    @Value("${config.server.demo}")
    private String message;

    @Bean
    public CommandLineRunner commandLineRunner(RestTemplate restTemplate) {
        return args -> {


            System.out.println("Config Server message: " + message);

        };
    }
   ```
1. Remember about following properties naming rules
   ```
   /{application}/{profile}[/{label}]
   /{application}-{profile}.yml
   /{label}/{application}-{profile}.yml
   /{application}-{profile}.properties
   /{label}/{application}-{profile}.properties
   ```


## Vault Server
> Vault Server to narzędzie do bezpiecznego przechowywania i zarządzania tajemnicami, takimi jak hasła, klucze API czy certyfikaty. W architekturze mikroserwisowej pozwala na centralne zarządzanie sekretami, eliminując potrzebę trzymania wrażliwych danych w kodzie lub plikach konfiguracyjnych. Mikroserwisy mogą dynamicznie pobierać potrzebne sekrety z Vaulta podczas uruchamiania lub w trakcie działania, co zwiększa bezpieczeństwo i ułatwia rotację kluczy.

1. Pobierz i uruchom lokalnie Vault Server https://developer.hashicorp.com/vault/install#linux
2. Rozpakuj binarkę AMD64 i uruchom `./vault --version` oraz `./vault server -dev`

## API Gateway
> API Gateway to lekka warstwa pośrednia, która przyjmuje wszystkie żądania z zewnątrz i przekazuje je do odpowiednich mikroserwisów według zdefiniowanych reguł routingu. Umożliwia centralne dodawanie funkcji takich jak autoryzacja, logowanie, rate limiting czy load balancing, dzięki czemu mikroserwisy pozostają proste i odciążone od logiki brzegowej. W praktyce działa jako „jedno wejście do systemu”, które kontroluje i porządkuje cały ruch.

## Spring Security + Keycloak + Spring Authorization Server
[next class: secure your microservices and authorize your users](README2.md)